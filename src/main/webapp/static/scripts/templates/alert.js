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

const Alert = {
    template:
        `
    <div id="alert-container">
        <div v-for="(message, index) in alertMessages"
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
`,
    props: {
        alertBus: {
            type: Array
        }
    },
    watch: {
        alertBus: {
            handler: function (newValue) {
               this.addAlert(newValue.level, newValue.message, newValue.delay, newValue.iconType, newValue.iconClass);
            }
        }
    },
    data: function () {
        return {
            alertMessages: []
        };
    },
    methods: {
        /**
         * Method to show an alert.
         * @param level - The css level of the alert of bootstrap {alert-primary, alert-secondary, alert-success, alert-danger, alert-warning, alert-info, alert-light or alert-dark}
         * @param message - The message to show
         * @param delay - The delay, in seconds
         * @param iconType - The icon type class of Font Awesome {fas, fad, far or fal}
         * @param iconClass - The icon class of Font Awesome {the icon}
         */
        addAlert: function (level, message, delay, iconType, iconClass) {

            for (var messageIdx = 0; messageIdx < this.alertMessages.length; messageIdx++) {
                let alertMessage = (this.alertMessages)[messageIdx];

                if (alertMessage.message === message) {
                    alertMessage.initOrResetTimer(delay);
                    alertMessage.increment();
                    return;
                }
            }

            let ref = this;

            let alert = {
                "alertId": this.generateUuidV4(),
                "level": level,
                "message": message,
                "iconType": iconType,
                "iconClass": iconClass,
                "toRemove": false,
                "timer": null,
                "numberOfMessages": 1,
                "increment": function () {
                    this.numberOfMessages++;
                },
                "haveMoreThanOneMessage": function () {
                    return this.numberOfMessages > 1;
                },
                "initOrResetTimer": function (time) {
                    if (!this.timer) {
                        clearTimeout(this.timer);
                    }

                    let refAlert = this;

                    this.timer = setTimeout(function () {
                        for (var messageIdx = 0; messageIdx < ref.alertMessages.length; messageIdx++) {
                            let message = ref.alertMessages[messageIdx];

                            if (message.alertId === refAlert.alertId) {
                                ref.alertMessages.splice(messageIdx, 1);
                                break;
                            }
                        }

                    }, (time) ? time * 1000 : 5000);

                    return this;
                }
            };


            this.alertMessages.push(alert.initOrResetTimer(delay));
        },
        /**
         * Method to generate an UUID v4.
         * Thanks to @broofa https://stackoverflow.com/a/2117523/12511456
         */
        generateUuidV4: function () {
            return ([1e7] + -1e3 + -4e3 + -8e3 + -1e11).replace(/[018]/g, c =>
                (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
            )
        }
    }
};