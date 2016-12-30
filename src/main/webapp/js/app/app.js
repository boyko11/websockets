var App = App || {};

App.init = (function() {
    $('html').off('click', '#openButton').on('click', '#openButton', function() {
        App.WebSocketService.openConnection();
    });

    $('html').off('click', '#sendButton').on('click', '#sendButton', function() {

        var messageToSend = $('#sendLine').val();
        if (!messageToSend) {
            messageToSend = "You ugly. You yo daddy son.";
        }
        App.WebSocketService.sendMessage(messageToSend);
    });

    $('html').off('click', '#closeButton').on('click', '#closeButton', function() {
        App.WebSocketService.closeConnection();
    });
})();
