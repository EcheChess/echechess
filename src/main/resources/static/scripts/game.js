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

/**
 * Created by yannick on 4/18/2017.
 */
let currentUuid = null;
let lastSelectedBoardSquareHelper = null;
let helperSetItemMap = [];

$(document).ready(function () {
    initUiTriggers();
    drawBoard(null, "#board");

    $("#createGame").click(function () {
        currentUuid = createNewGame();
        ConnexionManager.connect(currentUuid, renderBoard, writeToGameLog);
        ConnexionManager.connectSideEvent(currentUuid, writeToGameLog);

        $('#uuid').text(currentUuid);
        renderBoard();
    });

    $("#joinGameButton").click(function () {
        let enteredUuid = $("#joinGame").val();

        if (enteredUuid !== '' && enteredUuid.length === 36) {
            currentUuid = enteredUuid;

            let side = $("#changeSide").find("option:selected").val();
            let response = jsonFromRequest("POST", '/game/join', {
                side: side,
                uuid: currentUuid
            }).response;

            if (response) {
                alertify.success("Joined game " + currentUuid, 6);
            } else {
                alertify.error("Unable to join the game: " + response.message, 6);
            }

            ConnexionManager.connect(currentUuid, renderBoard, writeToGameLog);
            renderBoard();
            ConnexionManager.connectSideEvent(currentUuid, writeToGameLog);
        }
    });

    $('.menu .item').tab();
});


function writeToGameLog(message, type) {
    $('#chessLog').append("<option>" + message + "</option>");

    switch (type) {
        case 'MOVE':
        case 'PLAYER_JOINED':
        case 'GAME_WON':
        case 'GAME_WON_EVENT_MOVE':
            alertify.notify(message, 'custom', 3);
            break;
        case 'PLAYER_TURN':
            alertify.success(message, 6);
            break;
    }
}

function setHelperBoardEvents() {
    $("#helperBoard").find("tr > td.board-square").popup({
        on: 'click',
        hoverable: true,
        html: "<div class='header'>Choose a piece</div><div class='content'>" +
        "<table>" +
        "<tr>" +
        "<td class='helperBordPieces' data-helper-icon='W_KING'>♔</td>" +
        "<td class='helperBordPieces' data-helper-icon='W_QUEEN'>♕</td>" +
        "<td class='helperBordPieces' data-helper-icon='W_ROOK'>♖</td>" +
        "<td class='helperBordPieces' data-helper-icon='W_BISHOP'>♗</td>" +
        "<td class='helperBordPieces' data-helper-icon='W_KNIGHT'>♘</td>" +
        "<td class='helperBordPieces' data-helper-icon='W_PAWN'>♙</td>" +
        "</tr>" +
        "<tr>" +
        "<td class='helperBordPieces' data-helper-icon='B_KING'>♚</td>" +
        "<td class='helperBordPieces' data-helper-icon='B_QUEEN'>♛</td>" +
        "<td class='helperBordPieces' data-helper-icon='B_ROOK'>♜</td>" +
        "<td class='helperBordPieces' data-helper-icon='B_BISHOP'>♝</td>" +
        "<td class='helperBordPieces' data-helper-icon='B_KNIGHT'>♞</td>" +
        "<td class='helperBordPieces' data-helper-icon='B_PAWN'>♟</td>" +
        "</tr>" +
        "</table>" +
        "<button id='helperActionRemovePiece' class='ui red basic button'>Empty the case</button>" +
        "</div>"
    });
}

function writeToSpecialGameInput(tempStrucks) {
    let values = "";

    if (tempStrucks) {
        tempStrucks.forEach(function (element) {
            values += element.caseName;
            values += ':';
            values += element.iconName;
            values += ';';
        });
        values = values.substring(0, values.length - 1); //remove the ';' at the end
    } else {
        values = null;
    }

    $("#inputValidatePatternSpecialGame").val(values);
}

function convertSpecialGameToDrawable() {
    let tempStrucks = [];
    for (let key in helperSetItemMap) {
        let struct = helperSetItemMap[key];
        tempStrucks.push({
            iconName: struct.name,
            caseName: struct.caseName,
            value1: {
                x: struct.x,
                y: struct.y
            }, value2: {
                unicodeIcon: struct.icon
            }
        });
    }
    return tempStrucks;
}

function initHelperEvents() {
    setHelperBoardEvents();

    $(document).on("click", ".helperBordPieces", function () {
        let $currentCase = $(lastSelectedBoardSquareHelper);
        let caseName = $currentCase.attr("data-case-id");
        helperSetItemMap[caseName] =
            {
                icon: $(this).text(),
                name: $(this).attr("data-helper-icon"),
                caseName: $currentCase.attr("data-case-id"),
                x: parseInt($currentCase.attr("data-case-x")),
                y: parseInt($currentCase.attr("data-case-y"))
            };

        let tempStrucks = convertSpecialGameToDrawable();
        drawBoard(tempStrucks, "#helperBoard");
        setHelperBoardEvents();
        writeToSpecialGameInput(tempStrucks);
    });

    $(document).on("click", "#helperActionRemovePiece", function () {
        let $currentCase = $(lastSelectedBoardSquareHelper);
        let caseName = $currentCase.attr("data-case-id");
        delete helperSetItemMap[caseName];
        let tempStrucks = convertSpecialGameToDrawable();
        drawBoard(tempStrucks, "#helperBoard");
        setHelperBoardEvents();
        writeToSpecialGameInput(tempStrucks);
    });

    $(document).on("click", "#helperBoard > tr > td.board-square", function () {
        lastSelectedBoardSquareHelper = $(this);
    });
}

