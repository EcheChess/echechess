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
import ca.watier.responses.BooleanResponse;
import ca.watier.responses.DualValueResponse;
import ca.watier.sessions.Player;
import ca.watier.utils.Assert;
import ca.watier.utils.Constants;
import ca.watier.utils.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static ca.watier.enums.ChessEventMessage.*;
import static ca.watier.enums.ChessEventMessage.PLAYER_TURN;
import static ca.watier.utils.Constants.*;

/**
 * Created by yannick on 4/17/2017.
 */

@Service
public class GameService {
    private final Map<UUID, GenericGameHandler> GAMES_HANDLER_MAP = new HashMap<>();
    private final ConstraintService constraintService;
    private final WebSocketService webSocketService;

    @Autowired
    public GameService(ConstraintService constraintService, WebSocketService webSocketService) {
        this.constraintService = constraintService;
        this.webSocketService = webSocketService;
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

            CustomPieceWithStandardRulesHandler customPieceWithStandardRulesHandler = new CustomPieceWithStandardRulesHandler(constraintService, player);
            customPieceWithStandardRulesHandler.setPieces(specialGamePieces);
            genericGameHandler = customPieceWithStandardRulesHandler;
        } else {
            genericGameHandler = new StandardGameHandler(constraintService, player);
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

        boolean isMoved = false;

        Side playerSide = getPlayerSide(uuid, player);

        if (gameFromUuid.isGameDone()) {
            webSocketService.fireSideEvent(uuid, playerSide, GAME_WON_EVENT_MOVE, GAME_ENDED);
        } else {
            isMoved = gameFromUuid.movePiece(from, to, gameFromUuid.getPlayerSide(player));
        }

        if (isMoved) {
            webSocketService.fireGameEvent(uuid, MOVE, String.format(PLAYER_MOVE, playerSide, from, to));
            webSocketService.fireSideEvent(uuid, Side.getOtherPlayerSide(playerSide), PLAYER_TURN, Constants.PLAYER_TURN);
            webSocketService.fireGameEvent(uuid, SCORE_UPDATE, gameFromUuid.getGameScore());
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
        List<String> values = new ArrayList<>();

        if (!gameFromUuid.hasPlayer(player)) {
            return values;
        }

        Side playerSide = gameFromUuid.getPlayerSide(player);

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

    public BooleanResponse joinGame(String uuid, Side side, String uiUuid, Player player) {
        Assert.assertNotNull(side, player);
        Assert.assertNotEmpty(uuid);

        boolean joined = false;
        GenericGameHandler gameFromUuid = getGameFromUuid(uuid);

        if (gameFromUuid == null) {
            return new BooleanResponse(false);
        }


        boolean allowObservers = gameFromUuid.isAllowObservers();
        boolean allowOtherToJoin = gameFromUuid.isAllowOtherToJoin();


        if ((!allowOtherToJoin && !allowObservers) ||
                (allowOtherToJoin && !allowObservers && Side.OBSERVER.equals(side)) ||
                (!allowOtherToJoin && (Side.BLACK.equals(side) || Side.WHITE.equals(side)))) {
            webSocketService.fireUiEvent(uiUuid, TRY_JOIN_GAME, NOT_AUTHORIZED_TO_JOIN);
            return new BooleanResponse(false);
        }

        UUID gameUuid = UUID.fromString(uuid);
        if (!player.getCreatedGameList().contains(gameUuid) && !player.getJoinedGameList().contains(gameUuid)) {
            joined = gameFromUuid.setPlayerToSide(player, side);
        }

        if (joined) {
            webSocketService.fireGameEvent(uuid, PLAYER_JOINED, String.format(NEW_PLAYER_JOINED_SIDE, side));
            webSocketService.fireUiEvent(uiUuid, PLAYER_JOINED, String.format(JOINING_GAME, uuid));
            player.addJoinedGame(gameUuid);
        }


        return new BooleanResponse(joined);
    }

    public List<DualValueResponse> getPieceLocations(String uuid, Player player) {
        Assert.assertNotNull(player);
        Assert.assertNotEmpty(uuid);

        GenericGameHandler gameFromUuid = getGameFromUuid(uuid);

        List<DualValueResponse> values = new ArrayList<>();

        if (gameFromUuid == null || !gameFromUuid.hasPlayer(player)) {
            return values;
        }

        for (Map.Entry<CasePosition, Pieces> casePositionPiecesEntry : gameFromUuid.getPiecesLocation().entrySet()) {
            values.add(new DualValueResponse(casePositionPiecesEntry.getKey(), casePositionPiecesEntry.getValue(), ""));
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
}
