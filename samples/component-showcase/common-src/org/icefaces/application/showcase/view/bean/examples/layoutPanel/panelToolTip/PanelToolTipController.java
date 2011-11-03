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
package org.icefaces.application.showcase.view.bean.examples.layoutPanel.panelToolTip;

import com.icesoft.faces.component.DisplayEvent;
import org.icefaces.application.showcase.util.MessageBundleLoader;
import org.icefaces.application.showcase.util.RandomNumberGenerator;
import org.icefaces.application.showcase.view.bean.examples.common.inventory.Inventory;
import org.icefaces.application.showcase.view.bean.examples.common.inventory.InventoryItem;

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.component.ValueHolder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.Serializable;

/**
 * @since 1.7
 */
public class PanelToolTipController implements Serializable {


    /**
     * Main data models for this example.
     */
    // Store Iventory maintains the number of stock available for a given
    // stock item.
    private Inventory storeInventory;

    private PanelToolTipModel panelToolTipModel;

    private ArrayList cityList = new ArrayList();


    /**
     * Creates a new instace and initializes the store and chopping cart
     * Iventory models.
     */
    public PanelToolTipController() {
        panelToolTipModel = new PanelToolTipModel();
        init();
    }

    /**
     * Initializes storeInventory with four default items that have radomly
     * generated price and quantity values.
     */
    private void init() {
        RandomNumberGenerator randomNumberGenerator =
                RandomNumberGenerator.getInstance();

        // we need to create four inventory items which has random values
        // for price and inventory count.
        storeInventory = new Inventory();
        ArrayList store = storeInventory.getInventory();
        store.add(new InventoryItem(1, "Laptop", "laptop",
                randomNumberGenerator.getRandomDouble(699, 3200),
                (int) randomNumberGenerator.getRandomDouble(15, 20)));
        store.add(new InventoryItem(2, "Monitor", "monitor",
                randomNumberGenerator.getRandomDouble(299, 799),
                (int) randomNumberGenerator.getRandomDouble(5, 10)));
        store.add(new InventoryItem(4, "Desktop", "desktop",
                randomNumberGenerator.getRandomDouble(299, 499),
                (int) randomNumberGenerator.getRandomDouble(25, 50)));
        store.add(new InventoryItem(3, "PDA", "pda",
                randomNumberGenerator.getRandomDouble(60, 300),
                (int) randomNumberGenerator.getRandomDouble(5, 20)));
    }

    /**
     * @param event
     */
    public void displayListener(DisplayEvent event) {
        // updated the city list for the city that activated the tooltip
        if (event.isVisible()) {
            String province = event.getContextValue().toString();
            final int len = 5;
            ArrayList cities = new ArrayList(len);
            for(int i = 1; i <= len; i++) {
                cities.add(MessageBundleLoader.getMessage(province + ".city"+i+".label"));
            }
            cityList = cities;
        }
    }

    public Inventory getStoreInventory() {
        return storeInventory;
    }

    public PanelToolTipModel getPanelToolTipModel() {
        return panelToolTipModel;
    }

    public ArrayList getCityList() {
        return cityList;
    }
}
