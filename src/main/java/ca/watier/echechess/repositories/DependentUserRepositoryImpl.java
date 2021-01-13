/*
 *    Copyright 2014 - 2021 Yannick Watier
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
import ca.watier.echechess.models.UserInformation;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class DependentUserRepositoryImpl extends AbstractUserRepository {
    public DependentUserRepositoryImpl(@NotNull PasswordEncoder passwordEncoder) {
        super(passwordEncoder);
    }

    @Override
    public UserInformation getUserByName(@NotBlank String username) throws UserException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<UserInformation> getUserByEmail(@NotBlank @Email String email) throws UserException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addGameToUser(@NotBlank String username, @NotNull UUID game) throws UserException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void saveOrUpdateUserInformation(@NotNull UserInformation userInformation) {
        throw new UnsupportedOperationException();
    }
}
