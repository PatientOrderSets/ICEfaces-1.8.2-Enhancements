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

[ Ice.Community.Connection = new Object, Ice.Connection, Ice.Ajax, Ice.Reliability.Heartbeat, Ice.Cookie, Ice.Parameter.Query ].as(function(This, Connection, Ajax, Heartbeat, Cookie, Query) {
    This.AsyncConnection = Object.subclass({
        initialize: function(logger, sessionID, viewID, configuration, defaultQuery, commandDispatcher) {
            this.logger = logger.child('async-connection');
            this.sendChannel = new Ajax.Client(this.logger.child('ui'));
            this.receiveChannel = new Ajax.Client(this.logger.child('blocking'));
            this.defaultQuery = defaultQuery;
            this.onSendListeners = [];
            this.onReceiveFromSendListeners = [];
            this.onReceiveListeners = [];
            this.onServerErrorListeners = [];
            this.connectionDownListeners = [];
            this.connectionTroubleListeners = [];

            this.listener = { close: Function.NOOP, abort: Function.NOOP };
            this.listening = { remove: Function.NOOP };
            this.timeoutBomb = { cancel: Function.NOOP };
            this.heartbeat = { stop: Function.NOOP };

            this.pingURI = configuration.pingURI;
            this.getURI = configuration.receiveUpdatesURI;
            this.sendURI = configuration.sendReceiveUpdatesURI;
            this.receiveURI = configuration.receiveUpdatedViewsURI;

            //clear connectionDownListeners to avoid bogus connection lost messages
            window.onBeforeUnload(function() {
                this.connectionDownListeners.clear();
            }.bind(this));

            var timeout = configuration.timeout ? configuration.timeout : 60000;
            this.onSend(function() {
                this.timeoutBomb.cancel();
                this.timeoutBomb = this.connectionDownListeners.broadcaster().delayExecutionFor(timeout);
            }.bind(this));

            this.onReceive(function() {
                this.timeoutBomb.cancel();
            }.bind(this));

            this.serverErrorCallback = this.onServerErrorListeners.broadcaster();
            this.receiveCallback = function(response) {
                try {
                    this.onReceiveListeners.broadcast(response);
                } catch (e) {
                    this.logger.error('receive broadcast failed', e);
                }
            }.bind(this);
            this.sendXWindowCookie = Function.NOOP;
            this.receiveXWindowCookie = function (response) {
                var xWindowCookie = response.getResponseHeader("X-Set-Window-Cookie");
                if (xWindowCookie) {
                    this.sendXWindowCookie = function(request) {
                        request.setRequestHeader("X-Window-Cookie", xWindowCookie);
                    };
                }
            }.bind(this);

            //read/create cookie that contains the updated views
            try {
                this.updatedViews = Cookie.lookup('updates');
            } catch (e) {
                this.updatedViews = new Cookie('updates', '');
            }

            //register command that handles the updated-views message
            commandDispatcher.register('updated-views', function(message) {
                var views = this.updatedViews.loadValue().split(' ');
                var text = message.firstChild;
                if (text && !text.data.blank()) {
                    this.updatedViews.saveValue(views.concat(text.data.split(' ')).asSet().join(' '));
                } else {
                    this.logger.warn("No updated views were returned.");
                }
            }.bind(this));


            //remove the blocking connection marker so that everytime a new
            //bridge instance is created the blocking connection will
            //be re-established
            //this strategy is mainly employed to fix the window.onunload issue
            //in Opera -- see http://jira.icefaces.org/browse/ICE-1872
            try {
                this.listening = Cookie.lookup('bconn');
                this.listening.remove();
            } catch (e) {
                //do nothing
            }

            //build up retry actions
            var timedRetryAbort = function (retryAction, abortAction, timeouts) {
                var index = 0;
                var errorCallbacks = timeouts.inject([abortAction], function(callbacks, interval) {
                    callbacks.unshift(retryAction.delayFor(interval));
                    return callbacks;
                });
                return function() {
                    if (index < errorCallbacks.length) {
                        errorCallbacks[index].apply(this, arguments);
                        index++;
                    }
                };
            };

            function registeredSessions() {
                return Cookie.lookup('ice.sessions').loadValue().split(' ').collect(function(s) {
                    return s.split('#')[0];
                });
            }

            this.connect = function() {
                this.logger.debug("closing previous connection...");
                this.listener.close();
                this.logger.debug("connect...");
                var query = new Query();
                registeredSessions().each(function(sessionID) {
                    query.add('ice.session', sessionID);
                });
                this.listener = this.receiveChannel.postAsynchronously(this.receiveURI, query.asURIEncodedString(), function(request) {
                    this.sendXWindowCookie(request);
                    Connection.FormPost(request);
                    request.on(Connection.ServerError, retryOnServerError);
                    request.on(Connection.OK, this.receiveXWindowCookie);
                    request.on(Connection.OK, function(response) {
                        if (!response.isEmpty()) {
                            this.receiveCallback(response);
                        }
                        if (response.getResponseHeader('X-Connection') != 'close') {
                            this.connect();
                        } else {
                            this.heartbeat.stop();
                        }
                    }.bind(this));
                    request.on(Connection.OK, Connection.Close);
                }.bind(this));
            }.bind(this);

            //build callbacks only after this.connetion function was defined
            var retryOnServerError = timedRetryAbort(this.connect, this.serverErrorCallback, configuration.serverErrorRetryTimeouts || [1000, 2000, 4000]);

            //avoid error messages for 'pong' messages that arrive after blocking connection is closed
            commandDispatcher.register('pong', Function.NOOP);
            //heartbeat setup
            var heartbeatInterval = configuration.heartbeat.interval ? configuration.heartbeat.interval : 50000;
            var heartbeatTimeout = configuration.heartbeat.timeout ? configuration.heartbeat.timeout : 30000;
            var heartbeatRetries = configuration.heartbeat.retries ? configuration.heartbeat.retries : 3;
            var initializeConnection = function() {
                //stop the previous heartbeat instance
                this.heartbeat.stop();
                this.heartbeat = new Heartbeat(heartbeatInterval, heartbeatTimeout, this.logger);
                this.heartbeat.onPing(function(ping) {
                    //re-register a pong command on every ping
                    commandDispatcher.register('pong', function() {
                        ping.pong();
                    });
                    this.sendChannel.postAsynchronously(this.pingURI, this.defaultQuery.asURIEncodedString(), function(request) {
                        Connection.FormPost(request);
                        request.on(Connection.OK, this.receiveCallback);
                        request.on(Connection.OK, Connection.Close);
                    }.bind(this));
                }.bind(this));

                this.heartbeat.onLostPongs(this.connectionDownListeners.broadcaster(), heartbeatRetries);
                this.heartbeat.onLostPongs(this.connectionTroubleListeners.broadcaster());
                this.heartbeat.onLostPongs(function() {
                    this.logger.debug('retry to connect...');
                    this.connect();
                }.bind(this));

                this.heartbeat.start();
                this.connect();
            }.bind(this);

            //monitor if the blocking connection needs to be started
            var pollingPeriod = 1000;
            var fullViewID = sessionID + ':' + viewID;
            var leaseCookie = Cookie.lookup('ice.lease', (new Date).getTime().toString());
            var connectionCookie = this.listening = Cookie.lookup('bconn', '-');

            function updateLease() {
                leaseCookie.saveValue((new Date).getTime() + pollingPeriod * 2);
            }

            function isLeaseExpired() {
                return leaseCookie.loadValue().asNumber() < (new Date).getTime();
            }

            function shouldEstablishBlockingConnection() {
                return !Cookie.exists('bconn') || Cookie.lookup('bconn').value == '-';
            }

            function offerCandidature() {
                connectionCookie.saveValue(fullViewID);
            }

            function isWinningCandidate() {
                return connectionCookie.loadValue().startsWith(fullViewID);
            }

            function markAsOwned() {
                connectionCookie.saveValue(fullViewID + ':acquired');
            }

            function hasOwner() {
                return connectionCookie.loadValue().endsWith(':acquired');
            }

            this.blockingConnectionMonitor = function() {
                try {
                    if (shouldEstablishBlockingConnection()) {
                        offerCandidature();
                        this.logger.info('blocking connection not initialized...candidate for its creation');
                    } else {
                        if (isWinningCandidate()) {
                            if (!hasOwner()) {
                                markAsOwned();
                                //start blocking connection since no other view has started it
                                initializeConnection();
                            }
                            updateLease();
                        }
                        if (hasOwner() && isLeaseExpired()) {
                            offerCandidature();
                            this.logger.info('blocking connection lease expired...candidate for its creation');
                        }
                    }
                } catch (e) {
                    this.logger.info("could not determine the state of the blocking connection...retrying", e);
                }
            }.bind(this).repeatExecutionEvery(pollingPeriod);

            var pickUpdates = function() {
                this.sendChannel.postAsynchronously(this.getURI, this.defaultQuery.asURIEncodedString(), function(request) {
                    Connection.FormPost(request);
                    request.on(Connection.OK, this.receiveCallback);
                    request.on(Connection.OK, Connection.Close);
                }.bind(this));
            }.bind(this);

            //pick any updates that might be generated in between bridge re-initialization
            //todo: replace heuristic with more exact solution
            pickUpdates.delayExecutionFor(pollingPeriod);

            //monitor & pick updates for this view
            this.updatesMonitor = function() {
                try {
                    var views = this.updatedViews.loadValue().split(' ');
                    if (views.include(fullViewID)) {
                        pickUpdates();
                        this.updatedViews.saveValue(views.complement([ fullViewID ]).join(' '));
                    }
                } catch (e) {
                    this.logger.warn('failed to listen for updates', e);
                }
            }.bind(this).repeatExecutionEvery(300);

            this.lock = configuration.blockUI ? new Connection.Lock() : new Connection.NOOPLock();

            this.logger.info('asynchronous mode');
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
                    this.sendChannel.postAsynchronously(this.sendURI, compoundQuery.asURIEncodedString(), function(request) {
                        Connection.FormPost(request);
                        request.on(Connection.OK, this.lock.release);
                        request.on(Connection.OK, this.onReceiveFromSendListeners.broadcaster());
                        request.on(Connection.OK, this.receiveCallback);
                        request.on(Connection.ServerError, this.serverErrorCallback);
                        request.on(Connection.OK, Connection.Close);
                        this.onSendListeners.broadcast();
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

        whenTrouble: function(callback) {
            this.connectionTroubleListeners.push(callback);
        },

        shutdown: function() {
            try {
                //shutdown once
                this.shutdown = Function.NOOP;
                //avoid sending XMLHTTP requests that might create new sessions on the server
                this.send = Function.NOOP;
                this.connect = Function.NOOP;
                this.heartbeat.stop();
            } catch (e) {
                //ignore, we really need to shutdown
            } finally {
                [ this.onSendListeners, this.onReceiveListeners, this.connectionDownListeners, this.onServerErrorListeners, this.onReceiveFromSendListeners ].eachWithGuard(function(listeners) {
                    listeners.clear();
                });
                this.listener.abort();

                [ this.updatesMonitor, this.blockingConnectionMonitor ].eachWithGuard(function(monitor) {
                    monitor.cancel();
                });
                this.listening.remove();
            }
        }
    });
});