function initUiTriggers() {
    let $changeSide = $('#changeSide');
    let $gameType = $('#gameType');
    let $specialGameLabel = $('#specialGamePiecesLabel');
    let $specialGame = $('#specialGamePieces');

    $("#divSpecialGamePieces").popup({
        hoverable: true,
        html: "<div class='header'>Use this format</div><div class='content'>Position" +
        "<span class='special-game-item-spacer'>:</span>" +
        "Piece" +
        "<span class='special-game-item-spacer'>;</span>" +
        "Position" +
        "<span class='special-game-item-spacer'>:</span>" +
        "Piece..." +
        "<br>" +
        "Click <button id='moreInfoSpecialGame'>HERE</button> to show the helper" +
        "</div>"
    });

    $("#buttonValidatePatternSpecialGame").click(function () {
        let $iconValidatePatternSpecialGame = $("#iconValidatePatternSpecialGame");

        if (isSpecialGamePatternValid()) {
            $iconValidatePatternSpecialGame.removeClass('warning');
            $iconValidatePatternSpecialGame.removeClass('remove');
            $iconValidatePatternSpecialGame.removeClass('red');
            $iconValidatePatternSpecialGame.addClass('check');
            $iconValidatePatternSpecialGame.addClass('green');

            helperSetItemMap = [];

            let values = $("#inputValidatePatternSpecialGame").val().split(";");
            for (let i = 0; i < values.length; i++) {
                let items = values[i].split(":");
                let caseName = items[0];
                let pieceName = items[1];
                let coordinates = BoardHelper.getCoordinateFromCaseId(caseName);
                helperSetItemMap[caseName] =
                    {
                        icon: BoardHelper.getPieceIconByPieceName(pieceName),
                        name: pieceName,
                        caseName: caseName,
                        x: parseInt(coordinates.x),
                        y: parseInt(coordinates.y)
                    };

                drawBoard(convertSpecialGameToDrawable(), "#helperBoard");
                setHelperBoardEvents();
            }
        } else {
            $iconValidatePatternSpecialGame.removeClass('warning');
            $iconValidatePatternSpecialGame.removeClass('check');
            $iconValidatePatternSpecialGame.removeClass('green');
            $iconValidatePatternSpecialGame.addClass('remove');
            $iconValidatePatternSpecialGame.addClass('red');
        }
    });

    $("#buttonSendPatternSpecialGame").click(function () {
        $("#specialGamePieces").val($("#inputValidatePatternSpecialGame").val());
        $('#modalSpecialGameMoreInfo').modal('hide');
    });

    $("#buttonResetPatternSpecialGame").click(function () {
        helperSetItemMap = [];
        drawBoard(null, "#helperBoard");
        writeToSpecialGameInput(null);
        setHelperBoardEvents();
    });

    $changeSide.change(function () {
        if (currentUuid) {
            let response = jsonFromRequest("POST", '/game/side', {
                side: $(this).find("option:selected").val(),
                uuid: currentUuid
            }).response;

            if (response) {
                alertify.success("Side changed with success !", 6);
                ConnexionManager.connectSideEvent(currentUuid, writeToGameLog);
            } else {
                alertify.error("Unable to change side !", 6);
            }
        }
    });

    $gameType.change(function () {
        if ($(this).find("option:selected").val() === 'SPECIAL') {
            $specialGameLabel.show();
            $specialGame.show();
        } else { //Reset
            $specialGameLabel.hide();
            $specialGame.hide();
        }
    });

    $changeSide.dropdown({
        allowAdditions: true
    });

    $gameType.dropdown({
        allowAdditions: true
    });

    $(document).on("click", "#moreInfoSpecialGame", function () {
        let $modalSpecialGameMoreInfo = $('#modalSpecialGameMoreInfo');
        $modalSpecialGameMoreInfo.modal('show');
        drawBoard(convertSpecialGameToDrawable(), "#helperBoard");
        initHelperEvents();
        $modalSpecialGameMoreInfo.modal('refresh');
    });
}

function jsonFromRequest(type, url, data) {
    let value = null;

    $.ajax({
        url: url,
        type: type,
        data: data,
        async: false,
        cache: false,
        timeout: 30000,
        success: function (json) {
            value = json;
        }
    });

    return value;
}

