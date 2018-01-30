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

package ca.watier.integration;

import ca.watier.EchechessApplication;
import ca.watier.GameTest;
import ca.watier.PgnTest;
import ca.watier.game.GenericGameHandler;
import ca.watier.services.GameService;
import ca.watier.utils.PgnParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {EchechessApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameIntegrationLoadTest extends GameTest {

    private RestTemplate restTemplate;
    private MultiValueMap<String, String> preInitializedMap = new LinkedMultiValueMap<>();
    private List<GenericGameHandler> parsedGames;

    private String url;

    @LocalServerPort
    private int port;

    @Autowired
    private GameService gameService;

    @Before
    public void setUp() {
        preInitializedMap.clear();
        restTemplate = new RestTemplate();
        url = "http://127.0.0.1:" + port + '/';

        System.out.println("[*** Loading the games ***]");
        PgnParser pgnParser = new PgnParser(CONSTRAINT_SERVICE, WEB_SOCKET_SERVICE);
        parsedGames = pgnParser.parse(PgnTest.getGamesAsFile());
        System.out.println("[*** The games are loaded ***]");
    }

    @Test
    public void createNewLoadTest1() {
        System.out.println();
    }
}
