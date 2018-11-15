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

package ca.watier.echechess.components;

import ca.watier.echechess.exceptions.UserException;
import ca.watier.echechess.models.UserDetailsImpl;
import ca.watier.echechess.models.UserInformation;
import ca.watier.echechess.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

public class UserDetailsServiceImpl implements UserDetailsService {
    private final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        logger.info("Fetching the user -> " + username);

        UserInformation userInformation;
        try {
            userInformation = userRepository.getUserByName(username);
        } catch (UserException userException) {
            String message = String.format("Unable to find user %s", username);
            logger.warn(message);

            throw new UsernameNotFoundException(message, userException);
        }

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(userInformation.getRoleAsString()));

        return new UserDetailsImpl(userInformation.getId(), userInformation.getName(), userInformation.getHash(), userInformation.getEmail(), true, true, true, true, authorities);
    }
}