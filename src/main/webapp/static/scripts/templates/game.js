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
    <div id="alert-container">
        <div v-for="(message, index) in this.$getGameMessages()"
             v-bind:class="['d-flex', 'flex-row', 'justify-content-between', 'alert', message.level, 'alert-dismissible', 'fade', 'show']"
             role="alert"
             v-bind:key="message.alertId">

            <i v-bind:class="[message.iconType, message.iconClass]" style="font-size:25px"></i>

            <span class="alert-massage">{{message.message}}</span>&nbsp;<span v-if="message.haveMoreThanOneMessage()">
            (x<span class="alert-count">{{message.numberOfMessages}}</span>)
            </span>

            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
    </div>
    <nav id="navbar-main-menu" class="navbar">
        <nav id="navbar-left-menu" class="navbar">
            <a class="navbar-brand" href="#">
                <img src="images/EcheChess.svg" width="40" height="60" class="d-inline-block align-top">
            </a>
            <a class="nav-link nav-top-bar-game-link" v-on:click="newGame">New Game</a>
            <a class="nav-link nav-top-bar-game-link" v-on:click="joinGame">Join Game</a>
        </nav>
        <nav id="navbar-right-menu" class="navbar">
            <a class="nav-link nav-top-bar-game-link" href="/swagger-ui.html">Swagger-ui</a>
        </nav>
    </nav>
    <div id="game">
        <div id="board">
            <div id="board-header">
                <span id="game-uuid">{{gameUuid}}</span>
                <div id="carousel-game-history" v-if="moveLog.length > 0" class="carousel slide" data-ride="carousel" data-interval="0">
                    <div id="carousel-game-history-items" class="carousel-inner text-center">
                        <div class="carousel-item" v-bind:class="[index === (moveLog.length - 1) ? 'active' : '']" v-for="(message, index) in moveLog">
                            <span class="d-block w-100">{{message + ' (' + (index + 1) + " of " + moveLog.length  +  ')'}}</span>
                        </div>
                    </div>
                    <a class="carousel-control-prev" href="#carousel-game-history" role="button" data-slide="prev">
                        <span class="carousel-control-prev-icon" aria-hidden="true"/>
                        <span class="sr-only">Previous</span>
                    </a>
                    <a class="carousel-control-next" href="#carousel-game-history" role="button" data-slide="next">
                        <span class="carousel-control-next-icon" aria-hidden="true"/>
                        <span class="sr-only">Next</span>
                    </a>
                </div>
            </div>
            <div v-bind:class="['board-case', (piece.isSelected ? 'piece-available-moves' : null)]" v-bind:data-case-id="piece.rawPosition" v-for="(piece, index) in board">
                <span class="board-pieces" draggable="true" v-bind:data-piece-side="mapSideByteToText(piece.side)" v-if="piece.unicodeIcon" v-html="piece.unicodeIcon"></span>
            </div>
        </div>
    </div>
    
        <div id="game-points">
            <span>Black: {{blackPlayerScore}}</span>
            <span>White: {{whitePlayerScore}}</span>
        </div>
        
        <button class="btn btn-outline-light" type="button" data-toggle="collapse" data-target="#collapseGameLog">
            Show logs
        </button>
        
        <div class="collapse" id="collapseGameLog">
          <div class="card card-body">
            <div class="form-control" v-for="(log, index) in eventLog">
                {{log}}<br/>
            </div>
          </div>
        </div>
    
    <!-- New Game Modal -->
    <div class="modal" id="new-game-modal" tabindex="-1" role="dialog">
        <div class="modal-dialog modal-dialog-centered" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Create a new game</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                <form>
                    <div class="form-group">
                        <label for="new-game-side">Side</label>
                        <select id="new-game-side" class="form-control form-control-sm" v-model="gameSide">
                            <option>WHITE</option>
                            <option>BLACK</option>
                            <option>OBSERVER</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <div class="form-check">
                            <input type="checkbox" v-model="againstComputer" class="form-check-input" id="new-game-against-computer">
                            <label class="form-check-label" for="new-game-against-computer">Play against computer</label>
                        </div>
                        <div class="form-check">
                            <input type="checkbox" v-model="observers" class="form-check-input" id="new-game-observer">
                            <label class="form-check-label" for="new-game-observer">Allows observers</label>
                        </div>
                        <div class="form-group">
                            <div class="form-check">
                                <input type="checkbox" v-model="specialGamePatternEnabled" class="form-check-input" id="game-special-game-enable">
                                <label class="form-check-label" for="game-special-game-enable">FEN game pattern</label>
                                <input type="text" v-model="specialGamePattern" v-if="specialGamePatternEnabled" class="form-control form-control-sm" id="new-game-special-game" placeholder="8/3Q4/8/1Q1k1Q2/8/3Q4/8/8 w">
                            </div>
                        </div>
                    </div>
                </form>
                </div>
                <div class="modal-footer">
                    <button type="button" v-on:click="createNewGameWithProperties" class="btn btn-primary">Create game</button>
                    <button type="button" class="btn btn-light" data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
    <!-- Join Game Modal -->
    <div class="modal" id="join-game-modal" tabindex="-1" role="dialog">
        <div class="modal-dialog modal-dialog-centered" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Join an existing game</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form>
                        <div class="form-group">
                            <label for="join-game-side">Side</label>
                            <select id="join-game-side" class="form-control form-control-sm" v-model="joinGameModel.gameSide">
                                <option>WHITE</option>
                                <option>BLACK</option>
                                <option>OBSERVER</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="join-game-uuid">Game ID</label>
                            <input type="text" maxlength="36" minlength="36" class="form-control" id="join-game-uuid" placeholder="Game ID" v-model="joinGameModel.gameUuid">
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" v-on:click="joinGameWithProperties" class="btn btn-primary">Join game</button>
                    <button type="button" class="btn btn-light" data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
    <!-- Pawn Promotion Game Modal -->
    <div class="modal" id="pawn-promotion-game-modal" tabindex="-1" role="dialog">
        <div class="modal-dialog modal-dialog-centered" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Pawn promotion ({{this.pawnPromotionModel.from}} to {{this.pawnPromotionModel.to}})</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form>
                        <div class="form-group">
                            <select class="form-control form-control-sm" v-model="pawnPromotionModel.piece">
                                <option>QUEEN</option>
                                <option>ROOK</option>
                                <option>BISHOP</option>
                                <option>KNIGHT</option>
                            </select>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" v-on:click="confirmPawnPromotion" class="btn btn-primary">Confirm the pawn promotion</button>
                </div>
            </div>
        </div>
    </div>
