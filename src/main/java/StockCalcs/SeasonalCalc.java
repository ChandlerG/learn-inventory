package StockCalcs;

import java.time.LocalDate;

import com.cjpowered.learn.inventory.InventoryNeeded;
import com.cjpowered.learn.inventory.Item;
import com.cjpowered.learn.marketing.MarketingInfo;
import com.cjpowered.learn.marketing.Season;

public class SeasonalCalc implements InventoryNeeded {
	
	private final Season season;
	LocalDate date;
	
	public SeasonalCalc(Season season)
	{
		this.season = season;
		
	}

	@Override
	public int amountNeeded(int normal, MarketingInfo marketing, Item item) {
		if (marketing.season(date).equals(season))
		{
			return normal*2;
		}
		return normal;

	}

}
