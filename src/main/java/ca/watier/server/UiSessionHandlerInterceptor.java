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

package ca.watier.server;

import ca.watier.enums.ChessEventMessage;
import ca.watier.services.UiSessionService;
import ca.watier.services.WebSocketService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

import static ca.watier.utils.Constants.THE_CLIENT_LOST_THE_CONNECTION;

/**
 * Created by yannick on 6/12/2017.
 */
@Component
public class UiSessionHandlerInterceptor extends HandlerInterceptorAdapter {

    private UiSessionService uiSessionService;
    private WebSocketService webSocketService;

    public UiSessionHandlerInterceptor(UiSessionService uiSessionService, WebSocketService webSocketService) {
        this.uiSessionService = uiSessionService;
        this.webSocketService = webSocketService;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean isAllowed = false;

        String[] uiUuids = request.getParameterValues("uiUuid");

        if (uiUuids != null && uiUuids.length == 1) { //Authorize only those with an active ui session
            String uiUuid = uiUuids[0];
            isAllowed = uiSessionService.isUiSessionActive(UUID.fromString(uiUuid));

            if (!isAllowed) {
                webSocketService.fireUiEvent(uiUuid, ChessEventMessage.UI_SESSION_EXPIRED, THE_CLIENT_LOST_THE_CONNECTION);
            }
        }

        return isAllowed;
    }
}
