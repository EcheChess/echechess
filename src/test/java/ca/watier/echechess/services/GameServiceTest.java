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

import ca.watier.echechess.common.enums.*;
import ca.watier.echechess.common.responses.BooleanResponse;
import ca.watier.echechess.common.responses.GameScoreResponse;
import ca.watier.echechess.common.services.WebSocketService;
import ca.watier.echechess.common.services.WebSocketServiceTestImpl;
import ca.watier.echechess.common.sessions.Player;
import ca.watier.echechess.common.utils.Constants;
import ca.watier.echechess.communication.redis.model.GenericGameHandlerWrapper;
import ca.watier.echechess.delegates.GameMessageDelegate;
import ca.watier.echechess.engine.delegates.PieceMoveConstraintDelegate;
import ca.watier.echechess.engine.engines.GenericGameHandler;
import ca.watier.echechess.engine.exceptions.FenParserException;
import ca.watier.echechess.engine.interfaces.GameEventEvaluatorHandler;
import ca.watier.echechess.engine.interfaces.GameHandler;
import ca.watier.echechess.engine.interfaces.PlayerHandler;
import ca.watier.echechess.engine.utils.GameUtils;
import ca.watier.echechess.models.PawnPromotionPiecesModel;
import ca.watier.echechess.models.PieceLocationModel;
import ca.watier.repository.KeyValueRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static ca.watier.echechess.common.enums.CasePosition.*;
import static ca.watier.echechess.common.enums.ChessEventMessage.*;
import static ca.watier.echechess.common.enums.Pieces.B_KING;
import static ca.watier.echechess.common.enums.Pieces.W_QUEEN;
import static ca.watier.echechess.common.enums.Side.*;
import static ca.watier.echechess.common.utils.Constants.*;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameServiceTest {
    private static final BooleanResponse FALSE_BOOLEAN_RESPONSE = new BooleanResponse(false);
    private static final BooleanResponse TRUE_BOOLEAN_RESPONSE = new BooleanResponse(true);
    private static final PieceMoveConstraintDelegate DEFAULT_GAME_MOVE_DELEGATE = new PieceMoveConstraintDelegate();
    private static final GameScoreResponse EMPTY_GAME_SCORE_RESPONSE = new GameScoreResponse((short) 0, (short) 0);


    private GenericGameHandler givenGameHandler;
    private GameEventEvaluatorHandler gameEventEvaluatorHandler;
    private PlayerHandler playerHandler;
    private GameMessageDelegate gameMessageDelegate;
    private KeyValueRepository redisGameRepository;
    private WebSocketService currentWebSocketService;
    private GameService gameService;
    private Player firstPlayer;

    @BeforeEach
    public void setup() {
        playerHandler = mock(PlayerHandler.class);
        gameEventEvaluatorHandler = mock(GameEventEvaluatorHandler.class);
        givenGameHandler = spy(new GenericGameHandler(DEFAULT_GAME_MOVE_DELEGATE, playerHandler, gameEventEvaluatorHandler));
        gameMessageDelegate = mock(GameMessageDelegate.class);
        firstPlayer = spy(new Player(UUID.randomUUID().toString()));
        currentWebSocketService = spy(new WebSocketServiceTestImpl());
        redisGameRepository = spy(new KeyValueRepository());
        gameService = spy(new GameServiceImpl(
                DEFAULT_GAME_MOVE_DELEGATE,
                currentWebSocketService,
                redisGameRepository,
                gameMessageDelegate));
    }

    @Test
    public void upgradePiece() throws FenParserException {
        WebSocketServiceTestImpl currentWebSocketService = (WebSocketServiceTestImpl) this.currentWebSocketService;
        UUID gameUuid = gameService.createNewGame("K7/6P1/8/8/8/8/6p1/k7 w", WHITE, false, false, firstPlayer);
        GenericGameHandler gameFromUuid = gameService.getGameFromUuid(gameUuid.toString());

        assertEquals(MoveType.PAWN_PROMOTION, gameFromUuid.movePiece(G7, G8, WHITE));

        String uuid = gameFromUuid.getUuid();
        assertTrue(gameFromUuid.isGamePaused());
        assertTrue(gameService.upgradePiece(G8, uuid, PawnPromotionPiecesModel.QUEEN, firstPlayer));
        assertFalse(gameFromUuid.isGamePaused());

        assertThat(currentWebSocketService.getMessages()).containsOnly(
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
        PlayerHandler playerHandler = normalGameHandler.getPlayerHandler();

        //Check if the player1 is set to black
        assertEquals(player1, normalGameHandler.getPlayerBlack());
        assertNull(playerHandler.getPlayerWhite());
        assertTrue(playerHandler.getObserverList().isEmpty());

        gameService.setSideOfPlayer(WHITE, uuid, player1);

        //Check if the player1 is set to white (was black before)
        assertNull(normalGameHandler.getPlayerBlack());
        assertEquals(player1, normalGameHandler.getPlayerWhite());
        assertTrue(playerHandler.getObserverList().isEmpty());

        //compare the service vs player1 data
        assertEquals(allIdGamesFromGameService, gameListIdFromPlayer);
        assertEquals(1, allIdGamesFromGameService.size());
        assertEquals(1, gameListIdFromPlayer.size());

        //Try to associate the white to the player 2 (already set to player 1)
        gameService.setSideOfPlayer(WHITE, uuid, player2);

        //Check if the player1 is still associated to black, player2 not set yet
        assertNull(normalGameHandler.getPlayerBlack());
        assertEquals(player1, normalGameHandler.getPlayerWhite());
        assertTrue(playerHandler.getObserverList().isEmpty());

        //Try to associate the black to the player 2 (not set yet)
        gameService.setSideOfPlayer(BLACK, uuid, player2);

        assertEquals(player2, normalGameHandler.getPlayerBlack());
        assertEquals(player1, normalGameHandler.getPlayerWhite());
        assertTrue(playerHandler.getObserverList().isEmpty());

        //Change both of the player to observers
        gameService.setSideOfPlayer(OBSERVER, uuid, player1);
        gameService.setSideOfPlayer(OBSERVER, uuid, player2);

        assertNull(normalGameHandler.getPlayerBlack());
        assertNull(normalGameHandler.getPlayerWhite());
        assertTrue(playerHandler.getObserverList().containsAll(asList(player1, player2)));
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
        assertNotNull(gameFromUuidCustom);

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
    public void movePiece_player_not_in_game() {
        // given
        String givenUuid = "superUUID";
        Player givenPlayer = mock(Player.class);

        // when
        when(givenGameHandler.hasPlayer(givenPlayer)).thenReturn(false);
        doReturn(givenGameHandler).when(gameService).getGameFromUuid(any(String.class));

        gameService.movePiece(H7, H4, givenUuid, givenPlayer);

        // then
        verifyNoInteractions(gameMessageDelegate);
        verifyNoInteractions(currentWebSocketService);
    }


    @Test
    public void movePiece_game_paused() {
        // given
        String givenUuid = "superUUID";
        Player givenPlayer = mock(Player.class);

        // when
        when(givenGameHandler.hasPlayer(givenPlayer)).thenReturn(true);
        when(givenGameHandler.isGamePaused()).thenReturn(true);
        doReturn(givenGameHandler).when(gameService).getGameFromUuid(any(String.class));

        gameService.movePiece(H7, H4, givenUuid, givenPlayer);

        // then
        verifyNoInteractions(gameMessageDelegate);
        verifyNoInteractions(currentWebSocketService);
    }


    @Test
    public void movePiece_game_draw() {
        // given
        String givenUuid = "superUUID";
        Player givenPlayer = mock(Player.class);

        // when
        when(givenGameHandler.hasPlayer(givenPlayer)).thenReturn(true);
        when(givenGameHandler.isGamePaused()).thenReturn(false);
        when(givenGameHandler.isGameDraw()).thenReturn(true);
        doReturn(givenGameHandler).when(gameService).getGameFromUuid(any(String.class));

        gameService.movePiece(H7, H4, givenUuid, givenPlayer);

        // then
        verifyNoInteractions(gameMessageDelegate);
        verifyNoInteractions(currentWebSocketService);
    }


    @Test
    public void movePiece_game_ended() {
        // given
        String givenUuid = "superUUID";
        Player givenPlayer = mock(Player.class);
        Side givenPlayerSide = WHITE;

        // when
        when(givenGameHandler.getPlayerSide(givenPlayer)).thenReturn(givenPlayerSide);
        when(givenGameHandler.hasPlayer(givenPlayer)).thenReturn(true);
        when(givenGameHandler.isGamePaused()).thenReturn(false);
        when(givenGameHandler.isGameDraw()).thenReturn(false);
        when(givenGameHandler.isGameEnded()).thenReturn(true);
        doReturn(givenGameHandler).when(gameService).getGameFromUuid(any(String.class));

        gameService.movePiece(H7, H4, givenUuid, givenPlayer);

        // then
        verifyNoInteractions(gameMessageDelegate);
        verify(currentWebSocketService).fireSideEvent(givenUuid, givenPlayerSide, GAME_WON_EVENT_MOVE, GAME_ENDED);
    }


    @Test
    public void movePiece_game_stalemate() {
        // given
        String givenUuid = "superUUID";
        Player givenPlayer = mock(Player.class);
        Side givenPlayerSide = WHITE;

        // when
        when(givenGameHandler.getPlayerSide(givenPlayer)).thenReturn(givenPlayerSide);
        when(givenGameHandler.hasPlayer(givenPlayer)).thenReturn(true);
        when(givenGameHandler.isGamePaused()).thenReturn(false);
        when(givenGameHandler.isGameDraw()).thenReturn(false);
        when(givenGameHandler.isGameEnded()).thenReturn(false);
        when(givenGameHandler.isKing(KingStatus.STALEMATE, givenPlayerSide)).thenReturn(true);
        doReturn(givenGameHandler).when(gameService).getGameFromUuid(any(String.class));

        gameService.movePiece(H7, H4, givenUuid, givenPlayer);

        // then
        verifyNoInteractions(gameMessageDelegate);
        verify(currentWebSocketService).fireSideEvent(givenUuid, givenPlayerSide, GAME_WON_EVENT_MOVE, PLAYER_KING_STALEMATE);
    }


    @Test
    void getAllAvailableMoves_player_not_in_game() {
        // given
        String givenUuid = "superUUID";
        Player givenPlayer = mock(Player.class);
        CasePosition givenPosition = H7;

        // when
        when(givenGameHandler.hasPlayer(givenPlayer)).thenReturn(false);
        doReturn(givenGameHandler).when(gameService).getGameFromUuid(any(String.class));

        gameService.getAllAvailableMoves(givenPosition, givenUuid, givenPlayer);

        // then
        verifyNoInteractions(gameMessageDelegate);
    }


    @Test
    void getAllAvailableMoves_player_wrong_color_move() {
        // given
        String givenUuid = "superUUID";
        Player givenPlayer = mock(Player.class);
        CasePosition givenPosition = A7; // Black PAWN
        Side givenSide = WHITE;

        // when
        when(givenGameHandler.hasPlayer(givenPlayer)).thenReturn(true);
        when(givenGameHandler.getPlayerSide(givenPlayer)).thenReturn(givenSide);
        doReturn(givenGameHandler).when(gameService).getGameFromUuid(any(String.class));

        gameService.getAllAvailableMoves(givenPosition, givenUuid, givenPlayer);

        // then
        verifyNoInteractions(gameMessageDelegate);
    }


    @Test
    void getAllAvailableMoves_player_same_color_move() {
        // given
        String givenUuid = "superUUID";
        Player givenPlayer = mock(Player.class);
        CasePosition givenPosition = A7; // Black PAWN
        Side givenSide = BLACK;

        // when
        when(givenGameHandler.hasPlayer(givenPlayer)).thenReturn(true);
        when(givenGameHandler.getPlayerSide(givenPlayer)).thenReturn(givenSide);
        doReturn(givenGameHandler).when(gameService).getGameFromUuid(any(String.class));

        gameService.getAllAvailableMoves(givenPosition, givenUuid, givenPlayer);

        // then
        verify(gameMessageDelegate).handleAvailableMoveMessage(anyString());
    }


    @Test
    void joinGame_wrong_id() {
        // given
        String givenGameUuid = "superUUID";
        String givenUiUuid = "superUIID";
        Player givenPlayer = mock(Player.class);
        Side givenSide = BLACK;

        // when
        doReturn(null).when(gameService).getGameFromUuid(any(String.class));

        BooleanResponse booleanResponse = gameService.joinGame(givenGameUuid, givenSide, givenUiUuid, givenPlayer);

        // then
        assertThat(booleanResponse).isEqualTo(BooleanResponse.NO);
        verifyNoInteractions(gameMessageDelegate);
    }

    @Test
    void joinGame_not_allowed_single_player_no_observers() {
        // given
        String givenGameUuid = "superUUID";
        String givenUiUuid = "superUIID";
        Player givenPlayer = mock(Player.class);
        Side givenSide = BLACK;

        // when
        doReturn(givenGameHandler).when(gameService).getGameFromUuid(any(String.class));
        doReturn(false).when(givenGameHandler).isAllowObservers();
        doReturn(false).when(givenGameHandler).isAllowOtherToJoin();

        BooleanResponse booleanResponse = gameService.joinGame(givenGameUuid, givenSide, givenUiUuid, givenPlayer);

        // then
        assertThat(booleanResponse).isEqualTo(BooleanResponse.NO);
        verifyNoInteractions(gameMessageDelegate);
    }

    @Test
    void joinGame_allowed_except_observers_choose_observer() {
        // given
        String givenGameUuid = "superUUID";
        String givenUiUuid = "superUIID";
        Player givenPlayer = mock(Player.class);
        Side givenSide = OBSERVER;

        // when
        doReturn(givenGameHandler).when(gameService).getGameFromUuid(any(String.class));
        doReturn(false).when(givenGameHandler).isAllowObservers();
        doReturn(true).when(givenGameHandler).isAllowOtherToJoin();

        BooleanResponse booleanResponse = gameService.joinGame(givenGameUuid, givenSide, givenUiUuid, givenPlayer);

        // then
        assertThat(booleanResponse).isEqualTo(BooleanResponse.NO);
        verifyNoInteractions(gameMessageDelegate);
    }

    @Test
    void joinGame_allowed_except_observers_choose_black() {
        // given
        String givenGameUuid = "23770896-069d-43c3-9a83-336031b153fe";
        String givenUiUuid = "07693684-082b-4f3c-9ea7-a8133a78225a";
        Player givenPlayer = mock(Player.class);
        Side givenSide = BLACK;

        // when
        doReturn(givenGameHandler).when(gameService).getGameFromUuid(any(String.class));
        doReturn(false).when(givenGameHandler).isAllowObservers();
        doReturn(true).when(givenGameHandler).isAllowOtherToJoin();
        doReturn(true).when(playerHandler).setPlayerToSide(givenPlayer, givenSide);

        BooleanResponse booleanResponse = gameService.joinGame(givenGameUuid, givenSide, givenUiUuid, givenPlayer);

        // then
        assertThat(booleanResponse).isEqualTo(BooleanResponse.YES);
        verifyNoInteractions(gameMessageDelegate);

        verify(givenPlayer).addJoinedGame(UUID.fromString(givenGameUuid));
        verify(currentWebSocketService).fireGameEvent(givenGameUuid, PLAYER_JOINED, String.format(NEW_PLAYER_JOINED_SIDE, givenSide));
        verify(currentWebSocketService).fireUiEvent(givenUiUuid, PLAYER_JOINED, String.format(JOINING_GAME, givenGameUuid));
        verify(redisGameRepository).add(any(GenericGameHandlerWrapper.class));
    }

    @Test
    void joinGame_allowed_except_observers_unable_to_join() {
        // given
        String givenGameUuid = "23770896-069d-43c3-9a83-336031b153fe";
        String givenUiUuid = "07693684-082b-4f3c-9ea7-a8133a78225a";
        Player givenPlayer = mock(Player.class);
        Side givenSide = BLACK;

        // when
        doReturn(givenGameHandler).when(gameService).getGameFromUuid(any(String.class));
        doReturn(false).when(givenGameHandler).isAllowObservers();
        doReturn(true).when(givenGameHandler).isAllowOtherToJoin();
        doReturn(false).when(playerHandler).setPlayerToSide(givenPlayer, givenSide);

        BooleanResponse booleanResponse = gameService.joinGame(givenGameUuid, givenSide, givenUiUuid, givenPlayer);

        // then
        assertThat(booleanResponse).isEqualTo(BooleanResponse.NO);
        verifyNoInteractions(gameMessageDelegate);
        verifyNoInteractions(currentWebSocketService);
        verifyNoInteractions(redisGameRepository);
    }

    @Test
    public void getPieceLocations_start_pieces_player_not_in_game()  {
        // given
        String givenGameUuid = "23770896-069d-43c3-9a83-336031b153fe";
        Player givenPlayer = mock(Player.class);

        // when
        when(givenGameHandler.hasPlayer(givenPlayer)).thenReturn(false);
        doReturn(givenGameHandler).when(gameService).getGameFromUuid(any(String.class));

        List<PieceLocationModel> pieceLocations = gameService.getPieceLocations(givenGameUuid, givenPlayer);

        // then
        assertThat(pieceLocations).isEmpty();
    }
}
