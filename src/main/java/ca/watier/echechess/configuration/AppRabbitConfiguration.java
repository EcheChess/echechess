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

import ca.watier.echechess.communication.rabbitmq.configuration.RabbitMqConfiguration;
import ca.watier.echechess.communication.redis.pojos.ServerInfoPojo;
import ca.watier.echechess.services.GameMessageHandlerService;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppRabbitConfiguration extends RabbitMqConfiguration {

    @Autowired
    public AppRabbitConfiguration(@Qualifier("rabbitMqServerPojo") ServerInfoPojo redisServerPojo) {
        super(redisServerPojo);
    }

    @Bean
    public MessageListenerAdapter messageListener(GameMessageHandlerService gameMessageHandlerService) {
        return new MessageListenerAdapter(gameMessageHandlerService);
    }
}
