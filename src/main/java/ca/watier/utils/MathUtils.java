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

import ca.watier.defassert.Assert;
import ca.watier.enums.CasePosition;
import ca.watier.game.Direction;

import static ca.watier.enums.CasePosition.getCasePositionByCoor;
import static ca.watier.game.Direction.*;

/**
 * Created by yannick on 4/25/2017.
 */
public class MathUtils {

    /**
     * Get the direction based on a position
     *
     * @param from
     * @param to
     * @return
     */
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

    /**
     * Get the nearest case based on the direction
     *
     * @param casePosition
     * @param direction
     * @return
     */
    public static CasePosition getNearestPositionFromDirection(CasePosition casePosition, Direction direction) {
        Assert.assertNotNull(casePosition, direction);

        CasePosition position = null;

        int x = casePosition.getX();
        int y = casePosition.getY();

        switch (direction) {
            case NORTH:
                position = getCasePositionByCoor(x, y + 1);
                break;
            case NORTH_EAST:
                position = getCasePositionByCoor(x + 1, y + 1);
                break;
            case NORTH_WEST:
                position = getCasePositionByCoor(x - 1, y + 1);
                break;
            case SOUTH:
                position = getCasePositionByCoor(x, y - 1);
                break;
            case SOUTH_EAST:
                position = getCasePositionByCoor(x + 1, y - 1);
                break;
            case SOUTH_WEST:
                position = getCasePositionByCoor(x - 1, y - 1);
                break;
            case EAST:
                position = getCasePositionByCoor(x + 1, y);
                break;
            case WEST:
                position = getCasePositionByCoor(x - 1, y);
                break;
        }

        return position;
    }

    /**
     * Get the slope based on the CasePosition
     *
     * @param from
     * @param to
     * @return
     */
    public static float getSlopeFromPosition(CasePosition from, CasePosition to) {
        Assert.assertNotNull(from, to);

        float xDiff = from.getX() - to.getX();
        float yDiff = from.getY() - to.getY();

        return (xDiff != 0) ? yDiff / xDiff : 0;
    }

    /**
     * Check if the position is on the same line than the others
     *
     * @param first
     * @param second
     * @param toCheck
     * @return
     */
    public static boolean isPositionInLine(CasePosition first, CasePosition second, CasePosition toCheck) {
        Assert.assertNotNull(first, second, toCheck);

        int yCurrent = first.getY();
        int xCurrent = first.getX();
        int yToCheck = toCheck.getY();
        int xToCheck = toCheck.getX();

        float m = getSlopeFromPosition(first, second);
        float b = yCurrent - (m * xCurrent);

        return yToCheck == (m * xToCheck + b);
    }
}
