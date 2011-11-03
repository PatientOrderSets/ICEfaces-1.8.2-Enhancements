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

package com.icesoft.applications.faces.auctionMonitor;

import com.icesoft.applications.faces.auctionMonitor.beans.AuctionBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Hashtable;

/**
 * Class used to handle an auction item that is added to the current auction
 * monitor list This class will also handle parsing an items file into a usable
 * item list
 */
public class AddAuctionItem extends AuctionBean {
    private static Log log = LogFactory.getLog(AddAuctionItem.class);

    public static Hashtable parseFile(Reader reader) {
        BufferedReader in = new BufferedReader(reader);
        Hashtable params = new Hashtable();
        String line;
        try {
            String name;
            String value;
            while (null != (line = in.readLine())) {
                int index = line.indexOf(' ');
                name = line.substring(0, index);
                value = line.substring(index + 1);
                params.put(name, value);
            }
        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error(
                        "Error while parsing an auction item file into the hashtable");
            }
        }
        return params;
    }

    public static String getPictureName(String fileName) {
        int index = fileName.lastIndexOf('.');
        return (fileName.substring(0, index) + ".jpg");
    }

}
