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

import ca.watier.enums.*;
import ca.watier.game.CustomPieceWithStandardRulesHandler;
import ca.watier.game.GenericGameHandler;
import ca.watier.game.StandardGameHandler;
import ca.watier.responses.BooleanResponse;
import ca.watier.responses.ChessEvent;
import ca.watier.responses.DualValueResponse;
import ca.watier.sessions.Player;
import ca.watier.utils.Assert;
import ca.watier.utils.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.*;

import static ca.watier.enums.ChessEventMessage.*;

/**
 * Created by yannick on 4/17/2017.
 */

@Service
public class GameService {
    private final static Map<UUID, GenericGameHandler> GAMES_HANDLER_MAP = new HashMap<>();
    private final ConstraintService constraintService;
    private final SimpMessagingTemplate template;

    @Autowired
    public GameService(ConstraintService constraintService, SimpMessagingTemplate template) {
        this.constraintService = constraintService;
        this.template = template;
    }

    /**
     * Create a new game, and associate it to the player
     *
     * @param player
     * @param specialGamePieces - If null, create a StandardGameHandler
     * @param side
     * @param againstComputer
     * @param observers
     */
    public GenericGameHandler createNewGame(Player player, String specialGamePieces, Side side, boolean againstComputer, boolean observers) {
        Assert.assertNotNull(player, side);

        GameType gameType = GameType.CLASSIC;
        GenericGameHandler genericGameHandler;

        if (specialGamePieces != null && !specialGamePieces.isEmpty()) {
            gameType = GameType.SPECIAL;

            CustomPieceWithStandardRulesHandler customPieceWithStandardRulesHandler = new CustomPieceWithStandardRulesHandler(constraintService);
            customPieceWithStandardRulesHandler.setPieces(specialGamePieces);
            genericGameHandler = customPieceWithStandardRulesHandler;
        } else {
            genericGameHandler = new StandardGameHandler(constraintService);
        }

        UUID uui = UUID.randomUUID();
        genericGameHandler.setGameType(gameType);
        genericGameHandler.setUuid(uui.toString());
        GAMES_HANDLER_MAP.put(uui, genericGameHandler);
        player.addCreatedGame(uui);

        genericGameHandler.setPlayerToSide(player, side);
        genericGameHandler.setAllowOtherToJoin(!againstComputer);
        genericGameHandler.setAllowObservers(observers);


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
    public BooleanResponse movePiece(CasePosition from, CasePosition to, String uuid, Player player) {
        Assert.assertNotNull(from, to, player);
        Assert.assertNotEmpty(uuid);


        GenericGameHandler gameFromUuid = getGameFromUuid(uuid);
        Assert.assertNotNull(gameFromUuid);

        if (!gameFromUuid.hasPlayer(player)) {
            return new BooleanResponse(false);
        }

        boolean isMoved = gameFromUuid.movePiece(from, to, gameFromUuid.getPlayerSide(player));

        if (gameFromUuid.isGameDone()) {
            fireGameChessEvent(uuid, GAME_WON_EVENT_MOVE, "The game is ended !");
        }

        if (isMoved) {
            Side playerSide = getPlayerSide(uuid, player);
            fireGameChessEvent(uuid, MOVE, String.format("%s player moved %s to %s", playerSide, from, to));
            fireSideChessEvent(uuid, Side.getOtherPlayerSide(playerSide), PLAYER_TURN, "It's your turn !");
        }

        return new BooleanResponse(isMoved);
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

    private void fireGameChessEvent(String uuid, ChessEventMessage evtMessage, String message) {
        template.convertAndSend("/topic/" + uuid, new ChessEvent(evtMessage, message));
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

    private void fireSideChessEvent(String uuid, Side side, ChessEventMessage evtMessage, String message) {
        Assert.assertNotNull(side, evtMessage);
        Assert.assertNotEmpty(uuid);
        Assert.assertNotEmpty(message);

        template.convertAndSend("/topic/" + uuid + '/' + side, new ChessEvent(evtMessage, message));
    }

    private void firePrivateChessEvent(ChessEventMessage evtMessage, String message) {
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();

        template.convertAndSend("/topic/" + sessionId, new ChessEvent(evtMessage, message));
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


        if (!gameFromUuid.hasPlayer(player)) {
            return null;
        }

        Side playerSide = gameFromUuid.getPlayerSide(player);

        List<String> values = new ArrayList<>();

        Map<CasePosition, Pieces> piecesLocation = gameFromUuid.getPiecesLocation();
        Pieces pieces = piecesLocation.get(from);
        Assert.assertNotNull(pieces);

        if (!pieces.getSide().equals(playerSide)) {
            return values;
        }

        List<CasePosition> positions =
                Pieces.isKing(pieces) ?
                        gameFromUuid.getPositionKingCanMove(playerSide) :
                        gameFromUuid.getAllAvailableMoves(from, playerSide, false);

        for (CasePosition casePosition : positions) {
            values.add(casePosition.name());
        }

        return values;
    }

    public BooleanResponse joinGame(String uuid, Side side, Player player) {
        Assert.assertNotNull(side, player);
        Assert.assertNotEmpty(uuid);

        boolean joined = false;
        GenericGameHandler gameFromUuid = getGameFromUuid(uuid);

        UUID gameUuid = UUID.fromString(uuid);
        if (gameFromUuid != null && (gameFromUuid.isAllowOtherToJoin() || gameFromUuid.isAllowObservers()) &&
                !player.getCreatedGameList().contains(gameUuid) && !player.getJoinedGameList().contains(gameUuid)) {
            player.addJoinedGame(gameUuid);
            joined = gameFromUuid.setPlayerToSide(player, side);
        }

        if (joined) {
            fireGameChessEvent(uuid, PLAYER_JOINED, String.format("New player joined the %s side", side));
        }

        return new BooleanResponse(joined, "");
    }

    public List<DualValueResponse> getPieceLocations(String uuid, Player player) {
        Assert.assertNotNull(player);
        Assert.assertNotEmpty(uuid);

        GenericGameHandler gameFromUuid = getGameFromUuid(uuid);
        List<DualValueResponse> values = null;

        if (gameFromUuid != null) {
            values = new ArrayList<>();

            if ((!gameFromUuid.isAllowObservers() || !gameFromUuid.isAllowOtherToJoin()) && !gameFromUuid.hasPlayer(player)) {
                return null;
            }

            for (Map.Entry<CasePosition, Pieces> casePositionPiecesEntry : gameFromUuid.getPiecesLocation().entrySet()) {
                values.add(new DualValueResponse(casePositionPiecesEntry.getKey(), casePositionPiecesEntry.getValue(), ""));
            }
        }

        return values;
    }

    public BooleanResponse setSideOfPlayer(Player player, Side side, String uuid) {

        GenericGameHandler game = getGameFromUuid(uuid);
        boolean isGameExist = game != null;
        boolean response = false;

        if (isGameExist) {
            response = game.setPlayerToSide(player, side);
        }

        return new BooleanResponse(isGameExist && response);
    }

    public SimpMessagingTemplate getTemplate() {
        return template;
    }
}
