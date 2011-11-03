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

/* Original Copyright
 * Copyright 2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.icesoft.faces.component.commandsortheader;

import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.ext.HtmlCommandLink;
import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.component.ext.taglib.Util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;

/**
 * This component is an extension of com.icesoft.faces.component.ext.HtmlCommandLink,
 * works in conjunction with dataTable.
 */
public class CommandSortHeader
        extends HtmlCommandLink {
    /* (non-Javadoc)
     * @see javax.faces.component.UIComponent#broadcast(javax.faces.event.FacesEvent)
     */
    public void broadcast(FacesEvent event) throws AbortProcessingException {
        super.broadcast(event);

        if (event instanceof ActionEvent) {
            HtmlDataTable dataTable = findParentDataTable();
            if (dataTable == null) {
                //log.error("parent table not found");
            } else {
                String colName = getColumnName();
                String currentSortColumn = dataTable.getSortColumn();
                boolean currentAscending = dataTable.isSortAscending();
                if (colName.equals(currentSortColumn)) {
                    dataTable.setSortColumn(getColumnName());
                    dataTable.setSortAscending(!currentAscending);
                } else {
                    dataTable.setSortColumn(getColumnName());
                    dataTable.setSortAscending(true);
                }
            }
        }
    }


    /**
     * <p>Return the instance of the <code>parentDataTable</code> of this
     * component.</p>
     */
    public HtmlDataTable findParentDataTable() {
        UIComponent parent = getParent();
        while (parent != null) {
            if (parent instanceof HtmlDataTable) {
                return (HtmlDataTable) parent;
            }
            parent = parent.getParent();
        }
        return null;
    }


    /**
     * <p>Gets the state of the instance as a <code>Serializable</code>
     * Object.</p>
     */
    public Object saveState(FacesContext context) {
        Object values[] = new Object[4];
        values[0] = super.saveState(context);
        values[1] = _columnName;
        values[2] = _arrow;
        values[3] = styleClass;
        return ((Object) (values));
    }

    /**
     * <p>Perform any processing required to restore the state from the entries
     * in the state Object.</p>
     */
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        _columnName = (String) values[1];
        _arrow = (Boolean) values[2];
        styleClass = (String)values[3];
    }

    private String styleClass;

    /**
     * <p>Set the value of the <code>styleClass</code> property.</p>
     */
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    /**
     * <p>Return the value of the <code>styleClass</code> property.</p>
     */
    public String getStyleClass() {
        return Util.getQualifiedStyleClass(this, styleClass,
                                             CSS_DEFAULT.COMMAND_SORT_HEADER_STYLE_CLASS
                                             , "styleClass", 
                                             isDisabled());
    }

    public static final String COMPONENT_TYPE = "com.icesoft.faces.SortHeader";
    public static final String COMPONENT_FAMILY = "javax.faces.Command";
    public static final String DEFAULT_RENDERER_TYPE =
            "com.icesoft.faces.SortHeader";

    private String _columnName = null;
    private Boolean _arrow = null;

    public CommandSortHeader() {
        setRendererType(DEFAULT_RENDERER_TYPE);
    }

    /**
     * <p>Return the value of the <code>COMPONENT_FAMILY</code> of this
     * component.</p>
     */
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    /**
     * <p>Set the value of the <code>columnName</code> property.</p>
     */
    public void setColumnName(String columnName) {
        _columnName = columnName;
    }

    /**
     * <p>Return the value of the <code>columnName</code> property.</p>
     */
    public String getColumnName() {
        if (_columnName != null) {
            return _columnName;
        }
        ValueBinding vb = getValueBinding("columnName");
        return vb != null ? vb.getValue(getFacesContext()).toString() : null;
    }

    /**
     * <p>Set the value of the <code>arrow</code> property.</p>
     * 
     * @deprecated It no longer has any effect in 1.7. To alter 
     * the appearance of the arrow images, the following style classes need 
     * to be customized: iceCmdSrtHdrAsc, iceCmdSrtHdrDesc
     */
    public void setArrow(boolean arrow) {
        _arrow = Boolean.valueOf(arrow);
    }

    /**
     * <p>Return the value of the <code>arrow</code> property.</p>
     * 
     * @deprecated It no longer has any effect in 1.7. To alter 
     * the appearance of the arrow images, the following style classes need 
     * to be customized: iceCmdSrtHdrAsc, iceCmdSrtHdrDesc
     */
    public boolean isArrow() {
        if (_arrow != null) {
            return _arrow.booleanValue();
        }
        ValueBinding vb = getValueBinding("arrow");
        Boolean v =
                vb != null ? (Boolean) vb.getValue(getFacesContext()) : null;
        return v != null ? v.booleanValue() : false;
    }


}
