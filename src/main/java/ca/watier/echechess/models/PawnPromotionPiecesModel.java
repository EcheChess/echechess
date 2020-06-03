/*
 *    Copyright 2014 - 2018 Yannick Watier
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

package ca.watier.echechess.models;

import ca.watier.echechess.common.enums.Pieces;
import ca.watier.echechess.common.enums.Side;

public enum PawnPromotionPiecesModel {
    QUEEN, KNIGHT, ROOK, BISHOP;

    public static Pieces from(PawnPromotionPiecesModel model, Side side) {
        if (model == null || side == null) {
            return null;
        }

        return switch (side) {
            case BLACK -> handleBlackSide(model);
            case WHITE -> handleWhiteSide(model);
            default -> null;
        };

    }

    private static Pieces handleBlackSide(PawnPromotionPiecesModel model) {
        return switch (model) {
            case QUEEN -> Pieces.B_QUEEN;
            case KNIGHT -> Pieces.B_KNIGHT;
            case ROOK -> Pieces.B_ROOK;
            case BISHOP -> Pieces.B_BISHOP;
        };
    }

    private static Pieces handleWhiteSide(PawnPromotionPiecesModel model) {
        return switch (model) {
            case QUEEN -> Pieces.W_QUEEN;
            case KNIGHT -> Pieces.W_KNIGHT;
            case ROOK -> Pieces.W_ROOK;
            case BISHOP -> Pieces.W_BISHOP;
        };
    }
}
