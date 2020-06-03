/*
 *    Copyright 2014 - 2019 Yannick Watier
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

package ca.watier.echechess.delegates;


import ca.watier.echechess.common.enums.CasePosition;
import ca.watier.echechess.common.enums.MoveType;
import ca.watier.echechess.common.enums.Side;
import ca.watier.echechess.communication.redis.interfaces.GameRepository;
import ca.watier.echechess.communication.redis.model.GenericGameHandlerWrapper;
import ca.watier.echechess.components.MessageActionExecutor;
import ca.watier.echechess.engine.engines.GenericGameHandler;
import ca.watier.echechess.interfaces.GameMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static ca.watier.echechess.communication.rabbitmq.configuration.RabbitMqConfiguration.AVAIL_MOVE_WORK_QUEUE_NAME;
import static ca.watier.echechess.communication.rabbitmq.configuration.RabbitMqConfiguration.MOVE_WORK_QUEUE_NAME;

public class IndependentGameMessageImpl implements GameMessage {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(IndependentGameMessageImpl.class);

    private final MessageActionExecutor actionExecutor;
    private final GameRepository<GenericGameHandler> gameRepository;
    private final ObjectMapper objectMapper;

    public IndependentGameMessageImpl(MessageActionExecutor actionExecutor, GameRepository<GenericGameHandler> gameRepository, ObjectMapper objectMapper) {
        this.actionExecutor = actionExecutor;
        this.gameRepository = gameRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handleMoveMessage(String message) {
        handleChessMessage(MOVE_WORK_QUEUE_NAME, message);
    }

    @Override
    public void handleAvailableMoveMessage(String message) {
        handleChessMessage(AVAIL_MOVE_WORK_QUEUE_NAME, message);
    }

    private void handleChessMessage(String queueName, String message) {
        String[] headers = message.split("\\|");
        String uuid = headers[0];
        GenericGameHandlerWrapper<GenericGameHandler> wrapper = gameRepository.get(uuid);
        GenericGameHandler genericGameHandler = wrapper.getGenericGameHandler();
        CasePosition from = CasePosition.valueOf(headers[1]);

        switch (queueName) {
            case AVAIL_MOVE_WORK_QUEUE_NAME -> handleAvailMoves(headers[2], uuid, genericGameHandler, from);
            case MOVE_WORK_QUEUE_NAME -> handleMove(headers, uuid, genericGameHandler, from);
        }
    }

    private void handleAvailMoves(String header, String uuid, GenericGameHandler genericGameHandler, CasePosition from) {
        Side playerSide;
        byte playerSideValue = Byte.parseByte(header);
        playerSide = Side.getFromValue(playerSideValue);


        List<String> postionsAsString = new ArrayList<>();
        for (CasePosition casePosition : genericGameHandler.getAllAvailableMoves(from, playerSide)) {
            postionsAsString.add(casePosition.name());
        }

        try {
            String payload = uuid + '|' + from + '|' + playerSideValue + '|' + objectMapper.writeValueAsString(postionsAsString);
            actionExecutor.handleAvailMoveResponseMessage(payload);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void handleMove(String[] headers, String uuid, GenericGameHandler genericGameHandler, CasePosition from) {
        Side playerSide;
        CasePosition to = CasePosition.valueOf(headers[2]);
        playerSide = Side.getFromValue(Byte.parseByte(headers[3]));
        MoveType moveType = genericGameHandler.movePiece(from, to, playerSide);

        if (MoveType.isMoved(moveType)) {
            actionExecutor.handleMoveResponseMessage(uuid + '|' + from + '|' + to + '|' + moveType.getValue() + '|' + playerSide.getValue());
        }
    }

}
