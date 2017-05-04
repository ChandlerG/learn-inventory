package com.cjpowered.learn.inventory;

public class StockedItem implements Item {
	
	
	private final int shouldHave;
	
	public StockedItem(int wantOnHand)
	{
		this.shouldHave = wantOnHand;
	}
	
	@Override
	public int wantOnHand()
	{
		return shouldHave;
	}
	

}
