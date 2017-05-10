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

import java.util.HashMap;
import java.util.Map;

import static ca.watier.enums.CasePosition.*;
import static ca.watier.enums.Pieces.*;
import static ca.watier.enums.SpecialGameRules.CAN_SET_PIECES;
import static ca.watier.enums.SpecialGameRules.NO_CHECK_OR_CHECKMATE;
import static ca.watier.enums.SpecialGameRules.NO_PLAYER_TURN;
import static junit.framework.TestCase.fail;

/**
 * Created by yannick on 5/8/2017.
 */
public class RookMovesTest {

    private static final Side WHITE = Side.WHITE;
    private static final ConstraintService constraintService = new ConstraintService();


    @Test
    public void moveTest() {
        Map<CasePosition, Pieces> pieces = new HashMap<>();

        //Cannot move (blocked in all ways)
        pieces.put(E4, W_ROOK);
        pieces.put(E5, W_PAWN);
        pieces.put(E3, W_PAWN);
        pieces.put(D4, W_PAWN);
        pieces.put(F4, W_PAWN);

        //Kill in all direction
        pieces.put(H1, W_ROOK);
        pieces.put(H8, B_ROOK);
        pieces.put(A8, B_ROOK);
        pieces.put(A1, B_ROOK);
        pieces.put(G1, B_ROOK);

        StandardGameHandler gameHandler = new StandardGameHandler(constraintService);
        gameHandler.addSpecialRule(CAN_SET_PIECES, NO_PLAYER_TURN, NO_CHECK_OR_CHECKMATE);
        gameHandler.setPieceLocation(pieces);

        try {
            Utils.addBothPlayerToGameAndSetUUID(gameHandler);

            //Cannot move (blocked in all ways)
            Assert.assertFalse(gameHandler.movePiece(E4, E8, WHITE));
            Assert.assertFalse(gameHandler.movePiece(E4, E1, WHITE));
            Assert.assertFalse(gameHandler.movePiece(E4, A4, WHITE));
            Assert.assertFalse(gameHandler.movePiece(E4, H4, WHITE));

            //Kill in all direction
            Assert.assertTrue(gameHandler.movePiece(H1, H8, WHITE));
            Assert.assertTrue(gameHandler.movePiece(H8, A8, WHITE));
            Assert.assertTrue(gameHandler.movePiece(A8, A1, WHITE));
            Assert.assertTrue(gameHandler.movePiece(A1, G1, WHITE));

            //cannot move diagonally
            Assert.assertFalse(gameHandler.movePiece(E4, D5, WHITE));
            Assert.assertFalse(gameHandler.movePiece(E4, D3, WHITE));
            Assert.assertFalse(gameHandler.movePiece(E4, F5, WHITE));
            Assert.assertFalse(gameHandler.movePiece(E4, F3, WHITE));

        } catch (GameException e) {
            e.printStackTrace();
            fail();
        }
    }
}
