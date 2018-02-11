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

package ca.watier.integration;

import ca.watier.EchechessApplication;
import ca.watier.game.GameService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {EchechessApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameIntegrationTest {

    private String url;
    private RestTemplate restTemplate;
    private String uiUuid;
    private MultiValueMap<String, String> preInitializedMap = new LinkedMultiValueMap<>();

    @LocalServerPort
    private int port;

    @Autowired
    private GameService gameService;

    @Before
    public void setUp() {
        preInitializedMap.clear();
        restTemplate = new RestTemplate();
        url = "http://127.0.0.1:" + port + '/';

        ResponseEntity<Object> forEntity = restTemplate.getForEntity(url + "api/ui/id/1", Object.class);
        Object body = forEntity.getBody();

        if (body instanceof LinkedHashMap) {
            uiUuid = (String) ((LinkedHashMap) body).get("response");
            preInitializedMap.add("uiUuid", uiUuid);
        }
    }


    @Test
    public void createNewGame() {
        String uuidKeyMapName = "response";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        preInitializedMap.add("side", "BLACK");
        preInitializedMap.add("againstComputer", "false");
        preInitializedMap.add("observers", "false");
        preInitializedMap.add("specialGamePieces", null);

        ResponseEntity<Object> response = restTemplate.exchange(url + "api/game/create/1", HttpMethod.POST, new HttpEntity<>(preInitializedMap, headers), Object.class);

        Object bodyResponse = response.getBody();
        assertThat(bodyResponse).isNotNull().isInstanceOf(LinkedHashMap.class);
        Map<String, Object> mappedValues = (Map<String, Object>) bodyResponse;

        assertThat(mappedValues).containsOnlyKeys(uuidKeyMapName);
        assertThat(mappedValues).doesNotContainValue(null);

        UUID uuid = UUID.fromString((String) mappedValues.get(uuidKeyMapName));

        assertThat(gameService.getGameFromUuid(uuid)).isNotNull(); //Make sure that the game is created and present in the service
    }
}
