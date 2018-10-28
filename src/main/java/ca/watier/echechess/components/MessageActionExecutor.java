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

package ca.watier.echechess.components;

import ca.watier.echechess.common.enums.CasePosition;
import ca.watier.echechess.common.enums.KingStatus;
import ca.watier.echechess.common.enums.MoveType;
import ca.watier.echechess.common.enums.Side;
import ca.watier.echechess.common.interfaces.WebSocketService;
import ca.watier.echechess.common.utils.Constants;
import ca.watier.echechess.communication.redis.interfaces.GameRepository;
import ca.watier.echechess.communication.redis.model.GenericGameHandlerWrapper;
import ca.watier.echechess.engine.engines.GenericGameHandler;
import ca.watier.echechess.pojos.AvailableMovePojo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static ca.watier.echechess.common.enums.ChessEventMessage.*;
import static ca.watier.echechess.common.enums.Side.getOtherPlayerSide;
import static ca.watier.echechess.common.utils.Constants.PLAYER_KING_CHECKMATE;
import static ca.watier.echechess.common.utils.Constants.PLAYER_MOVE;

public class MessageActionExecutor {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MessageActionExecutor.class);
    private final GameRepository<GenericGameHandler> gameRepository;
    private final WebSocketService webSocketService;
    private final ObjectMapper objectMapper;

    public MessageActionExecutor(GameRepository<GenericGameHandler> gameRepository, WebSocketService webSocketService, ObjectMapper objectMapper) {
        this.gameRepository = gameRepository;
        this.webSocketService = webSocketService;
        this.objectMapper = objectMapper;
    }

    public void handleMoveResponseMessage(String message) {

        if (StringUtils.isBlank(message)) {
            return;
        }

        handleReceivedMoveMessage(message);
    }

    private void handleReceivedMoveMessage(String messageAsString) {
        String[] messages = messageAsString.split("\\|");

        String uuid = messages[0];
        CasePosition from = CasePosition.valueOf(messages[1]);
        CasePosition to = CasePosition.valueOf(messages[2]);
        MoveType moveType = MoveType.getFromValue(Byte.parseByte(messages[3]));
        Side playerSide = Side.getFromValue(Byte.parseByte(messages[4]));

        GenericGameHandlerWrapper<GenericGameHandler> handlerWrapper = gameRepository.get(uuid);
        GenericGameHandler gameFromUuid = handlerWrapper.getGenericGameHandler();

        if (MoveType.isMoved(moveType)) {
            KingStatus currentKingStatus = gameFromUuid.getEvaluatedKingStatusBySide(playerSide);
            KingStatus otherKingStatus = gameFromUuid.getEvaluatedKingStatusBySide(Side.getOtherPlayerSide(playerSide));

            sendMovedPieceMessage(from, to, uuid, gameFromUuid, playerSide);
            sendCheckOrCheckmateMessages(currentKingStatus, otherKingStatus, playerSide, uuid);
        }
    }

    private void sendMovedPieceMessage(CasePosition from, CasePosition to, String uuid, GenericGameHandler gameFromUuid, Side playerSide) {
        webSocketService.fireGameEvent(uuid, MOVE, String.format(PLAYER_MOVE, playerSide, from, to));
        webSocketService.fireSideEvent(uuid, getOtherPlayerSide(playerSide), PLAYER_TURN, Constants.PLAYER_TURN);
        webSocketService.fireGameEvent(uuid, SCORE_UPDATE, gameFromUuid.getGameScore());
    }

    private void sendCheckOrCheckmateMessages(KingStatus currentkingStatus, KingStatus otherKingStatusAfterMove, Side playerSide, String uuid) {
        if (currentkingStatus == null || otherKingStatusAfterMove == null || playerSide == null) {
            return;
        }

        Side otherPlayerSide = getOtherPlayerSide(playerSide);

        if (KingStatus.CHECKMATE.equals(currentkingStatus)) {
            webSocketService.fireGameEvent(uuid, KING_CHECKMATE, String.format(PLAYER_KING_CHECKMATE, playerSide));
        } else if (KingStatus.CHECKMATE.equals(otherKingStatusAfterMove)) {
            webSocketService.fireGameEvent(uuid, KING_CHECKMATE, String.format(PLAYER_KING_CHECKMATE, otherPlayerSide));
        }

        if (KingStatus.CHECK.equals(currentkingStatus)) {
            webSocketService.fireSideEvent(uuid, playerSide, KING_CHECK, Constants.PLAYER_KING_CHECK);
        } else if (KingStatus.CHECK.equals(otherKingStatusAfterMove)) {
            webSocketService.fireSideEvent(uuid, otherPlayerSide, KING_CHECK, Constants.PLAYER_KING_CHECK);
        }
    }

    public void handleAvailMoveResponseMessage(String message) {

        if (StringUtils.isBlank(message)) {
            return;
        }

        String[] headers = message.split("\\|");
        String uuid = headers[0];
        String fromAsString = headers[1];
        Side playerSide = Side.getFromValue(Byte.valueOf(headers[2]));

        try {
            List<String> positions = objectMapper.readValue(headers[3], List.class);

            webSocketService.fireSideEvent(uuid, playerSide, AVAILABLE_MOVE, null, new AvailableMovePojo(fromAsString, positions));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
