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

package ca.watier.echechess.controllers;

import ca.watier.echechess.common.pojos.Ping;
import ca.watier.echechess.common.responses.StringResponse;
import ca.watier.echechess.common.utils.SessionUtils;
import ca.watier.echechess.services.UiSessionService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

import static ca.watier.echechess.controllers.GameController.UI_UUID_PLAYER;

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


    @ApiOperation("Create and bind a ui session to the player")
    @RequestMapping(path = "/id/1", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public StringResponse createNewGame(HttpSession session) {
        return new StringResponse(uiSessionService.createNewSession(SessionUtils.getPlayer(session)));
    }

    @ApiOperation("Used to update the user ping timer")
    @MessageMapping("/api/ui/ping")
    @SendTo("/topic/ping")
    public void ping(@ApiParam(value = UI_UUID_PLAYER, required = true) Ping uuid) {
        uiSessionService.refresh(uuid.getUuid());
    }
}
