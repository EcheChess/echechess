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

package ca.watier.utils;

import ca.watier.defassert.Assert;
import ca.watier.enums.CasePosition;
import ca.watier.enums.Pieces;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yannick on 4/23/2017.
 */
public class GameUtils extends BaseUtils {

    private static final Map<CasePosition, Pieces> DEFAULT_GAME_TEMPLATE = new HashMap<>();

    static {
        DEFAULT_GAME_TEMPLATE.put(CasePosition.A1, Pieces.W_ROOK);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.B1, Pieces.W_KNIGHT);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.C1, Pieces.W_BISHOP);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.D1, Pieces.W_QUEEN);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.E1, Pieces.W_KING);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.F1, Pieces.W_BISHOP);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.G1, Pieces.W_KNIGHT);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.H1, Pieces.W_ROOK);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.A2, Pieces.W_PAWN);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.B2, Pieces.W_PAWN);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.C2, Pieces.W_PAWN);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.D2, Pieces.W_PAWN);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.E2, Pieces.W_PAWN);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.F2, Pieces.W_PAWN);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.G2, Pieces.W_PAWN);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.H2, Pieces.W_PAWN);


        DEFAULT_GAME_TEMPLATE.put(CasePosition.A8, Pieces.B_ROOK);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.B8, Pieces.B_KNIGHT);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.C8, Pieces.B_BISHOP);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.D8, Pieces.B_QUEEN);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.E8, Pieces.B_KING);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.F8, Pieces.B_BISHOP);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.G8, Pieces.B_KNIGHT);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.H8, Pieces.B_ROOK);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.A7, Pieces.B_PAWN);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.B7, Pieces.B_PAWN);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.C7, Pieces.B_PAWN);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.D7, Pieces.B_PAWN);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.E7, Pieces.B_PAWN);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.F7, Pieces.B_PAWN);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.G7, Pieces.B_PAWN);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.H7, Pieces.B_PAWN);
    }

    public static Map<CasePosition, Pieces> getDefaultGame() {
        Map<CasePosition, Pieces> game = new HashMap<>();
        game.putAll(DEFAULT_GAME_TEMPLATE);
        return game;
    }


    public static boolean isDefaultPosition(CasePosition position, Pieces pieces) {
        Assert.assertNotNull(position, pieces);
        return pieces.equals(DEFAULT_GAME_TEMPLATE.get(position));
    }
}
