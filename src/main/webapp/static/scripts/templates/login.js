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
                <input type="text" id="username-input" name="username" class="form-control" placeholder="Username"  autofocus="">
            </div>
          
            <div class="input-group mb-3">
                <div class="input-group-prepend">
                <span class="input-group-text" id="basic-addon1"><i class="fas fa-lock"></i></span>
                </div>
                <input type="password" id="password-input" name="password" class="form-control" placeholder="Password">
            </div>
          
            <button id="login-button" type="button" class="btn btn-secondary btn-lg" v-on:click="login"  :disabled="isLoginInProgress">
                <i v-if="isLoginInProgress" class="fas fa-spinner fa-pulse"></i>
                <span v-else>Login</span>
            </button>
        </form>
        <div class="ui error message"></div>
    </div>
</div>
`,
    data: function () {
        return {
            isLoginInProgress: false
        };
    },
    methods: {
        login: function () {

            let ref = this;
            let parent = ref.$parent;
            let user = $('#username-input').val();
            let pwd = $('#password-input').val();

            this.isLoginInProgress = true;

            if (!user || !pwd) {
                alertify.error("Password or the username cannot be empty!", 5);
                return;
            }

            $.ajax({
                url: `${parent.baseApi}/oauth/token`,
                type: "POST",
                cache: false,
                timeout: 30000,
                datatype: "x-www-form-urlencoded",
                data: `username=${user}&password=${pwd}&grant_type=password`,
                beforeSend: function (xhr) {
                    xhr.setRequestHeader("Authorization", "Basic Y2xpZW50SWQ6c2VjcmV0");
                },
            }).done(parent.authSuccessEvent).fail(function () {
                alertify.error("Bad credentials!", 5);
                ref.isLoginInProgress = false;
            });
        },
    }
};