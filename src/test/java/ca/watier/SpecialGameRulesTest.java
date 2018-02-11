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

import ca.watier.echechessengine.contexts.StandardGameHandlerContext;
import ca.watier.echechessengine.engines.GenericGameHandler;
import ca.watier.echesscommon.enums.KingStatus;
import ca.watier.echesscommon.enums.Side;
import org.junit.Assert;
import org.junit.Test;

import static ca.watier.echesscommon.enums.CasePosition.*;
import static ca.watier.echesscommon.enums.MoveType.MOVE_NOT_ALLOWED;
import static ca.watier.echesscommon.enums.MoveType.NORMAL_MOVE;
import static ca.watier.echesscommon.enums.SpecialGameRules.NO_CHECK_OR_CHECKMATE;
import static ca.watier.echesscommon.enums.SpecialGameRules.NO_PLAYER_TURN;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Those tests are meant to test if the rules doesn't interfere with the original game
 */
public class SpecialGameRulesTest extends GameTest {

    private static final Side WHITE = Side.WHITE;
    private static final Side BLACK = Side.BLACK;

    @Test
    public void noPlayerTurnTest() {
        GenericGameHandler gameHandler = new GenericGameHandler(CONSTRAINT_SERVICE, WEB_SOCKET_SERVICE);

        assertThat(gameHandler.getSpecialGameRules()).isEmpty(); //Make sure there's no rule applied at the beginning, in a standard game

        //No rules
        Assert.assertEquals(NORMAL_MOVE, gameHandler.movePiece(H2, H4, WHITE)); //White move
        Assert.assertEquals(MOVE_NOT_ALLOWED, gameHandler.movePiece(H4, H5, WHITE)); //White move again, supposed to fail
        Assert.assertEquals(NORMAL_MOVE, gameHandler.movePiece(H7, H6, BLACK)); //Black move
        Assert.assertEquals(MOVE_NOT_ALLOWED, gameHandler.movePiece(H6, H5, WHITE)); //Black move again, supposed to fail

        gameHandler.addSpecialRule(NO_PLAYER_TURN);

        //With the rule
        Assert.assertEquals(NORMAL_MOVE, gameHandler.movePiece(G2, G4, WHITE)); //White move
        Assert.assertEquals(NORMAL_MOVE, gameHandler.movePiece(G4, G5, WHITE)); //White move again, supposed to pass (with the rule only)

    }


    @Test
    public void noCheckOrCheckmateTest() {
        String positionPieces = "A8:B_KING;E1:W_KING;E3:B_QUEEN;D3:B_QUEEN;F3:B_QUEEN";

        StandardGameHandlerContext gameHandler = new StandardGameHandlerContext(CONSTRAINT_SERVICE, WEB_SOCKET_SERVICE, positionPieces);

        assertThat(gameHandler.getSpecialGameRules()).isEmpty(); //Make sure there's no rule applied at the beginning, in a standard game

        //No rule
        Assert.assertEquals(KingStatus.CHECKMATE, gameHandler.getKingStatus(WHITE, true));

        //With the rule
        gameHandler.addSpecialRule(NO_CHECK_OR_CHECKMATE);

        Assert.assertEquals(KingStatus.OK, gameHandler.getKingStatus(WHITE, true));
    }

    @Test
    public void addAndRemoveRuleTest() {
        String positionPieces = "A8:B_KING;E1:W_KING;E3:B_QUEEN;D3:B_QUEEN;F3:B_QUEEN";
        StandardGameHandlerContext gameHandler = new StandardGameHandlerContext(CONSTRAINT_SERVICE, WEB_SOCKET_SERVICE, positionPieces);
        assertThat(gameHandler.getSpecialGameRules()).isEmpty(); //Make sure there's no rule applied at the beginning, in a standard game

        gameHandler.addSpecialRule(NO_CHECK_OR_CHECKMATE, NO_PLAYER_TURN);
        assertThat(gameHandler.getSpecialGameRules()).containsOnly(NO_CHECK_OR_CHECKMATE, NO_PLAYER_TURN);

        gameHandler.removeSpecialRule(NO_PLAYER_TURN);
        assertThat(gameHandler.getSpecialGameRules()).containsOnly(NO_CHECK_OR_CHECKMATE);

    }

}
