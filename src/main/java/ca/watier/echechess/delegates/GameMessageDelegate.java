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


import ca.watier.echechess.communication.redis.interfaces.GameRepository;
import ca.watier.echechess.components.MessageActionExecutor;
import ca.watier.echechess.engine.engines.GenericGameHandler;
import ca.watier.echechess.interfaces.GameMessage;
import ca.watier.echechess.models.EnvironmentProfile;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static ca.watier.echechess.models.EnvironmentProfile.DEPENDENT_MODE;

@Component
public class GameMessageDelegate implements GameMessage {

    private EnvironmentProfile activeProfile;
    private GameMessage independentGameMessage;
    private GameMessage dependentGameMessage;

    @Autowired(required = false)
    public GameMessageDelegate(@Value("${spring.profiles.active}") String activeProfile,
                               MessageActionExecutor messageActionExecutor,
                               GameRepository<GenericGameHandler> gameRepository,
                               ObjectMapper objectMapper) {

        this.activeProfile = EnumUtils.getEnum(EnvironmentProfile.class, activeProfile);
        independentGameMessage = new IndependentGameMessageImpl(messageActionExecutor, gameRepository, objectMapper);
    }

    @Autowired(required = false)
    public GameMessageDelegate(@Value("${spring.profiles.active}") String activeProfile,
                               RabbitTemplate rabbitTemplate) {
        this.activeProfile = EnumUtils.getEnum(EnvironmentProfile.class, activeProfile);
        dependentGameMessage = new DependentGameMessageImpl(rabbitTemplate);
    }


    @Override
    public void handleMoveMessage(String message) {
        if (DEPENDENT_MODE.equals(activeProfile)) {
            dependentGameMessage.handleMoveMessage(message);
        } else {
            independentGameMessage.handleMoveMessage(message);
        }
    }

    @Override
    public void handleAvailableMoveMessage(String message) {
        if (DEPENDENT_MODE.equals(activeProfile)) {
            dependentGameMessage.handleAvailableMoveMessage(message);
        } else {
            independentGameMessage.handleAvailableMoveMessage(message);
        }
    }
}
