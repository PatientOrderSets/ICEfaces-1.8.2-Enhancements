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

window.width = function() {
    return window.innerWidth ? window.innerWidth : (document.documentElement && document.documentElement.clientWidth) ? document.documentElement.clientWidth : document.body.clientWidth;
};

window.height = function() {
    return window.innerHeight ? window.innerHeight : (document.documentElement && document.documentElement.clientHeight) ? document.documentElement.clientHeight : document.body.clientHeight;
};

['onLoad', 'onUnload', 'onBeforeUnload', 'onResize', 'onScroll' ].each(function(name) {
    //avoid to redeclare the callback 
    if (!window[name]) {
        window[name] = function(listener) {
            var eventName = name.toLowerCase();
            var previousListener = window[eventName];
            var listeners = previousListener ? [ previousListener, listener ] : [ listener ];
            window[eventName] = listeners.broadcaster();
            //redefine for next calls
            window[name] = function(listener) {
                //add listener only when not previously added
                if (!listeners.detect(function(existingListener) {
                    return existingListener.toString() == listener.toString();
                })) listeners.push(listener);
            };
        };
    }
});

if(typeof OpenAjax!='undefined'){
    if(typeof OpenAjax.addOnLoad !='undefined'){
        var current = window.onLoad;
        window.onLoad = OpenAjax.addOnLoad;
        OpenAjax.addOnLoad(current);
    }
    if(typeof OpenAjax.addOnUnLoad !='undefined'){
        var current = window.onUnload;
        window.onUnload = OpenAjax.addOnUnLoad;
        OpenAjax.addOnLoad(current);
    }

}


window.onKeyPress = function(listener) {
    var previousListener = document.onkeypress;
    document.onkeypress = previousListener ? function(e) {
        listener(Ice.EventModel.Event.adaptToEvent(e));
        previousListener(e);
    } : function(e) {
        listener(Ice.EventModel.Event.adaptToKeyEvent(e));
    };
};
window.onKeyUp = function(listener) {
    var previousListener = document.onkeyup;
    document.onkeyup = previousListener ? function(e) {
        listener(Ice.EventModel.Event.adaptToEvent(e));
        previousListener(e);
    } : function(e) {
        listener(Ice.EventModel.Event.adaptToKeyEvent(e));
    };
};