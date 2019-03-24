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
 * Created by yannick on 4/18/2017.
 */

const Game = {
    template:
        `
<div id="main-div">
    <div id="main-menu" class="ui stackable menu">
      <div class="item">
        <img src="images/EcheChess.png" >
      </div>
      <a class="item" v-on:click="newGame">New Game</a>
      <a class="item">Join Game</a>
    </div>
    
    <div id="board">
        <div class="bord-case" v-for="(piece, key, index) in pieces">
            <span class="board-pieces" v-bind:data-case-id="key" v-html="piece.unicodeIcon"></span>
        </div>
    </div>
        `,
    data: function () {
        return {
            pieces: {
                H8: {
                    "unicodeIcon": "&#9820;",
                    "name": "Black Rook"
                },
                G8: {
                    "unicodeIcon": " &#9822;",
                    "name": "Black Knight"
                },
                F8: {
                    "unicodeIcon": "&#9821;",
                    "name": "Black Bishop"
                },
                E8: {
                    "unicodeIcon": "&#9818;",
                    "name": "Black King"
                },
                D8: {
                    "unicodeIcon": "&#9819;",
                    "name": "Black Queen "
                },
                C8: {
                    "unicodeIcon": "&#9821;",
                    "name": "Black Bishop"
                },
                B8: {
                    "unicodeIcon": " &#9822;",
                    "name": "Black Knight"
                },
                A8: {
                    "unicodeIcon": "&#9820;",
                    "name": "Black Rook"
                },
                H7: {
                    "unicodeIcon": "&#9823;",
                    "name": "Black Pawn"
                },
                G7: {
                    "unicodeIcon": "&#9823;",
                    "name": "Black Pawn"
                },
                F7: {
                    "unicodeIcon": "&#9823;",
                    "name": "Black Pawn"
                },
                E7: {
                    "unicodeIcon": "&#9823;",
                    "name": "Black Pawn"
                },
                D7: {
                    "unicodeIcon": "&#9823;",
                    "name": "Black Pawn"
                },
                C7: {
                    "unicodeIcon": "&#9823;",
                    "name": "Black Pawn"
                },
                B7: {
                    "unicodeIcon": "&#9823;",
                    "name": "Black Pawn"
                },
                A7: {
                    "unicodeIcon": "&#9823;",
                    "name": "Black Pawn"
                },
                H6: {
                    "unicodeIcon": "",
                    "name": ""
                },
                G6: {
                    "unicodeIcon": "",
                    "name": ""
                },
                F6: {
                    "unicodeIcon": "",
                    "name": ""
                },
                E6: {
                    "unicodeIcon": "",
                    "name": ""
                },
                D6: {
                    "unicodeIcon": "",
                    "name": ""
                },
                C6: {
                    "unicodeIcon": "",
                    "name": ""
                },
                B6: {
                    "unicodeIcon": "",
                    "name": ""
                },
                A6: {
                    "unicodeIcon": "",
                    "name": ""
                },
                H5: {
                    "unicodeIcon": "",
                    "name": ""
                },
                G5: {
                    "unicodeIcon": "",
                    "name": ""
                },
                F5: {
                    "unicodeIcon": "",
                    "name": ""
                },
                E5: {
                    "unicodeIcon": "",
                    "name": ""
                },
                D5: {
                    "unicodeIcon": "",
                    "name": ""
                },
                C5: {
                    "unicodeIcon": "",
                    "name": ""
                },
                B5: {
                    "unicodeIcon": "",
                    "name": ""
                },
                A5: {
                    "unicodeIcon": "",
                    "name": ""
                },
                H4: {
                    "unicodeIcon": "",
                    "name": ""
                },
                G4: {
                    "unicodeIcon": "",
                    "name": ""
                },
                F4: {
                    "unicodeIcon": "",
                    "name": ""
                },
                E4: {
                    "unicodeIcon": "",
                    "name": ""
                },
                D4: {
                    "unicodeIcon": "",
                    "name": ""
                },
                C4: {
                    "unicodeIcon": "",
                    "name": ""
                },
                B4: {
                    "unicodeIcon": "",
                    "name": ""
                },
                A4: {
                    "unicodeIcon": "",
                    "name": ""
                },
                H3: {
                    "unicodeIcon": "",
                    "name": ""
                },
                G3: {
                    "unicodeIcon": "",
                    "name": ""
                },
                F3: {
                    "unicodeIcon": "",
                    "name": ""
                },
                E3: {
                    "unicodeIcon": "",
                    "name": ""
                },
                D3: {
                    "unicodeIcon": "",
                    "name": ""
                },
                C3: {
                    "unicodeIcon": "",
                    "name": ""
                },
                B3: {
                    "unicodeIcon": "",
                    "name": ""
                },
                A3: {
                    "unicodeIcon": "",
                    "name": ""
                },
                H2: {
                    "unicodeIcon": "&#9817;",
                    "name": "White Pawn"
                },
                G2: {
                    "unicodeIcon": "&#9817;",
                    "name": "White Pawn"
                },
                F2: {
                    "unicodeIcon": "&#9817;",
                    "name": "White Pawn"
                },
                E2: {
                    "unicodeIcon": "&#9817;",
                    "name": "White Pawn"
                },
                D2: {
                    "unicodeIcon": "&#9817;",
                    "name": "White Pawn"
                },
                C2: {
                    "unicodeIcon": "&#9817;",
                    "name": "White Pawn"
                },
                B2: {
                    "unicodeIcon": "&#9817;",
                    "name": "White Pawn"
                },
                A2: {
                    "unicodeIcon": "&#9817;",
                    "name": "White Pawn"
                },
                H1: {
                    "unicodeIcon": "&#9814;",
                    "name": "White Rook"
                },
                G1: {
                    "unicodeIcon": "&#9816;",
                    "name": "White Knight"
                },
                F1: {
                    "unicodeIcon": "&#9815;",
                    "name": "White Bishop"
                },
                E1: {
                    "unicodeIcon": "&#9812;",
                    "name": "White King"
                },
                D1: {
                    "unicodeIcon": "&#9813;",
                    "name": "White Queen "
                },
                C1: {
                    "unicodeIcon": "&#9815;",
                    "name": "White Bishop"
                },
                B1: {
                    "unicodeIcon": "&#9816;",
                    "name": "White Knight"
                },
                A1: {
                    "unicodeIcon": "&#9814;",
                    "name": "White Rook"
                }
            }
        };
    },
    mounted: function () {
        this.registerEvents();
    },
    methods: {
        updateBoardPieces: function (items) {
            let length = items.length;
            for (let i = 0; i < length; i++) {
                let newPiece = items[i];
                let piece = this.pieces[newPiece.rawPosition];
                piece.unicodeIcon = newPiece.unicodeIcon;
                piece.name = newPiece.name;
            }
        },
        //---------------------------------------------------------------------------
        fetchPiecesAndPutOnBoard: function (data) {
            let ref = this;
            let parent = ref.$parent;
            let uuid = data.response;

            if (uuid) {
                $.ajax({
                    url: `${parent.baseApi}/api/v1/game/pieces`,
                    type: "GET",
                    cache: false,
                    timeout: 30000,
                    data: `uuid=${uuid}`,
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader("Authorization", `Bearer ${parent.oauth}`);
                        xhr.setRequestHeader("X-CSRF-TOKEN", parent.csrf);
                    },
                }).done(function (pieces) {
                    ref.updateBoardPieces(pieces);
                }).fail(function () {
                    alertify.error("Unable to fetch the pieces!", 5);
                });
            } else {
                alertify.error("Unable to fetch the pieces location (uuid is not available)!", 5);
            }
        },
        //---------------------------------------------------------------------------
        registerEvents: function () {
            document.addEventListener("dragover", function (event) {
                event.preventDefault();
            });

            $(document).on("dragstart", ".board-pieces", function (event) {
                let dataTransfer = event.originalEvent.dataTransfer;
                dataTransfer.setData("from", $(event.target).parent().data('case-id'));
            });

            $(document).on("drop", ".bord-case", this.whenPieceDraggedEvent);
        },
        //---------------------------------------------------------------------------
        createNewGame: function () {
            let ref = this;
            let parent = ref.$parent;

            //TODO: BIND TO THE UI
            let side = "WHITE";
            let againstComputer = false;
            let observers = false;

            $.ajax({
                url: `${this.$parent.baseApi}/api/v1/game/create`,
                type: "POST",
                cache: false,
                timeout: 30000,
                data: `side=${side}&againstComputer=${againstComputer}&observers=${observers}`,
                beforeSend: function (xhr) {
                    xhr.setRequestHeader("Authorization", `Bearer ${parent.oauth}`);
                    xhr.setRequestHeader("X-CSRF-TOKEN", parent.csrf);
                }
            }).done(function (data) {
                ref.fetchPiecesAndPutOnBoard(data)
            }).fail(function () {
                alertify.error("Unable to create a new game!", 5);
            });
        },
        //---------------------------------------------------------------------------
        newGame: function () {
            this.createNewGame();
        },
        //---------------------------------------------------------------------------
        whenPieceDraggedEvent: function (event) {
            let ref = this;
            let dataTransfer = event.originalEvent.dataTransfer;
            let from = dataTransfer.getData("from");
            let to = $(event.target).data('case-id');

            if (from && to && (from !== to)) {
                $.ajax({
                    url: `${this.$parent.baseApi}/api/v1/game/move`,
                    type: "POST",
                    cache: false,
                    timeout: 30000,
                    data: `from=${from}&to=${to}&uuid=${this.$parent.uuid}`,
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader("Authorization", `Bearer ${ref.$parent.oauth}`);
                    },
                }).fail(function () {
                    alertify.error("Unable to move to the selected position!", 5);
                });
            }
        }
    }
};