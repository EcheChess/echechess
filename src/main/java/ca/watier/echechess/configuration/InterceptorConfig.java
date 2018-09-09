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

package ca.watier.echechess.configuration;

import ca.watier.echechess.common.interfaces.WebSocketService;
import ca.watier.echechess.server.UiSessionHandlerInterceptor;
import ca.watier.echechess.services.UiSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by yannick on 6/12/2017.
 */

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    private final UiSessionService uiSessionService;
    private final WebSocketService webSocketService;

    @Autowired
    public InterceptorConfig(UiSessionService uiSessionService, WebSocketService webSocketService) {
        this.uiSessionService = uiSessionService;
        this.webSocketService = webSocketService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UiSessionHandlerInterceptor(uiSessionService, webSocketService)).addPathPatterns("/api/game/**");
    }
}
