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

import org.springframework.amqp.rabbit.annotation.RabbitListener;


public class DependentGameMessageHandler {
    private final MessageActionExecutor actionExecutor;

    public DependentGameMessageHandler(MessageActionExecutor actionExecutor) {
        this.actionExecutor = actionExecutor;
    }

    @RabbitListener(queues = "#{nodeToAppMoveQueue.name}")
    public void handleMoveResponseMessage(String message) {
        actionExecutor.handleMoveResponseMessage(message);
    }

    @RabbitListener(queues = "#{nodeToAppAvailMoveQueue.name}")
    public void handleAvailMoveResponseMessage(String message) {
        actionExecutor.handleAvailMoveResponseMessage(message);
    }
}
