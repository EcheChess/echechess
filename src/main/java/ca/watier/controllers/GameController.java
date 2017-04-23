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

import ca.watier.enums.CasePosition;
import ca.watier.enums.Side;
import ca.watier.game.GameHandler;
import ca.watier.responses.BooleanResponse;
import ca.watier.services.GameService;
import ca.watier.services.UserService;
import ca.watier.sessions.Player;
import ca.watier.utils.PlayerUtils;
import ca.watier.utils.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * Created by yannick on 4/22/2017.
 */

@RestController
@RequestMapping("/game")
public class GameController {
    private final UserService userService;
    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    @RequestMapping(path = "/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public GameHandler createNewGame(@RequestParam(required = false) Side side, HttpSession session) {

        GameHandler newGame = gameService.createNewGame(SessionUtils.getPlayer(session));

        if (side != null) {
            newGame.setPlayerToSide(SessionUtils.getPlayer(session), side);
        }

        return newGame;
    }

    @RequestMapping(path = "/move", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public BooleanResponse movePieceOfPlayer(CasePosition from, CasePosition to, String uuid, HttpSession session) {
        Player player = SessionUtils.getPlayer(session);
        boolean playerCanMoveInGame = PlayerUtils.isPlayerCanMovePiece(player, from, uuid, gameService);

        if (playerCanMoveInGame) {
            playerCanMoveInGame = gameService.movePiece(from, to, uuid);
        }

        return new BooleanResponse(playerCanMoveInGame, "");
    }
}
