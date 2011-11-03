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

[ Ice.Document = new Object, Ice.ElementModel.Element, Ice.Connection, Ice.Ajax ].as(function(This, Element, Connection, Ajax) {
    This.replaceContainerHTML = function(container, html) {
        var start = new RegExp('\<body[^\<]*\>', 'g').exec(html);
        var end = new RegExp('\<\/body\>', 'g').exec(html);
        var body = html.substring(start.index, end.index + end[0].length)
        var bodyContent = body.substring(body.indexOf('>') + 1, body.lastIndexOf('<'));
        var tag = container.tagName;
        var c = $element(container);
        c.disconnectEventListeners();
        c.replaceHtml(['<', tag, '>', bodyContent, '</', tag, '>'].join(''));
    };

    This.Synchronizer = Object.subclass({
        initialize: function(logger, sessionID, viewID) {
            this.logger = logger.child('synchronizer');
            this.ajax = new Ajax.Client(this.logger);
            var id = 'history-frame:' + sessionID + ':' + viewID;
            try {
                //ICE-3242 under certain circumstances accessing location's hash property can throw an exception in Firefox                
                window.frames[id].location.hash;
                this.historyFrame = window.frames[id];
            } catch (e) {
                //alternative way of looking up the frame
                this.historyFrame = id.asElement().contentWindow;
            }
            try {
                if (this.historyFrame.location.hash.length > 0) this.reload();
            } catch (e) {
                this.logger.error("History frame reload failed: " + e);
            }
        },

        synchronize: function() {
            this.synchronize = Function.NOOP;
            try {
                if (!this.historyFrame.location.hash.contains('reload')) {
                    this.historyFrame.location.replace(this.historyFrame.location + '#reload');
                    this.logger.debug('mark document as modified');
                }
            } catch(e) {
                this.logger.warn('could not mark document as modified', e);
            }
        },

        reload: function() {
            try {
                this.logger.info('synchronize body');
                this.ajax.getAsynchronously(document.URL, '', function(request) {
                    request.setRequestHeader('Connection', 'close');
                    request.on(Connection.OK, function(response) {
                        This.replaceContainerHTML(document.body, response.content());
                    });
                });
            } catch (e) {
                this.logger.error('failed to reload body', e);
            }
        },

        shutdown: function() {
            this.synchronize = Function.NOOP;
        }
    });
});
