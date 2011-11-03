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

package com.icesoft.applications.faces.auctionMonitor.beans;

import com.icesoft.applications.faces.auctionMonitor.AuctionState;
import com.icesoft.applications.faces.auctionMonitor.stubs.ItemType;
import com.icesoft.faces.async.render.SessionRenderer;
import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.Highlight;

import java.util.Calendar;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class used to represent a single item within the auction This class handles
 * such things as pricing, bidding, UI management, etc.
 */
public class AuctionMonitorItemBean extends ItemType {
    private static Log log = LogFactory.getLog(AuctionMonitorItemBean.class);
    private int localBidCount;
    private double localHighBid;
    private boolean expanded;
    private boolean bidExpanded;
    private double oldBid;
    private double tempLocalBid;
    private boolean bidMessage;
    private String imageUrl;
    private long[] timeLeftBrokenDown;
    private String timeLeftStyleClass;
    private Effect effect;

    private static final String NO_PHOTO_URL = "./images/noimage.gif";
    private static final String TIME_LEFT_5 = "./images/time_left_5.gif";
    private static final String TIME_LEFT_10 = "./images/time_left_10.gif";
    private static final String TIME_LEFT_15 = "./images/time_left_15.gif";
    private static final String TIME_LEFT_30 = "./images/time_left_30.gif";
    private static final String TIME_LEFT_60 = "./images/time_left_45.gif";
    private static final String TIME_LEFT_DAYS = "./images/time_left_days.gif";
    private static final String TIME_LEFT_HOURS =
            "./images/time_left_hours.gif";
    private static final String TRIANGLE_OPEN = "./images/triangle_open.gif";
    private static final String TRIANGLE_CLOSED = "./images/triangle_close.gif";
    private static final String SUCCESS = "success";

    private static final String STYLE_CLASS_EXPANDED_ROW = "rowClassHilite";

    public static final int MAX_BID_INCREASE = 1000000;
    public static final long MAX_BID = 1000000000;

    private static final int TIME_DAYS = 24 * 60 * 60 * 1000;
    private static final int TIME_HOURS = 60 * 60 * 1000;
    private static final int TIME_MINUTES = 60 * 1000;

//    private static final double NEW_BID_PRICE_INCREASE = 0.5;

    public AuctionMonitorItemBean(ItemType item) {
        super(item);
        localHighBid = getCurrentPrice();

        // Instead of a meaningless initial price of 0.0,
        tempLocalBid = localHighBid;
    }

    public String getPicture() {
        try {
            imageUrl = getPictureURL().toString();
            if (imageUrl.startsWith("file:")) {
                imageUrl = imageUrl.substring(5);
            }
        } catch (NullPointerException e) {
            if (log.isWarnEnabled()) {
                log.warn("Failed to get the picture for an item because of " +
                         e);
            }
        }

        return (null == imageUrl ? NO_PHOTO_URL :
                imageUrl);
    }

    public Effect getEffect() {
        return effect;
    }

    public void setEffect(Effect effect) {
        this.effect = effect;
    }

    public void setCurrentPrice(double currentPrice) {
    }

    public double getCurrentPrice() {
        double newBid = getLocalBid();

        if ((oldBid > 0) && (newBid > oldBid)) {
            if (effect == null) {
                effect = new Highlight("#FFCC0B");
            }
            effect.setFired(false);
        }
        oldBid = newBid;

        return oldBid;
    }

    public void setBidCount(int bidCount) {
    }

    public int getBidCount() {
        try {
            localBidCount = Integer.parseInt(AuctionState.getAuctionMap()
                    .get(getItemID() + ".bidCount").toString());
        } catch (NullPointerException e) {
            if (log.isWarnEnabled()) {
                log.warn("Failed to get the bid count for an item because of " +
                         e);
            }
        }

        return (localBidCount);
    }

    public void setTempLocalBid(double tempLocalBid) {
        if (tempLocalBid <= MAX_BID && 
            tempLocalBid - localHighBid <= MAX_BID_INCREASE) {
            this.tempLocalBid = tempLocalBid;
            setLocalBid();
            bidMessage = false;
        }
        else{
            bidMessage = true;
        }
    }

    public double getTempLocalBid() {
        return tempLocalBid;
    }

    public String setLocalBid() {
        if (tempLocalBid > localHighBid &&
            tempLocalBid <= MAX_BID && 
            tempLocalBid - localHighBid <= MAX_BID_INCREASE) {
            localHighBid = tempLocalBid;
            bidMessage = false;
            Map auctionMap = AuctionState.getAuctionMap();
            auctionMap.put(getItemID() + ".price", new Double(localHighBid));
            auctionMap.put(getItemID() + ".bidCount", new Integer(
                    Integer.parseInt(auctionMap.get(getItemID() +".bidCount")
                        .toString()) + 1 ) );
            getCurrentPrice();
            SessionRenderer.render("auction");
        }
        else if (tempLocalBid <= localHighBid){
            bidMessage = false;
        }
        else {
            bidMessage = true;
        }
        return SUCCESS;
    }

