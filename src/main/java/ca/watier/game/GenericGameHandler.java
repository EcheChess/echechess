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
import ca.watier.interfaces.WebSocketService;
import ca.watier.pojos.MoveHistory;
import ca.watier.responses.GameScoreResponse;
import ca.watier.services.ConstraintService;
import ca.watier.sessions.Player;
import ca.watier.utils.*;

import java.util.*;

import static ca.watier.enums.ChessEventMessage.*;
import static ca.watier.enums.ChessEventMessage.PLAYER_TURN;
import static ca.watier.enums.KingStatus.OK;
import static ca.watier.enums.KingStatus.STALEMATE;
import static ca.watier.enums.Side.BLACK;
import static ca.watier.enums.Side.WHITE;
import static ca.watier.utils.Constants.*;

/**
 * Created by yannick on 5/5/2017.
 */
public class GenericGameHandler extends GameBoard {
    private final ConstraintService CONSTRAINT_SERVICE;
    private final WebSocketService WEB_SOCKET_SERVICE;
    private final Set<SpecialGameRules> SPECIAL_GAME_RULES;
    protected String uuid;
    protected Player playerWhite;
    protected Player playerBlack;
    private KingStatus blackKingStatus;
    private KingStatus whiteKingStatus;
    private boolean allowOtherToJoin = false;
    private boolean allowObservers = false;
    private Side currentAllowedMoveSide = WHITE;
    private List<Player> observerList;
    private List<MoveHistory> moveHistoryList;
    private short blackPlayerPoint = 0;
    private short whitePlayerPoint = 0;
    private GameType gameType;

    public GenericGameHandler(ConstraintService constraintService, WebSocketService webSocketService) {
        SPECIAL_GAME_RULES = new HashSet<>();
        observerList = new ArrayList<>();
        moveHistoryList = new ArrayList<>();
        this.CONSTRAINT_SERVICE = constraintService;
        this.WEB_SOCKET_SERVICE = webSocketService;
    }


    /**
     * Move a piece to a selected position
     *
     * @param from
     * @param to
     * @param playerSide
     * @return
     */
    public MoveType movePiece(CasePosition from, CasePosition to, Side playerSide) {
        MoveHistory moveHistory = new MoveHistory(from, to, playerSide);

        MoveType moveType = movePiece(from, to, playerSide, moveHistory);
        moveHistory.setMoveType(moveType);
        moveHistoryList.add(moveHistory);
        return moveType;
    }

    public List<MoveHistory> getMoveHistory() {
        return moveHistoryList;
    }

