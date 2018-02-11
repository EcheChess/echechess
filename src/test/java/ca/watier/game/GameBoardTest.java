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
import ca.watier.echechessengine.contexts.StandardGameHandlerContext;
import ca.watier.echesscommon.enums.SpecialGameRules;
import org.junit.Before;
import org.junit.Test;

import static ca.watier.echesscommon.enums.CasePosition.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Created by yannick on 7/1/2017.
 */
public class GameBoardTest extends GameTest {
    private StandardGameHandlerContext context;

    @Before
    public void setUp() {
        context = new StandardGameHandlerContext(CONSTRAINT_SERVICE, WEB_SOCKET_SERVICE);
        context.addSpecialRule(SpecialGameRules.NO_PLAYER_TURN);
    }

    @Test
    public void getBlackTurnNumber() {
        assertThat(context.getBlackTurnNumber()).isZero();
        context.movePiece(H7, H6, BLACK);
        assertThat(context.getBlackTurnNumber()).isEqualTo(1);
        context.movePiece(H6, H5, BLACK);
        context.movePiece(H5, H4, BLACK);
        assertThat(context.getBlackTurnNumber()).isEqualTo(3);
    }

    @Test
    public void getWhiteTurnNumber() {
        assertThat(context.getWhiteTurnNumber()).isZero();
        context.movePiece(H2, H3, WHITE);
        assertThat(context.getWhiteTurnNumber()).isEqualTo(1);
        context.movePiece(H3, H4, WHITE);
        context.movePiece(H4, H5, WHITE);
        assertThat(context.getWhiteTurnNumber()).isEqualTo(3);
    }

    @Test
    public void isPieceMoved() {
        assertFalse(context.isPieceMoved(G1));

        context.movePiece(G1, F3, WHITE);

        assertTrue(context.isPieceMoved(F3));
        assertNull(context.isPieceMoved(G1));
    }

    @Test
    public void isPawnUsedSpecialMove() {
        assertFalse(context.isPawnUsedSpecialMove(H2));
        context.movePiece(H2, H4, WHITE);
        assertTrue(context.isPawnUsedSpecialMove(H4));

        assertFalse(context.isPawnUsedSpecialMove(G2));
        context.movePiece(G2, G3, WHITE);
        assertFalse(context.isPawnUsedSpecialMove(G3));

    }

    @Test
    public void getDefaultPositions() {
        assertThat(context.getDefaultPositions()).isEqualTo(context.getPiecesLocation());
        context.movePiece(G2, G3, WHITE);
        assertThat(context.getDefaultPositions()).isNotEqualTo(context.getPiecesLocation());
    }


    @Test
    public void getTurnNumberPiece() {
        assertThat(context.getPieceTurn(G2)).isZero();
        context.movePiece(G2, G3, WHITE);
        assertThat(context.getPieceTurn(G3)).isZero();
        context.movePiece(G3, G4, WHITE);
        assertThat(context.getPieceTurn(G3)).isNull();
        assertThat(context.getPieceTurn(G4)).isEqualTo(1);
    }


}