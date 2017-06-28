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

import ca.watier.utils.Assert;
import com.fasterxml.jackson.annotation.JsonFormat;

import static ca.watier.enums.Side.BLACK;
import static ca.watier.enums.Side.WHITE;

/**
 * Created by yannick on 4/18/2017.
 */

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Pieces {
    W_KING("White King", '♔', WHITE, (byte) -128), //TODO: Castling
    W_QUEEN("White Queen ", '♕', WHITE, (byte) 9),
    W_ROOK("White Rook", '♖', WHITE, (byte) 5),
    W_BISHOP("White Bishop", '♗', WHITE, (byte) 3),
    W_KNIGHT("White Knight", '♘', WHITE, (byte) 3),
    W_PAWN("White Pawn", '♙', WHITE, (byte) 1),
    B_KING("Black King", '♚', BLACK, (byte) -128), //TODO: Castling
    B_QUEEN("Black Queen ", '♛', BLACK, (byte) 9),
    B_ROOK("Black Rook", '♜', BLACK, (byte) 5),
    B_BISHOP("Black Bishop", '♝', BLACK, (byte) 3),
    B_KNIGHT("Black Knight", '♞', BLACK, (byte) 3),
    B_PAWN("Black Pawn", '♟', BLACK, (byte) 1);

    private final char unicodeIcon;
    private final String name;
    private final Side side;
    private byte point;

    Pieces(String name, char unicodeIcon, Side side, byte point) {
        this.name = name;
        this.unicodeIcon = unicodeIcon;
        this.side = side;
        this.point = point;
    }

    public static boolean isKing(Pieces piece) {
        Assert.assertNotNull(piece);

        return W_KING.equals(piece) || B_KING.equals(piece);
    }

    public static boolean isKnight(Pieces piece) {
        Assert.assertNotNull(piece);

        return W_KNIGHT.equals(piece) || B_KNIGHT.equals(piece);
    }

    public static Pieces getKingBySide(Side playerSide) {
        Assert.assertNotNull(playerSide);

        if (Side.OBSERVER.equals(playerSide)) {
            return null;
        }

        return BLACK.equals(playerSide) ? B_KING : W_KING;
    }

    public static boolean isRook(Pieces piece) {
        Assert.assertNotNull(piece);

        return W_ROOK.equals(piece) || B_ROOK.equals(piece);
    }

    public static boolean isSameSide(Pieces first, Pieces second) {
        Assert.assertNotNull(first, second);
        return first.getSide().equals(second.getSide());
    }

    public Side getSide() {
        return side;
    }

    public char getUnicodeIcon() {
        return unicodeIcon;
    }

    public String getName() {
        return name;
    }

    public byte getPoint() {
        return point;
    }
}
