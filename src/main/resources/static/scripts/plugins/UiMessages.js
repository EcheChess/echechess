class UiMessages {
    constructor() {
    }

    install(app, options) {
        app.config.globalProperties.uiMessages = [];

        app.config.globalProperties.$getGameMessages = function () {
            return _.cloneDeep(this.uiMessages);
        }

        /**
         * Method to show an alert.
         * @param level - The css level of the alert of bootstrap {alert-primary, alert-secondary, alert-success, alert-danger, alert-warning, alert-info, alert-light or alert-dark}
         * @param message - The message to show
         * @param delay - The delay, in seconds
         * @param iconType - The icon type class of Font Awesome {fas, fad, far or fal}
         * @param iconClass - The icon class of Font Awesome {the icon}
         */
        app.config.globalProperties.$addAlert = function (level, message, delay, iconType, iconClass) {
            for (var messageIdx = 0; messageIdx < this.uiMessages.length; messageIdx++) {
                let alertMessage = (this.uiMessages)[messageIdx];

                if (alertMessage.message === message) {
                    alertMessage.initOrResetTimer(delay);
                    alertMessage.increment();
                    return;
                }
            }

            let ref = this;

            let alert = {
                "alertId": this.$generateUuidV4(),
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

                    let alertRef = this;

                    this.timer = setTimeout(function () {
                        for (var messageIdx = 0; messageIdx < ref.uiMessages.length; messageIdx++) {
                            let message = ref.uiMessages[messageIdx];

                            if (message.alertId === alertRef.alertId) {
                                ref.uiMessages.splice(messageIdx, 1);
                                break;
                            }
                        }

                    }, (time) ? time * 1000 : 5000);

                    return this;
                }
            };


            this.uiMessages.push(alert.initOrResetTimer(delay));
        }

        /**
         * Wrapper method to add a warning alert.
         * @param message - The message to show
         */
        app.config.globalProperties.$addWarningAlert = function (message) {
            this.$addAlert('alert-warning', message)
        }
        /**
         * Wrapper method to add an error alert.
         * @param message - The message to show
         */
        app.config.globalProperties.$addErrorAlert = function (message) {
            this.$addAlert('alert-danger', message)
        }
        /**
         * Wrapper method to add a success alert.
         * @param message - The message to show
         */
        app.config.globalProperties.$addSuccessAlert = function (message) {
            this.$addAlert('alert-success', message)
        }
    }
}