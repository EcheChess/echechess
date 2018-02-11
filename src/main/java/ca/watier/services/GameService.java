/*
 *    Copyright 2014 - 2018 Yannick Watier
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

import ca.watier.echesscommon.enums.*;
import ca.watier.echesscommon.interfaces.WebSocketService;
import ca.watier.echesscommon.sessions.Player;
import ca.watier.echesscommon.utils.Assert;
import ca.watier.game.CustomPieceWithStandardRulesHandler;
import ca.watier.game.GameConstraints;
import ca.watier.game.GenericGameHandler;
import ca.watier.responses.BooleanResponse;
import ca.watier.responses.DualValueResponse;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static ca.watier.echesscommon.enums.ChessEventMessage.*;
import static ca.watier.echesscommon.utils.Constants.*;


/**
 * Created by yannick on 4/17/2017.
 */

@Service
public class GameService {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(GameService.class);
    private final Map<UUID, GenericGameHandler> GAMES_HANDLER_MAP = new HashMap<>();
    private final GameConstraints CONSTRAINT_SERVICE;
    private final WebSocketService WEB_SOCKET_SERVICE;

    @Autowired
    public GameService(GameConstraints gameConstraints, WebSocketService webSocketService) {
        this.CONSTRAINT_SERVICE = gameConstraints;
        this.WEB_SOCKET_SERVICE = webSocketService;
    }

    /**
     * Create a new game, and associate it to the player
     *
     * @param player
     * @param specialGamePieces - If null, create a {@link GenericGameHandler}
     * @param side
     * @param againstComputer
     * @param observers
     */
    public UUID createNewGame(Player player, String specialGamePieces, Side side, boolean againstComputer, boolean observers) {
        Assert.assertNotNull(player, side);

        GameType gameType = GameType.CLASSIC;
        GenericGameHandler genericGameHandler;

        if (specialGamePieces != null && !specialGamePieces.isEmpty()) {
            gameType = GameType.SPECIAL;

            CustomPieceWithStandardRulesHandler customPieceWithStandardRulesHandler = new CustomPieceWithStandardRulesHandler(CONSTRAINT_SERVICE, WEB_SOCKET_SERVICE);
            customPieceWithStandardRulesHandler.setPieces(specialGamePieces);
            genericGameHandler = customPieceWithStandardRulesHandler;
        } else {
            genericGameHandler = new GenericGameHandler(CONSTRAINT_SERVICE, WEB_SOCKET_SERVICE);
        }

        UUID uui = UUID.randomUUID();
        genericGameHandler.setGameType(gameType);
        genericGameHandler.setUuid(uui.toString());
        GAMES_HANDLER_MAP.put(uui, genericGameHandler);
        player.addCreatedGame(uui);

        genericGameHandler.setPlayerToSide(player, side);
        genericGameHandler.setAllowOtherToJoin(!againstComputer);
        genericGameHandler.setAllowObservers(observers);


        return uui;
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
     * @return
     */
    public BooleanResponse movePiece(CasePosition from, CasePosition to, String uuid, Player player) {
        Assert.assertNotNull(from, to, player);
        Assert.assertNotEmpty(uuid);

        GenericGameHandler gameFromUuid = getGameFromUuid(uuid);
        Side playerSide = getPlayerSide(uuid, player);
        Assert.assertNotNull(gameFromUuid);

        if (!gameFromUuid.hasPlayer(player) || gameFromUuid.isGamePaused() || gameFromUuid.isGameDraw()) {
            return BooleanResponse.NO;
        } else if (gameFromUuid.isGameDone()) {
            WEB_SOCKET_SERVICE.fireSideEvent(uuid, playerSide, GAME_WON_EVENT_MOVE, GAME_ENDED);
            return BooleanResponse.NO;
        } else if (KingStatus.STALEMATE.equals(gameFromUuid.getKingStatus(playerSide, true))) {
            WEB_SOCKET_SERVICE.fireSideEvent(uuid, playerSide, GAME_WON_EVENT_MOVE, PLAYER_KING_STALEMATE);
            return BooleanResponse.NO;
        }

        boolean isMoved = MoveType.isMoved(gameFromUuid.movePiece(from, to, gameFromUuid.getPlayerSide(player)));
        return BooleanResponse.getResponse(isMoved);
    }

    /**
     * Get the game associated to the uuid
     *
     * @param uuid
     * @return
     */
    public GenericGameHandler getGameFromUuid(String uuid) {
        Assert.assertNotEmpty(uuid);
        return getGameFromUuid(UUID.fromString(uuid));
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
     * Get the game associated to the uuid
     *
     * @param uuid
     * @return
     */
    public GenericGameHandler getGameFromUuid(UUID uuid) {
        Assert.assertNotNull(uuid);

        return GAMES_HANDLER_MAP.get(uuid);
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
                        gameFromUuid.getAllAvailableMoves(from, playerSide);

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
            return BooleanResponse.NO;
        }

        boolean allowObservers = gameFromUuid.isAllowObservers();
        boolean allowOtherToJoin = gameFromUuid.isAllowOtherToJoin();

        if ((!allowOtherToJoin && !allowObservers) ||
                (allowOtherToJoin && !allowObservers && Side.OBSERVER.equals(side)) ||
                (!allowOtherToJoin && (Side.BLACK.equals(side) || Side.WHITE.equals(side)))) {
            WEB_SOCKET_SERVICE.fireUiEvent(uiUuid, TRY_JOIN_GAME, NOT_AUTHORIZED_TO_JOIN);
            return BooleanResponse.NO;
        }

        UUID gameUuid = UUID.fromString(uuid);
        if (!player.getCreatedGameList().contains(gameUuid) && !player.getJoinedGameList().contains(gameUuid)) {
            joined = gameFromUuid.setPlayerToSide(player, side);
        }

        if (joined) {
            WEB_SOCKET_SERVICE.fireGameEvent(uuid, PLAYER_JOINED, String.format(NEW_PLAYER_JOINED_SIDE, side));
            WEB_SOCKET_SERVICE.fireUiEvent(uiUuid, PLAYER_JOINED, String.format(JOINING_GAME, uuid));
            player.addJoinedGame(gameUuid);
        }

        return BooleanResponse.getResponse(joined);
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

        return BooleanResponse.getResponse(isGameExist && response);
    }

    /**
     * Used when we need to upgrade a piece in the board (example: pawn promotion)
     *
     * @param uuid
     * @param piece
     * @param player
     * @return
     */
    public BooleanResponse upgradePiece(CasePosition to, String uuid, String piece, Player player) {
        Assert.assertNotNull(to, player);
        Assert.assertNotEmpty(uuid);
        Assert.assertNotEmpty(piece);

        GenericGameHandler gameFromUuid = getGameFromUuid(uuid);
        Side playerSide = gameFromUuid.getPlayerSide(player);

        String finalPieceName = null;

        switch (playerSide) {
            case WHITE:
                finalPieceName = "W_" + piece.toUpperCase();
                break;
            case BLACK:
                finalPieceName = "B_" + piece.toUpperCase();
                break;
            case OBSERVER:
            default:
                break;
        }


        boolean isChanged = false;

        Assert.assertNotEmpty(finalPieceName);

        try {
            isChanged = gameFromUuid.upgradePiece(to, Pieces.valueOf(finalPieceName), playerSide);

            if (isChanged) { //Refresh the boards
                WEB_SOCKET_SERVICE.fireGameEvent(uuid, REFRESH_BOARD);
            }

        } catch (IllegalArgumentException ex) {
            LOGGER.error(ex.toString(), ex);
        }

        return BooleanResponse.getResponse(isChanged);
    }
}
