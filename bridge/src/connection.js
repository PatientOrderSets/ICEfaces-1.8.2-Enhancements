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

[ Ice.Connection = new Object, Ice.Connection, Ice.Ajax, Ice.Parameter.Query ].as(function(This, Connection, Ajax, Query) {
    This.FormPost = function(request) {
        request.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8');
    };

    This.Close = function(response) {
        response.close();
    };

    This.BadResponse = function(response) {
        return response.statusCode() == 0;
    };

    This.ServerError = function(response) {
        var code = response.statusCode();
        return code >= 500 && code < 600;
    };

    This.OK = function(response) {
        return response.statusCode() == 200;
    };

    This.Lock = Object.subclass({
        initialize: function() {
            var locked = false;
            this.acquire = function() {
                locked = true;
            }.bind(this);

            this.release = function() {
                locked = false;
            }.bind(this);

            this.isReleased = function() {
                return !locked;
            }.bind(this);
        }
    });

    This.NOOPLock = Object.subclass({
        initialize: function() {
            this.acquire = Function.NOOP;
            this.release = Function.NOOP;
            this.isReleased = function() {
                return true;
            };
        }
    });

    This.SyncConnection = Object.subclass({
        initialize: function(logger, configuration, defaultQuery, sessionID) {
            this.logger = logger.child('sync-connection');
            this.channel = new Ajax.Client(this.logger);
            this.defaultQuery = defaultQuery;
            this.onSendListeners = [];
            this.onReceiveFromSendListeners = [];
            this.onReceiveListeners = [];
            this.onServerErrorListeners = [];
            this.connectionDownListeners = [];
            this.timeoutBomb = { cancel: Function.NOOP };
            this.logger.info('synchronous mode');
            this.sendURI = configuration.sendReceiveUpdatesURI;
            var timeout = configuration.timeout ? configuration.timeout : 60000;

            //clear connectionDownListeners to avoid bogus connection lost messages
            window.onBeforeUnload(function() {
                this.connectionDownListeners.clear();
            }.bind(this));

            this.onSend(function() {
                this.timeoutBomb.cancel();
                this.timeoutBomb = this.connectionDownListeners.broadcaster().delayExecutionFor(timeout);
            }.bind(this));

            this.onReceive(function() {
                this.timeoutBomb.cancel();
            }.bind(this));

            this.whenDown(function() {
                this.timeoutBomb.cancel();
            }.bind(this));

            this.receiveCallback = function(response) {
                try {
                    this.onReceiveListeners.broadcast(response);
                } catch (e) {
                    this.logger.error('receive broadcast failed', e);
                }
            }.bind(this);

            this.badResponseCallback = this.connectionDownListeners.broadcaster();
            this.serverErrorCallback = this.onServerErrorListeners.broadcaster();

            this.lock = configuration.blockUI ? new Connection.Lock() : new Connection.NOOPLock();
        },

        send: function(query) {
            if (this.lock.isReleased()) {
                this.lock.acquire();
                try {
                    var compoundQuery = new Query();
                    compoundQuery.addQuery(query);
                    compoundQuery.addQuery(this.defaultQuery);
                    compoundQuery.add('ice.focus', window.currentFocus);

                    this.logger.debug('send > ' + compoundQuery.asString());
                    this.channel.postAsynchronously(this.sendURI, compoundQuery.asURIEncodedString(), function(request) {
                        This.FormPost(request);
                        request.on(Connection.OK, this.lock.release);
                        request.on(Connection.OK, this.onReceiveFromSendListeners.broadcaster());
                        request.on(Connection.OK, this.receiveCallback);
                        request.on(Connection.BadResponse, this.badResponseCallback);
                        request.on(Connection.ServerError, this.serverErrorCallback);
                        request.on(Connection.OK, Connection.Close);
                        this.onSendListeners.broadcast(request);
                    }.bind(this));
                } catch (e) {
                    this.lock.release();
                }
            }
        },

        onSend: function(sendCallback, receiveCallback) {
            this.onSendListeners.push(sendCallback);
            if (receiveCallback) this.onReceiveFromSendListeners.push(receiveCallback);
        },

        onReceive: function(callback) {
            this.onReceiveListeners.push(callback);
        },

        onServerError: function(callback) {
            this.onServerErrorListeners.push(callback);
        },

        whenDown: function(callback) {
            this.connectionDownListeners.push(callback);
        },

        whenTrouble: Function.NOOP,

        shutdown: function() {
            //shutdown once
            this.shutdown = Function.NOOP;
            //avoid sending XMLHTTP requests that might create new sessions on the server
            this.send = Function.NOOP;
            [ this.onSendListeners, this.onReceiveListeners, this.onServerErrorListeners, this.connectionDownListeners, this.onReceiveFromSendListeners ].eachWithGuard(function(listeners) {
                listeners.clear();
            });
        }
    });
});

