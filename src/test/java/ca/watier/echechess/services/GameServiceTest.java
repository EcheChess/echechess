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

package ca.watier.echechess.services;

import ca.watier.echechess.engine.exceptions.FenParserException;
import ca.watier.echechess.models.GenericPiecesModel;
import ca.watier.echechess.clients.MessageClient;
import ca.watier.echechess.common.enums.CasePosition;
import ca.watier.echechess.common.enums.MoveType;
import ca.watier.echechess.common.enums.Pieces;
import ca.watier.echechess.common.impl.WebSocketServiceTestImpl;
import ca.watier.echechess.common.interfaces.WebSocketService;
import ca.watier.echechess.common.responses.BooleanResponse;
import ca.watier.echechess.common.sessions.Player;
import ca.watier.echechess.common.tests.GameTest;
import ca.watier.echechess.common.utils.Constants;
import ca.watier.echechess.engine.engines.GenericGameHandler;
import ca.watier.echechess.engine.factories.GameConstraintFactory;
import ca.watier.echechess.engine.interfaces.GameConstraint;
import ca.watier.echechess.engine.utils.GameUtils;
import ca.watier.repository.KeyValueRepository;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static ca.watier.echechess.common.enums.CasePosition.*;
import static ca.watier.echechess.common.enums.ChessEventMessage.REFRESH_BOARD;
import static ca.watier.echechess.common.enums.GameType.CLASSIC;
import static ca.watier.echechess.common.enums.GameType.SPECIAL;
import static ca.watier.echechess.common.enums.Pieces.B_KING;
import static ca.watier.echechess.common.enums.Pieces.W_QUEEN;
import static ca.watier.echechess.common.enums.Side.OBSERVER;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

@ActiveProfiles(profiles = "test")
@RunWith(MockitoJUnitRunner.class)
public class GameServiceTest extends GameTest {
    private static final BooleanResponse FALSE_BOOLEAN_RESPONSE = new BooleanResponse(false);
    private static final BooleanResponse TRUE_BOOLEAN_RESPONSE = new BooleanResponse(true);
    private static final GameConstraint CONSTRAINT_SERVICE = GameConstraintFactory.getDefaultGameConstraint();
    private WebSocketService currentWebSocketService;
    private GameService gameService;
    private Player player1;
    private KeyValueRepository redisGameRepository = new KeyValueRepository();

    @Mock
    private MessageClient messageClient;


    @Before
    public void setup() {
        player1 = new Player(UUID.randomUUID().toString());
        currentWebSocketService = new WebSocketServiceTestImpl();
        gameService = new GameService(
                CONSTRAINT_SERVICE,
                currentWebSocketService,
                redisGameRepository,
                messageClient);
    }

    @Test
    public void upgradePiece() throws FenParserException {
        WebSocketServiceTestImpl currentWebSocketService = (WebSocketServiceTestImpl) this.currentWebSocketService;
        UUID gameUuid = gameService.createNewGame("K7/6P1/8/8/8/8/6p1/k7 w", WHITE, false, false, player1);
        GenericGameHandler gameFromUuid = gameService.getGameFromUuid(gameUuid.toString());

        Assert.assertEquals(MoveType.PAWN_PROMOTION, gameFromUuid.movePiece(G7, G8, WHITE));

        gameFromUuid.isGameDone();

        String uuid = gameFromUuid.getUuid();
        assertTrue(gameFromUuid.isGamePaused());
        Assert.assertTrue(gameService.upgradePiece(G8, uuid, GenericPiecesModel.QUEEN, player1));
        Assert.assertFalse(gameFromUuid.isGamePaused());

        Assertions.assertThat(currentWebSocketService.getMessages()).containsOnly(
                "G8",
                String.format(Constants.GAME_PAUSED_PAWN_PROMOTION, "WHITE"),
                "G8",
                String.format(Constants.GAME_PAUSED_PAWN_PROMOTION, "WHITE"),
                "It's your turn !", //Black turn
                EMPTY_GAME_SCORE_RESPONSE,
                REFRESH_BOARD);
    }

