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

import ca.watier.defassert.Assert;
import ca.watier.enums.CasePosition;
import ca.watier.enums.Pieces;
import ca.watier.enums.Side;
import ca.watier.sessions.Player;
import ca.watier.utils.GameUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by yannick on 4/17/2017.
 */
public class GameHandler {

    @JsonIgnore
    private Player playerWhite, playerBlack;
    @JsonIgnore
    private List<Player> observerList;
    @JsonIgnore
    private final Map<CasePosition, Pieces> CURRENT_PIECES_LOCATION = GameUtils.getDefaultGame();
    private String uuid;
    private boolean allowOtherToJoin = false;
    private boolean allowObservers = false;

    private Side currentAllowedMoveSide = Side.WHITE;

    public GameHandler() {
        observerList = new ArrayList<>();
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

    public boolean setPlayerToSide(Player player, Side side) {
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

    private boolean changePlayerToBlack(Player player) {
        if (playerBlack == null) {
            playerBlack = player;
            return true;
        }

        return false;
    }

    private boolean changePlayerToWhite(Player player) {
        if (playerWhite == null) {
            playerWhite = player;
            return true;
        }

        return false;
    }

    private void removePlayerFromWhite(Player player) {
        if (playerWhite == player) {
            playerWhite = null;
        }
    }

    private void removePlayerFromBlack(Player player) {
        if (playerBlack == player) {
            playerBlack = null;
        }
    }

    public List<Player> getObserverList() {
        return Collections.unmodifiableList(observerList);
    }

    @Override
    public String toString() {
        return "GameHandler{" +
                "playerWhite=" + playerWhite +
                ", playerBlack=" + playerBlack +
                ", observerList=" + observerList +
                '}';
    }

    /**
     * Check if the player can movePiece the piece at the specified location
     *
     * @param player
     * @param from
     * @return
     */
    public boolean playerCanMovePiece(Player player, CasePosition from) {
        Assert.assertNotNull(player, from);
        Pieces pieceToMove = CURRENT_PIECES_LOCATION.get(from);

        return pieceToMove != null && pieceToMove.getSide().equals(getPlayerSide(player));

    }

    /**
     * Get the side of the player, null if not available
     *
     * @param player
     * @return
     */
    public Side getPlayerSide(Player player) {
        Side side = null;

        if (playerWhite == player) {
            side = Side.WHITE;
        } else if (playerBlack == player) {
            side = Side.BLACK;
        } else if (observerList.contains(player)) {
            side = Side.OBSERVER;
        }

        return side;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    private void changeAllowedMoveSide() {
        if (Side.BLACK.equals(currentAllowedMoveSide)) {
            currentAllowedMoveSide = Side.WHITE;
        } else {
            currentAllowedMoveSide = Side.BLACK;
        }
    }

    public boolean movePiece(CasePosition from, CasePosition to, Side playerSide) {
        Assert.assertNotNull(from, to);

        Pieces piecesFrom = CURRENT_PIECES_LOCATION.get(from);
        boolean isMoved = false;

        if (piecesFrom != null) {
            Side sideFrom = piecesFrom.getSide();

            if (currentAllowedMoveSide.equals(sideFrom) && sideFrom.equals(playerSide)) {
                CURRENT_PIECES_LOCATION.remove(from);
                CURRENT_PIECES_LOCATION.put(to, piecesFrom);
                changeAllowedMoveSide();

                isMoved = true;
            }
        }

        return isMoved;
    }

    public Map<CasePosition, Pieces> getPiecesLocation() {
        return Collections.unmodifiableMap(CURRENT_PIECES_LOCATION);
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
}
