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
import ca.watier.utils.Assert;
import ca.watier.utils.GameUtils;
import ca.watier.utils.MultiArrayMap;
import ca.watier.utils.Pair;

import java.util.*;

import static ca.watier.enums.Side.BLACK;
import static ca.watier.enums.Side.WHITE;

/**
 * Created by yannick on 5/5/2017.
 */
public class GenericGameHandler {
    private final ConstraintService CONSTRAINT_SERVICE;
    private final Set<SpecialGameRules> SPECIAL_GAME_RULES;
    private final Player playerWhoCreatedGame;
    protected Map<CasePosition, Pieces> CURRENT_PIECES_LOCATION;
    protected String uuid;
    private KingStatus blackKingStatus, whiteKingStatus;
    private boolean allowOtherToJoin = false;
    private boolean allowObservers = false;
    private Side currentAllowedMoveSide = WHITE;
    protected Player playerWhite, playerBlack;
    private List<Player> observerList;
    private short blackPlayerPoint = 0, whitePlayerPoint = 0;
    private GameType gameType;

    public GenericGameHandler(ConstraintService constraintService, Player playerWhoCreatedGame) {
        SPECIAL_GAME_RULES = new HashSet<>();
        CURRENT_PIECES_LOCATION = GameUtils.getDefaultGame();
        observerList = new ArrayList<>();
        this.CONSTRAINT_SERVICE = constraintService;
        this.playerWhoCreatedGame = playerWhoCreatedGame;
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
                default: break;
        }
    }

    /**
     * Return a List containing all the moves for the selected piece
     *
     * @param from
     * @param playerSide
     * @param ignoreOtherPieces - Gives the full move of the piece, ignoring the other pieces
     * @return
     */
    public List<CasePosition> getAllAvailableMoves(CasePosition from, Side playerSide, boolean ignoreOtherPieces) {
        List<CasePosition> positions = new ArrayList<>();

        Pieces pieces = CURRENT_PIECES_LOCATION.get(from);

        if (pieces == null || !pieces.getSide().equals(playerSide)) {
            return positions;
        }

        for (CasePosition position : CasePosition.values()) {
            if (!from.equals(position) && ignoreOtherPieces ?
                    isPieceMovableToWithIgnoreOtherPieces(from, position, playerSide) :
                    isPieceMovableTo(from, position, playerSide)) {
                positions.add(position);
            }
        }

        return positions;
    }

    public final boolean isPieceMovableToWithIgnoreOtherPieces(CasePosition from, CasePosition to, Side playerSide) {
        return CONSTRAINT_SERVICE.isPieceMovableTo(from, to, playerSide, CURRENT_PIECES_LOCATION, MoveMode.NORMAL_OR_ATTACK_MOVE);
    }

    public final boolean isPieceMovableTo(CasePosition from, CasePosition to, Side playerSide) {
        return CONSTRAINT_SERVICE.isPieceMovableTo(from, to, playerSide, CURRENT_PIECES_LOCATION, MoveMode.NORMAL_OR_ATTACK_MOVE);
    }

    public Map<CasePosition, Pieces> getPiecesLocation() {
        return Collections.unmodifiableMap(CURRENT_PIECES_LOCATION);
    }

    public boolean movePiece(CasePosition from, CasePosition to, Side playerSide) {
        return true;
    }

    protected final void changeAllowedMoveSide() {
        if (BLACK.equals(currentAllowedMoveSide)) {
            currentAllowedMoveSide = WHITE;
        } else {
            currentAllowedMoveSide = BLACK;
        }
    }

    protected final boolean isPlayerTurn(Side sideFrom) {
        if (isGameHaveRule(SpecialGameRules.NO_PLAYER_TURN)) {
            return true;
        }

        return currentAllowedMoveSide.equals(sideFrom);
    }

    public boolean isGameHaveRule(SpecialGameRules rule) {
        return SPECIAL_GAME_RULES.contains(rule);
    }

    public List<CasePosition> getPositionKingCanMove(Side playerSide) {
        return null;
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

    protected KingStatus getKingStatus(Side playerSide) {
        return KingStatus.OK;
    }

    /**
     * Gets the pieces / CasePosition based on a side
     *
     * @param side
     * @return
     */
    public final Map<CasePosition, Pieces> getPiecesLocation(Side side) {
        Assert.assertNotNull(side);

        Map<CasePosition, Pieces> values = new HashMap<>();

        for (Map.Entry<CasePosition, Pieces> casePositionPiecesEntry : CURRENT_PIECES_LOCATION.entrySet()) {
            CasePosition key = casePositionPiecesEntry.getKey();
            Pieces value = casePositionPiecesEntry.getValue();

            if (side.equals(value.getSide())) {
                values.put(key, value);
            }
        }

        return values;
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
            for (Map.Entry<CasePosition, Pieces> casePositionPiecesEntry : CURRENT_PIECES_LOCATION.entrySet()) {
                CasePosition key = casePositionPiecesEntry.getKey();
                Pieces value = casePositionPiecesEntry.getValue();

                Side pieceSide = value.getSide();
                if (!pieceSide.equals(sideToKeep)) {
                    continue;
                }

                if (CONSTRAINT_SERVICE.isPieceMovableTo(key, position, pieceSide, CURRENT_PIECES_LOCATION, MoveMode.IS_KING_CHECK_MODE)) {
                    values.put(position, new Pair<>(key, value));
                }
            }
        }

        return values;
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

    private boolean isPlayerAllowed(Player player) {
        return playerWhite == null || playerBlack == null;
    }

    public boolean isWhiteSet() {
        return playerWhite != null;
    }

    public boolean isBlackSet() {
        return playerBlack != null;
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

    @Override
    public String toString() {
        return "StandardGameHandler{" +
                "playerWhite=" + playerWhite +
                ", playerBlack=" + playerBlack +
                ", observerList=" + observerList +
                '}';
    }

    public void addSpecialRule(SpecialGameRules... rules) {
        Assert.assertNotEmpty(rules);
        SPECIAL_GAME_RULES.addAll(Arrays.asList(rules));
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

    public Player getPlayerWhoCreatedGame() {
        return playerWhoCreatedGame;
    }

    public boolean isGameDone() {
        return KingStatus.CHECKMATE.equals(whiteKingStatus) || KingStatus.CHECKMATE.equals(blackKingStatus);
    }

    protected void setBlackKingStatus(KingStatus blackKingStatus) {
        this.blackKingStatus = blackKingStatus;
    }

    protected void setWhiteKingStatus(KingStatus whiteKingStatus) {
        this.whiteKingStatus = whiteKingStatus;
    }
}
