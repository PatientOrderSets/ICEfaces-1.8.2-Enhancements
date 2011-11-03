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
package org.icefaces.application.showcase.view.bean.examples.common.inventory;

import java.util.ArrayList;

/**
 * <p>The Inventory class is resposible for storing IventoryItems and
 * the total price of the inventory items.</p>
 *
 * @since 1.7
 */
public class Inventory implements InventoryInterface {

    // collection of inventory items.
    private ArrayList inventory;
    // total value of inventory
    private double inventoryPriceTotal = 0.0;
    private int inventoryUnitTotal = 0;

    /**
     * Crates a new instance of Inventory.
     */
    public Inventory() {
        inventory = new ArrayList();
    }

    /**
     * Gets the collection of Inventory items.
     *
     * @return current inventory
     */
    public ArrayList getInventory() {
        return inventory;
    }

    /**
     * Gets the total price of the InventoryItems.
     *
     * @return total price of all InventoryItems.
     */
    public double getInventoryPriceTotal() {
        return inventoryPriceTotal;
    }

    /**
     * Gets the total number of items in the ineventory.
     *
     * @return inventory unit total.
     */
    public int getInventoryUnitTotal() {
        return inventoryUnitTotal;
    }

    /**
     * <p>Adds the specified InventoryItem to the Inventory.  If an Item with
     * the same ID all ready exists then then its quantity count is incremented.
     * If the newItem does not exist in the Iventory it is added to the inventory
     * and its count is unaffected.</p>
     *
     * @param newItem new IventoryItem to add to the Inventory.
     */
    public void addInventoryItem(InventoryItem newItem) {

        boolean found = false;
        // if iventory item exist increment quantity count. 
        InventoryItem item;
        for(int i= 0, max= inventory.size(); i < max; i++){
            item = (InventoryItem)inventory.get(i);
            if (item.getId() == newItem.getId()) {
                item.incrementQuantity();
                found = true;
                break;
            }
        }

        // add item if not found in inventory.
        if (!found) {
            inventory.add(newItem);
            newItem.incrementQuantity();
        }

        // update invenory
        recalculateIventoryTotal();
    }

    /**
     * <p>Removes the specified InventoryItem to the Inventory.  If an Item with
     * the same ID all ready exists then then its quantity count is decremented.
     * If the newItem does not exist in the Iventory, no change is made.</p>
     *
     * @param newItem new IventoryItem to add to the Inventory.
     */
    public void removeInventoryItem(InventoryItem newItem, boolean removeItemOnZero) {

        // if iventory item exist increment quantity count.
        InventoryItem foundItem = null;
        InventoryItem item;
        for(int i= 0, max= inventory.size(); i < max; i++){
            item = (InventoryItem)inventory.get(i);
            if (item.getId() == newItem.getId()) {
                item.decrementQuantity();
                foundItem = item;
                break;
            }
        }

        // remove the item if qauntity is less then zero and is allowd by
        // removeItemOnZero
        if (removeItemOnZero && foundItem != null &&
                foundItem.getQuantity() <= 0) {
            inventory.remove(foundItem);
        }

        // update invenory
        recalculateIventoryTotal();
    }

    /**
     * Utility method for recalculateding the total inventory price.
     */
    private void recalculateIventoryTotal() {
        inventoryPriceTotal = 0;
        inventoryUnitTotal = 0;
        InventoryItem item;
        for(int i= 0, max= inventory.size(); i < max; i++){
            item = (InventoryItem)inventory.get(i);
            inventoryPriceTotal += item.getPrice() * item.getQuantity();
            inventoryUnitTotal += item.getQuantity();
        }
    }
}
