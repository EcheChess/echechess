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

import ca.watier.enums.CasePosition;
import ca.watier.enums.Pieces;
import ca.watier.enums.Side;
import ca.watier.exceptions.GameException;
import ca.watier.game.StandardGameHandler;
import ca.watier.services.ConstraintService;
import ca.watier.testUtils.Utils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ca.watier.enums.CasePosition.*;
import static ca.watier.enums.Pieces.*;
import static ca.watier.enums.SpecialGameRules.CAN_SET_PIECES;
import static ca.watier.enums.SpecialGameRules.NO_PLAYER_TURN;
import static junit.framework.TestCase.fail;

/**
 * Created by yannick on 5/8/2017.
 */
public class BishopMovesTest {

    private static final Side WHITE = Side.WHITE;
    private static final ConstraintService constraintService = new ConstraintService();


    @Test
    public void moveTest() {
        List<CasePosition> allowedMoves = Arrays.asList(A8, C8, A6, C6);
        Map<CasePosition, Pieces> pieces = new HashMap<>();
        pieces.put(E2, W_KING);
        pieces.put(E7, B_KING);

        //Cannot move (blocked in all ways)
        pieces.put(E4, W_BISHOP);
        pieces.put(D5, W_PAWN);
        pieces.put(D3, W_PAWN);
        pieces.put(F5, W_PAWN);
        pieces.put(F3, W_PAWN);

        StandardGameHandler gameHandler = new StandardGameHandler(constraintService);
        gameHandler.addSpecialRule(CAN_SET_PIECES, NO_PLAYER_TURN);
        gameHandler.setPieceLocation(pieces);

        try {
            Utils.addBothPlayerToGameAndSetUUID(gameHandler);


            //Cannot move (blocked in all ways)
            Assert.assertFalse(gameHandler.movePiece(E4, C6, WHITE));
            Assert.assertFalse(gameHandler.movePiece(E4, G6, WHITE));
            Assert.assertFalse(gameHandler.movePiece(E4, C2, WHITE));
            Assert.assertFalse(gameHandler.movePiece(E4, G2, WHITE));


            //Kill in all direction
            for (CasePosition position : allowedMoves) {
                pieces.clear();
                pieces.put(E2, W_KING);
                pieces.put(E7, B_KING);
                pieces.put(B7, W_BISHOP);
                pieces.put(A8, B_ROOK);
                pieces.put(C8, B_ROOK);
                pieces.put(A6, B_ROOK);
                pieces.put(C6, B_ROOK);

                Assert.assertTrue(gameHandler.movePiece(B7, position, WHITE));
            }
        } catch (GameException e) {
            e.printStackTrace();
            fail();
        }
    }
}
