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

import ca.watier.echechess.common.enums.CasePosition;
import ca.watier.echechess.common.enums.Side;
import ca.watier.echechess.common.responses.BooleanResponse;
import ca.watier.echechess.common.responses.StringResponse;
import ca.watier.echechess.engine.exceptions.FenParserException;
import ca.watier.echechess.exceptions.GameException;
import ca.watier.echechess.models.PawnPromotionPiecesModel;
import ca.watier.echechess.models.PieceLocationModel;
import ca.watier.echechess.models.UserDetailsImpl;
import ca.watier.echechess.services.GameService;
import ca.watier.echechess.services.UserService;
import ca.watier.echechess.utils.AuthenticationUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Created by yannick on 4/22/2017.
 */

@RestController
@RequestMapping("api/v1/game")
@PreAuthorize("#oauth2.hasScope('read')")
public class GameController {
    public static final String UI_UUID_PLAYER = "The UI-UUID of the player";
    private static final String UUID_GAME = "The UUID of the game";
    private static final String SIDE_PLAYER = "The side of the player";
    private static final String UPGRADED_PIECE = "The upgraded piece";
    private static final String TO_POSITION = "The to position";
    private static final String FROM_POSITION = "The from position";
    private static final String PLAY_AGAINST_THE_AI = "Create a new game to play against the AI";
    private static final String WITH_OR_WITHOUT_OBSERVERS = "Create a new game with or without observers";
    private static final String PATTERN_CUSTOM_GAME = "Pattern used to create a custom game";
    private static final ResponseEntity NO_CONTENT_RESPONSE_ENTITY = ResponseEntity.noContent().build();
    private static final ResponseEntity BAD_REQUEST_RESPONSE_ENTITY = ResponseEntity.badRequest().build();

    private final GameService gameService;
    private final UserService userService;

