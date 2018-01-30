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

package ca.watier.pojos;

import ca.watier.enums.CasePosition;
import ca.watier.enums.KingStatus;
import ca.watier.enums.MoveType;
import ca.watier.enums.Side;

public class MoveHistory {
    private final CasePosition from;
    private final CasePosition to;
    private final Side playerSide;
    private MoveType moveType;
    private KingStatus currentKingStatus;
    private KingStatus otherKingStatus;

    public MoveHistory(CasePosition from, CasePosition to, Side playerSide) {
        this.from = from;
        this.to = to;
        this.playerSide = playerSide;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public void setMoveType(MoveType moveType) {
        this.moveType = moveType;
    }

    public CasePosition getFrom() {
        return from;
    }

    public CasePosition getTo() {
        return to;
    }

    public Side getPlayerSide() {
        return playerSide;
    }

    public KingStatus getCurrentKingStatus() {
        return currentKingStatus;
    }

    public void setCurrentKingStatus(KingStatus currentKingStatus) {
        this.currentKingStatus = currentKingStatus;
    }

    public KingStatus getOtherKingStatus() {
        return otherKingStatus;
    }

    public void setOtherKingStatus(KingStatus otherKingStatus) {
        this.otherKingStatus = otherKingStatus;
    }

    @Override
    public String toString() {
        return "MoveHistory{" +
                "from=" + from +
                ", to=" + to +
                ", playerSide=" + playerSide +
                ", moveType=" + moveType +
                ", currentKingStatus=" + currentKingStatus +
                ", otherKingStatus=" + otherKingStatus +
                '}';
    }
}
