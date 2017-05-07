package test.com.cjpowered.learn.inventory;

import java.time.LocalDate;

import com.cjpowered.learn.inventory.Item;
import com.cjpowered.learn.marketing.MarketingInfo;
import com.cjpowered.learn.marketing.Season;

public class MarketingTemplate implements MarketingInfo {
	
	final private int saleBuffer = 20;
	
	public int saleAmount(int onHand)
	{
		return onHand + saleBuffer;
	}

	public int seasonalAmount(int onHand)
	{
		return onHand*2;//rule for seasons
	}
    @Override
    public boolean onSale(final Item item) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Season season(final LocalDate when) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    
    

}
