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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yannick on 4/22/2017.
 */

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CasePosition {
    A8(-4, 4, 'a', 8), B8(-3, 4, 'b', 8), C8(-2, 4, 'c', 8), D8(-1, 4, 'd', 8), E8(0, 4, 'e', 8), F8(1, 4, 'f', 8), G8(2, 4, 'g', 8), H8(3, 4, 'h', 8),
    A7(-4, 3, 'a', 7), B7(-3, 3, 'b', 7), C7(-2, 3, 'c', 7), D7(-1, 3, 'd', 7), E7(0, 3, 'e', 7), F7(1, 3, 'f', 7), G7(2, 3, 'g', 7), H7(3, 3, 'h', 7),
    A6(-4, 2, 'a', 6), B6(-3, 2, 'b', 6), C6(-2, 2, 'c', 6), D6(-1, 2, 'd', 6), E6(0, 2, 'e', 6), F6(1, 2, 'f', 6), G6(2, 2, 'g', 6), H6(3, 2, 'h', 6),
    A5(-4, 1, 'a', 5), B5(-3, 1, 'b', 5), C5(-2, 1, 'c', 5), D5(-1, 1, 'd', 5), E5(0, 1, 'e', 5), F5(1, 1, 'f', 5), G5(2, 1, 'g', 5), H5(3, 1, 'h', 5),
    A4(-4, 0, 'a', 4), B4(-3, 0, 'b', 4), C4(-2, 0, 'c', 4), D4(-1, 0, 'd', 4), E4(0, 0, 'e', 4), F4(1, 0, 'f', 4), G4(2, 0, 'g', 4), H4(3, 0, 'h', 4),
    A3(-4, -1, 'a', 3), B3(-3, -1, 'b', 3), C3(-2, -1, 'c', 3), D3(-1, -1, 'd', 3), E3(0, -1, 'e', 3), F3(1, -1, 'f', 3), G3(2, -1, 'g', 3), H3(3, -1, 'h', 3),
    A2(-4, -2, 'a', 2), B2(-3, -2, 'b', 2), C2(-2, -2, 'c', 2), D2(-1, -2, 'd', 2), E2(0, -2, 'e', 2), F2(1, -2, 'f', 2), G2(2, -2, 'g', 2), H2(3, -2, 'h', 2),
    A1(-4, -3, 'a', 1), B1(-3, -3, 'b', 1), C1(-2, -3, 'c', 1), D1(-1, -3, 'd', 1), E1(0, -3, 'e', 1), F1(1, -3, 'f', 1), G1(2, -3, 'g', 1), H1(3, -3, 'h', 1);

    private final int x, y, row;
    private final char col;

    CasePosition(int x, int y, char col, int row) {
        this.x = x;
        this.y = y;
        this.col = col;
        this.row = row;
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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public static List<CasePosition> getAllPositionaFromColumn(char value) {
        value = Character.toLowerCase(value);
        List<CasePosition> values = new ArrayList<>();

        if (value >= 'a' && value <= 'h') {
            for (CasePosition casePosition : CasePosition.values()) {
                if (casePosition.getCol() == value) {
                    values.add(casePosition);
                }
            }
        }

        return values;
    }

    public char getCol() {
        return col;
    }

    public static List<CasePosition> getAllPositionsFromRow(int value) {
        value = Character.toLowerCase(value);
        List<CasePosition> values = new ArrayList<>();

        if (value >= 1 && value <= 8) {
            for (CasePosition casePosition : CasePosition.values()) {
                if (casePosition.getRow() == value) {
                    values.add(casePosition);
                }
            }
        }

        return values;
    }

    public boolean isOnSameColumn(char value) {
        return this.col == value;
    }

    public boolean isOnSameRow(int value) {
        return this.row == value;
    }

    public int getRow() {
        return row;
    }
}
