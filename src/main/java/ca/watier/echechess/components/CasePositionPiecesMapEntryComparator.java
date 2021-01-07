/*
 *    Copyright 2014 - 2021 Yannick Watier
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

package ca.watier.echechess.components;

import ca.watier.echechess.common.enums.CasePosition;
import ca.watier.echechess.common.enums.Pieces;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Comparator;
import java.util.Map;

public class CasePositionPiecesMapEntryComparator implements Comparator<Map.Entry<CasePosition, Pieces>> {
    @Override
    public int compare(Map.Entry<CasePosition, Pieces> first, Map.Entry<CasePosition, Pieces> second) {
        if (ObjectUtils.anyNull(first, second)) {
            throw new NullPointerException();
        }

        CasePosition firstPosition = first.getKey();
        CasePosition secondPosition = second.getKey();

        return Integer.compare(firstPosition.getX(), secondPosition.getX());
    }
}
