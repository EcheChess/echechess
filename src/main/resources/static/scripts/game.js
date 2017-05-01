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

$(document).ready(function () {
    initUiTriggers();

    $("#createGame").click(function () {
        currentUuid = createNewGame();
        wsClient = connect(currentUuid);

        var url = document.location.href + '?game=' + currentUuid;
        var $uuid = $('#uuid');
        $uuid.text(url);
        $uuid.attr('href', url);

        renderBoard();
    });

    $("#joinGameButton").click(function () {
        currentUuid = $("#joinGame").val();

        var response = jsonFromRequest("POST", '/game/join', {
            side: $("#changeSide").find("option:selected").val(),
            uuid: currentUuid
        }).response;

        wsClient = connect(currentUuid);
        renderBoard();
    });
});

function connect(uuid) {
    var stompClient = null;
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/' + uuid, function (greeting) {
            var message = JSON.parse(greeting.body).event;

            switch (message) {
                case 'MOVE':
                    renderBoard();
                    break;
            }

        });
    });

    return stompClient;
}


function initUiTriggers() {
    $("#changeSide").change(function () {
        if (currentUuid) {
            var response = jsonFromRequest("POST", '/game/side', {
                side: $("#changeSide").find("option:selected").val(),
                uuid: currentUuid
            }).response;

            if (response) {
                alert("Side changed !");
            }
        }
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
        otherPlayer: $("#allowOtherPlayers").is(':checked'),
        observers: $("#allowOtherObserver").is(':checked')
    }).uuid;
}

function renderBoard() {

    if (!currentUuid) {
        return;
    }
    var $board = $("#board");

    $board.empty();

    var tableInnerHtml = '';
    var boardColumnLetters = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'];


    var piecesLocation = jsonFromRequest('GET', '/game/pieces', {
        uuid: currentUuid
    });


    var caseColorIndex = 0;
    var letterIdx = 0;
    var numberIdx = 8;

    for (var y = 4; y > -4; y--) { //lines
        tableInnerHtml += '<tr>';
        letterIdx = 0;
        for (var x = -4; x < 4; x++) { //columns
            var caseLetter = boardColumnLetters[letterIdx];
            var caseColor = (((caseColorIndex & 1) === 1) ? 'black' : 'white');
            var pieceIcon = '';

            for (var key in piecesLocation) {
                var currentPiece = piecesLocation[key];
                var currentElementX = currentPiece.value1.x;
                var currentElementY = currentPiece.value1.y;

                if (x === currentElementX && y === currentElementY) {
                    pieceIcon = currentPiece.value2.unicodeIcon;
                    break;
                }
            }

            tableInnerHtml += '<td data-case-id="' + caseLetter + numberIdx + '" class="board-square ' + caseColor + '"><span class="board-pieces">' + pieceIcon + '</span></td>';


            caseColorIndex++;
            letterIdx++;
        }
        numberIdx--;
        caseColorIndex++;
        tableInnerHtml += '</tr>';
    }

    $board.append(tableInnerHtml);

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
}