    private MoveType movePiece(CasePosition from, CasePosition to, Side playerSide, MoveHistory moveHistory) {
        Assert.assertNotNull(from, to, playerSide);

        Side otherPlayerSide = Side.getOtherPlayerSide(playerSide);
        Pieces piecesFrom = getPiece(from);
        Pieces piecesTo = getPiece(to);

        if (piecesFrom == null || !isPlayerTurn(playerSide) || !piecesFrom.getSide().equals(playerSide)) {
            return MoveType.MOVE_NOT_ALLOWED;
        } else if (Pieces.isPawn(piecesFrom) && Ranks.EIGHT.equals(Ranks.getRank(to, playerSide))) {
            addPawnPromotion(from, to, playerSide);
            setGamePaused(true);
            changeAllowedMoveSide();

            sendPawnPromotionMessage(to, playerSide);
            sendMovedMessages(from, to, playerSide);
            return MoveType.PAWN_PROMOTION;
        }

        MoveType moveType = CONSTRAINT_SERVICE.getMoveType(from, to, this);
        KingStatus currentKingStatus = KingStatus.OK;
        boolean isEatingPiece = piecesTo != null;

        if (MoveType.NORMAL_MOVE.equals(moveType)) {
            if (!isPieceMovableTo(from, to, playerSide)) {
                return MoveType.MOVE_NOT_ALLOWED;
            }

            movePieceTo(from, to, piecesFrom);
            currentKingStatus = getKingStatus(playerSide, true);

            if (KingStatus.isCheckOrCheckMate(currentKingStatus)) { //Cannot move, revert
                setPiecePositionWithoutMoveState(piecesFrom, from);

                if (isEatingPiece) {
                    setPiecePositionWithoutMoveState(piecesTo, to); //reset the attacked piece
                } else {
                    removePieceAt(to);
                }

                return MoveType.MOVE_NOT_ALLOWED;
            } else {
                changeAllowedMoveSide();

                if (isEatingPiece) { //Count the point for the piece
                    updatePointsForSide(playerSide, piecesTo.getPoint());
                    moveType = MoveType.CAPTURE;
                }
            }
        } else if (MoveType.CASTLING.equals(moveType)) {
            /*
                If queen side, move rook to D1 / D8 and king to C1 / C8
                Otherwise, move rook to F1 / F8 and king to G1 / G8
             */
            CastlingPositionHelper castlingPositionHelper = new CastlingPositionHelper(from, to, playerSide).invoke();
            CasePosition kingPosition = castlingPositionHelper.getKingPosition();
            CasePosition rookPosition = castlingPositionHelper.getRookPosition();

            movePieceTo(from, kingPosition, piecesFrom);
            movePieceTo(to, rookPosition, piecesTo);
            changeAllowedMoveSide();
        } else if (MoveType.EN_PASSANT.equals(moveType)) {
            movePieceTo(from, to, piecesFrom);
            changeAllowedMoveSide();

            CasePosition enemyPawnPosition = MathUtils.getNearestPositionFromDirection(to, otherPlayerSide.equals(Side.BLACK) ? Direction.SOUTH : Direction.NORTH);
            Pieces enemyPawnToEat = getPiece(enemyPawnPosition);
            updatePointsForSide(playerSide, enemyPawnToEat.getPoint());
            removePieceAt(enemyPawnPosition);
        }

        KingStatus otherKingStatusAfterMove = getKingStatus(otherPlayerSide, true);
        switch (playerSide) {
            case WHITE:
                setWhiteKingStatus(currentKingStatus);
                setBlackKingStatus(otherKingStatusAfterMove);
                break;
            case BLACK:
                setWhiteKingStatus(otherKingStatusAfterMove);
                setBlackKingStatus(currentKingStatus);
                break;
            default:
                break;
        }

        if (MoveType.isMoved(moveType)) {
            sendMovedMessages(from, to, playerSide);
        }

        moveHistory.setCurrentKingStatus(currentKingStatus);
        moveHistory.setOtherKingStatus(otherKingStatusAfterMove);
        sendCheckOrCheckmateMessages(currentKingStatus, otherKingStatusAfterMove, playerSide);

        return moveType;
    }

    private void sendPawnPromotionMessage(CasePosition to, Side playerSide) {
        WEB_SOCKET_SERVICE.fireSideEvent(uuid, playerSide, PAWN_PROMOTION, to.name());
        WEB_SOCKET_SERVICE.fireGameEvent(uuid, PAWN_PROMOTION, String.format(GAME_PAUSED_PAWN_PROMOTION, playerSide));
    }

    private void sendCheckOrCheckmateMessages(KingStatus currentkingStatus, KingStatus otherKingStatusAfterMove, Side playerSide) {
        Assert.assertNotNull(currentkingStatus, otherKingStatusAfterMove);
        Side otherPlayerSide = Side.getOtherPlayerSide(playerSide);

        if (KingStatus.CHECKMATE.equals(currentkingStatus)) {
            WEB_SOCKET_SERVICE.fireGameEvent(uuid, KING_CHECKMATE, String.format(PLAYER_KING_CHECKMATE, playerSide));
        } else if (KingStatus.CHECKMATE.equals(otherKingStatusAfterMove)) {
            WEB_SOCKET_SERVICE.fireGameEvent(uuid, KING_CHECKMATE, String.format(PLAYER_KING_CHECKMATE, otherPlayerSide));
        }

        if (KingStatus.CHECK.equals(currentkingStatus)) {
            WEB_SOCKET_SERVICE.fireSideEvent(uuid, playerSide, KING_CHECK, Constants.PLAYER_KING_CHECK);
        } else if (KingStatus.CHECK.equals(otherKingStatusAfterMove)) {
            WEB_SOCKET_SERVICE.fireSideEvent(uuid, otherPlayerSide, KING_CHECK, Constants.PLAYER_KING_CHECK);
        }

    }

    protected final boolean isPlayerTurn(Side sideFrom) {
        return isGameHaveRule(SpecialGameRules.NO_PLAYER_TURN) || currentAllowedMoveSide.equals(sideFrom);

    }

