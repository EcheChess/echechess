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

package ca.watier.enums;

import ca.watier.game.Direction;

/**
 * Created by yannick on 4/29/2017.
 */
public enum DirectionPattern {
    NORMAL(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST),
    DIAGONAL(Direction.NORTH_EAST, Direction.NORTH_WEST, Direction.SOUTH_EAST, Direction.SOUTH_WEST),
    ALL(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.NORTH_EAST, Direction.NORTH_WEST, Direction.SOUTH_EAST, Direction.SOUTH_WEST);

    private Direction[] directions;

    DirectionPattern(Direction... directions) {
        this.directions = directions;
    }

    public Direction[] getDirections() {
        return directions;
    }
}

