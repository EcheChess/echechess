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

package ca.watier.echechess.repositories;

import ca.watier.echechess.exceptions.UserException;
import ca.watier.echechess.models.Roles;
import ca.watier.echechess.models.User;
import ca.watier.echechess.models.UserInformation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(MockitoJUnitRunner.class)
public class IndependentUserRepositoryImplTest {

    @Spy
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(4);

    @InjectMocks
    private IndependentUserRepositoryImpl userRepository;

    @Test
    public void repoTest() {
        // given
        String givenEmail = "email";
        String givenUsername = "admin";
        String givenPassword = "adminPwd";
        Roles givenRole = Roles.ADMIN;

        // then & when
        try {
            userRepository.addNewUserWithRole(new User(givenUsername, givenPassword, givenEmail), givenRole);
            UserInformation userInformation = userRepository.getUserByName(givenUsername);
            assertThat(userInformation).isNotNull();
            assertThat(userInformation.getName()).isNotBlank().isEqualTo(givenUsername);
            assertThat(userInformation.getHash()).isNotBlank().startsWith("$2a$04$").hasSize(60);
            assertThat(userInformation.getRole()).isNotNull().isEqualByComparingTo(givenRole);
        } catch (UserException e) {
            fail(e.getMessage(), e);
        }
    }
}