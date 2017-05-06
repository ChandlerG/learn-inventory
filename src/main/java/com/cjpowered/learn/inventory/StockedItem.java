package com.cjpowered.learn.inventory;

import com.cjpowered.learn.marketing.Season;

public class StockedItem implements Item {
	
	
	private final int shouldHave;
	private  Season season;//finals??
	private final boolean isSeasonal;
	private  boolean isFirst;
	
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
	
	public StockedItem(int wantOnHand, Season season, boolean isFirst)
	{
		this.shouldHave = wantOnHand;
		this.season = season;
		this.isSeasonal = true;
		this.isFirst = isFirst;
	}
	
	public StockedItem(int wantOnHand, boolean isFirst)
	{
		this.shouldHave = wantOnHand;
		this.season = Season.Fall;
		this.isSeasonal = false;
		this.isFirst = isFirst;
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
	
	public boolean isFirstOrderableOnly()
	{
		return isFirst;
	}
	
	

}
