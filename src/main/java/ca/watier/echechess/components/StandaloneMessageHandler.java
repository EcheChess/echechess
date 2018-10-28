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
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.ReceiveAndReplyCallback;
import org.springframework.amqp.core.ReplyToAddressCallback;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelCallback;
import org.springframework.amqp.rabbit.core.RabbitOperations;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.core.ParameterizedTypeReference;

import java.util.ArrayList;
import java.util.List;

public class StandaloneMessageHandler implements RabbitOperations {

    private final MessageActionExecutor actionExecutor;
    private final GameRepository<GenericGameHandler> gameRepository;
    private final ObjectMapper objectMapper;

    public StandaloneMessageHandler(MessageActionExecutor actionExecutor, GameRepository<GenericGameHandler> gameRepository, ObjectMapper objectMapper) {
        this.actionExecutor = actionExecutor;
        this.gameRepository = gameRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> T execute(ChannelCallback<T> channelCallback) throws AmqpException {
        return null;
    }

    @Override
    public <T> T invoke(OperationsCallback<T> operationsCallback) throws AmqpException {
        return null;
    }

    @Override
    public boolean waitForConfirms(long l) throws AmqpException {
        return false;
    }

    @Override
    public void waitForConfirmsOrDie(long l) throws AmqpException {

    }

    @Override
    public ConnectionFactory getConnectionFactory() {
        return null;
    }

    @Override
    public void send(String s, String s1, Message message, CorrelationData correlationData) throws AmqpException {

    }

    @Override
    public void correlationConvertAndSend(Object o, CorrelationData correlationData) throws AmqpException {

    }

    @Override
    public void convertAndSend(String s, Object o, CorrelationData correlationData) throws AmqpException {

    }

    @Override
    public void convertAndSend(String s, String s1, Object o, CorrelationData correlationData) throws AmqpException {

    }

    @Override
    public void convertAndSend(Object o, MessagePostProcessor messagePostProcessor, CorrelationData correlationData) throws AmqpException {

    }

    @Override
    public void convertAndSend(String s, Object o, MessagePostProcessor messagePostProcessor, CorrelationData correlationData) throws AmqpException {

    }

    @Override
    public void convertAndSend(String s, String s1, Object o, MessagePostProcessor messagePostProcessor, CorrelationData correlationData) throws AmqpException {

    }

    @Override
    public Object convertSendAndReceive(Object o, CorrelationData correlationData) throws AmqpException {
        return null;
    }

    @Override
    public Object convertSendAndReceive(String s, Object o, CorrelationData correlationData) throws AmqpException {
        return null;
    }

    @Override
    public Object convertSendAndReceive(String s, String s1, Object o, CorrelationData correlationData) throws AmqpException {
        return null;
    }

    @Override
    public Object convertSendAndReceive(Object o, MessagePostProcessor messagePostProcessor, CorrelationData correlationData) throws AmqpException {
        return null;
    }

    @Override
    public Object convertSendAndReceive(String s, Object o, MessagePostProcessor messagePostProcessor, CorrelationData correlationData) throws AmqpException {
        return null;
    }

    @Override
    public Object convertSendAndReceive(String s, String s1, Object o, MessagePostProcessor messagePostProcessor, CorrelationData correlationData) throws AmqpException {
        return null;
    }

    @Override
    public <T> T convertSendAndReceiveAsType(Object o, CorrelationData correlationData, ParameterizedTypeReference<T> parameterizedTypeReference) throws AmqpException {
        return null;
    }

    @Override
    public <T> T convertSendAndReceiveAsType(String s, Object o, CorrelationData correlationData, ParameterizedTypeReference<T> parameterizedTypeReference) throws AmqpException {
        return null;
    }

    @Override
    public <T> T convertSendAndReceiveAsType(String s, String s1, Object o, CorrelationData correlationData, ParameterizedTypeReference<T> parameterizedTypeReference) throws AmqpException {
        return null;
    }

    @Override
    public <T> T convertSendAndReceiveAsType(Object o, MessagePostProcessor messagePostProcessor, CorrelationData correlationData, ParameterizedTypeReference<T> parameterizedTypeReference) throws AmqpException {
        return null;
    }

    @Override
    public <T> T convertSendAndReceiveAsType(String s, Object o, MessagePostProcessor messagePostProcessor, CorrelationData correlationData, ParameterizedTypeReference<T> parameterizedTypeReference) throws AmqpException {
        return null;
    }

    @Override
    public <T> T convertSendAndReceiveAsType(String s, String s1, Object o, MessagePostProcessor messagePostProcessor, CorrelationData correlationData, ParameterizedTypeReference<T> parameterizedTypeReference) throws AmqpException {
        return null;
    }

    @Override
    public void send(Message message) throws AmqpException {

    }

    @Override
    public void send(String s, Message message) throws AmqpException {

    }

    @Override
    public void send(String s, String s1, Message message) throws AmqpException {

    }

    @Override
    public void convertAndSend(Object o) throws AmqpException {

    }

    @Override
    public void convertAndSend(String queueName, Object message) throws AmqpException {
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
                handleAvailMoves(message, headers[2], uuid, genericGameHandler, from);
                break;
            case RabbitMqConfiguration.MOVE_WORK_QUEUE_NAME:
                handleMove(headers, uuid, genericGameHandler, from);
                break;
        }
    }

