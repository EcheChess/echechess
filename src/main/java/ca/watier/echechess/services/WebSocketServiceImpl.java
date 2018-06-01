/*
 *    Copyright 2014 - 2017 Yannick Watier
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

import ca.watier.echechess.common.enums.ChessEventMessage;
import ca.watier.echechess.common.enums.Side;
import ca.watier.echechess.common.interfaces.WebSocketService;
import ca.watier.echechess.common.responses.ChessEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by yannick on 6/10/2017.
 */

@Service
public class WebSocketServiceImpl implements WebSocketService {

    private static final String TOPIC = "/topic/";
    private final SimpMessagingTemplate template;

    @Autowired
    public WebSocketServiceImpl(SimpMessagingTemplate template) {
        this.template = template;
    }

    public void fireSideEvent(String uuid, Side side, ChessEventMessage evtMessage, String message) {
        assertThat(side).isNotNull();
        assertThat(evtMessage).isNotNull();
        assertThat(uuid).isNotEmpty();
        assertThat(message).isNotEmpty();

        template.convertAndSend(TOPIC + uuid + '/' + side, new ChessEvent(evtMessage, message));
    }

    @Override
    public void fireSideEvent(String uuid, Side side, ChessEventMessage evtMessage, String message, Object obj) {
        assertThat(side).isNotNull();
        assertThat(evtMessage).isNotNull();
        assertThat(uuid).isNotEmpty();
        assertThat(message).isNotEmpty();


        ChessEvent payload = new ChessEvent(evtMessage, message);
        payload.setObj(obj);
        template.convertAndSend(TOPIC + uuid + '/' + side, payload);
    }

    public void fireUiEvent(String uiUuid, ChessEventMessage evtMessage, String message) {
        template.convertAndSend(TOPIC + uiUuid, new ChessEvent(evtMessage, message));
    }


    public void fireGameEvent(String uuid, ChessEventMessage evtMessage, Object message) {
        template.convertAndSend(TOPIC + uuid, new ChessEvent(evtMessage, message));
    }

    @Override
    public void fireGameEvent(String uuid, ChessEventMessage refreshBoard) {
        template.convertAndSend(TOPIC + uuid, new ChessEvent(refreshBoard));
    }
}
