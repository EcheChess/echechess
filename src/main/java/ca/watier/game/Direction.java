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

package ca.watier.game;

import ca.watier.defassert.Assert;
import ca.watier.enums.CasePosition;

/**
 * Created by yannick on 4/23/2017.
 */

/**
 * N
 * W-E
 * S
 * 1 2
 * 3 4
 */

public enum Direction {
    NORTH, SOUTH, WEST, EAST, NORTH_WEST, NORTH_EAST, SOUTH_WEST, SOUTH_EAST, NONE;

    public static Direction getDirectionFromPosition(CasePosition from, CasePosition to) {
        Assert.assertNotNull(from, to);

        if (from == to) { //Same
            return NONE;
        }

        Direction direction = null;

        int xTo = to.getX();
        int xFrom = from.getX();
        int yTo = to.getY();
        int yFrom = from.getY();

        if (xTo == xFrom) { //North or South
            if (yTo > yFrom) {
                direction = NORTH;
            } else {
                direction = SOUTH;
            }
        } else if (yTo == yFrom) { //East or West
            if (xTo > xFrom) {
                direction = EAST;
            } else {
                direction = WEST;
            }
        } else if (xTo < xFrom && yTo < yFrom) {
            direction = SOUTH_WEST;
        } else if (xTo < xFrom && yTo > yFrom) {
            direction = NORTH_WEST;
        } else if (xTo > xFrom && yTo < yFrom) {
            direction = SOUTH_EAST;
        } else if (xTo > xFrom && yTo > yFrom) {
            direction = NORTH_EAST;
        }

        return direction;
    }
}
