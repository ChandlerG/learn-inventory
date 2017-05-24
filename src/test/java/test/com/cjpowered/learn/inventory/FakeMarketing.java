package test.com.cjpowered.learn.inventory;

import java.time.LocalDate;

import com.cjpowered.learn.inventory.Item;
import com.cjpowered.learn.marketing.MarketingInfo;
import com.cjpowered.learn.marketing.Season;

public class FakeMarketing implements MarketingInfo {

	@Override
	public boolean onSale(Item item) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Season season(LocalDate when) {
		// TODO Auto-generated method stub
		return Season.Fall;
	}

}
