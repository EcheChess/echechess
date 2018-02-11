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

package ca.watier.utils;

import ca.watier.echesscommon.sessions.Player;
import ca.watier.echesscommon.utils.Constants;
import ca.watier.echesscommon.utils.SessionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpSession;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Created by yannick on 6/20/2017.
 */
public class SessionUtilsTest {
    @Test
    public void getPlayer() {
        HttpSession httpSessionWithoutPlayer = new MockHttpSession();
        HttpSession httpSessionWithPlayer = new MockHttpSession();
        httpSessionWithPlayer.setAttribute(Constants.PLAYER, new Player());

        Assert.assertNotNull(SessionUtils.getPlayer(httpSessionWithPlayer));

        assertThatExceptionOfType(AssertionError.class).isThrownBy(() ->
                SessionUtils.getPlayer(httpSessionWithoutPlayer)
        ).withMessage("The object cannot be null !");
    }
}