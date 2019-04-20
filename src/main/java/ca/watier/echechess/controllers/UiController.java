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

import ca.watier.echechess.common.responses.StringResponse;
import ca.watier.echechess.models.UserDetailsImpl;
import ca.watier.echechess.services.UiSessionService;
import ca.watier.echechess.utils.AuthenticationUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by yannick on 4/22/2017.
 */

@RestController
@RequestMapping("api/v1/ui")
@PreAuthorize("#oauth2.hasScope('read')")
public class UiController {
    private final UiSessionService uiSessionService;

    @Autowired
    public UiController(UiSessionService uiSessionService) {
        this.uiSessionService = uiSessionService;
    }

    @ApiOperation("Create and bind a ui session to the player")
    @GetMapping(path = "/id", produces = MediaType.APPLICATION_JSON_VALUE)
    public StringResponse bindNewUiUuidToUser() {
        UserDetailsImpl userDetail = AuthenticationUtils.getUserDetail();
        return new StringResponse(uiSessionService.bindNewSessionToPlayer(userDetail));
    }
}
