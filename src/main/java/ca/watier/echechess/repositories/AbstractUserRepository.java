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
import ca.watier.echechess.models.UserDetailsImpl;
import ca.watier.echechess.models.UserInformation;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Validated
public abstract class AbstractUserRepository implements UserRepository {
    private final PasswordEncoder passwordEncoder;

    public AbstractUserRepository(@NotNull PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public final void addNewUserWithRole(@Valid @NotNull User user, @NotNull Roles role) throws UserException {
        saveOrUpdateUserIfNotExist(user, role);
    }

    @Override
    public void addNewUser(@Valid @NotNull User user) throws UserException {
        saveOrUpdateUserIfNotExist(user);
    }

    /**
     * TODO: Implements the email change feature
     *
     * @param user
     * @param userDetails
     */
    @Override
    public void updateUser(@Valid @NotNull User user, @NotNull UserDetailsImpl userDetails) {
        throw new UnsupportedOperationException();
    }

    private void saveOrUpdateUserIfNotExist(@Valid @NotNull User user, Roles... role) throws UserException {
        if (!isUserExist(user.getName())) {
            UserInformation userInformation;

            if (ArrayUtils.isEmpty(role)) {
                userInformation = createUserCredentialsFromUser(user);
            } else {
                //TODO: Support multiples roles
                userInformation = createUserCredentialsFromUser(user).withRole(role[0]);
            }

            saveOrUpdateUserInformation(userInformation);
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

    private UserInformation createUserCredentialsFromUser(@Valid @NotNull User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        return new UserInformation(user.getName(), encodedPassword, user.getEmail());
    }

    protected abstract void saveOrUpdateUserInformation(@NotNull UserInformation userInformation);
}
