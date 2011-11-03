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

import com.icesoft.applications.faces.auctionMonitor.AuctionState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

/**
 * The original concept of the Auction Monitor demo relied on the ebay SDK to
 * retrieve "live" auction data.  From the standpoint of a demonstration app,
 * the requirement to have all the proper ebay jars as well as a valid
 * development token became a problem.  Instead, we've stubbed out the minimum
 * required set of classes and methods and we fake the API calls by simply
 * reading in a property file and populating the required data classes.  In this
 * way, we make it easier to switch over to the real ebay SDK libraries as
 * required
 */
public class StubServer {
    // Variables
    private ItemType[] itemList = null;
    private static StubServer ourInstance = new StubServer();
    private static Log log = LogFactory.getLog(StubServer.class);

    // Property variables
    public static final String ITEM_PROPERTIES_RESOURCE =
            "com/icesoft/applications/faces/auctionMonitor/stubs/auction.properties";
    private static final String ITEM = "item";
    private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
    private static final String ID = "id";
    private static final String END_TIME = "endTime";
    private static final String DESCRIPTION = "description";
    private static final String IMAGE = "image";
    private static final String BID_COUNT = "bidCount";
    private static final String INITIAL_BID_COUNT = "initialBidCount";
    private static final String CURRENCY = "currency";
    private static final String PRICE = "price";
    private static final String INITIAL_PRICE = "initialPrice";
    private static final String SITE = "site";
    private static final String TITLE = "title";
    private static final String LOCATION = "location";
    private static final String SELLER = "seller";
    private static final String EXPIRESINDAYS = "expiresindays";

    /**
     * Private constructor to fulfill singleton requirements
     */
    private StubServer() {
        if (AuctionState.getAuctionMap().isEmpty()) {
            loadItemList();
        }
    }

    public static synchronized StubServer getInstance() {
        return ourInstance;
    }

    /**
     * Method used to find and read an item properties file The item values will
     * then be loaded into the global auction state In the getSearchResults
     * method, these values will be converted into a list of ItemTypes
     */
    private void loadItemList() {
        // Determine the location of the property file
        Properties props = new Properties();
        try {
            ClassLoader cl = this.getClass().getClassLoader();
            props = new Properties();
            props.load(cl.getResourceAsStream(ITEM_PROPERTIES_RESOURCE));
        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error("Property file \'" + ITEM_PROPERTIES_RESOURCE +
                          "\' could not be found because of " + e);
            }
        }

        // Loop through each property and put the values in the global auction state
        int itemCounter = 0;
        String itemPrefix, idValue, key;
        while (true) {
            itemPrefix = ITEM + itemCounter + ".";
            idValue = props.getProperty(itemPrefix + ID);
            key = idValue + ".";

            // No more item sets were found, so break the loop
            if (idValue == null) {
                break;
            }

            // Add the core values for the current item
            AuctionState.getAuctionMap().put(key + ID, idValue);
            AuctionState.getAuctionMap().put(key + BID_COUNT, new Integer(
                    props.getProperty(itemPrefix + BID_COUNT)));
            AuctionState.getAuctionMap().put(key + INITIAL_BID_COUNT,
                                             new Integer(props.getProperty(
                                                     itemPrefix + BID_COUNT)));
            AuctionState.getAuctionMap().put(key + CURRENCY, props.getProperty(
                    itemPrefix + CURRENCY));
            AuctionState.getAuctionMap().put(key + DESCRIPTION,
                                             props.getProperty(
                                                     itemPrefix + DESCRIPTION));
            AuctionState.getAuctionMap()
                    .put(key + IMAGE, props.getProperty(itemPrefix + IMAGE));
            AuctionState.getAuctionMap().put(key + LOCATION, props.getProperty(
                    itemPrefix + LOCATION));
            AuctionState.getAuctionMap().put(key + PRICE, new Double(
                    props.getProperty(itemPrefix + PRICE)));
            AuctionState.getAuctionMap().put(key + INITIAL_PRICE, new Double(
                    props.getProperty(itemPrefix + PRICE)));
            AuctionState.getAuctionMap()
                    .put(key + SITE, props.getProperty(itemPrefix + SITE));
            AuctionState.getAuctionMap()
                    .put(key + SELLER, props.getProperty(itemPrefix + SELLER));
            AuctionState.getAuctionMap()
                    .put(key + TITLE, props.getProperty(itemPrefix + TITLE));
            AuctionState.getAuctionMap().put(key + EXPIRESINDAYS,
                                             props.getProperty(itemPrefix +
                                                               EXPIRESINDAYS));

            // Calculate and add the expiry date
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, Integer.parseInt((String) AuctionState
                    .getAuctionMap().get(key + EXPIRESINDAYS)));

