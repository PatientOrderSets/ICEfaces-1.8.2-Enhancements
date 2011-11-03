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
//           (c) 2005 Ivan Krstic (http://blogs.law.harvard.edu/ivan)
//           (c) 2005 Jon Tirsen (http://www.tirsen.com)
// Contributors:
//  Richard Livsey
//  Rahul Bhargava
//  Rob Wills
// 
// See scriptaculous.js for full license.

var Autocompleter = {};

Autocompleter.Finder = {
    list:new Array(),
    add: function(ele, autocomplete) {
        this.list[ele.id] = autocomplete;
    },
    find: function(id) {
        return this.list[id];
    }
};

Autocompleter.Base = function() {
};

Autocompleter.Base.prototype = {
    baseInitialize: function(element, update, options, rowC, selectedRowC) {
        this.element = $(element);
        this.update = $(update);
        this.hasFocus = false;
        this.changed = false;
        this.active = false;
        this.index = -1;
        this.entryCount = 0;
        this.rowClass = rowC;
        this.selectedRowClass = selectedRowC;

        if (this.setOptions)
            this.setOptions(options);
        else
            this.options = options || {};

        this.options.paramName = this.options.paramName || this.element.name;
        this.options.tokens = this.options.tokens || [];
        this.options.frequency = this.options.frequency || 0.4;
        this.options.minChars = this.options.minChars || 1;
        this.options.onShow = this.options.onShow ||
                              function(element, update) {
                                  if (!update.style.position || update.style.position == 'absolute') {
                                      update.style.position = 'absolute';
                                      Position.clone(element, update, {setHeight: false, offsetTop: element.offsetHeight});
                                      update.clonePosition(element.parentNode, {setTop:false, setWidth:false, setHeight:false,
                                          offsetLeft: element.offsetLeft - element.parentNode.offsetLeft});
                                  }
                                  Effect.Appear(update, {duration:0.15});
                              };
        this.options.onHide = this.options.onHide ||
                              function(element, update) {
                                  new Effect.Fade(update, {duration:0.15})
                              };

        if (typeof(this.options.tokens) == 'string')
            this.options.tokens = new Array(this.options.tokens);

        this.observer = null;
        this.element.setAttribute('autocomplete', 'off');
        Element.hide(this.update);
        Event.observe(this.element, "blur", this.onBlur.bindAsEventListener(this));
        Event.observe(this.element, "keypress", this.onKeyPress.bindAsEventListener(this));
        if (Prototype.Browser.IE || Prototype.Browser.WebKit)
            Event.observe(this.element, "keydown", this.onKeyDown.bindAsEventListener(this));
        // ICE-3830
        if (Prototype.Browser.IE || Prototype.Browser.WebKit)
            Event.observe(this.element, "paste", this.onPaste.bindAsEventListener(this));
    },

    show: function() {
        if (Element.getStyle(this.update, 'display') == 'none')this.options.onShow(this.element, this.update);
        if (!this.iefix &&
            (navigator.appVersion.indexOf('MSIE') > 0) &&
            (navigator.userAgent.indexOf('Opera') < 0) &&
            (Element.getStyle(this.update, 'position') == 'absolute')) {
            var sendURI = Ice.ElementModel.Element.adaptToElement(this.element).findConnection().sendURI;
            var webappContext = sendURI.substring(0, sendURI.indexOf("block/send-receive-updates"));
            new Insertion.After(this.update,
                    '<iframe id="' + this.update.id + '_iefix" title="IE6_Fix" ' +
                    'style="display:none;position:absolute;filter:progid:DXImageTransform.Microsoft.Alpha(opacity=0);" ' +
                    'src="' + webappContext + 'xmlhttp/blank" frameborder="0" scrolling="no"></iframe>');
            this.iefix = $(this.update.id + '_iefix');
        }
        if (this.iefix) setTimeout(this.fixIEOverlapping.bind(this), 50);
        this.element.focus();        
    },

    fixIEOverlapping: function() {
        Position.clone(this.update, this.iefix);
        this.iefix.style.zIndex = 1;
        this.update.style.zIndex = 2;
        Element.show(this.iefix);
    },

    hide: function() {
        this.stopIndicator();
        if (Element.getStyle(this.update, 'display') != 'none') this.options.onHide(this.element, this.update);
        if (this.iefix) Element.hide(this.iefix);
    },

    startIndicator: function() {
        if (this.options.indicator) Element.show(this.options.indicator);
    },

    stopIndicator: function() {
        if (this.options.indicator) Element.hide(this.options.indicator);
    },

    onKeyPress: function(event) {
        if (!this.active) {
            Ice.Autocompleter.logger.debug("Key press ignored. Not active.");
            switch (event.keyCode) {
                case Event.KEY_TAB:
                case Event.KEY_RETURN:
                    this.getUpdatedChoices(true, event, -1);
                    return;
                case Event.KEY_DOWN:
                    this.getUpdatedChoices(false, event, -1);
                    return;
            }
        }

        Ice.Autocompleter.logger.debug("Key Press");
        if (this.active) {
            switch (event.keyCode) {
                case Event.KEY_TAB:
                case Event.KEY_RETURN:
                //this.selectEntry();
                //Event.stop(event);

                    this.hidden = true; // Hack to fix before beta. Was popup up the list after a selection was made
                    var idx = this.selectEntry();
                    Ice.Autocompleter.logger.debug("Getting updated choices on enter");
                    this.getUpdatedChoices(true, event, idx);
                    this.hide();
                    Event.stop(event);
                    return;
                case Event.KEY_ESC:
                    this.hide();
                    this.active = false;
                    Event.stop(event);
                    return;
                case Event.KEY_LEFT:
                case Event.KEY_RIGHT:
                    return;
                case Event.KEY_UP:
                    //ICE-4549 (the KEY_UP and KEY_DOWN would be handled by the onkeydown event for IE and WebKit)
                    if (!(Prototype.Browser.IE || Prototype.Browser.WebKit)) {                      
                        this.markPrevious();
                        this.render();
                        //if(navigator.appVersion.indexOf('AppleWebKit')>0)
                        Event.stop(event);
                        return;
	                }
                case Event.KEY_DOWN:
                    //ICE-4549 
                    if (!(Prototype.Browser.IE || Prototype.Browser.WebKit)) {                 
                        this.markNext();
                        this.render();
                        //if(navigator.appVersion.indexOf('AppleWebKit')>0)
                        Event.stop(event);
                        return;
                    }

            }
        }
        else {
            if (event.keyCode == Event.KEY_TAB || event.keyCode == Event.KEY_RETURN) return;
        }

        this.changed = true;
        this.hasFocus = true;
        this.index = -1;
        //This is to avoid an element being select because the mouse just happens to be over the element when the list pops up
        this.skip_mouse_hover = true;
        if (this.active) this.render();
        if (this.observer) clearTimeout(this.observer);
        this.observer = setTimeout(this.onObserverEvent.bind(this), this.options.frequency * 1000);
    },

    onKeyDown: function(event) {
        if (!this.active) {
            switch (event.keyCode) {
                case Event.KEY_DOWN:
                    this.getUpdatedChoices(false, event, -1);
                    return;
                case Event.KEY_BACKSPACE:
                case Event.KEY_DELETE:
                    if (this.observer) clearTimeout(this.observer);
                    this.observer = setTimeout(this.onObserverEvent.bind(this), this.options.frequency * 1000);
                    return;
            }
        }
        else if (this.active) {
            switch (event.keyCode) {
                case Event.KEY_UP:
                    this.markPrevious();
                    this.render();
                    Event.stop(event);
                    return;
                case Event.KEY_DOWN:
                    this.markNext();
                    this.render();
                    Event.stop(event);
                    return;
                case Event.KEY_ESC:
                    if (Prototype.Browser.WebKit) {
                        this.hide();
                        this.active = false;
                        Event.stop(event);
                        return;
                    }
                case Event.KEY_BACKSPACE:
                case Event.KEY_DELETE:
                    if (this.observer) clearTimeout(this.observer);
                    this.observer = setTimeout(this.onObserverEvent.bind(this), this.options.frequency * 1000);
                    return;
            }
        }
    },

    activate: function() {
        this.changed = false;
        this.hasFocus = true;
    },

    onHover: function(event) {
        var element = Event.findElement(event, 'DIV');
        if (this.index != element.autocompleteIndex) {
            if (!this.skip_mouse_hover) this.index = element.autocompleteIndex;
            this.render();
        }
        Event.stop(event);
    },

    onMove: function(event) {
        if (this.skip_mouse_hover) {
            this.skip_mouse_hover = false;
            this.onHover(event);
        }
    },

    onClick: function(event) {
        this.hidden = true;
        // Hack to fix before beta. Was popup up the list after a selection was made
        var element = Event.findElement(event, 'DIV');
        this.index = element.autocompleteIndex;
        var idx = element.autocompleteIndex;
        this.selectEntry();
        this.getUpdatedChoices(true, event, idx);
        this.hide();

    },

    onBlur: function(event) {
        if (navigator.userAgent.indexOf("MSIE") >= 0) { // ICE-2225
            var strictMode = document.compatMode && document.compatMode == "CSS1Compat";
            var docBody = strictMode ? document.documentElement : document.body;
            // Right or bottom border, if any, will be treated as scrollbar.
            // No way to determine their width or scrollbar width accurately.
            if (event.clientX > docBody.clientLeft + docBody.clientWidth ||
                event.clientY > docBody.clientTop + docBody.clientHeight) {
                this.element.focus();
                return;
            }
        }
        // needed to make click events working
        setTimeout(this.hide.bind(this), 250);
        this.hasFocus = false;
        this.active = false;
    },
    
    // ICE-3830
    onPaste: function(event) {
        this.changed = true;
        this.hasFocus = true;
        this.index = -1;
        this.skip_mouse_hover = true;
        if (this.active) this.render();
        if (this.observer) clearTimeout(this.observer);
        this.observer = setTimeout(this.onObserverEvent.bind(this), this.options.frequency * 1000);
        return;
    },

    render: function() {
        if (this.entryCount > 0) {
            for (var i = 0; i < this.entryCount; i++)
                if (this.index == i) {
                    ar = this.rowClass.split(" ");
                    for (var ai = 0; ai < ar.length; ai++)
                        Element.removeClassName(this.getEntry(i), ar[ai]);
                    ar = this.selectedRowClass.split(" ");
                    for (var ai = 0; ai < ar.length; ai++)
                        Element.addClassName(this.getEntry(i), ar[ai]);
                }
                else {
                    ar = this.selectedRowClass.split(" ");
                    for (var ai = 0; ai < ar.length; ai++)
                        Element.removeClassName(this.getEntry(i), ar[ai]);
                    ar = this.rowClass.split(" ");
                    for (var ai = 0; ai < ar.length; ai++)
                        Element.addClassName(this.getEntry(i), ar[ai]);
                }
            if (this.hasFocus) {
                this.show();
                this.active = true;
            }
        } else {
            this.active = false;
            this.hide();
        }
    },

    markPrevious: function() {
        if (this.index > 0) this.index--
        else this.index = this.entryCount - 1;
    },

    markNext: function() {
        if (this.index == -1) {
            this.index++;
            return;
        }
        if (this.index < this.entryCount - 1) this.index++
        else this.index = 0;
    },

    getEntry: function(index) {
        try {
            return this.update.firstChild.childNodes[index];
        } catch(ee) {
            return null;
        }
    },

    getCurrentEntry: function() {
        return this.getEntry(this.index);
    },

    selectEntry: function() {
        var idx = -1;
        this.active = false;
        if (this.index >= 0) {
            idx = this.index;
            this.updateElement(this.getCurrentEntry());
            this.index = -1;
        }
        return idx;
    },

    updateElement: function(selectedElement) {
        if (this.options.updateElement) {
            this.options.updateElement(selectedElement);
            return;
        }
        var value = '';
        if (this.options.select) {
            var nodes = document.getElementsByClassName(this.options.select, selectedElement) || [];
            if (nodes.length > 0) value = Element.collectTextNodes(nodes[0], this.options.select);
        } else
            value = Element.collectTextNodesIgnoreClass(selectedElement, 'informal');

        var lastTokenPos = this.findLastToken();
        if (lastTokenPos != -1) {
            var newValue = this.element.value.substr(0, lastTokenPos + 1);
            var whitespace = this.element.value.substr(lastTokenPos + 1).match(/^\s+/);
            if (whitespace)
                newValue += whitespace[0];
            this.element.value = newValue + value;
        } else {
            this.element.value = value;
        }
        this.element.focus();

        if (this.options.afterUpdateElement)
            this.options.afterUpdateElement(this.element, selectedElement);
    },

    updateChoices: function(choices) {
        if (!this.changed && this.hasFocus) {
            this.update.innerHTML = choices;
            Element.cleanWhitespace(this.update);
            Element.cleanWhitespace(this.update.firstChild);

            if (this.update.firstChild && this.update.firstChild.childNodes) {
                this.entryCount =
                this.update.firstChild.childNodes.length;
                for (var i = 0; i < this.entryCount; i++) {
                    var entry = this.getEntry(i);
                    entry.autocompleteIndex = i;
                    this.addObservers(entry);
                }
            } else {
                this.entryCount = 0;
            }
            this.stopIndicator();
            this.index = -1;
            this.render();
        } else {
            Ice.Autocompleter.logger.debug("Not updating choices Not Changed[" + this.changed + "] hasFocus[" + this.hasFocus + "]");
        }
    },

    addObservers: function(element) {
        Event.observe(element, "mouseover", this.onHover.bindAsEventListener(this));
        Event.observe(element, "click", this.onClick.bindAsEventListener(this));
        Event.observe(element, "mousemove", this.onMove.bindAsEventListener(this));
    },

    dispose:function() {
        for (var i = 0; i < this.entryCount; i++) {
            var entry = this.getEntry(i);
            entry.autocompleteIndex = i;
            Event.stopObserving(entry, "mouseover", this.onHover);
            Event.stopObserving(entry, "click", this.onClick);
            Event.stopObserving(entry, "mousemove", this.onMove);
        }
        Event.stopObserving(this.element, "mouseover", this.onHover);
        Event.stopObserving(this.element, "click", this.onClick);
        Event.stopObserving(this.element, "mousemove", this.onMove);
        Event.stopObserving(this.element, "blur", this.onBlur);
        Event.stopObserving(this.element, "keypress", this.onKeyPress);
        if (Prototype.Browser.IE || Prototype.Browser.WebKit)
            Event.stopObserving(this.element, "keydown", this.onKeyDown);
        Autocompleter.Finder.list[this.element.id] = null;
        Ice.Autocompleter.logger.debug("Destroyed autocomplete [" + this.element.id + "]");
    },

    onObserverEvent: function() {
        this.changed = false;
        if (this.getToken().length >= this.options.minChars) {
            this.startIndicator();
            this.getUpdatedChoices(false, undefined, -1);
        } else {
            this.active = false;
            this.hide();
            this.getUpdatedChoices(false, undefined, -1);
        }
    },

    getToken: function() {
        var tokenPos = this.findLastToken();
        if (tokenPos != -1)
            var ret = this.element.value.substr(tokenPos + 1).replace(/^\s+/, '').replace(/\s+$/, '');
        else
            var ret = this.element.value;

        return /\n/.test(ret) ? '' : ret;
    },

    findLastToken: function() {
        var lastTokenPos = -1;

        for (var i = 0; i < this.options.tokens.length; i++) {
            var thisTokenPos = this.element.value.lastIndexOf(this.options.tokens[i]);
            if (thisTokenPos > lastTokenPos)
                lastTokenPos = thisTokenPos;
        }
        return lastTokenPos;
    }
}

