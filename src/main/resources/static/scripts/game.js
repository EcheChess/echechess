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

var currentUuid = null;
var wsClient = null;
var helperLastSelectedCase = null;
var helperContainer = [];
var boardColumnLetters = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'];

$(document).ready(function () {
    initUiTriggers();
    drawBoard(null, "#board");

    $("#createGame").click(function () {
        currentUuid = createNewGame();
        wsClient = connect(currentUuid);

        $('#uuid').text(currentUuid);
        renderBoard();
    });

    $("#joinGameButton").click(function () {
        var enteredUuid = $("#joinGame").val();

        if (enteredUuid !== '' && enteredUuid.length === 36) {
            currentUuid = enteredUuid;

            var response = jsonFromRequest("POST", '/game/join', {
                side: $("#changeSide").find("option:selected").val(),
                uuid: currentUuid
            }).response;

            wsClient = connect(currentUuid);
            renderBoard();
        }
    });

    $('.menu .item').tab();
});

function connect(uuid) {
    var stompClient = null;
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/' + uuid, function (greeting) {
            var parsed = JSON.parse(greeting.body);
            var chessEvent = parsed.event;
            var message = parsed.message;

            switch (chessEvent) {
                case 'MOVE':
                    renderBoard();
                    writeToGameLog(message);
                    break;
                case 'PLAYER_TURN':
                    writeToGameLog(message);
                    break;
                case 'PLAYER_JOINED':
                    writeToGameLog(message);
                    break;
                case 'GAME_WON':
                    writeToGameLog(message);
                    break;
                case 'GAME_WON_EVENT_MOVE':
                    alert(message);
                    break;
            }
        });
    });

    return stompClient;
}


function writeToGameLog(message) {
    $('#chessLog').append("<option>" + message + "</option>");
}