function createNewGame() {
    return jsonFromRequest('POST', '/game/create', {
        side: $("#changeSide").find("option:selected").val(),
        againstComputer: $("#againstComputer").checkbox('is checked'),
        observers: $("#allowOtherObserver").checkbox('is checked'),
        specialGamePieces: $("#specialGamePieces").val()
    }).uuid;
}

function renderBoard() {

    if (!currentUuid) {
        return;
    }

    let piecesLocation = jsonFromRequest('GET', '/game/pieces', {
        uuid: currentUuid
    });

    drawBoard(piecesLocation, "#board");

    let sourceEvt = null;

    let $currentPiece = $(".board-pieces");
    $currentPiece.draggable({
        containment: "#board",
        helper: "clone",
        start: function () {
            sourceEvt = $(this).parent();
        }
    });

    $(".board-square").droppable({
        drop: function (event, ui) {
            let from = $(sourceEvt).attr("data-case-id");
            let to = $(this).attr("data-case-id");

            let response = jsonFromRequest('POST', '/game/move', {
                from: from,
                to: to,
                uuid: currentUuid
            }).response;

            if (response) {
                $(this).find('.board-pieces').each(function () {
                    let item = $(this).text();

                    if (item) {
                        $(this).hide();
                    }
                });

                $(this).append(ui.draggable);
            }

            $currentPiece.draggable("option", "revert", !response);
        }
    });

    let $boardCaseWithPieceSelector = $(".board-square > span.board-pieces");

    $boardCaseWithPieceSelector.mouseover(function () {
        let piecesLocation = jsonFromRequest('GET', '/game/moves', {
            from: $(this).parent().attr("data-case-id"),
            uuid: currentUuid
        });

        if (piecesLocation) {
            for (let i = 0; i < piecesLocation.length; i++) {
                $("[data-case-id='" + piecesLocation[i] + "']").addClass("pieceAvailMoves");
            }
        }
    });


    $boardCaseWithPieceSelector.mouseleave(function () {
        $("td").removeClass("pieceAvailMoves");
    });
}

function drawBoard(piecesLocation, boardId) {
    let $board = $(boardId);
    $board.empty();

    let tableInnerHtml = '';
    let caseColorIndex = 0;
    let letterIdx = 0;
    let numberIdx = 8;

    for (let y = 4; y > -4; y--) { //lines
        letterIdx = 0;
        tableInnerHtml += '<tr><td class="board-number">' + (y + 4) + '</td>';
        for (let x = -4; x < 4; x++) { //columns
            let caseLetter = BoardHelper.BOARD_COLUMN_LETTERS[letterIdx];
            let caseColor = (((caseColorIndex & 1) === 1) ? 'black' : 'white');
            let pieceIcon = '';

            if (piecesLocation) {
                for (let key in piecesLocation) {
                    let currentPiece = piecesLocation[key];
                    let currentElementX = currentPiece.value1.x;
                    let currentElementY = currentPiece.value1.y;

                    if (x === currentElementX && y === currentElementY) {
                        pieceIcon = currentPiece.value2.unicodeIcon;
                        break;
                    }
                }
            }

            tableInnerHtml += '<td data-case-id="' + caseLetter + numberIdx + '" data-case-x="' + x + '" data-case-y="' + y + '" class="board-square ' + caseColor + '"><span class="board-pieces">' + pieceIcon + '</span></td>';
            caseColorIndex++;
            letterIdx++;
        }
        numberIdx--;
        caseColorIndex++;
        tableInnerHtml += '</tr>';
    }
    tableInnerHtml += '<tr><td></td>' +
        '<td class="board-letter">' + BoardHelper.BOARD_COLUMN_LETTERS[0] + '</td>' +
        '<td class="board-letter">' + BoardHelper.BOARD_COLUMN_LETTERS[1] + '</td>' +
        '<td class="board-letter">' + BoardHelper.BOARD_COLUMN_LETTERS[2] + '</td>' +
        '<td class="board-letter">' + BoardHelper.BOARD_COLUMN_LETTERS[3] + '</td>' +
        '<td class="board-letter">' + BoardHelper.BOARD_COLUMN_LETTERS[4] + '</td>' +
        '<td class="board-letter">' + BoardHelper.BOARD_COLUMN_LETTERS[5] + '</td>' +
        '<td class="board-letter">' + BoardHelper.BOARD_COLUMN_LETTERS[6] + '</td>' +
        '<td class="board-letter">' + BoardHelper.BOARD_COLUMN_LETTERS[7] + '</td></tr>';

    $board.append(tableInnerHtml);
}

function isSpecialGamePatternValid() {
    let isPatternValid = 1;
    let values = $("#inputValidatePatternSpecialGame").val().split(";");
    let positionRegex = /[A-H][1-9]/g;

    for (let i = 0; i < values.length; i++) {
        let items = values[i].split(":");

        if (items.length > 2) {
            isPatternValid = false;
            break;
        }

        isPatternValid &= items[0].match(positionRegex) !== null; //Position
        isPatternValid &= ($.inArray(items[1], BoardHelper.PIECE_NAMES) !== -1); //Piece
    }

    return isPatternValid;
}