package com.icesoft.faces.component.gmap;

import java.io.IOException;

import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

public class GMapLatLng extends UIPanel{
	public static final String COMPONENT_TYPE = "com.icesoft.faces.GMapLatLng";
    public static final String COMPONENT_FAMILY = "com.icesoft.faces.GMapLatLng";

    private String longitude;
    private String latitude;
    private String localeLng;
    private String localeLat;
    public GMapLatLng() {
		setRendererType(null);
	}
	
	public GMapLatLng(String latitude, String longitude) {
		this();
		this.latitude = latitude;
		this.longitude = longitude;
	}

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getComponentType() {
        return COMPONENT_TYPE;
    }
    
    public void encodeBegin(FacesContext context) throws IOException {
    	setRendererType(null);
        super.encodeBegin(context);    	
    	generateLatLngScript();
    }
    
	public String getLongitude() {
	       if (longitude != null) {
	            return longitude;
	        }
	        ValueBinding vb = getValueBinding("longitude");
	        return vb != null ? (String) vb.getValue(getFacesContext()) : "-122.1419";
		}

		public String getLatitude() {
	       if (latitude != null) {
	            return latitude;
	        }
	        ValueBinding vb = getValueBinding("latitude");
	        return vb != null ? (String) vb.getValue(getFacesContext()) : "37.4419";
		}

		public  void setLongitude(String longitude) {
			this.longitude = longitude;
		}

		public  void setLatitude(String latitude) {
			this.latitude = latitude;
		}

		public void generateLatLngScript() {
            String currentLat = getLatitude();
            String currentLng = getLongitude();
            String changed = "";
            if (localeLat != null && !localeLat.equals(currentLat)) {
                changed = "changed";
            }
            if (localeLng != null && !localeLng.equals(currentLng)) {
                changed = "changed";
            }
            localeLat = currentLat;
            localeLng = currentLng;
			String script = "new GLatLng("+ getLatitude() + ","+ getLongitude() +")"+ changed;
			getAttributes().put("latLngScript", script);
		}

    private transient Object values[];
    public void restoreState(FacesContext context, Object state) {

        values = (Object[])state;
        super.restoreState(context, values[0]);
        latitude = (String)values[1];
        longitude = (String)values[2];
        localeLat = (String)values[3];
        localeLng = (String)values[4];
    }

    public Object saveState(FacesContext context) {

        if(values == null){
            values = new Object[5];
        }
        values[0] = super.saveState(context);
        values[1] = latitude;
        values[2] = longitude;
        values[3] = localeLat;
        values[4] = localeLng;        
        return values;
    }

}
