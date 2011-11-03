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

[ Ice.Log = new Object ].as(function(This) {
    This.Priority = Object.subclass({
        debug: function(handler, category, message, exception) {
            handler.debug(category, message, exception);
        },

        info: function(handler, category, message, exception) {
            handler.info(category, message, exception);
        },

        warn: function(handler, category, message, exception) {
            handler.warn(category, message, exception);
        },

        error: function(handler, category, message, exception) {
            handler.error(category, message, exception);
        }
    });

    This.Debug = This.Priority.subclass({
        asString: function() {
            return 'Debug';
        }
    });

    This.Info = This.Debug.subclass({
        debug: Function.NOOP,

        asString: function() {
            return 'Info';
        }
    });

    This.Warn = This.Info.subclass({
        info: Function.NOOP,

        asString: function() {
            return 'Warn';
        }
    });

    This.Error = This.Warn.subclass({
        warn: Function.NOOP,

        asString: function() {
            return 'Error';
        }
    });

    This.Priority.DEBUG = new This.Debug;
    This.Priority.INFO = new This.Info;
    This.Priority.WARN = new This.Warn;
    This.Priority.ERROR = new This.Error;
    This.Priority.Levels = [ This.Priority.DEBUG, This.Priority.INFO, This.Priority.WARN, This.Priority.ERROR ];

    This.Logger = Object.subclass({
        initialize: function(category, handler, priority) {
            this.handler = handler || { debug: Function.NOOP, info: Function.NOOP, warn: Function.NOOP, error: Function.NOOP };
            this.category = category;
            this.children = [];
            this.priority = priority || This.Priority.ERROR;
        },

        debug: function(message, exception) {
            this.priority.debug(this.handler, this.category, message, exception);
        },

        info: function(message, exception) {
            this.priority.info(this.handler, this.category, message, exception);
        },

        warn: function(message, exception) {
            this.priority.warn(this.handler, this.category, message, exception);
        },

        error: function(message, exception) {
            this.priority.error(this.handler, this.category, message, exception);
        },

        child: function(category) {
            var childCategory = this.category.copy();
            childCategory.push(category);
            var child = new This.Logger(childCategory, this.handler, this.priority);
            this.children.push(child);
            return child;
        },

        threshold: function(thresholdPriority) {
            this.priority = thresholdPriority;
            this.children.each(function(logger) {
                logger.threshold(thresholdPriority);
            });
        },

        handleWith: function(handler) {
            this.handler = handler;
        }
    });

    This.WindowLogHandler = Object.subclass({
        initialize: function(logger, parentWindow, lines, thresholdPriority) {
            this.lineOptions = [ 25, 50, 100, 200, 400 ];
            this.logger = logger;
            this.logger.handleWith(this);
            this.parentWindow = parentWindow;
            this.lines = lines || this.lineOptions[3];
            this.thresholdPriority = thresholdPriority || This.Priority.DEBUG;
            this.categoryMatcher = /.*/;
            this.closeOnExit = true;
            this.toggle();

            this.parentWindow.onKeyUp(function(e) {
                var key = e.keyCode();
                if ((key == 20 || key == 84) && (e.isCtrlPressed() || e.isAltPressed()) && e.isShiftPressed()) {
                    this.enable();
                }
            }.bind(this));
            this.parentWindow.onUnload(function() {
                window.logger.info('page unloaded!');
                this.disable();
            }.bind(this));
        },

        enable: function() {
            try {
                this.window = this.parentWindow.open('', 'log' + window.identifier, 'scrollbars=1,width=800,height=680');
                var windowDocument = this.window.document;

                this.log = this.window.document.getElementById('log-window');
                this.toggle();
                if (this.log) return;

                windowDocument.body.appendChild(windowDocument.createTextNode(' Close on exit '));
                var closeOnExitCheckbox = windowDocument.createElement('input');
                closeOnExitCheckbox.style.margin = '2px';
                closeOnExitCheckbox.setAttribute('type', 'checkbox');
                closeOnExitCheckbox.defaultChecked = true;
                closeOnExitCheckbox.checked = true;
                closeOnExitCheckbox.onclick = function() {
                    this.closeOnExit = closeOnExitCheckbox.checked;
                }.bind(this);
                windowDocument.body.appendChild(closeOnExitCheckbox);

                windowDocument.body.appendChild(windowDocument.createTextNode(' Lines '));
                var lineCountDropDown = windowDocument.createElement('select');
                lineCountDropDown.style.margin = '2px';
                this.lineOptions.each(function(count, index) {
                    var option = lineCountDropDown.appendChild(windowDocument.createElement('option'));
                    if (this.lines == count) lineCountDropDown.selectedIndex = index;
                    option.appendChild(windowDocument.createTextNode(count.toString()));
                }.bind(this));

                lineCountDropDown.onchange = function(event) {
                    this.lines = this.lineOptions[lineCountDropDown.selectedIndex];
                    this.clearPreviousEvents();
                }.bind(this);
                windowDocument.body.appendChild(lineCountDropDown);

                windowDocument.body.appendChild(windowDocument.createTextNode(' Category '));
                var categoryInputText = windowDocument.createElement('input');
                categoryInputText.style.margin = '2px';
                categoryInputText.setAttribute('type', 'text');
                categoryInputText.setAttribute('value', this.categoryMatcher.source);
                categoryInputText.onchange = function() {
                    this.categoryMatcher = categoryInputText.value.asRegexp();
                }.bind(this);
                windowDocument.body.appendChild(categoryInputText);


                windowDocument.body.appendChild(windowDocument.createTextNode(' Level '));
                var levelDropDown = windowDocument.createElement('select');
                levelDropDown.style.margin = '2px';
                This.Priority.Levels.each(function(priority, index) {
                    var option = levelDropDown.appendChild(windowDocument.createElement('option'));
                    if (this.thresholdPriority == priority) levelDropDown.selectedIndex = index;
                    option.appendChild(windowDocument.createTextNode(priority.asString()));
                }.bind(this));

                this.logger.threshold(this.thresholdPriority);
                levelDropDown.onchange = function(event) {
                    this.thresholdPriority = This.Priority.Levels[levelDropDown.selectedIndex];
                    this.logger.threshold(this.thresholdPriority);
                }.bind(this);
                windowDocument.body.appendChild(levelDropDown);

                var startStopButton = windowDocument.createElement('input');
                startStopButton.style.margin = '2px';
                startStopButton.setAttribute('type', 'button');
                startStopButton.setAttribute('value', 'Stop');
                startStopButton.onclick = function() {
                    startStopButton.setAttribute('value', this.toggle() ? 'Stop' : 'Start');
                }.bind(this);
                windowDocument.body.appendChild(startStopButton);

                var clearButton = windowDocument.createElement('input');
                clearButton.style.margin = '2px';
                clearButton.setAttribute('type', 'button');
                clearButton.setAttribute('value', 'Clear');
                clearButton.onclick = function() {
                    this.clearAllEvents();
                }.bind(this);
                windowDocument.body.appendChild(clearButton);

                this.log = windowDocument.body.appendChild(windowDocument.createElement('pre'));
                this.log.id = 'log-window';
                this.log.style.width = '100%';
                this.log.style.minHeight = '0';
                this.log.style.maxHeight = '550px';
                this.log.style.borderWidth = '1px';
                this.log.style.borderStyle = 'solid';
                this.log.style.borderColor = '#999';
                this.log.style.backgroundColor = '#ddd';
                this.log.style.overflow = 'scroll';

                this.window.onunload = function() {
                    this.disable();
                }.bind(this);
            } catch (e) {
                this.disable();
            }
        },

        disable: function() {
            this.logger.threshold(This.Priority.ERROR);
            this.handle = Function.NOOP;
            if (this.closeOnExit && this.window) this.window.close();
        },

        toggle: function() {
            if (this.handle == Function.NOOP) {
                delete this.handle;
                return true;
            } else {
                this.handle = Function.NOOP;
                return false;
            }
        },

        debug: function(category, message, exception) {
            this.handle('#333', 'debug', category, message, exception)
        },

        info: function(category, message, exception) {
            this.handle('green', 'info', category, message, exception)
        },

        warn: function(category, message, exception) {
            this.handle('orange', 'warn', category, message, exception)
        },

        error: function(category, message, exception) {
            this.handle('red', 'error', category, message, exception)
        },

        //private
        handle: function(colorName, priorityName, category, message, exception) {
            try {
                if (this.categoryMatcher.test(category.join('.'))) {
                    var elementDocument = this.log.ownerDocument;
                    var timestamp = (new Date()).toTimestamp();
                    var categoryName = category.join('.');
                    ('[' + categoryName + '] : ' + message +
                     (exception ? ('\n' + exception) : '')).split('\n').each(function(line) {
                        if (line.containsWords()) {
                            var eventNode = elementDocument.createElement('div');
                            eventNode.style.padding = '3px';
                            eventNode.style.color = colorName;
                            eventNode.setAttribute("title", timestamp + ' | ' + priorityName)
                            this.log.appendChild(eventNode).appendChild(elementDocument.createTextNode(line));
                        }
                    }.bind(this));
                    this.log.scrollTop = this.log.scrollHeight;
                }
                this.clearPreviousEvents();
            } catch (e) {
                this.disable();
            }
        },

        //private
        clearPreviousEvents: function() {
            var nodes = $A(this.log.childNodes);
            nodes.copyFrom(0, nodes.length - this.lines).each(function(node) {
                this.log.removeChild(node)
            }.bind(this));
        },

        //private
        clearAllEvents: function() {
            $A(this.log.childNodes).each(function(node) {
                this.log.removeChild(node)
            }.bind(this));
        }
    });

    This.NOOPConsole = {
        debug: Function.NOOP, info: Function.NOOP, warn: Function.NOOP, error: Function.NOOP
    };

    This.FirebugLogHandler = Object.subclass({
        initialize: function(logger) {
            logger.handleWith(this);
            this.logger = logger;
            this.console = This.NOOPConsole;
            this.enable();
        },

        enable: function() {
            this.console = window.console;
            this.logger.threshold(This.Priority.DEBUG);
            this.logger.warn('Firebug (version < 1.2) logging can cause increased memory consumption when running for a long period of time!');
        },

        disable: function() {
            this.console = This.NOOPConsole;
            this.logger.threshold(This.Priority.ERROR);
        },

        toggle: Function.NOOP,

        debug: function(category, message, exception) {
            exception ? this.console.debug(this.format(category, message), exception) : this.console.debug(this.format(category, message));
        },

        info: function(category, message, exception) {
            exception ? this.console.info(this.format(category, message), exception) : this.console.info(this.format(category, message));
        },

        warn: function(category, message, exception) {
            exception ? this.console.warn(this.format(category, message), exception) : this.console.warn(this.format(category, message));
        },

        error: function(category, message, exception) {
            exception ? this.console.error(this.format(category, message), exception) : this.console.error(this.format(category, message));
        },

        //private
        format: function(category, message) {
            return '[' + category.join('.') + '] ' + message;
        }
    });
});



