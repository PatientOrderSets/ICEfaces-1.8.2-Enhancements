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

package org.icefaces.application.showcase.view.bean.examples.layoutPanel.panelPositioned;

import com.icesoft.faces.component.panelpositioned.PanelPositionedEvent;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

/**
 * Simple bean which holds a list of objects which is manipulated by the
 * position panel
 */
public class PositionedPanelBean implements Serializable {

    private List people;

    public PositionedPanelBean() {

        // add some objects to the list
        people = new ArrayList(7);
        people.add(new PostionedPanelPerson("Mary Smith"));
        people.add(new PostionedPanelPerson("James Johnson"));
        people.add(new PostionedPanelPerson("Patricia Williams"));
        people.add(new PostionedPanelPerson("John Jones"));
        people.add(new PostionedPanelPerson("Linda Brown"));
        people.add(new PostionedPanelPerson("Robert Davis"));
        people.add(new PostionedPanelPerson("Barbara Miller"));
        resetRank();
    }


    private void resetRank() {
        for (int i = 0; i < people.size(); i++) {
            ((PostionedPanelPerson) people.get(i)).setRank(i + 1);
        }
    }


    public void changed(PanelPositionedEvent evt) {
        resetRank();
        if (evt.getOldIndex() >= 0) {
            ((PostionedPanelPerson) people.get(
                    evt.getIndex())).getEffect().setFired(false);
        }
    }

    public List getPeople() {
        return people;
    }

    public void setPeople(List people) {
        this.people = people;
    }

}
