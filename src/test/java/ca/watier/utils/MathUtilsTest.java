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
import ca.watier.game.Direction;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by yannick on 4/25/2017.
 */
public class MathUtilsTest {
    @Test
    public void getDistanceBetweenPositions() throws Exception {
        Assert.assertNull(MathUtils.getDistanceBetweenPositions(D_5, D_5));
        Assert.assertEquals(Integer.valueOf(7), MathUtils.getDistanceBetweenPositions(CasePosition.H1, CasePosition.H8));
        Assert.assertEquals(Integer.valueOf(4), MathUtils.getDistanceBetweenPositions(CasePosition.H1, CasePosition.H5));
        Assert.assertEquals(Integer.valueOf(7), MathUtils.getDistanceBetweenPositions(CasePosition.H8, CasePosition.H1));
        Assert.assertEquals(Integer.valueOf(4), MathUtils.getDistanceBetweenPositions(CasePosition.H5, CasePosition.H1));
        Assert.assertEquals(Integer.valueOf(7), MathUtils.getDistanceBetweenPositions(CasePosition.H5, CasePosition.A5));
        Assert.assertEquals(Integer.valueOf(3), MathUtils.getDistanceBetweenPositions(CasePosition.H5, CasePosition.E5));
        Assert.assertEquals(Integer.valueOf(7), MathUtils.getDistanceBetweenPositions(CasePosition.A5, CasePosition.H5));
        Assert.assertEquals(Integer.valueOf(3), MathUtils.getDistanceBetweenPositions(CasePosition.E5, CasePosition.H5));
        Assert.assertEquals(Integer.valueOf(9), MathUtils.getDistanceBetweenPositions(CasePosition.H1, CasePosition.A8));
        Assert.assertEquals(Integer.valueOf(7), MathUtils.getDistanceBetweenPositions(CasePosition.F3, CasePosition.A8));
        Assert.assertEquals(Integer.valueOf(9), MathUtils.getDistanceBetweenPositions(CasePosition.A8, CasePosition.H1));
        Assert.assertEquals(Integer.valueOf(7), MathUtils.getDistanceBetweenPositions(CasePosition.A8, CasePosition.F3));
    }

    private static final Direction NORTH = Direction.NORTH;
    private static final Direction NORTH_WEST = Direction.NORTH_WEST;
    private static final Direction WEST = Direction.WEST;
    private static final Direction SOUTH_WEST = Direction.SOUTH_WEST;
    private static final Direction SOUTH = Direction.SOUTH;
    private static final Direction SOUTH_EAST = Direction.SOUTH_EAST;
    private static final Direction EAST = Direction.EAST;
    private static final Direction NORTH_EAST = Direction.NORTH_EAST;
    private static final CasePosition D_5 = CasePosition.D5;
    private static final float DELTA_SLOPE_TEST = 0f;

    @Test
    public void getNearestPositionFromDirection() throws Exception {
        Assert.assertEquals(CasePosition.D6, MathUtils.getNearestPositionFromDirection(D_5, Direction.NORTH));
        Assert.assertEquals(CasePosition.D4, MathUtils.getNearestPositionFromDirection(D_5, Direction.SOUTH));
        Assert.assertEquals(CasePosition.C5, MathUtils.getNearestPositionFromDirection(D_5, Direction.WEST));
        Assert.assertEquals(CasePosition.E5, MathUtils.getNearestPositionFromDirection(D_5, Direction.EAST));

        Assert.assertEquals(CasePosition.C6, MathUtils.getNearestPositionFromDirection(D_5, Direction.NORTH_WEST));
        Assert.assertEquals(CasePosition.E6, MathUtils.getNearestPositionFromDirection(D_5, Direction.NORTH_EAST));

        Assert.assertEquals(CasePosition.C4, MathUtils.getNearestPositionFromDirection(D_5, Direction.SOUTH_WEST));
        Assert.assertEquals(CasePosition.E4, MathUtils.getNearestPositionFromDirection(D_5, Direction.SOUTH_EAST));
    }

