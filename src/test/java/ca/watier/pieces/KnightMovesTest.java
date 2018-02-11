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
import ca.watier.echechessengine.contexts.StandardGameHandlerContext;
import ca.watier.echesscommon.enums.CasePosition;
import ca.watier.echesscommon.enums.Pieces;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ca.watier.echesscommon.enums.CasePosition.*;
import static ca.watier.echesscommon.enums.MoveType.MOVE_NOT_ALLOWED;
import static ca.watier.echesscommon.enums.MoveType.NORMAL_MOVE;
import static ca.watier.echesscommon.enums.Pieces.*;
import static ca.watier.echesscommon.enums.SpecialGameRules.NO_CHECK_OR_CHECKMATE;
import static ca.watier.echesscommon.enums.SpecialGameRules.NO_PLAYER_TURN;

/**
 * Created by yannick on 5/8/2017.
 */
public class KnightMovesTest extends GameTest {


    @Test
    public void moveTest() {
        List<CasePosition> allowedMoves = Arrays.asList(C3, C5, D6, F6, G5, G3, D2, F2);

        Map<CasePosition, Pieces> pieces = new HashMap<>();

        StandardGameHandlerContext gameHandler = new StandardGameHandlerContext(CONSTRAINT_SERVICE, WEB_SOCKET_SERVICE);
        gameHandler.addSpecialRule(NO_PLAYER_TURN, NO_CHECK_OR_CHECKMATE);

        // Not allowed moves
        for (CasePosition position : CasePosition.values()) {
            pieces.clear();
            pieces.put(E4, W_KNIGHT);
            gameHandler.setPieces(pieces);

            if (!allowedMoves.contains(position) && !position.equals(E4)) {
                Assert.assertEquals(MOVE_NOT_ALLOWED, gameHandler.movePiece(E4, position, WHITE));
            }
        }

        //Allowed moves
        for (CasePosition position : allowedMoves) {
            pieces.clear();
            pieces.put(E4, W_KNIGHT);
            gameHandler.setPieces(pieces);

            Assert.assertEquals(NORMAL_MOVE, gameHandler.movePiece(E4, position, WHITE));
        }

        //Cannot attack a friendly or a king
        pieces.put(A8, W_KNIGHT);
        pieces.put(C7, B_KING);
        pieces.put(B6, W_KING);
        Assert.assertEquals(MOVE_NOT_ALLOWED, gameHandler.movePiece(A8, C7, WHITE));
        Assert.assertEquals(MOVE_NOT_ALLOWED, gameHandler.movePiece(A8, B6, WHITE));

    }
}