    protected final void changeAllowedMoveSide() {
        if (BLACK.equals(currentAllowedMoveSide)) {
            currentAllowedMoveSide = WHITE;
        } else {
            currentAllowedMoveSide = BLACK;
        }
    }

    private void sendMovedMessages(CasePosition from, CasePosition to, Side playerSide) {
        WEB_SOCKET_SERVICE.fireGameEvent(uuid, MOVE, String.format(PLAYER_MOVE, playerSide, from, to));
        WEB_SOCKET_SERVICE.fireSideEvent(uuid, Side.getOtherPlayerSide(playerSide), PLAYER_TURN, Constants.PLAYER_TURN);
        WEB_SOCKET_SERVICE.fireGameEvent(uuid, SCORE_UPDATE, getGameScore());
    }

    /**
     * 1) Check if the king can move / kill to escape.
     * 2) If not, try to liberate a case around the king, by killing / blocking the piece with an ally piece (if only one that can hit this target).
     * 3) If not, the king is checkmate.
     *
     * @param playerSide
     * @param enableCheckForStalemate - Prevent an infinite loop when evaluating
     * @return
     */
    public KingStatus getKingStatus(Side playerSide, boolean enableCheckForStalemate) {
        KingStatus kingStatus = OK;

        Pieces kingPiece = Pieces.getKingBySide(playerSide);
        CasePosition kingPosition = GameUtils.getSinglePiecePosition(kingPiece, getPiecesLocation());

        if (isGameHaveRule(SpecialGameRules.NO_CHECK_OR_CHECKMATE)) {
            return kingStatus;
        }

        Assert.assertNotNull(kingPosition, playerSide);
        MultiArrayMap<CasePosition, Pair<CasePosition, Pieces>> piecesThatCanHitOriginalPosition = getPiecesThatCanHitPosition(Side.getOtherPlayerSide(playerSide), kingPosition);

        boolean isCheckmate = !piecesThatCanHitOriginalPosition.isEmpty();
        if (isCheckmate) {
            kingStatus = KingStatus.CHECKMATE;

            //Try to move the king
            if (!getPositionKingCanMove(playerSide).isEmpty()) {
                return KingStatus.CHECK;
            }

            //If not able to move, try to kill the enemy piece with an other piece
            if (piecesThatCanHitOriginalPosition.size() == 1) {
                Pair<CasePosition, Pieces> enemyPiecesPair = piecesThatCanHitOriginalPosition.get(kingPosition).get(0);
                CasePosition positionFrom = enemyPiecesPair.getFirstValue();

                for (Map.Entry<CasePosition, Pieces> casePositionPiecesEntry : getPiecesLocation(playerSide).entrySet()) {
                    if (!Pieces.isKing(casePositionPiecesEntry.getValue()) && isPieceMovableTo(casePositionPiecesEntry.getKey(), positionFrom, playerSide)) {
                        return KingStatus.CHECK; //One or more piece is able to kill the enemy
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
                    return KingStatus.CHECKMATE;
                }

                for (CasePosition casePosition : MathUtils.getPositionsBetweenTwoPosition(enemyPosition, kingPosition)) { //For each position between the king and the enemy, we try to block it
                    for (CasePosition position : getPiecesLocation(playerSide).keySet()) { //Try to find if one of our piece can block the target
                        if (Pieces.isKing(getPiece(position))) {
                            continue;
                        }

                        if (isPieceMovableTo(position, casePosition, playerSide)) {
                            return KingStatus.CHECK;
                        }
                    }
                }
            }
        } else if (getPositionKingCanMove(playerSide).isEmpty() && enableCheckForStalemate) { //Check if not a stalemate
            boolean isStalemate = true;

            //Check if we can move the pieces around the king (same color)
            for (CasePosition moveFrom : MathUtils.getAllPositionsAroundPosition(kingPosition)) {
                Pieces currentPiece = getPiece(moveFrom);

                if (currentPiece != null && !Pieces.isKing(currentPiece) && Pieces.isSameSide(currentPiece, kingPiece)) {
                    for (CasePosition moveTo : getAllAvailableMoves(moveFrom, playerSide)) {
                        if (isPieceMovableTo(moveFrom, moveTo, playerSide)) {

                            Pieces pieceEaten = getPiece(moveTo);

                            removePieceAt(moveFrom);
                            setPiecePositionWithoutMoveState(currentPiece, moveTo);

                            KingStatus evaluatedKingStatus = getKingStatus(playerSide, false);

                            //Reset the piece(s)
                            setPiecePositionWithoutMoveState(currentPiece, moveFrom);
                            if (pieceEaten != null) {
                                setPiecePositionWithoutMoveState(pieceEaten, moveTo);
                            } else {
                                removePieceAt(moveTo);
                            }

                            if (!KingStatus.isCheckOrCheckMate(evaluatedKingStatus)) {
                                isStalemate = false;
                                break;
                            }
                        }
                    }
                }
            }
            kingStatus = isStalemate ? STALEMATE : kingStatus;
        }

        return kingStatus;
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

    public GameScoreResponse getGameScore() {
        return new GameScoreResponse(whitePlayerPoint, blackPlayerPoint);
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
            for (Map.Entry<CasePosition, Pieces> casePositionPiecesEntry : getPiecesLocation().entrySet()) {
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
        CasePosition kingPosition = GameUtils.getSinglePiecePosition(Pieces.getKingBySide(playerSide), getPiecesLocation());

        List<CasePosition> values = new ArrayList<>();
        List<CasePosition> caseAround = MathUtils.getAllPositionsAroundPosition(kingPosition);
        for (CasePosition position : caseAround) {  //Check if the king can kill something to save himself
            if (isPieceMovableTo(kingPosition, position, playerSide) && !isKingCheckAtPosition(position, playerSide)) {
                values.add(position);
            }
        }

        //Add the position, if the castling is authorized on the rook
        Pieces rook = Side.WHITE.equals(playerSide) ? Pieces.W_ROOK : Pieces.B_ROOK;
        for (CasePosition rookPosition : GameUtils.getPiecesPosition(rook, getPiecesLocation())) {
            if (MoveType.CASTLING.equals(CONSTRAINT_SERVICE.getMoveType(kingPosition, rookPosition, this))) {
                values.add(rookPosition);
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

        for (Map.Entry<CasePosition, Pieces> casePositionPiecesEntry : getPiecesLocation().entrySet()) {
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

    /**
     * Return a List containing all the moves for the selected piece
     *
     * @param from
     * @param playerSide
     * @return
     */
    public List<CasePosition> getAllAvailableMoves(CasePosition from, Side playerSide) {
        List<CasePosition> positions = new ArrayList<>();
        Pieces pieces = getPiece(from);

        if (pieces == null || !pieces.getSide().equals(playerSide)) {
            return positions;
        }

        for (CasePosition position : CasePosition.values()) {

            boolean isSpecialMove = MoveType.isSpecialMove(CONSTRAINT_SERVICE.getMoveType(from, position, this));

            if (isSpecialMove || !from.equals(position) && isPieceMovableTo(from, position, playerSide)) {
                positions.add(position);
            }
        }

        return positions;
    }

    /**
     * Return a list of @{@link Pieces} that can moves to the selected position
     *
     * @param position
     * @param sideToKeep
     * @return
     */
    public List<Pair<CasePosition, Pieces>> getAllPiecesThatCanMoveTo(CasePosition position, Side sideToKeep) {

        List<Pair<CasePosition, Pieces>> values = new ArrayList<>();

        for (Map.Entry<CasePosition, Pieces> casePositionPiecesEntry : getPiecesLocation().entrySet()) {
            CasePosition key = casePositionPiecesEntry.getKey();
            Pieces value = casePositionPiecesEntry.getValue();

            if (!sideToKeep.equals(value.getSide())) {
                continue;
            }

            if (isPieceMovableTo(key, position, sideToKeep)) {
                values.add(new Pair<>(key, value));
            }
        }

        return values;
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

    public void removeSpecialRule(SpecialGameRules... rules) {
        Assert.assertNotEmpty(rules);
        SPECIAL_GAME_RULES.removeAll(Arrays.asList(rules));
    }

    public List<Player> getObserverList() {
        return Collections.unmodifiableList(observerList);
    }

    public Set<SpecialGameRules> getSpecialGameRules() {
        return Collections.unmodifiableSet(SPECIAL_GAME_RULES);
    }

    public boolean isGameDone() {
        return KingStatus.CHECKMATE.equals(whiteKingStatus) || KingStatus.CHECKMATE.equals(blackKingStatus) || isGameDraw();
    }

}
