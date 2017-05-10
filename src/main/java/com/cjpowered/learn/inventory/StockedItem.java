package com.cjpowered.learn.inventory;

import java.time.LocalDate;

import com.cjpowered.learn.marketing.MarketingInfo;
import com.cjpowered.learn.marketing.Season;

public class StockedItem implements Item {
	
	
	private final int shouldHave;
	
	private  boolean isFirst;
	
	public StockedItem(int wantOnHand, boolean isFirst)
	{
		this.shouldHave = wantOnHand;
		this.isFirst = isFirst;	
	}
	

	
	@Override
	public int wantOnHand(LocalDate today, MarketingInfo mi)
	{
		if (isFirst && !(today.getDayOfMonth() == 1))
		{
			return 0;
		}
		else
		{
		if (mi.onSale(this))
			return mi.saleAmount(shouldHave);
		else
			return shouldHave;
		}
	}
	

}
