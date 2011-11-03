/*
 *  Template Generator ase
 */
package com.icesoft.jsfmeta.templates;

import com.icesoft.jsfmeta.util.InternalConfig;
import com.sun.rave.jsfmeta.beans.ComponentBean;
import com.sun.rave.jsfmeta.beans.FacesConfigBean;
import com.sun.rave.jsfmeta.beans.PropertyBean;
import com.sun.rave.jsfmeta.beans.RenderKitBean;
import com.sun.rave.jsfmeta.beans.RendererBean;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fye
 */
public abstract class AbstractTempGen {

    private InternalConfig internalConfig;
    private FacesConfigBean facesConfigBean;
    //TODO: RenderKitFactory jsf 12 
    private String defaultRenderKitId = "HTML_BASIC";

    public void setInternalConfig(InternalConfig internalConfig) {
        this.internalConfig = internalConfig;
    }

    public InternalConfig getInternalConfig() {
        return internalConfig;
    }

    public String getDefaultRenderKitId() {
        return defaultRenderKitId;
    }

    public void setDefaultRenderKitId(String defaultRenderKitId) {
        this.defaultRenderKitId = defaultRenderKitId;
    }

    public void setFacesConfigBean(FacesConfigBean facesConfigBean) {
        this.facesConfigBean = facesConfigBean;
    }

    public FacesConfigBean getFacesConfigBean() {
        return facesConfigBean;
    }

    protected Configuration getConfiguration() {
        Configuration configuration = new Configuration();
        TemplateLoader templateLoader = new ClassTemplateLoader(this.getClass(), "resources");
        configuration.setTemplateLoader(templateLoader);
        configuration.setObjectWrapper(new DefaultObjectWrapper());

        return configuration;
    }

    protected Configuration getConfiguration(String folder) {
        Configuration configuration = new Configuration();
        TemplateLoader templateLoader = new ClassTemplateLoader(this.getClass(), folder);
        configuration.setTemplateLoader(templateLoader);
        configuration.setObjectWrapper(new DefaultObjectWrapper());

        return configuration;
    }

    private String[] markKeys(String[] keys) {
        String markKeys[] = new String[keys.length];
        for (int i = 0; i < keys.length; i++) {
            markKeys[i] = keys[i].replace('.', '_');
        }

        return markKeys;
    }

    public Map defaultMap() {
        Map defaultMap = new HashMap();
        String timeStamp = DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
        defaultMap.put("timestamp", timeStamp);
        String keys[] = new String[]{"project.taglib.package",
            "project.taglib.prefix",
            "project.taglib.uri",
            "project.tld.fileName",
            "project.verbose",
            "project.warning",
            "project.tld.file",
            "project.base.beaninfo",
            "project.impl.beanDescriptor",
            "project.impl.propertyDescriptor",
            "project.impl.categoryDescriptors",
            "project.version",
            "project.name"
        };
        String markKeys[] = markKeys(keys);
        for (int i = 0; i < markKeys.length; i++) {
            String value = getInternalConfig().getProperty(keys[i]);
            defaultMap.put(markKeys[i], value);
        }
        return defaultMap;
    }

    public String packageName(ComponentBean cb) {
        String componentClass = cb.getComponentClass();
        int last = componentClass.lastIndexOf('.');
        if (last >= 0) {
            return componentClass.substring(0, last);
        } else {
            return "";
        }
    }

    public String tagPackageName(ComponentBean cb) {
        String componentClass = cb.getComponentClass();
        int last = componentClass.lastIndexOf('.');
        if (last >= 0) {

            String pkn = componentClass.substring(0, last);
            return stripExt(pkn);
        } else {
            return "";
        }
    }

    private String stripExt(String pkn) {
        if (pkn.indexOf(".ext") > 0) {
            return simplePackageName(pkn);
        }
        return pkn;
    }

    private String simplePackageName(String fqcn) {

        int last = fqcn.lastIndexOf('.');
        if (last >= 0) {
            return fqcn.substring(0, last);
        } else {
            return fqcn;
        }
    }

    public String simpleClassName(String className) {

        int last = className.lastIndexOf('.');
        if (last >= 0) {
            return className.substring(last + 1);
        } else {
            return className;
        }
    }

    public String componentFamily(ComponentBean cb) {

        String componentFamily = cb.getComponentFamily();
        if (componentFamily == null) {
            ComponentBean bcb = baseComponent(cb);
            do {
                if ((componentFamily != null) || (bcb == null)) {
                    break;
                }
                componentFamily = bcb.getComponentFamily();
                if (componentFamily == null) {
                    bcb = baseComponent(bcb);
                }
            } while (true);
        }
        return componentFamily;
    }

    protected ComponentBean baseComponent(ComponentBean cb) {
        String baseComponentType = cb.getBaseComponentType();
        if (baseComponentType == null) {
            return null;
        }
        ComponentBean bcb = getFacesConfigBean().getComponent(baseComponentType);
        if (bcb == null) {

            throw new IllegalArgumentException(" invalid base component");
        } else {
            return bcb;
        }
    }

    public String rendererType(ComponentBean cb) {

        String rendererType = cb.getRendererType();
        if (rendererType == null) {
            ComponentBean bcb = baseComponent(cb);
            do {
                if ((rendererType != null) || (bcb == null)) {
                    break;
                }
                rendererType = bcb.getRendererType();
                if (rendererType == null) {
                    bcb = baseComponent(bcb);
                }
            } while (true);
        }
        return rendererType;
    }

