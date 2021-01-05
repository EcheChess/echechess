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

import ca.watier.echechess.common.enums.Pieces;
import org.junit.jupiter.api.Test;

import static ca.watier.echechess.common.enums.Pieces.*;
import static ca.watier.echechess.common.enums.Side.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by yannick on 6/20/2017.
 */
public class PiecesTest {
    @Test
    public void getName() {
        assertEquals("Black King", B_KING.getName());
        assertEquals("White King", W_KING.getName());
    }

    @Test
    public void isKing() {
        assertTrue(Pieces.isKing(B_KING));
        assertTrue(Pieces.isKing(W_KING));
        assertFalse(Pieces.isKing(W_BISHOP));
        assertFalse(Pieces.isKing(B_BISHOP));
    }

    @Test
    public void isKnight() {
        assertTrue(Pieces.isKnight(B_KNIGHT));
        assertTrue(Pieces.isKnight(W_KNIGHT));
        assertFalse(Pieces.isKnight(W_BISHOP));
        assertFalse(Pieces.isKnight(B_BISHOP));
    }

    @Test
    public void getKingBySide() {
        assertEquals(B_KING, Pieces.getKingBySide(BLACK));
        assertEquals(W_KING, Pieces.getKingBySide(WHITE));
        assertNull(Pieces.getKingBySide(OBSERVER));
    }
}