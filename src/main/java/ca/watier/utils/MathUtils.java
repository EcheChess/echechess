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
import ca.watier.interfaces.BaseUtils;
import org.apache.commons.math3.util.Precision;

import java.util.ArrayList;
import java.util.List;

import static ca.watier.enums.CasePosition.getCasePositionByCoor;
import static ca.watier.enums.Direction.*;

/**
 * Created by yannick on 4/25/2017.
 */
public class MathUtils implements BaseUtils {

    private static final double EPS = 1E-5;

    private MathUtils() {
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
        int xFrom = from.getX();
        double partOne = Math.pow((double) to.getX() - xFrom, 2);
        int yFrom = from.getY();
        double partTwo = Math.pow((double) to.getY() - yFrom, 2);
        double disRadius = Math.pow(getDistanceBetweenPositions(xFrom, yFrom, xRadius, yRadius), 2);

        return Precision.equals(partOne + partTwo, disRadius, EPS);
    }

    /**
     * Get the number of case between two position
     *
     * @param xFrom
     * @param xTo
     * @param yTo
     * @return
     */
    public static double getDistanceBetweenPositions(float xFrom, float yFrom, float xTo, float yTo) {
        Assert.assertNotNull(yFrom);

        double partOne = Math.pow(xFrom - xTo, 2);
        double partTwo = Math.pow(yFrom - yTo, 2);
        return Math.sqrt(partOne + partTwo);
    }

    /**
     * Gets all {@link CasePosition} around the target
     *
     * @param position
     * @return
     */
    public static List<CasePosition> getAllPositionsAroundPosition(CasePosition position) {

        List<CasePosition> values = new ArrayList<>();

        //Fetch all the position around
        for (Direction direction : Direction.values()) {
            CasePosition nearestPositionFromDirection = MathUtils.getNearestPositionFromDirection(position, direction);

            if (nearestPositionFromDirection != null) {
                values.add(nearestPositionFromDirection);
            }
        }

        return values;
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
     * Get the number of case between two position, must be a straight line, return null if not
     *
     * @param from
     * @param to
     * @return
     */
    public static Integer getDistanceBetweenPositionsWithCommonDirection(CasePosition from, CasePosition to) {
        Assert.assertNotNull(from, to);

        Direction directionFromPosition = getDirectionFromPosition(from, to);

        if (directionFromPosition == null) {
            return null;
        }

        CasePosition nearestPositionFromDirection = getNearestPositionFromDirection(from, directionFromPosition);

        if (nearestPositionFromDirection == null || !isPositionInLine(from, nearestPositionFromDirection, to)) {
            return null;
        }


        return (int) getDistanceBetweenPositions(from, to);
    }

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
            return null;
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
        } else if (xTo < xFrom) {
            direction = NORTH_WEST;
        } else if (yTo < yFrom) {
            direction = SOUTH_EAST;
        } else {
            direction = NORTH_EAST;
        }

        return direction;
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
        Float m = getSlopeFromPosition(first, second);
        m = (m != null) ? m : 0;
        float b = yCurrent - (m * xCurrent);

        return Precision.equals((float) yToCheck, m * xToCheck + b, EPS);
    }

    /**
     * Get the number of case between two position
     *
     * @param from
     * @param to
     * @return
     */
    public static double getDistanceBetweenPositions(CasePosition from, CasePosition to) {
        Assert.assertNotNull(from, to);

        return getDistanceBetweenPositions(from.getX(), from.getY(), to.getX(), to.getY());
    }

    /**
     * Get the slope based on the CasePosition, null if undefined
     *
     * @param from
     * @param to
     * @return
     */
    public static Float getSlopeFromPosition(CasePosition from, CasePosition to) {
        Assert.assertNotNull(from, to);

        float xDiff = (float) from.getX() - to.getX();
        float yDiff = (float) from.getY() - to.getY();

        if (xDiff == 0) {
            return null;
        }

        return yDiff / xDiff;
    }

    /**
     * Gets all the position between the targets
     *
     * @param from
     * @param to
     * @return
     */
    public static List<CasePosition> getPositionsBetweenTwoPosition(CasePosition from, CasePosition to) {
        Assert.assertNotNull(from, to);
        List<CasePosition> positions = new ArrayList<>();

        int xFrom = from.getX();
        int yFrom = from.getY();
        int xTo = to.getX();
        int yTo = to.getY();

        Float slopeFromPosition = getSlopeFromPosition(from, to);

        if (slopeFromPosition == null) { //Vertical
            int lesserY = (yFrom < yTo) ? yFrom : yTo;
            int greaterY = (yFrom > yTo) ? yFrom : yTo;

            for (int i = (lesserY + 1); i < greaterY; i++) {
                CasePosition casePositionByCoor = CasePosition.getCasePositionByCoor(xTo, i);
                if (casePositionByCoor != null && isPositionInLine(from, to, casePositionByCoor)) {
                    positions.add(casePositionByCoor);
                }
            }
        } else if (slopeFromPosition == 0) { //Horizontal
            int lesserX = (xFrom < xTo) ? xFrom : xTo;
            int greaterX = (xFrom > xTo) ? xFrom : xTo;

            for (int i = (lesserX + 1); i < greaterX; i++) {

                CasePosition casePositionByCoor = CasePosition.getCasePositionByCoor(i, yTo);
                if (casePositionByCoor != null && isPositionInLine(from, to, casePositionByCoor)) {
                    positions.add(casePositionByCoor);
                }
            }
        } else { //Diagonal
            CasePosition leftPosition;
            CasePosition rightPosition;

            if (xFrom < xTo) {
                leftPosition = from;
                rightPosition = to;
            } else {
                rightPosition = from;
                leftPosition = to;
            }

            int leftX = leftPosition.getX();
            int leftY = leftPosition.getY();
            int rightX = rightPosition.getX();

            for (int i = (leftX + 1); i < rightX; i++) {
                if (slopeFromPosition < 0) {
                    leftY--;
                } else {
                    leftY++;
                }

                CasePosition casePositionByCoor = CasePosition.getCasePositionByCoor(i, leftY);
                if (casePositionByCoor != null && isPositionInLine(from, to, casePositionByCoor)) {
                    positions.add(casePositionByCoor);
                }
            }
        }

        return positions;
    }
}
