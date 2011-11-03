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

package com.icesoft.faces.context.effects;


import com.icesoft.faces.application.D2DViewHandler;
import com.icesoft.faces.application.StartupTime;
import com.icesoft.faces.context.BridgeFacesContext;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Used to send Javascript to the browser
 */
public class JavascriptContext {
    private static final Random RANDOM = new Random();

    /**
     * Request scope key, used to store javascript code to be sent to the browser
     */
    private static final String REQUEST_KEY =
            "icesoft_javascript_request_key_7698193";
    /**
     * Effects scope key, used to store the fired effects in the request scope
     */
    private static final String EFFECTS_REQUEST_KEY =
            "icesoft_javascript_effects_request_key_9072451";
    /**
     * Request scope key for componenet focus
     */
    private static final String FOCUS_COMP_KEY =
            "icesoft_javascript_focus_comp";
    /**
     * Request Scope Key, storing the focus
     */
    private static final String FOCUS_APP_KEY = "icesoft_javascript_focus_app";
    /**
     * Request Scope Key, indicating that extras needs to be included
     */
    public static final String LIB_KEY =
            "icesoft_javascript_required_libs_897241";
    /**
     * ID of script node used to send Javascript in a dom update
     */
    public static final String DYNAMIC_CODE_ID = "dynamic-code";

    /**
     * URL of the ICE Bridge lib
     */
    public static final String ICE_BRIDGE = "/xmlhttp" + StartupTime.getStartupInc() + "icefaces-d2d.js";

    /**
     * URL of the ICE Extras lib
     */
    public static final String ICE_EXTRAS = "/xmlhttp" + StartupTime.getStartupInc() + "ice-extras.js";

    /**
     * Include a script tag in the &lt;head&gt; section of the page with the src
     * attribute set to libname. This will insure that all icefaces pages
     * contain this script for the remainder of the session.
     *
     * @param libname      the source of the javascript file
     * @param facesContext The facescontext this file is needed for
     */
    public static void includeLib(String libname, FacesContext facesContext) {
        if (facesContext == null)
            return;
        ExternalContext externalContext = facesContext.getExternalContext();
        if (externalContext == null)
            return;
        Map sessionMap = externalContext.getSessionMap();
        if (sessionMap == null)
            return;
        List libs = (List) sessionMap.get(LIB_KEY);
        if (libs == null) {
            libs = new ArrayList();
            sessionMap.put(LIB_KEY, libs);
        }
        if (!libs.contains(libname))
            libs.add(libname);
    }

    /**
     * Get the included javascript libraries
     *
     * @param facesContext
     * @return
     */
    public static String[] getIncludedLibs(FacesContext facesContext) {
        List libs = (List) facesContext.getExternalContext().getSessionMap()
                .get(LIB_KEY);
        String[] result = new String[0];
        if (libs != null) {
            result = new String[libs.size()];
            libs.toArray(result);
        }
        return result;
    }

    /**
     * Add a javascript call to be executed on the browser. Code will be
     * executed at the end of the current render cycle.
     * <p/>
     * Note: When sending function definitions you must specify the function
     * name as this['functionName'] = function(){} For example: <code>
     * helloWorld(name}{ alert('Hello [' + name + ']'); }</code>
     * <p/>
     * Would need to be written as <code> this['helloWorld'] = function(name){
     * alert('Hello [' + name + ']'); }</code>
     *
     * @param facesContext
     * @param call         Javascript code to execute
     */
    public static void addJavascriptCall(FacesContext facesContext,
                                         String call) {
        Map map = facesContext.getExternalContext().getRequestMap();
        addJavascriptCall(map, call);
    }

    /**
     * Add a javascript call to the request map
     *
     * @param map
     * @param call
     */
    private static void addJavascriptCall(Map map, String call) {
        String currentValue = (String) map.get(REQUEST_KEY);
        if (currentValue == null) {
            map.put(REQUEST_KEY, call);
        } else {
            map.put(REQUEST_KEY, currentValue + call);
        }
    }

    /**
     * Add a javascript call to this request
     *
     * @param facesContext
     * @return
     */
    public static String getJavascriptCalls(FacesContext facesContext) {
        Map map = facesContext.getExternalContext().getRequestMap();
        return getJavascriptCalls(map);
    }

    /**
     * Get javascript calls from the Request map
     *
     * @param map
     * @return
     */
    public static String getJavascriptCalls(Map map) {
        addtEffectJavascriptCalls(map);
        String code = (String) map.get(REQUEST_KEY);
        map.put(REQUEST_KEY, "");
        String focus = getFocusCall(map);
        code = replaceDupes(
                code == null || "".equals(code) ? focus : code + ';' + focus);
        // Hack so that javascript will be called from both the bridge and the html parser
        // Remove when we have a better way
        if ("".equals(code)) {
            return "";
        } else {
            return code + randomComment();
        }
    }

