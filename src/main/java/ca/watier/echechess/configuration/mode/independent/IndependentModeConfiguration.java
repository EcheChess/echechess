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

package ca.watier.echechess.configuration.mode.independent;

import ca.watier.echechess.communication.redis.interfaces.GameRepository;
import ca.watier.echechess.engine.engines.GenericGameHandler;
import ca.watier.echechess.repositories.IndependentGameRepositoryImpl;
import ca.watier.echechess.repositories.IndependentUserRepositoryImpl;
import ca.watier.echechess.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("independent-mode")
public class IndependentModeConfiguration {
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public IndependentModeConfiguration(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public GameRepository<GenericGameHandler> gameRepository() {
        return new IndependentGameRepositoryImpl();
    }

    @Bean
    public UserRepository userRepository() {
        return new IndependentUserRepositoryImpl(passwordEncoder);
    }
}
