/*
 *    Copyright 2014 - 2019 Yannick Watier
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

const Login = {
    template:
        `
<div id="login-container" v-on:keyup.enter="login">
    <div id="login-panel">
        <form class="form-signin text-center">
            <h1 id="login-header-text" class="h3 mb-3 font-weight-normal">Log-in</h1>
            <div class="input-group mb-3">
                <div class="input-group-prepend">
                    <span class="input-group-text" id="basic-addon1"><i class="fas fa-user"></i></span>
                </div>
                <input v-model="username" type="text" name="username" class="form-control" placeholder="Username" autofocus="">
            </div>
          
            <div class="input-group mb-3">
                <div class="input-group-prepend">
                <span class="input-group-text" id="basic-addon1"><i class="fas fa-lock"></i></span>
                </div>
                <input v-model="password" type="password" name="password" class="form-control" placeholder="Password">
            </div>
          
            <button id="login-button" type="button" class="btn btn-secondary btn-lg" v-on:click="login"  :disabled="isLoginInProgress">
                <i v-if="isLoginInProgress" class="fas fa-spinner fa-pulse"></i>
                <span v-else>Login</span>
            </button>
        </form>
        <div class="ui error message"></div>
    </div>
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
</div>
`,
    data: function () {
        return {
            isLoginInProgress: false,
            username: null,
            password: null
        };
    },
    methods: {
        login: function () {
            let ref = this;

            this.isLoginInProgress = true;

            if (!this.username || !this.password) {
                this.$addErrorAlert("Password or the username cannot be empty!");
                return;
            }

            this.$login(this.username, this.password,
                function () {
                    ref.$router.push({path: '/game'});
                }, function () {
                    ref.$addErrorAlert("Bad credentials!");
                    ref.isLoginInProgress = false;
                })
        }
    }
};