    /**
     * Get the focus call from the request map
     *
     * @param map
     * @return
     */
    private static String getFocusCall(Map map) {
        String focus = (String) map.get(FOCUS_APP_KEY);
        if (focus == null) {
            focus = (String) map.get(FOCUS_COMP_KEY);
        }
        if (focus == null) {
            return "";
        } else {
            map.remove(FOCUS_APP_KEY);
            map.remove(FOCUS_COMP_KEY);
            return "Ice.Focus.setFocus('" + focus + "');";
        }
    }


    /**
     * Wrap the effect in a javascript method to be called later. Returns the
     * method name. Used in local effects.
     *
     * @param effect
     * @param id
     * @param context
     * @return The name of the method that wraps the effect
     */
    public static String applyEffect(Effect effect, String id,
                                     FacesContext context) {
        //Get the real ID if a JSF component
        //UIComponent uiComponent = context.getViewRoot().findComponent(id);

        UIComponent uiComponent = D2DViewHandler.findComponent(id, context.getViewRoot());
        if (uiComponent != null) {
            id = uiComponent.getClientId(context);
        }

        String name = genFunctionName(id, effect);

        String call = "window['" + name + "'] =  function (){" +
                "id = '" + id + "';" + effect.toString() + "};";

        addJavascriptCall(context, call);
        return name + "();";
    }

    /**
     * Fire an effect at the end of the current render cycle. Fired from server, non local.
     *
     * @param effect
     * @param id      Target element of the effect
     * @param context
     */
    public static void fireEffect(Effect effect, String id,
                                  FacesContext context) {
        if (effect == null || effect.isFired()) return;
        effect.setFired(true);
        Object viewRoot = context.getViewRoot();
        try {


            UIComponent uiComponent = D2DViewHandler.findComponent(id, context.getViewRoot());
            if (uiComponent != null) {
                id = uiComponent.getClientId(context);
            }
        } catch (Exception e) {
            /*Class clazz = context.getViewRoot().getClass();
            Method[] methods =clazz.getMethods();
            for(int i = 0; i < methods.length; i++){
                Method m = methods[i];
                System.err.println("Method [" + m.getName() + "]");
                Class[] args = m.getParameterTypes();
                for(int a =0; a<args.length;a++){
                    System.err.println("Arg [" + args[a].getName() + "]");
                }
            } */
            System.err.println("View Root is [" + viewRoot.getClass().getName() + "]");
            e.printStackTrace();
        }
        effect.setId(id);
        addEffect(effect, context);

    }

    /**
     * Fire an effect at the end of the current render cycle
     *
     * @param effect
     * @param component
     */
    public static void fireEffect(Effect effect, UIComponent component) {
        if (effect == null || effect.isFired()) return;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        fireEffect(effect, component, facesContext);
    }

    /**
     * Fire an effect at the end of the current render cycle
     *
     * @param effect
     * @param component
     * @param facesContext
     */
    public static void fireEffect(Effect effect, UIComponent component,
                                  FacesContext facesContext) {
        if (effect == null || effect.isFired()) return;
        String id = component.getClientId(facesContext);
        fireEffect(effect, id, facesContext);
    }

    /**
     * Fire an effect at the end of the current render cycle
     *
     * @param uiComponent
     * @param facesContext
     */

    public static void fireEffect(UIComponent uiComponent,
                                  FacesContext facesContext) {
        Effect effect = (Effect) uiComponent.getAttributes().get("effect");
        if (effect != null)
            fireEffect(effect, uiComponent, facesContext);
    }

    /**
     * Get the Effect function for a given event
     *
     * @param uiComponent
     * @param event
     * @param id
     * @param facesContext
     * @return
     */
    public static String getEffectFunctionForEvent(UIComponent uiComponent,
                                                   String event, String id,
                                                   FacesContext facesContext) {
        String result = null;
        Effect fx = getEffectForEvent(uiComponent, event);
        if (fx != null) {
            result = applyEffect(fx, id, facesContext);
        }
        return result;
    }

    /**
     * Get the Effect for a givin function
     *
     * @param uiComponent
     * @param event
     * @return
     */
    private static Effect getEffectForEvent(UIComponent uiComponent,
                                            String event) {
        Effect result = null;
        Object o = uiComponent.getAttributes().get(event);
        if (o != null && o instanceof Effect) {
            result = (Effect) o;
        }
        return result;
    }


    /**
     * Generate a unique function name for local effects
     *
     * @param id
     * @param effect
     * @return
     */
    private static String genFunctionName(String id, Effect effect) {
        StringBuffer sb = new StringBuffer("iceEffect");
        char[] ca = id.toCharArray();
        // Make sure no invalid characters are in the function name
        //65 - 90  A - Z
        //97 - 122 a - z
        //48 - 57 / 0 - 9
        for (int i = 0; i < ca.length; i++) {
            int c = (int) ca[i];
            if ((c > 64 && c < 91) || (c > 96 && c < 123) || (c > 47 && c < 58)) {
                sb.append(ca[i]);
            } else {
                sb.append('_');
            }
        }
        sb.append(effect.hashCode());
        return sb.toString();
    }

