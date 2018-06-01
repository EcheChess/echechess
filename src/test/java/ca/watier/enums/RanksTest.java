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

package ca.watier.enums;

import ca.watier.echechess.common.tests.GameTest;
import org.junit.Assert;
import org.junit.Test;

import static ca.watier.echechess.common.enums.CasePosition.*;
import static ca.watier.echechess.common.enums.Ranks.*;

/**
 * Created by yannick on 7/5/2017.
 */
public class RanksTest extends GameTest {
    @Test
    public void getRankWhite() {
        Assert.assertEquals(ONE, getRank(A1, WHITE));
        Assert.assertEquals(TWO, getRank(B2, WHITE));
        Assert.assertEquals(THREE, getRank(C3, WHITE));
        Assert.assertEquals(FOUR, getRank(D4, WHITE));
        Assert.assertEquals(FIVE, getRank(E5, WHITE));
        Assert.assertEquals(SIX, getRank(F6, WHITE));
        Assert.assertEquals(SEVEN, getRank(G7, WHITE));
        Assert.assertEquals(EIGHT, getRank(H8, WHITE));
    }

    @Test
    public void getRankBlack() {
        Assert.assertEquals(ONE, getRank(H8, BLACK));
        Assert.assertEquals(TWO, getRank(G7, BLACK));
        Assert.assertEquals(THREE, getRank(F6, BLACK));
        Assert.assertEquals(FOUR, getRank(E5, BLACK));
        Assert.assertEquals(FIVE, getRank(D4, BLACK));
        Assert.assertEquals(SIX, getRank(C3, BLACK));
        Assert.assertEquals(SEVEN, getRank(B2, BLACK));
        Assert.assertEquals(EIGHT, getRank(A1, BLACK));
    }

}