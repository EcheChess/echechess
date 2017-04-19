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
import ca.watier.enums.Side;
import ca.watier.sessions.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by yannick on 4/17/2017.
 */
public class GameHandler {
    private Player playerWhite, playerBlack;
    private List<Player> observerList;

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

    public void setPlayerToSide(Player player, Side side) {
        Assert.assertNotNull(player, side);

        switch (side) {
            case BLACK: {
                removePlayerFromWhite(player);
                changePlayerToBlack(player);
                observerList.remove(player);
                break;
            }
            case WHITE: {
                removePlayerFromBlack(player);
                changePlayerToWhite(player);
                observerList.remove(player);
                break;
            }
            default: {
                removePlayerFromWhite(player);
                removePlayerFromBlack(player);
                observerList.add(player);
                break;
            }
        }
    }

    private void changePlayerToBlack(Player player) {
        if (playerBlack == null) {
            playerBlack = player;
        }
    }

    private void changePlayerToWhite(Player player) {
        if (playerWhite == null) {
            playerWhite = player;
        }
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
}
