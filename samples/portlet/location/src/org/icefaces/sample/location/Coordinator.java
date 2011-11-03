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
package org.icefaces.sample.location;

import com.icesoft.faces.async.render.SessionRenderer;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletSession;

public class Coordinator {

    private CityDictionary cityDictionary;
    private String groupName;

    private static final String CURRENT_CITY_KEY = "org.icefaces.location.current.city";
    private static final String LOCATION_UPDATED_KEY = "org.icefaces.location.updated";

    public Coordinator() {
    }

    public CityDictionary getCityDictionary() {
        return cityDictionary;
    }

    public void setCityDictionary(CityDictionary cityDictionary) {
        this.cityDictionary = cityDictionary;
    }

    public City getCurrentCity() {
        return (City)getApplicationAttribute(CURRENT_CITY_KEY);
    }

    public void setCurrentCity(City currentCity) {
        if (groupName == null) {
            groupName = getSessionID();
            SessionRenderer.addCurrentSession(groupName);
        }
        setApplicationAttribute(CURRENT_CITY_KEY,currentCity);
        setApplicationAttribute(LOCATION_UPDATED_KEY,Boolean.TRUE);
        SessionRenderer.render(groupName);
    }

    public boolean isAddressUpdated() {
        Object att = getApplicationAttribute(LOCATION_UPDATED_KEY);
        if( att == null ){
            return false;
        }
        return ((Boolean)att).booleanValue();
    }

    public void setAddressUpdated(boolean addressUpdated) {
        if(addressUpdated){
            setApplicationAttribute(LOCATION_UPDATED_KEY,Boolean.TRUE);
        } else {
            setApplicationAttribute(LOCATION_UPDATED_KEY,Boolean.FALSE);
        }
    }

    private Object getApplicationAttribute(String name) {
        return getPortletSession().getAttribute(name, PortletSession.APPLICATION_SCOPE);
    }

    private void setApplicationAttribute(String name, Object val) {
        getPortletSession().setAttribute(name, val, PortletSession.APPLICATION_SCOPE);
    }

    private String getSessionID() {
        return getPortletSession().getId();
    }

    private PortletSession getPortletSession() {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        Object sessObj = ec.getSession(false);
        if (sessObj != null && sessObj instanceof PortletSession) {
            return (PortletSession) sessObj;
        }
        return null;
    }

}
