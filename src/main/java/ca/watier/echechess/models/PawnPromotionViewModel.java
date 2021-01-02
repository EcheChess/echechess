/*
 *    Copyright 2014 - 2019 Yannick Watier
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

package ca.watier.echechess.models;

import ca.watier.echechess.common.enums.Side;

import java.io.Serial;
import java.io.Serializable;

public class PawnPromotionViewModel implements Serializable {
    @Serial
    private static final long serialVersionUID = -1770750370916758813L;

    private Side gameSide;
    private String from;
    private String to;

    public Side getGameSide() {
        return gameSide;
    }

    public void setGameSide(Side gameSide) {
        this.gameSide = gameSide;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
