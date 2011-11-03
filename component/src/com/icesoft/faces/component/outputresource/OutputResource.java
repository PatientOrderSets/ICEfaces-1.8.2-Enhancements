package com.icesoft.faces.component.outputresource;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;
import java.net.URLEncoder;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.ext.taglib.Util;
import com.icesoft.faces.context.FileResource;
import com.icesoft.faces.context.Resource;
import com.icesoft.faces.context.ResourceRegistry;
import com.icesoft.faces.webapp.http.core.ResourceDispatcher;

public class OutputResource extends UIComponentBase {

	public static final String COMPONENT_FAMILY = "com.icesoft.faces.OutputResource";
	public static final String COMPONENT_TYPE = "com.icesoft.faces.OutputResource";
	public static final String DEFAULT_RENDERER_TYPE = "com.icesoft.faces.OutputResourceRenderer";
    private static Log log = LogFactory.getLog(OutputResource.class);
	protected Resource resource;
	private String mimeType;
	private Date lastModified;
	private String fileName;
	private String image;
	private String type;
	private String label;
	private String style;
	private String styleClass;
	private String renderedOnUserRole;
	private Boolean attachment;
	private transient int lastResourceHashCode;	
	transient String path;
	private Boolean shared;
    private String target;

	public static final String TYPE_IMAGE = "image";
	public static final String TYPE_BUTTON = "button";

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.component.UIComponent#getFamily()
	 */
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public String getComponentType() {
		return COMPONENT_TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.component.UIComponent#getRendererType()
	 */
	public String getRendererType() {
		return DEFAULT_RENDERER_TYPE;
	}

	public Resource getResource() {
	    final Resource currResource;
	    if (this.resource != null) {
	        currResource = this.resource;
	    }else {
            ValueBinding vb = getValueBinding("resource");
    		if (vb == null) {
                addInfo();
    		    return null;
    		}
    		currResource = (Resource) vb.getValue(getFacesContext());
	    }
	    if (currResource == null) return null;
		final String fileName = getFileName();
        if( currResource != null ){
			int newResourceHashCode = currResource.hashCode();
			if( lastResourceHashCode != newResourceHashCode ){
				Resource r = new RegisteredResource(this, currResource, fileName);
				path = ((ResourceRegistry) FacesContext.getCurrentInstance()).registerResource(
						r).getRawPath();
			}
		}
		return currResource;
	}
	
    public void addInfo() {
        if (log.isInfoEnabled()) {
            log.info("The \"resource\" is not defined");
        }
    }

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public String getMimeType() {
        if (mimeType != null) {
			return mimeType;
		}
		ValueBinding vb = getValueBinding("mimeType");
		return vb != null ? (String) vb.getValue(getFacesContext()) : null;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		//must check fileName as valuebinding first and set local as 
		//ResourceDispatcher may not have access to FacesContext
		ValueBinding vb = getValueBinding("fileName");
		if( vb != null ){
			fileName = (String) vb.getValue(getFacesContext());
		}
		return fileName;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public Date getLastModified() {
		if (lastModified != null)
			return lastModified;
		ValueBinding vb = getValueBinding("lastModified");
		return vb != null ? (Date) vb.getValue(getFacesContext()) : null;
	}

	public void setImage(String img) {
		this.image = img;
	}

	public String getImage() {
		if (image != null) {
			return image;
		}
		ValueBinding vb = getValueBinding("image");
		return vb != null ? (String) vb.getValue(getFacesContext()) : null;

	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		if (type != null) {
			return type;
		}
		ValueBinding vb = getValueBinding("type");
		return vb != null ? (String) vb.getValue(getFacesContext()) : null;

	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		if (label != null) {
			return label;
		}
		ValueBinding vb = getValueBinding("label");
		if( vb != null )
			return (String) vb.getValue(getFacesContext());
		String fileName = getFileName();
		if( fileName != null && fileName.length() > 0 )
			return fileName;
		vb = getValueBinding("resource");
		Resource r = null;
		if( vb != null )
			r = (Resource) vb.getValue(getFacesContext());
		if( r != null && r instanceof FileResource )
			return ((FileResource)r).getFile().getName();
		return null;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getStyle() {
		if (style != null) {
			return style;
		}
		ValueBinding vb = getValueBinding("style");
		return vb != null ? (String) vb.getValue(getFacesContext()) : null;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	/**
	 * <p>
	 * Return the value of the <code>styleClass</code> property.
	 * </p>
	 * 
	 * @return String styleClass
	 */
	public String getStyleClass() {
		String defaultStyle = CSS_DEFAULT.OUTPUT_LINK_DEFAULT_STYLE_CLASS;
		if (TYPE_BUTTON.equals(getType()))
			defaultStyle = CSS_DEFAULT.COMMAND_BTN_DEFAULT_STYLE_CLASS;
		return Util.getQualifiedStyleClass(this, styleClass, defaultStyle,
				"styleClass", false);
	}

	public void setRenderedOnUserRole(String role) {
		this.renderedOnUserRole = role;
	}

	/**
	 * <p>
	 * Return the value of the <code>renderedOnUserRole</code> property.
	 * </p>
	 */
	public String getRenderedOnUserRole() {
		if (renderedOnUserRole != null) {
			return renderedOnUserRole;
		}
		ValueBinding vb = getValueBinding("renderedOnUserRole");
		return vb != null ? (String) vb.getValue(getFacesContext()) : null;
	}

	/**
	 * <p>
	 * Return the value of the <code>rendered</code> property.
	 * </p>
	 */
	public boolean isRendered() {
		if (!Util.isRenderedOnUserRole(this)) {
			return false;
		}
		return super.isRendered();
	}

	public void setAttachment(boolean b) {
		this.attachment = Boolean.valueOf(b);
	}

	public boolean isAttachment() {
		if (attachment != null) {
			return (this.attachment.booleanValue());
		}
		ValueBinding vb = getValueBinding("attachment");
		if (vb != null) {
			return ((Boolean) vb.getValue(getFacesContext())).booleanValue();
		} else {
			return (true);
		}

	}

	/**
	 * <p>
	 * Gets the state of the instance as a <code>Serializable</code> Object.
	 * </p>
	 */
	public Object saveState(FacesContext context) {
		Object values[] = new Object[14];
		values[0] = super.saveState(context);
		values[1] = resource;
		values[2] = mimeType;
		values[3] = lastModified;
		values[4] = fileName;
		values[5] = image;
		values[6] = type;
		values[7] = label;
		values[8] = style;
		values[9] = styleClass;
		values[10] = renderedOnUserRole;
		values[11] = attachment;
		values[12] = shared;
		values[13] = target;
		return ((Object) (values));
	}

	/**
	 * <p>
	 * Perform any processing required to restore the state from the entries in
	 * the state Object.
	 * </p>
	 */
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		resource = (Resource) values[1];
		mimeType = (String) values[2];
		lastModified = (Date) values[3];
		fileName = (String) values[4];
		image = (String) values[5];
		type = (String) values[6];
		label = (String) values[7];
		style = (String) values[8];
		styleClass = (String) values[9];
		renderedOnUserRole = (String) values[10];
		attachment = (Boolean) values[11];
		shared = (Boolean)values[12];
        target = (String) values[13];
    }

	public boolean getAttachment() {
		return this.isAttachment();
	}

	public int getLastResourceHashCode() {
		return lastResourceHashCode;
	}

	public String getPath() {
		return path;
	}
	
	public boolean isShared(){
		if (shared != null) {
			return (this.shared.booleanValue());
		}
		ValueBinding vb = getValueBinding("shared");
		if (vb != null) {
			return ((Boolean) vb.getValue(getFacesContext())).booleanValue();
		} else {
			return (true);
		}
	}
	
	public void setShared(boolean s){
		this.shared = Boolean.valueOf(s);
	}

    public String getTarget() {
        if (target != null) return target;
        ValueBinding vb = getValueBinding("target");
        if (vb == null) return "_blank";
        Object value = vb.getValue(getFacesContext());
        if (value == null) return "_blank";
        return (String) value;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}

class RegisteredResource implements Resource {
    private final Resource resource;
    private final String fileName;
    private String label;
    private Date lastModified;
    private String mimeType;
    private boolean isAttachment;
    private boolean isShared;
    private String contentDispositionFileName;

    public RegisteredResource(OutputResource outputResource, Resource resource, String fileName) {
        this.resource = resource;
        this.fileName = fileName;
        label = outputResource.getLabel();
        lastModified = outputResource.getLastModified();
        mimeType = outputResource.getMimeType();
        isAttachment = outputResource.isAttachment();
        isShared = outputResource.isShared();
        // ICE-4342
        // Encoded filename in Content-Disposition header; to be used in save file dialog;
        // See http://greenbytes.de/tech/tc2231/
        contentDispositionFileName = encodeContentDispositionFilename(fileName);
    }

    public String calculateDigest() {
        return resource.calculateDigest() + (isShared ? "" : String.valueOf(this.hashCode()));
    }

    public Date lastModified() {
        return lastModified;
    }

    public InputStream open() throws IOException {
        return resource.open();
    }

    public void withOptions(Options options) {
        ResourceOptions resourceOptions = new ResourceOptions();
        try {
            resource.withOptions(resourceOptions);
        } catch (IOException e) {
        }
        if (resourceOptions.fileName != null)
            options.setFileName(resourceOptions.fileName);
        else if (fileName != null)
            options.setFileName(fileName);
        else if (resource instanceof FileResource)
            options.setFileName(((FileResource) resource).getFile()
                    .getName());
        else if (label != null)
            options.setFileName(label.replace(' ', '_'));

        if (resourceOptions.lastModified != null)
            options.setLastModified(resourceOptions.lastModified);
        else if (lastModified != null)
            options.setLastModified(lastModified);

        if (resourceOptions.mimeType != null)
            options.setMimeType(resourceOptions.mimeType);
        else if (mimeType != null)
            options.setMimeType(mimeType);

        if (resourceOptions.isAttachment)
            options.setAsAttachement();
        else if (isAttachment)
            options.setAsAttachement();

        if (options instanceof ResourceDispatcher.ExtendedResourceOptions) {
            ((ResourceDispatcher.ExtendedResourceOptions) options).setContentDispositionFileName(contentDispositionFileName);
        }
    }

    private class ResourceOptions implements Resource.Options {
        private Date lastModified;
        private String mimeType;
        private String fileName;
        private boolean isAttachment;

        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }

        public void setLastModified(Date lastModified) {
            this.lastModified = lastModified;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public void setExpiresBy(Date date) {
        }

        public void setAsAttachement() {
            isAttachment = true;
        }
    }

    // ICE-4342
    // Encode filename for Content-Disposition header; to be used in save file dialog;
    // See http://greenbytes.de/tech/tc2231/
    // Some code suggested by Deryk Sinotte 
    private static String encodeContentDispositionFilename(String fileName) {
        if (fileName == null || fileName.trim().length() == 0) return null;

        String defaultFileName = "=\"" + fileName + "\"";
        String userAgent = null;
        Map headerMap = FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderMap();
        userAgent = (String) headerMap.get("user-agent");
        //WebLogic does not provide the user-agent for some reason
        //System.out.println("RegisteredResource.encodeContentDispositionFilename: user-agent = " + userAgent);
        if (userAgent == null || userAgent.trim().length() == 0) return defaultFileName;


        userAgent = userAgent.toLowerCase();
        try {
            if (userAgent.indexOf("msie") > -1) return encodeForIE(fileName);
            if (userAgent.indexOf("firefox") > -1 || userAgent.indexOf("opera") > -1) return encodeForFirefox(fileName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return defaultFileName;
    }

    // contributed by Robert Vojta
    private static String encodeForIE(String fileName)
            throws UnsupportedEncodingException {
        /*
         * http://greenbytes.de/tech/tc2231/#attwithfnrawpctenca
         *
         * IE decodes %XY to characters and than if it detects
         * UTF-8 stream (after decoding of %XY), than it creates
         * UTF-8 string.
         *
         * We use this behavior to offer correct file name
         * for download.
         */
        StringBuffer encodedFileName = new StringBuffer();
        encodedFileName.append("=\""); // ICEfaces 1.7.2 bug
        encodedFileName.append(URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20"));
        encodedFileName.append("\""); // ICEfaces 1.7.2 bug

        return encodedFileName.toString();
    }
    // contributed by Robert Vojta
    private static String encodeForFirefox(String fileName)
            throws UnsupportedEncodingException {
        /*
         * http://greenbytes.de/tech/tc2231/#attwithfn2231utf8 
         */
        StringBuffer encodedFileName = new StringBuffer();

        encodedFileName.append("*=UTF-8''");

        encodedFileName.append(URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20"));

        return encodedFileName.toString();
    }
}
