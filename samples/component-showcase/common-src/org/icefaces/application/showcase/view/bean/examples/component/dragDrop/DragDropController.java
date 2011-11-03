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
package org.icefaces.application.showcase.view.bean.examples.component.dragDrop;

import com.icesoft.faces.component.dragdrop.DndEvent;
import com.icesoft.faces.component.dragdrop.DragEvent;
import com.icesoft.faces.component.ext.HtmlPanelGroup;
import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.Highlight;
import org.icefaces.application.showcase.util.RandomNumberGenerator;
import org.icefaces.application.showcase.view.bean.examples.common.inventory.Inventory;
import org.icefaces.application.showcase.view.bean.examples.common.inventory.InventoryItem;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.util.ArrayList;
import java.util.Map;
import java.io.Serializable;

/**
 * <p>The DragDropController is responsible for handling all actions
 * involved when dragging and droping items into the cart as well as
 * removal of items from the cart.</p>
 * <p>This class also provides access to two data models; shopingCart and
 * storeInventory.  When the the DragDropController is initialized the two data
 * models are initialized and more importantly the StoreIventory data is
 * generated.</p>
 *
 * @since 1.7
 */
public class DragDropController implements Serializable {

    /**
     * Main data models for this example.
     */
    // Store Iventory maintains the number of stock available for a given
    // stock item.
    protected Inventory storeInventory;

    // Shopping cart maintains the number of stock items that are contained
    // in a users shopping cart.
    protected Inventory shoppingCart;

    private Effect cartTableEffect;
    private Effect cartEffect;

    /**
     * Creates a new instace and initializes the store and chopping cart
     * Iventory models.
     */
    public DragDropController() {
        init();
    }

    /**
     * Initializes storeInventory with four default items that have radomly
     * generated price and quantity values.
     */
    protected void init() {

        // build simple effects
        cartTableEffect = new Highlight("#fda505");
        cartTableEffect.setFired(true);
        cartEffect = new Highlight("#fda505");
        cartEffect.setFired(true);

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

        shoppingCart = new Inventory();

    }

    /**
     * <p>Called when a items in the users shopping cart is to be removed.  The
     * method assums that the &ltf:param /&gt; method is used to pass in
     * an named attribue "inventoryId".  This inventoryId is used as key when
     * finding the respective IventoryItem to remove from the shoppingCart
     * inventory. <p>
     *
     * @param event JSF action event, not used by method
     */
    public void returnShoppingCartItem(ActionEvent event) {
        // Get the inventory item ID from the context.
        FacesContext context = FacesContext.getCurrentInstance();
        Map map = context.getExternalContext().getRequestParameterMap();
        int inventoryId = Integer.parseInt((String) map.get("inventoryId"));

        // find the corresponding inventory and return it to the store
         InventoryItem item;
         for(int i = 0, max= shoppingCart.getInventory().size(); i < max; i++){
             item = (InventoryItem) shoppingCart.getInventory().get(i);
             if (item.getId() == inventoryId) {
                // return item to store
                storeInventory.addInventoryItem(item);
                // remove from shopping cart, and force removal of item when
                // quantity reaches zero.
                shoppingCart.removeInventoryItem(item, true);
                // we're done.
                break;
            }
        }
    }

    /**
     * Adds the InventoryItem contained in the DragEvent object the shoppingCart
     * inventory.
     *
     * @param event DragEvent object which contains the IvenotryItem object
     *              that was dragged by the user on the ShoppingCart dropzone on the JSPX
     *              page.
     */
    public void addShoppingCartItem(DragEvent event) {
        // we are only concerned with the drop event
        if (event.getEventType() == DndEvent.DROPPED) {
            String targetId = event.getTargetClientId();
            if ((targetId != null)) {
                // the inventory item being dragged
                InventoryItem inventoryItem =
                        (InventoryItem) ((HtmlPanelGroup) event.getComponent()).getDragValue();

                // make sure we have inventory to sell
                if (inventoryItem.getQuantity() > 0) {
                    // remove the inventory item from cart but don't remove
                    // the item on quanity of zero. 
                    storeInventory.removeInventoryItem(inventoryItem, false);

                    // add the inventory item to the shopping chart
                    shoppingCart.addInventoryItem(new InventoryItem(inventoryItem));

                }
            }
        }
        // fire effect when a drag is started. 
        else if (event.getEventType() == DndEvent.HOVER_START) {
            cartTableEffect.setFired(false);
            cartEffect.setFired(false);
        }

    }

    public Inventory getStoreInventory() {
        return storeInventory;
    }

    public Inventory getShoppingCart() {
        return shoppingCart;
    }

    public Effect getCartTableEffect() {
        return cartTableEffect;
    }

    public Effect getCartEffect() {
        return cartEffect;
    }
}
