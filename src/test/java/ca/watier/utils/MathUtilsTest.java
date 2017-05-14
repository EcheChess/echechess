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

import ca.watier.constraints.KnightMoveConstraint;
import ca.watier.enums.CasePosition;
import ca.watier.enums.Direction;
import org.junit.Assert;
import org.junit.Test;

import static ca.watier.enums.CasePosition.*;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by yannick on 4/25/2017.
 */
public class MathUtilsTest {
    private static final Direction NORTH = Direction.NORTH;
    private static final Direction NORTH_WEST = Direction.NORTH_WEST;
    private static final Direction WEST = Direction.WEST;
    private static final Direction SOUTH_WEST = Direction.SOUTH_WEST;
    private static final Direction SOUTH = Direction.SOUTH;
    private static final Direction SOUTH_EAST = Direction.SOUTH_EAST;
    private static final Direction EAST = Direction.EAST;
    private static final Direction NORTH_EAST = Direction.NORTH_EAST;
    private static final CasePosition D_5 = D5;
    private static final float DELTA_SLOPE_TEST = 0f;

    @Test
    public void getPositionsBetweenTwoPosition() throws Exception {
        assertThat(MathUtils.getPositionsBetweenTwoPosition(A1, H8)).containsOnly(B2, C3, D4, E5, F6, G7);
        assertThat(MathUtils.getPositionsBetweenTwoPosition(H8, A1)).containsOnly(B2, C3, D4, E5, F6, G7);
        assertThat(MathUtils.getPositionsBetweenTwoPosition(E1, E8)).containsOnly(E2, E3, E4, E5, E6, E7);
        assertThat(MathUtils.getPositionsBetweenTwoPosition(E8, E1)).containsOnly(E2, E3, E4, E5, E6, E7);
        assertThat(MathUtils.getPositionsBetweenTwoPosition(A4, H4)).containsOnly(B4, C4, D4, E4, F4, G4);
        assertThat(MathUtils.getPositionsBetweenTwoPosition(H4, A4)).containsOnly(B4, C4, D4, E4, F4, G4);
        assertThat(MathUtils.getPositionsBetweenTwoPosition(A8, H1)).containsOnly(B7, C6, D5, E4, F3, G2);
        assertThat(MathUtils.getPositionsBetweenTwoPosition(H1, A8)).containsOnly(B7, C6, D5, E4, F3, G2);
        assertThat(MathUtils.getPositionsBetweenTwoPosition(E4, G4)).containsOnly(F4);
        assertThat(MathUtils.getPositionsBetweenTwoPosition(F3, F5)).containsOnly(F4);
        assertThat(MathUtils.getPositionsBetweenTwoPosition(E3, G5)).containsOnly(F4);
        assertThat(MathUtils.getPositionsBetweenTwoPosition(G5, E3)).containsOnly(F4);
        assertThat(MathUtils.getPositionsBetweenTwoPosition(A4, H8)).isEmpty();
    }

    @Test
    public void isPositionOnCirclePerimeter_knight() throws Exception {

        int x = D_5.getX();
        int y = D_5.getY();

        Assert.assertTrue(MathUtils.isPositionOnCirclePerimeter(D_5, B4, x + KnightMoveConstraint.KNIGHT_RADIUS_EQUATION, y));
        Assert.assertTrue(MathUtils.isPositionOnCirclePerimeter(D_5, B6, x + KnightMoveConstraint.KNIGHT_RADIUS_EQUATION, y));

        Assert.assertTrue(MathUtils.isPositionOnCirclePerimeter(D_5, C7, x + KnightMoveConstraint.KNIGHT_RADIUS_EQUATION, y));
        Assert.assertTrue(MathUtils.isPositionOnCirclePerimeter(D_5, E7, x + KnightMoveConstraint.KNIGHT_RADIUS_EQUATION, y));

        Assert.assertTrue(MathUtils.isPositionOnCirclePerimeter(D_5, F4, x + KnightMoveConstraint.KNIGHT_RADIUS_EQUATION, y));
        Assert.assertTrue(MathUtils.isPositionOnCirclePerimeter(D_5, F6, x + KnightMoveConstraint.KNIGHT_RADIUS_EQUATION, y));

        Assert.assertTrue(MathUtils.isPositionOnCirclePerimeter(D_5, C3, x + KnightMoveConstraint.KNIGHT_RADIUS_EQUATION, y));
        Assert.assertTrue(MathUtils.isPositionOnCirclePerimeter(D_5, E3, x + KnightMoveConstraint.KNIGHT_RADIUS_EQUATION, y));

        Assert.assertFalse(MathUtils.isPositionOnCirclePerimeter(D_5, D7, x + KnightMoveConstraint.KNIGHT_RADIUS_EQUATION, y));
        Assert.assertFalse(MathUtils.isPositionOnCirclePerimeter(D_5, D3, x + KnightMoveConstraint.KNIGHT_RADIUS_EQUATION, y));
        Assert.assertFalse(MathUtils.isPositionOnCirclePerimeter(D_5, B5, x + KnightMoveConstraint.KNIGHT_RADIUS_EQUATION, y));
        Assert.assertFalse(MathUtils.isPositionOnCirclePerimeter(D_5, F5, x + KnightMoveConstraint.KNIGHT_RADIUS_EQUATION, y));
    }

