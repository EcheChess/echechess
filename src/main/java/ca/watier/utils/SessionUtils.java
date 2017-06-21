/*
 *    Copyright 2014 - 2017 Yannick Watier
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

package ca.watier.utils;

import ca.watier.sessions.Player;

import javax.servlet.http.HttpSession;

/**
 * Created by yannick on 4/23/2017.
 */
public class SessionUtils implements BaseUtils {

    private SessionUtils() {
    }

    /**
     * Fetch the player from the HttpSession, the session cannot be null, same for the player
     *
     * @param session
     * @return
     */
    public static Player getPlayer(HttpSession session) {

        Assert.assertNotNull(session);
        Player player = (Player) session.getAttribute(Constants.PLAYER);
        Assert.assertNotNull(player);

        return player;
    }
}
