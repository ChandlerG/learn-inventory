package com.cjpowered.learn.inventory.ace;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cjpowered.learn.inventory.InventoryDatabase;
import com.cjpowered.learn.inventory.InventoryManager;
import com.cjpowered.learn.inventory.Item;
import com.cjpowered.learn.inventory.Order;

import test.com.cjpowered.learn.inventory.MarketingTemplate;

public final class AceInventoryManager implements InventoryManager {

	
	private final InventoryDatabase database;
	private final MarketingTemplate marketing;
	private final int saleBuffer;
	
	public AceInventoryManager(InventoryDatabase db, MarketingTemplate marketing)
	{
		database = db;
		this.marketing = marketing;
		this.saleBuffer = marketing.saleBuffer();
	}
	

	
     @Override
    public List<Order> getOrders(final LocalDate today) {
        List<Item> items = database.stockItems();
        List <Order> orders = new ArrayList<>();
       
        
        for (Item item: items)
        {
        	int onHand = database.onHand(item);
        	int wantOnHand = item.wantOnHand();
        	boolean onSale = marketing.onSale(item);
        	if (onSale)
        	{
        		wantOnHand += saleBuffer;
        	}
        	
        	int toOrder = wantOnHand - onHand;
        
        	if (toOrder > 0)
        	{
        	final Order order = new Order(item, toOrder);
        	orders.add(order);
        	}
        }
        
        
        return orders;
    }

}
