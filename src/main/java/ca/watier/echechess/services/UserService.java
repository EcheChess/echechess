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

package ca.watier.echechess.services;

import ca.watier.echechess.exceptions.UserException;
import ca.watier.echechess.models.User;
import ca.watier.echechess.models.UserDetailsImpl;
import ca.watier.echechess.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Validated
@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addNewUser(@NotNull User user) throws UserException {
        userRepository.addNewUser(user);
    }

    public void updateUser(@Valid @NotNull User user, @NotNull UserDetailsImpl principal) throws UserException {
        userRepository.updateUser(user, principal);
    }

    public void addGameToUser(@NotEmpty String username, @NotNull UUID game) throws UserException {
        userRepository.addGameToUser(username, game);
    }
}
