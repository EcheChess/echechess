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
import ca.watier.responses.GameScoreResponse;
import ca.watier.services.ConstraintService;
import ca.watier.sessions.Player;
import ca.watier.utils.*;

import java.util.*;

import static ca.watier.enums.CasePosition.*;
import static ca.watier.enums.KingStatus.*;
import static ca.watier.enums.Side.BLACK;
import static ca.watier.enums.Side.WHITE;

/**
 * Created by yannick on 5/5/2017.
 */
public class GenericGameHandler {
    private final ConstraintService CONSTRAINT_SERVICE;
    private final Set<SpecialGameRules> SPECIAL_GAME_RULES;
    protected Map<CasePosition, Pieces> positionPiecesMap;
    protected Map<Pieces, Boolean> movedPiecesMap;
    protected String uuid;
    protected Player playerWhite;
    protected Player playerBlack;
    private KingStatus blackKingStatus;
    private KingStatus whiteKingStatus;
    private boolean allowOtherToJoin = false;
    private boolean allowObservers = false;
    private Side currentAllowedMoveSide = WHITE;
    private List<Player> observerList;
    private short blackPlayerPoint = 0;
    private short whitePlayerPoint = 0;
    private GameType gameType;

    public GenericGameHandler(ConstraintService constraintService) {
        SPECIAL_GAME_RULES = new HashSet<>();
        positionPiecesMap = GameUtils.getDefaultGame();
        movedPiecesMap = GameUtils.initNewMovedPieceMap();
        observerList = new ArrayList<>();
        this.CONSTRAINT_SERVICE = constraintService;
    }

    /**
     * Return a List containing all the moves for the selected piece
     *
     * @param from
     * @param playerSide
     * @return
     */
    public List<CasePosition> getAllAvailableMoves(CasePosition from, Side playerSide) {
        List<CasePosition> positions = new ArrayList<>();

        Pieces pieces = positionPiecesMap.get(from);

        if (pieces == null || !pieces.getSide().equals(playerSide)) {
            return positions;
        }

        for (CasePosition position : CasePosition.values()) {
            if (!from.equals(position) &&
                    isPieceMovableTo(from, position, playerSide)) {
                positions.add(position);
            }
        }

        return positions;
    }

    /**
     * Check if the piece can be moved to the selected position
     *
     * @param from
     * @param to
     * @param playerSide
     * @return
     */
    public final boolean isPieceMovableTo(CasePosition from, CasePosition to, Side playerSide) {
        return CONSTRAINT_SERVICE.isPieceMovableTo(from, to, playerSide, this, MoveMode.NORMAL_OR_ATTACK_MOVE);
    }


    /**
     * Get an unmodifiable {@link Map} of the current game
     *
     * @return
     */
    public Map<CasePosition, Pieces> getPiecesLocation() {
        return Collections.unmodifiableMap(positionPiecesMap);
    }

    /**
     * Move a piece to a selected position
     *
     * @param from
     * @param to
     * @param playerSide
     * @return
     */
    public boolean movePiece(CasePosition from, CasePosition to, Side playerSide) {
        Assert.assertNotNull(from, to, playerSide);

        MoveType moveType = CONSTRAINT_SERVICE.getMoveType(from, to, this);
        Pieces piecesFrom = positionPiecesMap.get(from);
        Pieces piecesTo = positionPiecesMap.get(to);
        boolean isEatingPiece = piecesTo != null;
        Assert.assertNotNull(piecesFrom);
        Side sideFrom = piecesFrom.getSide();
        boolean isMoved = true;

        if (MoveType.NORMAL.equals(moveType)) {
            if (!isPlayerTurn(playerSide) || !isPieceMovableTo(from, to, playerSide) || !sideFrom.equals(playerSide)) {
                return false;
            }

            movePieceTo(from, to, piecesFrom);
            KingStatus kingStatusAfterMove = getKingStatus(playerSide);

            if (KingStatus.isCheckOrCheckMate(kingStatusAfterMove)) { //Cannot move, revert
                movePieceTo(to, from, piecesFrom);

                if (isEatingPiece) {
                    positionPiecesMap.put(to, piecesTo); //reset the attacked piece
                }

                isMoved = false;
            } else {
                changeAllowedMoveSide();
            }

            if (isMoved && isEatingPiece) { //Count the point for the piece
                updatePointsForSide(playerSide, piecesTo.getPoint());
            }

            if (isMoved) {
                movedPiecesMap.put(piecesFrom, true);
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
                default:
                    break;
            }
        } else if (MoveType.CASTLING.equals(moveType)) {
            /*
                If queen side, move rook to D1 / D8 and king to C1 / C8
                Otherwise, move rook to F1 / F8 and king to G1 / G8
             */
            boolean isQueenSide = Direction.WEST.equals(MathUtils.getDirectionFromPosition(from, to));
            CasePosition kingPosition = null;
            CasePosition rookPosition = null;

            switch (sideFrom) {
                case BLACK:
                    kingPosition = (isQueenSide ? C8 : G8);
                    rookPosition = (isQueenSide ? D8 : F8);
                    break;
                case WHITE:
                    kingPosition = (isQueenSide ? C1 : G1);
                    rookPosition = (isQueenSide ? D1 : F1);
                    break;
                case OBSERVER:
                default:
                    break;
            }

            movePieceTo(from, kingPosition, piecesFrom);
            movePieceTo(to, rookPosition, piecesTo);
            changeAllowedMoveSide();
        }


        return isMoved;
    }

