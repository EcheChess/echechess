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

package ca.watier.conditions;

import ca.watier.contexts.StandardGameHandlerContext;
import ca.watier.enums.CasePosition;
import ca.watier.enums.KingStatus;
import ca.watier.enums.Pieces;
import ca.watier.enums.Side;
import ca.watier.exceptions.GameException;
import ca.watier.services.ConstraintService;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static ca.watier.enums.CasePosition.*;
import static ca.watier.enums.Pieces.*;
import static ca.watier.enums.Side.BLACK;
import static ca.watier.enums.SpecialGameRules.NO_PLAYER_TURN;
import static junit.framework.TestCase.fail;

/**
 * Created by yannick on 5/9/2017.
 */
public class CheckAndCheckMateTest {

    private static final Side WHITE = Side.WHITE;
    private static final ConstraintService constraintService = new ConstraintService();

    @Test
    public void checkMateFromLongRangeTest() {
        Map<CasePosition, Pieces> pieces = new HashMap<>();
        pieces.put(H8, B_KING);
        pieces.put(E1, W_KING);
        pieces.put(E4, B_ROOK);
        pieces.put(D3, B_ROOK);
        pieces.put(F3, B_ROOK);

        StandardGameHandlerContext gameHandler = new StandardGameHandlerContext(constraintService, pieces);
        gameHandler.addSpecialRule(NO_PLAYER_TURN);
        Assert.assertEquals(KingStatus.CHECKMATE, gameHandler.getKingStatus(E1, WHITE));
        Assert.assertEquals(KingStatus.OK, gameHandler.getKingStatus(H8, BLACK));

        //Move the E4, no more check mate
        pieces.remove(E4);
        pieces.put(D4, B_ROOK);
        Assert.assertEquals(KingStatus.OK, gameHandler.getKingStatus(E1, WHITE));
        pieces.put(A1, B_ROOK);
        Assert.assertEquals(KingStatus.CHECK, gameHandler.getKingStatus(E1, WHITE)); //Can move to E2
        pieces.put(A2, B_ROOK);
        Assert.assertEquals(KingStatus.CHECKMATE, gameHandler.getKingStatus(E1, WHITE));

    }


    /**
     * In this test, the king should be movable only to D3, F3, D5, E5 & F5
     */
    @Test
    public void checkFromShortRangeWithPawnTest() {
        List<CasePosition> allPosition = new ArrayList<>();
        allPosition.addAll(Arrays.asList(E3, E5, D4, F4, D5, F5, D3, F3));

        Map<CasePosition, Pieces> pieces = new HashMap<>();

        StandardGameHandlerContext gameHandler = new StandardGameHandlerContext(constraintService);
        gameHandler.addSpecialRule(NO_PLAYER_TURN);
        try {
            for (CasePosition position : allPosition) {
                pieces.clear();
                pieces.put(H8, B_KING);
                pieces.put(E4, W_KING);
                pieces.put(E3, B_PAWN);
                pieces.put(E5, B_PAWN);
                pieces.put(D4, B_PAWN);
                pieces.put(F4, B_PAWN);
                pieces.put(D5, B_PAWN);
                pieces.put(F5, B_PAWN);
                pieces.put(D3, B_PAWN);
                pieces.put(F3, B_PAWN);
                gameHandler.setPieces(pieces);

                if (position.equals(D3) || position.equals(D5) || position.equals(E5) || position.equals(F3) || position.equals(F5)) {
                    Assert.assertEquals(KingStatus.OK, gameHandler.getKingStatus(position, WHITE));
                } else {
                    Assert.assertEquals(KingStatus.CHECK, gameHandler.getKingStatus(E4, WHITE));
                    Assert.assertFalse(gameHandler.movePiece(E4, position, WHITE));  //Cannot move, will be check again (Need an exception to be valid)
                }
            }

            Assert.assertEquals(KingStatus.CHECK, gameHandler.getKingStatus(E4, WHITE));
        } catch (GameException e) {
            e.printStackTrace();
            fail();
        }
    }


    /**
     * In this test, the king should be movable only to E5, F3 & D3
     */
    @Test
    public void checkFromMixShortAndLongRangeWithPawn_multipleExitTest() {

        Map<CasePosition, Pieces> pieces = new HashMap<>();
        pieces.put(H8, B_KING);
        pieces.put(E4, W_KING);
        pieces.put(B5, B_QUEEN); //Prevent the king to move to D5 & D3
        pieces.put(E3, B_PAWN);
        pieces.put(E5, B_PAWN);
        pieces.put(D4, B_PAWN);
        pieces.put(F4, B_PAWN);
        pieces.put(D5, B_PAWN);
        pieces.put(F5, B_PAWN);
        pieces.put(D3, B_PAWN);
        pieces.put(F3, B_PAWN);

        StandardGameHandlerContext gameHandler = new StandardGameHandlerContext(constraintService, pieces);
        gameHandler.addSpecialRule(NO_PLAYER_TURN);
        Assert.assertEquals(KingStatus.CHECK, gameHandler.getKingStatus(E4, WHITE));
    }


