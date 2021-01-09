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


<!-- TODO: Create a static builder to be able to reuse the `router` variables, ect  -->

const router = VueRouter.createRouter({
    history: VueRouter.createWebHashHistory(),
    routes: [
        {path: '/:pathMatch(.*)*', component: Login},
        {path: '/', component: Login},
        {path: '/game', component: Game}
    ]
});

const vm = Vue.createApp({});

vm.use(router);
vm.use(new UiMessages());
vm.use(new Utils());
vm.use(new StompAndJqueryAjaxWebApi());

vm.mount('#app');
