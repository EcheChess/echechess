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
import ca.watier.interfaces.WebSocketService;
import ca.watier.pojos.MoveHistory;
import ca.watier.services.ConstraintService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ca.watier.enums.Side.BLACK;
import static ca.watier.enums.Side.WHITE;

public class PgnParser {
    public static final PgnMoveToken NORMAL_MOVE = PgnMoveToken.NORMAL_MOVE;
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(PgnParser.class);
    private static final List<PgnMoveToken> PAWN_PROMOTION_WITH_CAPTURE_TOKENS = new ArrayList<>();
    private static final Pattern POSITION_PATTERN = Pattern.compile("[a-h][1-8]");
    private static final String THE_GAME_IS_SUPPOSED_TO_BE_WON_BY = "The game is supposed to be won by the {} player";

    static {
        PAWN_PROMOTION_WITH_CAPTURE_TOKENS.add(PgnMoveToken.PAWN_PROMOTION);
        PAWN_PROMOTION_WITH_CAPTURE_TOKENS.add(PgnMoveToken.CAPTURE);
    }

    private final List<GenericGameHandler> handlerList = new ArrayList<>();
    private final ConstraintService constraintService;
    private final WebSocketService webSocketService;

    private GenericGameHandler gameHandler;
    private Side currentSide = WHITE;
    private Side otherSide = BLACK;


    public PgnParser(@NotNull ConstraintService constraintService, @NotNull WebSocketService webSocketService) {
        this.constraintService = constraintService;
        this.webSocketService = webSocketService;
    }

    public List<GenericGameHandler> parse(@NotNull String rawText) {
        String[] headersAndGames = rawText.replace("\r\n", "\n").split("\n\n");
        int nbOfGames = headersAndGames.length;
        int currentIdx = 1;

        for (int i = 0; i < nbOfGames; i = i + 2) {
            String rawCurrentGame = headersAndGames[i + 1];
            String currentGame = rawCurrentGame.substring(2, rawCurrentGame.length()).replace("\n", " ");

            LOGGER.debug("=================================================");
            LOGGER.debug("***{}***", currentIdx);
            LOGGER.debug(currentGame);
            LOGGER.debug("=================================================");

            currentIdx++;

            String[] tokens = currentGame.split("\\s+\\d+\\.");

            if (tokens.length == 0) {
                continue;
            }

            resetSide();
            gameHandler = new GenericGameHandler(constraintService, webSocketService);
            handlerList.add(gameHandler);

            for (String currentToken : tokens) {
                currentToken = currentToken.trim();

                String[] actions = currentToken.split(" ");

                for (String action : actions) {
                    action = action.trim();

                    if (action.isEmpty()) {
                        continue;
                    }

                    parseAction(action);
                }
            }
        }

        return handlerList;
    }

    private void resetSide() {
        currentSide = WHITE;
        otherSide = BLACK;
    }

    private void parseAction(String action) {
        PgnEndGameToken endGameTokenByAction = PgnEndGameToken.getEndGameTokenByAction(action);
        if (PgnEndGameToken.isGameEnded(endGameTokenByAction)) {
            LOGGER.info("Game ending code ({})", endGameTokenByAction);
            validateGameEnding(endGameTokenByAction);
            return;
        }

        List<PgnMoveToken> pieceMovesFromLetter = PgnMoveToken.getPieceMovesFromLetter(action);
        boolean pawnPromotionWithCapture = pieceMovesFromLetter.containsAll(PAWN_PROMOTION_WITH_CAPTURE_TOKENS);

        for (PgnMoveToken pgnMoveToken : pieceMovesFromLetter) {
            switch (pgnMoveToken) {
                case KINGSIDE_CASTLING_CHECK:
                case QUEENSIDE_CASTLING_CHECK:
                    executeCastling(pgnMoveToken);
                    validateCheck();
                    break;
                case KINGSIDE_CASTLING_CHECKMATE:
                case QUEENSIDE_CASTLING_CHECKMATE:
                    executeCastling(pgnMoveToken);
                    validateCheckMate();
                    break;
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
                    executeCastling(pgnMoveToken);
                    break;
                case PAWN_PROMOTION:
                    validatePawnPromotion(pawnPromotionWithCapture);
                    break;
            }
        }

        switchSide();
    }

