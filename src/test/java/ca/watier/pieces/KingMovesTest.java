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

package ca.watier.pieces;

import ca.watier.contexts.StandardGameHandlerContext;
import ca.watier.enums.CasePosition;
import ca.watier.enums.Pieces;
import ca.watier.enums.Side;
import ca.watier.services.ConstraintService;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ca.watier.enums.CasePosition.*;
import static ca.watier.enums.Pieces.B_PAWN;
import static ca.watier.enums.Pieces.W_KING;
import static ca.watier.enums.SpecialGameRules.NO_CHECK_OR_CHECKMATE;
import static ca.watier.enums.SpecialGameRules.NO_PLAYER_TURN;
import static junit.framework.TestCase.fail;

/**
 * Created by yannick on 5/8/2017.
 */
public class KingMovesTest {

    private static final Side WHITE = Side.WHITE;
    private static final ConstraintService constraintService = new ConstraintService();


    @Test
    public void moveTest() {
        List<CasePosition> allowedMoves = Arrays.asList(A8, C8, A6, C6, B8, B6, C7, A7);
        Map<CasePosition, Pieces> pieces = new HashMap<>();
        pieces.put(B7, W_KING);

        StandardGameHandlerContext gameHandler = new StandardGameHandlerContext(constraintService, pieces);
        gameHandler.addSpecialRule(NO_PLAYER_TURN, NO_CHECK_OR_CHECKMATE);

        //Kill in all direction
        for (CasePosition position : allowedMoves) {
            pieces.clear();
            pieces.put(B7, W_KING);
            pieces.put(A8, B_PAWN);
            pieces.put(C8, B_PAWN);
            pieces.put(A6, B_PAWN);
            pieces.put(C6, B_PAWN);
            pieces.put(B8, B_PAWN);
            pieces.put(B6, B_PAWN);
            pieces.put(C7, B_PAWN);
            pieces.put(A7, B_PAWN);

            Assert.assertTrue(gameHandler.movePiece(B7, position, WHITE));
        }

    }
}
