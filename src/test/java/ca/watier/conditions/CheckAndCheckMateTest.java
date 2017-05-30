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
import ca.watier.enums.KingStatus;
import ca.watier.enums.Side;
import ca.watier.services.ConstraintService;
import org.junit.Assert;
import org.junit.Test;

import static ca.watier.enums.CasePosition.*;
import static ca.watier.enums.Side.BLACK;
import static ca.watier.enums.SpecialGameRules.NO_PLAYER_TURN;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by yannick on 5/9/2017.
 */
public class CheckAndCheckMateTest {

    private static final Side WHITE = Side.WHITE;
    private static final ConstraintService constraintService = new ConstraintService();

    /**
     * In this test, the king should be movable only to E5, F5 & F3
     */
    @Test
    public void checkFromMixShortAndLongRangeWithPawn_multipleExitTest() {
        String positionPieces = "H8:B_KING;E4:W_KING;B5:B_QUEEN;D5:B_PAWN;D4:B_PAWN;D3:B_PAWN;E5:B_PAWN;E3:B_PAWN;F3:B_PAWN;F4:B_PAWN;F5:B_PAWN";
        StandardGameHandlerContext gameHandler = new StandardGameHandlerContext(constraintService, positionPieces);
        gameHandler.addSpecialRule(NO_PLAYER_TURN);

        //gameHandler.getPositionKingCanMove()
        Assert.assertEquals(KingStatus.CHECK, gameHandler.getKingStatus(WHITE));
        assertThat(gameHandler.getPositionKingCanMove(WHITE)).containsOnly(E5, F3, F5);
    }


    /**
     * In this test, the king should be movable only to E5
     */
    @Test
    public void checkFromMixShortAndLongRangeWithPawn_oneExitTest() {
        String positionPieces = "H8:B_KING;E4:W_KING;B5:B_QUEEN;H5:B_QUEEN;E3:B_PAWN;E5:B_PAWN;D4:B_PAWN;F4:B_PAWN;D5:B_PAWN;F5:B_PAWN;D3:B_PAWN;F3:B_PAWN";
        StandardGameHandlerContext gameHandler = new StandardGameHandlerContext(constraintService, positionPieces);
        gameHandler.addSpecialRule(NO_PLAYER_TURN);
        Assert.assertEquals(KingStatus.CHECK, gameHandler.getKingStatus(WHITE));
        assertThat(gameHandler.getPositionKingCanMove(WHITE)).containsOnly(E5);
    }

    /**
     * In this test, the king is checkmate
     */
    @Test
    public void checkFromMixShortAndLongRangeWithPawn_noExitTest() {
        String positionPieces = "H8:B_KING;E4:W_KING;B5:B_QUEEN;H5:B_QUEEN;E7:B_ROOK;E3:B_PAWN;E5:B_PAWN;D4:B_PAWN;F4:B_PAWN;D5:B_PAWN;F5:B_PAWN;D3:B_PAWN;F3:B_PAWN";
        StandardGameHandlerContext gameHandler = new StandardGameHandlerContext(constraintService, positionPieces);
        gameHandler.addSpecialRule(NO_PLAYER_TURN);
        Assert.assertEquals(KingStatus.CHECKMATE, gameHandler.getKingStatus(WHITE));
    }

    /**
     * In this test, the king is checkmate <br>
     * The king is blocked by it's own pawn, and a rook can hit the king <br>
     * This test make sure that the king is not blocking their field of view
     */
    @Test
    public void checkmateFromLongRange_horizontal_Test() {
        String positionPieces = "H8:B_KING;E1:W_KING;H1:B_ROOK;D2:W_PAWN;E2:W_PAWN;F2:W_PAWN";

        StandardGameHandlerContext context = new StandardGameHandlerContext(constraintService, positionPieces);
        context.addSpecialRule(NO_PLAYER_TURN);

        Assert.assertEquals(KingStatus.CHECKMATE, context.getKingStatus(WHITE));
        context.movePieceTo(H1, A1);
        Assert.assertEquals(KingStatus.CHECKMATE, context.getKingStatus(WHITE));
    }


    /**
     * In this test, the king is checkmate <br>
     * The king is blocked by it's own pawn, and a rook can hit the king <br>
     * This test make sure that the king is not blocking their field of view
     */
    @Test
    public void checkmateFromLongRange_vertical_Test() {
        String positionPieces = "H8:B_KING;A4:W_KING;A8:B_ROOK;B3:W_PAWN;B4:W_PAWN;B5:W_PAWN";

        StandardGameHandlerContext context = new StandardGameHandlerContext(constraintService, positionPieces);
        context.addSpecialRule(NO_PLAYER_TURN);

        Assert.assertEquals(KingStatus.CHECKMATE, context.getKingStatus(WHITE));
        context.movePieceTo(A8, A1); //Move the rook
        Assert.assertEquals(KingStatus.CHECKMATE, context.getKingStatus(WHITE));
    }

