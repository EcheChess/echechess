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

import ca.watier.echechess.models.UserCredentials;
import org.springframework.stereotype.Repository;

@Repository
public class StandaloneUserRepositoryImpl implements UserRepository {

    @Override
    public UserCredentials getUserByName(String username) {
        return new UserCredentials(0, "admin", "$2a$16$hCYBiKUZ1tKGOJ9WMKSlieczcnHpX6pl5TpSa886XBcxtIC8DJcPG", "ADMIN");
    }
}
