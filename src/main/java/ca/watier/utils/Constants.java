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

package ca.watier.utils;

/**
 * Created by yannick on 4/17/2017.
 */
public final class Constants {
    public static final String PLAYER = "SESSION_PLAYER_ID";
    public static final String THE_CLIENT_LOST_THE_CONNECTION = "The client lost the connection, refreshing the page in 10 seconds";
    public static final String NOT_AUTHORIZED_TO_JOIN = "You are not authorized to join this game !";
    public static final String NEW_PLAYER_JOINED_SIDE = "New player joined the %s side";
    public static final String PLAYER_TURN = "It's your turn !";
    public static final String PLAYER_MOVE = "%s player moved %s to %s";
    public static final String GAME_PAUSED_PAWN_PROMOTION = "The game will continue after the %s player choose his piece";
    public static final String PLAYER_KING_CHECK = "Your king is checked !";
    public static final String PLAYER_KING_CHECKMATE = "The %s king is checkmate !";
    public static final String PLAYER_KING_STALEMATE = "Your king is stalemate (draw) !";
    public static final String GAME_ENDED = "The game is ended !";
    public static final String REQUESTED_SESSION_ALREADY_DEFINED = "The requested session id is already defined !";
    public static final String JOINING_GAME = "Joining the game %s";

    private Constants() {
    }
}
