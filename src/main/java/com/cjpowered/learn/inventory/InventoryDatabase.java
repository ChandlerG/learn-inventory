package com.cjpowered.learn.inventory;

import java.util.List;

public interface InventoryDatabase {

    /**
     * Fetch number on-hand.
     *
     * @param item
     *            item to query
     *
     * @return fetched value
     */
    int onHand(Item item);

    /**
     * Fetch list of all stocked items.
     *
     * @return fetched value
     */
    List<Item> stockItems();
    
    /**
     * Fetch number on order
     * @param item
     *            item to query
     * @return fetched value
     */
    int onOrder(Item item);
    
    
    /**
     * Change the required on-hand amount for an item.
     * 
     * @param item item to change
     * 
     * @param newAmount new nominal stock level
     */
    void setRequiredOnHand(Item item, int newAmount);
    
    
    

}