    @Test
    public void getDirectionFromPosition() throws Exception {

        Assert.assertEquals(Direction.NONE, MathUtils.getDirectionFromPosition(D_5, D_5));

        Assert.assertEquals(NORTH, MathUtils.getDirectionFromPosition(D_5, CasePosition.D6));
        Assert.assertEquals(NORTH, MathUtils.getDirectionFromPosition(D_5, CasePosition.D8));

        Assert.assertEquals(NORTH_WEST, MathUtils.getDirectionFromPosition(D_5, CasePosition.C8));
        Assert.assertEquals(NORTH_WEST, MathUtils.getDirectionFromPosition(D_5, CasePosition.C6));
        Assert.assertEquals(NORTH_WEST, MathUtils.getDirectionFromPosition(D_5, CasePosition.A8));

        Assert.assertEquals(WEST, MathUtils.getDirectionFromPosition(D_5, CasePosition.A5));
        Assert.assertEquals(WEST, MathUtils.getDirectionFromPosition(D_5, CasePosition.C5));

        Assert.assertEquals(SOUTH_WEST, MathUtils.getDirectionFromPosition(D_5, CasePosition.B4));
        Assert.assertEquals(SOUTH_WEST, MathUtils.getDirectionFromPosition(D_5, CasePosition.C4));
        Assert.assertEquals(SOUTH_WEST, MathUtils.getDirectionFromPosition(D_5, CasePosition.A1));

        Assert.assertEquals(SOUTH, MathUtils.getDirectionFromPosition(D_5, CasePosition.D4));
        Assert.assertEquals(SOUTH, MathUtils.getDirectionFromPosition(D_5, CasePosition.D1));

        Assert.assertEquals(SOUTH_EAST, MathUtils.getDirectionFromPosition(D_5, CasePosition.F1));
        Assert.assertEquals(SOUTH_EAST, MathUtils.getDirectionFromPosition(D_5, CasePosition.E4));
        Assert.assertEquals(SOUTH_EAST, MathUtils.getDirectionFromPosition(D_5, CasePosition.H1));

        Assert.assertEquals(EAST, MathUtils.getDirectionFromPosition(D_5, CasePosition.E5));
        Assert.assertEquals(EAST, MathUtils.getDirectionFromPosition(D_5, CasePosition.H5));

        Assert.assertEquals(NORTH_EAST, MathUtils.getDirectionFromPosition(D_5, CasePosition.G6));
        Assert.assertEquals(NORTH_EAST, MathUtils.getDirectionFromPosition(D_5, CasePosition.E6));
        Assert.assertEquals(NORTH_EAST, MathUtils.getDirectionFromPosition(D_5, CasePosition.H8));
    }

    @Test
    public void getSlopeFromPosition() throws Exception {
        Assert.assertEquals(0f, MathUtils.getSlopeFromPosition(D_5, D_5), DELTA_SLOPE_TEST);
        Assert.assertEquals(-1f, MathUtils.getSlopeFromPosition(CasePosition.B8, CasePosition.H2), DELTA_SLOPE_TEST);
        Assert.assertEquals(7 / 3f, MathUtils.getSlopeFromPosition(CasePosition.E1, CasePosition.H8), DELTA_SLOPE_TEST);
    }

    @Test
    public void isPositionInLine() {
        Assert.assertTrue(MathUtils.isPositionInLine(CasePosition.D6, CasePosition.E5, CasePosition.H2));
        Assert.assertTrue(MathUtils.isPositionInLine(CasePosition.A6, CasePosition.B5, CasePosition.D3));
        Assert.assertTrue(MathUtils.isPositionInLine(CasePosition.H8, CasePosition.G7, CasePosition.C3));
        Assert.assertTrue(MathUtils.isPositionInLine(CasePosition.E4, CasePosition.D4, CasePosition.A4));
        Assert.assertTrue(MathUtils.isPositionInLine(CasePosition.E4, CasePosition.E5, CasePosition.E8));
        Assert.assertFalse(MathUtils.isPositionInLine(CasePosition.H8, CasePosition.G7, CasePosition.C4));
    }
}