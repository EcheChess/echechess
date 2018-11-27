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
import ca.watier.echechess.exceptions.UserNotFoundException;
import ca.watier.echechess.models.UserInformation;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class IndependentUserRepositoryImpl extends AbstractUserRepository {
    protected final Map<String, UserInformation> users = new HashMap<>();

    public IndependentUserRepositoryImpl(PasswordEncoder passwordEncoder) {
        super(passwordEncoder);
    }

    @Override
    protected void saveOrUpdateUserInformation(@NotNull UserInformation userInformation) {
        users.put(userInformation.getName(), userInformation);
    }

    @Override
    public UserInformation getUserByName(@NotBlank String username) throws UserException {
        return Optional.ofNullable(users.get(username)).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public List<UserInformation> getUserByEmail(@NotBlank @Email String email) throws UserException {
        Predicate<UserInformation> userCredentialsPredicate = userCredentials -> email.equals(userCredentials.getEmail());

        List<UserInformation> values = users.values()
                .parallelStream()
                .filter(userCredentialsPredicate)
                .collect(Collectors.toList());

        if (values.isEmpty()) {
            throw new UserNotFoundException();
        }

        return values;
    }

    @Override
    public void addGameToUser(@NotBlank String username, @NotNull UUID game) throws UserException {
        UserInformation userByName = getUserByName(username);
        userByName.addGame(game);

        saveOrUpdateUserInformation(userByName);
    }
}
