package com.icesoft.faces.component.effect;

import com.icesoft.faces.renderkit.dom_html_basic.DomBasicRenderer;
import com.icesoft.faces.context.effects.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import java.io.IOException;

public class ApplyEffectRenderer extends DomBasicRenderer {

    private static Log log = LogFactory.getLog(ApplyEffectRenderer.class);


    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent) throws IOException {

        try {
            String parentId = uiComponent.getParent().getClientId(facesContext);
            ApplyEffect af = (ApplyEffect) uiComponent;
            Effect fx = EffectBuilder.build(af.getEffectType());
            if (fx == null) {
                log.error("No Effect for effectType [" + af.getEffectType() + "]");
            } else {
                fx.setSequence(af.getSequence());
                fx.setSequenceId(af.getSequenceNumber().intValue());
                fx.setSubmit(af.getSubmit().booleanValue());
                fx.setTransitory(af.getTransitory().booleanValue());
                fx.setOptions(af.getOptions());

                if (af.getFire().booleanValue()) {
                    JavascriptContext.fireEffect(fx, uiComponent.getParent(), facesContext);
                    if (af.getAutoReset().booleanValue())
                        af.setFire(Boolean.FALSE);
                }
                if (af.getEvent() != null) {
                    String event = af.getEvent();
                    LocalEffectEncoder.encodeLocalEffect(parentId, fx, event, facesContext);
                }
            }
        } catch (Exception e) {
            log.error("Unexpected Exception in ApplyEffectRenderer",e);
        }
    }


}

