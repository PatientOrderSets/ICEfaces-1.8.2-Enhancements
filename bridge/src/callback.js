[Ice].as(function(This) {
    function findBridgeInParentsOrChildren(id) {
        try {
            //search the bridge through the parent elements
            return id.asExtendedElement().findBridge();
        } catch (e) {
            var children = id.asElement().getElementsByTagName('*');
            var size = children.length;
            for (var i = 0; i < size; i++) {
                var child = children[i];
                if (child.bridge) {
                    return child.bridge;
                }
            }

            throw 'Cannot find bridge instance among the children or parents of [' + id + ']';
        }
    }

    window.disposeOnViewRemoval = function(id) {
        findBridgeInParentsOrChildren(id).disposeAndNotify();
    };

    This.onSendReceive = function(id, sendCallback, receiveCallback) {
        findBridgeInParentsOrChildren(id).connection.onSend(sendCallback, receiveCallback);
    };

    This.onAsynchronousReceive = function(id, receiveCallback) {
        findBridgeInParentsOrChildren(id).connection.onReceive(receiveCallback);
    };

    This.onServerError = function(id, serverErrorCallback) {
        findBridgeInParentsOrChildren(id).connection.onServerError(function(response) {
            serverErrorCallback(response.content());
        });
    };

    This.onSessionExpired = function(id, connectionLostCallback) {
        findBridgeInParentsOrChildren(id).onSessionExpired(connectionLostCallback);
    };

    This.onConnectionTrouble = function(id, connectionTroubleCallback) {
        findBridgeInParentsOrChildren(id).connection.whenTrouble(connectionTroubleCallback);
    };

    This.onConnectionLost = function(id, connectionLostCallback) {
        findBridgeInParentsOrChildren(id).connection.whenDown(connectionLostCallback);
    };
});