package com.cjpowered.learn.inventory;

import com.cjpowered.learn.marketing.Season;

public interface Item {

	
	public int wantOnHand();

	public boolean isSeasonal();
	
	public Season season();
	
}
