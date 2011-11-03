package com.icesoft.faces.component.gmap;

import java.io.IOException;

import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import org.w3c.dom.Element;

import com.icesoft.faces.application.D2DViewHandler;
import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.context.effects.JavascriptContext;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;

public class GMapDirection extends UIPanel{
	public static final String COMPONENT_TYPE = "com.icesoft.faces.GMapDirection";
    public static final String COMPONENT_FAMILY = "com.icesoft.faces.GMapDirection";
	private Boolean locateAddress;
    private boolean initilized = false;
    private String from;
    private String to;
    private String textualDivId;
    private String textualDivClientId;
    private String locale;
    
	
	public GMapDirection() {
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
    	String textualDivId = getTextualDivId(); 
		GMap gmap = (GMap)this.getParent();
    	String mapId = gmap.getClientId(context);
    	if (textualDivId == null) {
    		//user didn't defined the textual div, so create one

    		textualDivId = mapId + "textualDiv";
    		DOMContext domContext = DOMContext.getDOMContext(context, gmap);

    		Element texttualTd =  (Element) domContext.createElement(HTML.TD_ELEM);
    		texttualTd.setAttribute(HTML.CLASS_ATTR, gmap.getTxtTdStyleClass());
    		Element textualDiv = (Element) domContext.createElement(HTML.DIV_ELEM);
    		textualDiv.setAttribute(HTML.STYLE_ATTR, "width:300px;");
    		textualDiv.setAttribute(HTML.ID_ATTR, textualDivId );
    		texttualTd.setAttribute("VALIGN", "top");
    		texttualTd.appendChild(textualDiv);
    		domContext.getRootNode().getFirstChild().appendChild(texttualTd);
    	} else {
    		if (textualDivClientId == null) {
    			textualDivClientId = getClientIdOfTextualDiv(context);
    		}
    	}
    	
    	String query = "";
    	String from = getFrom();
    	String to = getTo();
    	if((isLocateAddress() || !initilized)) {
    		 if (from != null && from.length() > 2) {
    			 query = "from: "+ from + " ";
    		 }
    		 if (to != null && to.length() > 2) {
    			 query += "to: "+ to;
    		 }
    		 if (query.length() > 2 ) {
    			 JavascriptContext.addJavascriptCall(context, 
    					 "Ice.GoogleMap.loadDirection('"+ mapId +"', '"+ 
    					 textualDivClientId +"', '"+ query +"');");
    		 }
    		 initilized = true;    		
    	}
    }

	public boolean isLocateAddress() {
        if (locateAddress != null) {
            return locateAddress.booleanValue();
        }
        ValueBinding vb = getValueBinding("locateAddress");
        return vb != null ?
               ((Boolean) vb.getValue(getFacesContext())).booleanValue() :
               false;
	}

	public void setLocateAddress(boolean locateAddress) {
		this.locateAddress = new Boolean(locateAddress);
	}

	public String getFrom() {
	       if (from != null) {
	            return from;
	        }
	        ValueBinding vb = getValueBinding("from");
	        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
       if (to != null) {
            return to;
        }
        ValueBinding vb = getValueBinding("to");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getTextualDivId() {
       if (textualDivId != null) {
            return textualDivId;
        }
        ValueBinding vb = getValueBinding("textualDivId");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
	}

	public void setTextualDivId(String textualDivId) {
		this.textualDivId = textualDivId;
	}

	public String getLocale() {
	       if (locale != null) {
	            return locale;
	        }
	        ValueBinding vb = getValueBinding("locale");
	        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}
	
	public String getClientIdOfTextualDiv(FacesContext context) {
		String forString = getTextualDivId();
		UIComponent forComponent =  D2DViewHandler.findComponent(forString, this);
		String forClientId = forComponent.getClientId(context);
		return (forClientId.indexOf(':') > 1)? forClientId : null;
	}

    private transient Object values[];
    public void restoreState(FacesContext context, Object state) {
        values =  (Object[])state;
        super.restoreState(context, values[0]);
        to = (String)values[1];
        from = (String)values[2];
        textualDivId = (String)values[3];
        locale = (String)values[4];
        locateAddress = (Boolean)values[5];
        textualDivClientId = (String)values[6];
        initilized = ((Boolean) values[7]).booleanValue();
    }

    public Object saveState(FacesContext context) {
        if(values == null){
            values = new Object[8];
        }
        values[0] = super.saveState(context);
        values[1] = to;
        values[2] = from;
        values[3] = textualDivId;
        values[4] = locale;
        values[5] = locateAddress;  
        values[6] = textualDivClientId;
        values[7] = Boolean.valueOf(initilized);
        return values;
    }


}
