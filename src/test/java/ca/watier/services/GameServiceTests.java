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
import ca.watier.enums.Pieces;
import ca.watier.game.GenericGameHandler;
import ca.watier.impl.WebSocketServiceTestImpl;
import ca.watier.responses.BooleanResponse;
import ca.watier.responses.GameScoreResponse;
import ca.watier.sessions.Player;
import ca.watier.utils.GameUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static ca.watier.enums.CasePosition.*;
import static ca.watier.enums.GameType.CLASSIC;
import static ca.watier.enums.GameType.SPECIAL;
import static ca.watier.enums.Pieces.B_KING;
import static ca.watier.enums.Pieces.W_QUEEN;
import static ca.watier.enums.Side.*;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class GameServiceTests {

    private static final ConstraintService CONSTRAINT_SERVICE = new ConstraintService();
    private static final BooleanResponse FALSE_BOOLEAN_RESPONSE = new BooleanResponse(false);
    private static final BooleanResponse TRUE_BOOLEAN_RESPONSE = new BooleanResponse(true);
    private static final GameScoreResponse EMPTY_GAME_SCORE_RESPONSE = new GameScoreResponse((short) 0, (short) 0);
    private WebSocketService currentWebSocketService;
    private GameService gameService;

    @Before
    public void setup() throws Exception {
        currentWebSocketService = new WebSocketServiceTestImpl();
        gameService = new GameService(CONSTRAINT_SERVICE, currentWebSocketService);
    }

    @Test
    public void setSideOfPlayerTest() {
        Player player1 = new Player();
        Player player2 = new Player();

        GenericGameHandler gameUuid = gameService.createNewGame(player1, "", WHITE, false, false);
        String uuid = gameUuid.getUuid();

        gameService.setSideOfPlayer(player1, BLACK, uuid);

        Map<UUID, GenericGameHandler> mapOfGames = gameService.getAllGames();
        Set<UUID> allIdGamesFromGameService = mapOfGames.keySet();

        //Get the current game
        Set<UUID> gameListIdFromPlayer = new HashSet<>(player1.getCreatedGameList());
        List<GenericGameHandler> allGames = new ArrayList<>(mapOfGames.values());
        GenericGameHandler normalGameHandler = allGames.get(0);

        //Check if the player1 is set to black
        assertEquals(player1, normalGameHandler.getPlayerBlack());
        assertEquals(null, normalGameHandler.getPlayerWhite());
        Assert.assertTrue(normalGameHandler.getObserverList().isEmpty());

        gameService.setSideOfPlayer(player1, WHITE, uuid);

        //Check if the player1 is set to white (was black before)
        assertEquals(null, normalGameHandler.getPlayerBlack());
        assertEquals(player1, normalGameHandler.getPlayerWhite());
        Assert.assertTrue(normalGameHandler.getObserverList().isEmpty());

        //compare the service vs player1 data
        assertEquals(allIdGamesFromGameService, gameListIdFromPlayer);
        assertEquals(1, allIdGamesFromGameService.size());
        assertEquals(1, gameListIdFromPlayer.size());

        //Try to associate the white to the player 2 (already set to player 1)
        gameService.setSideOfPlayer(player2, WHITE, uuid);

        //Check if the player1 is still associated to black, player2 not set yet
        assertEquals(null, normalGameHandler.getPlayerBlack());
        assertEquals(player1, normalGameHandler.getPlayerWhite());
        Assert.assertTrue(normalGameHandler.getObserverList().isEmpty());

        //Try to associate the black to the player 2 (not set yet)
        gameService.setSideOfPlayer(player2, BLACK, uuid);

        assertEquals(player2, normalGameHandler.getPlayerBlack());
        assertEquals(player1, normalGameHandler.getPlayerWhite());
        Assert.assertTrue(normalGameHandler.getObserverList().isEmpty());

        //Change both of the player to observers
        gameService.setSideOfPlayer(player1, OBSERVER, uuid);
        gameService.setSideOfPlayer(player2, OBSERVER, uuid);

        assertEquals(null, normalGameHandler.getPlayerBlack());
        assertEquals(null, normalGameHandler.getPlayerWhite());
        Assert.assertTrue(normalGameHandler.getObserverList().containsAll(asList(player1, player2)));
    }

    @Test
    public void createNewGameTest() {
        Player player1 = new Player();

        GenericGameHandler gameFromUuid = gameService.createNewGame(player1, "", WHITE, false, false);
        GenericGameHandler gameFromUuidCustom = gameService.createNewGame(player1, "D5:B_KING;B5:W_QUEEN;D3:W_QUEEN;D7:W_QUEEN;F5:W_QUEEN", WHITE, false, false);

        UUID specialGame = UUID.fromString(gameFromUuidCustom.getUuid());
        UUID normalGame = UUID.fromString(gameFromUuid.getUuid());

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
    public void movePieceTest() {
        WebSocketServiceTestImpl currentWebSocketService = (WebSocketServiceTestImpl) this.currentWebSocketService;
        Player player1 = new Player();
        Player player2 = new Player();
        Player playerNotInGame = new Player();

        GenericGameHandler gameFromUuid = gameService.createNewGame(player1, "", WHITE, false, false);
        String uuid = gameFromUuid.getUuid();
        gameFromUuid.setPlayerToSide(player2, BLACK);

        //Invalid moves, by the wrong player
        assertEquals(FALSE_BOOLEAN_RESPONSE, gameService.movePiece(H2, H4, uuid, playerNotInGame));
        assertEquals(FALSE_BOOLEAN_RESPONSE, gameService.movePiece(H2, H4, uuid, player2));

        //Valid move
        assertEquals(TRUE_BOOLEAN_RESPONSE, gameService.movePiece(H2, H4, uuid, player1));

        //Unable to move twice
        assertEquals(FALSE_BOOLEAN_RESPONSE, gameService.movePiece(H4, H5, uuid, player1));

        //Valid move for the black player
        assertEquals(TRUE_BOOLEAN_RESPONSE, gameService.movePiece(H7, H5, uuid, player2));

        List<Object> messages = currentWebSocketService.getMessages();

        /*
            Asserting the messages
        */

        assertThat(messages).containsOnly(
                "WHITE player moved H2 to H4",
                "It's your turn !", //To the black player
                EMPTY_GAME_SCORE_RESPONSE,
                "BLACK player moved H7 to H5",
                "It's your turn !",  //To the white player
                EMPTY_GAME_SCORE_RESPONSE);

        currentWebSocketService.clearMessages();

        gameFromUuid.setGameDone(true);
        assertEquals(FALSE_BOOLEAN_RESPONSE, gameService.movePiece(G2, G4, uuid, player1));
        assertThat(messages).containsOnly("The game is ended !");

    }

    @Test
    public void getAllAvailableMovesTest() {
        Player player1 = new Player();
        Player player2 = new Player();
        Player playerNotInGame = new Player();

        GenericGameHandler gameFromUuid = gameService.createNewGame(player1, "", WHITE, false, false);
        gameFromUuid.setPlayerToSide(player2, BLACK);
        String uuid = gameFromUuid.getUuid();

        //Valid for the WHITE player
        assertThat(gameService.getAllAvailableMoves(H2, uuid, player1)).isNotEmpty().containsOnly("H4", "H3");
        assertThat(gameService.getAllAvailableMoves(E1, uuid, player1)).isEmpty(); //King

        //Cannot see the piece moves if your not in the game, or not the color of the piece
        assertThat(gameService.getAllAvailableMoves(H2, uuid, playerNotInGame)).isEmpty();
        assertThat(gameService.getAllAvailableMoves(H2, uuid, player2)).isEmpty();
    }


    @Test
    public void joinGameTest() {
        WebSocketServiceTestImpl currentWebSocketService = (WebSocketServiceTestImpl) this.currentWebSocketService;

        Player player1 = new Player();
        Player player2 = new Player();
        Player playerObserver = new Player();

        List<Object> messages = currentWebSocketService.getMessages();

        //Able to join any side, except WHITE
        GenericGameHandler game1 = gameService.createNewGame(player1, "", WHITE, false, true);
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
        GenericGameHandler game2 = gameService.createNewGame(player1, "", WHITE, true, true);
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
        GenericGameHandler game3 = gameService.createNewGame(player1, "", WHITE, true, false);
        String uuidGame3 = game3.getUuid();

        assertEquals(FALSE_BOOLEAN_RESPONSE, gameService.joinGame(uuidGame3, WHITE, "8ddf1de9-d366-40c5-acdb-703e1438f543", player2)); //Unable to join, the WHITE is already taken
        assertEquals(FALSE_BOOLEAN_RESPONSE, gameService.joinGame(uuidGame3, BLACK, "8ddf1de9-d366-40c5-acdb-703e1438f543", player2)); // AI, cannot join
        assertEquals(FALSE_BOOLEAN_RESPONSE, gameService.joinGame(uuidGame3, OBSERVER, "8ddf1de9-d366-40c5-acdb-703e1438f543", playerObserver)); //Cannot join observers

        assertThat(messages).containsOnly("You are not authorized to join this game !"); //Private message (x3)
    }

    @Test
    public void getPieceLocationsTest() {
        Player player1 = new Player();
        Player player2 = new Player();
        Player playerNotInGame = new Player();

        GenericGameHandler game = gameService.createNewGame(player1, "", WHITE, false, true);
        String uuid = game.getUuid();
        game.setPlayerToSide(player2, BLACK);

        assertThat(gameService.getPieceLocations(uuid, player1)).isNotEmpty();
        assertThat(gameService.getPieceLocations(uuid, player2)).isNotEmpty();


        assertThat(gameService.getPieceLocations(uuid, playerNotInGame)).isNull();
    }
}
