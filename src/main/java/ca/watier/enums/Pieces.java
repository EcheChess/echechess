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
    W_KING("White King", '♔'),
    W_QUEEN("White Queen ", '♕'),
    W_ROOK("White Rook", '♖'),
    W_BISHOP("White Bishop", '♗'),
    W_KNIGHT("White Knight", '♘'),
    W_PAWN("White Pawn", '♙'),
    B_KING("Black King", '♚'),
    B_QUEEN("Black Queen ", '♛'),
    B_ROOK("Black Rook", '♜'),
    B_BISHOP("Black Bishop", '♝'),
    B_KNIGHT("Black Knight", '♞'),
    B_PAWN("Black Pawn", '♟');

    private char unicodeIcon;
    private String name;

    Pieces(String name, char unicodeIcon) {
        this.name = name;
        this.unicodeIcon = unicodeIcon;
    }

    public char getUnicodeIcon() {
        return unicodeIcon;
    }

    public String getName() {
        return name;
    }
}
