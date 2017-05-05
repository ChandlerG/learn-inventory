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

import test.com.cjpowered.learn.inventory.MarketingTemplate;

public final class AceInventoryManager implements InventoryManager {

	
	private final InventoryDatabase database;
	private final MarketingInfo marketing;

	
	public AceInventoryManager(InventoryDatabase db, MarketingInfo marketing)
	{
		database = db;
		this.marketing = marketing;
		
	}
	

	
     @Override
    public List<Order> getOrders(final LocalDate today) {
        List<Item> items = database.stockItems();
        List <Order> orders = new ArrayList<>();
        Season currSeason = marketing.season(today);
       
        
        for (Item item: items)
        {
        	int onHand = database.onHand(item);
        	int wantOnHand = item.wantOnHand();
        	boolean onSale = marketing.onSale(item);
        	boolean isSeasonal = item.isSeasonal(); // prevents repeated access but may obfuscate?
        	
        	boolean inSeason = false; //for compilation warnings? e.g. might not've been initialized
        	if (isSeasonal)//this skips checking the equals... necessary? Or just too much?
        	{
        	if (marketing.season(today).equals(item.season())) //enums equals overloaded? should dbl check
        		inSeason = true;
        	}
        	
        	
        	//cascading ifs. Could these be more clean? 
        	if (inSeason)
        	{
        		int seasonalNeed = marketing.seasonalAmount(wantOnHand);
        		if (onSale)
        		{
        			int saleNeed = marketing.saleAmount(wantOnHand);//should this be a func? Like "calc seasonal need?" Does that belong to marketing's database? VERY IMPORTANT
        			wantOnHand = Math.max(saleNeed,  seasonalNeed);
        		}
        		else
        		{
        			wantOnHand = seasonalNeed;
        		}
        	}
        	else
        	{
        		if (onSale)
        		{
        			wantOnHand = marketing.saleAmount(wantOnHand);
        		}
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
