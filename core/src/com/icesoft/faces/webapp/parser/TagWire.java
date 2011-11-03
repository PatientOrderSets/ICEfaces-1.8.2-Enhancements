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

package com.icesoft.faces.webapp.parser;

import org.xml.sax.Attributes;

import javax.servlet.jsp.tagext.Tag;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is needed to help wire the tags together.  Because tags do not
 * contain lists of their children, we manage that here.  This object gets
 * placed on the digester stack after the tag object itself.  When the tag rule
 * fires, it wires everything up so that we can traverse the tree of tags
 * later.
 *
 * @author Steve Maryka
 */
public class TagWire {
    private Tag tag;
    private Attributes attributes;
    private List children = new ArrayList();

    /**
     * MyTag setter.
     *
     * @param tag tag
     */
    public void setTag(Tag tag) {
        this.tag = tag;
    }

    /**
     * MyTag getter.
     *
     * @return tag
     */
    public Tag getTag() {
        return tag;
    }

    /**
     * Adds a child wire to this tag wire.
     *
     * @param child child to add.
     */
    public void addChild(TagWire child) {
        children.add(child);
    }

    /**
     * Returns vector of children.
     *
     * @return Vector of children tag wires.
     */
    public List getChildren() {
        return children;
    }

    /**
     * Attributes getter.
     *
     * @return tag attributes
     */
    public Attributes getAttributes() {
        return attributes;
    }

    /**
     * Attribute setter.
     *
     * @param attrs attributes
     */
    public void setAttributes(Attributes attrs) {
        attributes = attrs;
    }


    /**
     * Replace a child of this tagWire with all its children, respecting
     * the original order. Does nothing if child argument isn't a child
     * of this TagWire
     *  
     * @param child The TagWire to replace
     */
    public void replaceTagWireWithChildren(TagWire child) {

        TagWire myChild;
        for (int idx = 0; idx < children.size(); idx ++ ) {
            myChild = (TagWire) children.get(idx);
            if (myChild.equals( child ) ) {
                ArrayList newChildren = new ArrayList (  );

                List temp = children.subList(0, idx);
                newChildren.addAll(temp) ;
                newChildren.addAll( child.getChildren() );

                if (idx < children.size()-1 ) {
                    temp = children.subList(idx+1, children.size());
                    newChildren.addAll( temp );
                }
                children = newChildren;
            }
        }
    }
}
