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

package ca.watier.enums;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum PgnPieceFound {
    QUEEN('Q', Pieces.B_QUEEN, Pieces.W_QUEEN),
    BISHOP('B', Pieces.B_BISHOP, Pieces.W_BISHOP),
    KING('K', Pieces.W_KING, Pieces.B_KING),
    ROOK('R', Pieces.B_ROOK, Pieces.W_ROOK),
    KNIGHT('N', Pieces.B_KNIGHT, Pieces.W_KNIGHT),
    PAWN('\0', Pieces.B_PAWN, Pieces.W_PAWN);

    private char letter;
    private List<Pieces> pieces = new ArrayList<>();

    PgnPieceFound(char letter, Pieces... pieces) {
        this.letter = letter;

        if (pieces != null) {
            this.pieces.addAll(Arrays.asList(pieces));
        }
    }

    public static PgnPieceFound getPieceFromAction(@NotNull String action) {
        PgnPieceFound pgnPieceFoundFromLetter = PAWN;

        for (byte b : action.getBytes()) {
            PgnPieceFound currentPgnPieceFound = getPieceFromLetter((char) b);

            if (!PAWN.equals(currentPgnPieceFound)) {
                pgnPieceFoundFromLetter = currentPgnPieceFound;
                break;
            }
        }

        return pgnPieceFoundFromLetter;
    }

    private static PgnPieceFound getPieceFromLetter(char letter) {
        for (PgnPieceFound pgnPieceFound : PgnPieceFound.values()) {
            if (pgnPieceFound.getLetter() == letter) {
                return pgnPieceFound;
            }
        }

        return PgnPieceFound.PAWN;
    }

    public char getLetter() {
        return letter;
    }

    public Pieces getPieceBySide(Side side) {
        Pieces value = null;
        for (Pieces piece : pieces) {
            if (piece.getSide() == side) {
                value = piece;
                break;
            }
        }
        return value;
    }

    public List<Pieces> getPieces() {
        return pieces;
    }
}