    /**
     * In this test, the king should be movable only to E5
     */
    @Test
    public void checkFromMixShortAndLongRangeWithPawn_oneExitTest() {
        Map<CasePosition, Pieces> pieces = new HashMap<>();
        pieces.put(H8, B_KING);
        pieces.put(E4, W_KING);
        pieces.put(B5, B_QUEEN); //Prevent the king to move to D5 & D3
        pieces.put(H5, B_QUEEN); //Prevent the king to move to F5 & F3
        pieces.put(E3, B_PAWN);
        pieces.put(E5, B_PAWN);
        pieces.put(D4, B_PAWN);
        pieces.put(F4, B_PAWN);
        pieces.put(D5, B_PAWN);
        pieces.put(F5, B_PAWN);
        pieces.put(D3, B_PAWN);
        pieces.put(F3, B_PAWN);

        StandardGameHandlerContext gameHandler = new StandardGameHandlerContext(constraintService, pieces);
        gameHandler.addSpecialRule(NO_PLAYER_TURN);
        Assert.assertEquals(KingStatus.CHECK, gameHandler.getKingStatus(E4, WHITE));
    }


    /**
     * In this test, the king is checkmate
     */
    @Test
    public void checkmateFromMixShortAndLongRangeWithPawn_noAllyTest() {
        Map<CasePosition, Pieces> pieces = new HashMap<>();
        pieces.put(H8, B_KING);
        pieces.put(E4, W_KING);
        pieces.put(B5, B_QUEEN); //Prevent the king to move to D5 & D3
        pieces.put(H5, B_QUEEN); //Prevent the king to move to F5 & F3
        pieces.put(E7, B_ROOK);  //Prevent the king to move to E5
        pieces.put(E3, B_PAWN);
        pieces.put(E5, B_PAWN);
        pieces.put(D4, B_PAWN);
        pieces.put(F4, B_PAWN);
        pieces.put(D5, B_PAWN);
        pieces.put(F5, B_PAWN);
        pieces.put(D3, B_PAWN);
        pieces.put(F3, B_PAWN);

        StandardGameHandlerContext gameHandler = new StandardGameHandlerContext(constraintService, pieces);
        gameHandler.addSpecialRule(NO_PLAYER_TURN);
        Assert.assertEquals(KingStatus.CHECKMATE, gameHandler.getKingStatus(E4, WHITE));
    }

    /**
     * In this test, the king is checkmate <br>
     * The king is blocked by it's own pawn, and a rook can hit the king <br>
     * This test make sure that the king is not blocking their field of view
     */
    @Test
    public void checkmateFromLongRange_horizontal_Test() {
        Map<CasePosition, Pieces> piecesContext = new HashMap<>();
        piecesContext.put(H8, B_KING);
        piecesContext.put(E1, W_KING);
        piecesContext.put(H1, B_ROOK);

        //Block the king
        piecesContext.put(A2, W_PAWN);
        piecesContext.put(B2, W_PAWN);
        piecesContext.put(C2, W_PAWN);
        piecesContext.put(D2, W_PAWN);
        piecesContext.put(E2, W_PAWN);
        piecesContext.put(F2, W_PAWN);
        piecesContext.put(G2, W_PAWN);
        piecesContext.put(H2, W_PAWN);

        StandardGameHandlerContext context = new StandardGameHandlerContext(constraintService, piecesContext);
        context.addSpecialRule(NO_PLAYER_TURN);

        Assert.assertEquals(KingStatus.CHECKMATE, context.getKingStatus(E1, WHITE));

        piecesContext.remove(H1);
        piecesContext.put(A1, B_ROOK);
        Assert.assertEquals(KingStatus.CHECKMATE, context.getKingStatus(E1, WHITE));
    }


    /**
     * In this test, the king is checkmate <br>
     * The king is blocked by it's own pawn, and a rook can hit the king <br>
     * This test make sure that the king is not blocking their field of view
     */
    @Test
    public void checkmateFromLongRange_vertical_Test() {
        Map<CasePosition, Pieces> piecesContext = new HashMap<>();
        piecesContext.put(H8, B_KING);
        piecesContext.put(A4, W_KING);
        piecesContext.put(A8, B_ROOK);

        //Block the king
        piecesContext.put(B3, W_PAWN);
        piecesContext.put(B4, W_PAWN);
        piecesContext.put(B5, W_PAWN);

        StandardGameHandlerContext context = new StandardGameHandlerContext(constraintService, piecesContext);
        context.addSpecialRule(NO_PLAYER_TURN);

        Assert.assertEquals(KingStatus.CHECKMATE, context.getKingStatus(A4, WHITE));

        piecesContext.remove(A8);
        piecesContext.put(A1, B_ROOK);
        Assert.assertEquals(KingStatus.CHECKMATE, context.getKingStatus(A4, WHITE));
    }

}
