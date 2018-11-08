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
import ca.watier.echechess.models.UserCredentials;
import ca.watier.echechess.models.UserDetailsImpl;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
@Validated
public interface UserRepository {
    void addNewUserWithRole(@NotNull User user, @NotNull Roles role) throws UserException;

    UserCredentials getUserByName(@NotBlank String username) throws UserException;

    List<UserCredentials> getUserByEmail(@NotBlank String email) throws UserException;

    void addNewUser(@NotNull User user) throws UserException;

    void updateUser(@NotNull User user, @NotNull UserDetailsImpl userDetails) throws UserException;
}