    protected final boolean isPlayerTurn(Side sideFrom) {
        if (isGameHaveRule(SpecialGameRules.NO_PLAYER_TURN)) {
            return true;
        }

        return currentAllowedMoveSide.equals(sideFrom);
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

        positionPiecesMap.remove(from);
        positionPiecesMap.put(to, piece);
    }

    /**
     * 1) Check if the king can move / kill to escape.
     * 2) If not, try to liberate a case around the king, by killing / blocking the piece with an ally piece (if only one that can hit this target).
     * 3) If not, the king is checkmate.
     *
     * @param playerSide
     * @return
     */
    public KingStatus getKingStatus(Side playerSide) {
        KingStatus kingStatus = OK;

        CasePosition kingPosition = GameUtils.getPosition(Pieces.getKingBySide(playerSide), positionPiecesMap);

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
                        if (Pieces.isKing(positionPiecesMap.get(position))) {
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

    protected final void changeAllowedMoveSide() {
        if (BLACK.equals(currentAllowedMoveSide)) {
            currentAllowedMoveSide = WHITE;
        } else {
            currentAllowedMoveSide = BLACK;
        }
    }

    protected void updatePointsForSide(Side side, byte point) {
        Assert.assertNotNull(side);
        Assert.assertNumberSuperiorOrEqualsTo(point, (byte) 0);

        switch (side) {
            case BLACK:
                blackPlayerPoint += point;
                break;
            case WHITE:
                whitePlayerPoint += point;
                break;
            default:
                break;
        }
    }

    protected void setWhiteKingStatus(KingStatus whiteKingStatus) {
        this.whiteKingStatus = whiteKingStatus;
    }

    protected void setBlackKingStatus(KingStatus blackKingStatus) {
        this.blackKingStatus = blackKingStatus;
    }

    public boolean isGameHaveRule(SpecialGameRules rule) {
        return SPECIAL_GAME_RULES.contains(rule);
    }

    /**
     * Gets the pieces that can hit the target, the {@link CasePosition} inside the {@link Pair} is the starting position of the attacking {@link Pieces}
     *
     * @param positions
     * @param sideToKeep
     * @return
     */
    public MultiArrayMap<CasePosition, Pair<CasePosition, Pieces>> getPiecesThatCanHitPosition(Side sideToKeep, CasePosition... positions) {
        Assert.assertNotEmpty(positions);

        MultiArrayMap<CasePosition, Pair<CasePosition, Pieces>> values = new MultiArrayMap<>();

        for (CasePosition position : positions) {
            for (Map.Entry<CasePosition, Pieces> casePositionPiecesEntry : positionPiecesMap.entrySet()) {
                CasePosition key = casePositionPiecesEntry.getKey();
                Pieces value = casePositionPiecesEntry.getValue();

                Side pieceSide = value.getSide();
                if (!pieceSide.equals(sideToKeep)) {
                    continue;
                }

                if (CONSTRAINT_SERVICE.isPieceMovableTo(key, position, pieceSide, this, MoveMode.IS_KING_CHECK_MODE)) {
                    values.put(position, new Pair<>(key, value));
                }
            }
        }

        return values;
    }

    public List<CasePosition> getPositionKingCanMove(Side playerSide) {
        Assert.assertNotNull(playerSide);
        CasePosition kingPosition = GameUtils.getPosition(Pieces.getKingBySide(playerSide), positionPiecesMap);

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
     * Gets the pieces / CasePosition based on a side
     *
     * @param side
     * @return
     */
    public final Map<CasePosition, Pieces> getPiecesLocation(Side side) {
        Assert.assertNotNull(side);

        Map<CasePosition, Pieces> values = new EnumMap<>(CasePosition.class);

        for (Map.Entry<CasePosition, Pieces> casePositionPiecesEntry : positionPiecesMap.entrySet()) {
            CasePosition key = casePositionPiecesEntry.getKey();
            Pieces value = casePositionPiecesEntry.getValue();

            if (side.equals(value.getSide())) {
                values.put(key, value);
            }
        }

        return values;
    }

    public boolean isKingCheckAtPosition(CasePosition currentPosition, Side playerSide) {
        Assert.assertNotNull(currentPosition, playerSide);

        if (isGameHaveRule(SpecialGameRules.NO_CHECK_OR_CHECKMATE)) {
            return false;
        }

        Assert.assertNotNull(currentPosition, playerSide);

        return !getPiecesThatCanHitPosition(Side.getOtherPlayerSide(playerSide), currentPosition).isEmpty();
    }

    public final boolean setPlayerToSide(Player player, Side side) {
        Assert.assertNotNull(player, side);
        boolean value;

        switch (side) {
            case BLACK: {
                removePlayerFromWhite(player);
                value = changePlayerToBlack(player);
                observerList.remove(player);
                break;
            }
            case WHITE: {
                removePlayerFromBlack(player);
                value = changePlayerToWhite(player);
                observerList.remove(player);
                break;
            }
            default: {
                removePlayerFromWhite(player);
                removePlayerFromBlack(player);
                observerList.add(player);
                value = true;
                break;
            }
        }

        return value;
    }

    private void removePlayerFromWhite(Player player) {
        if (playerWhite == player) {
            playerWhite = null;
        }
    }

    private boolean changePlayerToBlack(Player player) {
        if (playerBlack == null) {
            playerBlack = player;
            return true;
        }

        return false;
    }

    private void removePlayerFromBlack(Player player) {
        if (playerBlack == player) {
            playerBlack = null;
        }
    }

    private boolean changePlayerToWhite(Player player) {
        if (playerWhite == null) {
            playerWhite = player;
            return true;
        }

        return false;
    }

    /**
     * Get the side of the player, null if not available
     *
     * @param player
     * @return
     */
    public final Side getPlayerSide(Player player) {
        Side side = null;

        if (playerWhite == player) {
            side = WHITE;
        } else if (playerBlack == player) {
            side = BLACK;
        } else if (observerList.contains(player)) {
            side = Side.OBSERVER;
        }

        return side;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public final boolean hasPlayer(Player player) {
        return observerList.contains(player) || playerBlack == player || playerWhite == player;
    }

    public Player getPlayerWhite() {
        return playerWhite;
    }

    public Player getPlayerBlack() {
        return playerBlack;
    }

    public boolean isAllowOtherToJoin() {
        return allowOtherToJoin;
    }

    public void setAllowOtherToJoin(boolean allowOtherToJoin) {
        this.allowOtherToJoin = allowOtherToJoin;
    }

    public boolean isAllowObservers() {
        return allowObservers;
    }

    public void setAllowObservers(boolean allowObservers) {
        this.allowObservers = allowObservers;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void addSpecialRule(SpecialGameRules... rules) {
        Assert.assertNotEmpty(rules);
        SPECIAL_GAME_RULES.addAll(Arrays.asList(rules));
    }

    public boolean isPieceMoved(Pieces piece) {
        Assert.assertNotNull(piece);

        return movedPiecesMap.get(piece);
    }

    public List<Player> getObserverList() {
        return Collections.unmodifiableList(observerList);
    }

    public Set<SpecialGameRules> getSpecialGameRules() {
        return Collections.unmodifiableSet(SPECIAL_GAME_RULES);
    }

    public GameScoreResponse getGameScore() {
        return new GameScoreResponse(whitePlayerPoint, blackPlayerPoint);
    }

    public boolean isGameDone() {
        return KingStatus.CHECKMATE.equals(whiteKingStatus) || KingStatus.CHECKMATE.equals(blackKingStatus);
    }

    public Pieces getPiece(CasePosition position) {
        Assert.assertNotNull(position);

        return positionPiecesMap.get(position);
    }
}
