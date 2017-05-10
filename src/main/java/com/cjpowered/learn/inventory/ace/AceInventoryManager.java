package com.cjpowered.learn.inventory.ace;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cjpowered.learn.inventory.InventoryDatabase;
import com.cjpowered.learn.inventory.InventoryManager;
import com.cjpowered.learn.inventory.Item;
import com.cjpowered.learn.inventory.Order;
import com.cjpowered.learn.marketing.MarketingInfo;
import com.cjpowered.learn.marketing.Season;



public final class AceInventoryManager implements InventoryManager {

	private final InventoryDatabase database;
	private final MarketingInfo marketing;

	public AceInventoryManager(InventoryDatabase db, MarketingInfo marketing) {
		this.database = db;
		this.marketing = marketing;

	}

	@Override
	public List<Order> getOrders(final LocalDate today) {//I'm getting today but immediately passing it. Bad?
		List<Item> items = database.stockItems();
		List<Order> orders = new ArrayList<>();

		for (Item item : items) 
		{
			int onHand = database.onHand(item);
			int want = item.wantOnHand(today, marketing) - onHand;
			
			if (want > 0)
				orders.add(new Order(item, want));
		
			}
		
		return orders;
	}
	


}
