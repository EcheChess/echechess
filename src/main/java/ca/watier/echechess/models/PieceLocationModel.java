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
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

public class PieceLocationModel implements Serializable {
    @Serial
    private static final long serialVersionUID = -4788739945570270875L;

    @JsonIgnore
    private transient final Pieces pieces;

    @JsonIgnore
    private transient final CasePosition position;

    private final String rawPosition;
    private final byte side;
    private String borderColor = null; // Used in Vue to change the border color (selection, ect)

    public PieceLocationModel(Pieces pieces, CasePosition position) {
        this.pieces = pieces;
        this.position = position;

        if (position == null) {
            throw new IllegalArgumentException("The piece position cannot be null");
        }

        this.side = initSide(pieces);
        this.rawPosition = position.name();
    }

    private byte initSide(Pieces piece) {
        return Optional.ofNullable(piece)
                .map(Pieces::getSide)
                .map(Side::getValue)
                .orElse((byte) -1);
    }

    public String getUnicodeIcon() {
        return Optional.ofNullable(pieces)
                .map(Pieces::getUnicodeIcon)
                .orElse("");
    }

    public String getName() {
        return Optional.ofNullable(pieces)
                .map(Pieces::getName)
                .orElse("");
    }

    public String getRawPosition() {
        return rawPosition;
    }

    public byte getSide() {
        return side;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public Pieces getPieces() {
        return pieces;
    }

    public CasePosition getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "PieceLocationModel{" +
                "pieces=" + pieces +
                ", position=" + position +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PieceLocationModel that = (PieceLocationModel) o;
        return pieces == that.pieces && position == that.position;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieces, position);
    }
}
