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

package ca.watier.responses;

import ca.watier.enums.ChessEventMessage;

/**
 * Created by yannick on 5/1/2017.
 */
public class ChessEvent {
    private ChessEventMessage event;
    private Object message;
    private Object obj;

    public ChessEvent(ChessEventMessage event, Object message) {
        this.event = event;
        this.message = message;
    }

    public ChessEvent(ChessEventMessage event) {
        this.event = event;
    }

    public ChessEventMessage getEvent() {
        return event;
    }

    public Object getMessage() {
        return message;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
