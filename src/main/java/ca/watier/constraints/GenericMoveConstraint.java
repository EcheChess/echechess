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

package ca.watier.constraints;

import ca.watier.defassert.Assert;
import ca.watier.enums.CasePosition;
import ca.watier.enums.DirectionPattern;
import ca.watier.enums.Pieces;
import ca.watier.enums.Side;
import ca.watier.game.Direction;
import ca.watier.utils.GameUtils;
import ca.watier.utils.MathUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by yannick on 4/23/2017.
 */
public class GenericMoveConstraint implements MoveConstraint {

    @Override
    public boolean isMoveValid(CasePosition from, CasePosition to, Side side, Map<CasePosition, Pieces> positionPiecesMap) {

        DirectionPattern directionPattern = authorizedMoves();

        if (directionPattern == null) {
            return false;
        }

        Direction[] directions = directionPattern.getDirections();

        if (directions == null) {
            return false;
        }

        List<Direction> directionList = Arrays.asList(directions);

        Assert.assertNotNull(from, to, side);
        Pieces pieces = positionPiecesMap.get(to);

        Direction directionFromPosition = MathUtils.getDirectionFromPosition(from, to);
        boolean isMoveValid = !GameUtils.isOtherPiecesBetweenTarget(from, to, positionPiecesMap) && directionList.contains(directionFromPosition);

        if (pieces != null) {
            isMoveValid &= !side.equals(pieces.getSide());
        }

        return isMoveValid;
    }

    protected DirectionPattern authorizedMoves() {
        return DirectionPattern.ALL;
    }
}
