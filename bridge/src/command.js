[ Ice.Command = new Object ].as(function(This) {

    This.Dispatcher = Object.subclass({
        initialize: function() {
            this.commands = new Object;
        },

        register: function(messageName, command) {
            this.commands[messageName] = command;
        },

        deserializeAndExecute: function(message) {
            var messageName = message.tagName;
            for (var commandName in this.commands) {
                if (commandName == messageName) {
                    this.commands[messageName](message);
                    return;
                }
            }

            throw 'Unknown message received: ' + messageName;
        }
    });

    This.SetCookie = function(message) {
        document.cookie = message.firstChild.data;
    };

    This.ParsingError = function(message) {
        logger.error('Parsing error');
        var errorNode = message.firstChild;
        logger.error(errorNode.data);
        var sourceNode = errorNode.firstChild;
        logger.error(sourceNode.data);
    };
});
