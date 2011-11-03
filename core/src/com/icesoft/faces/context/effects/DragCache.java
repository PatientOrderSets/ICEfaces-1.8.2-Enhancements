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

package com.icesoft.faces.context.effects;

import javax.faces.context.FacesContext;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Store values of Draggable and Droppables between requests
 *
 */
public class DragCache {
    private static Logger log = Logger.getLogger(DragCache.class.getName());

    private static final String CACHE_CLEARED =
            "ICESOFT_DRAG_CACHE_CLEARED_FLAG";
    private static final String CACHE_KEY = "ICESOFT_DRAG_CACHE_RGDM_FLAG";
    private static final String KEY_START = "ICEDRAGKEY[";
    private static final String KEY_END = "]ICEDRAGKEY";


    /**
     * Add a value to the cache
     * @param value
     * @param context
     * @return
     */
    public static String put(Object value, FacesContext context) {
        Map sessionMap = context.getExternalContext().getSessionMap();
        //TODO: Clear out when values are no longer needed. How can this be detected?
        if (sessionMap.get(CACHE_KEY) == null) {
            sessionMap.put(CACHE_KEY, new HashMap());
        }
        Map cache = (Map) sessionMap.get(CACHE_KEY);
        String key = KEY_START + cache.size() + KEY_END;
        cache.put(key, value);

        return key;
    }



    /**
     * Get a value from the cache
     * @param key
     * @param context
     * @return
     */
    public static Object get(String key, FacesContext context) {
        if (key == null) {
            throw new NullPointerException("Key can't be null");
        }
        key = key.trim();
        Map sessionMap = context.getExternalContext().getSessionMap();
        Map cache = (Map) sessionMap.get(CACHE_KEY);
        if (cache == null) {
            throw new IllegalStateException(
                    "No Drag component placed a value in the cache.");
        }
        Object o = cache.get(key);
        if (o == null) {
            log.log(Level.SEVERE, "DragCache: No value found for key [" + key + "]");
        }
        return cache.get(key);
    }

}
