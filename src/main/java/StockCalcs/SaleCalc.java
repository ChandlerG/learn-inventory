package StockCalcs;

import com.cjpowered.learn.inventory.InventoryNeeded;
import com.cjpowered.learn.inventory.Item;
import com.cjpowered.learn.marketing.MarketingInfo;

public class SaleCalc implements InventoryNeeded {

	
	public int amountNeeded(int normal, MarketingInfo marketing, Item item) 
	{
		if (marketing.onSale(item))
			return normal + 20;
		return normal;
	}

}
