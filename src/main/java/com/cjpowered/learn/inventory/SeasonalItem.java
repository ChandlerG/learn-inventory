package com.cjpowered.learn.inventory;

import java.time.LocalDate;

import com.cjpowered.learn.marketing.MarketingInfo;
import com.cjpowered.learn.marketing.Season;

public class SeasonalItem implements Item {
	
	
	private final int shouldHave;
	private  Season season;//finals??
	private  boolean isFirst;

	
	
	
	public SeasonalItem(int wantOnHand, boolean isFirst, Season season)
	{
		this.shouldHave = wantOnHand;
		this.season = season;
		this.isFirst = isFirst;
	}
	
	

	@Override
	public int wantOnHand(LocalDate today, MarketingInfo mi) {
		
		final boolean inSeason = this.season.equals(mi.season(today));
		final int toOrder;
		if (isFirst && !(today.getDayOfMonth() == 1))
		{
			return 0;
		}
		else
		{
		if (mi.onSale(this))
		{
			if (inSeason)
			{
				toOrder = Math.max(mi.saleAmount(shouldHave), mi.seasonalAmount(shouldHave));
			}
			else
				toOrder = mi.saleAmount(shouldHave);
			
		}
		else
		{
			if (inSeason)
			{
				toOrder = mi.seasonalAmount(shouldHave);
			}
			else
				toOrder = shouldHave;
		}
		}
		
		
		return toOrder;
		
	}

}
