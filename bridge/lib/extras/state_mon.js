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

Ice.StateMon = Class.create();
Ice.StateMon = {
    logger:null,

    monitors:Array(),

    add:function(monitor) {
        this.logger.debug('Added monitor for [' + monitor.id + '] type [' + monitor.type + ']');
        this.monitors.push(monitor);
    },

    checkAll:function() {
        // Remove all elements no longer found, que found elements
        // that have new HTML elements for rebuilding
        var i = 0;
        var monitor = null;
        var size = this.monitors.length;
        for (i = 0; i < size; i++) {
            monitor = this.monitors[i];
            try {
                if (monitor.changeDetected()) {
                    this.logger.debug('Monitor [' + monitor.id + '] has been replaced');
                    monitor.rebuildMe = true;
                } else {
                    this.logger.debug('Monitor [' + monitor.id + '] has not been replaced');
                    monitor.rebuildMe = false;
                }
                if (!this.elementExists(monitor.id)) {
                    this.logger.debug('Monitor [' + monitor.id + '] no longer exists in dom');
                    monitor.destroyMe = true;
                }
            } catch(ee) {
                this.logger.error("Error checking monitor [" + monitor.id + "] Msg [" + ee + "]");
            }
        }
        this.destroy();
        // Rebuild monitors
        this.rebuild();
    },

    removeMonitors:function(monitor) {
        var nm = Array();
        var i = 0;
        for (i = 0; i < this.monitors.length; i++) {
            if (!this.monitors[i].removeMe)
                nm.push(this.monitors[i]);
        }
        this.monitors = nm;
    },

    destroy:function() {
        var i = 0;
        var monitor = null;
        // Destroy monitors that no longer have HTML elements
        var newMonitors = Array();
        for (i = 0; i < this.monitors.length; i++) {
            monitor = this.monitors[i];
            try {
                if (!monitor.destroyMe) {
                    newMonitors.push(monitor);
                } else {
                    try {
                        this.logger.debug("Destroyed monitor [" + monitor.id + "]");
                        monitor.destroy();
                    } catch(destroyException) {
                        this.logger.warn("Monitor [" + monitor.id + "] destroyed with exception [" + destroyException + "]");
                    }
                    monitor = null;
                }
            } catch(ee) {
                this.logger.error("Error destroying monitor [" + monitor.id + "] Msg [" + ee + "]");
            }
        }
        this.monitors = newMonitors;
    },

    destroyAll:function() {
        var i = 0;
        var monitor = null;
        // Destroy monitors that no longer have HTML elements
        var newMonitors = Array();
        for (i = 0; i < this.monitors.length; i++) {
            monitor = this.monitors[i];
            try {
                if (monitor != null) {
                    this.logger.debug("Destroyed monitor [" + monitor.id + "]");
                    monitor.destroy();
                }
            } catch(destroyException) {
                this.logger.warn("Monitor [" + monitor.id + "] destroyed with exception [" + destroyException + "]");
            }
            monitor = null;

        }
        this.monitors = Array();
    },

    rebuild:function() {
        var size = this.monitors.length;
        try {
            for (i = 0; i < size; i++) {
                monitor = this.monitors[i];
                if (monitor.rebuildMe) {
                    this.logger.debug('Rebuilding [' + monitor.id + ']');
                    try {
                        monitor.destroy();
                    } catch(monitorDestroyException) {
                        this.logger.warn('Could not destroy monitor before rebuilding ID[' + monitor.id + '] msg [' + monitorDestroyException + ']');
                    }
                    monitor.rebuild();
                    monitor.rebuildMe = false;
                    monitor.removeMe = true;
                } else {
                    this.logger.debug("Not rebuilding [" + monitor.id + "] type [" + monitor.type + "]");
                }
            }
            // This monitor is dead. A new one was created
            this.removeMonitors();
        } catch(ee) {
            this.logger.error("Error rebuilding monitors [" + ee + "]");
        }
    },

    elementExists:function(id) {
        var o = $(id);
        if (!o)return false;
        return true;
    },

    elementReplaced:function(ele) {
        if (ele && !ele.id) {
            // If element does not have an ID then it wont require initialization
            return false;
        }
        var currentEle = $(ele.id);
        if (!currentEle) {
            this.logger.debug('Element not found id[' + ele.id + '] element[' + ele + '] type [' + ele.nodeName + ']');
        }
        if (currentEle != null && currentEle != ele) {
            this.logger.debug("Element replaced");
            return true;
        }
    }
};

