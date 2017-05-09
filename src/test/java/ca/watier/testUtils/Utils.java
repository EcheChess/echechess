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

package ca.watier.testUtils;

import ca.watier.enums.Side;
import ca.watier.exceptions.GameException;
import ca.watier.game.GenericGameHandler;
import ca.watier.sessions.Player;
import ca.watier.utils.Assert;

import java.util.UUID;

/**
 * Created by yannick on 5/8/2017.
 */
public class Utils {
    public static final Side BLACK = Side.BLACK;
    public static final Side WHITE = Side.WHITE;
    private static final Player blackPlayer = new Player();
    private static final Player whitePlayer = new Player();

    public static void addBothPlayerToGameAndSetUUID(GenericGameHandler gameHandler) throws GameException {
        Assert.assertNotNull(gameHandler);
        UUID uuid = UUID.randomUUID();
        blackPlayer.addJoinedGame(uuid);
        whitePlayer.addJoinedGame(uuid);
        gameHandler.setUuid(uuid.toString());
        gameHandler.setPlayerToSide(blackPlayer, BLACK);
        gameHandler.setPlayerToSide(whitePlayer, WHITE);
    }
}
