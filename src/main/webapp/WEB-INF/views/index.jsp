<!--
~ Copyright 2014 - 2018 Yannick Watier
~
~ Licensed under the Apache License, Version 2.0 (the "License");
~ you may not use this file except in compliance with the License.
~ You may obtain a copy of the License at
~
~ http://www.apache.org/licenses/LICENSE-2.0
~
~ Unless required by applicable law or agreed to in writing, software
~ distributed under the License is distributed on an "AS IS" BASIS,
~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
~ See the License for the specific language governing permissions and
~ limitations under the License.
-->
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<body>
<h1>Login</h1>
<h3>CUSTOM PAGE</h3>

<form name='f' action="api/v1/login" method='POST'>
    <table>
        <tr>
            <td>User:</td>
            <td><input type='text' name='username'></td>
        </tr>
        <tr>
            <td>Password:</td>
            <td><input type='password' name='password'/></td>
        </tr>
        <tr>
            <td><input name="submit" type="submit" value="submit"/></td>
        </tr>
        <input id="csrfToken" name="_csrf" type="hidden" value="${_csrf.token}">
    </table>
</form>
</body>
</html>