    @Test
    public void getDistanceBetweenPositions() throws Exception {
        Assert.assertNull(MathUtils.getDistanceBetweenPositionsWithCommonDirection(D_5, D_5));
        Assert.assertEquals(Integer.valueOf(7), MathUtils.getDistanceBetweenPositionsWithCommonDirection(H1, H8));
        Assert.assertEquals(Integer.valueOf(4), MathUtils.getDistanceBetweenPositionsWithCommonDirection(H1, H5));
        Assert.assertEquals(Integer.valueOf(7), MathUtils.getDistanceBetweenPositionsWithCommonDirection(H8, H1));
        Assert.assertEquals(Integer.valueOf(4), MathUtils.getDistanceBetweenPositionsWithCommonDirection(H5, H1));
        Assert.assertEquals(Integer.valueOf(7), MathUtils.getDistanceBetweenPositionsWithCommonDirection(H5, A5));
        Assert.assertEquals(Integer.valueOf(3), MathUtils.getDistanceBetweenPositionsWithCommonDirection(H5, E5));
        Assert.assertEquals(Integer.valueOf(7), MathUtils.getDistanceBetweenPositionsWithCommonDirection(A5, H5));
        Assert.assertEquals(Integer.valueOf(3), MathUtils.getDistanceBetweenPositionsWithCommonDirection(E5, H5));
        Assert.assertEquals(Integer.valueOf(9), MathUtils.getDistanceBetweenPositionsWithCommonDirection(H1, A8));
        Assert.assertEquals(Integer.valueOf(7), MathUtils.getDistanceBetweenPositionsWithCommonDirection(F3, A8));
        Assert.assertEquals(Integer.valueOf(9), MathUtils.getDistanceBetweenPositionsWithCommonDirection(A8, H1));
        Assert.assertEquals(Integer.valueOf(7), MathUtils.getDistanceBetweenPositionsWithCommonDirection(A8, F3));
    }

    @Test
    public void getNearestPositionFromDirection() throws Exception {
        Assert.assertEquals(D6, MathUtils.getNearestPositionFromDirection(D_5, Direction.NORTH));
        Assert.assertEquals(D4, MathUtils.getNearestPositionFromDirection(D_5, Direction.SOUTH));
        Assert.assertEquals(C5, MathUtils.getNearestPositionFromDirection(D_5, Direction.WEST));
        Assert.assertEquals(E5, MathUtils.getNearestPositionFromDirection(D_5, Direction.EAST));

        Assert.assertEquals(C6, MathUtils.getNearestPositionFromDirection(D_5, Direction.NORTH_WEST));
        Assert.assertEquals(E6, MathUtils.getNearestPositionFromDirection(D_5, Direction.NORTH_EAST));

        Assert.assertEquals(C4, MathUtils.getNearestPositionFromDirection(D_5, Direction.SOUTH_WEST));
        Assert.assertEquals(E4, MathUtils.getNearestPositionFromDirection(D_5, Direction.SOUTH_EAST));


        Assert.assertEquals(D7, MathUtils.getNearestPositionFromDirection(D_5, Direction.NORTH, 2));
        Assert.assertEquals(D3, MathUtils.getNearestPositionFromDirection(D_5, Direction.SOUTH, 2));
        Assert.assertEquals(B5, MathUtils.getNearestPositionFromDirection(D_5, Direction.WEST, 2));
        Assert.assertEquals(F5, MathUtils.getNearestPositionFromDirection(D_5, Direction.EAST, 2));

        Assert.assertEquals(B7, MathUtils.getNearestPositionFromDirection(D_5, Direction.NORTH_WEST, 2));
        Assert.assertEquals(F7, MathUtils.getNearestPositionFromDirection(D_5, Direction.NORTH_EAST, 2));

        Assert.assertEquals(B3, MathUtils.getNearestPositionFromDirection(D_5, Direction.SOUTH_WEST, 2));
        Assert.assertEquals(F3, MathUtils.getNearestPositionFromDirection(D_5, Direction.SOUTH_EAST, 2));
    }