            AuctionState.getAuctionMap().put(key + END_TIME, calendar);

            // Update the running total of number of items
            itemCounter++;
        }
    }

    /**
     * Method to convert the global properties file values into ItemTypes
     * Ideally this method would use the Ebay SDK, but for now will "fake it"
     * with the property file
     *
     * @return ItemType[] resulting list of auction items
     */
    public ItemType[] getSearchResults() {
        // Ensure that the item list is only read once
        if (itemList == null) {
            ItemType it;
            ArrayList items = new ArrayList();
            String bidId, prefix;

            // Loop until no new properties remain
            Iterator keys = AuctionState.getAuctionMap().keySet().iterator();
            while (keys.hasNext()) {
                bidId = keys.next().toString();

                // Ensure a valid ID is present before using this property
                if (bidId.indexOf(".id") > 0) {
                    // Break down the bid ID into a usable value
                    bidId = bidId.substring(0, bidId.indexOf(".id"));
                    prefix = bidId + ".";

                    // Create a new ItemType and start to populate it with the required values
                    it = new ItemType();

                    // Set the ID
                    it.setItemID(bidId);

                    // Set the title
                    it.setTitle(AuctionState.getAuctionMap()
                            .get(prefix + TITLE).toString());

                    // Set the end time
                    it.setEndTimeCal((Calendar) AuctionState.getAuctionMap()
                            .get(prefix + END_TIME));

                    // Set the description
                    it.setDescription(AuctionState.getAuctionMap()
                            .get(prefix + DESCRIPTION).toString());

                    // Set the location
                    it.setLocation(AuctionState.getAuctionMap()
                            .get(prefix + LOCATION).toString());

                    // Set the seller
                    it.setSeller(AuctionState.getAuctionMap()
                            .get(prefix + SELLER).toString());

                    // Set the picture
                    try {
                        it.setPictureURL(new URL(AuctionState.getAuctionMap()
                                .get(prefix + IMAGE).toString()));
                    } catch (MalformedURLException e) {
                        if (log.isWarnEnabled()) {
                            log.warn("Malformed picture URL because of " + e);
                        }
                    }

                    // Add the populated item to the available list
                    items.add(it);
                }
            }

            // Convert the results to a simple array and store them
            itemList = (ItemType[]) items.toArray(new ItemType[items.size()]);
        }

        return (itemList);
    }

    /**
     * Convenience method to get a formatted calendar based on the passed date
     * time string
     *
     * @param dateTimeValue date time to use as a base
     * @return Calendar based on passed String
     * @throws ParseException on invalid date format
     */
    private Calendar getCalendar(String dateTimeValue) throws ParseException {
        SimpleDateFormat parser = new SimpleDateFormat(DATE_FORMAT);
        Calendar cal = Calendar.getInstance();
        Date parsedDate = parser.parse(dateTimeValue);
        cal.setTime(parsedDate);

        return cal;
    }

    /**
     * Method to retrieve a single ItemType with a matching itemID
     *
     * @param itemID itemID to match
     * @return ItemType matching item, or null if not found
     */
    public ItemType getItem(String itemID) {
        // Get the whole list of items
        ItemType[] items = getSearchResults();
        ItemType item;

        // Loop through the list of items looking for a matching ID 
        for (int index = 0; index < items.length; index++) {
            item = items[index];
            if (itemID.equals(item.getItemID())) {
                return item;
            }
        }

        return null;
    }

    /**
     * Method to reset the auction state This will perform 4 actions on each
     * item: reset price, reset bid, reset expiry days, reset end time
     */
    public static void resetAuction() {
        Calendar calendar;
        String endDate;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String bidId, prefix;
        Iterator keys = AuctionState.getAuctionMap().keySet().iterator();

        // Loop through the global list of available IDs
        while (keys.hasNext()) {
            bidId = keys.next().toString();
            if (bidId.indexOf(".id") > 0) {
                bidId = bidId.substring(0, bidId.indexOf(".id"));
                prefix = bidId + ".";

                // Reset the price and bid count of the current item
                AuctionState.getAuctionMap().put(prefix + PRICE, AuctionState
                        .getAuctionMap().get(prefix + INITIAL_PRICE));
                AuctionState.getAuctionMap().put(prefix + BID_COUNT,
                                                 AuctionState
                                                         .getAuctionMap().get(
                                                         prefix +
                                                         INITIAL_BID_COUNT));

                // Reset the number of expiry days
                calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, Integer.parseInt(AuctionState
                        .getAuctionMap()
                        .get(prefix + EXPIRESINDAYS).toString()));

                // Reset the end time
                AuctionState.getAuctionMap().put(prefix + END_TIME, calendar);
            }
        }
    }
}
