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

package com.icesoft.applications.faces.auctionMonitor.stubs;

import java.io.Serializable;
import java.net.URL;
import java.util.Calendar;

/**
 * Class that stores and manages all information for a single auction item
 * Example item information is title, startPrice, etc.
 */
public class ItemType implements Serializable {
    private URL picture;
    private String title;
    private String itemID;
    private String description;
    private String location;
    private String seller;
    private Calendar endTime;

    public ItemType() {
    }

    public ItemType(ItemType parent) {
        this.picture = parent.getPictureURL();
        this.title = parent.getTitle();
        this.itemID = parent.getItemID();
        this.description = parent.getDescription();
        this.location = parent.getLocation();
        this.seller = parent.getSeller();
        this.endTime = parent.getEndTimeCal();
    }

    public Calendar getEndTimeCal() {
        return endTime;
    }

    public String getSeller() {
        return seller;
    }

    public String getTitle() {
        return title;
    }

    public String getItemID() {
        return itemID;
    }

    public URL getPictureURL() {
        return picture;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public void setEndTimeCal(Calendar endTime) {
        this.endTime = endTime;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public void setPictureURL(URL picture) {
        this.picture = picture;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
