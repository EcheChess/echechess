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

package ca.watier;

import ca.watier.enums.CasePosition;
import ca.watier.enums.GameType;
import ca.watier.enums.Pieces;
import ca.watier.enums.Side;
import ca.watier.game.GenericGameHandler;
import ca.watier.services.GameService;
import ca.watier.sessions.Player;
import ca.watier.utils.GameUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static ca.watier.enums.CasePosition.*;
import static ca.watier.enums.Pieces.B_KING;
import static ca.watier.enums.Pieces.W_QUEEN;
import static ca.watier.utils.Constants.PLAYER;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EchechessApplicationTests {

    private static final String BLACK = Side.BLACK.name();
    private static final String WHITE = Side.WHITE.name();
    private static final String OBSERVER = Side.OBSERVER.name();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private GameService gameService;

    @Before
    public void setup() throws Exception {
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void setSideOfPlayerTest() throws Exception {
        MockHttpSession sessionPlayer1 = new MockHttpSession();
        MockHttpSession sessionPlayer2 = new MockHttpSession();
        Player player1 = new Player();
        Player player2 = new Player();
        sessionPlayer1.setAttribute(PLAYER, player1);
        sessionPlayer2.setAttribute(PLAYER, player2);

        UUID gameUuid = createNewGame(WHITE, false, false, null, sessionPlayer1);

        String uuid = gameUuid.toString();
        changePlayerSide(BLACK, uuid, sessionPlayer1);

        Map<UUID, GenericGameHandler> mapOfGames = gameService.getAllGames();
        Set<UUID> allIdGamesFromGameService = mapOfGames.keySet();

        //Get the current game
        Set<UUID> gameListIdFromPlayer = new HashSet<>(player1.getCreatedGameList());
        List<GenericGameHandler> allGames = new ArrayList<>(mapOfGames.values());
        GenericGameHandler normalGameHandler = allGames.get(0);

        //Check if the player1 is set to black
        Assert.assertEquals(player1, normalGameHandler.getPlayerBlack());
        Assert.assertEquals(null, normalGameHandler.getPlayerWhite());
        Assert.assertTrue(normalGameHandler.getObserverList().isEmpty());

        changePlayerSide(WHITE, uuid, sessionPlayer1);

        //Check if the player1 is set to white (was black before)
        Assert.assertEquals(null, normalGameHandler.getPlayerBlack());
        Assert.assertEquals(player1, normalGameHandler.getPlayerWhite());
        Assert.assertTrue(normalGameHandler.getObserverList().isEmpty());

        //compare the service vs player1 data
        Assert.assertEquals(allIdGamesFromGameService, gameListIdFromPlayer);
        Assert.assertEquals(1, allIdGamesFromGameService.size());
        Assert.assertEquals(1, gameListIdFromPlayer.size());

        //Try to associate the white to the player 2 (already set to player 1)
        changePlayerSide(WHITE, uuid, sessionPlayer2);

        //Check if the player1 is still associated to black, player2 not set yet
        Assert.assertEquals(null, normalGameHandler.getPlayerBlack());
        Assert.assertEquals(player1, normalGameHandler.getPlayerWhite());
        Assert.assertTrue(normalGameHandler.getObserverList().isEmpty());

        //Try to associate the black to the player 2 (not set yet)
        changePlayerSide(BLACK, uuid, sessionPlayer2);

        Assert.assertEquals(player2, normalGameHandler.getPlayerBlack());
        Assert.assertEquals(player1, normalGameHandler.getPlayerWhite());
        Assert.assertTrue(normalGameHandler.getObserverList().isEmpty());

        //Change both of the player to observers
        changePlayerSide(OBSERVER, uuid, sessionPlayer1);
        changePlayerSide(OBSERVER, uuid, sessionPlayer2);

        Assert.assertEquals(null, normalGameHandler.getPlayerBlack());
        Assert.assertEquals(null, normalGameHandler.getPlayerWhite());
        Assert.assertTrue(normalGameHandler.getObserverList().containsAll(asList(player1, player2)));
    }

    private UUID createNewGame(String side, boolean againstComputer, boolean observers, String specialGamePieces, MockHttpSession session) {
        UUID value = null;

        try {
            MvcResult resultActions = mockMvc.perform(post("https://localhost:8443/game/create")
                    .session(session)
                    .param("side", side)
                    .param("againstComputer", Boolean.toString(againstComputer))
                    .param("observers", Boolean.toString(observers))
                    .param("specialGamePieces", specialGamePieces)
                    .contentType(MULTIPART_FORM_DATA))
                    .andExpect(status().isOk())
                    .andReturn();


            JsonNode rootNode = OBJECT_MAPPER.readTree(resultActions.getResponse().getContentAsString());
            JsonNode idNode = rootNode.path("uuid");
            value = UUID.fromString(idNode.asText());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

    private void changePlayerSide(String side, String uuid, MockHttpSession session) {
        try {
            mockMvc.perform(post("https://localhost:8443/game/side")
                    .session(session)
                    .param("side", side)
                    .param("uuid", uuid)
                    .contentType(MULTIPART_FORM_DATA))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void createNewGameTest() throws InterruptedException, ExecutionException, TimeoutException {
        MockHttpSession sessionPlayer1 = new MockHttpSession();
        Player player1 = new Player();
        sessionPlayer1.setAttribute(PLAYER, player1);

        UUID specialGame = createNewGame(WHITE, false, false, "D5:B_KING;B5:W_QUEEN;D3:W_QUEEN;D7:W_QUEEN;F5:W_QUEEN", sessionPlayer1);
        UUID normalGame = createNewGame(WHITE, false, false, "", sessionPlayer1);
        GenericGameHandler gameFromUuidCustom = gameService.getGameFromUuid(specialGame.toString());
        GenericGameHandler gameFromUuid = gameService.getGameFromUuid(normalGame.toString());

        //Check if the game is associated with the player
        assertThat(specialGame).isNotNull();
        assertThat(normalGame).isNotNull();
        assertThat(player1.getCreatedGameList()).containsOnly(specialGame, normalGame);

        //Check the type of the game
        Assert.assertNotNull(gameFromUuidCustom);
        Assert.assertEquals(GameType.SPECIAL, gameFromUuidCustom.getGameType());

        Assert.assertNotNull(gameFromUuidCustom);
        Assert.assertEquals(GameType.CLASSIC, gameFromUuid.getGameType());

        //Check if the pieces are set
        Map<CasePosition, Pieces> piecesLocationCustom = gameFromUuidCustom.getPiecesLocation();
        assertThat(piecesLocationCustom).isNotNull();
        assertThat(piecesLocationCustom).hasSize(5);
        Assert.assertEquals(W_QUEEN, piecesLocationCustom.get(B5));
        Assert.assertEquals(W_QUEEN, piecesLocationCustom.get(D3));
        Assert.assertEquals(W_QUEEN, piecesLocationCustom.get(F5));
        Assert.assertEquals(W_QUEEN, piecesLocationCustom.get(D7));
        Assert.assertEquals(B_KING, piecesLocationCustom.get(D5));

        Map<CasePosition, Pieces> piecesLocation = gameFromUuid.getPiecesLocation();
        assertThat(piecesLocation).isNotNull();
        assertThat(piecesLocation).hasSize(32);
        Assert.assertEquals(GameUtils.getDefaultGame(), piecesLocation);
    }


    @Test
    public void movePieceTest() throws InterruptedException, ExecutionException, TimeoutException {
        MockHttpSession sessionPlayer1 = new MockHttpSession();
        Player player1 = new Player();
        sessionPlayer1.setAttribute(PLAYER, player1);

        UUID normalGame = createNewGame(WHITE, false, false, "", sessionPlayer1);
        GenericGameHandler gameFromUuid = gameService.getGameFromUuid(normalGame.toString());
        fail("FIX_ME"); //FIXME
    }
}
