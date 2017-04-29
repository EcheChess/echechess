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
import org.apache.commons.math3.util.Precision;

import static ca.watier.enums.CasePosition.getCasePositionByCoor;
import static ca.watier.game.Direction.*;

/**
 * Created by yannick on 4/25/2017.
 */
public class MathUtils extends BaseUtils {

    /**
     * Get the direction based on a position, this method is not precise when the depth is more than 1.
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
        return getNearestPositionFromDirection(casePosition, direction, 1);
    }

    /**
     * Get the case based on the direction
     *
     * @param casePosition
     * @param direction
     * @return
     */
    public static CasePosition getNearestPositionFromDirection(CasePosition casePosition, Direction direction, int nbOfCases) {
        Assert.assertNotNull(casePosition, direction);

        CasePosition position = null;

        int x = casePosition.getX();
        int y = casePosition.getY();

        switch (direction) {
            case NORTH:
                position = getCasePositionByCoor(x, y + nbOfCases);
                break;
            case NORTH_EAST:
                position = getCasePositionByCoor(x + nbOfCases, y + nbOfCases);
                break;
            case NORTH_WEST:
                position = getCasePositionByCoor(x - nbOfCases, y + nbOfCases);
                break;
            case SOUTH:
                position = getCasePositionByCoor(x, y - nbOfCases);
                break;
            case SOUTH_EAST:
                position = getCasePositionByCoor(x + nbOfCases, y - nbOfCases);
                break;
            case SOUTH_WEST:
                position = getCasePositionByCoor(x - nbOfCases, y - nbOfCases);
                break;
            case EAST:
                position = getCasePositionByCoor(x + nbOfCases, y);
                break;
            case WEST:
                position = getCasePositionByCoor(x - nbOfCases, y);
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
     * Get the number of case between two position, must be a straight line, return null if not
     *
     * @param from
     * @param to
     * @return
     */
    public static Integer getDistanceBetweenPositions(CasePosition from, CasePosition to) {
        Assert.assertNotNull(from, to);

        Direction directionFromPosition = getDirectionFromPosition(from, to);
        CasePosition nearestPositionFromDirection = getNearestPositionFromDirection(from, directionFromPosition);

        if (nearestPositionFromDirection == null || !isPositionInLine(from, nearestPositionFromDirection, to)) {
            return null;
        }

        double partOne = Math.pow(from.getX() - to.getX(), 2);
        double partTwo = Math.pow(from.getY() - to.getY(), 2);
        return (int) (Math.sqrt(partOne + partTwo));
    }


    /**
     * Get the number of case between two position, must be a straight line, return null if not
     *
     * @param from
     * @param xTo
     * @param yTo
     * @return
     */
    public static double getDistanceBetweenPositions(CasePosition from, float xTo, float yTo) {
        Assert.assertNotNull(from);

        double partOne = Math.pow(from.getX() - xTo, 2);
        double partTwo = Math.pow(from.getY() - yTo, 2);
        return Math.sqrt(partOne + partTwo);
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

        if (xCurrent == xToCheck && xCurrent == toCheck.getX() || yCurrent == yToCheck && yCurrent == toCheck.getY())
            return true;

        // y = mx + b
        float m = getSlopeFromPosition(first, second);
        float b = yCurrent - (m * xCurrent);

        return yToCheck == (m * xToCheck + b);
    }


    /**
     * Check if the position is on the perimeter of the circle
     *
     * @param from
     * @param to
     * @param xRadius
     * @param yRadius
     * @return
     */
    public static boolean isPositionOnCirclePerimeter(CasePosition from, CasePosition to, float xRadius, float yRadius) {
        Assert.assertNotNull(from, to);

        //(x−a)^2 + (y−b)^2 = r^2
        double partOne = Math.pow(to.getX() - from.getX(), 2);
        double partTwo = Math.pow(to.getY() - from.getY(), 2);
        double disRadius = Math.pow(getDistanceBetweenPositions(from, xRadius, yRadius), 2);

        return Precision.equals(partOne + partTwo, disRadius, 0.1);
    }
}