    @Test
    public void longRangeBlocked_Test() {

        String positionPieces = "G8:B_KING;E4:W_KING;D5:W_PAWN;E5:W_PAWN;F5:W_PAWN;D4:B_PAWN;F4:W_PAWN;D3:B_PAWN;E3:B_PAWN;F3:B_PAWN;E1:B_ROOK;A4:B_ROOK;H4:B_ROOK;A8:B_BISHOP;A1:B_BISHOP;H1:B_BISHOP;H7:B_BISHOP";
        StandardGameHandlerContext context = new StandardGameHandlerContext(constraintService, positionPieces);
        context.addSpecialRule(NO_PLAYER_TURN);

        Assert.assertEquals(KingStatus.OK, context.getKingStatus(WHITE));
    }


    @Test
    public void checkmate_with_pawns_Test() {
        String positionPieces = "D5:B_KING;H8:W_KING;C4:W_PAWN;D4:W_PAWN;E4:W_PAWN;C5:B_PAWN;E5:B_PAWN;C6:B_PAWN;D6:B_PAWN;E6:B_PAWN;B3:W_PAWN;C3:W_PAWN;D3:W_PAWN;E3:W_PAWN;F3:W_PAWN";
        StandardGameHandlerContext context = new StandardGameHandlerContext(constraintService, positionPieces);
        context.addSpecialRule(NO_PLAYER_TURN);

        Assert.assertEquals(KingStatus.CHECKMATE, context.getKingStatus(BLACK));
    }


    /**
     * The king can kill the E4 pawn and remove the check status
     */
    @Test
    public void check_with_pawns_Test() {
        String positionPieces = "D5:B_KING;H8:W_KING;C4:W_PAWN;D4:W_PAWN;E4:W_PAWN;C5:B_PAWN;E5:B_PAWN;C6:B_PAWN;D6:B_PAWN;E6:B_PAWN;B3:W_PAWN;C3:W_PAWN;E3:W_PAWN";
        StandardGameHandlerContext context = new StandardGameHandlerContext(constraintService, positionPieces);
        context.addSpecialRule(NO_PLAYER_TURN);

        Assert.assertEquals(KingStatus.CHECK, context.getKingStatus(BLACK));
    }


    /**
     * In this test, the white king should not be checked by the pawn
     */
    @Test
    public void check_with_pawns_front_move_two_position_Test() {
        String positionPieces = "B7:B_PAWN;B8:B_KING;B5:W_KING";
        StandardGameHandlerContext context = new StandardGameHandlerContext(constraintService, positionPieces);
        context.addSpecialRule(NO_PLAYER_TURN);

        Assert.assertEquals(KingStatus.OK, context.getKingStatus(BLACK));
    }


    /**
     * In this test, the white king should not be checked by the pawn
     */
    @Test
    public void check_with_pawns_front_move_one_position_Test() {
        String positionPieces = "B7:B_PAWN;B8:B_KING;B6:W_KING";
        StandardGameHandlerContext context = new StandardGameHandlerContext(constraintService, positionPieces);
        context.addSpecialRule(NO_PLAYER_TURN);

        Assert.assertEquals(KingStatus.OK, context.getKingStatus(BLACK));
    }


    @Test
    public void checkmate_with_knight_Test() {
        String positionPieces = "E4:W_KING;H8:B_KING;G5:B_KNIGHT;G2:B_KNIGHT;G1:B_KNIGHT;G7:B_KNIGHT;H5:B_KNIGHT;C6:B_KNIGHT;B6:B_KNIGHT;B5:B_KNIGHT;B2:B_KNIGHT;D1:B_KNIGHT;B3:B_KNIGHT";
        StandardGameHandlerContext context = new StandardGameHandlerContext(constraintService, positionPieces);
        context.addSpecialRule(NO_PLAYER_TURN);

        Assert.assertEquals(KingStatus.CHECKMATE, context.getKingStatus(WHITE));
    }

    /**
     * The king is movable to E5
     */
    @Test
    public void check_with_knight_Test() {
        String positionPieces = "E4:W_KING;H8:B_KING;G5:B_KNIGHT;G2:B_KNIGHT;G1:B_KNIGHT;G7:B_KNIGHT;H5:B_KNIGHT;B6:B_KNIGHT;B5:B_KNIGHT;B2:B_KNIGHT;D1:B_KNIGHT;B3:B_KNIGHT";
        StandardGameHandlerContext context = new StandardGameHandlerContext(constraintService, positionPieces);
        context.addSpecialRule(NO_PLAYER_TURN);

        Assert.assertEquals(KingStatus.CHECK, context.getKingStatus(WHITE));
    }

    @Test
    public void checkmate_with_rook_Test() {
        String positionPieces = "E4:W_KING;H8:B_KING;A3:B_ROOK;A5:B_ROOK;A4:B_ROOK";
        StandardGameHandlerContext context = new StandardGameHandlerContext(constraintService, positionPieces);
        context.addSpecialRule(NO_PLAYER_TURN);

        Assert.assertEquals(KingStatus.CHECKMATE, context.getKingStatus(WHITE));
    }

    /**
     * The king can moves to the D4 or F4
     */
    @Test
    public void check_with_rook_Test() {
        String positionPieces = "E4:W_KING;H8:B_KING;A3:B_ROOK;A5:B_ROOK;E8:B_ROOK";
        StandardGameHandlerContext context = new StandardGameHandlerContext(constraintService, positionPieces);
        context.addSpecialRule(NO_PLAYER_TURN);

        Assert.assertEquals(KingStatus.CHECK, context.getKingStatus(WHITE));
    }

}
