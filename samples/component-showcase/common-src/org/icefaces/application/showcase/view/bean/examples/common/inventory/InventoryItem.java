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

import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.Highlight;

import java.io.Serializable;

/**
 * <p>The InventoryItem is a descriptive class that respresent a sudo inventory
 * item. It is made up of a basic description as well the number of avialable
 * units (quantity).</p>
 * <p/>
 * <p>InventoryItem are collected in the Iventory class.</p>
 *
 * @see Inventory
 * @since 1.7
 */
public class InventoryItem implements Serializable {
    // unique inventory id
    private int id;
    // name of inventory item
    private String name;
    // picture name used to define inventory item
    private String pictureName;
    // price of one unit of this inventory
    private double price;
    // stock or quantity of iventory items available
    private int quantity;
    // effect to highlight when an inventory quantity has changed.
    private Effect changeQuantityEffect;

    /**
     * <p>Constructs a new Inventory item by copying the id, name, pictureName
     * and price from the item attribute.  The quantity is set to zero for
     * the newly create InventoryItem object.</p>
     *
     * @param item iventory object who's values will be used to create the new
     *             instance.
     */
    public InventoryItem(InventoryItem item) {
        this(item.getId(), item.getName(), item.getPictureName(),
                item.getPrice(), 0);
    }

    /**
     * <p>Constructs a new Inventory item by copying the id, name, pictureName
     * and price from the item attribute.  The quantity is set to zero for
     * the newly create InventoryItem object.</p>
     *
     * @param id          unique for this instance
     * @param name        name of the inventory item for display purposes
     * @param pictureName picture name of the inventory item
     * @param price       price of each individual item.
     * @param quantity    number of units for this inventory item.
     */
    public InventoryItem(int id, String name, String pictureName,
                         double price, int quantity) {
        this.id = id;
        this.name = name;
        this.pictureName = pictureName;
        this.price = price;
        this.quantity = quantity;
        changeQuantityEffect = new Highlight("#fda505");
        changeQuantityEffect.setFired(true);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPictureName() {
        return pictureName;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    /**
     * Increments the quqntity count and resets the Effect to fire on the
     * next render pass.
     */
    public void incrementQuantity() {
        quantity++;
        // show an effect indicating a change. 
        changeQuantityEffect.setFired(false);
    }

    /**
     * Decrements the quqntity count and resets the Effect to fire on the
     * next render pass.
     */
    public void decrementQuantity() {
        quantity--;
        changeQuantityEffect.setFired(false);
    }

    public Effect getChangeQuantityEffect() {
        return changeQuantityEffect;
    }
}
