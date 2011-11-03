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

Ice.DnD.StyleReader = Class.create();
Ice.DnD.StyleReader = {

    styles: 'position,top,left,display',

    buildStyle: function(ele) {
        //Ice.DnD.logger.debug("Building Style");
        var result = '';
        Ice.DnD.StyleReader.styles.split(',').each(
                function(style) {
                    result += style + ':' + Ice.DnD.StyleReader.getStyle(ele, style) + ';';
                });
        return result;
    },
    getStyle: function(x, styleProp) {
        if (x.currentStyle)
            var y = x.currentStyle[styleProp];
        else if (window.getComputedStyle)
            var y = document.defaultView.getComputedStyle(x, null).getPropertyValue(styleProp);
        else
            var y = x.style[styleProp];
        return y;
    },

    findCssField:function(ele, f) {
        if (!f)
            f = Ice.util.findForm(ele);
        var fe = f.getElementsByTagName('input');
        var cssUpdate = null;
        var i = 0;
        // We only want hidden fields.
        for (i = 0; i < fe.length; i++) {
            if (fe[i].type == 'hidden' && fe[i].name == 'icefacesCssUpdates') {
                cssUpdate = fe[i];
                break;
            }
        }
        return cssUpdate;
    },
    upload: function(ele, submit) {

        var cssUpdate = Ice.DnD.StyleReader.findCssField(ele);

        if (cssUpdate) {
            var val = cssUpdate.value;
            var css = Ice.DnD.StyleReader.buildStyle(ele);
            Ice.DnD.logger.debug("Update CSS ID[" + ele.id + "] CSS[" + css + "] form filed name = [" + cssUpdate.name + "]");
            cssUpdate.value = val + ele.id + '{' + css + '}';
            if (submit) {
                var form = Ice.util.findForm(ele);
                iceSubmitPartial(form, ele, null);
            }
        }
    }
}


