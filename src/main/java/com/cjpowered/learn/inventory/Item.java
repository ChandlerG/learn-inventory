package com.cjpowered.learn.inventory;

import java.time.LocalDate;

import com.cjpowered.learn.marketing.MarketingInfo;
import com.cjpowered.learn.marketing.Season;

public interface Item {

	
	public int toOrder(LocalDate today, MarketingInfo mi, int onHandAndOnOrder);
	

	public int getNormalStock();

}
