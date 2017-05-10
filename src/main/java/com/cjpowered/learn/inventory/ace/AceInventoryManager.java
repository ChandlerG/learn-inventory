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

		for (Item item : items) {
			
			if (isOrderable(item, today))
			{
			int toOrder = numNeeded(item, today);//should I pass marketing? Or is it cool for it be a global? DISCUSS GLOBALS
			
			if (toOrder > 0) {
				final Order order = new Order(item, toOrder);
				orders.add(order);
			}
			}
		}
		return orders;
	}
	
	private int numNeeded(Item item, LocalDate today)
	{
		int toOrder = 0;
		int onHand = database.onHand(item);
		int wantOnHand = item.wantOnHand();
		boolean onSale = marketing.onSale(item);
		boolean isSeasonal = item.isSeasonal(); // prevents repeated access
												// but may obfuscate?

		boolean inSeason = false; // for compilation warnings? e.g. might
									// not've been initialized, style of false then if true bad?

		
			if (isSeasonal)// this skips checking the equals... necessary?  Or just too much?
			{
				if (marketing.season(today).equals(item.season())) // enums overload? double check?
					inSeason = true;
			}

			// cascading ifs. Could these be more clean?
			if (inSeason) {
				int seasonalNeed = marketing.seasonalAmount(wantOnHand);
				if (onSale) {
					int saleNeed = marketing.saleAmount(wantOnHand);
					wantOnHand = Math.max(saleNeed, seasonalNeed);
				} else {
					wantOnHand = seasonalNeed;
				}
			} else {
				if (onSale) {
					wantOnHand = marketing.saleAmount(wantOnHand);
				}
			}

			 toOrder = wantOnHand - onHand;

		
		
		return toOrder;
	}

	private boolean isOrderable(Item item, LocalDate today)// multiple breakpoints?
	{
		if (item.isFirstOrderableOnly()) {
			if (today.getDayOfMonth() == 1) {
				return true;
			} else
				return false;
		}
		return true;
	}

}
