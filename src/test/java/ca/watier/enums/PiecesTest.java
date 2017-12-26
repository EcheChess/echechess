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

package ca.watier.enums;

import org.junit.Assert;
import org.junit.Test;

import static ca.watier.enums.Pieces.*;
import static ca.watier.enums.Side.*;

/**
 * Created by yannick on 6/20/2017.
 */
public class PiecesTest {
    @Test
    public void getName() {
        Assert.assertEquals("Black King", B_KING.getName());
        Assert.assertEquals("White King", W_KING.getName());
    }

    @Test
    public void isKing() {
        Assert.assertTrue(Pieces.isKing(B_KING));
        Assert.assertTrue(Pieces.isKing(W_KING));
        Assert.assertFalse(Pieces.isKing(W_BISHOP));
        Assert.assertFalse(Pieces.isKing(B_BISHOP));
    }

    @Test
    public void isKnight() {
        Assert.assertTrue(Pieces.isKnight(B_KNIGHT));
        Assert.assertTrue(Pieces.isKnight(W_KNIGHT));
        Assert.assertFalse(Pieces.isKnight(W_BISHOP));
        Assert.assertFalse(Pieces.isKnight(B_BISHOP));
    }

    @Test
    public void getKingBySide() {
        Assert.assertEquals(B_KING, Pieces.getKingBySide(BLACK));
        Assert.assertEquals(W_KING, Pieces.getKingBySide(WHITE));
        Assert.assertNull(Pieces.getKingBySide(OBSERVER));
    }
}