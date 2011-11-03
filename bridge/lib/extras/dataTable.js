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

Ice.dataTable = {};

Ice.dataTable.DataTable = Class.create({
    initialize: function(id) {
        this.id = id;
        this.resizeObserver = this.resize.bindAsEventListener(this);
    },

    resize: function() {
        var table = $(this.id);
        if (!table) return;
        var scrollTable = table.select("div.iceDatTblScrlSpr")[0];
	//no scrollabletable
        if (!scrollTable) return;

        var spacer = scrollTable.select("table > thead > tr > th:last-child > div")[0];
        var body = table.select("div.iceDatTblScrlSpr + div")[0];
	//nobody
        if (!body) return;

        var borderLeftWidth = body.getStyle("borderLeftWidth");
        var borderRightWidth = body.getStyle("borderRightWidth");
        if (Prototype.Browser.IE && body.scrollHeight > body.clientHeight) {
            body.style.overflowX = "hidden";
            body.style.overflowY = "scroll";
        }
        var width=body.getWidth();
        var scrollWidth=width - body.clientWidth;
	
	//no scroller
        if (scrollWidth == 0) return;

        body.setStyle({borderLeftWidth:0, borderRightWidth:0});
        var innerTable = body.select("table")[0];
        var headerTable = scrollTable.select("table")[0];
  
        if (spacer)
          spacer.setStyle({width:scrollWidth + "px"});

        //fixing IE6 bug, table width should be decreased by scrollWidth    
        var innerTable = body.select("table")[0];
        if (innerTable){
          var innerTableWidth = innerTable.getWidth(); 
          if (Prototype.Browser.IE) { 
//            innerTable.setStyle({width:body.clientWidth  + "px"});
          }
        }

        body.setStyle({borderLeftWidth:borderLeftWidth, borderRightWidth:borderRightWidth});

    }
});

Ice.dataTable.DataTable.hash = $H();

Ice.dataTable.onLoad = function(id) {
    var table = Ice.dataTable.DataTable.hash.get(id);
    if (table) {
        Event.stopObserving(window, "load", table.resizeObserver);
        Event.stopObserving(window, "resize", table.resizeObserver);
    }
    table = new Ice.dataTable.DataTable(id);
    table.resize();
    Event.observe(window, "load", table.resizeObserver);
    Event.observe(window, "resize", table.resizeObserver);
    Ice.dataTable.DataTable.hash.set(id, table);
};
