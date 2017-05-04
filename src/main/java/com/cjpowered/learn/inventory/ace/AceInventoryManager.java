package com.cjpowered.learn.inventory.ace;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cjpowered.learn.inventory.InventoryDatabase;
import com.cjpowered.learn.inventory.InventoryManager;
import com.cjpowered.learn.inventory.Item;
import com.cjpowered.learn.inventory.Order;

public final class AceInventoryManager implements InventoryManager {

	
	private final InventoryDatabase database;
	
	public AceInventoryManager(InventoryDatabase db)
	{
		database = db;
	}
	
     @Override
    public List<Order> getOrders(final LocalDate today) {
        List<Item> items = database.stockItems();
        List <Order> orders = new ArrayList<>();
       
        
        for (Item item: items)
        {
        	int onHand = database.onHand(item);
        	int toOrder = item.wantOnHand() - onHand;
        	final Order order = new Order(item, toOrder);
        	orders.add(order);
        }
        
        
        return orders;
    }

}
