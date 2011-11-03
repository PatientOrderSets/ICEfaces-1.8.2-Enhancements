package com.icesoft.faces.context;

import com.icesoft.faces.util.DOMUtils;
import com.icesoft.faces.webapp.command.Reload;
import com.icesoft.faces.webapp.command.UpdateElements;
import com.icesoft.faces.webapp.http.common.Response;
import com.icesoft.faces.webapp.http.common.standard.NoCacheContentHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class PushModeSerializer implements DOMSerializer {
    private BridgeFacesContext.DocumentStore store;
    private View view;
    private BridgeFacesContext context;
    private String viewNumber;

    public PushModeSerializer(BridgeFacesContext.DocumentStore store, View view, BridgeFacesContext context, String viewNumber) {
        this.store = store;
        this.view = view;
        this.context = context;
        this.viewNumber = viewNumber;
    }

    public void serialize(final Document document) throws IOException {
        Node[] changed = DOMUtils.domDiff(store.load(), document);
        HashMap depthMaps = new HashMap();
        for (int i = 0; i < changed.length; i++) {
            Element changeRoot =
                    DOMUtils.ascendToNodeWithID(changed[i]);
            changed[i] = changeRoot;
            Integer depth = new Integer(getDepth(changeRoot));
            HashSet peers = (HashSet) depthMaps.get(depth);
            if (null == peers) {
                peers = new HashSet();
                depthMaps.put(depth, peers);
            }
            //place the node in a collection of all nodes
            //at its same depth in the DOM
            peers.add(changeRoot);
        }
        Iterator allDepths = depthMaps.keySet().iterator();
        while (allDepths.hasNext()) {
            Integer baseDepth = (Integer) allDepths.next();
            Iterator checkDepths = depthMaps.keySet().iterator();
            while (checkDepths.hasNext()) {
                Integer checkDepth = (Integer) checkDepths.next();
                if (baseDepth.intValue() < checkDepth.intValue()) {
                    pruneAncestors(baseDepth, (HashSet) depthMaps.get(baseDepth),
                            checkDepth, (HashSet) depthMaps.get(checkDepth));
                }
            }
        }

        //Merge all remaining elements at different depths
        //Collection is a Set so duplicates will be discarded
        HashSet topElements = new HashSet();
        Iterator allDepthMaps = depthMaps.values().iterator();
        while (allDepthMaps.hasNext()) {
            topElements.addAll((HashSet) allDepthMaps.next());
        }

        if (!topElements.isEmpty()) {
            boolean reload = false;
            int j = 0;
            Element[] elements = new Element[topElements.size()];
            HashSet dupCheck = new HashSet();
            //copy the succsessful changed elements and check for change
            //to head or body
            for (int i = 0; i < changed.length; i++) {
                Element element = (Element) changed[i];
                String tag = element.getTagName();
                //send reload command if 'html', 'body', or 'head' elements need to be updated (see: ICE-3063)
                reload = reload || "html".equalsIgnoreCase(tag) || "head".equalsIgnoreCase(tag);
                if (topElements.contains(element)) {
                    if (!dupCheck.contains(element)) {
                        dupCheck.add(element);
                        elements[j++] = element;
                    }
                }
            }
            if (reload) {
                //reload document instead of applying an update for the entire page (see: ICE-2189)
                view.preparePage(new PreparedPage(document));
                view.put(new Reload(viewNumber));
            } else {
                view.put(new UpdateElements(elements));
            }
        }
    }

    //prune the children by looking for ancestors in the parents collection 
    private void pruneAncestors(Integer parentDepth, Collection parents,
                                Integer childDepth, Collection children) {
        Iterator parentList = parents.iterator();
        while (parentList.hasNext()) {
            Node parent = (Node) parentList.next();
            Iterator childList = children.iterator();
            while (childList.hasNext()) {
                Node child = (Node) childList.next();
                if (isAncestor(parentDepth, parent, childDepth, child)) {
                    childList.remove();
                }
            }

        }
    }

    private int getDepth(Node node) {
        int depth = 0;
        Node parent = node;
        while ((parent = parent.getParentNode()) != null) {
            depth++;
        }
        return depth;
    }

    private boolean isAncestor(Integer parentDepth, Node parent,
                               Integer childDepth, Node child) {
        if (!parent.hasChildNodes()) {
            return false;
        }
        Node testParent = child;
        int testDepth = childDepth.intValue();
        int stopDepth = parentDepth.intValue();
        while (((testParent = testParent.getParentNode()) != null) &&
                (testDepth > stopDepth)) {
            testDepth--;
            if (testParent.equals(parent)) {
                return true;
            }
        }
        return false;
    }

    private class PreparedPage extends NoCacheContentHandler {
        private final Document document;

        public PreparedPage(Document document) {
            super("text/html", "UTF-8");
            this.document = document;
        }

        public void respond(Response response) throws Exception {
            super.respond(response);
            Writer writer = new OutputStreamWriter(response.writeBody(), "UTF-8");
            DOMSerializer serializer = new NormalModeSerializer(context, writer);
            serializer.serialize(document);
        }
    }
}
