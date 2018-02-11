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

import ca.watier.echesscommon.enums.CasePosition;
import ca.watier.echesscommon.enums.Pieces;
import ca.watier.echesscommon.interfaces.WebSocketService;
import ca.watier.echesscommon.utils.Assert;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by yannick on 5/22/2017.
 */
public class CustomPieceWithStandardRulesHandler extends GenericGameHandler {
    public static final String THE_NUMBER_OF_PARAMETER_IS_INCORRECT = "The number of parameter is incorrect !";

    public CustomPieceWithStandardRulesHandler(GameConstraints gameConstraints, WebSocketService webSocketService) {
        super(gameConstraints, webSocketService);
    }

    public void setPieces(Map<CasePosition, Pieces> positionPieces) {
        setPositionPiecesMap(positionPieces);
    }

    public void setPieces(String specialGamePieces) {
        Assert.assertNotEmpty(specialGamePieces);

        Map<CasePosition, Pieces> positionPieces = new EnumMap<>(CasePosition.class);

        for (String section : specialGamePieces.split(";")) {
            String[] values = section.split(":");

            if (values.length != 2) {
                throw new UnsupportedOperationException(THE_NUMBER_OF_PARAMETER_IS_INCORRECT);
            }

            positionPieces.put(CasePosition.valueOf(values[0]), Pieces.valueOf(values[1]));
        }

        setPositionPiecesMap(positionPieces);
    }
}
