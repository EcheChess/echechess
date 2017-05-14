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

import ca.watier.enums.CasePosition;
import ca.watier.enums.Direction;
import ca.watier.enums.Pieces;
import ca.watier.enums.Side;
import ca.watier.utils.Assert;
import ca.watier.utils.BaseUtils;
import ca.watier.utils.GameUtils;
import ca.watier.utils.MathUtils;

import java.util.Map;

/**
 * Created by yannick on 4/23/2017.
 */
public class PawnMoveConstraint implements MoveConstraint {

    @Override
    public boolean canAttackTo(CasePosition from, CasePosition to, Side side, Map<CasePosition, Pieces> positionPiecesMap) {
        return isMoveValid(from, to, side, positionPiecesMap, true);
    }

    @Override
    public boolean isMoveValid(CasePosition from, CasePosition to, Side side, Map<CasePosition, Pieces> positionPiecesMap) {
        return isMoveValid(from, to, side, positionPiecesMap, false);
    }

    private boolean isMoveValid(CasePosition from, CasePosition to, Side side, Map<CasePosition, Pieces> positionPiecesMap, boolean skipHittingValidation) {
        Assert.assertNotNull(from, to, side);

        Direction direction = Direction.NORTH,
                directionAttack1 = Direction.NORTH_WEST,
                directionAttack2 = Direction.NORTH_EAST;

        //Pre checks, MUST BE FIRST
        if (Side.BLACK.equals(side)) {
            direction = Direction.SOUTH;
            directionAttack1 = Direction.SOUTH_WEST;
            directionAttack2 = Direction.SOUTH_EAST;
        }

        Pieces hittingPiece = positionPiecesMap.get(to);
        int nbCaseBetweenPositions = BaseUtils.getSafeInteger(MathUtils.getDistanceBetweenPositionsWithCommonDirection(from, to));
        Direction directionFromPosition = MathUtils.getDirectionFromPosition(from, to);
        boolean otherPiecesBetweenTarget = GameUtils.isOtherPiecesBetweenTarget(from, to, positionPiecesMap);

        boolean isFrontMove = direction.equals(directionFromPosition);
        boolean isNbOfCaseIsOne = nbCaseBetweenPositions == 1;
        boolean normalMove = (GameUtils.isDefaultPosition(from, positionPiecesMap.get(from)) &&
                nbCaseBetweenPositions == 2 || isNbOfCaseIsOne) && isFrontMove && !otherPiecesBetweenTarget;

        if (normalMove && hittingPiece == null) { //Normal move
            return true;
        } else if (normalMove) { //Blocked by another piece, with a normal move
            return false;
        }

        if (directionFromPosition == null) {
            return false;
        }

        boolean checkHit = true;
        if (!skipHittingValidation) {
            checkHit = hittingPiece != null && !hittingPiece.getSide().equals(side) && !Pieces.isKing(hittingPiece);
        }

        //Attack move
        return isNbOfCaseIsOne && checkHit &&
                (directionFromPosition.equals(directionAttack1) || directionFromPosition.equals(directionAttack2));
    }
}
