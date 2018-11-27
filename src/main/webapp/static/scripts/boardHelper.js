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


class BoardHelper {
    static getPieceIconByPieceName(name) {
        var value = null;
        for (var currentName in this.MAPPED_NAME_WITH_ICON) {
            if (currentName === name) {
                value = this.MAPPED_NAME_WITH_ICON[currentName];
                break;
            }
        }

        return value;
    }

    static getCoordinateFromCaseId(id) {
        var value = null;
        for (var caseId in this.MAPPED_CASENAME_WITH_COORDINATE) {
            if (caseId === id) {
                value = this.MAPPED_CASENAME_WITH_COORDINATE[caseId];
                break;
            }
        }

        return value;
    }

    static getCaseIdFromCoordinate(x, y) {
        var value = null;
        for (var caseId in this.MAPPED_CASENAME_WITH_COORDINATE) {
            var currentCoor = this.MAPPED_CASENAME_WITH_COORDINATE[caseId];
            if (currentCoor.x === x && currentCoor.y === y) {
                value = caseId;
                break;
            }
        }

        return value;
    }
}

BoardHelper.BOARD_COLUMN_LETTERS = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'];
BoardHelper.PIECE_NAMES = ["W_KING", "W_QUEEN", "W_ROOK", "W_BISHOP", "W_KNIGHT", "W_PAWN", "B_KING", "B_QUEEN", "B_ROOK", "B_BISHOP", "B_KNIGHT", "B_PAWN"];

BoardHelper.MAPPED_NAME_WITH_ICON = {
    W_KING: '♔',
    W_QUEEN: '♕',
    W_ROOK: '♖',
    W_BISHOP: '♗',
    W_KNIGHT: '♘',
    W_PAWN: '♙',
    B_KING: '♚',
    B_QUEEN: '♛',
    B_ROOK: '♜',
    B_BISHOP: '♝',
    B_KNIGHT: '♞',
    B_PAWN: '♟'
};

BoardHelper.MAPPED_COLUMNS_AND_LINE = [
    ["A8", "B8", "C8", "D8", "E8", "F8", "G8", "H8"],
    ["A7", "B7", "C7", "D7", "E7", "F7", "G7", "H7"],
    ["A6", "B6", "C6", "D6", "E6", "F6", "G6", "H6"],
    ["A5", "B5", "C5", "D5", "E5", "F5", "G5", "H5"],
    ["A4", "B4", "C4", "D4", "E4", "F4", "G4", "H4"],
    ["A3", "B3", "C3", "D3", "E3", "F3", "G3", "H3"],
    ["A2", "B2", "C2", "D2", "E2", "F2", "G2", "H2"],
    ["A1", "B1", "C1", "D1", "E1", "F1", "G1", "H1"]
];


BoardHelper.MAPPED_CASENAME_WITH_COORDINATE = {
    "A8": {x: -4, y: 4},
    "B8": {x: -3, y: 4},
    "C8": {x: -2, y: 4},
    "D8": {x: -1, y: 4},
    "E8": {x: 0, y: 4},
    "F8": {x: 1, y: 4},
    "G8": {x: 2, y: 4},
    "H8": {x: 3, y: 4},
    "A7": {x: -4, y: 3},
    "B7": {x: -3, y: 3},
    "C7": {x: -2, y: 3},
    "D7": {x: -1, y: 3},
    "E7": {x: 0, y: 3},
    "F7": {x: 1, y: 3},
    "G7": {x: 2, y: 3},
    "H7": {x: 3, y: 3},
    "A6": {x: -4, y: 2},
    "B6": {x: -3, y: 2},
    "C6": {x: -2, y: 2},
    "D6": {x: -1, y: 2},
    "E6": {x: 0, y: 2},
    "F6": {x: 1, y: 2},
    "G6": {x: 2, y: 2},
    "H6": {x: 3, y: 2},
    "A5": {x: -4, y: 1},
    "B5": {x: -3, y: 1},
    "C5": {x: -2, y: 1},
    "D5": {x: -1, y: 1},
    "E5": {x: 0, y: 1},
    "F5": {x: 1, y: 1},
    "G5": {x: 2, y: 1},
    "H5": {x: 3, y: 1},
    "A4": {x: -4, y: 0},
    "B4": {x: -3, y: 0},
    "C4": {x: -2, y: 0},
    "D4": {x: -1, y: 0},
    "E4": {x: 0, y: 0},
    "F4": {x: 1, y: 0},
    "G4": {x: 2, y: 0},
    "H4": {x: 3, y: 0},
    "A3": {x: -4, y: -1},
    "B3": {x: -3, y: -1},
    "C3": {x: -2, y: -1},
    "D3": {x: -1, y: -1},
    "E3": {x: 0, y: -1},
    "F3": {x: 1, y: -1},
    "G3": {x: 2, y: -1},
    "H3": {x: 3, y: -1},
    "A2": {x: -4, y: -2},
    "B2": {x: -3, y: -2},
    "C2": {x: -2, y: -2},
    "D2": {x: -1, y: -2},
    "E2": {x: 0, y: -2},
    "F2": {x: 1, y: -2},
    "G2": {x: 2, y: -2},
    "H2": {x: 3, y: -2},
    "A1": {x: -4, y: -3},
    "B1": {x: -3, y: -3},
    "C1": {x: -2, y: -3},
    "D1": {x: -1, y: -3},
    "E1": {x: 0, y: -3},
    "F1": {x: 1, y: -3},
    "G1": {x: 2, y: -3},
    "H1": {x: 3, y: -3}
};