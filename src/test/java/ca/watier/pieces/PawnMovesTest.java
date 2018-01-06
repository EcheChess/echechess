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

import ca.watier.GameTest;
import ca.watier.contexts.StandardGameHandlerContext;
import ca.watier.enums.CasePosition;
import ca.watier.enums.KingStatus;
import ca.watier.enums.Pieces;
import ca.watier.responses.GameScoreResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static ca.watier.enums.CasePosition.*;
import static ca.watier.enums.MoveType.*;
import static ca.watier.enums.Pieces.*;
import static ca.watier.enums.SpecialGameRules.NO_CHECK_OR_CHECKMATE;
import static ca.watier.enums.SpecialGameRules.NO_PLAYER_TURN;

/**
 * Created by yannick on 5/8/2017.
 */
public class PawnMovesTest extends GameTest {


    private StandardGameHandlerContext context;

    @Before
    public void setUp() {
        context = new StandardGameHandlerContext(CONSTRAINT_SERVICE, WEB_SOCKET_SERVICE);
        context.addSpecialRule(NO_PLAYER_TURN);
    }

    /**
     * In this test, the white king should not be checked by the pawn
     */
    @Test
    public void check_with_pawns_front_move_two_position_Test() {
        context.setPieces("B7:B_PAWN;B8:B_KING;B5:W_KING");
        Assert.assertEquals(KingStatus.OK, context.getKingStatus(BLACK, true));
    }


    /**
     * In this test, the white king should not be checked by the pawn
     */
    @Test
    public void check_with_pawns_front_move_one_position_Test() {
        context.setPieces("B7:B_PAWN;B8:B_KING;B6:W_KING");
        Assert.assertEquals(KingStatus.OK, context.getKingStatus(BLACK, true));
    }


    @Test
    public void moveTest() {
        Map<CasePosition, Pieces> pieces = new HashMap<>();

        //Cannot move (front)
        pieces.put(H2, W_PAWN);
        pieces.put(H3, W_ROOK); //Is blocking the H2 pawn
        pieces.put(H7, B_PAWN);
        pieces.put(H6, B_ROOK); //Is blocking the H7 pawn

        //can move
        pieces.put(A2, W_PAWN);
        pieces.put(B2, W_PAWN);
        pieces.put(F2, W_PAWN);
        pieces.put(A7, B_PAWN);
        pieces.put(B7, B_PAWN);
        pieces.put(F7, B_PAWN);

        context.setPieces(pieces);
        context.addSpecialRule(NO_CHECK_OR_CHECKMATE);


        //Cannot move (blocked in front)
        Assert.assertEquals(MOVE_NOT_ALLOWED, context.movePiece(H2, H4, WHITE)); // 2 cases
        Assert.assertEquals(MOVE_NOT_ALLOWED, context.movePiece(H2, H3, WHITE));
        Assert.assertEquals(MOVE_NOT_ALLOWED, context.movePiece(H7, H5, BLACK)); // 2 cases
        Assert.assertEquals(MOVE_NOT_ALLOWED, context.movePiece(H7, H6, BLACK));

        //Can move
        Assert.assertEquals(NORMAL_MOVE, context.movePiece(A2, A4, WHITE)); // 2 cases
        Assert.assertEquals(NORMAL_MOVE, context.movePiece(B2, B3, WHITE));
        Assert.assertEquals(NORMAL_MOVE, context.movePiece(A7, A5, BLACK)); // 2 cases
        Assert.assertEquals(NORMAL_MOVE, context.movePiece(B7, B6, BLACK));

        //Cannot move by 2 position (not on the starting position)
        Assert.assertEquals(MOVE_NOT_ALLOWED, context.movePiece(B3, B5, WHITE));
        Assert.assertEquals(MOVE_NOT_ALLOWED, context.movePiece(B6, B4, WHITE));

        //Can move by one position
        Assert.assertEquals(NORMAL_MOVE, context.movePiece(B3, B4, WHITE));
        Assert.assertEquals(NORMAL_MOVE, context.movePiece(B6, B5, BLACK));

        //cannot move diagonally (without attack)
        Assert.assertEquals(MOVE_NOT_ALLOWED, context.movePiece(F2, E3, WHITE));
        Assert.assertEquals(MOVE_NOT_ALLOWED, context.movePiece(F2, G3, WHITE));
        Assert.assertEquals(MOVE_NOT_ALLOWED, context.movePiece(F2, D4, WHITE)); // 2 cases
        Assert.assertEquals(MOVE_NOT_ALLOWED, context.movePiece(F2, H4, WHITE)); // 2 cases
        Assert.assertEquals(MOVE_NOT_ALLOWED, context.movePiece(F7, E6, BLACK));
        Assert.assertEquals(MOVE_NOT_ALLOWED, context.movePiece(F7, G6, BLACK));
        Assert.assertEquals(MOVE_NOT_ALLOWED, context.movePiece(F7, D5, WHITE)); // 2 cases
        Assert.assertEquals(MOVE_NOT_ALLOWED, context.movePiece(F7, H5, WHITE)); // 2 cases

        //Kill in all direction
        pieces.clear();
        pieces.put(D5, W_PAWN);
        pieces.put(D3, B_PAWN);
        pieces.put(F5, W_PAWN);
        pieces.put(F3, B_PAWN);

        pieces.put(C6, B_PAWN);
        pieces.put(G6, B_PAWN);
        pieces.put(C2, W_PAWN);
        pieces.put(G2, W_PAWN);

        Assert.assertEquals(CAPTURE, context.movePiece(D5, C6, WHITE));
        Assert.assertEquals(CAPTURE, context.movePiece(D3, C2, BLACK));
        Assert.assertEquals(CAPTURE, context.movePiece(F5, G6, WHITE));
        Assert.assertEquals(CAPTURE, context.movePiece(F3, G2, BLACK));
    }

    @Test
    public void enPassantBlackSide() {
        context.movePiece(H2, H4, WHITE);
        context.movePiece(H4, H5, WHITE);
        context.movePiece(G7, G5, BLACK); //Move by 2
        Assert.assertEquals(EN_PASSANT, context.movePiece(H5, G6, WHITE)); // En passant on the black pawn
        Assert.assertEquals(W_PAWN, context.getPiece(G6));
        Assert.assertEquals(new GameScoreResponse((short) 1, (short) 0), context.getGameScore());
    }

    @Test
    public void enPassantWhiteSide() {
        context.movePiece(G7, G5, BLACK);
        context.movePiece(G5, G4, BLACK);
        context.movePiece(H2, H4, WHITE);
        Assert.assertEquals(EN_PASSANT, context.movePiece(G4, H3, BLACK)); // En passant on the white pawn
        Assert.assertEquals(B_PAWN, context.getPiece(H3));
        Assert.assertEquals(new GameScoreResponse((short) 0, (short) 1), context.getGameScore());
    }
}