Ice.MonitorBase = Class.create();
Ice.MonitorBase.prototype = {
    object:null,
    id:null,
    htmlElement:null,
    rebuildMe:false,

    rebuild:function() {
    },

    createOptions:null,
    options:null,
    destroyMe:false,

    destroy:function() {
    },

    type:'Base',

    initialize:function() {
    },

    changeDetected:function() {
        return Ice.StateMon.elementReplaced(this.htmlElement);
    },

    removeMe:false
};

Ice.SortableMonitor = Class.create();
Ice.SortableMonitor.prototype = Object.extend(new Ice.MonitorBase(), {
    initialize:function(htmlElement, createOptions) {
        this.type = 'Sortable';
        this.object = null;
        this.id = htmlElement.id;
        this.htmlElement = htmlElement;
        this.createOptions = createOptions;
    },

    destroy:function() {
        Sortable.destroy(this.htmlElement);
    },

    rebuild:function() {
        Ice.StateMon.logger.debug('Rebuilding Sortable ID[' + this.id + '] Options[' + this.createOptions + ']');
        Sortable.create(this.id, this.createOptions);
    },

    changeDetected:function() {
        return true;
    }
});

Ice.DraggableMonitor = Class.create();
Ice.DraggableMonitor.prototype = Object.extend(new Ice.MonitorBase(), {
    initialize:function(htmlElement, createOptions) {
        this.type = 'Draggable';
        this.object = null;
        this.id = htmlElement.id;
        this.htmlElement = htmlElement;
        this.createOptions = createOptions;
    },

    destroy:function() {
        this.object.destroy();
        Ice.StateMon.logger.debug('Destroyed Draggable [' + this.id + ']');
        $A(Draggables.drags).each(function(drag) {
            Ice.StateMon.logger.debug('Draggable [' + drag.element.id + "] not destroyed");
        });
    },

    rebuild:function() {
        Ice.StateMon.logger.debug('Rebuilding Draggable ID[' + this.id + '] Options[' + this.createOptions + ']');
        var d = new Draggable(this.id, this.createOptions);
        Ice.StateMon.logger.debug('Rebuilding Draggable ID[' + this.id + '] Options[' + this.createOptions + '] Complete');
    }
});


Ice.DroppableMonitor = Class.create();
Ice.DroppableMonitor.prototype = Object.extend(new Ice.MonitorBase, {
    initialize:function(htmlElement, createOptions) {
        this.type = 'Droppable';
        this.object = null;
        this.id = htmlElement.id;
        this.htmlElement = htmlElement;
        this.createOptions = createOptions;
    },

    destroy:function() {
        Droppables.remove(this.htmlElement);
    },

    rebuild:function() {
        Ice.StateMon.logger.debug('Rebuilding Droppables ID[' + this.id + '] Options[' + this.createOptions + ']');
        Droppables.add(this.id, this.createOptions);
    }
});

Ice.AutocompleterMonitor = Class.create();
Ice.AutocompleterMonitor.prototype = Object.extend(new Ice.MonitorBase, {
    initialize:function(htmlElement, update, createOptions, rowClass, selectedRowClass) {
        this.type = 'Autocompleter';
        this.object = null;
        this.id = htmlElement.id;
        this.htmlElement = htmlElement;
        this.createOptions = createOptions;
        this.update = update;
        this.rowClass = rowClass;
        this.selectedRowClass = selectedRowClass;
    },

    destroy:function() {
        this.object.dispose();
    },

    rebuild:function() {
        Ice.StateMon.logger.debug('Rebuilding Autocompleter ID[' + this.id + '] Options[' + this.createOptions + ']');
        return new Ice.Autocompleter(this.id, this.update.id, this.createOptions, this.rowClass, this.selectedRowClass);
    }
});
