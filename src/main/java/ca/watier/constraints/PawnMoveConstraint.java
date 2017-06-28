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

import ca.watier.enums.*;
import ca.watier.game.GenericGameHandler;
import ca.watier.interfaces.BaseUtils;
import ca.watier.interfaces.MoveConstraint;
import ca.watier.utils.Assert;
import ca.watier.utils.GameUtils;
import ca.watier.utils.MathUtils;

import java.util.Map;

/**
 * Created by yannick on 4/23/2017.
 */
public class PawnMoveConstraint implements MoveConstraint {

    @Override
    public boolean isMoveValid(CasePosition from, CasePosition to, GenericGameHandler gameHandler, MoveMode moveMode) {
        Assert.assertNotNull(from, to);

        Direction direction = Direction.NORTH;
        Direction directionAttack1 = Direction.NORTH_WEST;
        Direction directionAttack2 = Direction.NORTH_EAST;

        Pieces pieceFrom = gameHandler.getPiece(from);
        Side sideFrom = pieceFrom.getSide();

        //Pre checks, MUST BE FIRST
        if (Side.BLACK.equals(sideFrom)) {
            direction = Direction.SOUTH;
            directionAttack1 = Direction.SOUTH_WEST;
            directionAttack2 = Direction.SOUTH_EAST;
        }

        Map<CasePosition, Pieces> positionPiecesMap = gameHandler.getPiecesLocation();
        Pieces hittingPiece = positionPiecesMap.get(to);
        int nbCaseBetweenPositions = BaseUtils.getSafeInteger(MathUtils.getDistanceBetweenPositionsWithCommonDirection(from, to));
        Direction directionFromPosition = MathUtils.getDirectionFromPosition(from, to);
        boolean otherPiecesBetweenTarget = GameUtils.isOtherPiecesBetweenTarget(from, to, positionPiecesMap);

        boolean isFrontMove = direction.equals(directionFromPosition);
        boolean isNbOfCaseIsOne = nbCaseBetweenPositions == 1;
        boolean normalMove = (GameUtils.isDefaultPosition(from, positionPiecesMap.get(from)) &&
                nbCaseBetweenPositions == 2 || isNbOfCaseIsOne) && isFrontMove && !otherPiecesBetweenTarget;


        if (directionFromPosition == null) {
            return false;
        }

        boolean isAttackMove = directionFromPosition.equals(directionAttack1) || directionFromPosition.equals(directionAttack2);
        boolean isMoveValid = false;

        if (MoveMode.NORMAL_OR_ATTACK_MOVE.equals(moveMode)) {

            if (normalMove && hittingPiece == null) { //Normal move
                return true;
            } else if (normalMove) { //Blocked by another piece, with a normal move
                return false;
            }

            isMoveValid = hittingPiece != null && !hittingPiece.getSide().equals(sideFrom) && !Pieces.isKing(hittingPiece) &&
                    isAttackMove;

        } else if (MoveMode.IS_KING_CHECK_MODE.equals(moveMode)) {
            isMoveValid = isAttackMove;
        }

        return isMoveValid && isNbOfCaseIsOne;
    }
}