    @Test
    public void getDirectionFromPosition() throws Exception {

        Assert.assertNull(MathUtils.getDirectionFromPosition(D_5, D_5));

        Assert.assertEquals(NORTH, MathUtils.getDirectionFromPosition(D_5, D6));
        Assert.assertEquals(NORTH, MathUtils.getDirectionFromPosition(D_5, D8));

        Assert.assertEquals(NORTH_WEST, MathUtils.getDirectionFromPosition(D_5, C8));
        Assert.assertEquals(NORTH_WEST, MathUtils.getDirectionFromPosition(D_5, C6));
        Assert.assertEquals(NORTH_WEST, MathUtils.getDirectionFromPosition(D_5, A8));

        Assert.assertEquals(WEST, MathUtils.getDirectionFromPosition(D_5, A5));
        Assert.assertEquals(WEST, MathUtils.getDirectionFromPosition(D_5, C5));

        Assert.assertEquals(SOUTH_WEST, MathUtils.getDirectionFromPosition(D_5, B4));
        Assert.assertEquals(SOUTH_WEST, MathUtils.getDirectionFromPosition(D_5, C4));
        Assert.assertEquals(SOUTH_WEST, MathUtils.getDirectionFromPosition(D_5, A1));

        Assert.assertEquals(SOUTH, MathUtils.getDirectionFromPosition(D_5, D4));
        Assert.assertEquals(SOUTH, MathUtils.getDirectionFromPosition(D_5, D1));

        Assert.assertEquals(SOUTH_EAST, MathUtils.getDirectionFromPosition(D_5, F1));
        Assert.assertEquals(SOUTH_EAST, MathUtils.getDirectionFromPosition(D_5, E4));
        Assert.assertEquals(SOUTH_EAST, MathUtils.getDirectionFromPosition(D_5, H1));

        Assert.assertEquals(EAST, MathUtils.getDirectionFromPosition(D_5, E5));
        Assert.assertEquals(EAST, MathUtils.getDirectionFromPosition(D_5, H5));

        Assert.assertEquals(NORTH_EAST, MathUtils.getDirectionFromPosition(D_5, G6));
        Assert.assertEquals(NORTH_EAST, MathUtils.getDirectionFromPosition(D_5, E6));
        Assert.assertEquals(NORTH_EAST, MathUtils.getDirectionFromPosition(D_5, H8));
    }

    @Test
    public void getSlopeFromPosition() throws Exception {
        Assert.assertNull(MathUtils.getSlopeFromPosition(D_5, D_5));
        Assert.assertEquals(-1f, MathUtils.getSlopeFromPosition(B8, H2), DELTA_SLOPE_TEST);
        Assert.assertEquals(7 / 3f, MathUtils.getSlopeFromPosition(E1, H8), DELTA_SLOPE_TEST);
    }

    @Test
    public void isPositionInLine() {
        Assert.assertTrue(MathUtils.isPositionInLine(D6, E5, H2));
        Assert.assertTrue(MathUtils.isPositionInLine(A6, B5, D3));
        Assert.assertTrue(MathUtils.isPositionInLine(H8, G7, C3));
        Assert.assertTrue(MathUtils.isPositionInLine(E4, D4, A4));
        Assert.assertTrue(MathUtils.isPositionInLine(E4, E5, E8));
        Assert.assertFalse(MathUtils.isPositionInLine(H8, G7, C4));
    }
}