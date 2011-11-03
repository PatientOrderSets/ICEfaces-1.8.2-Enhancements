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

[ Ice.Status = new Object ].as(function(This) {
    This.NOOPIndicator = {
        on: Function.NOOP,
        off: Function.NOOP
    };

    This.RedirectIndicator = Object.subclass({
        initialize: function(uri) {
            this.uri = uri;
        },

        on: function() {
            window.location.href = this.uri;
        },

        off: Function.NOOP
    });

    This.ElementIndicator = Object.subclass({
        initialize: function(elementID, indicators) {
            this.elementID = elementID;
            this.indicators = indicators;
            this.indicators.push(this);
            this.off();
        },

        on: function() {
            this.indicators.each(function(indicator) {
                if (indicator != this) indicator.off();
            }.bind(this));
            var e = this.elementID.asElement();
            if (e) {
                e.style.visibility = 'visible';
            }
        },

        off: function() {
            var e = this.elementID.asElement();
            if (e) {
                e.style.visibility = 'hidden';
            }
        }
    });

    This.OverlappingStateProtector = Object.subclass({
        initialize: function(indicator) {
            this.indicator = indicator;
            this.counter = 0;
        },

        on: function() {
            if (this.counter == 0) this.indicator.on();
            ++this.counter;
        },

        off: function() {
            if (this.counter < 1) return;
            if (this.counter == 1) this.indicator.off();
            --this.counter;
        }
    });

    This.ToggleIndicator = Object.subclass({
        initialize: function(onElement, offElement) {
            this.onElement = onElement;
            this.offElement = offElement;
            this.off();
        },

        on: function() {
            this.onElement.on();
            this.offElement.off();
        },

        off: function() {
            this.onElement.off();
            this.offElement.on();
        }
    });

    This.MuxIndicator = Object.subclass({
        initialize: function() {
            this.indicators = arguments;
            this.off();
        },

        on: function() {
            $enumerate(this.indicators).each(function(i) {
                i.on();
            });
        },

        off: function() {
            $enumerate(this.indicators).each(function(i) {
                i.off();
            });
        }
    });

    This.PointerIndicator = Object.subclass({
        initialize: function(element) {
            this.toggle = function() {
                //block any other action from triggering the indicator before being in 'off' state again
                this.on = Function.NOOP;
                //prepare cursor shape rollback 
                var cursorRollbacks = ['input', 'select', 'textarea', 'button', 'a'].inject([ element ], function(result, type) {
                    return result.concat($enumerate(element.getElementsByTagName(type)).toArray());
                }).collect(function(e) {
                    var c = e.style.cursor;
                    e.style.cursor = 'wait';
                    return function() {
                        e.style.cursor = c;
                    };
                });

                this.off = function() {
                    cursorRollbacks.broadcast();

                    this.on = this.toggle;
                    this.off = Function.NOOP;
                };
            };

            this.on = /Safari/.test(navigator.userAgent) ? Function.NOOP : this.toggle;
            this.off = Function.NOOP;
        }
    });

    This.OverlayIndicator = Object.subclass({
        initialize: function(configuration) {
            this.configuration = configuration;
        },

        on: function() {
            if (/MSIE/.test(navigator.userAgent)) {
                this.overlay = document.createElement('iframe');
                this.overlay.setAttribute('src', this.configuration.connection.context + "xmlhttp/wait-cursor");
                this.overlay.setAttribute('frameborder', '0');
                document.body.appendChild(this.overlay);
            } else {
                this.overlay = document.body.appendChild(document.createElement('div'));
                this.overlay.style.cursor = 'wait';
            }

            var overlayStyle = this.overlay.style;
            overlayStyle.position = 'absolute';
            overlayStyle.backgroundColor = 'white';
            overlayStyle.zIndex = '38000';
            overlayStyle.top = '0';
            overlayStyle.left = '0';
            overlayStyle.opacity = '0';
            overlayStyle.filter = 'alpha(opacity=0)';
            overlayStyle.width = (Math.max(document.documentElement.scrollWidth, document.body.scrollWidth) - 20) + 'px';
            overlayStyle.height = (Math.max(document.documentElement.scrollHeight, document.body.scrollHeight) - 20) + 'px';
        },

        off: function() {
            if (this.overlay) {
                if (/MSIE/.test(navigator.userAgent)) {
                    var overlay = document.createElement('iframe');
                    overlay.setAttribute('src', this.configuration.connection.context + "xmlhttp/blank");
                    overlay.setAttribute('frameborder', '0');
                    document.body.replaceChild(overlay, this.overlay);
                    document.body.removeChild(overlay);
                } else {
                    document.body.removeChild(this.overlay);
                }
            }
        }
    });

    This.PopupIndicator = Object.subclass({
        initialize: function(message, description, buttonText, iconPath, panel) {
            this.message = message;
            this.description = description;
            this.buttonText = buttonText;
            this.iconPath = iconPath;
            this.panel = panel;
        },

        on: function() {
            this.panel.on();
            var messageContainer = document.createElement('div');
            document.body.appendChild(messageContainer);
            var messageContainerStyle = messageContainer.style;
            messageContainerStyle.position = 'absolute';
            messageContainerStyle.textAlign = 'center';
            messageContainerStyle.zIndex = '28001';
            messageContainerStyle.color = 'black';
            messageContainerStyle.backgroundColor = 'white';
            messageContainerStyle.paddingLeft = '0';
            messageContainerStyle.paddingRight = '0';
            messageContainerStyle.paddingTop = '15px';
            messageContainerStyle.paddingBottom = '15px';
            messageContainerStyle.borderBottomColor = 'gray';
            messageContainerStyle.borderRightColor = 'gray';
            messageContainerStyle.borderTopColor = 'silver';
            messageContainerStyle.borderLeftColor = 'silver';
            messageContainerStyle.borderWidth = '2px';
            messageContainerStyle.borderStyle = 'solid';
            messageContainerStyle.width = '270px';

            var messageElement = document.createElement('div');
            messageElement.appendChild(document.createTextNode(this.message));
            var messageElementStyle = messageElement.style;
            messageElementStyle.marginLeft = '30px';
            messageElementStyle.textAlign = 'left';
            messageElementStyle.fontSize = '14px';
            messageElementStyle.fontSize = '14px';
            messageElementStyle.fontWeight = 'bold';
            messageContainer.appendChild(messageElement);

            var descriptionElement = document.createElement('div');
            descriptionElement.appendChild(document.createTextNode(this.description));
            var descriptionElementStyle = descriptionElement.style;
            descriptionElementStyle.fontSize = '11px';
            descriptionElementStyle.marginTop = '7px';
            descriptionElementStyle.marginBottom = '7px';
            descriptionElementStyle.fontWeight = 'normal';
            messageElement.appendChild(descriptionElement);

            var buttonElement = document.createElement('input');
            buttonElement.type = 'button';
            buttonElement.value = this.buttonText;
            var buttonElementStyle = buttonElement.style;
            buttonElementStyle.fontSize = '11px';
            buttonElementStyle.fontWeight = 'normal';
            buttonElement.onclick = function() {
                window.location.reload();
            };
            messageContainer.appendChild(buttonElement);
            var resize = function() {
                messageContainerStyle.left = ((window.width() - messageContainer.clientWidth) / 2) + 'px';
                messageContainerStyle.top = ((window.height() - messageContainer.clientHeight) / 2) + 'px';
            }.bind(this);
            resize();
            window.onResize(resize);
        },

        off: Function.NOOP
    });

    This.DefaultStatusManager = Object.subclass({
        initialize: function(configuration, container) {
            this.configuration = configuration;
            this.container = container;
            this.connectionLostRedirect = configuration.connectionLostRedirectURI ? new This.RedirectIndicator(configuration.connectionLostRedirectURI) : null;
            this.sessionExpiredRedirect = configuration.sessionExpiredRedirectURI ? new This.RedirectIndicator(configuration.sessionExpiredRedirectURI) : null;
            var messages = configuration.messages;
            var sessionExpiredIcon = configuration.connection.context + '/xmlhttp/css/xp/css-images/connect_disconnected.gif';
            var connectionLostIcon = configuration.connection.context + '/xmlhttp/css/xp/css-images/connect_caution.gif';

            var pointerIndicator = new This.PointerIndicator(container);
            this.busy = new This.OverlappingStateProtector(configuration.blockUI ? new This.MuxIndicator(pointerIndicator, new This.OverlayIndicator(configuration)) : pointerIndicator);
            this.sessionExpired = this.sessionExpiredRedirect ? this.sessionExpiredRedirect : new This.PopupIndicator(messages.sessionExpired, messages.description, messages.buttonText, sessionExpiredIcon, this);
            this.connectionLost = this.connectionLostRedirect ? this.connectionLostRedirect : new This.PopupIndicator(messages.connectionLost, messages.description, messages.buttonText, connectionLostIcon, this);
            this.serverError = new This.PopupIndicator(messages.serverError, messages.description, messages.buttonText, connectionLostIcon, this);
            this.connectionTrouble = { on: Function.NOOP, off: Function.NOOP };
        },

        on: function() {
            var overlay = this.container.ownerDocument.createElement('iframe');
            overlay.setAttribute('src', this.configuration.connection.context + "xmlhttp/blank");
            overlay.setAttribute('frameborder', '0');
            var overlayStyle = overlay.style;
            overlayStyle.position = 'absolute';
            overlayStyle.display = 'block';
            overlayStyle.visibility = 'visible';
            overlayStyle.backgroundColor = 'white';
            overlayStyle.zIndex = '28000';
            overlayStyle.top = '0';
            overlayStyle.left = '0';
            overlayStyle.opacity = 0.22;
            overlayStyle.filter = 'alpha(opacity=22)';
            this.container.appendChild(overlay);

            var resize = this.container.tagName.toLowerCase() == 'body' ?
                         function() {
                             overlayStyle.width = Math.max(document.documentElement.scrollWidth, document.body.scrollWidth) + 'px';
                             overlayStyle.height = Math.max(document.documentElement.scrollHeight, document.body.scrollHeight) + 'px';
                         } :
                         function() {
                             overlayStyle.width = this.container.offsetWidth + 'px';
                             overlayStyle.height = this.container.offsetHeight + 'px';
                         };
            resize();
            window.onResize(resize);
        },

        off: Function.NOOP
    });

    This.ComponentStatusManager = Object.subclass({
        initialize: function(workingID, idleID, troubleID, lostID, defaultStatusManager, showPopups, displayHourglassWhenActive) {
            var indicators = [];
            var connectionWorking = new Ice.Status.ElementIndicator(workingID, indicators);
            var connectionIdle = new Ice.Status.ElementIndicator(idleID, indicators);
            var connectionLost = new Ice.Status.ElementIndicator(lostID, indicators);
            var busyElementIndicator = new Ice.Status.ToggleIndicator(connectionWorking, connectionIdle);
            var busyIndicator = defaultStatusManager.configuration.blockUI ?
                                new Ice.Status.MuxIndicator(busyElementIndicator, new Ice.Status.OverlayIndicator(defaultStatusManager.configuration)) : busyElementIndicator;

            this.busy = new Ice.Status.OverlappingStateProtector(displayHourglassWhenActive ? new Ice.Status.MuxIndicator(defaultStatusManager.busy, busyIndicator) : busyIndicator);
            this.connectionTrouble = new Ice.Status.ElementIndicator(troubleID, indicators);
            if (showPopups) {
                this.dsm = defaultStatusManager;
                this.connectionLost = new Ice.Status.MuxIndicator(connectionLost, this.dsm.connectionLost);
                this.sessionExpired = new Ice.Status.MuxIndicator(connectionLost, this.dsm.sessionExpired);
                this.serverError = new Ice.Status.MuxIndicator(connectionLost, this.dsm.serverError);
            } else {
                this.connectionLost = defaultStatusManager.connectionLostRedirect ? defaultStatusManager.connectionLostRedirect : connectionLost;
                this.sessionExpired = defaultStatusManager.sessionExpiredRedirect ? defaultStatusManager.sessionExpiredRedirect : connectionLost;
                this.serverError = connectionLost;
            }
        },

        on: function() {
            this.dsm.on();
        },

        off: function() {
            [this.busy, this.sessionExpired, this.serverError, this.connectionLost, this.connectionTrouble].eachWithGuard(function(indicator) {
                indicator.off();
            });
        }
    });
});