Ice.modal = Class.create();
Ice.modal = {
    running:false,
    target:null,
    ids: [],
    tabindexValues: [],
    zIndexCount: 25000,
    start:function(target, iframeUrl,trigger, manualPosition) {
        var modal = document.getElementById(target);
        modal.style.visibility = 'hidden';
        modal.style.position = 'absolute';
        var iframe = document.getElementById('iceModalFrame' + target);
        if (!iframe) {
            iframe = document.createElement('iframe');
            iframe.title = 'Ice Modal Frame';
            iframe.frameborder = "0";
            iframe.id = 'iceModalFrame' + target;
            iframe.src = iframeUrl;
            iframe.style.zIndex = Ice.modal.zIndexCount;
            Ice.modal.zIndexCount += 3;
            iframe.style.opacity = 0.5;
            iframe.style.filter = 'alpha(opacity=50)';

            iframe.style.position = 'absolute';
            iframe.style.visibility = 'hidden';
            iframe.style.backgroundColor = 'black';
            iframe.style.borderWidth = "0";

            iframe.style.top = '0';
            iframe.style.left = '0';
            //trick to avoid bug in IE, see http://support.microsoft.com/kb/927917
            modal.parentNode.insertBefore(iframe, modal);
            var modalDiv = document.createElement('div');
            modalDiv.style.position = 'absolute';
            modalDiv.style.zIndex = parseInt(iframe.style.zIndex) + 1;
            modalDiv.style.backgroundColor = 'transparent';
            modal.parentNode.insertBefore(modalDiv, modal);
            var resize = function() {
                //lookup element again because 'resize' closure is registered only once
                var frame = document.getElementById('iceModalFrame' + target);
                if (frame) {
                    var frameDisp = frame.style.display;
                    frame.style.display = "none";
                    var documentWidth = document.documentElement.scrollWidth;
                    var bodyWidth = document.body.scrollWidth;
                    var documentHeight = document.documentElement.scrollHeight;
                    var bodyHeight = document.body.scrollHeight;
                    var width = (bodyWidth > documentWidth ? bodyWidth : documentWidth) ;
                    var height = (bodyHeight > documentHeight ? bodyHeight : documentHeight);
                    var viewportHeight = document.viewport.getHeight();
                    if (height < viewportHeight) height = viewportHeight;
                    frame.style.width = width + 'px';
                    frame.style.height = height + 'px';
                    frame.style.visibility = 'visible';
                    var modalWidth = 100;
                    var modalHeight = 100;
                    try {
                        modalWidth = Element.getWidth(modal);
                        modalHeight = Element.getHeight(modal);
                    } catch (e) {
                    }
                    modalWidth = parseInt(modalWidth) / 2;
                    modalHeight = parseInt(modalHeight) / 2;
                    if (!manualPosition) {
                        modal.style.top = (parseInt(height) / 2) - modalHeight + "px";
                        modal.style.left = (parseInt(width) / 2 ) - modalWidth + "px";
                    }
                    frame.style.display = frameDisp;
                    $(frame.nextSibling).clonePosition(frame);
                }
            };
            resize();
            window.onResize(resize);
            window.onScroll(resize);
        }

        var modal = document.getElementById(target);

        modal.style.zIndex = parseInt(iframe.style.zIndex) + 2;
        Ice.modal.target = modal;
        Ice.modal.ids.push(target);
        if (!Ice.modal.running) {
            Ice.modal.disableTabindex();
        }
        Ice.modal.running = true;
        modal.style.visibility = 'visible';
        if (trigger) {
            Ice.modal.trigger = trigger; 
            $(trigger).blur();
            setFocus('');
        }
    },
    stop:function(target) {
        if (Ice.modal.ids.last() == target) {
            var iframe = document.getElementById('iceModalFrame' + target);
            if (iframe) {
                iframe.parentNode.removeChild(iframe.nextSibling);
                iframe.parentNode.removeChild(iframe);
                logger.debug('removed modal iframe for : ' + target);
            }
            Ice.modal.ids.pop();
            Ice.modal.zIndexCount -= 3;
            Ice.modal.running = false;
            if (Ice.modal.trigger) {
                Ice.Focus.setFocus(Ice.modal.trigger);
                Ice.modal.trigger = '';
            }
            Ice.modal.restoreTabindex();
        }
    },
    disableTabindex: function() {
        var focusables = {};
        focusables.a = document.getElementsByTagName('a');
        focusables.area = document.getElementsByTagName('area');
        focusables.button = document.getElementsByTagName('button');
        focusables.input = document.getElementsByTagName('input');
        focusables.object = document.getElementsByTagName('object');
        focusables.select = document.getElementsByTagName('select');
        focusables.textarea = document.getElementsByTagName('textarea');
        
        var tabindexValues = [];
        for (listName in focusables) {
            var list = focusables[listName]
            for (var j = 0; j < list.length; j++) {
                var ele = list[j];
                if (!Ice.modal.containedInId(ele,Ice.modal.ids.last())) {
                    var obj = {};
                    obj.element = ele;
                    obj.tabIndex = ele.tabIndex ? ele.tabIndex : '';
                    ele.tabIndex = '-1';
                    tabindexValues.push(obj);
                }
            }
        }
        Ice.modal.tabindexValues = tabindexValues;
    },
    restoreTabindex: function() {
        Ice.modal.tabindexValues.each(function(obj) {
            obj.element.tabIndex = obj.tabIndex;
        });
        Ice.modal.tabindexValues = [];
    }, 
    containedInId:function(node, id) {
        if (node.id == id) {
            return true;
        }
        var parent = node.parentNode;
        if (parent) {
            return Ice.modal.containedInId(parent, id);
        }
        return false;
    }
};

Ice.autoCentre = Class.create();
Ice.autoCentre = {
    ids:[],
    centerAll:function() {
        Ice.autoCentre.ids.each(Ice.autoCentre.keepCentred);
    },
    keepCentred:function(id) {
        var scrollX = window.pageXOffset || document.body.scrollLeft || document.documentElement.scrollLeft;
        var scrollY = window.pageYOffset || document.body.scrollTop || document.documentElement.scrollTop;
        var div = document.getElementById(id);
        if (div) {
            var x = Math.round((Element.getWidth(document.body) - Element.getWidth(div)) / 2 + scrollX);
            if (x < 0) x = 0;
            var y = Math.round(((window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight) - Element.getHeight(div)) / 2 + scrollY);
            if (y < 0) y = 0;
            x = x + "px";
            y = y + "px";
            Element.setStyle(div, {position:'absolute'});
            Element.setStyle(div, {left: x});
            Element.setStyle(div, {top:y});
        }
    },
    start:function(target) {
        Ice.autoCentre.keepCentred(target);
        if (Ice.autoCentre.ids.size() == 0) {
            Event.observe(window, 'resize', Ice.autoCentre.centerAll);
            Event.observe(window, 'scroll', Ice.autoCentre.centerAll);
        }
        if (Ice.autoCentre.ids.indexOf(target) < 0) {
            Ice.autoCentre.ids.push(target);
        }
    },
    stop:function(target) {
        Ice.autoCentre.ids = Ice.autoCentre.ids.without(target);
        if (Ice.autoCentre.ids.size() == 0) {
            Event.stopObserving(window, 'resize', Ice.autoCentre.centerAll);
            Event.stopObserving(window, 'scroll', Ice.autoCentre.centerAll);
        }
    }
};

