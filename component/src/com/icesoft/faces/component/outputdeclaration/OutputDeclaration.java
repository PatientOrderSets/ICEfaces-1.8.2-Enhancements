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

package com.icesoft.faces.component.outputdeclaration;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * This is an extension of javax.faces.component.UIOutput, which provides
 * design-time support for SunStudioCreator 2
 */
public class OutputDeclaration
        extends javax.faces.component.UIOutput {

    public static final String COMPONENT_TYPE =
            "com.icesoft.faces.OutputDeclaration";
    public static final String COMPONENT_FAMILY = "javax.faces.Output";
    public static final String RENDERER_TYPE =
            "com.icesoft.faces.OutputDeclaration";

    public OutputDeclaration() {
        super();
        setRendererType(RENDERER_TYPE);
    }

    public String getRendererType() {
        return RENDERER_TYPE;
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }


    // doctypePublic
    private String doctypePublic = null;

    /**
     * <p>An identifier for the DTD without giving a specific location.</p>
     */
    public String getDoctypePublic() {
        if (this.doctypePublic != null) {
            return this.doctypePublic;
        }
        ValueBinding _vb = getValueBinding("doctypePublic");
        if (_vb != null) {
            return (String) _vb.getValue(getFacesContext());
        }
        return null;
    }

    /**
     * <p>An identifier for the DTD without giving a specific location.</p>
     *
     * @see #getDoctypePublic()
     */
    public void setDoctypePublic(String doctypePublic) {
        this.doctypePublic = doctypePublic;
    }

    // doctypeRoot
    private String doctypeRoot = null;

    /**
     * <p>Indicates the root element of the XML document.</p>
     */
    public String getDoctypeRoot() {
        if (this.doctypeRoot != null) {
            return this.doctypeRoot;
        }
        ValueBinding _vb = getValueBinding("doctypeRoot");
        if (_vb != null) {
            return (String) _vb.getValue(getFacesContext());
        }
        return null;
    }

    /**
     * <p>Indicates the root element of the XML document.</p>
     *
     * @see #getDoctypeRoot()
     */
    public void setDoctypeRoot(String doctypeRoot) {
        this.doctypeRoot = doctypeRoot;
    }

    // doctypeSystem
    private String doctypeSystem = null;

    /**
     * <p>Indicates the URI reference to the DTD.</p>
     */
    public String getDoctypeSystem() {
        if (this.doctypeSystem != null) {
            return this.doctypeSystem;
        }
        ValueBinding _vb = getValueBinding("doctypeSystem");
        if (_vb != null) {
            return (String) _vb.getValue(getFacesContext());
        }
        return null;
    }

    /**
     * <p>Indicates the URI reference to the DTD.</p>
     *
     * @see #getDoctypeSystem()
     */
    public void setDoctypeSystem(String doctypeSystem) {
        this.doctypeSystem = doctypeSystem;
    }

    /**
     * <p>Restore the state of this component.</p>
     */
    public void restoreState(FacesContext _context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        this.doctypePublic = (String) _values[1];
        this.doctypeRoot = (String) _values[2];
        this.doctypeSystem = (String) _values[3];
    }

    /**
     * <p>Save the state of this component.</p>
     */
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[4];
        _values[0] = super.saveState(_context);
        _values[1] = this.doctypePublic;
        _values[2] = this.doctypeRoot;
        _values[3] = this.doctypeSystem;
        return _values;
    }
}
