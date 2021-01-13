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

package ca.watier.echechess.configuration.mode.dependent;

import ca.watier.echechess.communication.redis.pojos.ServerInfoPojo;
import ca.watier.echechess.repositories.DependentUserRepositoryImpl;
import ca.watier.echechess.repositories.UserRepository;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("dependent-mode")
public class DependentModeConfiguration {

    private final String rabbitIp;
    private final Short rabbitPort;
    private final String redisIp;
    private final Short redisPort;
    private final String rabbitUser;
    private final String rabbitPassword;

    @Autowired
    public DependentModeConfiguration(Environment environment) {
        rabbitIp = environment.getRequiredProperty("node.rabbit.ip");
        rabbitPort = environment.getRequiredProperty("node.rabbit.port", Short.class);
        redisIp = environment.getRequiredProperty("node.redis.ip");
        redisPort = environment.getRequiredProperty("node.redis.port", Short.class);
        rabbitUser = environment.getRequiredProperty("node.rabbit.user");
        rabbitPassword = environment.getRequiredProperty("node.rabbit.password");
    }

    @Bean
    public UserRepository userRepository(PasswordEncoder passwordEncoder) {
        //TODO: Implements a new transactional database repository and find a proper database (PostgreSQL ?) to store the settings.
        return new DependentUserRepositoryImpl(passwordEncoder);
    }

    @Bean
    public ServerInfoPojo redisServerPojo() {
        return new ServerInfoPojo(redisIp, redisPort);
    }

    @Bean
    public ServerInfoPojo rabbitMqServerPojo() {
        return new ServerInfoPojo(rabbitIp, rabbitPort);
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(rabbitIp);
        factory.setPort(rabbitPort);
        factory.setUsername(rabbitUser);
        factory.setPassword(rabbitPassword);
        return factory;
    }
}
