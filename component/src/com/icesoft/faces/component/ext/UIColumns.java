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

package com.icesoft.faces.component.ext;

import com.icesoft.faces.component.panelseries.UISeries;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.PhaseId;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;


public class UIColumns extends UISeries {
    public static final String COMPONENT_TYPE = "com.icesoft.faces.Columns";
    public static final String COMPONENT_FAMILY = "javax.faces.Columns";

    public String getFamily() {
        return (COMPONENT_FAMILY);
    }

    public UIColumns() {
        setRendererType(null);
    }


    /**
     * <p>The "should this component be rendered" flag.</p>
     */
    private boolean rendered = true;
    private boolean renderedSet = false;

    public boolean isRendered() {
        if (renderedSet) {
            return (rendered);
        }
        ValueBinding vb = getValueBinding("rendered");
        if (vb != null) {
            return (!Boolean.FALSE.equals(vb.getValue(getFacesContext())));
        } else {
            return (this.rendered);
        }

    }


    public void setRendered(boolean rendered) {
        this.rendered = rendered;
        this.renderedSet = true;
    }

    protected void iterate(FacesContext facesContext, PhaseId phase) {
        // process the column header facet
        if (isHeaderFacet() && (UIComponent) getFacet("header")!= null) {
            // clear row index
            setRowIndex(-1);
            UIComponent facet = (UIComponent) getFacet("header");
            int rowsProcessed = 0;
            int currentRowIndex = getFirst() - 1;
            int displayedRows = getRows();
            // loop over dataModel processing each row once
            while (1 == 1) {
                // break if we have processed the number of rows requested
                if ((displayedRows > 0) && (++rowsProcessed > displayedRows)) {
                    break;
                }
                // process the row at currentRowIndex
                setRowIndex(++currentRowIndex);
                // break if we've moved past the last row
                if (!isRowAvailable()) {
                    break;
                }
                //process the header facet exactly once
                processKids(facesContext, phase, facet);
            }
        } else {
            // clear row index
            setRowIndex(-1);
            int rowsProcessed = 0;
            int currentRowIndex = getFirst() - 1;
            int displayedRows = getRows();
            // loop over dataModel processing each row once
            while (1 == 1) {
                // break if we have processed the number of rows requested
                if ((displayedRows > 0) && (++rowsProcessed > displayedRows)) {
                    break;
                }
                // process the row at currentRowIndex
                setRowIndex(++currentRowIndex);
                // break if we've moved past the last row
                if (!isRowAvailable()) {
                    break;
                }
                if (getChildCount() > 0) {
                    Iterator kids = getChildren().iterator();
                    while (kids.hasNext()) {
                        UIComponent kid = (UIComponent) kids.next();
                        processKids(facesContext, phase, kid);
                    }
                }
            }
            // reset row index
            setRowIndex(-1);
        }
    }

    public void processKids(FacesContext context, PhaseId phaseId,
                            UIComponent kid) {
        if (phaseId == PhaseId.APPLY_REQUEST_VALUES) {
            kid.processDecodes(context);
        } else if (phaseId == PhaseId.PROCESS_VALIDATIONS) {
            kid.processValidators(context);
        } else if (phaseId == PhaseId.UPDATE_MODEL_VALUES) {
            kid.processUpdates(context);
        } else {
            throw new IllegalArgumentException();
        }
    }


    protected void restoreChildrenState(FacesContext facesContext) {
        Iterator children = null;
        if (isHeaderFacet()) {
            children = getFacets().values().iterator();
        } else {
            if (getRowIndex() < 0) {
                return;
            } else {
                children = getChildren().iterator();
            }
        }
        while (children.hasNext()) {
            UIComponent child = (UIComponent) children.next();
            restoreChildState(facesContext, child);
        }
    }

    /**
     * <p>Save state information for all descendant components, as described for
     * <code>setRowIndex()</code>.</p>
     */
    protected void saveChildrenState(FacesContext facesContext) {
        Iterator children = null;
        if (isHeaderFacet()) {
            children = getFacets().values().iterator();
        } else {
            if (getRowIndex() < 0) {
                return;
            } else {
                children = getChildren().iterator();
            }
        }
        while (children.hasNext()) {
            UIComponent child = (UIComponent) children.next();
            saveChildState(facesContext, child);
        }
    }

    public int getRowId() {
        if (getParent() != null &&
            ((HtmlDataTable) getParent()).getRowIndex() >= 0) {
            return ((HtmlDataTable) getParent()).getRowIndex();
        } else {
            return 0;
        }
    }

    public String getClientId(FacesContext context) {
        if (context == null) {
            throw new NullPointerException();
        }
        String baseClientId = super.getClientId(context);
        if (getRowIndex() >= 0) {
            return (baseClientId + NamingContainer.SEPARATOR_CHAR +
                    getRowIndex() + getRowId());
        } else {
            return (baseClientId);
        }

    }

    private transient Object values[];
    public void restoreState(FacesContext context, Object state) {
        values = (Object[])state;
        super.restoreState(context, values[0]);
        rendered = Boolean.valueOf(values[1].toString()).booleanValue();
        renderedSet = Boolean.valueOf(values[2].toString()).booleanValue();                
    }

    public Object saveState(FacesContext context) {
        if(values == null){
            values = new Object[3];
        }
        values[0] = super.saveState(context);
        values[1] = Boolean.valueOf(rendered);
        values[2] = Boolean.valueOf(renderedSet);
        return values;
    }

    public boolean isHeaderFacet() {
        return (((HtmlDataTable) getParent()).getRowIndex() < 0) ? true : false;
    }

    public void processDecodes(FacesContext context) {

        if (context == null) {
            throw new NullPointerException();
        }
        if (!isRendered()) {
            return;
        }

        dataModel = null; // Re-evaluate even with server-side state saving
        if (null == savedChildren) {
            savedChildren = new HashMap();
        }
        iterate(context, PhaseId.APPLY_REQUEST_VALUES);
        decode(context);
        getAttributes().remove("rowServed");
    }
    

}