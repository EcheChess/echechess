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

import ca.watier.enums.CasePosition;
import ca.watier.enums.Direction;
import ca.watier.enums.Pieces;
import ca.watier.game.GameBoard;
import ca.watier.interfaces.BaseUtils;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yannick on 4/23/2017.
 */
public class GameUtils implements BaseUtils {

    private static final Map<CasePosition, Pieces> DEFAULT_GAME_TEMPLATE = new EnumMap<>(CasePosition.class);
    private static final Map<CasePosition, Boolean> DEFAULT_GAME_MOVED_TEMPLATE = new EnumMap<>(CasePosition.class);

    static {
        DEFAULT_GAME_TEMPLATE.put(CasePosition.A1, Pieces.W_ROOK);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.B1, Pieces.W_KNIGHT);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.C1, Pieces.W_BISHOP);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.D1, Pieces.W_QUEEN);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.E1, Pieces.W_KING);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.F1, Pieces.W_BISHOP);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.G1, Pieces.W_KNIGHT);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.H1, Pieces.W_ROOK);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.A2, Pieces.W_PAWN);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.B2, Pieces.W_PAWN);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.C2, Pieces.W_PAWN);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.D2, Pieces.W_PAWN);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.E2, Pieces.W_PAWN);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.F2, Pieces.W_PAWN);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.G2, Pieces.W_PAWN);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.H2, Pieces.W_PAWN);

        DEFAULT_GAME_TEMPLATE.put(CasePosition.A8, Pieces.B_ROOK);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.B8, Pieces.B_KNIGHT);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.C8, Pieces.B_BISHOP);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.D8, Pieces.B_QUEEN);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.E8, Pieces.B_KING);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.F8, Pieces.B_BISHOP);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.G8, Pieces.B_KNIGHT);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.H8, Pieces.B_ROOK);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.A7, Pieces.B_PAWN);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.B7, Pieces.B_PAWN);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.C7, Pieces.B_PAWN);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.D7, Pieces.B_PAWN);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.E7, Pieces.B_PAWN);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.F7, Pieces.B_PAWN);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.G7, Pieces.B_PAWN);
        DEFAULT_GAME_TEMPLATE.put(CasePosition.H7, Pieces.B_PAWN);

        DEFAULT_GAME_MOVED_TEMPLATE.put(CasePosition.A1, false);
        DEFAULT_GAME_MOVED_TEMPLATE.put(CasePosition.B1, false);
        DEFAULT_GAME_MOVED_TEMPLATE.put(CasePosition.C1, false);
        DEFAULT_GAME_MOVED_TEMPLATE.put(CasePosition.D1, false);
        DEFAULT_GAME_MOVED_TEMPLATE.put(CasePosition.E1, false);
        DEFAULT_GAME_MOVED_TEMPLATE.put(CasePosition.F1, false);
        DEFAULT_GAME_MOVED_TEMPLATE.put(CasePosition.G1, false);
        DEFAULT_GAME_MOVED_TEMPLATE.put(CasePosition.H1, false);
        DEFAULT_GAME_MOVED_TEMPLATE.put(CasePosition.A2, false);
        DEFAULT_GAME_MOVED_TEMPLATE.put(CasePosition.B2, false);
        DEFAULT_GAME_MOVED_TEMPLATE.put(CasePosition.C2, false);
        DEFAULT_GAME_MOVED_TEMPLATE.put(CasePosition.D2, false);
        DEFAULT_GAME_MOVED_TEMPLATE.put(CasePosition.E2, false);
        DEFAULT_GAME_MOVED_TEMPLATE.put(CasePosition.F2, false);
        DEFAULT_GAME_MOVED_TEMPLATE.put(CasePosition.G2, false);
        DEFAULT_GAME_MOVED_TEMPLATE.put(CasePosition.H2, false);

        DEFAULT_GAME_MOVED_TEMPLATE.put(CasePosition.A8, false);
        DEFAULT_GAME_MOVED_TEMPLATE.put(CasePosition.B8, false);
        DEFAULT_GAME_MOVED_TEMPLATE.put(CasePosition.C8, false);
        DEFAULT_GAME_MOVED_TEMPLATE.put(CasePosition.D8, false);
        DEFAULT_GAME_MOVED_TEMPLATE.put(CasePosition.E8, false);
        DEFAULT_GAME_MOVED_TEMPLATE.put(CasePosition.F8, false);
        DEFAULT_GAME_MOVED_TEMPLATE.put(CasePosition.G8, false);
        DEFAULT_GAME_MOVED_TEMPLATE.put(CasePosition.H8, false);
        DEFAULT_GAME_MOVED_TEMPLATE.put(CasePosition.A7, false);
        DEFAULT_GAME_MOVED_TEMPLATE.put(CasePosition.B7, false);
        DEFAULT_GAME_MOVED_TEMPLATE.put(CasePosition.C7, false);
        DEFAULT_GAME_MOVED_TEMPLATE.put(CasePosition.D7, false);
        DEFAULT_GAME_MOVED_TEMPLATE.put(CasePosition.E7, false);
        DEFAULT_GAME_MOVED_TEMPLATE.put(CasePosition.F7, false);
        DEFAULT_GAME_MOVED_TEMPLATE.put(CasePosition.G7, false);
        DEFAULT_GAME_MOVED_TEMPLATE.put(CasePosition.H7, false);
    }

    private GameUtils() {
    }

    /**
     * Create a new EnumMap containing the default game
     *
     * @return
     */
    public static Map<CasePosition, Pieces> getDefaultGame() {
        Map<CasePosition, Pieces> game = new EnumMap<>(CasePosition.class);
        game.putAll(DEFAULT_GAME_TEMPLATE);
        return game;
    }


    /**
     * Init an {@link EnumMap} based of a board
     *
     * @param positionPiecesMap
     * @return
     */
    public static Map<CasePosition, Boolean> initNewMovedPieceMap(Map<CasePosition, Pieces> positionPiecesMap) {
        Assert.assertNotEmpty(positionPiecesMap);

        Map<CasePosition, Boolean> values = new EnumMap<>(CasePosition.class);
        for (CasePosition position : positionPiecesMap.keySet()) {
            values.put(position, false);
        }

        return values;
    }


    /**
     * Init an {@link EnumMap} based of a board, return the pawns only
     *
     * @param positionPiecesMap
     * @return
     */
    public static Map<CasePosition, Boolean> initPawnMap(Map<CasePosition, Pieces> positionPiecesMap) {
        Assert.assertNotEmpty(positionPiecesMap);

        Map<CasePosition, Boolean> values = new EnumMap<>(CasePosition.class);

        for (Map.Entry<CasePosition, Pieces> casePositionPiecesEntry : positionPiecesMap.entrySet()) {
            if (Pieces.isPawn(casePositionPiecesEntry.getValue())) {
                values.put(casePositionPiecesEntry.getKey(), false);
            }
        }

        return values;
    }


    /**
     * Init an {@link EnumMap} based of a board
     *
     * @param positionPiecesMap
     * @return
     */
    public static Map<CasePosition, Integer> initTurnMap(Map<CasePosition, Pieces> positionPiecesMap) {
        Assert.assertNotEmpty(positionPiecesMap);

        Map<CasePosition, Integer> values = new EnumMap<>(CasePosition.class);

        for (Map.Entry<CasePosition, Pieces> casePositionPiecesEntry : positionPiecesMap.entrySet()) {
            values.put(casePositionPiecesEntry.getKey(), 0);
        }

        return values;
    }

    /**
     * Check if it's the default position for the piece
     *
     * @param position
     * @param pieces
     * @param gameBoard
     * @return
     */
    public static boolean isDefaultPosition(CasePosition position, Pieces pieces, GameBoard gameBoard) {
        Assert.assertNotNull(position, pieces);
        return pieces.equals(gameBoard.getDefaultPositions().get(position));
    }

    /**
     * Check if there's one or more piece between two pieces
     *
     * @param from
     * @param to
     * @param pieces
     * @return
     */
    public static boolean isOtherPiecesBetweenTarget(CasePosition from, CasePosition to, Map<CasePosition, Pieces> pieces) {
        Assert.assertNotNull(from, to, pieces);

        return !getPiecesBetweenPosition(from, to, pieces).isEmpty();
    }

    /**
     * Gets all {@link CasePosition} that have a piece between two positions
     *
     * @param from
     * @param to
     * @param pieces
     * @return
     */
    public static List<CasePosition> getPiecesBetweenPosition(CasePosition from, CasePosition to, Map<CasePosition, Pieces> pieces) {
        Assert.assertNotNull(from, to, pieces);

        List<CasePosition> positions = new ArrayList<>();

        int distanceFromDestination = BaseUtils.getSafeInteger(MathUtils.getDistanceBetweenPositionsWithCommonDirection(from, to));
        Direction directionToDestination = MathUtils.getDirectionFromPosition(from, to);

        for (Map.Entry<CasePosition, Pieces> casePositionPiecesEntry : pieces.entrySet()) {

            CasePosition key = casePositionPiecesEntry.getKey();

            if (casePositionPiecesEntry.getValue() != null && key != from && key != to) {

                int distanceToOther = BaseUtils.getSafeInteger(MathUtils.getDistanceBetweenPositionsWithCommonDirection(from, key));
                Direction directionToOther = MathUtils.getDirectionFromPosition(from, key);

                if (MathUtils.isPositionInLine(from, to, key) && distanceFromDestination > distanceToOther && directionToOther == directionToDestination) {
                    positions.add(key);
                }
            }
        }

        return positions;
    }

    /**
     * Gets the position of a piece (first found)
     *
     * @param pieces
     * @param positionPiecesMap
     * @return
     */
    public static CasePosition getSinglePiecePosition(Pieces pieces, Map<CasePosition, Pieces> positionPiecesMap) {
        Assert.assertNotNull(pieces);
        CasePosition position = null;

        for (Map.Entry<CasePosition, Pieces> casePositionPiecesEntry : positionPiecesMap.entrySet()) {
            if (pieces.equals(casePositionPiecesEntry.getValue())) {
                position = casePositionPiecesEntry.getKey();
                break;
            }
        }

        return position;
    }


    /**
     * Gets the position of a piece
     *
     * @param pieces
     * @param positionPiecesMap
     * @return
     */
    public static List<CasePosition> getPiecesPosition(Pieces pieces, Map<CasePosition, Pieces> positionPiecesMap) {
        Assert.assertNotNull(pieces);
        Assert.assertNotEmpty(positionPiecesMap);
        List<CasePosition> positions = new ArrayList<>();

        for (Map.Entry<CasePosition, Pieces> casePositionPiecesEntry : positionPiecesMap.entrySet()) {
            if (pieces.equals(casePositionPiecesEntry.getValue())) {
                positions.add(casePositionPiecesEntry.getKey());
            }
        }

        return positions;
    }
}
