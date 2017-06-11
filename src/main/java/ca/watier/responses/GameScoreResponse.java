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

/**
 * Created by yannick on 6/9/2017.
 */
public class GameScoreResponse {
    private short whitePlayerPoint, blackPlayerPoint;

    public GameScoreResponse(short whitePlayerPoint, short blackPlayerPoint) {
        this.whitePlayerPoint = whitePlayerPoint;
        this.blackPlayerPoint = blackPlayerPoint;
    }

    public short getWhitePlayerPoint() {
        return whitePlayerPoint;
    }

    public short getBlackPlayerPoint() {
        return blackPlayerPoint;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameScoreResponse response = (GameScoreResponse) o;

        return whitePlayerPoint == response.whitePlayerPoint && blackPlayerPoint == response.blackPlayerPoint;
    }
}
