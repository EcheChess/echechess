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
import org.junit.Assert;
import org.junit.Test;

import static ca.watier.enums.CasePosition.*;
import static ca.watier.enums.MoveType.MOVE_NOT_ALLOWED;
import static ca.watier.enums.MoveType.NORMAL_MOVE;
import static ca.watier.enums.SpecialGameRules.NO_CHECK_OR_CHECKMATE;
import static ca.watier.enums.SpecialGameRules.NO_PLAYER_TURN;

;

/**
 * Created by yannick on 5/8/2017.
 */
public class RookMovesTest extends GameTest {

    @Test
    public void moveTest() {

        String positionPieces = "E4:W_ROOK;E5:B_PAWN;E3:B_PAWN;F4:B_PAWN;D4:B_PAWN;H1:W_ROOK;H8:B_ROOK;A8:B_ROOK;A1:B_ROOK;G1:B_ROOK";

        StandardGameHandlerContext gameHandler = new StandardGameHandlerContext(constraintService, positionPieces);
        gameHandler.addSpecialRule(NO_PLAYER_TURN, NO_CHECK_OR_CHECKMATE);

        //Cannot move (blocked in all ways)
        Assert.assertEquals(MOVE_NOT_ALLOWED, gameHandler.movePiece(E4, E8, WHITE));
        Assert.assertEquals(MOVE_NOT_ALLOWED, gameHandler.movePiece(E4, E1, WHITE));
        Assert.assertEquals(MOVE_NOT_ALLOWED, gameHandler.movePiece(E4, A4, WHITE));
        Assert.assertEquals(MOVE_NOT_ALLOWED, gameHandler.movePiece(E4, H4, WHITE));

        //Kill in all direction
        Assert.assertEquals(NORMAL_MOVE, gameHandler.movePiece(H1, H8, WHITE));
        Assert.assertEquals(NORMAL_MOVE, gameHandler.movePiece(H8, A8, WHITE));
        Assert.assertEquals(NORMAL_MOVE, gameHandler.movePiece(A8, A1, WHITE));
        Assert.assertEquals(NORMAL_MOVE, gameHandler.movePiece(A1, G1, WHITE));

        //cannot move diagonally
        Assert.assertEquals(MOVE_NOT_ALLOWED, gameHandler.movePiece(E4, D5, WHITE));
        Assert.assertEquals(MOVE_NOT_ALLOWED, gameHandler.movePiece(E4, D3, WHITE));
        Assert.assertEquals(MOVE_NOT_ALLOWED, gameHandler.movePiece(E4, F5, WHITE));
        Assert.assertEquals(MOVE_NOT_ALLOWED, gameHandler.movePiece(E4, F3, WHITE));

    }
}