    /**
     * Remove duplicate semi-colons from a givin string
     *
     * @param s
     * @return
     */
    private static String replaceDupes(String s) {
        if (s.indexOf(";;") == -1) return s;
        s = s.replaceAll(";;", ";");
        return s;
    }

    /**
     * \ Set focus on an HTML element.
     *
     * @param context
     * @param id
     */
    public static void focus(FacesContext context, String id) {
        //this method relies on XMLRenderer to create these "script" elements
        if (!id.equals("")) {
            Map map = context.getExternalContext().getRequestMap();
            map.put(FOCUS_COMP_KEY, id);
        }
    }

    /**
     * Set the application focus for the current request, overrides and setFocus call.
     * Generally setFocus is used by components, while setApplicationFocus is used by the application.
     *
     * @param id
     */
    public static void applicationFocus(FacesContext facesContext, String id) {
        if (!id.equals("")) {

            Map map = facesContext.getExternalContext().getRequestMap();
            map.put(FOCUS_APP_KEY, id);
        }
    }

    /**
     * Get the focus for the current request
     *
     * @param context
     * @return
     */
    public static String getFocus(FacesContext context) {
        String focusId = ((BridgeFacesContext) context).getFocusId();
        Map map = context.getExternalContext().getRequestMap();
        if (map.containsKey(FOCUS_COMP_KEY))
            focusId = (String) map.get(FOCUS_COMP_KEY);
        if (map.containsKey(FOCUS_APP_KEY))
            focusId = (String) map.get(FOCUS_APP_KEY);
        return focusId;
    }

    /**
     * Add an effect call for the current request
     *
     * @param effect
     * @param facesContext
     */
    private static void addEffect(Effect effect, FacesContext facesContext) {
        Map map = facesContext.getExternalContext().getRequestMap();
        List list = (ArrayList) map.get(EFFECTS_REQUEST_KEY);
        if (list == null) {
            list = new ArrayList();
            map.put(EFFECTS_REQUEST_KEY, list);
        }
        list.add(effect);
    }

    /**
     * Add fired effects as javascript calls
     *
     * @param map
     */
    private static void addtEffectJavascriptCalls(Map map) {

        List list = (ArrayList) map.get(EFFECTS_REQUEST_KEY);
        if (list == null) {
            return;
        }
        Map sequencedEffects = new HashMap();
        Iterator iter = list.iterator();
        while (iter.hasNext()) {
            Effect effect = (Effect) iter.next();
            if (effect.getSequence() == null &&
                    !(effect instanceof EffectQueue)) {
                String call =
                        "id = '" + effect.getId() + "';" + effect.toString();
                addJavascriptCall(map, call);
            } else {

                extractEffectSequence(effect, sequencedEffects);
            }
        }

        if (sequencedEffects.size() > 0) {
            iter = sequencedEffects.values().iterator();
            int sequence = 0;
            while (iter.hasNext()) {
                list = (List) iter.next();
                Collections.sort(list, new EffectComparator());
                String call = buildSequenceEffectCall(list, sequence);
                addJavascriptCall(map, call);
                sequence++;
            }
        }
        map.remove(EFFECTS_REQUEST_KEY);
    }

    /**
     * Build sequence of javascript effect calls
     *
     * @param list
     * @param sequence
     * @return
     */
    private static String buildSequenceEffectCall(List list, int sequence) {
        StringBuffer sb = new StringBuffer();
        Iterator effectIter = list.iterator();
        int effect = 0;
        String lastCall = null;
        while (effectIter.hasNext()) {
            Effect fx = (Effect) effectIter.next();
            String var = getVariableName(sequence, effect);
            lastCall = "var " + var + " = '" + fx.getId() + "';" +
                    fx.toString(var, lastCall);
            effect++;
        }
        return lastCall;
    }

    /**
     * Extra Javascript Effect Sequence from a map of effects
     *
     * @param effect
     * @param sequencedEffects
     */
    private static void extractEffectSequence(Effect effect,
                                              Map sequencedEffects) {
        String seqName = effect.getSequence();

        List seq = (List) sequencedEffects.get(seqName);
        if (seq == null) {
            seq = new ArrayList();
            sequencedEffects.put(seqName, seq);
        }
        if (effect instanceof EffectQueue) {
            List eqList = ((EffectQueue) effect).getEffects();
            Iterator eqIter = eqList.iterator();
            int seqId = 0;
            while (eqIter.hasNext()) {
                Effect fx = (Effect) eqIter.next();
                fx.setSequence(seqName);
                fx.setSequenceId(seqId);
                fx.setId(effect.getId());
                seq.add(fx);
                seqId++;

            }
        } else {
            seq.add(effect);
        }
    }

    /**
     * Get variable name for an effect in a sequence
     *
     * @param sequence
     * @param effect
     * @return
     */
    private static String getVariableName(int sequence, int effect) {
        return "s" + sequence + "e" + effect;
    }

    /**
     * Generate random Javascript comment to force a diff on the 'script'
     * elements, thus triggering their evaluation in the browser.
     */
    private static String randomComment() {
        return "//" + RANDOM.nextInt();
    }
}
