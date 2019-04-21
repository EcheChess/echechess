/*
 *    Copyright 2014 - 2019 Yannick Watier
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

package ca.watier.echechess.models;

import ca.watier.echechess.common.enums.CasePosition;
import ca.watier.echechess.common.enums.Pieces;
import ca.watier.echechess.common.enums.Side;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

public class PieceLocationModel implements Serializable {
    private static final long serialVersionUID = 826241513797550414L;
    private final String unicodeIcon;
    private final String name;
    private final String rawPosition;
    private final byte side;

    public PieceLocationModel(Pieces pieces, CasePosition position) {

        if (Objects.isNull(position)) {
            throw new IllegalArgumentException("The piece position cannot be null");
        }

        if (Objects.nonNull(pieces)) {
            this.unicodeIcon = pieces.getUnicodeIcon();
            this.name = pieces.getName();
        } else {
            this.unicodeIcon = "";
            this.name = "";
        }

        this.side = initSide(pieces);
        this.rawPosition = position.name();
    }

    private byte initSide(Pieces piece) {
        return Optional.ofNullable(piece)
                .map(Pieces::getSide)
                .map(Side::getValue)
                .orElse((byte) 0x00);
    }

    public String getUnicodeIcon() {
        return unicodeIcon;
    }

    public String getName() {
        return name;
    }

    public String getRawPosition() {
        return rawPosition;
    }

    public byte getSide() {
        return side;
    }
}
