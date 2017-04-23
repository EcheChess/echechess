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
 * Created by yannick on 4/18/2017.
 */
public enum Pieces {
    W_KING("White King", '♔', Side.WHITE),
    W_QUEEN("White Queen ", '♕', Side.WHITE),
    W_ROOK("White Rook", '♖', Side.WHITE),
    W_BISHOP("White Bishop", '♗', Side.WHITE),
    W_KNIGHT("White Knight", '♘', Side.WHITE),
    W_PAWN("White Pawn", '♙', Side.WHITE),
    B_KING("Black King", '♚', Side.BLACK),
    B_QUEEN("Black Queen ", '♛', Side.BLACK),
    B_ROOK("Black Rook", '♜', Side.BLACK),
    B_BISHOP("Black Bishop", '♝', Side.BLACK),
    B_KNIGHT("Black Knight", '♞', Side.BLACK),
    B_PAWN("Black Pawn", '♟', Side.BLACK);

    private char unicodeIcon;
    private String name;
    private Side side;

    Pieces(String name, char unicodeIcon, Side side) {
        this.name = name;
        this.unicodeIcon = unicodeIcon;
        this.side = side;
    }

    public char getUnicodeIcon() {
        return unicodeIcon;
    }

    public String getName() {
        return name;
    }

    public Side getSide() {
        return side;
    }
}
