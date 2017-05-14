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
import ca.watier.exceptions.GameException;
import ca.watier.services.ConstraintService;
import ca.watier.utils.Assert;
import ca.watier.utils.MathUtils;
import ca.watier.utils.MultiArrayMap;
import ca.watier.utils.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ca.watier.enums.KingStatus.*;

/**
 * Created by yannick on 4/17/2017.
 */
public class StandardGameHandler extends GenericGameHandler {

    public StandardGameHandler(ConstraintService constraintService) {
        super(constraintService);
    }

    @Override
    public boolean movePiece(CasePosition from, CasePosition to, Side playerSide) throws GameException {
        Assert.assertNotNull(from, to);

        if (!isPieceMovableTo(from, to, playerSide)) {
            return false;
        }
        boolean isMoved = false;
        Pieces piecesFrom = CURRENT_PIECES_LOCATION.get(from);

        if (piecesFrom != null) {
            boolean kingCheckMate = isKingCheck(playerSide);


            //TODO: Fix the bug when we pass from OK to CHECK when we moves a pieces (need to evaluate the move BEFORE this check)

            if (kingCheckMate && Pieces.isKing(piecesFrom)) {
                return false;
            } else if (kingCheckMate) {

                Map<CasePosition, Pieces> futurePositions = new HashMap<>(CURRENT_PIECES_LOCATION);
                Pieces pieceToMove = futurePositions.get(from);
                futurePositions.remove(from);
                futurePositions.put(to, pieceToMove);

                //Evaluate if the king is still check, with the new move
                KingStatus kingStatus = getKingStatusWithGamePieces(getPosition(Pieces.getKingOfCurrentSide(playerSide)), playerSide, futurePositions);

                if (KingStatus.CHECK.equals(kingStatus) || KingStatus.CHECKMATE.equals(kingStatus)) {
                    return false;
                }
            }
            Side sideFrom = piecesFrom.getSide();

            if (isPlayerTurn(sideFrom) && sideFrom.equals(playerSide)) {
                CURRENT_PIECES_LOCATION.remove(from);
                CURRENT_PIECES_LOCATION.put(to, piecesFrom);
                changeAllowedMoveSide();

                isMoved = true;
            }
        }

        assertGameNotWon();
        return isMoved;
    }

    /**
     * 1) Check if the king can move / kill to escape.
     * 2) If not, try to liberate a case around the king, by killing / blocking the piece with an ally piece (if only one that can hit this target).
     * 3) If not, the king is checkmate.
     *
     * @param kingPosition
     * @param playerSide
     * @return
     */
    @Override
    public KingStatus getKingStatus(CasePosition kingPosition, Side playerSide) {
        return getKingStatusWithGamePieces(kingPosition, playerSide, CURRENT_PIECES_LOCATION);
    }

    /**
     * Gets the king status based on the specified pieces
     *
     * @param kingPosition
     * @param playerSide
     * @param currentPiecesLocations
     * @return
     */
    private KingStatus getKingStatusWithGamePieces(CasePosition kingPosition, Side playerSide, Map<CasePosition, Pieces> currentPiecesLocations) {
        KingStatus kingStatus = OK;

        if (isGameHaveRule(SpecialGameRules.NO_CHECK_OR_CHECKMATE)) {
            return kingStatus;
        }

        Assert.assertNotNull(kingPosition, playerSide);


        MultiArrayMap<CasePosition, Pair<CasePosition, Pieces>> piecesThatCanHitOriginalPosition = getPiecesThatCanHitPosition(getOtherPlayerSide(playerSide), kingPosition);

        boolean isCheckmate = !piecesThatCanHitOriginalPosition.isEmpty();
        if (isCheckmate) {
            kingStatus = CHECKMATE;

            //Try to move the king
            List<CasePosition> caseAround = MathUtils.getAllPositionsAroundPosition(kingPosition);
            for (CasePosition position : caseAround) {  //Check if the king can kill something to save himself
                if (isPieceMovableTo(kingPosition, position, playerSide) && !isKingCheckAtPosition(position, playerSide)) {
                    return CHECK;
                }
            }

            //If not able to move, try to kill the enemy piece with an other piece
            if (piecesThatCanHitOriginalPosition.size() == 1) {
                Pair<CasePosition, Pieces> enemyPiecesPair = piecesThatCanHitOriginalPosition.get(kingPosition).get(0);
                CasePosition positionFrom = enemyPiecesPair.getFirstValue();

                for (Map.Entry<CasePosition, Pieces> casePositionPiecesEntry : getPiecesLocation(playerSide).entrySet()) {
                    if (!Pieces.isKing(casePositionPiecesEntry.getValue()) && CONSTRAINT_SERVICE.isPieceMovableTo(casePositionPiecesEntry.getKey(), positionFrom, playerSide, currentPiecesLocations)) {
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

                for (CasePosition position : getPiecesLocation(playerSide).keySet()) { //Try to find if one of our piece can block the target
                    if (Pieces.isKing(currentPiecesLocations.get(position))) {
                        continue;
                    }

                    for (CasePosition casePosition : MathUtils.getPositionsBetweenTwoPosition(position, enemyPosition)) {
                        if (CONSTRAINT_SERVICE.isPieceMovableTo(position, casePosition, playerSide, currentPiecesLocations)) {
                            return CHECK;
                        }
                    }
                }
            }
        }

        return kingStatus;
    }

    private boolean isKingCheckAtPosition(CasePosition currentPosition, Side playerSide) {
        if (isGameHaveRule(SpecialGameRules.NO_CHECK_OR_CHECKMATE)) {
            return false;
        }

        Assert.assertNotNull(currentPosition, playerSide);

        return !getPiecesThatCanHitPosition(getOtherPlayerSide(playerSide), currentPosition).isEmpty();
    }
}
