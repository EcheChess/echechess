/*
 *    Copyright 2014 - 2021 Yannick Watier
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

package ca.watier.echechess.configuration.mode;

import ca.watier.echechess.common.services.WebSocketService;
import ca.watier.echechess.communication.redis.interfaces.GameRepository;
import ca.watier.echechess.components.MessageActionExecutor;
import ca.watier.echechess.components.MessageActionExecutorImpl;
import ca.watier.echechess.engine.delegates.PieceMoveConstraintDelegate;
import ca.watier.echechess.engine.engines.GenericGameHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GenericConfiguration {

    @Bean
    public MessageActionExecutor messageActionExecutor(GameRepository<GenericGameHandler> gameRepository,
                                                       WebSocketService webSocketService,
                                                       ObjectMapper objectMapper,
                                                       PieceMoveConstraintDelegate gameMoveConstraintDelegate) {
        return new MessageActionExecutorImpl(gameMoveConstraintDelegate, gameRepository, webSocketService, objectMapper);
    }

}
