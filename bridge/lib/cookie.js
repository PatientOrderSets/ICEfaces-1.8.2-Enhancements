[ Ice ].as(function(This) {
    This.Cookie = This.Parameter.Association.subclass({
        initialize: function(name, value, path) {
            this.name = name;
            this.value = value || '';
            this.path = path || '/';
            this.save();
        },

        saveValue: function(value) {
            this.value = value;
            this.save();
        },

        loadValue: function() {
            this.load();
            return this.value;
        },

        save: function() {
            document.cookie = this.name + '=' + this.value + '; path=' + this.path;
            return this;
        },

        load: function() {
            var foundTuple = This.Cookie.parse().detect(function(tuple) {
                return this.name == tuple[0];
            }.bind(this));
            this.value = foundTuple ? foundTuple[1] : null;
            return this;
        },

        remove: function() {
            var date = new Date();
            date.setTime(date.getTime() - 24 * 60 * 60 * 1000);
            document.cookie = this.name + '=; expires=' + date.toGMTString() + '; path=' + this.path;
        }
    });

    This.Cookie.all = function() {
        return This.Cookie.parse().collect(function(tuple) {
            var name = tuple[0];
            var value = tuple[1];
            return new This.Cookie(name, value);
        });
    };

    This.Cookie.lookup = function(name, value) {
        var foundTuple = This.Cookie.parse().detect(function(tuple) {
            return name == tuple[0];
        });
        if (foundTuple) {
            return new This.Cookie(name, foundTuple[1]);
        } else {
            if (value) {
                return new This.Cookie(name, value);
            } else {
                throw 'Cannot find cookie named: ' + name;
            }
        }
    };

    This.Cookie.exists = function(name) {
        return document.cookie.contains(name + '=');
    };

    //private
    This.Cookie.parse = function() {
        return document.cookie.split('; ').collect(function(tupleDetails) {
            return tupleDetails.contains('=') ? tupleDetails.split('=') : [tupleDetails, ''];
        });
    };
});
