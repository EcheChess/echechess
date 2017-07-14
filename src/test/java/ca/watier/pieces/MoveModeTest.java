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
import ca.watier.enums.KingStatus;
import org.junit.Assert;
import org.junit.Test;

import static ca.watier.enums.CasePosition.*;
import static ca.watier.enums.SpecialGameRules.NO_PLAYER_TURN;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by yannick on 5/30/2017.
 */
public class MoveModeTest extends GameTest {

    @Test
    public void rook_Test() {
        String positionPieces = "A8:B_ROOK;A7:B_PAWN;A5:W_KING";
        StandardGameHandlerContext gameHandler = new StandardGameHandlerContext(CONSTRAINT_SERVICE, WEB_SOCKET_SERVICE, positionPieces);
        gameHandler.addSpecialRule(NO_PLAYER_TURN);

        assertThat(gameHandler.getPositionKingCanMove(WHITE)).containsOnly(A4, A6, B5, B4);
        Assert.assertEquals(KingStatus.OK, gameHandler.getKingStatus(WHITE, true));

        gameHandler.removePieceAt(A7); //remove the pawn blocking

        assertThat(gameHandler.getPositionKingCanMove(WHITE)).containsOnly(B6, B5, B4);
        Assert.assertEquals(KingStatus.CHECK, gameHandler.getKingStatus(WHITE, true));

    }


    @Test
    public void knight_Test() {
        String positionPieces = "D5:W_KING;F6:B_KNIGHT;F3:B_KNIGHT";
        StandardGameHandlerContext gameHandler = new StandardGameHandlerContext(CONSTRAINT_SERVICE, WEB_SOCKET_SERVICE, positionPieces);
        gameHandler.addSpecialRule(NO_PLAYER_TURN);

        assertThat(gameHandler.getPositionKingCanMove(WHITE)).containsOnly(C6, D6, E6, C5, C4);
        Assert.assertEquals(KingStatus.CHECK, gameHandler.getKingStatus(WHITE, true));
    }


    @Test
    public void bishop_Test() {
        String positionPieces = "D5:W_KING;C6:B_ROOK;B7:B_BISHOP";
        StandardGameHandlerContext gameHandler = new StandardGameHandlerContext(CONSTRAINT_SERVICE, WEB_SOCKET_SERVICE, positionPieces);
        gameHandler.addSpecialRule(NO_PLAYER_TURN);

        assertThat(gameHandler.getPositionKingCanMove(WHITE)).containsOnly(D4, E4, E5);
        Assert.assertEquals(KingStatus.OK, gameHandler.getKingStatus(WHITE, true));

        gameHandler.removePieceAt(C6); //remove the rook blocking

        assertThat(gameHandler.getPositionKingCanMove(WHITE)).containsOnly(D6, E6, E5, D4, C4, C5);
        Assert.assertEquals(KingStatus.CHECK, gameHandler.getKingStatus(WHITE, true));
    }


    @Test
    public void king_Test() {
        String positionPieces = "D5:W_KING;C6:B_KING";
        StandardGameHandlerContext gameHandler = new StandardGameHandlerContext(CONSTRAINT_SERVICE, WEB_SOCKET_SERVICE, positionPieces);
        gameHandler.addSpecialRule(NO_PLAYER_TURN);

        assertThat(gameHandler.getPositionKingCanMove(WHITE)).containsOnly(C4, D4, E4, E5, E6);
        Assert.assertEquals(KingStatus.CHECK, gameHandler.getKingStatus(WHITE, true));
    }


    @Test
    public void queen_Test() {
        String positionPieces = "D5:W_KING;C6:W_PAWN;B7:B_QUEEN";
        StandardGameHandlerContext gameHandler = new StandardGameHandlerContext(CONSTRAINT_SERVICE, WEB_SOCKET_SERVICE, positionPieces);
        gameHandler.addSpecialRule(NO_PLAYER_TURN);

        assertThat(gameHandler.getPositionKingCanMove(WHITE)).containsOnly(D6, E6, C5, C4, D4, E4, E5);
        Assert.assertEquals(KingStatus.OK, gameHandler.getKingStatus(WHITE, true));

        gameHandler.removePieceAt(C6); //remove the pawn blocking

        assertThat(gameHandler.getPositionKingCanMove(WHITE)).containsOnly(D6, E6, C5, C4, D4, E5);
        Assert.assertEquals(KingStatus.CHECK, gameHandler.getKingStatus(WHITE, true));
    }

    @Test
    public void pawn_front_Test() {
        String positionPieces = "A5:W_KING;A6:B_PAWN;A7:B_PAWN";
        StandardGameHandlerContext gameHandler = new StandardGameHandlerContext(CONSTRAINT_SERVICE, WEB_SOCKET_SERVICE, positionPieces);
        gameHandler.addSpecialRule(NO_PLAYER_TURN);

        assertThat(gameHandler.getPositionKingCanMove(WHITE)).containsOnly(A4, A6, B4);
        Assert.assertEquals(KingStatus.OK, gameHandler.getKingStatus(WHITE, true));

        gameHandler.removePieceAt(A6); //remove the pawn blocking

        assertThat(gameHandler.getPositionKingCanMove(WHITE)).containsOnly(A4, A6, B4, B5);
        Assert.assertEquals(KingStatus.OK, gameHandler.getKingStatus(WHITE, true));

    }

    @Test
    public void pawn_diagonal_Test() {
        String positionPieces = "B7:B_PAWN;C6:W_KING";
        StandardGameHandlerContext gameHandler = new StandardGameHandlerContext(CONSTRAINT_SERVICE, WEB_SOCKET_SERVICE, positionPieces);
        gameHandler.addSpecialRule(NO_PLAYER_TURN);

        assertThat(gameHandler.getPositionKingCanMove(WHITE)).containsOnly(B7, C5, C7, D7, B6, D6, B5, D5);
        Assert.assertEquals(KingStatus.CHECK, gameHandler.getKingStatus(WHITE, true));
    }
}
