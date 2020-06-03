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


import ca.watier.echechess.interfaces.GameMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static ca.watier.echechess.communication.rabbitmq.configuration.RabbitMqConfiguration.AVAIL_MOVE_WORK_QUEUE_NAME;
import static ca.watier.echechess.communication.rabbitmq.configuration.RabbitMqConfiguration.MOVE_WORK_QUEUE_NAME;

public class DependentGameMessageImpl implements GameMessage {

    private final RabbitTemplate rabbitTemplate;

    public DependentGameMessageImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void handleMoveMessage(String message) {
        rabbitTemplate.convertAndSend(MOVE_WORK_QUEUE_NAME, message);
    }

    @Override
    public void handleAvailableMoveMessage(String message) {
        rabbitTemplate.convertAndSend(AVAIL_MOVE_WORK_QUEUE_NAME, message);
    }
}
