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
import ca.watier.enums.ChessEventMessage;
import ca.watier.enums.Pieces;
import ca.watier.enums.Side;
import ca.watier.exceptions.GameException;
import ca.watier.game.GenericGameHandler;
import ca.watier.game.StandardGameHandler;
import ca.watier.responses.BooleanResponse;
import ca.watier.responses.ChessEvent;
import ca.watier.responses.DualValueResponse;
import ca.watier.services.GameService;
import ca.watier.sessions.Player;
import ca.watier.utils.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by yannick on 4/22/2017.
 */

@RestController
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;
    private final SimpMessagingTemplate template;

    @Autowired
    public GameController(GameService gameService, SimpMessagingTemplate template) {
        this.gameService = gameService;
        this.template = template;
    }

    /**
     * Create a new game
     *
     * @param side
     * @param againstComputer
     * @param observers
     * @param session
     * @return
     */
    @RequestMapping(path = "/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public GenericGameHandler createNewGame(Side side, boolean againstComputer, boolean observers, HttpSession session) {

        StandardGameHandler newGame = gameService.createNewGame(SessionUtils.getPlayer(session));

        if (side != null) {
            try {
                newGame.setPlayerToSide(SessionUtils.getPlayer(session), side);
            } catch (GameException e) {
                e.printStackTrace();
                return null;
            }
            newGame.setAllowOtherToJoin(!againstComputer);
            newGame.setAllowObservers(observers);
        }

        return newGame;
    }

    /**
     * Checks if the move is valid
     *
     * @param from
     * @param to
     * @param uuid
     * @param session
     * @return
     */
    @RequestMapping(path = "/move", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public BooleanResponse movePieceOfPlayer(CasePosition from, CasePosition to, String uuid, HttpSession session) {
        Player player = SessionUtils.getPlayer(session);

        boolean isMoved = false;

        try {
            isMoved = gameService.movePiece(from, to, uuid, player);
        } catch (GameException e) {
            fireChessEvent(uuid, ChessEventMessage.GAME_WON_EVENT_MOVE, "Cannot moves, the game is ended !");
        }

        if (isMoved) {
            fireChessEvent(uuid, ChessEventMessage.MOVE, String.format("%s player moved %s to %s", gameService.getPlayerSide(uuid, player), from, to));
        }

        return new BooleanResponse(isMoved, "");
    }

    private void fireChessEvent(String uuid, ChessEventMessage evtMessage, String message) {
        template.convertAndSend("/topic/" + uuid, new ChessEvent(evtMessage, message));
    }

    /**
     * Return a list of position that the piece can moves
     *
     * @param from
     * @param uuid
     * @param session
     * @return
     */
    @RequestMapping(path = "/moves", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getMovesOfAPiece(CasePosition from, String uuid, HttpSession session) {
        Player player = SessionUtils.getPlayer(session);

        StandardGameHandler gameFromUuid = gameService.getGameFromUuid(uuid);
        List<String> positions = null;

        if (gameFromUuid != null && gameFromUuid.hasPlayer(player)) {
            positions = gameService.getAllAvailableMoves(from, uuid, player);
        }

        return positions;
    }

    /**
     * Gets the pieces location
     *
     * @param uuid
     * @param session
     * @return
     */
    @RequestMapping(path = "/pieces", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DualValueResponse> getPieceLocations(String uuid, HttpSession session) {
        Player player = SessionUtils.getPlayer(session);

        StandardGameHandler gameFromUuid = gameService.getGameFromUuid(uuid);
        List<DualValueResponse> values = null;

        if (gameFromUuid != null) {
            values = new ArrayList<>();

            if ((!gameFromUuid.isAllowObservers() || !gameFromUuid.isAllowOtherToJoin()) && !gameFromUuid.hasPlayer(player)) {
                return null;
            }

            for (Map.Entry<CasePosition, Pieces> casePositionPiecesEntry : gameFromUuid.getPiecesLocation().entrySet()) {
                values.add(new DualValueResponse(casePositionPiecesEntry.getKey(), casePositionPiecesEntry.getValue(), ""));
            }
        }

        return values;
    }

    /**
     * Join a game
     *
     * @param uuid
     * @param side
     * @param session
     * @return
     */
    @RequestMapping(path = "/join", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public BooleanResponse joinGame(String uuid, Side side, HttpSession session) {
        Player player = SessionUtils.getPlayer(session);
        boolean joined = false;

        StandardGameHandler gameFromUuid = gameService.getGameFromUuid(uuid);

        UUID gameUuid = UUID.fromString(uuid);
        if (gameFromUuid != null && (gameFromUuid.isAllowOtherToJoin() || gameFromUuid.isAllowObservers()) &&
                !player.getCreatedGameList().contains(gameUuid) && !player.getJoinedGameList().contains(gameUuid)) {
            player.addJoinedGame(gameUuid);
            try {
                joined = gameFromUuid.setPlayerToSide(player, side);
            } catch (GameException e) {
                joined = false;
                e.printStackTrace();
            }
        }

        if (joined) {
            fireChessEvent(uuid, ChessEventMessage.PLAYER_JOINED, String.format("New player joined the %s side", side));
        }

        return new BooleanResponse(joined, "");
    }

    /**
     * Change side of a player
     *
     * @param side
     * @param uuid
     * @param session
     * @return
     */
    @RequestMapping(path = "/side", method = RequestMethod.POST)
    public BooleanResponse setSideOfPlayer(Side side, String uuid, HttpSession session) {
        Player player = SessionUtils.getPlayer(session);

        StandardGameHandler game = gameService.getGameFromUuid(uuid);

        if (game == null) {
            game = gameService.createNewGame(player);
        }

        boolean response = false;
        try {
            response = game.setPlayerToSide(player, side);
        } catch (GameException e) {
            e.printStackTrace();
        }

        return new BooleanResponse(response, "");
    }

}
