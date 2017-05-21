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

package ca.watier.services;

import ca.watier.enums.CasePosition;
import ca.watier.enums.Side;
import ca.watier.exceptions.GameException;
import ca.watier.game.StandardGameHandler;
import ca.watier.sessions.Player;
import ca.watier.utils.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by yannick on 4/17/2017.
 */

@Service
public class GameService {
    private final static Map<UUID, StandardGameHandler> GAMES_HANDLER_MAP = new HashMap<>();

    private final ConstraintService constraintService;

    @Autowired
    public GameService(ConstraintService constraintService) {
        this.constraintService = constraintService;
    }

    /**
     * Create a new game, and associate it to tha player
     *
     * @param player
     * @return
     */
    public StandardGameHandler createNewGame(Player player) {
        StandardGameHandler normalGameHandler = new StandardGameHandler(constraintService);
        UUID uui = UUID.randomUUID();
        normalGameHandler.setUuid(uui.toString());
        GAMES_HANDLER_MAP.put(uui, normalGameHandler);
        player.addCreatedGame(uui);

        return normalGameHandler;
    }

    public Map<UUID, StandardGameHandler> getAllGames() {
        return GAMES_HANDLER_MAP;
    }

    /**
     * Moves the piece to the specified location
     *
     * @param from
     * @param to
     * @param uuid
     * @param player
     * @return
     */
    public boolean movePiece(CasePosition from, CasePosition to, String uuid, Player player) throws GameException {
        Assert.assertNotNull(from, to);
        Assert.assertNotEmpty(uuid);

        StandardGameHandler gameFromUuid = getGameFromUuid(uuid);
        Assert.assertNotNull(gameFromUuid);

        return gameFromUuid.movePiece(from, to, gameFromUuid.getPlayerSide(player));
    }

    /**
     * Get the game associated to the uuid
     *
     * @param uuid
     * @return
     */
    public StandardGameHandler getGameFromUuid(String uuid) {
        Assert.assertNotEmpty(uuid);
        UUID key = UUID.fromString(uuid);

        return GAMES_HANDLER_MAP.get(key);
    }


    /**
     * Get the side of the player for the associated game
     *
     * @param uuid
     * @return
     */
    public Side getPlayerSide(String uuid, Player player) {
        Assert.assertNotEmpty(uuid);

        StandardGameHandler standardGameHandler = GAMES_HANDLER_MAP.get(UUID.fromString(uuid));
        Assert.assertNotNull(standardGameHandler);
        return standardGameHandler.getPlayerSide(player);
    }

    /**
     * Gets all possible moves for the selected piece
     *
     * @param from
     * @param uuid
     * @param player
     * @return
     */
    public List<String> getAllAvailableMoves(CasePosition from, String uuid, Player player) {
        Assert.assertNotNull(from);
        Assert.assertNotEmpty(uuid);

        StandardGameHandler gameFromUuid = getGameFromUuid(uuid);
        Assert.assertNotNull(gameFromUuid);
        Side playerSide = gameFromUuid.getPlayerSide(player);

        List<String> values = new ArrayList<>();

        for (CasePosition casePosition : gameFromUuid.getAllAvailableMoves(from, playerSide, false)) {
            values.add(casePosition.name());
        }

        return values;
    }

}
