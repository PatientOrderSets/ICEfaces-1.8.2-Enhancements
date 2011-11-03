package com.icesoft.faces.application.state;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.FacesException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.io.Externalizable;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;

/**
 *  This class contains utility classes for converting a UIComponentTree
 * to a fully serializeable version and back. 
 */
public class Util {

      public static  UIComponent newInstance(TreeCaptureNode tcn, Map classMap)
            throws FacesException {

        try {
            Class t = (Class) ((classMap != null) ? classMap.get(tcn.className) : null);
            if (t == null) {
                t = loadClass(tcn.className, tcn);
                if (t != null && classMap != null) {
                    classMap.put(tcn.className, t);
                } else {
                    throw new NullPointerException();
                }
            }

            UIComponent comp = (UIComponent) t.newInstance();
            comp.setId(tcn.id);
            return comp;

        } catch (Exception e) {
            throw new FacesException(e);
        }
    }

    /**
     * Capture a Node of the tree structure in an object that is serializable. 
     * @param tree
     * @param parent
     * @param component
     */
    public static void captureChildNode(CaptureArray tree,
                                     int parent,
                                     UIComponent component) {

        if (!component.isTransient()) {
            TreeCaptureNode tcn = new TreeCaptureNode(parent, component);
            int pos = tree.index;
            tree.capture(tcn);
            captureAll(tree, pos, component);
        }
    }


    // There can be children of facet nodes, as this method captures the parentIdx.
    public static void captureFacetNode(CaptureArray tree,
                                     int parent,
                                     String name,
                                     UIComponent component) {

        if (!component.isTransient()) {
            FacetCaptureNode fcn = new FacetCaptureNode(parent, name, component);
            int pos = tree.index;
            tree.capture(fcn);
            captureAll(tree, pos, component);
        }
    }

    public static void captureAll(CaptureArray tree,
                                    int pos,
                                    UIComponent c) {

        int sz = c.getChildCount();
        if (sz > 0) {
            List child = c.getChildren();
            for (int i = 0; i < sz; i++) {
                captureChildNode(tree, pos, (UIComponent) child.get(i));
            }
        }

        Map m = c.getFacets();
        if (m.size() > 0) {
            Set s = m.entrySet();
            Iterator i = s.iterator();
            while (i.hasNext()) {
                Map.Entry entry = (Map.Entry) i.next();
                captureFacetNode(tree,
                             pos,
                             (String) entry.getKey(),
                             (UIComponent) entry.getValue() );
            }
        }
    }

     public static ClassLoader getCurrentLoader(Object fallbackClass) {
        ClassLoader loader =
                Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = fallbackClass.getClass().getClassLoader();
        }
        return loader;
    }

    public static Class loadClass(String name,
                                     Object fallbackClass)
           throws ClassNotFoundException {
           ClassLoader loader = getCurrentLoader(fallbackClass);

           if (name.charAt(0) == '[') {
               return Class.forName(name, true, loader);
           } else {
               return loader.loadClass(name);
           }
        }

    public static UIViewRoot restoreTree(Object[] tree, Map classMap)
                throws FacesException {

            UIComponent c;
            FacetCaptureNode fn;
            TreeCaptureNode tn;
            for (int i = 0; i < tree.length; i++) {
                if (tree[i]instanceof FacetCaptureNode) {
                    fn = (FacetCaptureNode) tree[i];
                    c = newInstance(fn, classMap);
                    tree[i] = c;
                    if (i != fn.parentIdx) {
                        ((UIComponent) tree[fn.parentIdx]).getFacets()
                                .put(fn.facetName, c);
                    }

                } else {
                    tn = (TreeCaptureNode) tree[i];
                    c = newInstance(tn, classMap);
                    tree[i] = c;
                    if (i != tn.parentIdx) {
                        ((UIComponent) tree[tn.parentIdx]).getChildren().add(c);
                    }
                }
            }
            return (UIViewRoot) tree[0];
        }

    /**
     * TreeCaptureNode captures the id of the component, the index of the parentIdx within the list
     * and the name of the class.
     */
    private static class TreeCaptureNode implements Externalizable {

        private static final String NO_ID = "";

        public String className;
        public String id;

        public int parentIdx;
        static final long serialVersionUID = -214427652801663237L;

        public TreeCaptureNode() { }

        /**
         * Describe a node with a parentIdx
         * @param parent Parent position in the Array
         * @param c The component to capture
         */
        public TreeCaptureNode(int parent, UIComponent c) {

            this.parentIdx = parent;
            this.id = c.getId();
            this.className = c.getClass().getName();
        }

        public void writeExternal(ObjectOutput out) throws IOException {

            out.writeInt(this.parentIdx);
            out.writeUTF(this.className);
            if (this.id != null) {
                out.writeUTF(this.id);
            } else {
                out.writeUTF(NO_ID);
            }
        }


        public void readExternal(ObjectInput in)
                throws IOException, ClassNotFoundException {

            this.parentIdx = in.readInt();
            this.className = in.readUTF();
            this.id = in.readUTF();
            if (id.length() == 0) {
                id = null;
            }
        }
    }

    private static final class FacetCaptureNode extends TreeCaptureNode {

        public String facetName;
        private static final long serialVersionUID = -4529208165281928474L;

        public FacetCaptureNode() { }

        /**
         *
         * @param parent Parent node position in array
         * @param name Name of Facet
         * @param c Component containing facet
         */
        public FacetCaptureNode(int parent,
                         String name,
                         UIComponent c) {

            super(parent, c);
            this.facetName = name;
        }


        public void readExternal(ObjectInput in)
                throws IOException, ClassNotFoundException {

            super.readExternal(in);
            this.facetName = in.readUTF();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            super.writeExternal(out);
            out.writeUTF(this.facetName);
        }
    }

    /**
     * Capture the details in an array directly 
     */
    public static class CaptureArray {
        int index;
        Object[] capturedNodes;

        public CaptureArray() {
            capturedNodes = new Object[64];
        } 

        private void expand() {
            Object[] newArray = new Object[ capturedNodes.length*2 ];
            System.arraycopy(capturedNodes, 0, newArray, 0, capturedNodes.length);
            capturedNodes = newArray;
        }

        
        public void capture(Object n) {
            if (index == capturedNodes.length) {
                expand();
            }
            capturedNodes[index++] = n;
        }

        public Object[] toArray() {
            Object[] returnVal = new Object[index];
            System.arraycopy(capturedNodes, 0, returnVal, 0, index);
            return returnVal;
        }
    }
}
