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
import ca.watier.echechess.models.UserCredentials;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Validated
public abstract class UserRepository {
    private final PasswordEncoder passwordEncoder;

    public UserRepository(@NotNull PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public final void addNewUser(@NotBlank String username, @NotBlank String password) throws UserException {
        save(createUser(username, password));
    }

    protected abstract void save(@NotNull UserCredentials userCredentials) throws UserException;

    private UserCredentials createUser(@NotBlank String username, @NotBlank String password) {
        String encodedPassword = passwordEncoder.encode(password);
        return new UserCredentials(username, encodedPassword);
    }

    public final void addNewUserWithRole(@NotBlank String username, @NotBlank String password, @NotNull Roles role) throws UserException {
        save(createUser(username, password).withRole(role));
    }

    public abstract UserCredentials getUserByName(@NotBlank String username) throws UserException;
}