    @Test
    public void setSideOfPlayerTest() throws FenParserException {
        Player player1 = new Player(UUID.randomUUID().toString());
        Player player2 = new Player(UUID.randomUUID().toString());

        UUID gameUuid = gameService.createNewGame("", WHITE, false, false, player1);
        String uuid = gameUuid.toString();

        gameService.setSideOfPlayer(BLACK, uuid, player1);

        Map<UUID, GenericGameHandler> mapOfGames = gameService.getAllGames();
        Set<UUID> allIdGamesFromGameService = mapOfGames.keySet();

        //Get the current game
        Set<UUID> gameListIdFromPlayer = new HashSet<>(player1.getCreatedGameList());
        List<GenericGameHandler> allGames = new ArrayList<>(mapOfGames.values());
        GenericGameHandler normalGameHandler = allGames.get(0);

        //Check if the player1 is set to black
        assertEquals(player1, normalGameHandler.getPlayerBlack());
        assertNull(normalGameHandler.getPlayerWhite());
        assertTrue(normalGameHandler.getObserverList().isEmpty());

        gameService.setSideOfPlayer(WHITE, uuid, player1);

        //Check if the player1 is set to white (was black before)
        assertNull(normalGameHandler.getPlayerBlack());
        assertEquals(player1, normalGameHandler.getPlayerWhite());
        assertTrue(normalGameHandler.getObserverList().isEmpty());

        //compare the service vs player1 data
        assertEquals(allIdGamesFromGameService, gameListIdFromPlayer);
        assertEquals(1, allIdGamesFromGameService.size());
        assertEquals(1, gameListIdFromPlayer.size());

        //Try to associate the white to the player 2 (already set to player 1)
        gameService.setSideOfPlayer(WHITE, uuid, player2);

        //Check if the player1 is still associated to black, player2 not set yet
        assertNull(normalGameHandler.getPlayerBlack());
        assertEquals(player1, normalGameHandler.getPlayerWhite());
        assertTrue(normalGameHandler.getObserverList().isEmpty());

        //Try to associate the black to the player 2 (not set yet)
        gameService.setSideOfPlayer(BLACK, uuid, player2);

        assertEquals(player2, normalGameHandler.getPlayerBlack());
        assertEquals(player1, normalGameHandler.getPlayerWhite());
        assertTrue(normalGameHandler.getObserverList().isEmpty());

        //Change both of the player to observers
        gameService.setSideOfPlayer(OBSERVER, uuid, player1);
        gameService.setSideOfPlayer(OBSERVER, uuid, player2);

        assertNull(normalGameHandler.getPlayerBlack());
        assertNull(normalGameHandler.getPlayerWhite());
        assertTrue(normalGameHandler.getObserverList().containsAll(asList(player1, player2)));
    }

    @Test
    public void createNewGameTest() throws FenParserException {
        Player player1 = new Player(UUID.randomUUID().toString());

        UUID specialGame = gameService.createNewGame("", WHITE, false, false, player1);
        GenericGameHandler gameFromUuid = gameService.getGameFromUuid(specialGame.toString());

        UUID normalGame = gameService.createNewGame("8/3Q4/8/1Q1k1Q2/8/3Q4/8/8 w", WHITE, false, false, player1);
        GenericGameHandler gameFromUuidCustom = gameService.getGameFromUuid(normalGame.toString());


        //Check if the game is associated with the player
        assertThat(specialGame).isNotNull();
        assertThat(normalGame).isNotNull();
        assertThat(player1.getCreatedGameList()).containsOnly(specialGame, normalGame);

        //Check the type of the game
        Assert.assertNotNull(gameFromUuidCustom);
        assertEquals(SPECIAL, gameFromUuidCustom.getGameType());

        Assert.assertNotNull(gameFromUuidCustom);
        assertEquals(CLASSIC, gameFromUuid.getGameType());

        //Check if the pieces are set
        Map<CasePosition, Pieces> piecesLocationCustom = gameFromUuidCustom.getPiecesLocation();
        assertThat(piecesLocationCustom).isNotNull();
        assertThat(piecesLocationCustom).hasSize(5);
        assertEquals(W_QUEEN, piecesLocationCustom.get(B5));
        assertEquals(W_QUEEN, piecesLocationCustom.get(D3));
        assertEquals(W_QUEEN, piecesLocationCustom.get(F5));
        assertEquals(W_QUEEN, piecesLocationCustom.get(D7));
        assertEquals(B_KING, piecesLocationCustom.get(D5));

        Map<CasePosition, Pieces> piecesLocation = gameFromUuid.getPiecesLocation();
        assertThat(piecesLocation).isNotNull();
        assertThat(piecesLocation).hasSize(32);
        assertEquals(GameUtils.getDefaultGame(), piecesLocation);
    }

