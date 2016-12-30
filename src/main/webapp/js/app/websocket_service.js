var App = App || {};
App.WebSocketService = ( function() {

    var websocketEndpoint = "ws://localhost:8080/echo";
    var websocket;

    var openConnection = function() {

        websocket = new WebSocket(websocketEndpoint);

        websocket.onopen = function(evt) {

            console.log('Websocket Connection Opened.');
            $('#openLine').val('Yes.');
            $('#closeLine').val('No.');
        };
        websocket.onclose = function(evt) {

            console.log('Websocket Connection Closed.');
            $('#closeLine').val('Yes.');
            $('#openLine').val('No');
        };
        websocket.onmessage = function(evt) {

            console.log('Websocket Message Received.');
            $('#receiveLine').val(evt.data);
            $('#sendLine').val('');

        };
        websocket.onerror = function(evt) {
            alert("Error: " + evt.data);
        };
    };

    var sendMessage = function(messageToSend) {

        websocket.send(messageToSend);
    };

    var closeConnection = function() {

        websocket.close();
    };

    return {
        openConnection: openConnection,
        sendMessage: sendMessage,
        closeConnection: closeConnection
    }


} )();