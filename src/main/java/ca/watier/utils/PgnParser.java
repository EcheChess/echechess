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

import ca.watier.enums.*;
import ca.watier.game.GenericGameHandler;
import ca.watier.pojos.MoveHistory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ca.watier.enums.Side.BLACK;
import static ca.watier.enums.Side.WHITE;

public class PgnParser {
    public static final MoveToken NORMAL_MOVE = MoveToken.NORMAL_MOVE;
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(PgnParser.class);
    private static final Map<CasePosition, Pieces> DEFAULT_GAME_TEMPLATE = new EnumMap<>(CasePosition.class);
    private final static Pattern POSITION_PATTERN = Pattern.compile("[a-h][1-8]");

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
    }

    private GenericGameHandler gameHandler;
    private Side currentSide = WHITE;
    private Side otherSide = BLACK;

    public PgnParser(@NotNull GenericGameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    public String parse(@NotNull String rawText) {
        String[] headersAndGames = rawText.replace("\r\n", "\n").split("\n\n");
        int nbOfGames = headersAndGames.length / 2;

        for (int i = 0; i < nbOfGames; i = i + 2) {
            String rawHeaders = headersAndGames[i];
            String[] currentHeaders = rawHeaders.split("\n");
            String rawCurrentGame = headersAndGames[i + 1];
            String currentGame = rawCurrentGame.substring(2, rawCurrentGame.length()).replace("\n", " ");
            String[] tokens = currentGame.split("\\s+\\d+\\.");

            if (tokens.length == 0) {
                continue;
            }

            for (String currentToken : tokens) {
                String[] actions = currentToken.split(" ");

                /*
                        Q:  Queen
                        B:  Bishop
                        K:  King
                        R:  Rook
                        N:  Knight

                        O-O:        kingside castling
                        O-O-O:      queenside castling

                        x:          capture move
                        +:          checking move
                        # or ++:    checkmating move

                        White wins:             1-0
                        Black wins:             0-1
                        drawn game:             1/2-1/2
                        game still in progress: *
                */

                for (String action : actions) {
                    parseAction(action);
                }
            }

            //FIXME: Reset the gameHandler
        }

        return "";
    }

    private void parseAction(String action) {
        for (MoveToken moveToken : getPieceMovesFromLetter(action)) {
            switch (moveToken) {
                case NORMAL_MOVE:
                    executeMove(action);
                    break;
                case CAPTURE:
                    validateCapture();
                    break;
                case CHECK:
                    validateCheck();
                    break;
                case CHECKMATE:
                    validateCheckMate();
                    break;
                case KINGSIDE_CASTLING:
                case QUEENSIDE_CASTLING:
                    executeCastling(action, moveToken);
                    break;
                case PAWN_PROMOTION:
                    validatePawnPromotion();
                    break;
            }
        }

        switchSide();
    }

    private List<MoveToken> getPieceMovesFromLetter(@NotNull String action) {
        List<MoveToken> moves = new ArrayList<>();

        for (MoveToken moveToken : MoveToken.values()) {
            for (String current : moveToken.getChars()) {
                if (action.contains(current)) {
                    switch (moveToken) { //The moves that contain a "normal move"
                        case CAPTURE:
                        case CHECK:
                        case CHECKMATE:
                        case PAWN_PROMOTION:
                            if (!moves.contains(NORMAL_MOVE)) {
                                moves.add(NORMAL_MOVE);
                            }
                            break;
                    }
                    moves.add(moveToken);
                }
            }
        }

        if (moves.isEmpty()) {
            moves.add(NORMAL_MOVE);
        }

        return moves;
    }

    private void executeMove(@NotNull String action) {

        List<String> casePositions = getPositionsFromAction(action);
        List<MoveToken> pieceMovesFromLetter = getPieceMovesFromLetter(action);
        String position = casePositions.get(0);
        CasePosition casePositionTo = CasePosition.valueOf(position.toUpperCase());
        MultiArrayMap<Pieces, Pair<CasePosition, Pieces>> similarPieceThatHitTarget = new MultiArrayMap<>();
        List<Pair<CasePosition, Pieces>> piecesThatCanHitPosition = gameHandler.getAllPiecesThatCanMoveTo(casePositionTo, currentSide);
        CasePosition casePositionFrom = null;

        boolean isPawnPromotion = pieceMovesFromLetter.contains(MoveToken.PAWN_PROMOTION);
        PieceFound pieceFound = isPawnPromotion ? PieceFound.PAWN : getPieceFromAction(action);
        List<Pieces> validPiecesFromAction = pieceFound.getPieces();

        //Group all similar pieces that can hit the target
        for (int i = 0; i < piecesThatCanHitPosition.size(); i++) {
            Pair<CasePosition, Pieces> firstLayer = piecesThatCanHitPosition.get(i);
            CasePosition firstPosition = firstLayer.getFirstValue();
            Pieces firstPiece = firstLayer.getSecondValue();

            if (!validPiecesFromAction.contains(firstPiece)) {
                continue;
            }

            boolean isOtherFound = false;

            for (int j = (i + 1); j < piecesThatCanHitPosition.size(); j++) {
                Pair<CasePosition, Pieces> secondLayer = piecesThatCanHitPosition.get(j);
                CasePosition secondPosition = secondLayer.getFirstValue();
                Pieces secondPiece = secondLayer.getSecondValue();

                if (firstPiece.equals(secondPiece)) {
                    similarPieceThatHitTarget.put(secondPiece, new Pair<>(secondPosition, secondPiece));
                    isOtherFound = true;
                }
            }

            if (isOtherFound) {
                similarPieceThatHitTarget.put(firstPiece, new Pair<>(firstPosition, firstPiece));
            }
        }

        if (!similarPieceThatHitTarget.isEmpty()) {
            int length = action.length();
            char colOrRow = action.charAt(1);
            mainLoop:
            for (Map.Entry<Pieces, List<Pair<CasePosition, Pieces>>> piecesListEntry : similarPieceThatHitTarget.entrySet()) {
                for (Pair<CasePosition, Pieces> casePositionPiecesPair : piecesListEntry.getValue()) {
                    if (length == 4) { //Extract the columns or row
                        if (Character.isLetter(colOrRow) && casePositionPiecesPair.getFirstValue().isOnSameColumn(colOrRow)) { //col (letter)
                            casePositionFrom = casePositionPiecesPair.getFirstValue();
                            break mainLoop;
                        } else if (Character.isDigit(colOrRow) && casePositionPiecesPair.getFirstValue().isOnSameRow(colOrRow)) { //row (number)
                            casePositionFrom = casePositionPiecesPair.getFirstValue();
                            break mainLoop;
                        }
                    } else if (length == 5) {//Extract the full coordinate
                        throw new NotImplementedException();
                    }
                }
            }
        } else {
            for (Pair<CasePosition, Pieces> casePositionPiecesPair : piecesThatCanHitPosition) {
                CasePosition casePosition = casePositionPiecesPair.getFirstValue();
                Pieces pieces = casePositionPiecesPair.getSecondValue();

                //FIXME: Merge in one if ?
                if (Pieces.isPawn(pieces) && PieceFound.PAWN.equals(pieceFound)) {
                    casePositionFrom = casePosition;
                    break;
                } else if (Pieces.isBishop(pieces) && PieceFound.BISHOP.equals(pieceFound)) {
                    casePositionFrom = casePosition;
                    break;
                } else if (Pieces.isKing(pieces) && PieceFound.KING.equals(pieceFound)) {
                    casePositionFrom = casePosition;
                    break;
                } else if (Pieces.isKnight(pieces) && PieceFound.KNIGHT.equals(pieceFound)) {
                    casePositionFrom = casePosition;
                    break;
                } else if (Pieces.isQueen(pieces) && PieceFound.QUEEN.equals(pieceFound)) {
                    casePositionFrom = casePosition;
                    break;
                } else if (Pieces.isRook(pieces) && PieceFound.ROOK.equals(pieceFound)) {
                    casePositionFrom = casePosition;
                    break;
                }
            }
        }

        LOGGER.debug(String.format("MOVE %s to %s (%s)", casePositionFrom, casePositionTo, currentSide));
        MoveType moveType = gameHandler.movePiece(casePositionFrom, casePositionTo, currentSide);

        if (MoveType.PAWN_PROMOTION.equals(moveType)) {
            PieceFound pieceFromAction = getPieceFromAction(action);
            Pieces pieceBySide = pieceFromAction.getPieceBySide(currentSide);
            gameHandler.upgradePiece(casePositionTo, pieceBySide, currentSide);
        } else if (!(MoveType.NORMAL_MOVE.equals(moveType) || MoveType.CAPTURE.equals(moveType))) {  //Issue with the move / case
            LOGGER.error(String.format("Unable to move at the selected position %s for the current color %s !", position, currentSide));
        }
    }

    private void validateCapture() {
        List<MoveHistory> moveHistory = gameHandler.getMoveHistory();
        MoveHistory lastMoveHistory = moveHistory.get(moveHistory.size() - 1);

        if (!MoveType.CAPTURE.equals(lastMoveHistory.getMoveType())) {
            throw new IllegalStateException("The capture is not in the history!");
        }
    }

    private void validateCheck() {
        if (!KingStatus.CHECK.equals(gameHandler.getKingStatus(otherSide, false))) {
            throw new IllegalStateException("The other player king is not check!");
        } else {
            LOGGER.debug(String.format("%s is CHECK", otherSide));
        }
    }

    private void validateCheckMate() {
        if (!KingStatus.CHECKMATE.equals(gameHandler.getKingStatus(otherSide, false))) {
            throw new IllegalStateException("The other player king is not check!");
        } else {
            LOGGER.debug(String.format("%s is CHECKMATE", otherSide));
        }
    }

    private void executeCastling(@NotNull String action, MoveToken moveToken) {
        Map<CasePosition, Pieces> piecesLocation = gameHandler.getPiecesLocation(currentSide);
        List<CasePosition> rookPositions = new ArrayList<>();
        CasePosition kingPosition = null;
        CasePosition selectedRookPosition = null;
        for (Map.Entry<CasePosition, Pieces> casePositionPiecesEntry : piecesLocation.entrySet()) {
            CasePosition key = casePositionPiecesEntry.getKey();
            Pieces value = casePositionPiecesEntry.getValue();

            if (Pieces.isKing(value)) {
                kingPosition = key;
            } else if (Pieces.isRook(value)) {
                rookPositions.add(key);
            }
        }

        //Find the rook position
        for (CasePosition position : rookPositions) {
            boolean isQueenSide = CastlingPositionHelper.isQueenSide(kingPosition, position);

            if ("O-O".equals(action) && !isQueenSide) { //kingside castling (right)
                selectedRookPosition = position;
                break;
            } else if ("O-O-O".equals(action) && isQueenSide) { //queenside castling (left)
                selectedRookPosition = position;
                break;
            }
        }

        if (!(ca.watier.enums.MoveType.CASTLING.equals(gameHandler.movePiece(kingPosition, selectedRookPosition, currentSide)))) {  //Issue with the move / case
            LOGGER.error(String.format("Unable to cast at the selected position %s for the current color %s !", selectedRookPosition, currentSide));
        }
    }

    private void validatePawnPromotion() {
        List<MoveHistory> moveHistory = gameHandler.getMoveHistory();
        MoveHistory lastMoveHistory = moveHistory.get(moveHistory.size() - 1);

        if (!MoveType.PAWN_PROMOTION.equals(lastMoveHistory.getMoveType())) {
            throw new IllegalStateException("The pawn promotion is not in the history!");
        }
    }

    private void switchSide() {
        if (BLACK.equals(currentSide)) {
            currentSide = WHITE;
            otherSide = BLACK;
        } else {
            currentSide = BLACK;
            otherSide = WHITE;
        }
    }

    private @NotNull List<String> getPositionsFromAction(@NotNull String action) {
        Matcher m = POSITION_PATTERN.matcher(action);
        List<String> casePositions = new ArrayList<>();

        while (m.find()) {
            String currentPosition = m.group();
            casePositions.add(currentPosition);
        }
        return casePositions;
    }

    private PieceFound getPieceFromAction(@NotNull String action) {
        PieceFound pieceFoundFromLetter = PieceFound.PAWN;

        for (byte b : action.getBytes()) {
            PieceFound currentPieceFound = PieceFound.getPieceFromLetter((char) b);

            if (!PieceFound.PAWN.equals(currentPieceFound)) {
                pieceFoundFromLetter = currentPieceFound;
                break;
            }
        }

        return pieceFoundFromLetter;
    }

    private enum MoveToken {
        CAPTURE("x"), CHECK("+"), CHECKMATE("#", "++"), PAWN_PROMOTION("="), KINGSIDE_CASTLING("O-O"), QUEENSIDE_CASTLING("O-O-O"), NORMAL_MOVE("\0");
        private List<String> chars = new ArrayList<>();

        MoveToken(@NotNull String... chars) {
            if (chars.length > 0) {
                this.chars.addAll(Arrays.asList(chars));
            }
        }

        public List<String> getChars() {
            return chars;
        }
    }

    private enum PieceFound {
        QUEEN('Q', Pieces.B_QUEEN, Pieces.W_QUEEN),
        BISHOP('B', Pieces.B_BISHOP, Pieces.W_BISHOP),
        KING('K', Pieces.W_KING, Pieces.B_KING),
        ROOK('R', Pieces.B_ROOK, Pieces.W_ROOK),
        KNIGHT('N', Pieces.B_KNIGHT, Pieces.W_KNIGHT),
        PAWN('\0', Pieces.B_PAWN, Pieces.W_PAWN);

        private char letter;
        private List<Pieces> pieces = new ArrayList<>();

        PieceFound(char letter, Pieces... pieces) {
            this.letter = letter;

            if (pieces != null) {
                this.pieces.addAll(Arrays.asList(pieces));
            }
        }

        public static PieceFound getPieceFromLetter(char letter) {
            for (PieceFound pieceFound : PieceFound.values()) {
                if (pieceFound.getLetter() == letter) {
                    return pieceFound;
                }
            }

            return PieceFound.PAWN;
        }

        public char getLetter() {
            return letter;
        }

        public Pieces getPieceBySide(Side side) {
            Pieces value = null;
            for (Pieces piece : pieces) {
                if (piece.getSide() == side) {
                    value = piece;
                    break;
                }
            }
            return value;
        }

        public List<Pieces> getPieces() {
            return pieces;
        }
    }
}
