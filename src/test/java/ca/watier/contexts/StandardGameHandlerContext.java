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

package ca.watier.contexts;

import ca.watier.enums.CasePosition;
import ca.watier.enums.Pieces;
import ca.watier.game.CustomPieceWithStandardRulesHandler;
import ca.watier.services.ConstraintService;
import ca.watier.sessions.Player;
import ca.watier.utils.Assert;

import java.util.Map;
import java.util.UUID;

/**
 * Created by yannick on 5/20/2017.
 */
public class StandardGameHandlerContext extends CustomPieceWithStandardRulesHandler {

    public StandardGameHandlerContext(ConstraintService constraintService) {
        super(constraintService);
        addBothPlayerToGameAndSetUUID();
    }

    private void addBothPlayerToGameAndSetUUID() {
        UUID uuid = UUID.randomUUID();
        setUuid(uuid.toString());
        playerBlack = new Player();
        playerBlack.addJoinedGame(uuid);
        playerWhite = new Player();
        playerWhite.addJoinedGame(uuid);
    }

    public StandardGameHandlerContext(ConstraintService constraintService, Map<CasePosition, Pieces> positionPieces) {
        super(constraintService);
        Assert.assertNotEmpty(positionPieces);

        setPieces(positionPieces);
        addBothPlayerToGameAndSetUUID();
    }

    public StandardGameHandlerContext(ConstraintService constraintService, String positionPieces) {
        super(constraintService);
        Assert.assertNotEmpty(positionPieces);

        setPieces(positionPieces);
        addBothPlayerToGameAndSetUUID();
    }

    public void movePieceTo(CasePosition from, CasePosition to) {
        Assert.assertNotNull(from, to);

        Pieces pieces = getPiece(from);
        Assert.assertNotNull(pieces);

        movePieceTo(from, to, pieces);
    }
}
