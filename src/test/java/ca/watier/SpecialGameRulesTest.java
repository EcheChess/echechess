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

import ca.watier.contexts.StandardGameHandlerContext;
import ca.watier.enums.KingStatus;
import ca.watier.enums.Side;
import ca.watier.enums.SpecialGameRules;
import ca.watier.game.StandardGameHandler;
import ca.watier.services.ConstraintService;
import ca.watier.sessions.Player;
import org.junit.Assert;
import org.junit.Test;

import static ca.watier.enums.CasePosition.*;
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

        assertThat(gameHandler.getSpecialGameRules()).isEmpty(); //Make sure there's no rule applied at the beginning, in a standard game

        //No rules
        Assert.assertTrue(gameHandler.movePiece(H2, H4, WHITE)); //White move
        Assert.assertFalse(gameHandler.movePiece(H4, H5, WHITE)); //White move again, supposed to fail
        Assert.assertTrue(gameHandler.movePiece(H7, H6, BLACK)); //Black move
        Assert.assertFalse(gameHandler.movePiece(H6, H5, WHITE)); //Black move again, supposed to fail

        gameHandler.addSpecialRule(SpecialGameRules.NO_PLAYER_TURN);

        //With the rule
        Assert.assertTrue(gameHandler.movePiece(G2, G4, WHITE)); //White move
        Assert.assertTrue(gameHandler.movePiece(G4, G5, WHITE)); //White move again, supposed to pass (with the rule only)

    }


    @Test
    public void noCheckOrCheckmateTest() {
        String positionPieces = "A8:B_KING;E1:W_KING;E3:B_QUEEN;D3:B_QUEEN;F3:B_QUEEN";

        StandardGameHandlerContext gameHandler = new StandardGameHandlerContext(constraintService, positionPieces);

        assertThat(gameHandler.getSpecialGameRules()).isEmpty(); //Make sure there's no rule applied at the beginning, in a standard game

        //No rule
        Assert.assertEquals(KingStatus.CHECKMATE, gameHandler.getKingStatus(WHITE));

        //With the rule
        gameHandler.addSpecialRule(SpecialGameRules.NO_CHECK_OR_CHECKMATE);

        Assert.assertEquals(KingStatus.OK, gameHandler.getKingStatus(WHITE));
    }

}