    protected RendererBean renderer(ComponentBean cb) {

        String rendererType = rendererType(cb);
        if (rendererType == null) {
            return null;
        }
        String componentFamily = componentFamily(cb);
        if (componentFamily == null) {

            throw new IllegalArgumentException("NoComponentFamily");
        }

        RenderKitBean renderKitBean = getFacesConfigBean().getRenderKit(
                getDefaultRenderKitId());
        RendererBean rb = renderKitBean.getRenderer(componentFamily,
                rendererType);

        if (rb == null) {
            throw new IllegalArgumentException("RenderBean componentFamily=" + componentFamily + " rendererType=" + rendererType);
        }

        if (rb == null) {
            throw new IllegalArgumentException("InvalidRendererType");
        } else {
            return rb;
        }
    }

    /*
     * set up rules with property name or default value as required
     */
    protected Map propertyDescriptors(ComponentBean cb, RendererBean rb) {
        try {
            Map map = new TreeMap();
            propertyDescriptors(cb, rb, map);
            PropertyBean idProp = (PropertyBean) map.get("id");
            if (idProp == null || idProp.getPropertyName() == null) {
                throw new IllegalArgumentException("FAILED +" + cb.getComponentClass() + " is not valid defined, no id attribute");
            }

            if (idProp.isHidden()) {
                idProp.setHidden(false);
            }
            return map;
        } catch (IOException ex) {
            Logger.getLogger(AbstractTempGen.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /*
     * set up rules with property name or default value as required
     */
    protected Map propertyDescriptors(ComponentBean cb) {
        try {
            Map map = new TreeMap();
            propertyDescriptors(cb, map);
            PropertyBean idProp = (PropertyBean) map.get("id");
            if (idProp == null || idProp.getPropertyName() == null) {
                throw new IllegalArgumentException("FAILED +" + cb.getComponentClass() + " is not valid defined, no id attribute");
            }

            if (idProp.isHidden()) {
                idProp.setHidden(false);
            }
            return map;
        } catch (IOException ex) {
            Logger.getLogger(AbstractTempGen.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void propertyDescriptors(ComponentBean cb, RendererBean rb,
            Map map)
            throws IOException {

        PropertyBean pbs[] = cb.getProperties();
        for (int i = 0; i < pbs.length; i++) {
            if (map.containsKey(pbs[i].getPropertyName().toLowerCase()) || "valid".equals(pbs[i].getPropertyName())) {
                continue;
            }
            if (!pbs[i].isSuppressed()) {
                map.put(pbs[i].getPropertyName().toLowerCase(), pbs[i]);
            }
        }

        String baseComponentType = cb.getBaseComponentType();
        if (baseComponentType != null) {
            ComponentBean bcb = getFacesConfigBean().getComponent(baseComponentType);
            propertyDescriptors(bcb, rb, map);
        }
    }

    private void propertyDescriptors(ComponentBean cb,
            Map map)
            throws IOException {

        PropertyBean pbs[] = cb.getProperties();
        for (int i = 0; i < pbs.length; i++) {
            if (map.containsKey(pbs[i].getPropertyName().toLowerCase()) || "valid".equals(pbs[i].getPropertyName())) {
                continue;
            }
            if (!pbs[i].isSuppressed()) {
                map.put(pbs[i].getPropertyName().toLowerCase(), pbs[i]);
            }
        }

        String baseComponentType = cb.getBaseComponentType();
        if (baseComponentType != null) {
            ComponentBean bcb = getFacesConfigBean().getComponent(baseComponentType);
            propertyDescriptors(bcb, map);
        }
    }

    protected String stripHtmlName(String s, String word) {

        String tmp = "";
        if (!s.startsWith(word) || s.equalsIgnoreCase(word)) {
            tmp = s;
        } else {
            tmp = s.substring(word.length());
        }
        return tmp;
    }

    public ComponentBean[] getSortedComponentBeans(ComponentBean[] origcbs) {

        ComponentBean[] cbs = new ComponentBean[origcbs.length];
        System.arraycopy(origcbs, 0, cbs, 0, origcbs.length);
        String[] compClasses = new String[cbs.length];
        for (int i = 0; i < compClasses.length; i++) {
            compClasses[i] = cbs[i].getComponentType();
        }
        Arrays.sort(compClasses);
        ArrayList<ComponentBean> sortedCompList = new ArrayList<ComponentBean>();
        for (int i = 0; i < compClasses.length; i++) {
            sortedCompList.add(getFacesConfigBean().getComponent(compClasses[i]));
        }

        return sortedCompList.toArray(new ComponentBean[cbs.length]);
    }

    public RendererBean[] getSortedRendererBeans(ComponentBean[] rdbs) {
        String[] renderClassesKey = new String[rdbs.length];
        for (int i = 0; i < renderClassesKey.length; i++) {
            renderClassesKey[i] = rdbs[i].getComponentFamily() + "|" + rdbs[i].getRendererType();
        }
        Arrays.sort(renderClassesKey);
        ArrayList<RendererBean> sortedRenderBeanList = new ArrayList<RendererBean>();
        RenderKitBean renderKitBean = getFacesConfigBean().getRenderKit(
                getDefaultRenderKitId());
        for (int i = 0; i < renderClassesKey.length; i++) {

            int index = renderClassesKey[i].indexOf("|");
            String componentFamily = renderClassesKey[i].substring(0, index);
            String rendererType = renderClassesKey[i].substring(index + 1);
            RendererBean rendererBean = renderKitBean.getRenderer(componentFamily, rendererType);
            if (rendererBean != null && rendererBean.getRendererType() != null && rendererBean.getRendererClass() != null) {
                sortedRenderBeanList.add(rendererBean);
            }
        }
        RendererBean[] orignrb = sortedRenderBeanList.toArray(new RendererBean[sortedRenderBeanList.size()]);
        RendererBean[] sortedrb = new RendererBean[sortedRenderBeanList.size()];
        System.arraycopy(orignrb, 0, sortedrb, 0, sortedRenderBeanList.size());
        return sortedrb;
    }
}
