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

package ca.watier.echechess.configuration;

import ca.watier.echechess.redis.configuration.RedisConfiguration;
import ca.watier.echechess.services.GameHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class AppRedisConfiguration extends RedisConfiguration {

    private final GameHandlerService gameHandlerService;

    @Autowired
    public AppRedisConfiguration(GameHandlerService gameHandlerService) {
        this.gameHandlerService = gameHandlerService;
    }

    @Override
    public RedisMessageListenerContainer redisMessageContainer() {
        RedisMessageListenerContainer redisMessageListenerContainer = super.redisMessageContainer();
        redisMessageListenerContainer.addMessageListener(messageListener(gameHandlerService), gameMessageTopic());
        return redisMessageListenerContainer;
    }

    @Bean
    public MessageListenerAdapter messageListener(GameHandlerService gameFetcher) {
        return new MessageListenerAdapter(gameFetcher);
    }
}
