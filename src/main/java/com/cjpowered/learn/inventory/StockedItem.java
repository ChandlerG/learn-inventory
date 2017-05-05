package com.cjpowered.learn.inventory;

import com.cjpowered.learn.marketing.Season;

public class StockedItem implements Item {
	
	
	private final int shouldHave;
	private final Season season;
	private final boolean isSeasonal;
	
	public StockedItem(int wantOnHand)
	{
		this.shouldHave = wantOnHand;
		this.season = Season.Fall;//bad default?
		this.isSeasonal = false;
		
	}
	
	public StockedItem(int wantOnHand, Season season)
	{
		this.shouldHave = wantOnHand;
		this.season = season;
		this.isSeasonal = true;
		
	}
	
	@Override
	public int wantOnHand()
	{
		return shouldHave;
	}
	
	public boolean isSeasonal()
	{
		return isSeasonal;
	}
	
	public Season season()
	{
		return season;
	}
	
	

}
