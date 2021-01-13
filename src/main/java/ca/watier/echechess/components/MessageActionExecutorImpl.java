/*
 *    Copyright 2014 - 2018 Yannick Watier
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
import ca.watier.echechess.common.enums.KingStatus;
import ca.watier.echechess.common.enums.MoveType;
import ca.watier.echechess.common.enums.Side;
import ca.watier.echechess.common.services.WebSocketService;
import ca.watier.echechess.common.utils.Constants;
import ca.watier.echechess.communication.redis.interfaces.GameRepository;
import ca.watier.echechess.communication.redis.model.GenericGameHandlerWrapper;
import ca.watier.echechess.engine.abstracts.GameBoardData;
import ca.watier.echechess.engine.delegates.PieceMoveConstraintDelegate;
import ca.watier.echechess.engine.engines.GenericGameHandler;
import ca.watier.echechess.models.AvailableMove;
import ca.watier.echechess.models.PawnPromotionViewModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ca.watier.echechess.common.enums.ChessEventMessage.*;
import static ca.watier.echechess.common.enums.Side.getOtherPlayerSide;
import static ca.watier.echechess.common.utils.Constants.PLAYER_KING_CHECKMATE;
import static ca.watier.echechess.common.utils.Constants.PLAYER_MOVE;

/**
 * This implementation is intended to replicate the comportment of a Queue when in independent mode.
 */
public class MessageActionExecutorImpl implements MessageActionExecutor {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MessageActionExecutorImpl.class);
    private static final String REGEX_VALUE_SEPARATOR = "\\|";
    private final PieceMoveConstraintDelegate gameMoveConstraintDelegate;
    private final GameRepository<GenericGameHandler> gameRepository;
    private final WebSocketService webSocketService;
    private final ObjectMapper objectMapper;
    private final CollectionLikeType listStringType;

    public MessageActionExecutorImpl(PieceMoveConstraintDelegate gameMoveConstraintDelegate, GameRepository<GenericGameHandler> gameRepository, WebSocketService webSocketService, ObjectMapper objectMapper) {
        this.gameMoveConstraintDelegate = gameMoveConstraintDelegate;
        this.gameRepository = gameRepository;
        this.webSocketService = webSocketService;
        this.objectMapper = objectMapper;
        TypeFactory typeFactory = this.objectMapper.getTypeFactory();
        listStringType = typeFactory.constructCollectionLikeType(ArrayList.class, String.class);
    }

    @Override
    public void handleMoveResponseMessage(String message) {
        if (StringUtils.isBlank(message)) {
            return;
        }

        handleReceivedMoveMessage(message);
    }

    @Override
    public void handleAvailMoveResponseMessage(String message) {
        if (StringUtils.isBlank(message)) {
            return;
        }

        String[] headers = message.split(REGEX_VALUE_SEPARATOR);
        String uuid = headers[0];
        String fromAsString = headers[1];
        Side playerSide = Side.getFromValue(Byte.parseByte(headers[2]));

        try {
            List<String> positions = objectMapper.readValue(headers[3], listStringType);

            webSocketService.fireSideEvent(uuid, playerSide, AVAILABLE_MOVE, null, new AvailableMove(fromAsString, positions));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void handleReceivedMoveMessage(String messageAsString) {
        String[] messages = messageAsString.split(REGEX_VALUE_SEPARATOR);

        String uuid = messages[0];
        CasePosition from = CasePosition.valueOf(messages[1]);
        CasePosition to = CasePosition.valueOf(messages[2]);
        MoveType moveType = MoveType.getFromValue(Byte.parseByte(messages[3]));
        Side playerSide = Side.getFromValue(Byte.parseByte(messages[4]));

        GenericGameHandlerWrapper<GenericGameHandler> handlerWrapper = gameRepository.get(uuid);
        GenericGameHandler gameFromUuid = handlerWrapper.getGenericGameHandler();

        if (MoveType.isMoved(moveType)) {
            if (MoveType.PAWN_PROMOTION.equals(moveType)) {
                PawnPromotionViewModel viewModel = new PawnPromotionViewModel();
                viewModel.setGameSide(playerSide);
                viewModel.setFrom(from.name());
                viewModel.setTo(to.name());

                webSocketService.fireGameEvent(uuid, PAWN_PROMOTION, viewModel);
            } else {

                GameBoardData boardData = gameFromUuid.getCloneOfCurrentDataState();

                KingStatus currentKingStatus = gameMoveConstraintDelegate.getKingStatus(playerSide, boardData);
                KingStatus otherKingStatus = gameMoveConstraintDelegate.getKingStatus(Side.getOtherPlayerSide(playerSide), boardData);

                sendMovedPieceMessage(from, to, uuid, gameFromUuid, playerSide);
                sendCheckOrCheckmateMessagesIfNeeded(currentKingStatus, otherKingStatus, playerSide, uuid);
            }
        }
    }

    private void sendMovedPieceMessage(CasePosition from, CasePosition to, String uuid, GenericGameHandler gameFromUuid, Side playerSide) {
        webSocketService.fireGameEvent(uuid, MOVE, String.format(PLAYER_MOVE, playerSide, from, to));
        webSocketService.fireSideEvent(uuid, getOtherPlayerSide(playerSide), PLAYER_TURN, Constants.PLAYER_TURN);
        webSocketService.fireGameEvent(uuid, SCORE_UPDATE, gameFromUuid.getGameScore());
    }

    private void sendCheckOrCheckmateMessagesIfNeeded(KingStatus currentKingStatus, KingStatus otherKingStatusAfterMove, Side playerSide, String uuid) {
        if (ObjectUtils.anyNull(currentKingStatus, otherKingStatusAfterMove, playerSide)) {
            return;
        }

        Side otherPlayerSide = getOtherPlayerSide(playerSide);

        if (KingStatus.CHECKMATE.equals(currentKingStatus)) {
            webSocketService.fireGameEvent(uuid, KING_CHECKMATE, String.format(PLAYER_KING_CHECKMATE, playerSide));
        } else if (KingStatus.CHECKMATE.equals(otherKingStatusAfterMove)) {
            webSocketService.fireGameEvent(uuid, KING_CHECKMATE, String.format(PLAYER_KING_CHECKMATE, otherPlayerSide));
        }

        if (KingStatus.CHECK.equals(currentKingStatus)) {
            webSocketService.fireSideEvent(uuid, playerSide, KING_CHECK, Constants.PLAYER_KING_CHECK);
        } else if (KingStatus.CHECK.equals(otherKingStatusAfterMove)) {
            webSocketService.fireSideEvent(uuid, otherPlayerSide, KING_CHECK, Constants.PLAYER_KING_CHECK);
        }
    }
}