</div>
`,
    data: function () {
        return {
            blackPlayerScore: 0,
            whitePlayerScore: 0,
            pawnPromotionModel: {
                from: null,
                to: null,
                piece: "QUEEN"
            },
            joinGameModel: {
                gameUuid: null,
                gameSide: "WHITE"
            },
            uiUuid: null,
            gameUuid: null,
            gameSide: "WHITE",
            againstComputer: false,
            observers: true,
            specialGamePattern: null,
            specialGamePatternEnabled: false,
            board: [
                {
                    "unicodeIcon": "&#9820;",
                    "rawPosition": "A8",
                    "isSelected": false
                },
                {
                    "unicodeIcon": " &#9822;",
                    "rawPosition": "B8",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "&#9821;",
                    "rawPosition": "C8",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "&#9819;",
                    "rawPosition": "D8",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "&#9818;",
                    "rawPosition": "E8",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "&#9821;",
                    "rawPosition": "F8",
                    "isSelected": false
                },
                {
                    "unicodeIcon": " &#9822;",
                    "rawPosition": "G8",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "&#9820;",
                    "rawPosition": "H8",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "&#9823;",
                    "rawPosition": "A7",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "&#9823;",
                    "rawPosition": "B7",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "&#9823;",
                    "rawPosition": "C7",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "&#9823;",
                    "rawPosition": "D7",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "&#9823;",
                    "rawPosition": "E7",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "&#9823;",
                    "rawPosition": "F7",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "&#9823;",
                    "rawPosition": "G7",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "&#9823;",
                    "rawPosition": "H7",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "",
                    "rawPosition": "A6",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "",
                    "rawPosition": "B6",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "",
                    "rawPosition": "C6",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "",
                    "rawPosition": "D6",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "",
                    "rawPosition": "E6",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "",
                    "rawPosition": "F6",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "",
                    "rawPosition": "G6",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "",
                    "rawPosition": "H6",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "",
                    "rawPosition": "A5",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "",
                    "rawPosition": "B5",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "",
                    "rawPosition": "C5",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "",
                    "rawPosition": "D5",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "",
                    "rawPosition": "E5",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "",
                    "rawPosition": "F5",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "",
                    "rawPosition": "G5",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "",
                    "rawPosition": "H5",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "",
                    "rawPosition": "A4",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "",
                    "rawPosition": "B4",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "",
                    "rawPosition": "C4",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "",
                    "rawPosition": "D4",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "",
                    "rawPosition": "E4",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "",
                    "rawPosition": "F4",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "",
                    "rawPosition": "G4",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "",
                    "rawPosition": "H4",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "",
                    "rawPosition": "A3",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "",
                    "rawPosition": "B3",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "",
                    "rawPosition": "C3",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "",
                    "rawPosition": "D3",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "",
                    "rawPosition": "E3",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "",
                    "rawPosition": "F3",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "",
                    "rawPosition": "G3",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "",
                    "rawPosition": "H3",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "&#9817;",
                    "rawPosition": "A2",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "&#9817;",
                    "rawPosition": "B2",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "&#9817;",
                    "rawPosition": "C2",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "&#9817;",
                    "rawPosition": "D2",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "&#9817;",
                    "rawPosition": "E2",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "&#9817;",
                    "rawPosition": "F2",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "&#9817;",
                    "rawPosition": "G2",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "&#9817;",
                    "rawPosition": "H2",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "&#9814;",
                    "rawPosition": "A1",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "&#9816;",
                    "rawPosition": "B1",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "&#9815;",
                    "rawPosition": "C1",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "&#9813;",
                    "rawPosition": "D1",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "&#9812;",
                    "rawPosition": "E1",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "&#9815;",
                    "rawPosition": "F1",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "&#9816;",
                    "rawPosition": "G1",
                    "isSelected": false
                },
                {
                    "unicodeIcon": "&#9814;",
                    "rawPosition": "H1",
                    "isSelected": false
                }
            ],
            eventLog: [],
            moveLog: []
        };
    },
    mounted: function () {
        this.registerEvents();
    },
    beforeRouteEnter(to, from, next) { // https://next.router.vuejs.org/guide/advanced/navigation-guards.html#using-the-options-api
        next(vm => {
            if (!vm.$isAuthenticated()) { //TODO: Find a better way by using the pre-guard
                vm.$router.push({path: '/'});
            }
        })
    },
    methods: {
        confirmPawnPromotion: function () {
            let ref = this;
            let pawnPromotionModel = this.pawnPromotionModel;

            this.$postV1('/game/piece/pawn/promotion', `to=${pawnPromotionModel.to}&uuid=${this.gameUuid}&piece=${pawnPromotionModel.piece}`,
                function () {
                    $('#pawn-promotion-game-modal').modal('toggle');
                }, function () {
                    ref.$addErrorAlert("Unable to upgrade the pawn!")
                });
        },
        //---------------------------------------------------------------------------
        mapSideByteToText: function (value) {
            switch (value) {
                case 0:
                    return "BLACK";
                case 1:
                    return "WHITE";
                case 2:
                    return "OBSERVER";
                default:
                    return "";
            }
        },
        //---------------------------------------------------------------------------
        updateBoardPieces: function (items) {
            for (let i = 0; i < 64; i++) {
                this.board.pop();
            }

            for (let i = 0; i < 64; i++) {
                this.board.push(items[i])
            }
        },
        //---------------------------------------------------------------------------
        refreshGamePieces: function () {
            let ref = this;

            if (this.gameUuid) {
                this.$getV1(`/game/pieces?uuid=${this.gameUuid}`,
                    function (pieces) {
                        ref.updateBoardPieces(pieces);
                    }, function () {
                        ref.$addErrorAlert("Unable to fetch the pieces!");
                    });
            } else {
                this.$addErrorAlert("Unable to fetch the pieces location (uuid is not available)!");
            }
        },
        //---------------------------------------------------------------------------
        getCaseIdFromTargetWhenPieceDragEvent: function (target) {
            const parentElement = _.get(target, 'parentElement');

            if (!parentElement) {
                return '';
            }

            return parentElement.getAttribute('data-case-id');
        },
        //---------------------------------------------------------------------------
        clearAvailableMoveIndicators: function () {
            for (const currentCase of this.board) {
                currentCase.isSelected = false;
            }
        },
        //---------------------------------------------------------------------------
        registerEvents: function () { //TODO: Unregister the events, before adding them!
            let ref = this;

            /**
             * Drag events
             */
            document.addEventListener("dragover", function (event) {
                event.preventDefault();
            });

            document.addEventListener("dragstart", function (event) {
                if (!ref.gameUuid) {
                    event.preventDefault();
                    return; // Game is not started!
                }

                const target = event.target;
                if (!target.classList.contains("board-pieces")) {
                    event.preventDefault();
                    return; // Not a piece
                }

                let dataTransfer = event.dataTransfer;
                dataTransfer.setData("from", ref.getCaseIdFromTargetWhenPieceDragEvent(target));
            });

            document.addEventListener("drop", function (event) {
                if (!ref.gameUuid) {
                    event.preventDefault();
                    return; // Game is not started!
                }

                const target = event.target;
                const classList = target.classList;

                const isBoardCase = classList.contains("board-case");
                const isBoardPiece = classList.contains("board-pieces");
                let dataTransfer = event.dataTransfer;

                if ((!isBoardCase && !isBoardPiece) || !dataTransfer) {
                    event.preventDefault();
                    return;
                }

                let from = dataTransfer.getData("from");

                if (from === '') {
                    event.preventDefault();
                    return;
                }

                let to;
                if (isBoardCase) {
                    to = target.getAttribute('data-case-id');
                } else if (isBoardPiece) {
                    to = ref.getCaseIdFromTargetWhenPieceDragEvent(target);
                }

                ref.whenPieceDraggedEvent(from, to);
            });

            document.addEventListener("mouseover", function (event) {
                if (!ref.gameUuid) {
                    event.preventDefault();
                    return; // Game is not started!
                }

                const target = event.target;
                const classList = target.classList;
                const isBoardPiece = classList.contains("board-pieces");

                if ((!isBoardPiece)) {
                    event.preventDefault();
                    return;
                }

                if (target.getAttribute('data-piece-side') !== ref.gameSide) {
                    event.preventDefault();
                    return;
                }

                const from = ref.getCaseIdFromTargetWhenPieceDragEvent(target);

                ref.$getV1(`/game/moves?from=${from}&uuid=${ref.gameUuid}`,
                    null,
                    function () {
                        ref.$addErrorAlert("Unable to get the moves positions!");
                    });
            });

            document.addEventListener("mouseout", function (event) {
                if (!ref.gameUuid) {
                    event.preventDefault();
                    return; // Game is not started!
                }

                const target = event.target;
                const classList = target.classList;
                const isBoardPiece = classList.contains("board-pieces");

                if ((!isBoardPiece)) {
                    event.preventDefault();
                    return;
                }

                ref.clearAvailableMoveIndicators();
            });
        },
        //---------------------------------------------------------------------------
        saveUuid: function (data) {
            this.gameUuid = data;
        },
        //---------------------------------------------------------------------------
        onGameEvent: function (payload) {
            let parsed = JSON.parse(payload.body);
            let chessEvent = parsed.event;
            let message = parsed.message;

            switch (chessEvent) {
                // case 'UI_SESSION_EXPIRED': //FIXME
                //     window.setInterval(function () {
                //         location.reload();
                //     }, 10 * 1000);
                //     this.$addErrorAlert(message);
                //     break;
                case 'PLAYER_JOINED':
                    this.$addSuccessAlert(message);
                    break;
                case 'TRY_JOIN_GAME':
                    this.$addErrorAlert(message);
                    break;
                case 'MOVE':
                    this.refreshGamePieces();
                    this.moveLog.push(message);
                    break;
                case 'GAME_WON':
                    this.eventLog.push(message);
                    break;
                case 'GAME_WON_EVENT_MOVE':
                    this.eventLog.push(message);
                    break;
                case 'SCORE_UPDATE':
                    this.blackPlayerScore = message.blackPlayerPoint;
                    this.whitePlayerScore = message.whitePlayerPoint;
                    break;
                case 'REFRESH_BOARD':
                    this.refreshGamePieces();
                    break;
                case 'PAWN_PROMOTION':
                    this.handlePawnPromotion(message);
                    break;
                case 'KING_CHECKMATE':
                    this.$addWarningAlert(message);
                    break;
            }
        },
        //---------------------------------------------------------------------------
        onGameSideEvent: function (payload) {
            let parsed = JSON.parse(payload.body);
            let chessEvent = parsed.event;
            let message = parsed.message;
            let obj = parsed.obj;

            switch (chessEvent) {
                case 'PLAYER_TURN':
                    this.eventLog.push(message);
                    this.$addSuccessAlert(message);
                    break;
                case 'PAWN_PROMOTION':
                    //FIXME
                    break;
                case 'KING_CHECK':
                    this.$addWarningAlert(message);
                    break;
                case 'AVAILABLE_MOVE':
                    const from = obj.from;
                    if (from) {
                        var positions = obj.positions;
                        for (const currentCase of this.board) {
                            currentCase.isSelected = false; // Unselect
                            const numberOfSelected = positions.length;

                            if(numberOfSelected === 0) {
                                break; // None left
                            }

                            for (let i = 0; i < numberOfSelected; i++) {
                                if(currentCase.rawPosition === positions[i]) {
                                    currentCase.isSelected = true;
                                    positions.splice(i, 1); // already found
                                    break;
                                }
                            }
                        }
                    }
                    break;
            }
        },
        //---------------------------------------------------------------------------
        initGameComponents: function () {
            let basePath = `/topic/${this.gameUuid}`;
            let sideEventPath = `${basePath}/${this.gameSide}`;
            this.$registerGameEvents(basePath, sideEventPath, this.onGameEvent, this.onGameSideEvent);
        },
        //---------------------------------------------------------------------------
        initNewGame: function (gameUuid, gameSide) {
            this.eventLog = [];
            this.moveLog = [];
            this.blackPlayerScore = 0;
            this.whitePlayerScore = 0;

            if (gameSide) {
                this.gameSide = gameSide;
            }

            this.saveUuid(gameUuid);
            this.refreshGamePieces();
            this.initGameComponents();
        },
        //---------------------------------------------------------------------------
        createNewGameWithProperties: function () {
            let ref = this;

            this.fetchNewUiUuidAndExecute(function () {
                let dataAsStr = `side=${ref.gameSide}&againstComputer=${ref.againstComputer}&observers=${ref.observers}`;

                if (ref.specialGamePatternEnabled) {
                    dataAsStr += `&specialGamePieces=${ref.specialGamePattern}`;
                }

                ref.$postV1('/game/create', dataAsStr,
                    function (data) {
                        ref.initNewGame(data.response);
                        $('#new-game-modal').modal('hide')
                    }, function () {
                        ref.$addErrorAlert("Unable to create a new game!");
                    })

            }, function () {
                ref.$addErrorAlert("Unable to obtain a game id!");
            });
        },
        //---------------------------------------------------------------------------
        joinGameWithProperties: function () {
            let ref = this;

            this.fetchNewUiUuidAndExecute(function () {
                ref.$postV1('/game/join', `uuid=${ref.joinGameModel.gameUuid}&side=${ref.joinGameModel.gameSide}&uiUuid=${ref.uiUuid}`,
                    function () {
                        ref.initNewGame(ref.joinGameModel.gameUuid, ref.joinGameModel.gameSide);
                        $('#join-game-modal').modal('hide')
                    }, function () {
                        ref.$addErrorAlert("Unable to join the selected game!");
                    })
            }, function () {
                ref.$addErrorAlert("Unable to obtain a game id!");
            });
        },
        //---------------------------------------------------------------------------
        fetchNewUiUuidAndExecute: function (passCallback, failCallback) {
            let ref = this;

            this.$getV1('/ui/id',
                function (data) {
                    passCallback(data);
                    ref.uiUuid = data.response;
                },
                function () {
                    failCallback();
                });
        },
        //---------------------------------------------------------------------------
        newGame: function () {
            $('#new-game-modal').modal('toggle')
        },
        //---------------------------------------------------------------------------
        joinGame: function () {
            $('#join-game-modal').modal('toggle')
        },
        //---------------------------------------------------------------------------
        whenPieceDraggedEvent: function (from, to) {
            let ref = this;

            if (this.gameUuid && from && to && (from !== to)) {
                this.$postV1('/game/move', `from=${from}&to=${to}&uuid=${ref.gameUuid}`, null,
                    function () {
                        ref.$addErrorAlert("Unable to move to the selected position!");
                    });
            }
        },
        //---------------------------------------------------------------------------
        handlePawnPromotion(message) {

            if (message.gameSide === this.gameSide) {
                let pawnPromotionModel = this.pawnPromotionModel;
                pawnPromotionModel.from = message.from;
                pawnPromotionModel.to = message.to;

                $('#pawn-promotion-game-modal').modal('toggle');
            }
        }
    }
};