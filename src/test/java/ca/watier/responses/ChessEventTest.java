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

package ca.watier.responses;

import ca.watier.enums.ChessEventMessage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static ca.watier.enums.ChessEventMessage.GAME_WON;
import static org.junit.Assert.*;

/**
 * Created by yannick on 6/20/2017.
 */
public class ChessEventTest {

    public static final String GAME_WON = "game won";
    private ChessEvent chessEvent;

    @Before
    public void setUp() throws Exception {
        chessEvent = new ChessEvent(ChessEventMessage.GAME_WON, GAME_WON);

    }

    @Test
    public void getEvent() throws Exception {
        Assert.assertEquals(ChessEventMessage.GAME_WON, chessEvent.getEvent());
    }

    @Test
    public void getMessage() throws Exception {
        Assert.assertEquals(GAME_WON, chessEvent.getMessage());
    }

}