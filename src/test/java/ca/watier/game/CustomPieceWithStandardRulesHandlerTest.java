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

import ca.watier.services.ConstraintService;
import org.junit.Before;
import org.junit.Test;

import static ca.watier.game.CustomPieceWithStandardRulesHandler.THE_NUMBER_OF_PARAMETER_IS_INCORRECT;
import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Created by yannick on 6/20/2017.
 */
public class CustomPieceWithStandardRulesHandlerTest {

    private static final Class<UnsupportedOperationException> UNSUPPORTED_OPERATION_EXCEPTION_CLASS = UnsupportedOperationException.class;
    private static final ConstraintService CONSTRAINT_SERVICE = new ConstraintService();
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
}