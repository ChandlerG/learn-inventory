package com.cjpowered.learn.inventory;

import java.time.LocalDate;
import java.util.Set;

import com.cjpowered.learn.marketing.MarketingInfo;

public class StockedItem implements Item {
	
	
	private final int shouldHave;
	private  boolean isFirst;
	private final int minOrderAmount;
	private final Set<InventoryNeeded> stockCalcs;
	
	public StockedItem(int wantOnHand, boolean isFirst, int minOrderAmount, Set<InventoryNeeded> stockCalcs)
	{
		this.shouldHave = wantOnHand;
		this.isFirst = isFirst;	
		this.minOrderAmount = minOrderAmount;
		this.stockCalcs = stockCalcs;
	}
	
	public int getNormalStock()
	{
		return shouldHave;
	}
	

	
	@Override
	public int toOrder(LocalDate today, MarketingInfo mi, int onHandAndOnOrder)
	{
		int needed = shouldHave;
		if (isFirst && today.getDayOfMonth() != 1)
		{
			return 0;
		}
		for (InventoryNeeded iNeeded: stockCalcs)
		{
			needed = Math.max(needed, iNeeded.amountNeeded(shouldHave, mi, this));
		}
		
		
		
		if (onHandAndOnOrder >= .8 * needed)
		{
			return 0;
		}
		
		
		
		if ((needed - onHandAndOnOrder) % minOrderAmount != 0) 
		{
			int multiple = (needed-onHandAndOnOrder) / minOrderAmount;
			int toOrder = (multiple + 1) * minOrderAmount;
			while (toOrder + onHandAndOnOrder > needed)
			{
				toOrder -= minOrderAmount;
			}
			needed = toOrder;
		} 
		else 
		{
			needed = needed - onHandAndOnOrder;
		}
		return needed;
	}
	

}
