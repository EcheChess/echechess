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

import static ca.watier.enums.CasePosition.*;

public enum PgnMoveToken {
    CAPTURE("x"),
    CHECK("+"),
    CHECKMATE("#", "++"),
    PAWN_PROMOTION("="),
    KINGSIDE_CASTLING("O-O"),
    QUEENSIDE_CASTLING("O-O-O"),
    KINGSIDE_CASTLING_CHECK("O-O+"),
    QUEENSIDE_CASTLING_CHECK("O-O-O+"),
    KINGSIDE_CASTLING_CHECKMATE("O-O#"),
    QUEENSIDE_CASTLING_CHECKMATE("O-O-O#"),
    NORMAL_MOVE("\0");

    private List<String> chars = new ArrayList<>();

    PgnMoveToken(@NotNull String... chars) {
        if (chars.length > 0) {
            this.chars.addAll(Arrays.asList(chars));
        }
    }

    public static List<PgnMoveToken> getPieceMovesFromLetter(@NotNull String action) {
        List<PgnMoveToken> moves = new ArrayList<>();

        if (QUEENSIDE_CASTLING.getChars().contains(action)) {  //The Queen side casting token contains also the king side (O-O in O-O-O...)
            moves.add(QUEENSIDE_CASTLING);
        } else if (KINGSIDE_CASTLING.getChars().contains(action)) {
            moves.add(KINGSIDE_CASTLING);
        } else if (QUEENSIDE_CASTLING_CHECK.getChars().contains(action)) {
            moves.add(QUEENSIDE_CASTLING_CHECK);
        } else if (KINGSIDE_CASTLING_CHECK.getChars().contains(action)) {
            moves.add(KINGSIDE_CASTLING_CHECK);
        } else if (QUEENSIDE_CASTLING_CHECKMATE.getChars().contains(action)) {
            moves.add(QUEENSIDE_CASTLING_CHECKMATE);
        } else if (KINGSIDE_CASTLING_CHECKMATE.getChars().contains(action)) {
            moves.add(KINGSIDE_CASTLING_CHECKMATE);
        } else {
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
        }

        return moves;
    }

    public List<String> getChars() {
        return chars;
    }

    public static CasePosition getCastlingRookPosition(PgnMoveToken pgnMoveToken, Side playerSide) {
        if (!PgnMoveToken.isCastling(pgnMoveToken)) {
            return null;
        }

        CasePosition value = null;
        boolean isQueenSide = PgnMoveToken.isQueenSideCastling(pgnMoveToken);


        switch (playerSide) {
            case BLACK:
                value = (isQueenSide ? A8 : H8);
                break;
            case WHITE:
                value = (isQueenSide ? A1 : H1);
                break;
        }

        return value;
    }

    private static boolean isCastling(PgnMoveToken pgnMoveToken) {
        return isQueenSideCastling(pgnMoveToken) || isKingSideCastling(pgnMoveToken);
    }

    private static boolean isQueenSideCastling(PgnMoveToken pgnMoveToken) {
        return PgnMoveToken.QUEENSIDE_CASTLING.equals(pgnMoveToken) ||
                PgnMoveToken.QUEENSIDE_CASTLING_CHECK.equals(pgnMoveToken) ||
                PgnMoveToken.QUEENSIDE_CASTLING_CHECKMATE.equals(pgnMoveToken);
    }

    private static boolean isKingSideCastling(PgnMoveToken pgnMoveToken) {
        return PgnMoveToken.KINGSIDE_CASTLING.equals(pgnMoveToken) ||
                PgnMoveToken.KINGSIDE_CASTLING_CHECK.equals(pgnMoveToken) ||
                PgnMoveToken.KINGSIDE_CASTLING_CHECKMATE.equals(pgnMoveToken);
    }
}
