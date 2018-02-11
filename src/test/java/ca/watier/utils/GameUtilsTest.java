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

package ca.watier.utils;

import ca.watier.echesscommon.enums.CasePosition;
import ca.watier.echesscommon.enums.Pieces;
import ca.watier.echesscommon.utils.GameUtils;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import static ca.watier.echesscommon.enums.Pieces.*;

/**
 * Created by yannick on 4/28/2017.
 */
public class GameUtilsTest {
    @Test
    public void getPiecesPosition1() {

    }

    @Test
    public void getPiecesPosition() {
        Map<CasePosition, Pieces> pieces = GameUtils.getDefaultGame();

        Assertions.assertThat(GameUtils.getPiecesPosition(W_PAWN, pieces)).isNotEmpty().hasSize(8);
        Assertions.assertThat(GameUtils.getPiecesPosition(W_ROOK, pieces)).isNotEmpty().hasSize(2);
        Assertions.assertThat(GameUtils.getPiecesPosition(W_KING, pieces)).isNotEmpty().hasSize(1);
    }

    @Test
    public void isOtherPiecesBetweenTarget() {

        Map<CasePosition, Pieces> defaultGame = GameUtils.getDefaultGame();
        Assert.assertTrue(GameUtils.isOtherPiecesBetweenTarget(CasePosition.H1, CasePosition.H7, defaultGame));
        Assert.assertFalse(GameUtils.isOtherPiecesBetweenTarget(CasePosition.H2, CasePosition.H7, defaultGame));
        Assert.assertTrue(GameUtils.isOtherPiecesBetweenTarget(CasePosition.H7, CasePosition.H1, defaultGame));
        Assert.assertFalse(GameUtils.isOtherPiecesBetweenTarget(CasePosition.H7, CasePosition.H2, defaultGame));

        Assert.assertTrue(GameUtils.isOtherPiecesBetweenTarget(CasePosition.A1, CasePosition.G7, defaultGame));
        Assert.assertFalse(GameUtils.isOtherPiecesBetweenTarget(CasePosition.B2, CasePosition.G7, defaultGame));
        Assert.assertTrue(GameUtils.isOtherPiecesBetweenTarget(CasePosition.G7, CasePosition.A1, defaultGame));
        Assert.assertFalse(GameUtils.isOtherPiecesBetweenTarget(CasePosition.G7, CasePosition.B2, defaultGame));

        Assert.assertFalse(GameUtils.isOtherPiecesBetweenTarget(CasePosition.H1, CasePosition.G1, defaultGame));
        Assert.assertFalse(GameUtils.isOtherPiecesBetweenTarget(CasePosition.G1, CasePosition.H1, defaultGame));

        Assert.assertTrue(GameUtils.isOtherPiecesBetweenTarget(CasePosition.H1, CasePosition.F1, defaultGame));
        Assert.assertTrue(GameUtils.isOtherPiecesBetweenTarget(CasePosition.F1, CasePosition.H1, defaultGame));

    }

}