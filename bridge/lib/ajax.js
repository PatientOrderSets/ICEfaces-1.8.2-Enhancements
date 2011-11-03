/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * "The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is ICEfaces 1.5 open source software code, released
 * November 5, 2006. The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2006 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 * License), in which case the provisions of the LGPL License are
 * applicable instead of those above. If you wish to allow use of your
 * version of this file only under the terms of the LGPL License and not to
 * allow others to use your version of this file under the MPL, indicate
 * your decision by deleting the provisions above and replace them with
 * the notice and other provisions required by the LGPL License. If you do
 * not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the LGPL License."
 *
 */

[ Ice.Ajax = new Object ].as(function(This) {
    This.Client = Object.subclass({
        initialize: function(logger) {
            this.logger = logger;
            this.cookies = new Object;
            document.cookie.split('; ').each(function(cookieDetails) {
                var cookie = cookieDetails.split('=');
                this.cookies[cookie.first()] = cookie.last();
            }.bind(this));
            //determine which request factory should be used
            try {
                if (window.createRequest) {
                    this.createRequest = function() {
                        var request = new This.RequestProxy(window.createRequest(), this.logger);
                        //override 'post' method since ICEBrowser cannot send 'POST' requests properly.
                        request.post = function(asynchronous, path, query, requestConfigurator) {
                            this.get(asynchronous, path, query, requestConfigurator);
                        };
                        return request;
                    }.bind(this);
                } else if (window.XMLHttpRequest) {
                    this.createRequest = function() {
                        return new This.RequestProxy(new XMLHttpRequest(), this.logger);
                    }.bind(this);
                } else if (window.ActiveXObject) {
                    this.createRequest = function() {
                        return new This.RequestProxy(new ActiveXObject('Microsoft.XMLHTTP'), this.logger);
                    }.bind(this);
                }
            } catch (e) {
                this.logger.error('failed to create factory request', e);
            }
        },

        getAsynchronously: function(path, query, requestConfigurator) {
            return this.createRequest().getAsynchronously(path, query, requestConfigurator);
        },

        getSynchronously: function(path, query, requestConfigurator) {
            return this.createRequest().getSynchronously(path, query, requestConfigurator);
        },

        postAsynchronously: function(path, query, requestConfigurator) {
            return this.createRequest().postAsynchronously(path, query, requestConfigurator);
        },

        postSynchronously: function(path, query, requestConfigurator) {
            return this.createRequest().postSynchronously(path, query, requestConfigurator);
        }
    });

    This.RequestProxy = Object.subclass({
        initialize: function(request, logger) {
            this.identifier = + Math.random().toString().substr(2, 7);
            this.request = request;
            this.logger = logger;
            this.callbacks = [];
            var self = this;
            //avoid using libraries for this callback -- see: http://dev.rubyonrails.org/ticket/5393
            this.responseCallback = function() {
                if (request.readyState == 4) {
                    self.logger.debug('[' + self.identifier + '] : receive [' + self.statusCode() + '] ' + self.statusText());
                    var size = self.callbacks.length;
                    for (var i = 0; i < size; i++) {
                        try {
                            self.callbacks[i](self);
                        } catch (e) {
                            logger.warn('connection closed prematurely', e);
                            self.close();
                        }
                    }
                }
            };
        },

        statusCode: function() {
            try {
                return this.request.status;
            } catch (e) {
                return 0;
            }
        },

        statusText: function() {
            try {
                return this.request.statusText;
            } catch (e) {
                return '';
            }
        },

        on: function(test, handler) {
            this.callbacks.push(function(request) {
                if (test(request)) handler(request);
            });
        },

        isServerError: function() {
            try {
                return this.request.status == 500;
            } catch (e) {
                return false;
            }
        },

        isEmpty: function() {
            try {
                return this.request.responseText == '';
            } catch (e) {
                return true;
            }
        },

        getAsynchronously: function(path, query, requestConfigurator) {
            //the 'rand' parameter is used to force IE into creating new request object, thus avoiding potential infinite loops.
            this.request.open('GET', path + '?' + query + '&rand=' + Math.random(), true);
            if (requestConfigurator) requestConfigurator(this);
            this.request.onreadystatechange = this.responseCallback;
            this.logger.debug('[' + this.identifier + '] : send asynchronous GET');
            this.request.send('');
            return this;
        },

        postAsynchronously:  function(path, query, requestConfigurator) {
            this.request.open('POST', path, true);
            if (requestConfigurator) requestConfigurator(this);
            this.request.onreadystatechange = this.responseCallback;
            //the 'rand' parameter is used to force Firefox to commit the response to the server.
            this.logger.debug('[' + this.identifier + '] : send asynchronous POST');
            this.request.send(query + '&rand=' + Math.random() + '\n\n');
            return this;
        },

        getSynchronously: function(path, query, requestConfigurator) {
            //the 'rand' parameter is used to force IE into creating new request object, thus avoiding potential infinite loops.
            this.request.open('GET', path + '?' + query + '&rand=' + Math.random(), false);
            if (requestConfigurator) requestConfigurator(this);
            this.logger.debug('[' + this.identifier + '] : send synchronous GET');
            this.request.send('');
            this.responseCallback();
            return this;
        },

        postSynchronously:  function(path, query, requestConfigurator) {
            this.request.open('POST', path, false);
            if (requestConfigurator) requestConfigurator(this);
            //the 'rand' parameter is used to force Firefox to commit the response to the server.
            this.logger.debug('[' + this.identifier + '] : send synchronous POST');
            this.request.send(query + '&rand=' + Math.random() + '\n\n');
            this.responseCallback();
            return this;
        },

        setRequestHeader: function(name, value) {
            this.request.setRequestHeader(name, value);
        },

        getResponseHeader: function(name) {
            try {
                return this.request.getResponseHeader(name);
            } catch (e) {
                return null;
            }
        },

        containsResponseHeader: function(name) {
            try {
                var header = this.request.getResponseHeader(name);
                return header && header != '';
            } catch (e) {
                return false;
            }
        },

        content: function() {
            try {
                return this.request.responseText;
            } catch (e) {
                return '';
            }
        },

        contentAsDOM: function() {
            return this.request.responseXML;
        },

        abort: function() {
            if (this.request) {
                try {
                    //replace the callback to avoid infinit loop since the callback is
                    //executed also when the connection is aborted.
                    //also, setting 'onreadystatechange' to null will cause a memory leak in IE6
                    this.request.onreadystatechange = Function.NOOP;
                    this.request.abort();
                } catch (e) {
                    //ignore, the request was discarded by the browser
                } finally {
                    //avoid potential memory leaks since 'this.request' is a native object
                    this.request = null;
                    this.logger.debug('[' + this.identifier + '] : connection aborted');
                }
            }
        },

        close: function() {
            if (this.request) {
                try {
                    //replace the callback to avoid infinit loop since the callback is
                    //executed also when the connection is aborted.
                    //also, setting 'onreadystatechange' to null will cause a memory leak in IE6
                    this.request.onreadystatechange = Function.NOOP;
                } catch (e) {
                    //ignore, the request was discarded by the browser
                } finally {
                    //avoid potential memory leaks since 'this.request' is a native object
                    this.request = null;
                    this.logger.debug('[' + this.identifier + '] : connection closed');
                }
            }
        }
    });
});