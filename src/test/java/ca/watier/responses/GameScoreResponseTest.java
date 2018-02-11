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

import ca.watier.echechessengine.responses.GameScoreResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by yannick on 6/20/2017.
 */
public class GameScoreResponseTest {

    public static final short WHITE_PLAYER_POINT = (short) 10;
    public static final short BLACK_PLAYER_POINT = (short) 258;
    private GameScoreResponse gameScoreResponse;

    @Before
    public void setUp() {
        gameScoreResponse = new GameScoreResponse(WHITE_PLAYER_POINT, BLACK_PLAYER_POINT);
    }

    @Test
    public void getWhitePlayerPoint() {
        Assert.assertEquals(WHITE_PLAYER_POINT, gameScoreResponse.getWhitePlayerPoint());
    }

    @Test
    public void getBlackPlayerPoint() {
        Assert.assertEquals(BLACK_PLAYER_POINT, gameScoreResponse.getBlackPlayerPoint());
    }
}