    @Test
    public void joinGameTest() throws FenParserException {
        WebSocketServiceTestImpl currentWebSocketService = (WebSocketServiceTestImpl) this.currentWebSocketService;

        Player player1 = new Player(UUID.randomUUID().toString());
        Player player2 = new Player(UUID.randomUUID().toString());
        Player playerObserver = new Player(UUID.randomUUID().toString());

        List<Object> messages = currentWebSocketService.getMessages();

        //Able to join any side, except WHITE
        UUID gameUuid1 = gameService.createNewGame("", WHITE, false, true, player1);
        GenericGameHandler game1 = gameService.getGameFromUuid(gameUuid1.toString());
        String uuidGame1 = game1.getUuid();

        assertEquals(FALSE_BOOLEAN_RESPONSE, gameService.joinGame(uuidGame1, WHITE, "8ddf1de9-d366-40c5-acdb-703e1438f543", player2)); //Unable to join, the WHITE is already taken
        assertEquals(TRUE_BOOLEAN_RESPONSE, gameService.joinGame(uuidGame1, BLACK, "8ddf1de9-d366-40c5-acdb-703e1438f543", player2)); // Valid choice
        assertEquals(TRUE_BOOLEAN_RESPONSE, gameService.joinGame(uuidGame1, OBSERVER, "8ddf1de9-d366-40c5-acdb-703e1438f543", playerObserver)); // Valid choice
        assertThat(messages).containsOnly(
                "New player joined the BLACK side",
                "Joining the game " + uuidGame1, //Black player
                "New player joined the OBSERVER side",
                "Joining the game " + uuidGame1 //Observer
        );

        //Not supposed to be able to choose the other color, but able to join as observer
        currentWebSocketService.clearMessages();


        UUID gameUuid2 = gameService.createNewGame("", WHITE, true, true, player1);
        GenericGameHandler game2 = gameService.getGameFromUuid(gameUuid2.toString());
        String uuidGame2 = game2.getUuid();

        assertEquals(FALSE_BOOLEAN_RESPONSE, gameService.joinGame(uuidGame2, WHITE, "8ddf1de9-d366-40c5-acdb-703e1438f543", player2)); //Unable to join, the WHITE is already taken
        assertEquals(FALSE_BOOLEAN_RESPONSE, gameService.joinGame(uuidGame2, BLACK, "8ddf1de9-d366-40c5-acdb-703e1438f543", player2)); // AI, cannot join
        assertEquals(TRUE_BOOLEAN_RESPONSE, gameService.joinGame(uuidGame2, OBSERVER, "8ddf1de9-d366-40c5-acdb-703e1438f543", playerObserver)); // Valid choice
        assertThat(messages).containsOnly(
                "You are not authorized to join this game !", //Private message
                "You are not authorized to join this game !", //Private message
                "New player joined the OBSERVER side",
                "Joining the game " + uuidGame2 //Observer
        );

        //Not supposed to be able join
        currentWebSocketService.clearMessages();


        UUID gameUuid3 = gameService.createNewGame("", WHITE, true, false, player1);
        GenericGameHandler game3 = gameService.getGameFromUuid(gameUuid3.toString());
        String uuidGame3 = game3.getUuid();

        assertEquals(FALSE_BOOLEAN_RESPONSE, gameService.joinGame(uuidGame3, WHITE, "8ddf1de9-d366-40c5-acdb-703e1438f543", player2)); //Unable to join, the WHITE is already taken
        assertEquals(FALSE_BOOLEAN_RESPONSE, gameService.joinGame(uuidGame3, BLACK, "8ddf1de9-d366-40c5-acdb-703e1438f543", player2)); // AI, cannot join
        assertEquals(FALSE_BOOLEAN_RESPONSE, gameService.joinGame(uuidGame3, OBSERVER, "8ddf1de9-d366-40c5-acdb-703e1438f543", playerObserver)); //Cannot join observers

        assertThat(messages).containsOnly("You are not authorized to join this game !"); //Private message (x3)
    }

    @Test
    public void getPieceLocationsTest() throws FenParserException {
        Player player1 = new Player(UUID.randomUUID().toString());
        Player player2 = new Player(UUID.randomUUID().toString());
        Player playerNotInGame = new Player(UUID.randomUUID().toString());

        UUID gameUuid = gameService.createNewGame("", WHITE, false, true, player1);
        GenericGameHandler game = gameService.getGameFromUuid(gameUuid.toString());
        String uuid = game.getUuid();
        game.setPlayerToSide(player2, BLACK);

        assertThat(gameService.getPieceLocations(uuid, player1)).isNotEmpty();
        assertThat(gameService.getPieceLocations(uuid, player2)).isNotEmpty();


        assertThat(gameService.getPieceLocations(uuid, playerNotInGame)).isEmpty();
    }
}
