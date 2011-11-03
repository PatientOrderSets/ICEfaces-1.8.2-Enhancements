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

Droppables.affectedDrop = null;

Droppables.ORIGINAL_show = Droppables.show;
Droppables.show = function(point, element) {
    if (!this.drops.length) return;

    if (this.last_active) this.deactivate(this.last_active);
    var dropsToScan = this.drops;
    if (DropRegions.init && this.drops.all(function(drop){return !drop.scrollid;})) {
        dropsToScan = DropRegions.drops(point);
    }
    dropsToScan.each(function(drop) {
        if (Position.within(drop.element, point[0], point[1])) {
            if (Droppables.isAffected(point, element, drop)) {
                //Ice.DnD.logger.debug("Affected True OnHover" + drop.onHover + "]");
                Droppables.affectedDrop = drop;
                if (drop.onHover)
                    drop.onHover(element, drop.element, Position.overlap(drop.overlap, drop.element));
                if (drop.greedy) {
                    Droppables.activate(drop);
                }
            }
        }
        if (!DropRegions.init) {
            DropRegions.register(drop);
        }
    });
    DropRegions.init = true;
};

Droppables.ORIGINAL_isAffected = Droppables.isAffected;
Droppables.isAffected = function(point, element, drop) {
    var result = false;
    result = Droppables.ORIGINAL_isAffected(point, element, drop);
    if (result && drop.sort) {
        if (!Ice.DnD.sortableDraggable(element)) {
            result = false;
        }
    }
    return result;
};

Droppables.ORIGINAL_add = Droppables.add;
Droppables.add = function(ele, options) {
    var monitors = Ice.StateMon.monitors;
    for (i = 0; i < monitors.length; i++) {
        monitor = monitors[i];
        if (monitor.id == ele && monitor.type == 'Droppable') {
            return;
        }
    }
    Droppables.ORIGINAL_add(ele, options);
    if (options && !options.sort) {
        var monitor = new Ice.DroppableMonitor($(ele), options);
        Ice.StateMon.add(monitor);
    }
}
