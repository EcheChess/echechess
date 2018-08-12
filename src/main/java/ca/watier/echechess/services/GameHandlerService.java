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

import ca.watier.echechess.common.enums.RedisGameEvent;
import ca.watier.echechess.redis.interfaces.GameRepository;
import ca.watier.echechess.redis.model.GenericGameHandlerWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;

@Service
public class GameHandlerService implements MessageListener {
    private final GameRepository gameRepository;
    private final RedisSerializer<String> valueSerializer;

    @Autowired
    public GameHandlerService(GameRepository gameRepository,
                              RedisMessageListenerContainer redisMessageContainer,
                              ChannelTopic gameMessageTopic,
                              RedisTemplate<String, GenericGameHandlerWrapper> redisTemplateGenericGameHandlerWrapper) {

        valueSerializer = (RedisSerializer<String>) redisTemplateGenericGameHandlerWrapper.getValueSerializer();

        this.gameRepository = gameRepository;
        redisMessageContainer.addMessageListener(this, gameMessageTopic); //Bind the message listener
    }

    @Override
    public void onMessage(@NotNull Message message, byte[] pattern) {
        String messageAsString = valueSerializer.deserialize(message.getBody());

        if(messageAsString == null || messageAsString.isEmpty()) {
            return;
        }

        int totalLength = messageAsString.length();
        int uuidStartPos = totalLength - 36;

        RedisGameEvent redisGameEvent = RedisGameEvent.getFromValue(Byte.parseByte(messageAsString.substring(0, uuidStartPos)));
        String gameId = messageAsString.substring(uuidStartPos, totalLength);

        handleGameMessage(redisGameEvent, gameId);
    }

    private void handleGameMessage(RedisGameEvent gameEvent, String gameId) {
        switch (gameEvent) {
            case MOVE:
                handleMoveMessage(gameId);
                break;
        }
    }

    private void handleMoveMessage(String gameId) {
        GenericGameHandlerWrapper genericGameHandlerWrapper = gameRepository.get(gameId);
        System.out.println();
    }

    public void fetchNewGames() {
        List<GenericGameHandlerWrapper> games = gameRepository.getAll();
        System.out.println(games);
    }
}