    @Autowired
    public GameController(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }


    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "There's an issue with the game creation"),
            @ApiResponse(code = 200, message = "The game is created")
    })
    @ApiOperation("Create a new game for the current player")
    @PostMapping(path = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createNewGame(@ApiParam(value = SIDE_PLAYER, required = true) Side side,
                                           @ApiParam(value = PLAY_AGAINST_THE_AI, required = true) boolean againstComputer,
                                           @ApiParam(value = WITH_OR_WITHOUT_OBSERVERS, required = true) boolean observers,
                                           @ApiParam(value = PATTERN_CUSTOM_GAME) String specialGamePieces) {

        try {
            UUID newGameUuid = gameService.createNewGame(specialGamePieces, side, againstComputer, observers, AuthenticationUtils.getUserDetail());

            UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            principal.addCreatedGame(newGameUuid);

            userService.addGameToUser(principal.getUsername(), newGameUuid);
            return ResponseEntity.ok(new StringResponse(newGameUuid.toString()));
        } catch (FenParserException | GameException ignored) {
            return BAD_REQUEST_RESPONSE_ENTITY;
        }
    }

    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "There's an issue with when moving the piece."),
            @ApiResponse(code = 204, message = "The result of this query will be sent on the web socket, when ready.")
    })
    @ApiOperation("Move the selected piece")
    @PreAuthorize("isPlayerInGame(#uuid)")
    @PostMapping(path = "/move", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> movePieceOfPlayer(@ApiParam(value = FROM_POSITION, required = true) CasePosition from,
                                                  @ApiParam(value = TO_POSITION, required = true) CasePosition to,
                                                  @ApiParam(value = UUID_GAME, required = true) String uuid) {

        try {
            gameService.movePiece(from, to, uuid, AuthenticationUtils.getUserDetail());
        } catch (GameException e) {
            return BAD_REQUEST_RESPONSE_ENTITY;
        }

        return NO_CONTENT_RESPONSE_ENTITY;
    }


    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "There's an issue when fetching the available moves."),
            @ApiResponse(code = 204, message = "The result of this query will be sent on the web socket, when ready.")
    })
    @ApiOperation("Get a list of position that the piece can moves")
    @PreAuthorize("isPlayerInGame(#uuid)")
    @GetMapping(path = "/moves", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> getMovesOfAPiece(@ApiParam(value = FROM_POSITION, required = true) CasePosition from,
                                                 @ApiParam(value = UUID_GAME, required = true) String uuid) {

        try {
            gameService.getAllAvailableMoves(from, uuid, AuthenticationUtils.getUserDetail());
        } catch (GameException e) {
            return BAD_REQUEST_RESPONSE_ENTITY;
        }

        return NO_CONTENT_RESPONSE_ENTITY;
    }

    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "There's an issue when promoting the pawn."),
            @ApiResponse(code = 200, message = "The result of the promoting (true / false).")
    })
    @ApiOperation("Used when there's a pawn promotion")
    @PreAuthorize("isPlayerInGame(#uuid)")
    @PostMapping(path = "/piece/pawn/promotion", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> pawnPromotion(@ApiParam(value = TO_POSITION, required = true) CasePosition to,
                                                 @ApiParam(value = UUID_GAME, required = true) String uuid,
                                                 @ApiParam(value = UPGRADED_PIECE, required = true) PawnPromotionPiecesModel piece) {
        try {
            return ResponseEntity.ok(gameService.upgradePiece(to, uuid, piece, AuthenticationUtils.getUserDetail()));
        } catch (GameException exception) {
            return BAD_REQUEST_RESPONSE_ENTITY;
        }
    }

    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "There's an issue when fetching the pieces of the game."),
            @ApiResponse(code = 200, message = "The pieces with their location.")
    })
    @ApiOperation("Gets the pieces location")
    @PreAuthorize("isPlayerInGame(#uuid)")
    @GetMapping(path = "/pieces", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PieceLocationModel>> getPieceLocations(@ApiParam(value = UUID_GAME, required = true) String uuid) {
        try {
            return ResponseEntity.ok(gameService.getIterableBoard(uuid, AuthenticationUtils.getUserDetail()));
        } catch (GameException e) {
            return BAD_REQUEST_RESPONSE_ENTITY;
        }
    }

    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "There's an issue when joining the game."),
            @ApiResponse(code = 200, message = "The player has joined the game successfully.")
    })
    @ApiOperation("Join a game for the current player")
    @PostMapping(path = "/join", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BooleanResponse> joinGame(@ApiParam(value = UUID_GAME, required = true) String uuid,
                                                    @ApiParam(value = SIDE_PLAYER, required = true) Side side,
                                                    @ApiParam(value = UI_UUID_PLAYER, required = true) String uiUuid) {


        BooleanResponse response;
        try {
            response = gameService.joinGame(uuid, side, uiUuid, AuthenticationUtils.getUserDetail());
        } catch (GameException e) {
            return BAD_REQUEST_RESPONSE_ENTITY;
        }

        if (response.isResponse()) {
            UUID newGameUuid = UUID.fromString(uuid);
            UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            principal.addCreatedGame(newGameUuid);

            userService.addGameToUser(principal.getUsername(), newGameUuid);
        }

        return ResponseEntity.ok(response);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "There's an issue when switching the side of the player."),
            @ApiResponse(code = 200, message = "The result if the player side is changed (true / false)")
    })
    @ApiOperation("Change the side of the current player")
    @PreAuthorize("isPlayerInGame(#uuid)")
    @PostMapping(path = "/side")
    public ResponseEntity<Boolean> setSideOfPlayer(@ApiParam(value = SIDE_PLAYER, required = true) Side side,
                                                   @ApiParam(value = UUID_GAME, required = true) String uuid) {
        try {
            return ResponseEntity.ok(gameService.setSideOfPlayer(side, uuid, AuthenticationUtils.getUserDetail()));
        } catch (GameException e) {
            return BAD_REQUEST_RESPONSE_ENTITY;
        }
    }
}
