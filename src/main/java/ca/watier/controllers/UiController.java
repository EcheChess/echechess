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

package ca.watier.controllers;

import ca.watier.echesscommon.pojos.Ping;
import ca.watier.echesscommon.utils.SessionUtils;
import ca.watier.echesscommon.responses.StringResponse;
import ca.watier.services.UiSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * Created by yannick on 4/22/2017.
 */

@RestController
@RequestMapping("/api/ui")
public class UiController {
    private final UiSessionService uiSessionService;

    @Autowired
    public UiController(UiSessionService uiSessionService) {
        this.uiSessionService = uiSessionService;
    }

    /**
     * Create and bind a ui session to the player
     *
     * @param session
     * @return
     */
    @RequestMapping(path = "/id/1", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public StringResponse createNewGame(HttpSession session) {
        return new StringResponse(uiSessionService.createNewSession(SessionUtils.getPlayer(session)));
    }

    @MessageMapping("/api/ui/ping")
    @SendTo("/topic/ping")
    public void ping(Ping uuid) {
        uiSessionService.refresh(uuid.getUuid());
    }
}