    private void handleAvailMoves(String message, String header, String uuid, GenericGameHandler genericGameHandler, CasePosition from) {
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
            e.printStackTrace();
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
    public void convertAndSend(String s, String s1, Object o) throws AmqpException {

    }

    @Override
    public void convertAndSend(Object o, MessagePostProcessor messagePostProcessor) throws AmqpException {

    }

    @Override
    public void convertAndSend(String s, Object o, MessagePostProcessor messagePostProcessor) throws AmqpException {

    }

    @Override
    public void convertAndSend(String s, String s1, Object o, MessagePostProcessor messagePostProcessor) throws AmqpException {

    }

    @Override
    public Message receive() throws AmqpException {
        return null;
    }

    @Override
    public Message receive(String s) throws AmqpException {
        return null;
    }

    @Override
    public Message receive(long l) throws AmqpException {
        return null;
    }

    @Override
    public Message receive(String s, long l) throws AmqpException {
        return null;
    }

    @Override
    public Object receiveAndConvert() throws AmqpException {
        return null;
    }

    @Override
    public Object receiveAndConvert(String s) throws AmqpException {
        return null;
    }

    @Override
    public Object receiveAndConvert(long l) throws AmqpException {
        return null;
    }

    @Override
    public Object receiveAndConvert(String s, long l) throws AmqpException {
        return null;
    }

    @Override
    public <T> T receiveAndConvert(ParameterizedTypeReference<T> parameterizedTypeReference) throws AmqpException {
        return null;
    }

    @Override
    public <T> T receiveAndConvert(String s, ParameterizedTypeReference<T> parameterizedTypeReference) throws AmqpException {
        return null;
    }

    @Override
    public <T> T receiveAndConvert(long l, ParameterizedTypeReference<T> parameterizedTypeReference) throws AmqpException {
        return null;
    }

    @Override
    public <T> T receiveAndConvert(String s, long l, ParameterizedTypeReference<T> parameterizedTypeReference) throws AmqpException {
        return null;
    }

    @Override
    public <R, S> boolean receiveAndReply(ReceiveAndReplyCallback<R, S> receiveAndReplyCallback) throws AmqpException {
        return false;
    }

    @Override
    public <R, S> boolean receiveAndReply(String s, ReceiveAndReplyCallback<R, S> receiveAndReplyCallback) throws AmqpException {
        return false;
    }

    @Override
    public <R, S> boolean receiveAndReply(ReceiveAndReplyCallback<R, S> receiveAndReplyCallback, String s, String s1) throws AmqpException {
        return false;
    }

    @Override
    public <R, S> boolean receiveAndReply(String s, ReceiveAndReplyCallback<R, S> receiveAndReplyCallback, String s1, String s2) throws AmqpException {
        return false;
    }

    @Override
    public <R, S> boolean receiveAndReply(ReceiveAndReplyCallback<R, S> receiveAndReplyCallback, ReplyToAddressCallback<S> replyToAddressCallback) throws AmqpException {
        return false;
    }

    @Override
    public <R, S> boolean receiveAndReply(String s, ReceiveAndReplyCallback<R, S> receiveAndReplyCallback, ReplyToAddressCallback<S> replyToAddressCallback) throws AmqpException {
        return false;
    }

    @Override
    public Message sendAndReceive(Message message) throws AmqpException {
        return null;
    }

    @Override
    public Message sendAndReceive(String s, Message message) throws AmqpException {
        return null;
    }

    @Override
    public Message sendAndReceive(String s, String s1, Message message) throws AmqpException {
        return null;
    }

    @Override
    public Object convertSendAndReceive(Object o) throws AmqpException {
        return null;
    }

    @Override
    public Object convertSendAndReceive(String s, Object o) throws AmqpException {
        return null;
    }

    @Override
    public Object convertSendAndReceive(String s, String s1, Object o) throws AmqpException {
        return null;
    }

    @Override
    public Object convertSendAndReceive(Object o, MessagePostProcessor messagePostProcessor) throws AmqpException {
        return null;
    }

    @Override
    public Object convertSendAndReceive(String s, Object o, MessagePostProcessor messagePostProcessor) throws AmqpException {
        return null;
    }

    @Override
    public Object convertSendAndReceive(String s, String s1, Object o, MessagePostProcessor messagePostProcessor) throws AmqpException {
        return null;
    }

    @Override
    public <T> T convertSendAndReceiveAsType(Object o, ParameterizedTypeReference<T> parameterizedTypeReference) throws AmqpException {
        return null;
    }

    @Override
    public <T> T convertSendAndReceiveAsType(String s, Object o, ParameterizedTypeReference<T> parameterizedTypeReference) throws AmqpException {
        return null;
    }

    @Override
    public <T> T convertSendAndReceiveAsType(String s, String s1, Object o, ParameterizedTypeReference<T> parameterizedTypeReference) throws AmqpException {
        return null;
    }

    @Override
    public <T> T convertSendAndReceiveAsType(Object o, MessagePostProcessor messagePostProcessor, ParameterizedTypeReference<T> parameterizedTypeReference) throws AmqpException {
        return null;
    }

    @Override
    public <T> T convertSendAndReceiveAsType(String s, Object o, MessagePostProcessor messagePostProcessor, ParameterizedTypeReference<T> parameterizedTypeReference) throws AmqpException {
        return null;
    }

    @Override
    public <T> T convertSendAndReceiveAsType(String s, String s1, Object o, MessagePostProcessor messagePostProcessor, ParameterizedTypeReference<T> parameterizedTypeReference) throws AmqpException {
        return null;
    }
}
