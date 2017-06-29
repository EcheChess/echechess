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
import ca.watier.utils.Assert;
import ca.watier.utils.GameUtils;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Created by yannick on 6/29/2017.
 */
public abstract class GameBoard {
    private final Map<CasePosition, Pieces> defaultPositions;
    private Map<CasePosition, Pieces> positionPiecesMap;
    private Map<CasePosition, Boolean> movedPiecesMap;

    public GameBoard() {
        defaultPositions = new EnumMap<>(CasePosition.class);
        positionPiecesMap = GameUtils.getDefaultGame();
        defaultPositions.putAll(positionPiecesMap);
        movedPiecesMap = GameUtils.initNewMovedPieceMap(positionPiecesMap);
    }

    /**
     * Get an unmodifiable {@link java.util.Collections.UnmodifiableMap} of the current game
     *
     * @return
     */
    public final Map<CasePosition, Pieces> getPiecesLocation() {
        return Collections.unmodifiableMap(positionPiecesMap);
    }

    public final void setPositionPiecesMap(Map<CasePosition, Pieces> positionPiecesMap) {
        Assert.assertNotEmpty(positionPiecesMap);

        this.positionPiecesMap = positionPiecesMap;
        this.defaultPositions.clear();
        this.defaultPositions.putAll(positionPiecesMap);
        this.movedPiecesMap = GameUtils.initNewMovedPieceMap(positionPiecesMap);
    }

    /**
     * Set the specified case at the position
     *
     * @param piece
     * @param position
     */
    public final void setPiecePosition(Pieces piece, CasePosition position) {
        positionPiecesMap.put(position, piece);
        changeMovedStateOfPiece(piece, position);
    }

    /**
     * If it's the default position of the piece, mark this one as moved
     *
     * @param piece
     * @param position
     */
    private void changeMovedStateOfPiece(Pieces piece, CasePosition position) {
        if (GameUtils.isDefaultPosition(position, piece, this)) {
            movedPiecesMap.put(position, true);
        }
    }

    /**
     * Get the piece at the specific position
     *
     * @param position
     * @return
     */
    public final Pieces getPiece(CasePosition position) {
        Assert.assertNotNull(position);

        return positionPiecesMap.get(position);
    }

    /**
     * Change a piece position, there's no check/constraint(s) on this method (Direct access to the Map)
     *
     * @param from
     * @param to
     * @param piece
     */
    protected final void movePieceTo(CasePosition from, CasePosition to, Pieces piece) {
        Assert.assertNotNull(from, to, piece);

        positionPiecesMap.remove(from);
        positionPiecesMap.put(to, piece);
        changeMovedStateOfPiece(piece, from);
    }

    /**
     * Remove a piece from the board
     *
     * @param from
     */
    public final void removePieceAt(CasePosition from) {
        Assert.assertNotNull(from);

        positionPiecesMap.remove(from);
        movedPiecesMap.remove(from);
    }

    /**
     * Check if the piece is moved
     *
     * @param position
     * @return
     */
    public final boolean isPieceMoved(CasePosition position) {
        Assert.assertNotNull(position);

        return movedPiecesMap.get(position);
    }

    public Map<CasePosition, Pieces> getDefaultPositions() {
        return Collections.unmodifiableMap(defaultPositions);
    }
}
