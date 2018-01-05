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

import ca.watier.enums.CasePosition;
import ca.watier.enums.Direction;
import ca.watier.enums.Side;

import static ca.watier.enums.CasePosition.*;

public class CastlingPositionHelper {
    private CasePosition from;
    private CasePosition to;
    private Side playerSide;
    private CasePosition kingPosition;
    private CasePosition rookPosition;

    public CastlingPositionHelper(CasePosition from, CasePosition to, Side playerSide) {
        this.from = from;
        this.to = to;
        this.playerSide = playerSide;
    }

    public CasePosition getKingPosition() {
        return kingPosition;
    }

    public CasePosition getRookPosition() {
        return rookPosition;
    }

    public CastlingPositionHelper invoke() {
        boolean isQueenSide = isQueenSide(from, to);

        switch (playerSide) {
            case BLACK:
                kingPosition = (isQueenSide ? C8 : G8);
                rookPosition = (isQueenSide ? D8 : F8);
                break;
            case WHITE:
                kingPosition = (isQueenSide ? C1 : G1);
                rookPosition = (isQueenSide ? D1 : F1);
                break;
            case OBSERVER:
            default:
                break;
        }
        return this;
    }

    public static boolean isQueenSide(CasePosition from, CasePosition to) {
        return Direction.WEST.equals(MathUtils.getDirectionFromPosition(from, to));
    }
}