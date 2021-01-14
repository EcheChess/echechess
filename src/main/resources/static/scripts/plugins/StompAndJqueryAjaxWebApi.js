class StompAndJqueryAjaxWebApi {
    constructor() {
    }

    install(app) {
        app.config.globalProperties.websocketClient = undefined;

        const protocol = `${window.location.protocol}`;
        const hostname = window.location.hostname;
        const port = window.location.port;
        const isSecure = protocol === 'https:';

        app.config.globalProperties.api = {
            "restUrl": `${protocol}//${hostname}:${port}`,
            "websocketUrl": `${isSecure ? 'wss' : 'ws'}://${hostname}:${port}`
        };

        app.config.globalProperties.oauth = {
            "token": undefined,
            "refreshToken": undefined,
            "expiration": undefined,
            "scopes": []
        }

        app.config.globalProperties.$isAuthenticated = function () {
            return (app.config.globalProperties.oauth && app.config.globalProperties.oauth.token) !== undefined;
        }

        app.config.globalProperties.$login = function (user, password, success, fail) {
            $.ajax({
                url: `${app.config.globalProperties.api.restUrl}/oauth/token`,
                type: "POST",
                cache: false,
                timeout: 30000,
                datatype: "x-www-form-urlencoded",
                data: `username=${user}&password=${password}&grant_type=password`,
                beforeSend: function (xhr) {
                    xhr.setRequestHeader("Authorization", "Basic Y2xpZW50SWQ6c2VjcmV0");
                },
            }).done(function (body) {
                app.config.globalProperties.oauth.token = body["access_token"];
                app.config.globalProperties.oauth.refreshToken = body["refresh_token"];
                app.config.globalProperties.oauth.expiration = body["expires_in"];

                for (const scope of body["scope"].split(" ")) {
                    app.config.globalProperties.oauth.scopes.push(scope);
                }

                if (success) {
                    success();
                }

            }).fail(function () {
                if (success) {
                    fail();
                }
            });
        }

        app.config.globalProperties.$get = function (baseUrl, success, fail) {
            const correctedUrl = app.config.globalProperties.api.restUrl + makeBaseUrlValid(baseUrl);
            fetch(correctedUrl, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${app.config.globalProperties.oauth.token}`,
                }
            }).then(response => {
                handleFetchRequestResponseWithCallback(response, success, fail);
            })
        }

        app.config.globalProperties.$post = function (baseUrl, body, success, fail) {
            const correctedUrl = app.config.globalProperties.api.restUrl + makeBaseUrlValid(baseUrl);
            fetch(correctedUrl, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${app.config.globalProperties.oauth.token}`,
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                },
                body: encodeURI(body),
            }).then(response => {
                handleFetchRequestResponseWithCallback(response, success, fail);
            })
        }

        app.config.globalProperties.$getV1 = function (baseUrl, success, fail) {
            app.config.globalProperties.$get(`/api/v1${makeBaseUrlValid(baseUrl)}`, success, fail);
        }

        app.config.globalProperties.$postV1 = function (baseUrl, body, success, fail) {
            app.config.globalProperties.$post(`/api/v1${makeBaseUrlValid(baseUrl)}`, body, success, fail);
        }

        //TODO: find a better than using callbacks
        app.config.globalProperties.$registerGameEvents = function (basePath, sideEventPath, gameEventCallback, sideEventCallback, gameStartCallback) {
            if (app.config.globalProperties.websocketClient) {
                app.config.globalProperties.websocketClient.unsubscribe();
            } else {
                let url = `${app.config.globalProperties.api.websocketUrl}/websocket?access_token=${app.config.globalProperties.oauth.token}`;
                app.config.globalProperties.websocketClient = Stomp.over(new WebSocket(url));
            }

            let headers = {
                "Authorization": `Bearer ${app.config.globalProperties.oauth.token}`
            };

            app.config.globalProperties.websocketClient.connect(headers, function () {
                if(gameStartCallback) {
                    gameStartCallback();
                }

                app.config.globalProperties.websocketClient.subscribe(sideEventPath, sideEventCallback);
                app.config.globalProperties.websocketClient.subscribe(basePath, gameEventCallback);
            });
        }

        function handleFetchRequestResponseWithCallback(response, success, fail) {
            if (response.ok) {
                if (success) { //TODO: Check if the return code is valid (! 4xx & 5xx, ect)
                    response.text().then(text => {
                        if (_.trim(text) !== '') {
                            success(JSON.parse(text));
                        } else {
                            success();
                        }
                    });
                }
            } else if (fail) {
                fail();
            }
        }

        function makeBaseUrlValid(url) {
            if (url === '/') {
                throw new Error("Unable to handle root URL in the API!");
            }

            if (!_.startsWith(url, '/')) {
                return `/${url}`;
            }

            return url;
        }
    }
}