Ajax.Autocompleter = Class.create();
Object.extend(Object.extend(Ajax.Autocompleter.prototype, Autocompleter.Base.prototype), {
    initialize: function(element, update, url, options) {
        this.baseInitialize(element, update, options);
        this.options.asynchronous = true;
        this.options.onComplete = this.onComplete.bind(this);
        this.options.defaultParams = this.options.parameters || null;
        this.url = url;
    },

    getUpdatedChoices: function() {
        entry = encodeURIComponent(this.options.paramName) + '=' +
                encodeURIComponent(this.getToken());

        this.options.parameters = this.options.callback ?
                                  this.options.callback(this.element, entry) : entry;

        if (this.options.defaultParams)
            this.options.parameters += '&' + this.options.defaultParams;

        new Ajax.Request(this.url, this.options);
    },

    onComplete: function(request) {
        this.updateChoices(request.responseText);
    }
});


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
//           (c) 2005 Ivan Krstic (http://blogs.law.harvard.edu/ivan)
//           (c) 2005 Jon Tirsen (http://www.tirsen.com)
// Contributors:
//  Richard Livsey
//  Rahul Bhargava
//  Rob Wills
//
// See scriptaculous.js for full license.

Ice.Autocompleter = Class.create();


Object.extend(Object.extend(Ice.Autocompleter.prototype, Autocompleter.Base.prototype), {
    initialize: function(id, updateId, options, rowClass, selectedRowClass) {
        Ice.Autocompleter.logger.debug("Building Ice Autocompleter ID [" + id + "]");
        var existing = Autocompleter.Finder.list[id];
        if (existing && !existing.monitor.changeDetected()) {
            return;
        }

        if (options)
            options.minChars = 0;
        else
            options = {minChars:0};
        var element = $(id);
        var ue = $(updateId);
        this.baseInitialize(element, ue, options, rowClass, selectedRowClass);

        this.options.onComplete = this.onComplete.bind(this);
        this.options.defaultParams = this.options.parameters || null;
        this.monitor = new Ice.AutocompleterMonitor(element, ue, options, rowClass, selectedRowClass);
        this.monitor.object = this;
        Ice.StateMon.add(this.monitor);
        Autocompleter.Finder.add(this.element, this);
        Ice.Autocompleter.logger.debug("Done building Ice Autocompleter");
        if (this.monitor.changeDetected()) {
            Ice.Autocompleter.logger.debug("Change has been detected");
        }
    },

    getUpdatedChoices: function(isEnterKey, event, idx) {
        if (!event) {
            event = new Object();
        }
        entry = encodeURIComponent(this.options.paramName) + '=' +
                encodeURIComponent(this.getToken());

        this.options.parameters = this.options.callback ?
                                  this.options.callback(this.element, entry) : entry;

        if (this.options.defaultParams)
            this.options.parameters += '&' + this.options.defaultParams;

        var form = Ice.util.findForm(this.element);
        if (idx > -1) {
            var indexName = this.element.id + "_idx";
            form[indexName].value = idx;
        }
        
        //     form.focus_hidden_field.value=this.element.id;
        if (isEnterKey) {
            Ice.Autocompleter.logger.debug("Sending submit");
            iceSubmit(form, this.element, event);
        }
        else {
            Ice.Autocompleter.logger.debug("Sending partial submit");
            iceSubmitPartial(form, this.element, event);
        }
    },

    onComplete: function(request) {
        this.updateChoices(request.responseText);
    },

    updateNOW: function(text) {


        if (this.hidden) {
            this.hidden = false;
            //Ice.Autocompleter.logger.debug("Not showing due to hide force");
            return;
        }
        this.hasFocus = true;
        Element.cleanWhitespace(this.update);
        this.updateChoices(text);
        this.show();
        this.render();
    }
});