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

package ca.watier.echechess.configuration.independent;

import ca.watier.echechess.exceptions.UserException;
import ca.watier.echechess.models.Roles;
import ca.watier.echechess.models.User;
import ca.watier.echechess.repositories.UserRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class DevConfiguration {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(DevConfiguration.class);

    @Autowired
    public DevConfiguration(UserRepository userRepository) {
        try {
            User admin = new User("admin", "admin", "adminEmail");
            User adminTwo = new User("admin2", "admin2", "adminEmail2");

            userRepository.addNewUserWithRole(admin, Roles.ADMIN);
            userRepository.addNewUserWithRole(adminTwo, Roles.ADMIN);
        } catch (UserException e) {
            LOGGER.error("Unable to create the default admin user!", e);
        }
    }
}
