package com.cjpowered.learn.inventory;

import com.cjpowered.learn.marketing.MarketingInfo;

public interface InventoryNeeded {
	
	
	public int amountNeeded(int normal, MarketingInfo marketing, Item item);

}
