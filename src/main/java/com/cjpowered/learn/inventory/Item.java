package com.cjpowered.learn.inventory;

import java.time.LocalDate;

import com.cjpowered.learn.marketing.MarketingInfo;
import com.cjpowered.learn.marketing.Season;

public interface Item {

	
	public int wantOnHand(LocalDate today, MarketingInfo mi);
	



}
