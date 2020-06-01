/*
 *    Copyright 2014 - 2020 Yannick Watier
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

class WebsocketDelegate {
    registerGameEvents(basePath, sideEventPath, baseApiWs, oauth, headers, gameEventCallback, sideEventCallback) {
        let stompClientRef = this.stompClient;
        if (stompClientRef) {
            stompClientRef.unsubscribe();
        } else {
            let sockJS = new SockJS(`${baseApiWs}/websocket?access_token=${oauth}`, {transports: ['xhr-streaming']});
            stompClientRef = Stomp.over(sockJS);
            this.stompClient = stompClientRef;
        }

        stompClientRef.connect(headers, function () {
            stompClientRef.subscribe(basePath, gameEventCallback);
            stompClientRef.subscribe(sideEventPath, sideEventCallback);
        });
    }
}