    private void validateGameEnding(@NotNull PgnEndGameToken ending) {
        switch (ending) {
            case WHITE_WIN:
                if (!(gameHandler.isGameDone() && KingStatus.CHECKMATE.equals(gameHandler.getKingStatus(BLACK, false)))) {
                    LOGGER.error(THE_GAME_IS_SUPPOSED_TO_BE_WON_BY, WHITE);
                }
                break;
            case BLACK_WIN:
                if (!(gameHandler.isGameDone() && KingStatus.CHECKMATE.equals(gameHandler.getKingStatus(WHITE, false)))) {
                    LOGGER.error(THE_GAME_IS_SUPPOSED_TO_BE_WON_BY, BLACK);
                }
                break;
            case DRAWN:
                if (!gameHandler.isGameDraw()) {
                    LOGGER.error("The game is supposed to be DRAWN");
                }
                break;
            case STILL_IN_PROGRESS:
            case UNKNOWN:
                LOGGER.error("The game ending is not known ({})", ending);
                break;
        }
    }

    private void executeCastling(PgnMoveToken pgnMoveToken) {
        Map<CasePosition, Pieces> piecesLocation = gameHandler.getPiecesLocation(currentSide);
        CasePosition kingPosition = null;
        for (Map.Entry<CasePosition, Pieces> casePositionPiecesEntry : piecesLocation.entrySet()) {
            CasePosition key = casePositionPiecesEntry.getKey();
            Pieces value = casePositionPiecesEntry.getValue();

            if (Pieces.isKing(value)) {
                kingPosition = key;
            }
        }

        CasePosition selectedRookPosition = PgnMoveToken.getCastlingRookPosition(pgnMoveToken, currentSide);

        if (ca.watier.enums.MoveType.CASTLING.equals(gameHandler.movePiece(kingPosition, selectedRookPosition, currentSide))) {
            LOGGER.debug("Castling: King -> {} | Rook {} | ({})", kingPosition, selectedRookPosition, currentSide);
        } else { //Issue with the move / case
            LOGGER.error("Unable to cast at the selected position {} for the current color {} !", selectedRookPosition, currentSide);
        }
    }

    private void validateCheck() {
        if (!KingStatus.CHECK.equals(gameHandler.getKingStatus(otherSide, false))) {
            throw new IllegalStateException("The other player king is not check!");
        } else {
            LOGGER.debug("{} is CHECK", otherSide);
        }
    }

    private void validateCheckMate() {
        if (!KingStatus.CHECKMATE.equals(gameHandler.getKingStatus(otherSide, false))) {
            throw new IllegalStateException("The other player king is not check!");
        } else {
            LOGGER.debug("{} is CHECKMATE", otherSide);
        }
    }

    private void executeMove(@NotNull String action) {

        List<String> casePositions = getPositionsFromAction(action);
        List<PgnMoveToken> pieceMovesFromLetter = PgnMoveToken.getPieceMovesFromLetter(action);

        String position = casePositions.get(0);
        CasePosition to = CasePosition.valueOf(position.toUpperCase());

        PgnPieceFound pgnPieceFound = isPawnPromotion(action, pieceMovesFromLetter);
        List<Pieces> validPiecesFromAction = pgnPieceFound.getPieces();
        List<Pair<CasePosition, Pieces>> piecesThatCanHitPosition = gameHandler.getAllPiecesThatCanMoveTo(to, currentSide);
        MultiArrayMap<Pieces, Pair<CasePosition, Pieces>> similarPieceThatHitTarget = getSimilarPiecesThatCanHitSameTarget(piecesThatCanHitPosition, validPiecesFromAction);

        CasePosition from = (!similarPieceThatHitTarget.isEmpty() ?
                getPositionWhenMultipleTargetCanHit(action, casePositions, similarPieceThatHitTarget) :
                getPositionWhenOneTargetCanHit(piecesThatCanHitPosition, pgnPieceFound));

        LOGGER.debug("MOVE {} to {} ({}) | action -> {}", from, to, currentSide, action);
        MoveType moveType = gameHandler.movePiece(from, to, currentSide);

        if (MoveType.PAWN_PROMOTION.equals(moveType)) {
            PgnPieceFound pieceFromAction = PgnPieceFound.getPieceFromAction(action);
            Pieces pieceBySide = pieceFromAction.getPieceBySide(currentSide);
            gameHandler.upgradePiece(to, pieceBySide, currentSide);
        } else if (!(MoveType.NORMAL_MOVE.equals(moveType) || MoveType.CAPTURE.equals(moveType) || MoveType.EN_PASSANT.equals(moveType))) {  //Issue with the move / case
            LOGGER.error("Unable to move at the selected position {} for the current color {} !", position, currentSide);
        }
    }

