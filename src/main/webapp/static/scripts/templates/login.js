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
                <button id="login-button" class="fluid ui large teal button" v-on:click="login">Login</button>
            </div>
            <div class="ui error message"></div>
            <!--PUT CSRF TOKEN-->
        </div>
    </div>
</div>
`,
    methods: {
        login: function () {
            this.$parent.login();
        }
    }
};

<!--<input id="csrfToken" name="_csrf" type="hidden" value="${_csrf.token}">-->