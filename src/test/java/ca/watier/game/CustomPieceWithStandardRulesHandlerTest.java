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

package ca.watier.game;

import ca.watier.GameTest;
import ca.watier.enums.Pieces;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static ca.watier.enums.CasePosition.*;
import static ca.watier.enums.MoveType.MOVE_NOT_ALLOWED;
import static ca.watier.game.CustomPieceWithStandardRulesHandler.THE_NUMBER_OF_PARAMETER_IS_INCORRECT;
import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Created by yannick on 6/20/2017.
 */
public class CustomPieceWithStandardRulesHandlerTest extends GameTest {

    private static final Class<UnsupportedOperationException> UNSUPPORTED_OPERATION_EXCEPTION_CLASS = UnsupportedOperationException.class;
    private CustomPieceWithStandardRulesHandler customPieceWithStandardRulesHandler;

    @Before
    public void setUp() throws Exception {
        customPieceWithStandardRulesHandler = new CustomPieceWithStandardRulesHandler(CONSTRAINT_SERVICE);
    }

    @Test
    public void setPieces() {
        assertThatExceptionOfType(UNSUPPORTED_OPERATION_EXCEPTION_CLASS).isThrownBy(() ->
                customPieceWithStandardRulesHandler.setPieces("B1")).withMessage(THE_NUMBER_OF_PARAMETER_IS_INCORRECT);

        assertThatExceptionOfType(UNSUPPORTED_OPERATION_EXCEPTION_CLASS).isThrownBy(() ->
                customPieceWithStandardRulesHandler.setPieces("B1:W_KING;B2:")).withMessage(THE_NUMBER_OF_PARAMETER_IS_INCORRECT);

        try {
            customPieceWithStandardRulesHandler.setPieces("B1:W_KING;B8:B_KING");
        } catch (UnsupportedOperationException ex) {
            fail();
        }
    }

    @Test
    public void movePieceRevertWhenCheck() {
        customPieceWithStandardRulesHandler.setPieces("E1:W_KING;E8:B_KING;E7:B_ROOK;E2:W_ROOK;C2:B_PAWN;C7:W_PAWN");

        //Cannot move the rook, the king is check
        Assert.assertEquals(MOVE_NOT_ALLOWED, customPieceWithStandardRulesHandler.movePiece(E2, C2, WHITE));
        Assert.assertEquals(MOVE_NOT_ALLOWED, customPieceWithStandardRulesHandler.movePiece(E7, C7, WHITE));

        //Make sure that the attacked pawn was reverted (not deleted from the map)
        Assert.assertEquals(Pieces.B_PAWN, customPieceWithStandardRulesHandler.getPiece(C2));
        Assert.assertEquals(Pieces.W_PAWN, customPieceWithStandardRulesHandler.getPiece(C7));


    }
}