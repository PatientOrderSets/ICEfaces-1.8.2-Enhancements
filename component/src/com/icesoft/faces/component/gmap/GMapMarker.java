package com.icesoft.faces.component.gmap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.icesoft.faces.context.effects.JavascriptContext;

public class GMapMarker extends UIPanel{
	public static final String COMPONENT_TYPE = "com.icesoft.faces.GMapMarker";
    public static final String COMPONENT_FAMILY = "com.icesoft.faces.GMapMarker";
    
	private Boolean draggable;
    private String longitude;
    private String latitude;
    private transient String oldLongitude;
    private transient String oldLatitude;    
    private List point = new ArrayList();
    
	public GMapMarker() {
		setRendererType(null);
	}
	 
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getComponentType() {
        return COMPONENT_TYPE;
    }
    
    public boolean getRendersChildren() {
    	return true;
    }
    public void encodeBegin(FacesContext context) throws IOException {
    	setRendererType(null);
        super.encodeBegin(context);    	
    	String currentLat = getLatitude();
    	String currentLon = getLongitude();
    	//create a marker if lat and lon defined on the component itself
    	if (currentLat != null &&  currentLon != null 
    	        && currentLat.length() > 0 && currentLon.length() > 0) {
    	    if (!currentLat.equals(oldLatitude) || 
    	            !currentLon.equals(oldLongitude)) {
    	        //to dynamic support first to remove if any
                JavascriptContext.addJavascriptCall(context, 
                        "Ice.GoogleMap.removeOverlay('"+ this.getParent()
                        .getClientId(context)+"', '"+ getClientId(context)+"');"); 
                JavascriptContext.addJavascriptCall(context, "Ice.GoogleMap." +
                        "addOverlay('"+ this.getParent().getClientId(context)+
                        "', '"+ getClientId(context)+"', " +
                      "'new GMarker(new GLatLng("+ currentLat+","+ currentLon +"))');");                
                
    	    }
    	    oldLatitude = currentLat;
    	    oldLongitude = currentLon;
    	}
    }
    
    public void encodeChildren(FacesContext context) throws IOException {
         if (getChildCount() == 0 )return;
	     Iterator kids = getChildren().iterator();
	     while (kids.hasNext()) {
		    UIComponent kid = (UIComponent) kids.next();
  
		    	kid.encodeBegin(context);
			    if (kid.getRendersChildren()) {
			    	kid.encodeChildren(context);
			    }
			    kid.encodeEnd(context);
			    if (kid instanceof GMapLatLng) {
			    	String call = kid.getAttributes().get("latLngScript").toString();
                    //if dynamically changed then remove the previous one
			    	if (call.endsWith("changed") || !kid.isRendered() || !isRendered()) {
			    	    call = call.substring(0, call.length() - "changed".length());
			    	    JavascriptContext.addJavascriptCall(context, 
			    	            "Ice.GoogleMap.removeOverlay('"+ this.getParent()
			    	            .getClientId(context)+"', '"+ kid.getClientId(context)+"');");
			    	} 
			    	if (!kid.isRendered() || !isRendered()) continue;
			    	JavascriptContext.addJavascriptCall(context, "Ice.GoogleMap." +
			    			"addOverlay('"+ this.getParent().getClientId(context)+
			    			"', '"+ kid.getClientId(context)+"', 'new GMarker("+ call +")');");
			    } else if(kid instanceof GMapLatLngs) {
			        //The list of GMapLatLngs can be dynamic so first remove previously 
			        //added markers
			        Iterator it = point.iterator();
			        while (it.hasNext()) {
			            JavascriptContext.addJavascriptCall(context, "Ice.GoogleMap." +
			            		"removeOverlay('"+ this.getParent()
			            		.getClientId(context)+"', '"+ it.next() +"');");		            
			        }
			        point.clear();
			        if (!kid.isRendered() || !isRendered()) continue;
			        //now add the fresh list of the markers
			        StringTokenizer st = new StringTokenizer(kid.getAttributes()
			                .get("latLngsScript").toString(), ";");
			    	while(st.hasMoreTokens()) {
			    		String[] scriptInfo =st.nextToken().split("kid-id");
			    		String call = scriptInfo[0];
			    		String latLngId = scriptInfo[1];
			    		point.add(latLngId);
			    		JavascriptContext.addJavascriptCall(context, "Ice.GoogleMap." +
			    				"addOverlay('"+ this.getParent().getClientId(context)+
			    				"', '"+ latLngId +"', 'new GMarker("+ call +")');");
			    	}
			    }
	     }
    }
    
    
	public boolean isDraggable() {
        if (draggable != null) {
            return draggable.booleanValue();
        }
        ValueBinding vb = getValueBinding("draggable");
        return vb != null ?
               ((Boolean) vb.getValue(getFacesContext())).booleanValue() :
               false;
	}

	public void setDraggable(boolean draggable) {
		this.draggable = new Boolean(draggable);
	}
	
	public String getLongitude() {
       if (longitude != null) {
            return longitude;
        }
        ValueBinding vb = getValueBinding("longitude");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
	}

	public String getLatitude() {
       if (latitude != null) {
            return latitude;
        }
        ValueBinding vb = getValueBinding("latitude");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

    private transient Object values[];
    public void restoreState(FacesContext context, Object state) {
        values = (Object[])state;
        super.restoreState(context, values[0]);
        latitude = (String)values[1];
        longitude = (String)values[2];
        draggable = (Boolean)values[3];        
        point = (List) values[4];        
    }

    public Object saveState(FacesContext context) {
        if(values == null){
            values = new Object[5];
        }
        values[0] = super.saveState(context);
        values[1] = latitude;
        values[2] = longitude;
        values[3] = draggable;        
        values[4] = point;        
        return values;
    }
    
    public boolean isRendered() {
        boolean rendered = super.isRendered();
        if (!rendered) {
            FacesContext context = getFacesContext();
            JavascriptContext.addJavascriptCall(context, 
                    "Ice.GoogleMap.removeOverlay('"+ this.getParent()
                    .getClientId(context)+"', '"+ getClientId(context)+"');");  
            oldLongitude = null;
            oldLatitude = null;
        }
        return rendered;
    }

}