function initUiTriggers() {
    var $changeSide = $('#changeSide');
    var $gameType = $('#gameType');
    var $specialGameLabel = $('#specialGamePiecesLabel');
    var $specialGame = $('#specialGamePieces');

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
        var $iconValidatePatternSpecialGame = $("#iconValidatePatternSpecialGame");

        if (isSpecialGamePatternValid()) {
            $iconValidatePatternSpecialGame.removeClass('warning');
            $iconValidatePatternSpecialGame.removeClass('remove');
            $iconValidatePatternSpecialGame.removeClass('red');
            $iconValidatePatternSpecialGame.addClass('check');
            $iconValidatePatternSpecialGame.addClass('green');
        } else {
            $iconValidatePatternSpecialGame.removeClass('warning');
            $iconValidatePatternSpecialGame.removeClass('check');
            $iconValidatePatternSpecialGame.removeClass('green');
            $iconValidatePatternSpecialGame.addClass('remove');
            $iconValidatePatternSpecialGame.addClass('red');
        }
    });

    var $inputValidatePatternSpecialGame = $("#inputValidatePatternSpecialGame");
    $(document).on("click", "#helperBoard > tr > td.board-square", function () {
        var oldText = $inputValidatePatternSpecialGame.val();
        $inputValidatePatternSpecialGame.val((oldText.length === 0 ? "" : oldText + ";") + $(this).attr("data-case-id"));

        helperLastSelectedCase = {
            value1: {
                x: parseInt($(this).attr("data-case-x")),
                y: parseInt($(this).attr("data-case-y"))
            }
        };
    });


    $(document).on("click", ".helperBordPieces", function () {
        var oldText = $inputValidatePatternSpecialGame.val();
        $inputValidatePatternSpecialGame.val((oldText.length === 0 ? "" : oldText + ":" ) + $(this).attr("data-helper-icon"));

        var xValue = helperLastSelectedCase.value1.x;
        var yValue = helperLastSelectedCase.value1.y;

        helperLastSelectedCase = {
            value1: {
                x: xValue,
                y: yValue
            }, value2: {
                unicodeIcon: $(this).text()
            }
        };

        helperContainer.push(helperLastSelectedCase);
        drawBoard(helperContainer, "#helperBoard");
    });

    $("#buttonSendPatternSpecialGame").click(function () {
        $("#specialGamePieces").val($("#inputValidatePatternSpecialGame").val());
        $('#modalSpecialGameMoreInfo').modal('hide');
    });

    $changeSide.change(function () {
        if (currentUuid) {
            var response = jsonFromRequest("POST", '/game/side', {
                side: $(this).find("option:selected").val(),
                uuid: currentUuid
            }).response;

            if (response) {
                alert("Side changed !");
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
        var $modalSpecialGameMoreInfo = $('#modalSpecialGameMoreInfo');
        $modalSpecialGameMoreInfo.modal('show');
        drawBoard(null, "#helperBoard");
        $modalSpecialGameMoreInfo.modal('refresh')
    });
}

function jsonFromRequest(type, url, data) {
    var value = null;

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

    var piecesLocation = jsonFromRequest('GET', '/game/pieces', {
        uuid: currentUuid
    });

    drawBoard(piecesLocation, "#board");

    var sourceEvt = null;

    var $currentPiece = $(".board-pieces");
    $currentPiece.draggable({
        containment: "#board",
        helper: "clone",
        start: function () {
            sourceEvt = $(this).parent();
        }
    });


    $(".board-square").droppable({
        drop: function (event, ui) {
            var from = $(sourceEvt).attr("data-case-id");
            var to = $(this).attr("data-case-id");

            var response = jsonFromRequest('POST', '/game/move', {
                from: from,
                to: to,
                uuid: currentUuid
            }).response;

            if (response) {
                $(this).find('.board-pieces').each(function () {
                    var item = $(this).text();

                    if (item) {
                        $(this).hide();
                    }
                });

                $(this).append(ui.draggable);
            }

            $currentPiece.draggable("option", "revert", !response);
        }
    });

    var $boardCaseWithPieceSelector = $(".board-square > span.board-pieces");

    $boardCaseWithPieceSelector.mouseover(function () {
        var piecesLocation = jsonFromRequest('GET', '/game/moves', {
            from: $(this).parent().attr("data-case-id"),
            uuid: currentUuid
        });

        if (piecesLocation) {
            for (var i = 0; i < piecesLocation.length; i++) {
                $("[data-case-id='" + piecesLocation[i] + "']").addClass("pieceAvailMoves");
            }
        }
    });


    $boardCaseWithPieceSelector.mouseleave(function () {
        $("td").removeClass("pieceAvailMoves");
    });
}

function drawBoard(piecesLocation, boardId) {
    var $board = $(boardId);
    $board.empty();

    var tableInnerHtml = '';
    var caseColorIndex = 0;
    var letterIdx = 0;
    var numberIdx = 8;

    for (var y = 4; y > -4; y--) { //lines
        letterIdx = 0;
        tableInnerHtml += '<tr><td class="board-number">' + (y + 4) + '</td>';
        for (var x = -4; x < 4; x++) { //columns
            var caseLetter = boardColumnLetters[letterIdx];
            var caseColor = (((caseColorIndex & 1) === 1) ? 'black' : 'white');
            var pieceIcon = '';

            if (piecesLocation) {
                for (var key in piecesLocation) {
                    var currentPiece = piecesLocation[key];
                    var currentElementX = currentPiece.value1.x;
                    var currentElementY = currentPiece.value1.y;

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
        '<td class="board-letter">' + boardColumnLetters[0] + '</td>' +
        '<td class="board-letter">' + boardColumnLetters[1] + '</td>' +
        '<td class="board-letter">' + boardColumnLetters[2] + '</td>' +
        '<td class="board-letter">' + boardColumnLetters[3] + '</td>' +
        '<td class="board-letter">' + boardColumnLetters[4] + '</td>' +
        '<td class="board-letter">' + boardColumnLetters[5] + '</td>' +
        '<td class="board-letter">' + boardColumnLetters[6] + '</td>' +
        '<td class="board-letter">' + boardColumnLetters[7] + '</td></tr>';

    $board.append(tableInnerHtml);
}

function isSpecialGamePatternValid() {
    var isPatternValid = 1;
    var values = $("#inputValidatePatternSpecialGame").val().split(";");
    var pieces = ["W_KING", "W_QUEEN", "W_ROOK", "W_BISHOP", "W_KNIGHT", "W_PAWN", "B_KING", "B_QUEEN", "B_ROOK", "B_BISHOP", "B_KNIGHT", "B_PAWN"];
    var positionRegex = /[A-H][1-9]/g;

    for (var i = 0; i < values.length; i++) {
        var items = values[i].split(":");

        if (items.length > 2) {
            isPatternValid = false;
            break;
        }

        isPatternValid &= items[0].match(positionRegex) !== null; //Position
        isPatternValid &= ($.inArray(items[1], pieces) !== -1); //Piece
    }

    return isPatternValid;
}