    public String getBidMessage(){
        if (!bidMessage){
            return "";
        }
        else{
            return "<br />Bid declined.";
        }
    }

    public double getLocalBid() {
        try {
            localHighBid =
                    Double.parseDouble(
                            AuctionState.getAuctionMap().get(getItemID() +
                                                             ".price").toString());
        } catch (NullPointerException e) {
            if (log.isErrorEnabled()) {
                log.error("Error getting local bid:");
            }
        }

        return localHighBid;
    }

    public void setTimeLeft(long timeLeft) {
    }

    public long getTimeLeft() {
        Calendar endTimeCal = (Calendar) AuctionState.getAuctionMap()
                .get(getItemID() + ".endTime");
        long endMillis = endTimeCal.getTime().getTime();
        return (endMillis - Calendar.getInstance().getTime().getTime());
    }

    public long[] getTimeLeftBrokenDown() {
        long left, days, hours, minutes, seconds;
        left = getTimeLeft();
        days = left / TIME_DAYS;
        left = left - days * TIME_DAYS;
        hours = left / TIME_HOURS;
        left = left - hours * TIME_HOURS;
        minutes = left / TIME_MINUTES;
        left = left - minutes * TIME_MINUTES;
        seconds = left / 1000;
        return new long[]{days, hours, minutes, seconds};
    }

    public void setTimeLeftStyleClass(String timeLeftStyleClass) {
    }

    public String getTimeLeftStyleClass() {
        return timeLeftStyleClass;
    }

    public void setTimeImageUrl(String timeImageUrl) {
    }

    public String getTimeImageUrl() {
        timeLeftBrokenDown = getTimeLeftBrokenDown();
        String timeImageUrl;
        if (0 != timeLeftBrokenDown[0]) {
            timeImageUrl = TIME_LEFT_DAYS;
            timeLeftStyleClass = "timeCellDays";
        } else if (0 != timeLeftBrokenDown[1]) {
            timeImageUrl = TIME_LEFT_HOURS;
            timeLeftStyleClass = "timeCellHours";
        } else if (timeLeftBrokenDown[2] >= 30) {
            timeImageUrl = TIME_LEFT_60;
            timeLeftStyleClass = "timeCellMins";
        } else if (timeLeftBrokenDown[2] >= 15) {
            timeImageUrl = TIME_LEFT_30;
            timeLeftStyleClass = "timeCellMins";
        } else if (timeLeftBrokenDown[2] >= 10) {
            timeImageUrl = TIME_LEFT_15;
            timeLeftStyleClass = "timeCellMins";
        } else if (timeLeftBrokenDown[2] >= 5) {
            timeImageUrl = TIME_LEFT_10;
            timeLeftStyleClass = "timeCellMins";
        } else {
            timeImageUrl = TIME_LEFT_5;
            timeLeftStyleClass = "timeCellMins";
        }

        return timeImageUrl;
    }

    public void setTimeLeftString(String timeLeftString) {
    }

    public String getTimeLeftString() {
        if (getTimeLeft() < 0) {
            return " Expired";
        }

        StringBuffer buf = new StringBuffer();
        buf.append("  ");
        if (0 != timeLeftBrokenDown[0]) {
            buf.append(Long.toString(timeLeftBrokenDown[0]));
            buf.append("d ");
        }

        if (0 != timeLeftBrokenDown[1]) {
            buf.append(Long.toString(timeLeftBrokenDown[1]));
            buf.append(":");
            if (timeLeftBrokenDown[2] < 10) {
                buf.append("0");
            }
        }

        buf.append(Long.toString(timeLeftBrokenDown[2]));
        buf.append(":");

        if (timeLeftBrokenDown[3] < 10) {
            buf.append("0");
        }

        buf.append(Long.toString(timeLeftBrokenDown[3]));

        return buf.toString();
    }

    public void setExpandedStyleClass(String expandedStyleClass) {
    }

    public String getExpandedStyleClass() {
        if (expanded) {
            return STYLE_CLASS_EXPANDED_ROW;
        } else {
            return "";
        }
    }

    public String pressExpandButton() {
        expanded = !expanded;
        return SUCCESS;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public void setExpandTriangleImage(String expandTriangleImage) {
    }

    public String getExpandTriangleImage() {
        if (expanded) {
            return TRIANGLE_OPEN;
        } else {
            return TRIANGLE_CLOSED;
        }
    }

    public String pressBidButton() {
        bidExpanded = !bidExpanded;
        return SUCCESS;
    }

    public boolean isExpired() {
        if (getTimeLeft() < 0) {
            return (true);
        }
        return (false);
    }

    public boolean isBidExpanded() {
        return bidExpanded;
    }

    public void setBidExpanded(boolean bidExpanded) {
        this.bidExpanded = bidExpanded;
    }
}
