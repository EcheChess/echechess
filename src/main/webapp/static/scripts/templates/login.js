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
<div id="login-container-div" class="ui middle aligned center aligned grid">
    <div class="column">
        <h2 class="ui teal header">
            Log-in
        </h2>
        <div class="ui large form">
            <div class="ui stacked segment">
                <div class="field">
                    <div class="ui left icon input">
                        <i class="user icon"></i>
                        <input id="username-input" type="text" name="username" placeholder="Username">
                    </div>
                </div>
                <div class="field">
                    <div class="ui left icon input">
                        <i class="lock icon"></i>
                        <input id="password-input" type="password" name="password" placeholder="Password">
                    </div>
                </div>
                
                <button class="fluid ui large teal button" v-on:click="login" :disabled="isLoginInProgress">
                    <i v-if="isLoginInProgress" class='spinner icon'></i>
                    <span v-else>Login</span>
                </button>
            </div>
            <div class="ui error message"></div>
        </div>
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
                    xhr.setRequestHeader("X-CSRF-TOKEN", parent.csrf);
                },
            }).done(parent.authSuccessEvent).fail(function () {
                alertify.error("Login failed!", 5);
                ref.isLoginInProgress = false;
            });
        },
    }
};