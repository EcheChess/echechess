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

import ca.watier.defassert.Assert;
import ca.watier.enums.Side;
import ca.watier.game.GameHandler;
import ca.watier.services.GameService;
import ca.watier.services.UserService;
import ca.watier.sessions.Player;
import ca.watier.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * Created by yannick on 4/17/2017.
 */

@RestController
@RequestMapping("/usr")
public class UserController {

    private final UserService userService;
    private final GameService gameService;

    @Autowired
    public UserController(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    @RequestMapping(path = "/side", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String setSideOfPlayer(Side side, String uuid, HttpSession session) {
        Player player = (Player) session.getAttribute(Constants.PLAYER);

        Assert.assertNotNull(player);

        GameHandler game = gameService.getGameFromUuid(uuid, player);
        game.setPlayerToSide(player, side);

        return userService.getName();
    }
}
