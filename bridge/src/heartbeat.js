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

[ Ice.Reliability = new Object ].as(function(This) {
    This.Heartbeat = Object.subclass({
        initialize: function(period, timeout, logger) {
            this.period = period;
            this.logger = logger.child('heartbeat');
            this.pingListeners = [];
            this.lostPongListeners = [];

            this.beat = function() {
                var timeoutBomb = function() {
                    this.logger.warn('pong lost');
                    this.lostPongListeners.each(function(listener) {
                        listener.notify();
                    });
                }.bind(this).delayExecutionFor(timeout);

                this.pingListeners.broadcast(new This.Ping(timeoutBomb, this, this.logger));
            }.bind(this);


            window.onKeyPress(function(e) {
                if (e.keyCode() == 46 && e.isCtrlPressed() && e.isShiftPressed()) {
                    this.beatPID ? this.stop() : this.start();
                }
            }.bind(this));
        },

        start: function() {
            this.beatPID = this.beat.repeatExecutionEvery(this.period);
            this.logger.info('heartbeat started');
            return this;
        },

        stop: function() {
            try {
                this.beatPID.cancel();
                this.beatPID = null;
                this.pingListeners.clear();
                this.lostPongListeners.each(function(listener) {
                    listener.ignoreNotifications();
                });
                this.logger.info('heartbeat stopped');
            } catch (e) {
                this.logger.warn('heartbeat not started', e);
            }
            return this;
        },

        reset: function() {
            this.lostPongListeners.each(function(listener) {
                listener.reset();
            });
        },

        onPing: function(callback) {
            this.pingListeners.push(callback);
        },

        onLostPongs: function(callback, lostPongs) {
            var retries = lostPongs || 1;
            this.lostPongListeners.push(new This.CoalescingListener(retries, callback));
        }
    });

    This.Ping = Object.subclass({
        initialize: function(pid, heartbeat, logger) {
            this.pid = pid;
            this.heartbeat = heartbeat;
            this.logger = logger;
            this.logger.info('ping');
        },

        pong: function() {
            if (this.pid) {
                this.heartbeat.reset();
                this.pid.cancel();
                this.pong = Function.NOOP;
                this.logger.info('pong');
            }
        }
    });

    This.CoalescingListener = Object.subclass({
        initialize: function(retries, callback) {
            this.count = 0;
            this.retries = retries;
            this.callback = callback;
        },

        notify: function() {
            this.count += 1;
            if (this.count == this.retries) {
                this.callback();
                this.reset();
            }
        },

        ignoreNotifications: function() {
            this.notify = Function.NOOP;
        },

        reset: function() {
            this.count = 0;
        }
    });
});