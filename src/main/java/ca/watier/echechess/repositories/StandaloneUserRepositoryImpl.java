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

import ca.watier.echechess.exceptions.UserAlreadyExistException;
import ca.watier.echechess.exceptions.UserException;
import ca.watier.echechess.exceptions.UserNotFoundException;
import ca.watier.echechess.models.Roles;
import ca.watier.echechess.models.UserCredentials;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Repository
public class StandaloneUserRepositoryImpl extends UserRepository {
    protected final Set<UserCredentials> users = new HashSet<>();

    private final PasswordEncoder passwordEncoder;

    public StandaloneUserRepositoryImpl(PasswordEncoder passwordEncoder) {
        super(passwordEncoder);
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void save(UserCredentials userCredentials) throws UserException {
        if (!users.add(userCredentials)) {
            throw new UserAlreadyExistException();
        }
    }

    @Override
    public UserCredentials getUserByName(String username) throws UserException {
        return users.stream()
                .filter(userCredentials -> username.equals(userCredentials.getName()))
                .findFirst()
                .orElseThrow(UserNotFoundException::new);
    }
}
