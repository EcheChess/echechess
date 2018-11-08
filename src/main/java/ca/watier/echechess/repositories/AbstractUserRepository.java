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
import ca.watier.echechess.models.Roles;
import ca.watier.echechess.models.User;
import ca.watier.echechess.models.UserCredentials;
import ca.watier.echechess.models.UserDetailsImpl;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Validated
public abstract class AbstractUserRepository implements UserRepository {
    private final PasswordEncoder passwordEncoder;

    public AbstractUserRepository(@NotNull PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public final void addNewUserWithRole(@NotNull User user, @NotNull Roles role) throws UserException {
        saveOrUpdateUserIfNotExist(user, role);
    }

    @Override
    public void addNewUser(@NotNull User user) throws UserException {
        saveOrUpdateUserIfNotExist(user);
    }

    /**
     * TODO: Implements the email change feature
     *
     * @param user
     * @param userDetails
     * @throws UserException
     */
    @Override
    public void updateUser(@NotNull User user, @NotNull UserDetailsImpl userDetails) throws UserException {
        throw new UnsupportedOperationException();
    }

    private void saveOrUpdateUserIfNotExist(@NotNull User user, Roles... role) throws UserException {
        if (!isUserExist(user.getName())) {
            UserCredentials userCredentials;
            if (ArrayUtils.isEmpty(role)) {
                userCredentials = createUserCredentialsFromUser(user);
            } else {
                //TODO: Support multiples roles
                userCredentials = createUserCredentialsFromUser(user).withRole(role[0]);
            }

            saveOrUpdateUserCredentials(userCredentials);
        } else {
            throw new UserAlreadyExistException();
        }
    }

    protected boolean isUserExist(@NotNull String username) {
        try {
            getUserByName(username);
            return true;
        } catch (UserException e) {
            return false;
        }
    }

    private UserCredentials createUserCredentialsFromUser(@NotNull User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        return new UserCredentials(user.getName(), encodedPassword, user.getEmail());
    }

    protected abstract void saveOrUpdateUserCredentials(@NotNull UserCredentials userCredentials);
}
