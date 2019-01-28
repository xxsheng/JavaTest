var WS = function(){
    var webSocket;
    var reconnectId;
    var hearBeatId;
    var _connect = function(params, onMessage){
        if (window.forcelogout && window.forcelogout === true) {
            return;
        }
        
        if (reconnectId) {
            clearInterval(reconnectId);
            reconnectId = null;
        }
        if (hearBeatId) {
            clearInterval(hearBeatId);
            hearBeatId = null;
        }
        if ('WebSocket' in window) {
            $.ajax({
                url: '/webSocketToken',
                data: {},
                success: function(res) {
                    if (res.token) {
                        if (window.location.protocol == 'http:') {
                            webSocket = new WebSocket("ws://" + window.location.host + "/websocket?" + params + "&token=" + res.token);
                            _process(params, onMessage);
                        }
                        else {
                            webSocket = new WebSocket("wss://" + window.location.host + "/websocket?" + params + "&token=" + res.token);
                            _process(params, onMessage);
                        }
                    }
                }
            })
        }else {
            $.ajax({
                url: '/webSocketToken',
                data: {},
                success: function(res) {
                    if (res.token) {
                        webSocket = new SockJS(window.location.protocol + "//" + window.location.host + "/websocket/sockjs?" + params + "&token=" + res.token);
                        _process(params, onMessage);
                    }
                }
            })
        }
    }

    var _process = function(params, onMessage) {
        webSocket.onopen = function () {
            if (reconnectId) {
                clearInterval(reconnectId);
            }
        };
        webSocket.onmessage = function (event) {
            onMessage(event.data);
        };
        webSocket.onclose = function (event) {
            if (!reconnectId && event.code === 1001) {
                console.log("socket关闭,自动重连");
                webSocket = null;
                reconnectId = setInterval(function(){
                    _connect(params, onMessage);
                }, 30000);
            }
        };
        webSocket.onerror = function () {
            console.log("socket发生错误,自动重连");
            if (!reconnectId) {
                webSocket = null;
                reconnectId = setInterval(function(){
                    _connect(params, onMessage);
                }, 30000);
            }
        };

        if (webSocket) {
            hearBeatId = setInterval(function () {
                if (webSocket && webSocket.readyState == 1) {
                    webSocket.send('h');
                }
            }, 10000);
        }
    }

    return {
        connect: function(params, onMessage) {
            _connect(params, onMessage);
        }
    }
}();