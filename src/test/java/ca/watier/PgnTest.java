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

package ca.watier;

import ca.watier.game.GenericGameHandler;
import ca.watier.utils.PgnParser;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;

public class PgnTest extends GameTest {

    @Test
    public void pgnTest1() throws IOException {
        GenericGameHandler gameHandler = new GenericGameHandler(CONSTRAINT_SERVICE, WEB_SOCKET_SERVICE);
        PgnParser pgnParser = new PgnParser(gameHandler);

        String gamesAsFile = IOUtils.toString(PgnTest.class.getResourceAsStream("/puzzle.pgn"), Charset.forName("UTF-8"));
        System.out.println(pgnParser.parse(gamesAsFile));
    }
}
