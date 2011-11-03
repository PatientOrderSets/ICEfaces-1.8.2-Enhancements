package com.icesoft.faces.component.facelets;

import com.sun.facelets.tag.jsf.ComponentHandler;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.MethodRule;
import com.icesoft.faces.component.dragdrop.DragEvent;
import com.icesoft.faces.component.dragdrop.DropEvent;
import com.icesoft.faces.component.ext.RowSelectorEvent;
import com.icesoft.faces.component.ext.ClickActionEvent;
import com.icesoft.faces.component.panelpositioned.PanelPositionedEvent;
import com.icesoft.faces.component.paneltabset.TabChangeEvent;
import com.icesoft.faces.component.outputchart.OutputChart;
import com.icesoft.faces.component.DisplayEvent;
import com.icesoft.faces.component.selectinputtext.TextChangeEvent;

import java.util.EventObject;

/**
 * @author Mark Collette
 * @since 1.6
 */
public class IceComponentHandler extends ComponentHandler {
    public IceComponentHandler(ComponentConfig componentConfig) {
        super(componentConfig);
    }
    
    protected MetaRuleset createMetaRuleset(Class type) {
        MetaRuleset m = super.createMetaRuleset(type);
        if( tag.getNamespace() != null &&
            tag.getNamespace().equals("http://www.icesoft.com/icefaces/component") )
        {
            if( tag.getLocalName().equals("inputFile") ) {
                m.addRule( new MethodRule("progressListener", null, new Class[] {EventObject.class}) );
            }
            else if( tag.getLocalName().equals("outputChart") ) {
                m.addRule( new MethodRule("renderOnSubmit", Boolean.TYPE, new Class[] {OutputChart.class}) );
            }
            else if( tag.getLocalName().equals("panelGroup") ) {
                m.addRule( new MethodRule("dragListener", null, new Class[] {DragEvent.class}) );
                m.addRule( new MethodRule("dropListener", null, new Class[] {DropEvent.class}) );
            }
            else if( tag.getLocalName().equals("panelPositioned") ) {
                m.addRule( new MethodRule("listener", null, new Class[] {PanelPositionedEvent.class}) );
            }
            else if( tag.getLocalName().equals("panelTabSet") ) {
                m.addRule( new MethodRule("tabChangeListener", null, new Class[] {TabChangeEvent.class}) );
            }
            else if( tag.getLocalName().equals("rowSelector") ) {
                m.addRule( new MethodRule("selectionListener", null, new Class[] {RowSelectorEvent.class}) );
                m.addRule( new MethodRule("selectionAction", null, new Class[0]) );
                m.addRule( new MethodRule("clickListener", null, new Class[] {ClickActionEvent.class}) );
                m.addRule( new MethodRule("clickAction", null, new Class[0]) );
            }
            else if( tag.getLocalName().equals("panelTooltip") ) {
                m.addRule( new MethodRule("displayListener", null, new Class[] {DisplayEvent.class}) );
            }
            else if( tag.getLocalName().equals("menuPopup") ) {
                m.addRule( new MethodRule("displayListener", null, new Class[] {DisplayEvent.class}) );
            }
            else if( tag.getLocalName().equals("selectInputText") ) {
                m.addRule( new MethodRule("textChangeListener", null, new Class[] {TextChangeEvent.class}) );
            }
        }
        return m;
    }
}
