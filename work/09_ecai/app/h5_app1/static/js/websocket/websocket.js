var WS = function(){
    var webSocket;
    var reconnectId;
    var hearBeatId;
    var lastParams;
    var _connect = function(params, onMessage){
        if (webSocket && params == lastParams) {
            return;
        }
        lastParams = params;

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
                type: 'POST',
                cache: false,
                dataType: 'json',
                contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
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
                },
                error: function() {
                },
                fail: function() {
                },
                complete: function() {
                }
            });
        }else {
            $.ajax({
                url: '/webSocketToken',
                type: 'POST',
                cache: false,
                dataType: 'json',
                contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
                data: {},
                success: function(res) {
                    if (res.token) {
                        webSocket = new SockJS(window.location.protocol + "//" + window.location.host + "/websocket/sockjs?" + params + "&token=" + res.token);
                        _process(params, onMessage);
                    }
                },
                error: function() {
                },
                fail: function() {
                },
                complete: function() {
                }
            });
        }
    }

    var _process = function(params, onMessage) {
        webSocket.onopen = function () {
            console.log("socket连接已建立");
            if (reconnectId) {
                clearInterval(reconnectId);
                reconnectId = null;
            }
        };
        webSocket.onmessage = function (event) {
            onMessage(event.data);
        };
        webSocket.onclose = function (event) {
            console.log("socket关闭");
            if (!reconnectId && event.code === 1001) {
                console.log("socket服务器断开,自动重连");
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

    var _disconnect = function(){
        if (reconnectId) {
            clearInterval(reconnectId);
            reconnectId = null;
        }
        if (hearBeatId) {
            clearInterval(hearBeatId);
            hearBeatId = null;
        }

        if (webSocket != null) {
            webSocket.close();
            webSocket = null;
        }
    }

    return {
        connect: function(params, onMessage) {
            _connect(params, onMessage);
        },
        disconnect: function() {
            _disconnect();
        }
    }
}();