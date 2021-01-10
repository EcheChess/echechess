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

/**
 * Created by yannick on 4/30/2017.
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.config.SimpleBrokerRegistration;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    private static final long TIMEOUT_SESSION_IN_MILLIS = TimeUnit.MINUTES.toMillis(2);
    private static final long SESSION_HEARTH_BEAT_INTERVAL_IN_MILLIS = TimeUnit.SECONDS.toMillis(10);
    private static final long[] HEARTBEAT =
            {
                    SESSION_HEARTH_BEAT_INTERVAL_IN_MILLIS, //Server
                    SESSION_HEARTH_BEAT_INTERVAL_IN_MILLIS //Client
            };

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        SimpleBrokerRegistration simpleBrokerRegistration = config.enableSimpleBroker("/topic");
        simpleBrokerRegistration.setTaskScheduler(new ConcurrentTaskScheduler());
        simpleBrokerRegistration.setHeartbeatValue(HEARTBEAT);

        config.setApplicationDestinationPrefixes("/app");
    }

    //https://docs.spring.io/spring-security/site/docs/current/reference/html5/#websocket-authorization
    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages.anyMessage().authenticated();
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxBinaryMessageBufferSize(8192);
        container.setMaxSessionIdleTimeout(TIMEOUT_SESSION_IN_MILLIS);
        return container;
    }

}