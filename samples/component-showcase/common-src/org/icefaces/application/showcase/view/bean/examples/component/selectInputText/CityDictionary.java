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
package org.icefaces.application.showcase.view.bean.examples.component.selectInputText;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.icefaces.application.showcase.util.MessageBundleLoader;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.beans.XMLDecoder;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * <p>Application-scope bean used to store static lookup information for
 * AutoComplete (selectInputText) example. Statically referenced by
 * SelectInputTextBean as the cityDictionary is rather large.</p>
 * <p>This class loads the city data from an xml data set.  Once an instance
 * is created a call to #generateCityMatches will generate a list of potential
 * matches. </p>
 *
 * @since 1.7
 */
public class CityDictionary implements Serializable {

    private static Log log = LogFactory.getLog(CityDictionary.class);

    // initialized flag, only occures once ber deployment. 
    private static boolean initialized;

    // list of cities.
    private static ArrayList cityDictionary;

    private static final String DATA_RESOURCE_PATH =
            "/WEB-INF/classes/org/icefaces/application/showcase/view/resources/city.xml.zip";

    /**
     * Creates a new instnace of CityDictionary.  The city dictionary is unpacked
     * and initialized during construction.  This will result in a short delay
     * of 2-3 seconds on the server as a result of processing the large file.
     */
    public CityDictionary() {

        try {
            log.info(MessageBundleLoader.getMessage(
                    "bean.selectInputText.info.initializingDictionary"));

            // initialized the bean, load xml database.
            synchronized (this) {
                init();
            }

        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(MessageBundleLoader.getMessage(
                        "bean.selectInputText.error.initializingDictionary"));
            }
        }
    }

    /**
     * Comparator utility for sorting city names.
     */
    private static final Comparator LABEL_COMPARATOR = new Comparator() {

        // compare method for city entries.
        public int compare(Object o1, Object o2) {
            SelectItem selectItem1 = (SelectItem) o1;
            SelectItem selectItem2 = (SelectItem) o2;
            // compare ignoring case, give the user a more automated feel when typing
            return selectItem1.getLabel().compareToIgnoreCase(selectItem2.getLabel());
        }
    };

    /**
     * Gets the cityDictionary of cities.
     *
     * @return cityDictionary list in sorted by city name, ascending.
     */
    public List getDictionary() {
        return cityDictionary;
    }

    /**
     * Generates a short list of cities that match the given searchWord.  The
     * length of the list is specified by the maxMatches attribute.
     *
     * @param searchWord city name to search for
     * @param maxMatches max number of possibilities to return
     * @return list of SelectItem objects which contain potential city names.
     */
    public ArrayList generateCityMatches(String searchWord, int maxMatches) {

        ArrayList matchList = new ArrayList(maxMatches);

        // ensure the autocomplete search word is present
        if ((searchWord == null) || (searchWord.trim().length() == 0)) {
            return matchList;
        }

        try {
            SelectItem searchItem = new SelectItem("", searchWord); 
            int insert = Collections.binarySearch(
                    cityDictionary,
                    searchItem,
                    LABEL_COMPARATOR);

            // less then zero if we have a partial match
            if (insert < 0) {
                insert = Math.abs(insert) - 1;
            }
            else {
                // If there are duplicates in a list, ensure we start from the first one
                if(insert != cityDictionary.size() && LABEL_COMPARATOR.compare(searchItem, cityDictionary.get(insert)) == 0) {
                    while(insert > 0 && LABEL_COMPARATOR.compare(searchItem, cityDictionary.get(insert-1)) == 0) {
                        insert = insert - 1;
                    }
                }
            }
            
            for (int i = 0; i < maxMatches; i++) {
                // quit the match list creation if the index is larger than
                // max entries in the cityDictionary if we have added maxMatches.
                if ((insert + i) >= cityDictionary.size() ||
                        i >= maxMatches) {
                    break;
                }
                matchList.add(cityDictionary.get(insert + i));
            }
        } catch (Throwable e) {
            log.error(MessageBundleLoader.getMessage(
                    "bean.selectInputText.error.findingMatches"), e);
        }
        // assign new matchList
        return matchList;
    }

    /**
     * Reads the zipped xml city cityDictionary and loads it into memory.
     */
    private static void init() throws IOException {

        if (!initialized) {

            initialized = true;

            // Loading of the resource must be done the "JSF way" so that
            // it is agnostic about it's environment (portlet vs servlet).
            // First we get the resource as an InputStream
            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();
            InputStream is = ec.getResourceAsStream(DATA_RESOURCE_PATH);

            //Wrap the InputStream as a ZipInputStream since it
            //is a zip file.
            ZipInputStream zipStream = new ZipInputStream(is);

            //Prime the stream by reading the first entry.  The way
            //we have it currently configured, there should only be
            //one.
            ZipEntry firstEntry = zipStream.getNextEntry();

            //Pass the ZipInputStream to the XMLDecoder so that it
            //can read in the list of cities and associated data.
            XMLDecoder xDecoder = new XMLDecoder(zipStream);
            List cityList = (List) xDecoder.readObject();

            //Close the decoder and the stream.
            xDecoder.close();
            zipStream.close();

            if (cityList == null) {
                throw new IOException();
            }

            cityDictionary = new ArrayList(cityList.size());
            City tmpCity;
            for (int i = 0, max = cityList.size(); i < max; i++) {
                tmpCity = (City) cityList.get(i);
                if (tmpCity != null && tmpCity.getCity() != null) {
                    cityDictionary.add(new SelectItem(tmpCity, tmpCity.getCity()));
                }
            }
            cityList.clear();

            Collections.sort(cityDictionary, LABEL_COMPARATOR);
        }

    }
}
