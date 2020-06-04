/*
 *    Copyright 2014 - 2020 Yannick Watier
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
Vue.mixin({
    data: function () {
        return {
            alertBus: null,
        }
    },
    methods: {
        /**
         * Wrapper method to add a warning alert.
         * @param message - The message to show
         */
        addWarningAlert: function (message) {
            this.createNewAlertEvent('alert-warning', message, 3)
        },
        /**
         * Wrapper method to add an error alert.
         * @param message - The message to show
         */
        addErrorAlert: function (message) {
            this.createNewAlertEvent('alert-danger', message, 5)
        },
        /**
         * Wrapper method to add a success alert.
         * @param message - The message to show
         */
        addSuccessAlert: function (message) {
            this.createNewAlertEvent('alert-success', message, 3)
        },
        createNewAlertEvent: function (level, message, delay, iconType, iconClass) {
            if (!level || !message) {
                return;
            }

            this.alertBus = {
                "level": level,
                "message": message,
                "delay": delay,
                "iconType": iconType,
                "iconClass": iconClass,
                "timestamp": Date.now()
            }
        }
    }
});
