/*
 *    Copyright 2014 - 2017 Yannick Watier
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ca.watier.game;

import ca.watier.enums.*;
import ca.watier.services.ConstraintService;
import ca.watier.sessions.Player;
import ca.watier.utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ca.watier.enums.KingStatus.*;

/**
 * Created by yannick on 4/17/2017.
 */
public class StandardGameHandler extends GenericGameHandler {

    public StandardGameHandler(ConstraintService constraintService, Player playerWhoCreatedGame) {
        super(constraintService, playerWhoCreatedGame);
    }

    @Override
    public boolean movePiece(CasePosition from, CasePosition to, Side playerSide) {
        Assert.assertNotNull(from, to);

        Pieces piecesFrom = CURRENT_PIECES_LOCATION.get(from);
        Pieces piecesTo = CURRENT_PIECES_LOCATION.get(to);
        boolean isEatingPiece = piecesTo != null;
        Assert.assertNotNull(piecesFrom);

        if (!isPieceMovableTo(from, to, playerSide)) {
            return false;
        }

        boolean isMoved = false;

        boolean isPlayerTurn = isPlayerTurn(playerSide);
        if (isPlayerTurn) {
            movePieceTo(from, to, piecesFrom);
            isMoved = true;
        }

        KingStatus kingStatusAfterMove = getKingStatus(playerSide);

        if (KingStatus.isCheckOrCheckMate(kingStatusAfterMove)) { //Cannot move, revert
            movePieceTo(to, from, piecesFrom);

            if (isEatingPiece) {
                CURRENT_PIECES_LOCATION.put(to, piecesTo); //reset the attacked piece
            }

            isMoved = false;
        } else if (isPlayerTurn) {
            changeAllowedMoveSide();
        }

        if (isMoved && isEatingPiece) { //Count the point for the piece
            updatePointsForSide(playerSide, piecesTo.getPoint());
        }

        Side otherPlayerSide = Side.getOtherPlayerSide(playerSide);
        KingStatus otherKingStatusAfterMove = getKingStatus(otherPlayerSide);

        switch (playerSide) {
            case WHITE:
                setWhiteKingStatus(kingStatusAfterMove);
                setBlackKingStatus(otherKingStatusAfterMove);
                break;
            case BLACK:
                setWhiteKingStatus(otherKingStatusAfterMove);
                setBlackKingStatus(kingStatusAfterMove);
                break;
        }

        return isMoved;
    }

    /**
     * Change a piece position, there's no check/constraint(s) on this method (Direct access to the Map)
     *
     * @param from
     * @param to
     * @param piece
     */
    protected void movePieceTo(CasePosition from, CasePosition to, Pieces piece) {
        Assert.assertNotNull(from, to, piece);

        CURRENT_PIECES_LOCATION.remove(from);
        CURRENT_PIECES_LOCATION.put(to, piece);
    }

    private boolean isKingCheckAtPosition(CasePosition currentPosition, Side playerSide) {
        Assert.assertNotNull(currentPosition, playerSide);

        if (isGameHaveRule(SpecialGameRules.NO_CHECK_OR_CHECKMATE)) {
            return false;
        }

        Assert.assertNotNull(currentPosition, playerSide);

        return !getPiecesThatCanHitPosition(Side.getOtherPlayerSide(playerSide), currentPosition).isEmpty();
    }

    @Override
    public List<CasePosition> getPositionKingCanMove(Side playerSide) {
        Assert.assertNotNull(playerSide);
        CasePosition kingPosition = GameUtils.getPosition(Pieces.getKingBySide(playerSide), CURRENT_PIECES_LOCATION);

        List<CasePosition> values = new ArrayList<>();
        List<CasePosition> caseAround = MathUtils.getAllPositionsAroundPosition(kingPosition);
        for (CasePosition position : caseAround) {  //Check if the king can kill something to save himself
            if (isPieceMovableTo(kingPosition, position, playerSide) && !isKingCheckAtPosition(position, playerSide)) {
                values.add(position);
            }
        }
        return values;
    }

    /**
     * 1) Check if the king can move / kill to escape.
     * 2) If not, try to liberate a case around the king, by killing / blocking the piece with an ally piece (if only one that can hit this target).
     * 3) If not, the king is checkmate.
     *
     * @param playerSide
     * @return
     */
    @Override
    public KingStatus getKingStatus(Side playerSide) {
        KingStatus kingStatus = OK;

        CasePosition kingPosition = GameUtils.getPosition(Pieces.getKingBySide(playerSide), CURRENT_PIECES_LOCATION);

        if (isGameHaveRule(SpecialGameRules.NO_CHECK_OR_CHECKMATE)) {
            return kingStatus;
        }

        Assert.assertNotNull(kingPosition, playerSide);
        MultiArrayMap<CasePosition, Pair<CasePosition, Pieces>> piecesThatCanHitOriginalPosition = getPiecesThatCanHitPosition(Side.getOtherPlayerSide(playerSide), kingPosition);

        boolean isCheckmate = !piecesThatCanHitOriginalPosition.isEmpty();
        if (isCheckmate) {
            kingStatus = CHECKMATE;

            //Try to move the king
            if (!getPositionKingCanMove(playerSide).isEmpty()) {
                return CHECK;
            }

            //If not able to move, try to kill the enemy piece with an other piece
            if (piecesThatCanHitOriginalPosition.size() == 1) {
                Pair<CasePosition, Pieces> enemyPiecesPair = piecesThatCanHitOriginalPosition.get(kingPosition).get(0);
                CasePosition positionFrom = enemyPiecesPair.getFirstValue();

                for (Map.Entry<CasePosition, Pieces> casePositionPiecesEntry : getPiecesLocation(playerSide).entrySet()) {
                    if (!Pieces.isKing(casePositionPiecesEntry.getValue()) && isPieceMovableTo(casePositionPiecesEntry.getKey(), positionFrom, playerSide)) {
                        return CHECK; //One or more piece is able to kill the enemy
                    }
                }
            }

            //Try to block the path of the enemy with one of the pieces
            List<Pair<CasePosition, Pieces>> pairs = piecesThatCanHitOriginalPosition.get(kingPosition);
            if (pairs.size() == 1) { //We can only block one piece, if more, checkmate
                Pair<CasePosition, Pieces> casePositionPiecesPair = pairs.get(0);
                Pieces enemyPiece = casePositionPiecesPair.getSecondValue();
                CasePosition enemyPosition = casePositionPiecesPair.getFirstValue();

                if (Pieces.isKnight(enemyPiece)) { //We cannot block a knight
                    return CHECKMATE;
                }


                for (CasePosition casePosition : MathUtils.getPositionsBetweenTwoPosition(enemyPosition, kingPosition)) { //For each position between the king and the enemy, we try to block it
                    for (CasePosition position : getPiecesLocation(playerSide).keySet()) { //Try to find if one of our piece can block the target
                        if (Pieces.isKing(CURRENT_PIECES_LOCATION.get(position))) {
                            continue;
                        }

                        if (isPieceMovableTo(position, casePosition, playerSide)) {
                            return CHECK;
                        }
                    }
                }
            }
        }

        return kingStatus;
    }
}
