package org.icefaces.application.showcase.view.bean.examples.component.gmap;

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

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.icesoft.faces.component.gmap.GMapLatLng;

/**
 * <p>The GmapBean is responsible for setting up default map markers,
 * as well as the selecting of cities, searching for addresses, and toggling
 * the visibility of the map ui controls.</p>
 *
 * @since 1.7
 */
public class GmapBean  implements Serializable {
    // address to search for
	private String geoCoderAddress = "";
    // city location selected from a preset list
	private String standardAddress = "";
    // whether we should search for an address or not
	private boolean locateAddress = false;
	private List points = new ArrayList();
	private boolean showControls = true;
	private boolean showMarkers = true;
    // value bound to the gmap component
	private String address = "";
	
	public GmapBean() {
        // Generate a set of default map marker locations
		points.add(new GMapLatLng("37.379434", "-121.92293"));
		points.add(new GMapLatLng("33.845449", "-84.368682"));
		points.add(new GMapLatLng("34.05333", "-118.24499"));
		points.add(new GMapLatLng("33.072694", "-97.06234"));
        points.add(new GMapLatLng("37.391278", "-121.952451"));
	}
	
	public String getStandardAddress() {
		return standardAddress;
	}

	public void setStandardAddress(String standardAddress) {	
		this.standardAddress = standardAddress;
		this.address = standardAddress;
	}
	
	public List getPoints() {
		return points;
	}

	public void setPoints(List points) {
		this.points = points;
	}

    public String getGeoCoderAddress() {
        return geoCoderAddress;
    }

	public void setGeoCoderAddress(String geoCoderAddress) {
		this.geoCoderAddress = geoCoderAddress;
	}
	
    public boolean isShowControls() {
        return showControls;
    }
    
    public void setShowControls(boolean showControls) {
        this.showControls = showControls;
    }
  
    public boolean isShowMarkers() {
        return showMarkers;
    }
    
    public void setShowMarkers(boolean showMarkers) {
        this.showMarkers = showMarkers;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    /**
     * Method called when the actionListener is fired on the locate address field
     *
     *@param event of the key press
     */
	public void enterKeyPressed(ActionEvent event) {
	  address = geoCoderAddress;
      locateAddress = true;
	}
	
    /**
     * Method to determine if we should use a preset address or search for an
     *  address
     *
     *@return true to locate an address, false otherwise
     */
	public boolean isLocateAddress() {
		if (locateAddress) {
			locateAddress = false;
			return true;
		}
		return false;
	}
	
    /**
     * Method called when we should search the map for an address
     * This happens when the preset location list is modified
     *
     *@param event of the change
     */
	public void findAddress(ValueChangeEvent event) {
		locateAddress = true;
	}
}
