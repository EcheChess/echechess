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

package ca.watier;

/**
 * Created by yannick on 5/8/2017.
 */

import ca.watier.enums.CasePosition;
import ca.watier.enums.Pieces;
import ca.watier.enums.Side;
import ca.watier.enums.SpecialGameRules;
import ca.watier.exceptions.GameException;
import ca.watier.game.StandardGameHandler;
import ca.watier.services.ConstraintService;
import ca.watier.testUtils.Utils;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Those tests are meant to test if the rules doesn't interfere with the original game
 */
public class SpecialGameRulesTest {

    private static final Side WHITE = Side.WHITE;
    private static final Side BLACK = Side.BLACK;
    private static final ConstraintService constraintService = new ConstraintService();

    @Test
    public void noPlayerTurnTest() {
        StandardGameHandler gameHandler = new StandardGameHandler(constraintService);

        try {
            Utils.addBothPlayerToGameAndSetUUID(gameHandler);

            assertThat(gameHandler.getSpecialGameRules()).isEmpty(); //Make sure there's no rule applied at the beginning, in a standard game

            //No rules
            Assert.assertTrue(gameHandler.movePiece(CasePosition.H2, CasePosition.H4, WHITE)); //White move
            Assert.assertFalse(gameHandler.movePiece(CasePosition.H4, CasePosition.H5, WHITE)); //White move again, supposed to fail
            Assert.assertTrue(gameHandler.movePiece(CasePosition.H7, CasePosition.H6, BLACK)); //Black move
            Assert.assertFalse(gameHandler.movePiece(CasePosition.H6, CasePosition.H5, WHITE)); //Black move again, supposed to fail

            gameHandler.addSpecialRule(SpecialGameRules.NO_PLAYER_TURN);

            //With the rule
            Assert.assertTrue(gameHandler.movePiece(CasePosition.G2, CasePosition.G4, WHITE)); //White move
            Assert.assertTrue(gameHandler.movePiece(CasePosition.G4, CasePosition.G5, WHITE)); //White move again, supposed to pass (with the rule only)

        } catch (GameException e) {
            e.printStackTrace();
            fail();
        }
    }


    @Test
    public void canSetPiecesTest() {
        StandardGameHandler gameHandler = new StandardGameHandler(constraintService);
        Map<CasePosition, Pieces> emptyMap = new HashMap<>();

        try {
            Utils.addBothPlayerToGameAndSetUUID(gameHandler);
            assertThat(gameHandler.getSpecialGameRules()).isEmpty(); //Make sure there's no rule applied at the beginning, in a standard game

            //No rule
            assertThat(gameHandler.getPiecesLocation()).isNotEmpty();
            gameHandler.setPieceLocation(emptyMap); //Is not set, because we need the CAN_SET_PIECES
            assertThat(gameHandler.getPiecesLocation()).isNotEmpty();

            //With the rule
            gameHandler.addSpecialRule(SpecialGameRules.CAN_SET_PIECES);
            gameHandler.setPieceLocation(emptyMap);
            assertThat(gameHandler.getPiecesLocation()).isEmpty();

        } catch (GameException e) {
            e.printStackTrace();
            fail();
        }
    }

}
