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
import ca.watier.echechess.common.enums.MoveType;
import ca.watier.echechess.common.enums.Side;
import ca.watier.echechess.communication.rabbitmq.configuration.RabbitMqConfiguration;
import ca.watier.echechess.communication.redis.interfaces.GameRepository;
import ca.watier.echechess.communication.redis.model.GenericGameHandlerWrapper;
import ca.watier.echechess.engine.engines.GenericGameHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.ConfirmCallback;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.ReceiveAndReplyCallback;
import org.springframework.amqp.core.ReplyToAddressCallback;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.ChannelCallback;
import org.springframework.amqp.rabbit.core.RabbitOperations;
import org.springframework.core.ParameterizedTypeReference;

import java.util.ArrayList;
import java.util.List;

public class StandaloneMessageHandler implements RabbitOperations {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(StandaloneMessageHandler.class);
    private final MessageActionExecutor actionExecutor;
    private final GameRepository<GenericGameHandler> gameRepository;
    private final ObjectMapper objectMapper;

    public StandaloneMessageHandler(MessageActionExecutor actionExecutor, GameRepository<GenericGameHandler> gameRepository, ObjectMapper objectMapper) {
        this.actionExecutor = actionExecutor;
        this.gameRepository = gameRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> T execute(ChannelCallback<T> channelCallback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T invoke(OperationsCallback<T> operationsCallback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T invoke(OperationsCallback<T> operationsCallback, ConfirmCallback confirmCallback, ConfirmCallback confirmCallback1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean waitForConfirms(long l) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void waitForConfirmsOrDie(long l) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConnectionFactory getConnectionFactory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void send(String s, String s1, Message message, CorrelationData correlationData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void correlationConvertAndSend(Object o, CorrelationData correlationData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void convertAndSend(String s, Object o, CorrelationData correlationData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void convertAndSend(String s, String s1, Object o, CorrelationData correlationData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void convertAndSend(Object o, MessagePostProcessor messagePostProcessor, CorrelationData correlationData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void convertAndSend(String s, Object o, MessagePostProcessor messagePostProcessor, CorrelationData correlationData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void convertAndSend(String s, String s1, Object o, MessagePostProcessor messagePostProcessor, CorrelationData correlationData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object convertSendAndReceive(Object o, CorrelationData correlationData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object convertSendAndReceive(String s, Object o, CorrelationData correlationData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object convertSendAndReceive(String s, String s1, Object o, CorrelationData correlationData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object convertSendAndReceive(Object o, MessagePostProcessor messagePostProcessor, CorrelationData correlationData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object convertSendAndReceive(String s, Object o, MessagePostProcessor messagePostProcessor, CorrelationData correlationData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object convertSendAndReceive(String s, String s1, Object o, MessagePostProcessor messagePostProcessor, CorrelationData correlationData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T convertSendAndReceiveAsType(Object o, CorrelationData correlationData, ParameterizedTypeReference<T> parameterizedTypeReference) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T convertSendAndReceiveAsType(String s, Object o, CorrelationData correlationData, ParameterizedTypeReference<T> parameterizedTypeReference) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T convertSendAndReceiveAsType(String s, String s1, Object o, CorrelationData correlationData, ParameterizedTypeReference<T> parameterizedTypeReference) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T convertSendAndReceiveAsType(Object o, MessagePostProcessor messagePostProcessor, CorrelationData correlationData, ParameterizedTypeReference<T> parameterizedTypeReference) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T convertSendAndReceiveAsType(String s, Object o, MessagePostProcessor messagePostProcessor, CorrelationData correlationData, ParameterizedTypeReference<T> parameterizedTypeReference) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T convertSendAndReceiveAsType(String s, String s1, Object o, MessagePostProcessor messagePostProcessor, CorrelationData correlationData, ParameterizedTypeReference<T> parameterizedTypeReference) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void send(Message message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void send(String s, Message message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void send(String s, String s1, Message message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void convertAndSend(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void convertAndSend(String queueName, Object message) {
        handleChessMessage(queueName, (String) message);
    }

    private void handleChessMessage(String queueName, String message) {
        String[] headers = message.split("\\|");
        String uuid = headers[0];
        GenericGameHandlerWrapper<GenericGameHandler> wrapper = gameRepository.get(uuid);
        GenericGameHandler genericGameHandler = wrapper.getGenericGameHandler();
        CasePosition from = CasePosition.valueOf(headers[1]);

        switch (queueName) {
            case RabbitMqConfiguration.AVAIL_MOVE_WORK_QUEUE_NAME:
                handleAvailMoves(headers[2], uuid, genericGameHandler, from);
                break;
            case RabbitMqConfiguration.MOVE_WORK_QUEUE_NAME:
                handleMove(headers, uuid, genericGameHandler, from);
                break;
        }
    }

    private void handleAvailMoves(String header, String uuid, GenericGameHandler genericGameHandler, CasePosition from) {
        Side playerSide;
        byte playerSideValue = Byte.valueOf(header);
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
        playerSide = Side.getFromValue(Byte.valueOf(headers[3]));
        MoveType moveType = genericGameHandler.movePiece(from, to, playerSide);

        if (MoveType.isMoved(moveType)) {
            actionExecutor.handleMoveResponseMessage(uuid + '|' + from + '|' + to + '|' + moveType.getValue() + '|' + playerSide.getValue());
        }
    }

    @Override
    public void convertAndSend(String s, String s1, Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void convertAndSend(Object o, MessagePostProcessor messagePostProcessor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void convertAndSend(String s, Object o, MessagePostProcessor messagePostProcessor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void convertAndSend(String s, String s1, Object o, MessagePostProcessor messagePostProcessor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Message receive() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Message receive(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Message receive(long l) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Message receive(String s, long l) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object receiveAndConvert() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object receiveAndConvert(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object receiveAndConvert(long l) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object receiveAndConvert(String s, long l) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T receiveAndConvert(ParameterizedTypeReference<T> parameterizedTypeReference) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T receiveAndConvert(String s, ParameterizedTypeReference<T> parameterizedTypeReference) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T receiveAndConvert(long l, ParameterizedTypeReference<T> parameterizedTypeReference) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T receiveAndConvert(String s, long l, ParameterizedTypeReference<T> parameterizedTypeReference) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <R, S> boolean receiveAndReply(ReceiveAndReplyCallback<R, S> receiveAndReplyCallback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <R, S> boolean receiveAndReply(String s, ReceiveAndReplyCallback<R, S> receiveAndReplyCallback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <R, S> boolean receiveAndReply(ReceiveAndReplyCallback<R, S> receiveAndReplyCallback, String s, String s1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <R, S> boolean receiveAndReply(String s, ReceiveAndReplyCallback<R, S> receiveAndReplyCallback, String s1, String s2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <R, S> boolean receiveAndReply(ReceiveAndReplyCallback<R, S> receiveAndReplyCallback, ReplyToAddressCallback<S> replyToAddressCallback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <R, S> boolean receiveAndReply(String s, ReceiveAndReplyCallback<R, S> receiveAndReplyCallback, ReplyToAddressCallback<S> replyToAddressCallback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Message sendAndReceive(Message message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Message sendAndReceive(String s, Message message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Message sendAndReceive(String s, String s1, Message message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object convertSendAndReceive(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object convertSendAndReceive(String s, Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object convertSendAndReceive(String s, String s1, Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object convertSendAndReceive(Object o, MessagePostProcessor messagePostProcessor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object convertSendAndReceive(String s, Object o, MessagePostProcessor messagePostProcessor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object convertSendAndReceive(String s, String s1, Object o, MessagePostProcessor messagePostProcessor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T convertSendAndReceiveAsType(Object o, ParameterizedTypeReference<T> parameterizedTypeReference) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T convertSendAndReceiveAsType(String s, Object o, ParameterizedTypeReference<T> parameterizedTypeReference) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T convertSendAndReceiveAsType(String s, String s1, Object o, ParameterizedTypeReference<T> parameterizedTypeReference) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T convertSendAndReceiveAsType(Object o, MessagePostProcessor messagePostProcessor, ParameterizedTypeReference<T> parameterizedTypeReference) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T convertSendAndReceiveAsType(String s, Object o, MessagePostProcessor messagePostProcessor, ParameterizedTypeReference<T> parameterizedTypeReference) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T convertSendAndReceiveAsType(String s, String s1, Object o, MessagePostProcessor messagePostProcessor, ParameterizedTypeReference<T> parameterizedTypeReference) {
        throw new UnsupportedOperationException();
    }
}
