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


var SortableObserver = Class.create();
SortableObserver.prototype = {

    initialize: function(element, observer) {
        this.element = $(element);
        this.observer = observer;
        this.lastValue = Sortable.serialize(this.element);
    },

    onStart: function(name, drag) {
        this.lastValue = Sortable.serialize(this.element);
        var options = Sortable.options(this.element);
        options.lastDrag = drag;
        //alert("Name [" + name + "] drag [" + drag.element.id + "]");
        options.lastDrag = drag.element.id;
        //SortableObserver.count++;
    },

    onEnd: function() {
        Sortable.unmark();
        var newValue = Sortable.serialize(this.element);
        var options = Sortable.options(this.element);
        options.serializeValue = newValue;
        if (this.lastValue != newValue) {
            this.observer(this.element)
        }
    }
}

var Sortable = {
    sortables: {},
    sortableElements: Array(),
    kids:null,
    _findRootElement: function(element) {
        while (element.tagName != "BODY") {
            if (element.id && Sortable.sortables[element.id]) return element;
            element = element.parentNode;
        }
    },

    options: function(element) {
        element = Sortable._findRootElement($(element));
        if (!element) return;
        return Sortable.sortables[element.id];
    },

    destroy: function(element) {
        var s = Sortable.options(element);

        if (s) {
            Draggables.removeObserver(s.element);
            s.droppables.each(function(d) {
                Droppables.remove(d)
            });
            s.draggables.invoke('destroy');

            delete Sortable.sortables[s.element.id];
            var i = 0;
            var n = Array();
            for (i = 0; i < Sortable.sortableElements.length; i++) {
                if (Sortable.sortableElements[i] && Sortable.sortableElements[i].id != s.element.id) {
                    n.push(Sortable.sortableElements[i]);
                }
            }
            Sortable.sortableElements = n;
        }
    },

    create: function(element, o, override) {
        element = $(element);
        if (Ice.DnD.alreadySort(element)) {
            Ice.DnD.logger.debug('Sort ID [' + element.id + '] already created');
            return;
        }
        var monitor = new Ice.SortableMonitor(element, o);
        var options = Object.extend({
            element:     element,
            tag:         'li',       // assumes li children, override with tag: 'tagname'
            dropOnEmpty: false,

            overlap:     'vertical', // one of 'vertical', 'horizontal'
            constraint:  'vertical', // one of 'vertical', 'horizontal', false
            containment: element,    // also takes array of elements (or id's); or false
            handle:      false,      // or a CSS class
            only:        false,
            hoverclass:  null,
            ghosting:    false,
            format:      null,
            onChange:    Prototype.emptyFunction,
            onUpdate:    Prototype.emptyFunction
        }, arguments[1] || {});
        // clear any old sortable with same element
        this.destroy(element);
        // build options for the draggables
        var options_for_draggable = {
            revert:      true,
            ghosting:    options.ghosting,
            constraint:  options.constraint,
            handle:      options.handle,
        // Sort flag is used by Drag and Drop javascript to avoid Drag and Drop events from being sent
            sort:        true};
        if (options.starteffect)
            options_for_draggable.starteffect = options.starteffect;
        if (options.reverteffect)
            options_for_draggable.reverteffect = options.reverteffect;
        else
            if (options.ghosting) options_for_draggable.reverteffect = function(element) {
                element.style.top = 0;
                element.style.left = 0;
            };
        if (options.endeffect)
            options_for_draggable.endeffect = options.endeffect;
        if (options.zindex)
            options_for_draggable.zindex = options.zindex;
        // build options for the droppables
        var options_for_droppable = {
            overlap:     options.overlap,
            containment: options.containment,
            hoverclass:  options.hoverclass,
            onHover:     Sortable.onHover,
            greedy:      !options.dropOnEmpty,
            sort:        true
        }
        // fix for gecko engine
        Element.cleanWhitespace(element);
        options.draggables = [];
        options.droppables = [];

        // drop on empty handling
        if (options.dropOnEmpty) {
            Droppables.add(element,
            {
                containment: options.containment,
                onHover: Sortable.onEmptyHover, greedy: false, sort:true});
            options.droppables.push(element);
        }
        (options.elements || this.findElements(element, options) || []).each( function(e,i) {
            var handle = options.handles ? $(options.handles[i]) :
            (options.handle ? $(e).select('.' + options.handle)[0] : e); 
            options.draggables.push(
            new Draggable(e, Object.extend(options_for_draggable, { handle: handle })));
            Droppables.add(e, options_for_droppable);
            if(options.tree) e.treeNode = element;
                options.droppables.push(e);      
        });

        // keep reference
        this.sortables[element.id] = options;
        this.sortableElements.push(element);
        monitor.options = options;
        Ice.StateMon.add(monitor);
        // for onupdate
        var observer = new SortableObserver(element, options.onUpdate);
        Draggables.addObserver(observer);
    },

//return all suitable-for-sortable elements in a guaranteed order
    findElements: function(element, options) {
        if (!element.hasChildNodes()) return null;
        var elements = [];
        $A(element.childNodes).each(function(e) {
            if (e.tagName && e.tagName.toUpperCase() == options.tag.toUpperCase() &&
                (!options.only || (Element.hasClassName(e, options.only))))
                elements.push(e);
        });
        return (elements.length > 0 ? elements.flatten() : null);
    },

    onHover: function(element, dropon, overlap) {
        if (overlap > 0.5) {
            Sortable.mark(dropon, 'before');
            if (dropon.previousSibling != element) {
                var oldParentNode = element.parentNode;
                element.style.visibility = "hidden";
                // fix gecko rendering
                dropon.parentNode.insertBefore(element, dropon);
                if (dropon.parentNode != oldParentNode)
                    Sortable.options(oldParentNode).onChange(element);
                Sortable.options(dropon.parentNode).onChange(element);
            }
        } else {
            Sortable.mark(dropon, 'after');
            var nextElement = dropon.nextSibling || null;
            if (nextElement != element) {
                var oldParentNode = element.parentNode;
                element.style.visibility = "hidden";
                // fix gecko rendering
                dropon.parentNode.insertBefore(element, nextElement);
                if (dropon.parentNode != oldParentNode)
                    Sortable.options(oldParentNode).onChange(element);
                Sortable.options(dropon.parentNode).onChange(element);
            }
        }
    },

    onEmptyHover: function(element, dropon) {
        if (element.parentNode != dropon) {
            var oldParentNode = element.parentNode;
            dropon.appendChild(element);
            Sortable.options(oldParentNode).onChange(element);
            Sortable.options(dropon).onChange(element);
        }
    },

    unmark: function() {
        if (Sortable._marker) Element.hide(Sortable._marker);
    },

    mark: function(dropon, position) {
        // mark on ghosting only
        var sortable = Sortable.options(dropon.parentNode);
        if (!sortable) return;
        if (sortable && !sortable.ghosting) return;

        if (!Sortable._marker) {
            Sortable._marker = $('dropmarker') || document.createElement('DIV');
            Element.hide(Sortable._marker);
            Element.addClassName(Sortable._marker, 'dropmarker');
            Sortable._marker.style.position = 'absolute';
            document.getElementsByTagName("body").item(0).appendChild(Sortable._marker);
        }
        var offsets = Position.cumulativeOffset(dropon);
        Sortable._marker.style.left = offsets[0] + 'px';
        Sortable._marker.style.top = offsets[1] + 'px';

        if (position == 'after')
            if (sortable.overlap == 'horizontal')
                Sortable._marker.style.left = (offsets[0] + dropon.clientWidth) + 'px';
            else
                Sortable._marker.style.top = (offsets[1] + dropon.clientHeight) + 'px';

        Element.show(Sortable._marker);
    },

    serialize : function(element) {
        element = $(element);
        var sortableOptions = Sortable.options(element);
        var options = Object.extend({
            tag:  sortableOptions.tag,
            only: sortableOptions.only,
            name: element.id,
            format: sortableOptions.format || /^[^_]*_(.*)$/
        }, arguments[1] || {});
        //alert("Last Drag [" + sortableOptions.lastDrag + "]");
        return "first;" + sortableOptions.lastDrag + ";changed;" + $(this.findElements(element, options) || []).map(function(item) {
            return item.id;
        }).join(";");
    }
}
