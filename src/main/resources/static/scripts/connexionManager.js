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
 * Created by yannick on 5/28/2017.
 */
let wsClient = null;
let wsClientColor = null;
let wsMainClient = null;

class ConnexionManager {
    static connectMainEvent(uuid, writeToGameLog) {
        if (wsMainClient) {
            wsMainClient.unsubscribe();
        }

        wsMainClient = Stomp.over(new SockJS('/gs-guide-websocket'));
        wsMainClient.connect({}, function () {
            wsMainClient.subscribe('/topic/' + getCookieValueByName('sessionId'), function (greeting) {
                let parsed = JSON.parse(greeting.body);
                let chessEvent = parsed.event;
                let message = parsed.message;

                switch (chessEvent) {
                    case 'PLAYER_TURN':
                        writeToGameLog(message, chessEvent);
                        break;
                }
            });
        });
    }


    static connectSideEvent(uuid, writeToGameLog) {
        if (wsClientColor) {
            wsClientColor.unsubscribe();
        }

        wsClientColor = Stomp.over(new SockJS('/gs-guide-websocket'));
        wsClientColor.connect({}, function () {
            wsClientColor.subscribe('/topic/' + uuid + '/' + $("#changeSide").find("option:selected").val(), function (greeting) {
                let parsed = JSON.parse(greeting.body);
                let chessEvent = parsed.event;
                let message = parsed.message;

                switch (chessEvent) {
                    case 'PLAYER_TURN':
                        writeToGameLog(message, chessEvent);
                        break;
                }
            });
        });
    }

    static connect(uuid, renderBoard, writeToGameLog) {
        if (wsClient) {
            wsClient.unsubscribe();
        }

        wsClient = Stomp.over(new SockJS('/gs-guide-websocket'));
        wsClient.connect({}, function () {
            wsClient.subscribe('/topic/' + uuid, function (greeting) {
                let parsed = JSON.parse(greeting.body);
                let chessEvent = parsed.event;
                let message = parsed.message;

                switch (chessEvent) {
                    case 'MOVE':
                        renderBoard();
                        writeToGameLog(message, chessEvent);
                        break;
                    case 'PLAYER_JOINED':
                        writeToGameLog(message, chessEvent);
                        break;
                    case 'GAME_WON':
                        writeToGameLog(message, chessEvent);
                        break;
                    case 'GAME_WON_EVENT_MOVE':
                        writeToGameLog(message, chessEvent);
                        break;
                }
            });
        });
    }
}
