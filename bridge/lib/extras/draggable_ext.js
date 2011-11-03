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
// Original license and copyright:
// Copyright (c) 2005 Thomas Fuchs (http://script.aculo.us, http://mir.aculo.us)
//           (c) 2005 Sammi Williams (http://www.oriontransfer.co.nz, sammi@oriontransfer.co.nz)
// 
// See scriptaculous.js for full license.

Draggable.prototype.dragGhost = false;
Draggable.prototype.ORIGINAL_initialize = Draggable.prototype.initialize;
Draggable.prototype.initialize = function(element) {

    var monitors = Ice.StateMon.monitors;
    for (i = 0; i < monitors.length; i++) {
        monitor = monitors[i];
        if (monitor.id == element && monitor.type == 'Draggable') {
        Ice.DnD.logger.debug("Draggable [" + $(element).id + "] has already been created");
            return;
        }
    }
    this.element = $(element);
    var ops = arguments[1];
    if (ops.dragGhost == true)
        this.dragGhost = true;
    if (!ops.starteffect) ops.starteffect = function() {
    };
    if (!ops.endeffect) ops.endeffect = function() {
    };
    if (ops.handle) {
        ops.handle = $(ops.handle);
        //Might not have the element the first time. When rebuilding we could have a ref to a non existing element
        ops.handle = $(ops.handle.id);
    }
    this.ORIGINAL_initialize(this.element, ops);
    if (!ops.sort) {
        Ice.DnD.logger.debug("Draggable Created ID[" + this.element.id + "]");
        var monitor = new Ice.DraggableMonitor(this.element, ops);
        monitor.object = this;
        Ice.StateMon.add(monitor);
    }

    Ice.DnD.logger.debug("Draggable [" + this.element.id + "] created");
};

Draggable.prototype.ORIGINAL_startDrag = Draggable.prototype.startDrag;
Draggable.prototype.startDrag = function(event) {
    this.dragging = true;
    if (this.dragGhost == true) {
        Ice.DnD.logger.debug('Init Drag Ghost ID[' + this.element.id + ']');
        Draggables.register(this);
        try {
            this._ghost = this.element.cloneNode(true);
            var form = Ice.util.findForm(this.element);
            //According to the tld all Draggable panels must be contained in a form
            form.appendChild(this._ghost);
            Position.absolutize(this._ghost);
            Element.makePositioned(this._ghost);
            this._original = this.element;
            Position.clone(this._original, this._ghost);
            var z = parseInt(this._original.style.zIndex);
            this._ghost.style.left =  Event.pointerX(event) + "px";
            this._ghost.style.zIndex = ++z;
            this.element = this._ghost;
            this.eventResize = this.resize.bindAsEventListener(this);
            Event.observe(window, "resize", this.eventResize);
        } catch(ee) {
            Ice.DnD.logger.error('Error init DragGhost  ID[' + this.element.id + ']', ee);
        }
    }
    if (this.options.dragCursor) {
        this._cursor = this.element.cloneNode(true);
        document.body.appendChild(this._cursor);
        Position.absolutize(this._cursor);
        var z = 1 + this.element.style.zIndex;
        this._cursor.style.zIndex = z;
        Ice.DnD.logger.debug('clone created');
    }
    this.ORIGINAL_startDrag(event);
};

Draggable.prototype.ORIGINAL_draw = Draggable.prototype.draw;
Draggable.prototype.draw = function(point) {
    if (!this.options.dragCursor) {
        return this.ORIGINAL_draw(point);
    }
    var pos = Position.cumulativeOffset(this.element);
    var d = this.currentDelta();
    pos[0] -= d[0];
    pos[1] -= d[1];
    var p = point;

    if (this.options.snap) {
        if (typeof this.options.snap == 'function') {
            p = this.options.snap(p[0], p[1]);
        } else {
            if (this.options.snap instanceof Array) {
                p = p.map(function(v, i) {
                    return Math.round(v / this.options.snap[i]) * this.options.snap[i]
                }.bind(this))
            } else {
                p = p.map(function(v) {
                    return Math.round(v / this.options.snap) * this.options.snap
                }.bind(this))
            }
        }
    }

    var style = this._cursor.style;
    if ((!this.options.constraint) || (this.options.constraint == 'horizontal'))
        style.left = p[0] + "px";
    if ((!this.options.constraint) || (this.options.constraint == 'vertical'))
        style.top = p[1] + "px";
    if (style.visibility == "hidden") style.visibility = ""; // fix gecko rendering
};

