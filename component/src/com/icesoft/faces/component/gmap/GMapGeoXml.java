package com.icesoft.faces.component.gmap;

import java.io.IOException;

import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.icesoft.faces.context.effects.JavascriptContext;

public class GMapGeoXml extends UIPanel{
	public static final String COMPONENT_TYPE = "com.icesoft.faces.GMapGeoXml";
    public static final String COMPONENT_FAMILY = "com.icesoft.faces.GMapGeoXml";
	private String url;
	
	public GMapGeoXml() {
		setRendererType(null);
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
        if (!isRendered()) {
            JavascriptContext.addJavascriptCall(context, "Ice.GoogleMap.removeOverlay('"+ this.getParent().getClientId(context)+"', '"+ this.getClientId(context) +"');");
        } else {
            JavascriptContext.addJavascriptCall(context, "Ice.GoogleMap.addOverlay('"+ this.getParent().getClientId(context)+"', '"+ this.getClientId(context) +"', 'new GGeoXml(\""+ getUrl() +"\")');");
        }
    }

	public String getUrl() {
       if (url != null) {
            return url;
        }
        ValueBinding vb = getValueBinding("url");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
	}

	public void setUrl(String url) {
		this.url = url;
	}

    private transient Object values[];
    public void restoreState(FacesContext context, Object state) {
        values = (Object[])state;
        super.restoreState(context, values[0]);
        url = (String)values[1];
    }

    public Object saveState(FacesContext context) {

        if(values == null){
            values = new Object[2];
        }
        values[0] = super.saveState(context);
        values[1] = url;
        return values;
    }
        
        
    
}
