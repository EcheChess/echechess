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

package ca.watier.enums;


import org.jetbrains.annotations.NotNull;

public enum PgnEndGameToken {
    WHITE_WIN("1-0"), BLACK_WIN("0-1"), DRAWN("1/2-1/2"), STILL_IN_PROGRESS("*"), UNKNOWN("\0");

    private final String ending;

    PgnEndGameToken(String ending) {
        this.ending = ending;
    }

    public static PgnEndGameToken getEndGameTokenByAction(@NotNull String action) {

        PgnEndGameToken token = UNKNOWN;

        for (PgnEndGameToken pgnEndGameToken : values()) {
            if (pgnEndGameToken.ending.equals(action)) {
                token = pgnEndGameToken;
                break;
            }
        }

        return token;
    }

    public static boolean isGameEnded(PgnEndGameToken endGameToken) {
        return !UNKNOWN.equals(endGameToken);
    }

    public String getEnding() {
        return ending;
    }
}
