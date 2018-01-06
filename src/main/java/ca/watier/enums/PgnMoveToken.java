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

import ca.watier.utils.PgnParser;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum PgnMoveToken {
    CAPTURE("x"), CHECK("+"), CHECKMATE("#", "++"), PAWN_PROMOTION("="), KINGSIDE_CASTLING("O-O"), QUEENSIDE_CASTLING("O-O-O"), NORMAL_MOVE("\0");
    private List<String> chars = new ArrayList<>();

    PgnMoveToken(@NotNull String... chars) {
        if (chars.length > 0) {
            this.chars.addAll(Arrays.asList(chars));
        }
    }

    public static List<PgnMoveToken> getPieceMovesFromLetter(@NotNull String action) {
        List<PgnMoveToken> moves = new ArrayList<>();

        for (PgnMoveToken pgnMoveToken : values()) {
            for (String current : pgnMoveToken.getChars()) {
                if (action.contains(current)) {
                    switch (pgnMoveToken) { //The moves that contain a "normal move"
                        case CAPTURE:
                        case CHECK:
                        case CHECKMATE:
                        case PAWN_PROMOTION:
                            if (!moves.contains(PgnParser.NORMAL_MOVE)) {
                                moves.add(PgnParser.NORMAL_MOVE);
                            }
                            break;
                    }
                    moves.add(pgnMoveToken);
                }
            }
        }

        if (moves.isEmpty()) {
            moves.add(PgnParser.NORMAL_MOVE);
        }

        return moves;
    }

    public List<String> getChars() {
        return chars;
    }
}
