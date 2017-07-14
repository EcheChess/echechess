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
import ca.watier.enums.Pieces;
import ca.watier.game.CustomPieceWithStandardRulesHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ca.watier.enums.CasePosition.*;
import static ca.watier.enums.MoveType.*;
import static ca.watier.enums.Pieces.B_PAWN;
import static ca.watier.enums.Pieces.W_KING;
import static ca.watier.enums.SpecialGameRules.NO_CHECK_OR_CHECKMATE;
import static ca.watier.enums.SpecialGameRules.NO_PLAYER_TURN;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by yannick on 5/8/2017.
 */
public class KingMovesTest extends GameTest {


    private CustomPieceWithStandardRulesHandler gameHandler;

    @Before
    public void setUp() throws Exception {
        gameHandler = new CustomPieceWithStandardRulesHandler(CONSTRAINT_SERVICE, WEB_SOCKET_SERVICE);
        gameHandler.addSpecialRule(NO_PLAYER_TURN);
    }

    @Test
    public void moveTest() {
        List<CasePosition> allowedMoves = Arrays.asList(A8, C8, A6, C6, B8, B6, C7, A7);
        Map<CasePosition, Pieces> pieces = new HashMap<>();
        pieces.put(B7, W_KING);

        StandardGameHandlerContext gameHandler = new StandardGameHandlerContext(CONSTRAINT_SERVICE, WEB_SOCKET_SERVICE, pieces);
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

            Assert.assertEquals(NORMAL_MOVE, gameHandler.movePiece(B7, position, WHITE));
        }

    }

    @Test
    public void validPathCastlingTest() {
        gameHandler.setPieces("E8:B_KING;E1:W_KING;A8:B_ROOK;H8:B_ROOK;H1:W_ROOK;A1:W_ROOK");

        Assert.assertEquals(CASTLING, gameHandler.movePiece(E1, A1, WHITE)); //Queen side
        Assert.assertEquals(CASTLING, gameHandler.movePiece(E8, H8, BLACK)); //Normal castling

        assertThat(gameHandler.getPiecesLocation()).isNotNull().containsKeys(A8, F8, G8, C1, D1, H1);
    }


    @Test
    public void attackedBothPathCastlingTest() {
        gameHandler.setPieces("E8:B_KING;E1:W_KING;A1:W_ROOK;A8:B_ROOK;H8:B_ROOK;H1:W_ROOK;B2:B_ROOK;G7:W_ROOK;C7:W_ROOK;F2:B_ROOK");

        Assert.assertEquals(MOVE_NOT_ALLOWED, gameHandler.movePiece(E1, A1, WHITE)); //Queen side
        Assert.assertEquals(MOVE_NOT_ALLOWED, gameHandler.movePiece(E1, H1, WHITE)); //Normal castling
        Assert.assertEquals(MOVE_NOT_ALLOWED, gameHandler.movePiece(E8, H8, BLACK)); //Normal castling
        Assert.assertEquals(MOVE_NOT_ALLOWED, gameHandler.movePiece(E8, A8, BLACK)); //Queen side
    }


    @Test
    public void attackedQueenSidePathCastlingTest() {
        gameHandler.setPieces("E8:B_KING;E1:W_KING;A1:W_ROOK;A8:B_ROOK;H8:B_ROOK;H1:W_ROOK;B2:B_ROOK;E2:W_PAWN;F2:W_PAWN;G2:W_PAWN;H2:W_PAWN;E7:B_PAWN;F7:B_PAWN;G7:B_PAWN;H7:B_PAWN;B7:W_ROOK");

        Assert.assertEquals(MOVE_NOT_ALLOWED, gameHandler.movePiece(E1, A1, WHITE)); //Queen side
        Assert.assertEquals(MOVE_NOT_ALLOWED, gameHandler.movePiece(E8, A8, BLACK)); //Queen side
        Assert.assertEquals(CASTLING, gameHandler.movePiece(E1, H1, WHITE)); //Normal castling
        Assert.assertEquals(CASTLING, gameHandler.movePiece(E8, H8, BLACK)); //Normal castling

        Assert.assertEquals(Pieces.W_KING, gameHandler.getPiece(G1));
        Assert.assertEquals(Pieces.W_ROOK, gameHandler.getPiece(F1));
        Assert.assertEquals(Pieces.B_KING, gameHandler.getPiece(G8));
        Assert.assertEquals(Pieces.B_ROOK, gameHandler.getPiece(F8));
    }


    @Test
    public void checkAtBeginningPositionQueenSidePathCastlingTest() {
        gameHandler.setPieces("E8:B_KING;E1:W_KING;A1:W_ROOK;A8:B_ROOK;H8:B_ROOK;H1:W_ROOK;F2:W_PAWN;G2:W_PAWN;H2:W_PAWN;F7:B_PAWN;G7:B_PAWN;H7:B_PAWN;E4:B_ROOK;E5:W_ROOK");

        Assert.assertEquals(MOVE_NOT_ALLOWED, gameHandler.movePiece(E1, A1, WHITE)); //Queen side
        Assert.assertEquals(MOVE_NOT_ALLOWED, gameHandler.movePiece(E8, A8, BLACK)); //Queen side
        Assert.assertEquals(MOVE_NOT_ALLOWED, gameHandler.movePiece(E1, H1, WHITE)); //Normal castling
        Assert.assertEquals(MOVE_NOT_ALLOWED, gameHandler.movePiece(E8, H8, BLACK)); //Normal castling
    }


    @Test
    public void checkAtEndingPositionQueenSidePathCastlingTest() {
        gameHandler.setPieces("E8:B_KING;E1:W_KING;A1:W_ROOK;A8:B_ROOK;H8:B_ROOK;H1:W_ROOK;E2:W_PAWN;F2:W_PAWN;G2:W_PAWN;H2:W_PAWN;E7:B_PAWN;F7:B_PAWN;G7:B_PAWN;H7:B_PAWN;C7:W_ROOK;C2:B_ROOK");

        Assert.assertEquals(MOVE_NOT_ALLOWED, gameHandler.movePiece(E1, A1, WHITE)); //Queen side
        Assert.assertEquals(MOVE_NOT_ALLOWED, gameHandler.movePiece(E8, A8, BLACK)); //Queen side
        Assert.assertEquals(CASTLING, gameHandler.movePiece(E1, H1, WHITE)); //Normal castling
        Assert.assertEquals(CASTLING, gameHandler.movePiece(E8, H8, BLACK)); //Normal castling

        Assert.assertEquals(Pieces.W_KING, gameHandler.getPiece(G1));
        Assert.assertEquals(Pieces.W_ROOK, gameHandler.getPiece(F1));
        Assert.assertEquals(Pieces.B_KING, gameHandler.getPiece(G8));
        Assert.assertEquals(Pieces.B_ROOK, gameHandler.getPiece(F8));
    }

    @Test
    public void blockingPieceQueenSidePathCastlingTest() {
        gameHandler.setPieces("E8:B_KING;E1:W_KING;A1:W_ROOK;A8:B_ROOK;H8:B_ROOK;H1:W_ROOK;E2:W_PAWN;F2:W_PAWN;G2:W_PAWN;H2:W_PAWN;E7:B_PAWN;F7:B_PAWN;G7:B_PAWN;H7:B_PAWN;D8:B_PAWN;D1:W_PAWN");


        Assert.assertEquals(MOVE_NOT_ALLOWED, gameHandler.movePiece(E1, A1, WHITE)); //Queen side
        Assert.assertEquals(MOVE_NOT_ALLOWED, gameHandler.movePiece(E8, A8, BLACK)); //Queen side
        Assert.assertEquals(CASTLING, gameHandler.movePiece(E1, H1, WHITE)); //Normal castling
        Assert.assertEquals(CASTLING, gameHandler.movePiece(E8, H8, BLACK)); //Normal castling

        Assert.assertEquals(Pieces.W_KING, gameHandler.getPiece(G1));
        Assert.assertEquals(Pieces.W_ROOK, gameHandler.getPiece(F1));
        Assert.assertEquals(Pieces.B_KING, gameHandler.getPiece(G8));
        Assert.assertEquals(Pieces.B_ROOK, gameHandler.getPiece(F8));
    }


    @Test
    public void movedPiecesQueenSidePathCastlingTest() {
        gameHandler.setPieces("E8:B_KING;E1:W_KING;A1:W_ROOK;A8:B_ROOK;H8:B_ROOK;H1:W_ROOK;E2:W_PAWN;F2:W_PAWN;G2:W_PAWN;H2:W_PAWN;E7:B_PAWN;F7:B_PAWN;G7:B_PAWN;H7:B_PAWN");

        //Move the white king
        gameHandler.movePiece(E1, D2, WHITE);
        //Move the white king to the original position
        gameHandler.movePiece(D2, E1, WHITE);

        //Move the black rook
        gameHandler.movePiece(A8, A7, BLACK);
        //Move the black rook to the original position
        gameHandler.movePiece(A7, A8, BLACK);

        Assert.assertEquals(MOVE_NOT_ALLOWED, gameHandler.movePiece(E1, A1, WHITE)); //Queen side
        Assert.assertEquals(MOVE_NOT_ALLOWED, gameHandler.movePiece(E8, A8, BLACK)); //Queen side
    }
}