    private void validateCapture() {
        List<MoveHistory> moveHistory = gameHandler.getMoveHistory();
        MoveHistory lastMoveHistory = moveHistory.get(moveHistory.size() - 1);
        MoveType moveType = lastMoveHistory.getMoveType();

        if (!(MoveType.CAPTURE.equals(moveType) || MoveType.EN_PASSANT.equals(moveType))) {
            throw new IllegalStateException("The capture is not in the history!");
        }
    }

    private void validatePawnPromotion(boolean pawnPromotionWithCapture) {
        List<MoveHistory> moveHistory = gameHandler.getMoveHistory();

        //In case of a capture and a pawn promotion in the same turn, the history index of the promotion is before the capture
        MoveHistory lastMoveHistory =
                pawnPromotionWithCapture ?
                        moveHistory.get(moveHistory.size() - 2) :
                        moveHistory.get(moveHistory.size() - 1);

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

    private PgnPieceFound isPawnPromotion(@NotNull String action, List<PgnMoveToken> pieceMovesFromLetter) {
        return pieceMovesFromLetter.contains(PgnMoveToken.PAWN_PROMOTION) ? PgnPieceFound.PAWN : PgnPieceFound.getPieceFromAction(action);
    }

    private MultiArrayMap<Pieces, Pair<CasePosition, Pieces>> getSimilarPiecesThatCanHitSameTarget(List<Pair<CasePosition, Pieces>> piecesThatCanHitPosition, List<Pieces> validPiecesFromAction) {
        MultiArrayMap<Pieces, Pair<CasePosition, Pieces>> similarPieceThatHitTarget = new MultiArrayMap<>();

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

        return similarPieceThatHitTarget;
    }

    private CasePosition getPositionWhenMultipleTargetCanHit(@NotNull String action, List<String> casePositions, MultiArrayMap<Pieces, Pair<CasePosition, Pieces>> similarPieceThatHitTarget) {
        CasePosition value = null;
        int length = casePositions.size();
        char colOrRow = findFirstColOrRowInAction(action);
        mainLoop:
        for (Map.Entry<Pieces, List<Pair<CasePosition, Pieces>>> piecesListEntry : similarPieceThatHitTarget.entrySet()) {
            for (Pair<CasePosition, Pieces> casePositionPiecesPair : piecesListEntry.getValue()) {
                if (length == 1) {
                    CasePosition firstValue = casePositionPiecesPair.getFirstValue();
                    if ((Character.isLetter(colOrRow) && firstValue.isOnSameColumn(colOrRow)) ||   //col (letter)
                            (Character.isDigit(colOrRow) && firstValue.isOnSameRow(colOrRow))) {  //row (number)
                        value = firstValue;
                        break mainLoop;
                    }
                } else if (length == 2) { //Extract the full coordinate
                    throw new IllegalStateException("The full coordinate is not implemented yet");
                } else {
                    throw new IllegalStateException("Invalid type of positioning");
                }
            }
        }
        return value;
    }

    private CasePosition getPositionWhenOneTargetCanHit(List<Pair<CasePosition, Pieces>> piecesThatCanHitPosition, PgnPieceFound pgnPieceFound) {
        CasePosition value = null;

        for (Pair<CasePosition, Pieces> casePositionPiecesPair : piecesThatCanHitPosition) {
            CasePosition casePosition = casePositionPiecesPair.getFirstValue();
            Pieces pieces = casePositionPiecesPair.getSecondValue();

            if ((Pieces.isPawn(pieces) && PgnPieceFound.PAWN.equals(pgnPieceFound)) ||
                    (Pieces.isBishop(pieces) && PgnPieceFound.BISHOP.equals(pgnPieceFound)) ||
                    (Pieces.isKing(pieces) && PgnPieceFound.KING.equals(pgnPieceFound)) ||
                    (Pieces.isKnight(pieces) && PgnPieceFound.KNIGHT.equals(pgnPieceFound)) ||
                    (Pieces.isQueen(pieces) && PgnPieceFound.QUEEN.equals(pgnPieceFound)) ||
                    (Pieces.isRook(pieces) && PgnPieceFound.ROOK.equals(pgnPieceFound))) {
                value = casePosition;

                break;
            }
        }

        return value;
    }

    private char findFirstColOrRowInAction(@NotNull String action) {
        char value = '\0';

        for (char c : action.toCharArray()) {
            if (Character.isDigit(c)) {
                int numericValue = Character.getNumericValue(c);

                if (numericValue >= 1 && numericValue <= 8) {
                    value = c;
                    break;
                }
            } else if (Character.isLetter(c) && Character.isLowerCase(c) && (c >= 'a' && c <= 'h')) {
                value = c;
                break;
            }
        }

        return value;
    }
}