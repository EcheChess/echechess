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
import ca.watier.utils.Assert;
import ca.watier.utils.GameUtils;
import ca.watier.utils.MathUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by yannick on 4/23/2017.
 */
public class GenericMoveConstraint implements MoveConstraint {

    private DirectionPattern pattern;

    public GenericMoveConstraint(DirectionPattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean isMoveValid(CasePosition from, CasePosition to, Side side, Map<CasePosition, Pieces> positionPiecesMap, MoveMode moveMode) {
        if (pattern == null) {
            return false;
        }

        Direction[] directions = pattern.getDirections();

        assert directions != null;

        List<Direction> directionList = Arrays.asList(directions);

        Assert.assertNotNull(from, to, side);
        Pieces pieces = positionPiecesMap.get(to);

        Direction directionFromPosition = MathUtils.getDirectionFromPosition(from, to);

        if (!directionList.contains(directionFromPosition)) {
            return false;
        }

        boolean isMoveValid = MathUtils.isPositionInLine(from, MathUtils.getNearestPositionFromDirection(from, directionFromPosition), to);

        if (MoveMode.NORMAL_OR_ATTACK_MOVE.equals(moveMode)) {
            isMoveValid &= !GameUtils.isOtherPiecesBetweenTarget(from, to, positionPiecesMap);

            if (pieces != null) {
                isMoveValid &= !side.equals(pieces.getSide()) && !Pieces.isKing(pieces);
            }
        } else if (MoveMode.IS_KING_CHECK_MODE.equals(moveMode)) {

            /*
                1) If a king between position and not covered, return true
                2) If other piece between position, return false
             */

            boolean isKingDirectOnTheAttackingPiece = false;

            List<CasePosition> piecesBetweenPosition = GameUtils.getPiecesBetweenPosition(from, to, positionPiecesMap);
            CasePosition kingPosition = GameUtils.getPosition(Pieces.getKingBySide(Side.getOtherPlayerSide(side)), positionPiecesMap);

            if (piecesBetweenPosition.contains(kingPosition)) { //If the king is on the path, check if he's covered by another piece
                isKingDirectOnTheAttackingPiece = GameUtils.getPiecesBetweenPosition(from, kingPosition, positionPiecesMap).isEmpty();
            }

            isMoveValid &= piecesBetweenPosition.isEmpty() || isKingDirectOnTheAttackingPiece;
        }

        return isMoveValid;
    }
}
