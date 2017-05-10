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

import ca.watier.enums.CasePosition;
import ca.watier.enums.Pieces;
import ca.watier.enums.Side;
import ca.watier.exceptions.GameException;
import ca.watier.exceptions.KingCheckException;
import ca.watier.game.StandardGameHandler;
import ca.watier.services.ConstraintService;
import ca.watier.testUtils.Utils;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static ca.watier.enums.CasePosition.*;
import static ca.watier.enums.Pieces.*;
import static ca.watier.enums.Side.BLACK;
import static ca.watier.enums.SpecialGameRules.CAN_SET_PIECES;
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

        StandardGameHandler gameHandler = new StandardGameHandler(constraintService);
        gameHandler.addSpecialRule(CAN_SET_PIECES, NO_PLAYER_TURN);
        try {
            Utils.addBothPlayerToGameAndSetUUID(gameHandler);
            gameHandler.setPieceLocation(pieces);

            Assert.assertTrue(gameHandler.isKingCheck(E1, WHITE));
            Assert.assertTrue(gameHandler.isKingCheckMate(E1, WHITE));
            Assert.assertFalse(gameHandler.isKingCheck(H8, BLACK));
            Assert.assertFalse(gameHandler.isKingCheckMate(H8, BLACK));

            //Move the E4, no more check mate
            pieces.remove(E4);
            pieces.put(D4, B_ROOK);
            Assert.assertFalse(gameHandler.isKingCheck(E1, WHITE));
            Assert.assertFalse(gameHandler.isKingCheckMate(E1, WHITE));
            pieces.put(A1, B_ROOK);
            Assert.assertTrue(gameHandler.isKingCheck(E1, WHITE));
            Assert.assertFalse(gameHandler.isKingCheckMate(E1, WHITE)); //Can move to E2
            pieces.put(A2, B_ROOK);
            Assert.assertTrue(gameHandler.isKingCheck(E1, WHITE));
            Assert.assertTrue(gameHandler.isKingCheckMate(E1, WHITE));

        } catch (GameException e) {
            e.printStackTrace();
            fail();
        }
    }


    /**
     * In this test, the king should be movable only to D3, F3, D5, E5 & F5
     */
    @Test
    public void checkFromShortRangeWithPawnTest() {
        List<CasePosition> allPosition = new ArrayList<>();
        allPosition.addAll(Arrays.asList(E3, E5, D4, F4, D5, F5, D3, F3));

        Map<CasePosition, Pieces> pieces = new HashMap<>();

        StandardGameHandler gameHandler = new StandardGameHandler(constraintService);
        gameHandler.addSpecialRule(CAN_SET_PIECES, NO_PLAYER_TURN);
        try {
            Utils.addBothPlayerToGameAndSetUUID(gameHandler);
            gameHandler.setPieceLocation(pieces);

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

                if (position.equals(D3) || position.equals(D5) || position.equals(E5) || position.equals(F3) || position.equals(F5)) {
                    Assert.assertFalse(gameHandler.isKingCheck(position, WHITE));
                    Assert.assertFalse(gameHandler.isKingCheckMate(position, WHITE));
                } else {
                    try {
                        Assert.assertTrue(gameHandler.isKingCheck(position, WHITE));
                        gameHandler.movePiece(E4, position, WHITE);
                        fail(); //Cannot move, will be check again (Need an exception to be valid)
                    } catch (KingCheckException ignored) {
                    }
                }
            }

            Assert.assertTrue(gameHandler.isKingCheck(E4, WHITE));
            Assert.assertFalse(gameHandler.isKingCheckMate(E4, WHITE));
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
        List<CasePosition> allPosition = new ArrayList<>();
        allPosition.addAll(Arrays.asList(E3, E5, D4, F4, D5, F5, D3, F3));

        Map<CasePosition, Pieces> pieces = new HashMap<>();

        StandardGameHandler gameHandler = new StandardGameHandler(constraintService);
        gameHandler.addSpecialRule(CAN_SET_PIECES, NO_PLAYER_TURN);
        try {
            Utils.addBothPlayerToGameAndSetUUID(gameHandler);
            gameHandler.setPieceLocation(pieces);

            for (CasePosition position : allPosition) {
                pieces.clear();
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

                if (position.equals(E5) || position.equals(F3) || position.equals(F5)) {
                    Assert.assertFalse(gameHandler.isKingCheck(position, WHITE));
                    Assert.assertFalse(gameHandler.isKingCheckMate(position, WHITE));
                } else {
                    try {
                        Assert.assertTrue(gameHandler.isKingCheck(position, WHITE));
                        gameHandler.movePiece(E4, position, WHITE);
                        fail(); //Cannot move, will be check again (Need an exception to be valid)
                    } catch (KingCheckException ignored) {
                    }
                }
            }

            Assert.assertTrue(gameHandler.isKingCheck(E4, WHITE));
            Assert.assertFalse(gameHandler.isKingCheckMate(E4, WHITE));
        } catch (GameException e) {
            e.printStackTrace();
            fail();
        }
    }


    /**
     * In this test, the king should be movable only to E5
     */
    @Test
    public void checkFromMixShortAndLongRangeWithPawn_oneExitTest() {
        List<CasePosition> allPosition = new ArrayList<>();
        allPosition.addAll(Arrays.asList(E3, E5, D4, F4, D5, F5, D3, F3));

        Map<CasePosition, Pieces> pieces = new HashMap<>();

        StandardGameHandler gameHandler = new StandardGameHandler(constraintService);
        gameHandler.addSpecialRule(CAN_SET_PIECES, NO_PLAYER_TURN);
        try {
            Utils.addBothPlayerToGameAndSetUUID(gameHandler);
            gameHandler.setPieceLocation(pieces);

            for (CasePosition position : allPosition) {
                pieces.clear();
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

                if (position.equals(E5)) {
                    Assert.assertFalse(gameHandler.isKingCheck(position, WHITE));
                    Assert.assertFalse(gameHandler.isKingCheckMate(position, WHITE));
                } else {
                    try {
                        Assert.assertTrue(gameHandler.isKingCheck(position, WHITE));
                        gameHandler.movePiece(E4, position, WHITE);
                        fail(); //Cannot move, will be check again (Need an exception to be valid)
                    } catch (KingCheckException ignored) {
                    }
                }
            }

            Assert.assertTrue(gameHandler.isKingCheck(E4, WHITE));
            Assert.assertFalse(gameHandler.isKingCheckMate(E4, WHITE));
        } catch (GameException e) {
            e.printStackTrace();
            fail();
        }
    }


    /**
     * In this test, the king is checkmate
     */
    @Test
    public void checkmateFromMixShortAndLongRangeWithPawn_noAllyTest() {
        List<CasePosition> allPosition = new ArrayList<>();
        allPosition.addAll(Arrays.asList(E3, E5, D4, F4, D5, F5, D3, F3));

        Map<CasePosition, Pieces> pieces = new HashMap<>();

        StandardGameHandler gameHandler = new StandardGameHandler(constraintService);
        gameHandler.addSpecialRule(CAN_SET_PIECES, NO_PLAYER_TURN);
        try {
            Utils.addBothPlayerToGameAndSetUUID(gameHandler);
            gameHandler.setPieceLocation(pieces);

            for (CasePosition position : allPosition) {
                pieces.clear();
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

                try {
                    Assert.assertTrue(gameHandler.isKingCheck(position, WHITE));
                    gameHandler.movePiece(E4, position, WHITE);
                    fail(); //Cannot move, will be check again (Need an exception to be valid)
                } catch (KingCheckException ignored) {
                }
            }

            Assert.assertTrue(gameHandler.isKingCheckMate(E4, WHITE));
        } catch (GameException e) {
            e.printStackTrace();
            fail();
        }
    }

}
