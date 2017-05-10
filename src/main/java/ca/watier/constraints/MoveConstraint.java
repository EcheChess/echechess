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

package ca.watier.constraints;

import ca.watier.enums.CasePosition;
import ca.watier.enums.Pieces;
import ca.watier.enums.Side;

import java.util.Map;

/**
 * Created by yannick on 4/23/2017.
 */
public interface MoveConstraint {
    /**
     * Check if the pieces can attack to the selected position, used to check to attack capability of the pieces <br>
     * <b>Warning</b>: This method doesn't validate if there's another pieces at the specified location
     *
     * @param from
     * @param to
     * @param side
     * @param positionPiecesMap
     * @return
     */
    boolean canAttackTo(CasePosition from, CasePosition to, Side side, Map<CasePosition, Pieces> positionPiecesMap);

    boolean isMoveValid(CasePosition from, CasePosition to, Side side, Map<CasePosition, Pieces> positionPiecesMap);
}