Ice.autoPosition = Class.create();
Ice.autoPosition = {
    id:null,
    xPos:null,
    yPos:null,
    keepPositioned:function() {
        var scrollX = window.pageXOffset || document.body.scrollLeft || document.documentElement.scrollLeft;
        var scrollY = window.pageYOffset || document.body.scrollTop || document.documentElement.scrollTop;
        var div = document.getElementById(Ice.autoPosition.id);
        if (div) {
            var x = Math.round(Ice.autoPosition.xPos + scrollX) + "px";
            var y = Math.round(Ice.autoPosition.yPos + scrollY) + "px";
            Element.setStyle(div, {position:'absolute'});
            Element.setStyle(div, {left: x});
            Element.setStyle(div, {top:y});
        }
    },
    start:function(target, x, y) {
        Ice.autoPosition.id = target;
        Ice.autoPosition.xPos = x;
        Ice.autoPosition.yPos = y;
        var s = document.getElementById(target).style;
        if (!Prototype.Browser.IE) s.visibility = 'hidden';
        Ice.autoPosition.keepPositioned();
        if (!Prototype.Browser.IE) s.visibility = 'visible';
        Event.observe(window, 'scroll', Ice.autoPosition.keepPositioned);
    },
    stop:function(target) {
        if (Ice.autoPosition.id == target) {
            Event.stopObserving(window, 'scroll', Ice.autoPosition.keepPositioned);
        }
    }
};

Ice.iFrameFix = Class.create();
Ice.iFrameFix = {
    start: function(elementId, url) {
        var index = navigator.userAgent.indexOf("MSIE");
        if (index == -1) return;

        var version = parseFloat(navigator.userAgent.substring(index + 5));
        if (version >= 7) return;

        var popupDiv = document.getElementById(elementId);
        if (!popupDiv) return;

        var popupIFrame = document.getElementById(elementId + ":iframe");
        if (!popupIFrame) {
            popupIFrame = document.createElement("iframe");
//          popupIFrame.src = "javascript:void 0;";
            popupIFrame.src = url;
            popupIFrame.setAttribute("id", elementId + ":iframe")
//          popupDiv.insertBefore(popupIFrame, popupDiv.firstChild);
            popupDiv.appendChild(popupIFrame);
        }
        popupIFrame.style.position = "absolute";
        popupIFrame.style.zIndex = -1;
        popupIFrame.style.filter = "progid:DXImageTransform.Microsoft.Alpha(opacity=0)";
        popupIFrame.style.left = "0px";
        popupIFrame.style.top = "0px";
        popupIFrame.style.width = popupDiv.offsetWidth + 'px';
        popupIFrame.style.height = popupDiv.offsetHeight + 'px';
    }
};

Ice.DnD.adjustPosition = function(id) {
    var element = $(id);
    var viewportDimensions = document.viewport.getDimensions();
    var elementDimensions = element.getDimensions();
    var viewportOffset = element.viewportOffset();
    var positionedOffset = element.positionedOffset();
    var widthDiff = viewportDimensions.width - viewportOffset.left - elementDimensions.width;
    var heightDiff = viewportDimensions.height - viewportOffset.top - elementDimensions.height;

    if (viewportOffset.left < 0) {
        element.style.left = positionedOffset.left - viewportOffset.left + "px";
    } else if (widthDiff < 0) {
        element.style.left = positionedOffset.left + widthDiff + "px";
    }
    if (viewportOffset.top < 0) {
        element.style.top = positionedOffset.top - viewportOffset.top + "px";
    } else if (heightDiff < 0) {
        element.style.top = positionedOffset.top + heightDiff + "px";
    }
}
