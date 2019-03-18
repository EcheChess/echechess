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
        <img src="/images/EcheChess.png" >
      </div>
      <a class="item" v-on:click="newGame">New Game</a>
      <a class="item">Join Game</a>
    </div>
   
    <div id="board">
        <div data-case-id='A8' class="bord-case"></div>
        <div data-case-id='B8' class="bord-case"></div>
        <div data-case-id='C8' class="bord-case"></div>
        <div data-case-id='D8' class="bord-case"></div>
        <div data-case-id='E8' class="bord-case"></div>
        <div data-case-id='F8' class="bord-case"></div>
        <div data-case-id='G8' class="bord-case"></div>
        <div data-case-id='H8' class="bord-case"></div>
    
        <div data-case-id='A7' class="bord-case"></div>
        <div data-case-id='B7' class="bord-case"></div>
        <div data-case-id='C7' class="bord-case"></div>
        <div data-case-id='D7' class="bord-case"></div>
        <div data-case-id='E7' class="bord-case"></div>
        <div data-case-id='F7' class="bord-case"></div>
        <div data-case-id='G7' class="bord-case"></div>
        <div data-case-id='H7' class="bord-case"></div>
    
        <div data-case-id='A6' class="bord-case"></div>
        <div data-case-id='B6' class="bord-case"></div>
        <div data-case-id='C6' class="bord-case"></div>
        <div data-case-id='D6' class="bord-case"></div>
        <div data-case-id='E6' class="bord-case"></div>
        <div data-case-id='F6' class="bord-case"></div>
        <div data-case-id='G6' class="bord-case"></div>
        <div data-case-id='H6' class="bord-case"></div>
    
        <div data-case-id='A5' class="bord-case"></div>
        <div data-case-id='B5' class="bord-case"></div>
        <div data-case-id='C5' class="bord-case"></div>
        <div data-case-id='D5' class="bord-case"></div>
        <div data-case-id='E5' class="bord-case"></div>
        <div data-case-id='F5' class="bord-case"></div>
        <div data-case-id='G5' class="bord-case"></div>
        <div data-case-id='H5' class="bord-case"></div>
    
        <div data-case-id='A4' class="bord-case"></div>
        <div data-case-id='B4' class="bord-case"></div>
        <div data-case-id='C4' class="bord-case"></div>
        <div data-case-id='D4' class="bord-case"></div>
        <div data-case-id='E4' class="bord-case"></div>
        <div data-case-id='F4' class="bord-case"></div>
        <div data-case-id='G4' class="bord-case"></div>
        <div data-case-id='H4' class="bord-case"></div>
    
        <div data-case-id='A3' class="bord-case"></div>
        <div data-case-id='B3' class="bord-case"></div>
        <div data-case-id='C3' class="bord-case"></div>
        <div data-case-id='D3' class="bord-case"></div>
        <div data-case-id='E3' class="bord-case"></div>
        <div data-case-id='F3' class="bord-case"></div>
        <div data-case-id='G3' class="bord-case"></div>
        <div data-case-id='H3' class="bord-case"></div>
    
        <div data-case-id='A2' class="bord-case"></div>
        <div data-case-id='B2' class="bord-case"></div>
        <div data-case-id='C2' class="bord-case"></div>
        <div data-case-id='D2' class="bord-case"></div>
        <div data-case-id='E2' class="bord-case"></div>
        <div data-case-id='F2' class="bord-case"></div>
        <div data-case-id='G2' class="bord-case"></div>
        <div data-case-id='H2' class="bord-case"></div>
    
        <div data-case-id='A1' class="bord-case"></div>
        <div data-case-id='B1' class="bord-case"></div>
        <div data-case-id='C1' class="bord-case"></div>
        <div data-case-id='D1' class="bord-case"></div>
        <div data-case-id='E1' class="bord-case"></div>
        <div data-case-id='F1' class="bord-case"></div>
        <div data-case-id='G1' class="bord-case"></div>
        <div data-case-id='H1' class="bord-case"></div>
    </div>
</div>
        `,
    data: function () {
        return {
            pieces: null
        };
    },
    mounted: function () {
        this.registerEvents();
    },
    methods: {
        //---------------------------------------------------------------------------
        updateBoardPieces: function (pieces) { //FIXME: REPLACE WITH VUE LOOP ?
            this.pieces = pieces;
            alert(JSON.stringify(pieces));
        },
        //---------------------------------------------------------------------------
        fetchPiecesAndPutOnBoard: function (data) {
            $(".board-pieces").remove();
            let ref = this;
            let parent = ref.$parent;
            let uuid = data.response;

            if(uuid) {
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
                    console.log("Unable to fetch the pieces!");
                });
            } else {
                alert("Unable to fetch the pieces location (uuid is not available)!");
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
                alert("Unable to create a new game!");
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
                    alert("move failed!");
                });
            }
        }
    }
};