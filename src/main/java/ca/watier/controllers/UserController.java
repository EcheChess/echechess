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

import ca.watier.enums.Side;
import ca.watier.game.GameHandler;
import ca.watier.services.GameService;
import ca.watier.sessions.Player;
import ca.watier.utils.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final GameService gameService;

    @Autowired
    public UserController(GameService gameService) {
        this.gameService = gameService;
    }

    @RequestMapping(path = "/side", method = RequestMethod.POST)
    public boolean setSideOfPlayer(Side side, String uuid, HttpSession session) {
        Player player = SessionUtils.getPlayer(session);

        GameHandler game = gameService.getGameFromUuid(uuid);

        if (game == null) {
            game = gameService.createNewGame(player);
        }

        return game.setPlayerToSide(player, side);
    }
}