Draggable.prototype.resize = function(event) {
};

Draggable.removeMe = function(element) {
    $(element).undoPositioned();
    var monitors = Ice.StateMon.monitors;
    var newMonitors = Array();
    for (i = 0; i < monitors.length; i++) {
        monitor = monitors[i];
        try {
            // remove only draggable monitors
	        if (monitor.id == element && monitor.type == 'Draggable') {
	           // don't remove while dragging
	            if (monitor.object.dragging) {
	                return;
	            }
	            try {
                    //this could be true for the panelPopup only.
                    //if the clientOnly is true on the panelPopup, it won't cause 
                    //any kind of submit on drag.
                    //so we don't want to destroy the draggable if clientOnly is true.
                    var clientOnly = $(element+"clientOnly");
                    if (!clientOnly) {
                       monitor.destroyMe = true;
                       monitor.destroy();
                    }
	            } catch(destroyException) {
	                logger.warn("Monitor [" + monitor.id + "] destroyed with exception [" + destroyException + "]");
	            }
	        }else {
	            newMonitors.push(monitor);
	        }
	    } catch(ee) {
	        logger.error("Error destroying monitor [" + monitor.id + "] Msg [" + ee + "]");
	    }
    }    
    Ice.StateMon.monitors = newMonitors;
}

Draggable.prototype.ORIGINAL_updateDrag = Draggable.prototype.updateDrag;
Draggable.prototype.updateDrag = function(event, pointer) {
    Droppables.affectedDrop = null;
    this.ORIGINAL_updateDrag(event, pointer);
    ad = Droppables.affectedDrop;
    iceEv = new Ice.DndEvent();
    iceEv.drag = this;


    if(this.dragGhost == true) {
      var height = parseInt(this.element.offsetHeight) ;
      var elementTop = parseInt(Element.getStyle(this.element, 'top').split("px")[0]) ;
      if (Prototype.Browser.IE) elementTop = this.element.cumulativeOffset().top; // ICE-3287
      var pointerTop =  Event.pointerY(event);
      var edge = height + elementTop;
      var inRegion = (pointerTop > elementTop &&  pointerTop < edge );
      if (!inRegion) {
        this.element.style.top =  pointerTop + "px"; 
      }
    }

    if (this.hoveringDrop && !ad) {
        iceEv.eventType = Ice.DnD.HOVER_END;
    }

    if (ad && (!this.hoveringDrop || this.hoveringDrop.element.id != ad.element.id)) {
        iceEv.eventType = Ice.DnD.HOVER_START;
        iceEv.drop = ad;
    }

    this.hoveringDrop = (ad != null) ? ad : null;
    if (!iceEv.eventType)iceEv.eventType = Ice.DnD.DRAG_START;
    iceEv.submit();
};

Draggable.prototype.ORIGINAL_finishDrag = Draggable.prototype.finishDrag;
Draggable.prototype.finishDrag = function(event, success) {
    if (!this.options.sort) {
        //drag has finished, set the dragging to false. Used by the Draggable.removeMe()
        this.dragging = false;
        if (success) {
            iceEv = new Ice.DndEvent();
            iceEv.drag = this;
            if (this.hoveringDrop) {
                iceEv.drop = this.hoveringDrop;
                iceEv.eventType = Ice.DnD.DROPPED;
            } else {
                iceEv.eventType = Ice.DnD.DRAG_CANCEL;
            }
            iceEv.submit();

            if (this.dragGhost == true) {
                this.element = this._original;
                Element.remove(this._ghost);
                this._ghost = null;

            }
            if (this.options.dragCursor) {
                Element.remove(this._cursor);
                this._cursor = null;
            }
            //remove the draggable monitor when the drag has completed.
            Draggable.removeMe(this.element.id);
        }
    }


    this.ORIGINAL_finishDrag(event, success);
    DropRegions.init = false;
    DropRegions.map = [];
    if (this.options.sort && success) {
        try {
            var form = Ice.util.findForm(this.element);
            var nothingEvent = new Object();
            Ice.DnD.logger.debug("Submitting Sortable [" + this.element + "]");
            iceSubmit(form, this.element, nothingEvent);
        } catch(ee) {
            Ice.DnD.logger.error('error submiting sortable element[' + this.element + '] Err Msg[' + ee + ']');
        }
    }
};
