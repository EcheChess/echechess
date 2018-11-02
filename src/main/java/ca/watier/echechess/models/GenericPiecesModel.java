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

public enum GenericPiecesModel {
    QUEEN, KNIGHT, ROOK, BISHOP;

    public static Pieces from(GenericPiecesModel model, Side side) {
        if (model == null || side == null) {
            return null;
        }

        switch (side) {
            case BLACK:
                return handleBlackSide(model);
            case WHITE:
                return handleWhiteSide(model);
        }

        return null;
    }

    private static Pieces handleBlackSide(GenericPiecesModel model) {
        switch (model) {
            case QUEEN:
                return Pieces.B_QUEEN;
            case KNIGHT:
                return Pieces.B_KNIGHT;
            case ROOK:
                return Pieces.B_ROOK;
            case BISHOP:
                return Pieces.B_BISHOP;
        }
        return null;
    }

    private static Pieces handleWhiteSide(GenericPiecesModel model) {
        switch (model) {
            case QUEEN:
                return Pieces.W_QUEEN;
            case KNIGHT:
                return Pieces.W_KNIGHT;
            case ROOK:
                return Pieces.W_ROOK;
            case BISHOP:
                return Pieces.W_BISHOP;
        }
        return null;
    }
}
