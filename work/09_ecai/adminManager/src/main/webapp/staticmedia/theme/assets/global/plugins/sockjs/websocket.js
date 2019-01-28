var WS = function(){
    var webSocket;
    var reconnectId;
    var hearBeatId;
    var _connect = function(params, onMessage){
        if (reconnectId) {
            clearInterval(reconnectId);
            reconnectId = null;
        }
        if (hearBeatId) {
            clearInterval(hearBeatId);
            hearBeatId = null;
        }
        if ('WebSocket' in window) {
            if (window.location.protocol == 'http:') {
                webSocket = new WebSocket("ws://" + window.location.host + "/websocket?" + params);
            }
            else {
                webSocket = new WebSocket("wss://" + window.location.host + "/websocket?" + params);
            }
        }else {
            webSocket = new SockJS(window.location.protocol + "//" + window.location.host + "/websocket/sockjs?" + params);
        }

        webSocket.onopen = function () {
            console.log("socket连接已建立");
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
                console.log(event);
                webSocket = null;
                reconnectId = setInterval(function(){
                    _connect(params, onMessage);
                }, 30000);
            }
        };
        webSocket.onerror = function () {
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