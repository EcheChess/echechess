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

import ca.watier.echechess.clients.MessageClient;
import ca.watier.echechess.common.interfaces.WebSocketService;
import ca.watier.echechess.communication.redis.interfaces.GameRepository;
import ca.watier.echechess.communication.redis.model.GenericGameHandlerWrapper;
import ca.watier.echechess.components.MessageActionExecutor;
import ca.watier.echechess.components.StandaloneMessageHandler;
import ca.watier.echechess.engine.engines.GenericGameHandler;
import ca.watier.echechess.exceptions.UserException;
import ca.watier.echechess.models.Roles;
import ca.watier.echechess.models.User;
import ca.watier.echechess.repositories.StandaloneUserRepositoryImpl;
import ca.watier.echechess.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Profile("standalone")
public class StandaloneConfiguration {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(StandaloneConfiguration.class);

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public StandaloneConfiguration(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public GameRepository<GenericGameHandler> gameRepository() {
        return new GameRepository<>() {
            private final Map<String, GenericGameHandlerWrapper<GenericGameHandler>> games = new HashMap<>();

            @Override
            public void add(GenericGameHandlerWrapper<GenericGameHandler> genericGameHandlerWrapper) {
                addGame(genericGameHandlerWrapper.getId(), genericGameHandlerWrapper);
            }

            private void addGame(String id, GenericGameHandlerWrapper<GenericGameHandler> genericGameHandlerWrapper) {
                LOGGER.info("Added new game with id {}", genericGameHandlerWrapper.getId());
                games.put(id, genericGameHandlerWrapper);
            }

            @Override
            public void add(String id, GenericGameHandler genericGameHandler) {
                addGame(id, new GenericGameHandlerWrapper<>(genericGameHandler));
            }

            @Override
            public void delete(String id) {
                games.remove(id);
            }

            @Override
            public GenericGameHandlerWrapper<GenericGameHandler> get(String id) {
                return games.get(id);
            }

            @Override
            public List<GenericGameHandlerWrapper<GenericGameHandler>> getAll() {
                return new ArrayList<>(games.values());
            }
        };
    }

    @Bean
    public MessageClient rabbitStandaloneClient(MessageActionExecutor actionExecutor, GameRepository<GenericGameHandler> gameRepository, ObjectMapper objectMapper) {
        return new MessageClient(new StandaloneMessageHandler(actionExecutor, gameRepository, objectMapper));
    }

    @Bean
    public MessageActionExecutor messageActionExecutor(GameRepository<GenericGameHandler> gameRepository,
                                                       WebSocketService webSocketService,
                                                       ObjectMapper objectMapper) {
        return new MessageActionExecutor(gameRepository, webSocketService, objectMapper);
    }

    @Bean
    public UserRepository userRepository() {
        StandaloneUserRepositoryImpl standaloneUserRepositoryImpl = new StandaloneUserRepositoryImpl(passwordEncoder);

        try {
            standaloneUserRepositoryImpl.addNewUserWithRole(new User("admin", "admin", "adminEmail"), Roles.ADMIN);
        } catch (UserException e) {
            e.printStackTrace();
            LOGGER.error("Unable to create the default admin user!", e);
        }

        return standaloneUserRepositoryImpl;
    }
}
