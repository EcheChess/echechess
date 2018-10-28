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

import ca.watier.echechess.communication.redis.pojos.ServerInfoPojo;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

@Configuration
@Profile("!standalone")
public class ServerConfiguration {

    private final Environment environment;
    private String rabbitIp;
    private Short rabbitPort;

    @Autowired
    public ServerConfiguration(Environment environment) {
        this.environment = environment;
        rabbitIp = environment.getProperty("node.rabbit.ip");
        rabbitPort = environment.getProperty("node.rabbit.port", Short.class);
    }

    @Bean
    public ServerInfoPojo redisServerPojo() {
        String ip = environment.getProperty("node.redis.ip");
        Short port = environment.getProperty("node.redis.port", Short.class);

        return new ServerInfoPojo(ip, port);
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
        factory.setUsername(environment.getProperty("node.rabbit.user"));
        factory.setPassword(environment.getProperty("node.rabbit.password"));
        return factory;
    }
}
