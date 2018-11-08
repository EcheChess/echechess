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
import ca.watier.echechess.models.UserCredentials;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StandaloneUserRepositoryImpl extends AbstractUserRepository {
    protected final Map<String, UserCredentials> users = new HashMap<>();

    public StandaloneUserRepositoryImpl(PasswordEncoder passwordEncoder) {
        super(passwordEncoder);
    }

    @Override
    protected void saveOrUpdateUserCredentials(UserCredentials userCredentials) {
        users.put(userCredentials.getName(), userCredentials);
    }

    @Override
    public UserCredentials getUserByName(String username) throws UserException {
        return Optional.ofNullable(users.get(username)).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public List<UserCredentials> getUserByEmail(@NotBlank String email) throws UserException {
        Predicate<UserCredentials> userCredentialsPredicate = userCredentials -> email.equals(userCredentials.getEmail());

        List<UserCredentials> values = users.values()
                .parallelStream()
                .filter(userCredentialsPredicate)
                .collect(Collectors.toList());

        if (values.isEmpty()) {
            throw new UserNotFoundException();
        }

        return values;
    }
}
