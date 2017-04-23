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

/**
 * Created by yannick on 4/22/2017.
 */
public enum CasePosition {
    A8(-4, 4), B8(-3, 4), C8(-2, 4), D8(-1, 4), E8(1, 4), F8(2, 4), G8(3, 4), H8(4, 4),
    A7(-4, 3), B7(-3, 3), C7(-2, 3), D7(-1, 3), E7(1, 3), F7(2, 3), G7(3, 3), H7(4, 3),
    A6(-4, 2), B6(-3, 2), C6(-2, 2), D6(-1, 2), E6(1, 2), F6(2, 2), G6(3, 2), H6(4, 2),
    A5(-4, 1), B5(-3, 1), C5(-2, 1), D5(-1, 1), E5(1, 1), F5(2, 1), G5(3, 1), H5(4, 1),
    A4(-4, -1), B4(-3, -1), C4(-2, -1), D4(-1, -1), E4(1, -1), F4(2, -1), G4(3, -1), H4(4, -1),
    A3(-4, -2), B3(-3, -2), C3(-2, -2), D3(-1, -2), E3(1, -2), F3(2, -2), G3(3, -2), H3(4, -2),
    A2(-4, -3), B2(-3, -3), C2(-2, -3), D2(-1, -3), E2(1, -3), F2(2, -3), G2(3, -3), H2(4, -3),
    A1(-4, -4), B1(-3, -4), C1(-2, -4), D1(-1, -4), E1(1, -4), F1(2, -4), G1(3, -4), H1(4, -4);

    private int x, y;

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
