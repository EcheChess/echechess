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

import org.junit.jupiter.api.Test;

import static ca.watier.echechess.common.enums.CasePosition.*;
import static ca.watier.echechess.common.enums.Ranks.*;
import static ca.watier.echechess.common.enums.Side.BLACK;
import static ca.watier.echechess.common.enums.Side.WHITE;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by yannick on 7/5/2017.
 */
public class RanksTest  {
    @Test
    public void getRankWhite() {
        assertEquals(ONE, getRank(A1, WHITE));
        assertEquals(TWO, getRank(B2, WHITE));
        assertEquals(THREE, getRank(C3, WHITE));
        assertEquals(FOUR, getRank(D4, WHITE));
        assertEquals(FIVE, getRank(E5, WHITE));
        assertEquals(SIX, getRank(F6, WHITE));
        assertEquals(SEVEN, getRank(G7, WHITE));
        assertEquals(EIGHT, getRank(H8, WHITE));
    }

    @Test
    public void getRankBlack() {
        assertEquals(ONE, getRank(H8, BLACK));
        assertEquals(TWO, getRank(G7, BLACK));
        assertEquals(THREE, getRank(F6, BLACK));
        assertEquals(FOUR, getRank(E5, BLACK));
        assertEquals(FIVE, getRank(D4, BLACK));
        assertEquals(SIX, getRank(C3, BLACK));
        assertEquals(SEVEN, getRank(B2, BLACK));
        assertEquals(EIGHT, getRank(A1, BLACK));
    }
}