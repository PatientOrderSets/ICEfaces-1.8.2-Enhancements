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
package com.icesoft.applications.faces.address;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Converts a CSV address list into an file of XML encoded objects
 * (XAddressDataWrapper) using the JavaBeans utilities.
 *
 * @see XAddressDataWrapper, MatchAddressDB
 */
public class XWrapperUtil {

    private static final String CSV_ADDRESS_DB = "address.db";
    private static final String XML_GZ_ADDRESS_DB = "address.xml";

    //logger
    private static final Log log = LogFactory.getLog(XWrapperUtil.class);

    public static void main(String[] args) {

        if (log.isDebugEnabled()) {
            log.debug("Converting database...");
        }

        //load the CSV file
        InputStream is =
                MatchAddressDB.class.getResourceAsStream(CSV_ADDRESS_DB);
        BufferedReader buff = new BufferedReader(new InputStreamReader(is));

        XMLEncoder xEncode = null;
        XAddressDataWrapper xData;

        //open the XML encoder and attempt to write the xml file
        try {
            xEncode = new XMLEncoder(new BufferedOutputStream(
                    new FileOutputStream(XML_GZ_ADDRESS_DB)));
        } catch (FileNotFoundException ex) {
            log.error("Database could not be written.", ex);
        }

        //first line
        char[] line = getNextLine(buff);

        while (line != null) {

            //get three strings within quotes
            String addressValues[] = new String[3];
            int stringValueStart = 0, stringValueEnd;

            for (int i = 0; i < 3; i++) {
                //opening quote
                while (line[stringValueStart++] != '\"') {
                }
                stringValueEnd = stringValueStart + 1;
                //closing quote
                while (line[stringValueEnd] != '\"') {
                    stringValueEnd++;
                }
                //value
                addressValues[i] = new String(line, stringValueStart,
                                              stringValueEnd -
                                              stringValueStart);
                stringValueStart = stringValueEnd + 1;
            }

            //assign the data to the wrapper
            xData = new XAddressDataWrapper();
            xData.setCity(addressValues[1]);
            xData.setState(addressValues[2]);
            xData.setZip(addressValues[0]);

            //read the next line (entry) in the CSV file
            line = getNextLine(buff);
        }
        //close the XML Encoder
        try {
            xEncode.close();
        }
        catch (NullPointerException npe) {
            log.error("Could not close XML Encoder.", npe);
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("Closed XML Encoder.");
        }
    }


    /**
     * Reads the next line in the CSV file.
     *
     * @param buff the CSV file
     * @return the next line
     */
    private static char[] getNextLine(BufferedReader buff) {
        String inputLine = null;

        //see if the next line exists
        try {
            inputLine = buff.readLine();
        } catch (IOException e) {
            System.err.println("MatchAddressDB.getNextLine(): " +
                               "error reading address database \n " + e);
        }
        //if the next line exists, return it as a char[]
        if (inputLine == null) {
            return null;
        } else {
            return inputLine.toCharArray();
        }
    }
}
