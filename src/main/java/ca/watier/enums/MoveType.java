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

package ca.watier.enums;

/**
 * Created by yannick on 6/28/2017.
 */
public enum MoveType {
    NORMAL_MOVE, MOVE_NOT_ALLOWED, CASTLING, EN_PASSANT,
    PAWN_PROMOTION; //PAWN_PROMOTION count as a move, but need to pause the game, and wait for the player too choose the promotion

    public static boolean isMoved(MoveType moveType) {
        return NORMAL_MOVE.equals(moveType) || PAWN_PROMOTION.equals(moveType) || isSpecialMove(moveType);
    }

    public static boolean isSpecialMove(MoveType moveType) {
        return CASTLING.equals(moveType) || EN_PASSANT.equals(moveType);
    }
}
