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
import static ca.watier.enums.Pieces.W_KNIGHT;
import static ca.watier.enums.SpecialGameRules.*;
import static junit.framework.TestCase.fail;

/**
 * Created by yannick on 5/8/2017.
 */
public class KnightMovesTest {

    private static final Side WHITE = Side.WHITE;
    private static final ConstraintService constraintService = new ConstraintService();


    @Test
    public void moveTest() {
        List<CasePosition> allowedMoves = Arrays.asList(C3, C5, D6, F6, G5, G3, D2, F2);

        Map<CasePosition, Pieces> pieces = new HashMap<>();

        StandardGameHandler gameHandler = new StandardGameHandler(constraintService);
        gameHandler.addSpecialRule(CAN_SET_PIECES, NO_PLAYER_TURN, NO_CHECK_OR_CHECKMATE);
        gameHandler.setPieceLocation(pieces);

        try {
            Utils.addBothPlayerToGameAndSetUUID(gameHandler);

            // Not allowed moves
            for (CasePosition position : CasePosition.values()) {
                pieces.clear();
                pieces.put(E4, W_KNIGHT);

                if (!allowedMoves.contains(position) && !position.equals(E4)) {
                    Assert.assertFalse(gameHandler.movePiece(E4, position, WHITE));
                }
            }

            //Allowed moves
            for (CasePosition position : allowedMoves) {
                pieces.clear();
                pieces.put(E4, W_KNIGHT);

                Assert.assertTrue(gameHandler.movePiece(E4, position, WHITE));
            }


        } catch (GameException e) {
            e.printStackTrace();
            fail();
        }
    }
}
