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
import ca.watier.enums.GameType;
import ca.watier.enums.Pieces;
import ca.watier.enums.Side;
import ca.watier.game.CustomPieceWithStandardRulesHandler;
import ca.watier.game.GenericGameHandler;
import ca.watier.game.StandardGameHandler;
import ca.watier.sessions.Player;
import ca.watier.utils.Assert;
import ca.watier.utils.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by yannick on 4/17/2017.
 */

@Service
public class GameService {
    private final static Map<UUID, GenericGameHandler> GAMES_HANDLER_MAP = new HashMap<>();

    private final ConstraintService constraintService;

    @Autowired
    public GameService(ConstraintService constraintService) {
        this.constraintService = constraintService;
    }

    /**
     * Create a new game, and associate it to tha player
     *
     * @param player
     * @param specialGamePieces - If null, create a StandardGameHandler
     * @return
     */
    public GenericGameHandler createNewGame(Player player, String specialGamePieces) {
        GameType gameType = GameType.CLASSIC;

        GenericGameHandler genericGameHandler;

        if (specialGamePieces != null && !specialGamePieces.isEmpty()) {
            gameType = GameType.SPECIAL;

            CustomPieceWithStandardRulesHandler customPieceWithStandardRulesHandler = new CustomPieceWithStandardRulesHandler(constraintService);

            Map<CasePosition, Pieces> positionPiecesMap = new HashMap<>();

            for (String section : specialGamePieces.split(";")) {
                String[] values = section.split(":");

                if (values.length != 2) {
                    break;
                }

                positionPiecesMap.put(CasePosition.valueOf(values[0]), Pieces.valueOf(values[1]));
            }
            customPieceWithStandardRulesHandler.setPieces(positionPiecesMap);
            genericGameHandler = customPieceWithStandardRulesHandler;
        } else {
            genericGameHandler = new StandardGameHandler(constraintService);
        }

        UUID uui = UUID.randomUUID();
        genericGameHandler.setGameType(gameType);
        genericGameHandler.setUuid(uui.toString());
        GAMES_HANDLER_MAP.put(uui, genericGameHandler);
        player.addCreatedGame(uui);

        return genericGameHandler;
    }

    public Map<UUID, GenericGameHandler> getAllGames() {
        return GAMES_HANDLER_MAP;
    }

    /**
     * Moves the piece to the specified location
     *
     * @param from
     * @param to
     * @param uuid
     * @param player
     * @return - A {@link Pair} Containing if the piece can move, and if the game is ended
     */
    public Pair<Boolean, Boolean> movePiece(CasePosition from, CasePosition to, String uuid, Player player) {
        Assert.assertNotNull(from, to);
        Assert.assertNotEmpty(uuid);

        GenericGameHandler gameFromUuid = getGameFromUuid(uuid);
        Assert.assertNotNull(gameFromUuid);

        return new Pair<>(gameFromUuid.movePiece(from, to, gameFromUuid.getPlayerSide(player)), gameFromUuid.isGameDone());
    }

    /**
     * Get the game associated to the uuid
     *
     * @param uuid
     * @return
     */
    public GenericGameHandler getGameFromUuid(String uuid) {
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

        GenericGameHandler standardGameHandler = GAMES_HANDLER_MAP.get(UUID.fromString(uuid));
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

        GenericGameHandler gameFromUuid = getGameFromUuid(uuid);
        Assert.assertNotNull(gameFromUuid);
        Side playerSide = gameFromUuid.getPlayerSide(player);

        List<String> values = new ArrayList<>();

        for (CasePosition casePosition : gameFromUuid.getAllAvailableMoves(from, playerSide, false)) {
            values.add(casePosition.name());
        }

        return values;
    }

}
