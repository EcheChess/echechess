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

/**
 * To enable Remode debuf on firefox:
 * SHIFT+F2
 * listen 6000
 */

$(document).ready(function () {
    var currentUuid = createNewGame();

    if (currentUuid) {
        initGame(currentUuid);
    }
});

function jsonFromGetRequest(url) {
    var value = null;

    $.ajax({
        url: url,
        type: 'GET',
        async: false,
        cache: false,
        timeout: 30000,
        success: function (json) {
            value = json;
        }
    });

    return value;
}

function jsonFromPostRequest(url, data) {
    var value = null;

    $.ajax({
        url: url,
        type: 'POST',
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
    return jsonFromPostRequest('/game/create', {side: 'WHITE'}).uuid;
}

function initGame(currentUuid) {
    var tableInnerHtml = '';
    var boardColumnLetters = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'];

    var idx = 0;
    for (var y = 0; y < 8; y++) { //lines
        tableInnerHtml += '<tr>';
        for (var x = 0; x < 8; x++) { //columns
            var caseLetter = boardColumnLetters[x];
            var caseNumber = (8 - y);
            var caseColor = (((idx & 1) === 1) ? 'black' : 'white');
            var pieceIcon = getPieceByPosition(x, y);

            tableInnerHtml += '<td data-case-id="' + caseLetter + caseNumber + '" class="board-square ' + caseColor + '"><span class="board-pieces">' + pieceIcon + '</span></td>';
            idx++;
        }
        idx++;
        tableInnerHtml += '</tr>';
    }

    $('#board').append(tableInnerHtml);

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

            var response = jsonFromPostRequest('/game/move', {
                from: from,
                to: to,
                uuid: currentUuid
            }).response;

            console.log($(ui.draggable).text() + " moved from " + from + " to " + to);

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

function getPieceByPosition(x, y) {
    if (y > 1 && y < 6) {
        return '';
    }

    if (y === 0) { //black
        return getSpecialPieces(false, x);
    } else if (y === 1) {
        return '♟';
    } else if (y === 6) {
        return '♙'
    }

    return getSpecialPieces(true, x);
}


function getSpecialPieces(isWhite, x) {

    switch (x) {
        case 0: //Rooks
        case 7:
            return isWhite ? '♖' : '♜';
        case 1: //Knights
        case 6:
            return isWhite ? '♘' : '♞';
        case 2: //Pawns
        case 5:
            return isWhite ? '♗' : '♝';
        case 3: //Queen
            return isWhite ? '♕' : '♛';
        case 4:  //King
            return isWhite ? '♔' : '♚';
    }

    return '';
}