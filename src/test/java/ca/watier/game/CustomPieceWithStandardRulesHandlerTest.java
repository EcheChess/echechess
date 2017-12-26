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
import ca.watier.enums.KingStatus;
import ca.watier.enums.Pieces;
import ca.watier.enums.SpecialGameRules;
import ca.watier.impl.WebSocketServiceTestImpl;
import ca.watier.utils.Constants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static ca.watier.enums.CasePosition.*;
import static ca.watier.enums.MoveType.MOVE_NOT_ALLOWED;
import static ca.watier.game.CustomPieceWithStandardRulesHandler.THE_NUMBER_OF_PARAMETER_IS_INCORRECT;
import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Created by yannick on 6/20/2017.
 */
public class CustomPieceWithStandardRulesHandlerTest extends GameTest {

    private static final Class<UnsupportedOperationException> UNSUPPORTED_OPERATION_EXCEPTION_CLASS = UnsupportedOperationException.class;
    private CustomPieceWithStandardRulesHandler customPieceWithStandardRulesHandler;

    @Before
    public void setUp() {
        customPieceWithStandardRulesHandler = new CustomPieceWithStandardRulesHandler(CONSTRAINT_SERVICE, WEB_SOCKET_SERVICE);
        ((WebSocketServiceTestImpl) WEB_SOCKET_SERVICE).clearMessages();
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


    @Test
    public void getKingStatusStaleMate() {
        customPieceWithStandardRulesHandler.addSpecialRule(SpecialGameRules.NO_PLAYER_TURN);

        Assert.assertEquals(KingStatus.OK, customPieceWithStandardRulesHandler.getKingStatus(WHITE, true));
        Assert.assertEquals(KingStatus.OK, customPieceWithStandardRulesHandler.getKingStatus(BLACK, true));

        /*
            STALEMATE
        */
        customPieceWithStandardRulesHandler.setPieces("H1:W_KING;D5:B_KING;C7:W_ROOK;E7:W_ROOK;B6:W_ROOK;B4:W_ROOK");
        Assert.assertEquals(KingStatus.STALEMATE, customPieceWithStandardRulesHandler.getKingStatus(BLACK, true));

        customPieceWithStandardRulesHandler.setPieces("D8:B_KING;D7:W_PAWN;D6:W_KING");
        Assert.assertEquals(KingStatus.STALEMATE, customPieceWithStandardRulesHandler.getKingStatus(BLACK, true));

        customPieceWithStandardRulesHandler.setPieces("D8:B_KING;D6:W_KING;D7:W_PAWN;C8:B_PAWN;A8:W_ROOK");
        Assert.assertEquals(KingStatus.STALEMATE, customPieceWithStandardRulesHandler.getKingStatus(BLACK, true));


        customPieceWithStandardRulesHandler.setPieces("D1:B_KING;C1:B_PAWN;C2:B_PAWN;D2:B_PAWN;E2:B_PAWN;E1:B_PAWN;D8:W_KING;C8:W_PAWN;C7:W_PAWN;D7:W_PAWN;E7:W_PAWN;E8:W_PAWN");
        Assert.assertEquals(KingStatus.STALEMATE, customPieceWithStandardRulesHandler.getKingStatus(WHITE, true));
        Assert.assertEquals(KingStatus.STALEMATE, customPieceWithStandardRulesHandler.getKingStatus(BLACK, true));


        /*
            Not STALEMATE
         */
        customPieceWithStandardRulesHandler.setPieces("D8:B_KING;D6:W_KING;D7:W_PAWN;C8:B_PAWN;A8:W_ROOK;E8:B_PAWN");
        Assert.assertEquals(KingStatus.OK, customPieceWithStandardRulesHandler.getKingStatus(BLACK, true));

        customPieceWithStandardRulesHandler.setPieces("H1:W_KING;D5:B_KING;C7:W_ROOK;B6:W_ROOK;B4:W_ROOK");
        Assert.assertEquals(KingStatus.OK, customPieceWithStandardRulesHandler.getKingStatus(BLACK, true));

        customPieceWithStandardRulesHandler.setPieces("D8:B_KING;D6:W_KING;D7:W_PAWN;C8:B_PAWN");
        Assert.assertEquals(KingStatus.OK, customPieceWithStandardRulesHandler.getKingStatus(BLACK, true));

        customPieceWithStandardRulesHandler.setPieces("D1:B_KING;C1:B_PAWN;C2:B_PAWN;D2:B_PAWN;E2:B_PAWN;D8:W_KING;C8:W_PAWN;C7:W_PAWN;D7:W_PAWN;E7:W_PAWN");
        Assert.assertEquals(KingStatus.OK, customPieceWithStandardRulesHandler.getKingStatus(WHITE, true));
        Assert.assertEquals(KingStatus.OK, customPieceWithStandardRulesHandler.getKingStatus(BLACK, true));

    }

    @Test
    public void getMessageBlackKingCheckMate() {
        customPieceWithStandardRulesHandler.addSpecialRule(SpecialGameRules.NO_PLAYER_TURN);
        customPieceWithStandardRulesHandler.setPieces("H8:B_KING;H1:W_KING;B7:W_QUEEN;A7:W_QUEEN");

        customPieceWithStandardRulesHandler.movePiece(A7, A8, WHITE); //Move the White queen to checkmate the black king

        assertThat(((WebSocketServiceTestImpl) WEB_SOCKET_SERVICE).getMessages()).containsOnly(
                "WHITE player moved A7 to A8",
                Constants.PLAYER_TURN,
                EMPTY_GAME_SCORE_RESPONSE,
                String.format(Constants.PLAYER_KING_CHECKMATE, "BLACK")
        );
    }

    @Test
    public void getMessageWhiteKingCheckMate() {
        customPieceWithStandardRulesHandler.addSpecialRule(SpecialGameRules.NO_PLAYER_TURN);
        customPieceWithStandardRulesHandler.setPieces("H8:B_KING;H1:W_KING;A2:B_QUEEN;B2:B_QUEEN");

        customPieceWithStandardRulesHandler.movePiece(A2, A1, BLACK); //Move the Black queen to checkmate the white king

        assertThat(((WebSocketServiceTestImpl) WEB_SOCKET_SERVICE).getMessages()).containsOnly(
                "BLACK player moved A2 to A1",
                Constants.PLAYER_TURN,
                EMPTY_GAME_SCORE_RESPONSE,
                String.format(Constants.PLAYER_KING_CHECKMATE, "WHITE")
        );
    }


    @Test
    public void getMessageBlackKingCheck() {
        customPieceWithStandardRulesHandler.addSpecialRule(SpecialGameRules.NO_PLAYER_TURN);
        customPieceWithStandardRulesHandler.setPieces("H8:B_KING;H1:W_KING;E7:W_QUEEN");

        customPieceWithStandardRulesHandler.movePiece(E7, E8, WHITE); //Move the White queen to checkmate the black king

        assertThat(((WebSocketServiceTestImpl) WEB_SOCKET_SERVICE).getMessages()).containsOnly(
                "WHITE player moved E7 to E8",
                Constants.PLAYER_TURN,
                EMPTY_GAME_SCORE_RESPONSE,
                Constants.PLAYER_KING_CHECK
        );
    }

    @Test
    public void getMessageWhiteKingCheck() {
        customPieceWithStandardRulesHandler.addSpecialRule(SpecialGameRules.NO_PLAYER_TURN);
        customPieceWithStandardRulesHandler.setPieces("H8:B_KING;H1:W_KING;E2:B_QUEEN");

        customPieceWithStandardRulesHandler.movePiece(E2, E1, BLACK); //Move the Black queen to checkmate the white king

        assertThat(((WebSocketServiceTestImpl) WEB_SOCKET_SERVICE).getMessages()).containsOnly(
                "BLACK player moved E2 to E1",
                Constants.PLAYER_TURN,
                EMPTY_GAME_SCORE_RESPONSE,
                Constants.PLAYER_KING_CHECK
        );
    }

}