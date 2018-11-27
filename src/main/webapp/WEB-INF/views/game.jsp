<!--
  ~    Copyright 2014 - 2018 Yannick Watier
  ~
  ~    Licensed under the Apache License, Version 2.0 (the "License");
  ~    you may not use this file except in compliance with the License.
  ~    You may obtain a copy of the License at
  ~
  ~        http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~    Unless required by applicable law or agreed to in writing, software
  ~    distributed under the License is distributed on an "AS IS" BASIS,
  ~    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~    See the License for the specific language governing permissions and
  ~    limitations under the License.
  -->

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="_csrf" content="${_csrf.token}"/>

    <title>Title</title>
    <link rel="stylesheet" type="text/css" href="style/game.css">
    <link rel="stylesheet" type="text/css" href="style/semantic.min.css">
    <link rel="stylesheet" type="text/css" href="style/alertify.min.css">
    <link rel="stylesheet" type="text/css" href="style/themes/default_alertifyjs.min.css">

    <script src="scripts/jquery-3.2.1.min.js"></script>
    <script src="scripts/semantic.min.js"></script>
    <script src="scripts/sockjs.min.js"></script>
    <script src="scripts/stomp.min.js"></script>
    <script src="scripts/alertify.min.js"></script>
    <script src="scripts/game.js"></script>
    <script src="scripts/boardHelper.js"></script>
    <script src="scripts/connexionManager.js"></script>
</head>
<body>

<div class="ui modal" id="modalSpecialGameMoreInfo">
    <div class="header">More info</div>
    <div class="content">
        <span>Click on a case to set a piece</span>
        <table id="helperBoard" class="tab"></table>
        <div class="ui icon input">
            <input id="inputValidatePatternSpecialGame" placeholder="Place your text here..." type="text">
            <i id="iconValidatePatternSpecialGame" class="warning circle icon"></i>
        </div>
        <button class="ui button blue" id="buttonValidatePatternSpecialGame">Validate & Show</button>
        <button class="ui button green" id="buttonSendPatternSpecialGame">Send & Close the helper</button>
        <button class="ui button red" id="buttonResetPatternSpecialGame">Reset the board</button>
    </div>
</div>


<div class="ui modal" id="modalPawnPromotion">
    <div class="header">Pick a piece</div>
    <div class="content">
        <select class="ui dropdown" id="dropdownPawnPromo">
            <option value="QUEEN" selected>Queen</option>
            <option value="KNIGHT">Knight</option>
            <option value="ROOK">Rook</option>
            <option value="BISHOP">Bishop</option>
        </select>
        <button class="ui button green" id="buttonSendChoicePawnPromotion">Choose</button>
    </div>
</div>

<div class="ui modal" id="modalJoinGame">
    <div class="header">Join a game</div>
    <div class="content">
        <div class="ui labeled action input" id="joinGameGroup">
            <input placeholder="Game ID" type="text" name="gameIdJoinGame" id="joinGame">
            <select id="chooseSideJoinGame" class="ui compact">
                <option value="WHITE">White</option>
                <option value="BLACK" selected>Black</option>
                <option value="OBSERVER">Observer</option>
            </select>
            <button class="ui button" id="joinGameButton">OK</button>
        </div>
    </div>
</div>

<div class="ui modal" id="modalCreateNewGame">
    <div class="header">Create a new game</div>
    <div class="content">
        <div class="ui segment">
            <div class="ui toggle checkbox" id="againstComputer">
                <input type="checkbox">
                <label>Play against a computer</label>
            </div>
        </div>
        <div class="ui segment">
            <div class="ui toggle checkbox" id="allowOtherObserver">
                <input type="checkbox">
                <label>Allow observers</label>
            </div>
        </div>
        <div class="ui segment">
            <select id="changeSide">
                <option value="WHITE" selected>White</option>
                <option value="BLACK">Black</option>
                <option value="OBSERVER">Observer</option>
            </select>
            <label for="changeSide">side and</label>
            <select id="gameType">
                <option value="CLASSIC" selected>Classic</option>
                <option value="SPECIAL">Special order</option>
            </select>
            <label for="gameType">game mode</label>
            <label id="specialGamePiecesLabel" for="specialGamePieces" hidden> with pieces set has</label>
            <div class="ui input" id="divSpecialGamePieces">
                <input id="specialGamePieces" placeholder="E1:W_KING;E8:B_KING" type="text" hidden>
            </div>
        </div>
        <div class="ui center aligned segment">
            <button class="ui compact green labeled icon button" id="createGame">
                <i class="icon plus"></i>
                Create a new game
            </button>
        </div>
    </div>
</div>

<div class="ui sidebar inverted vertical menu">
    <a class="item" id="mainMenuCreateGame">Create a new game</a>
    <a class="item" id="mainMenuJoinGame">Join a game</a>
</div>
<div class="pusher">
    <button class="ui button" id="showMainMenuButton"><i class="sidebar icon"></i></button>

    <div id="gameLayout">
        <div id="mainBoardLayout">
            Game ID: <span id="uuid">NONE</span><br>
            <table id="board"></table>
            <div class="ui label">
                White
                <div class="detail" id="whitePlayerScore">0</div>
            </div>
            <div class="ui label">
                Black
                <div class="detail" id="blackPlayerScore">0</div>
            </div>
        </div>
        <select id="chessLog" size="15" class="ui bottom attached"></select>
    </div>
</div>
</body>
</html>