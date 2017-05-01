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

import ca.watier.defassert.Assert;
import ca.watier.enums.CasePosition;
import ca.watier.game.GameHandler;
import ca.watier.sessions.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by yannick on 4/17/2017.
 */

@Service
public class GameService {
    private final static Map<UUID, GameHandler> GAMES_HANDLER_MAP = new HashMap<>();

    private final ConstraintService constraintService;

    @Autowired
    public GameService(ConstraintService constraintService) {
        this.constraintService = constraintService;
    }

    public GameHandler createNewGame(Player player) {
        GameHandler gameHandler = new GameHandler();
        UUID uui = UUID.randomUUID();
        gameHandler.setUuid(uui.toString());
        GAMES_HANDLER_MAP.put(uui, gameHandler);
        player.addCreatedGame(uui);

        return gameHandler;
    }

    public GameHandler getGameFromUuid(String uuid) {
        Assert.assertNotEmpty(uuid);
        UUID key = UUID.fromString(uuid);

        return GAMES_HANDLER_MAP.get(key);
    }

    public Map<UUID, GameHandler> getAllGames() {
        return GAMES_HANDLER_MAP;
    }

    public boolean movePiece(CasePosition from, CasePosition to, String uuid, Player player) {
        Assert.assertNotNull(from, to);
        Assert.assertNotEmpty(uuid);

        GameHandler gameFromUuid = getGameFromUuid(uuid);
        Assert.assertNotNull(gameFromUuid);

        return constraintService.movePiece(from, to, gameFromUuid.getPlayerSide(player), gameFromUuid);
    }
}
