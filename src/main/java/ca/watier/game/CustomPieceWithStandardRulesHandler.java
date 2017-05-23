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

import ca.watier.enums.CasePosition;
import ca.watier.enums.Pieces;
import ca.watier.services.ConstraintService;

import java.util.Map;

/**
 * Created by yannick on 5/22/2017.
 */
public class CustomPieceWithStandardRulesHandler extends StandardGameHandler {

    public CustomPieceWithStandardRulesHandler(ConstraintService constraintService) {
        super(constraintService);
    }

    public void setPieces(Map<CasePosition, Pieces> positionPieces) {
        CURRENT_PIECES_LOCATION = positionPieces;
    }
}
