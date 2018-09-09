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

package ca.watier.echechess.services;

import ca.watier.echechess.common.enums.CasePosition;
import ca.watier.echechess.common.enums.KingStatus;
import ca.watier.echechess.common.enums.MoveType;
import ca.watier.echechess.common.enums.Side;
import ca.watier.echechess.common.interfaces.WebSocketService;
import ca.watier.echechess.common.utils.Constants;
import ca.watier.echechess.engine.engines.GenericGameHandler;
import ca.watier.echechess.pojos.AvailableMovePojo;
import ca.watier.echechess.redis.interfaces.GameRepository;
import ca.watier.echechess.redis.model.GenericGameHandlerWrapper;
import ca.watier.echechess.redis.repositories.RedisGameRepositoryImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static ca.watier.echechess.common.enums.ChessEventMessage.*;
import static ca.watier.echechess.common.enums.Side.getOtherPlayerSide;
import static ca.watier.echechess.common.utils.Constants.PLAYER_KING_CHECKMATE;
import static ca.watier.echechess.common.utils.Constants.PLAYER_MOVE;

@Service
public class GameHandlerService implements MessageListener {
    private final GameRepository<GenericGameHandler> gameRepository;
    private final StringRedisSerializer stringRedisSerializer;
    private final WebSocketService webSocketService;
    private final ObjectMapper objectMapper;

    @Autowired
    public GameHandlerService(GameRepository<GenericGameHandler> gameRepository,
                              RedisMessageListenerContainer redisMessageContainer,
                              ChannelTopic moveNodeAppTopic,
                              ChannelTopic availableMoveNodeAppTopic,
                              RedisTemplate<String, GenericGameHandlerWrapper> redisTemplate,
                              WebSocketService webSocketService,
                              ObjectMapper objectMapper) {


        this.gameRepository = gameRepository;
        this.webSocketService = webSocketService;
        this.objectMapper = objectMapper;
        this.stringRedisSerializer = new StringRedisSerializer();

        redisTemplate.setValueSerializer(stringRedisSerializer);

        //Bind the message listener
        redisMessageContainer.addMessageListener(this, moveNodeAppTopic);
        redisMessageContainer.addMessageListener(this, availableMoveNodeAppTopic);
    }

    @Override
    public void onMessage(@NotNull Message message, byte[] pattern) {
        String messageAsString = stringRedisSerializer.deserialize(message.getBody());

        if (pattern == null || messageAsString == null) {
            return;
        }

        switch (new String(pattern)) {
            case RedisGameRepositoryImpl.REDIS_MOVE_NODE_TO_APP:
                handleReceivedMoveMessage(messageAsString);
                break;
            case RedisGameRepositoryImpl.REDIS_AVAILABLE_MOVE_NODE_TO_APP:
                handleReceivedAvailableMovesMessage(messageAsString);
                break;
        }
    }

    /**
     * Message pattern: {@link UUID#toString()}|{@link CasePosition from}|{@link CasePosition to}|{@link MoveType#getValue()}|{@link Side#getValue()()}
     *
     * @param messageAsString
     */
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

    /**
     * Message pattern: {@link UUID#toString()}|{@link CasePosition from}|{@link Byte side}|{@link List}
     *
     * @param messageAsString
     */
    private void handleReceivedAvailableMovesMessage(String messageAsString) {
        String[] headers = messageAsString.split("\\|");
        String uuid = headers[0];
        String fromAsString = headers[1];
        Side playerSide = Side.getFromValue(Byte.valueOf(headers[2]));

        try {
            List<String> positions = objectMapper.readValue(headers[3], List.class);

            webSocketService.fireSideEvent(uuid, playerSide, AVAILABLE_MOVE, null, new AvailableMovePojo(fromAsString, positions));
        } catch (IOException e) {
            e.printStackTrace();
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
}
