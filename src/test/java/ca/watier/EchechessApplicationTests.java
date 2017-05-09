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

import ca.watier.enums.Side;
import ca.watier.game.StandardGameHandler;
import ca.watier.services.GameService;
import ca.watier.sessions.Player;
import ca.watier.utils.Constants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EchechessApplicationTests {

    private static final String BLACK = Side.BLACK.name();
    private static final String WHITE = Side.WHITE.name();
    private static final String OBSERVER = Side.OBSERVER.name();
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
    public void setSideOfPlayer() throws Exception {
        MockHttpSession sessionPlayer1 = new MockHttpSession();
        MockHttpSession sessionPlayer2 = new MockHttpSession();
        Player player1 = new Player();
        Player player2 = new Player();
        sessionPlayer1.setAttribute(Constants.PLAYER, player1);
        sessionPlayer2.setAttribute(Constants.PLAYER, player2);

        changePlayerSide(BLACK, UUID.randomUUID().toString(), sessionPlayer1);

        Map<UUID, StandardGameHandler> mapOfGames = gameService.getAllGames();
        Set<UUID> allIdGamesFromGameService = mapOfGames.keySet();

        //Get the current game
        Set<UUID> gameListIdFromPlayer = new HashSet<>(player1.getCreatedGameList());
        List<StandardGameHandler> allGames = new ArrayList<>(mapOfGames.values());
        StandardGameHandler normalGameHandler = allGames.get(0);

        //Check if the player1 is set to black
        Assert.assertEquals(player1, normalGameHandler.getPlayerBlack());
        Assert.assertEquals(null, normalGameHandler.getPlayerWhite());
        Assert.assertTrue(normalGameHandler.getObserverList().isEmpty());

        UUID gameUuid = player1.getLastGameCreated();
        changePlayerSide(WHITE, gameUuid.toString(), sessionPlayer1);

        //Check if the player1 is set to white (was black before)
        Assert.assertEquals(null, normalGameHandler.getPlayerBlack());
        Assert.assertEquals(player1, normalGameHandler.getPlayerWhite());
        Assert.assertTrue(normalGameHandler.getObserverList().isEmpty());

        //compare the service vs player1 data
        Assert.assertEquals(allIdGamesFromGameService, gameListIdFromPlayer);
        Assert.assertEquals(1, allIdGamesFromGameService.size());
        Assert.assertEquals(1, gameListIdFromPlayer.size());

        //Try to associate the white to the player 2 (already set to player 1)
        changePlayerSide(WHITE, gameUuid.toString(), sessionPlayer2);

        //Check if the player1 is still associated to black, player2 not set yet
        Assert.assertEquals(null, normalGameHandler.getPlayerBlack());
        Assert.assertEquals(player1, normalGameHandler.getPlayerWhite());
        Assert.assertTrue(normalGameHandler.getObserverList().isEmpty());

        //Try to associate the black to the player 2 (not set yet)
        changePlayerSide(BLACK, gameUuid.toString(), sessionPlayer2);

        Assert.assertEquals(player2, normalGameHandler.getPlayerBlack());
        Assert.assertEquals(player1, normalGameHandler.getPlayerWhite());
        Assert.assertTrue(normalGameHandler.getObserverList().isEmpty());

        //Change both of the player to observers
        changePlayerSide(OBSERVER, gameUuid.toString(), sessionPlayer1);
        changePlayerSide(OBSERVER, gameUuid.toString(), sessionPlayer2);

        Assert.assertEquals(null, normalGameHandler.getPlayerBlack());
        Assert.assertEquals(null, normalGameHandler.getPlayerWhite());
        Assert.assertTrue(normalGameHandler.getObserverList().containsAll(Arrays.asList(player1, player2)));
    }

    private void changePlayerSide(String side, String uuid, MockHttpSession session) {
        try {
            mockMvc.perform(post("/game/side")
                    .session(session)
                    .param("side", side)
                    .param("uuid", uuid)
                    .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
