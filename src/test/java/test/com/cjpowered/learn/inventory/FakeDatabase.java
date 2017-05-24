package test.com.cjpowered.learn.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cjpowered.learn.inventory.InventoryDatabase;
import com.cjpowered.learn.inventory.Item;

public class FakeDatabase implements InventoryDatabase {

	
	
	
	public FakeDatabase ()
	{
		
	}
	
	
	public void setRequiredOnHand(Item i, int in)
	{
		
	}
	@Override
	public int onHand(Item item) {
		return 0;
	}

	@Override
	public List<Item> stockItems() {
		throw new UnsupportedOperationException("Not yet implemented");
	}



	@Override
	public int onOrder(Item item) 
	{
		return 0;
	}

}
