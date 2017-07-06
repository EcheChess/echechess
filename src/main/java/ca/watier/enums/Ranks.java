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

import ca.watier.utils.Assert;

/**
 * Created by yannick on 7/5/2017.
 */
public enum Ranks {
    ONE((byte) -3, (byte) 4),
    TWO((byte) -2, (byte) 3),
    THREE((byte) -1, (byte) 2),
    FOUR((byte) 0, (byte) 1),
    FIVE((byte) 1, (byte) 0),
    SIX((byte) 2, (byte) -1),
    SEVEN((byte) 3, (byte) -2),
    EIGHT((byte) 4, (byte) -3);

    //Represent the Y coordinate on the board
    private byte white;
    private byte black;

    Ranks(byte white, byte black) {
        this.white = white;
        this.black = black;
    }

    public static Ranks getRank(CasePosition position, Side side) {
        Assert.assertNotNull(position, side);

        if (Side.OBSERVER.equals(side)) {
            return null;
        }

        Ranks value = null;

        for (Ranks rank : values()) {
            int y = position.getY();
            if (Side.WHITE.equals(side) && rank.getWhitePosition() == y || Side.BLACK.equals(side) && rank.getBlackPosition() == y) {
                value = rank;
                break;
            }
        }

        return value;
    }

    public byte getWhitePosition() {
        return white;
    }

    public byte getBlackPosition() {
        return black;
    }
}


