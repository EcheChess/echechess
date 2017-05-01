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

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by yannick on 4/22/2017.
 */

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CasePosition {
    A8(-4, 4), B8(-3, 4), C8(-2, 4), D8(-1, 4), E8(0, 4), F8(1, 4), G8(2, 4), H8(3, 4),
    A7(-4, 3), B7(-3, 3), C7(-2, 3), D7(-1, 3), E7(0, 3), F7(1, 3), G7(2, 3), H7(3, 3),
    A6(-4, 2), B6(-3, 2), C6(-2, 2), D6(-1, 2), E6(0, 2), F6(1, 2), G6(2, 2), H6(3, 2),
    A5(-4, 1), B5(-3, 1), C5(-2, 1), D5(-1, 1), E5(0, 1), F5(1, 1), G5(2, 1), H5(3, 1),
    A4(-4, 0), B4(-3, 0), C4(-2, 0), D4(-1, 0), E4(0, 0), F4(1, 0), G4(2, 0), H4(3, 0),
    A3(-4, -1), B3(-3, -1), C3(-2, -1), D3(-1, -1), E3(0, -1), F3(1, -1), G3(2, -1), H3(3, -1),
    A2(-4, -2), B2(-3, -2), C2(-2, -2), D2(-1, -2), E2(0, -2), F2(1, -2), G2(2, -2), H2(3, -2),
    A1(-4, -3), B1(-3, -3), C1(-2, -3), D1(-1, -3), E1(0, -3), F1(1, -3), G1(2, -3), H1(3, -3);

    private final int x, y;

    CasePosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public static CasePosition getCasePositionByCoor(int x, int y) {
        CasePosition position = null;

        for (CasePosition casePosition : values()) {
            if (x == casePosition.getX() && y == casePosition.getY()) {
                position = casePosition;
                break;
            }
        }

        return position;
    }

}
