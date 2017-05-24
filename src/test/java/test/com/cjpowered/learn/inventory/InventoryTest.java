package test.com.cjpowered.learn.inventory;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.cjpowered.learn.inventory.InventoryDatabase;
import com.cjpowered.learn.inventory.InventoryManager;
import com.cjpowered.learn.inventory.InventoryNeeded;
import com.cjpowered.learn.inventory.Item;
import com.cjpowered.learn.inventory.Order;
import com.cjpowered.learn.inventory.StockedItem;
import com.cjpowered.learn.inventory.ace.AceInventoryManager;
import com.cjpowered.learn.marketing.MarketingInfo;
import com.cjpowered.learn.marketing.Season;

import static org.mockito.Mockito.*;

import StockCalcs.SaleCalc;
import StockCalcs.SeasonalCalc;

/*
 * We need to keep items in stock to prevent back orders. See the README.md
 * for the requirements.
 *
 */

public class InventoryTest {
	
	private final int saleAmount = 20;

    @Test
    public void whenNoStockItemsDoNotOrder() {
        // given
        final LocalDate today = LocalDate.now();
        MarketingTemplate mt = new MarketingTemplate();
        final InventoryDatabase db = new FakeDatabase(){
        	@Override
        	public List<Item> stockItems()
        	{
        		return Collections.emptyList();
        	}
        };
        
        final InventoryManager im = new AceInventoryManager(db, mt);

        // when
        final List<Order> actual = im.getOrders(today);

        // then
        assertTrue(actual.isEmpty());

    }
    
    /**
     * One item no specialness needs stock
     */
    @Test
    public void orderEnoughStockNotFirst()
    {
    	//given
    	int onHand = 10;
    	final int shouldHave = 16;
    	int onOrder = 2;
    	MarketingTemplate mt = new MarketingTemplate(){
    	    @Override
    	    public boolean onSale(final Item item) {
    	        return false;
    	    }
    	};
    	Set<InventoryNeeded> myList = new HashSet();	
    	Item item = new StockedItem(shouldHave, false, 1,  myList);
    	final InventoryDatabase db = new FakeDatabase() {
    		@Override
    		public int onHand(Item item) {
    			return onHand;
    		}
    		
    		@Override
    		public List<Item> stockItems() {
    			return Collections.singletonList(item);
    			
    		}
    		@Override 
    		public int onOrder(Item item)
    		{
    			return onOrder;
    		}
    	};
    	
    	final InventoryManager im = new AceInventoryManager(db, mt);
    	final LocalDate today = LocalDate.now();
    	
    	//when
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	assertEquals(1, actual.size());
    	assertEquals(item, actual.get(0).item);
    	assertEquals(shouldHave - onHand - onOrder,actual.get(0).quantity);    	
    	
    }
    
    
    /**
     * One item, no specialness, we have more than we need onHand
     */
    @Test
    public void tooMuchOnHand()
    {
    	//given
    	int onHand = 20;
    	final int shouldHave = 14;
    	int onOrder = 0;
    	MarketingTemplate mt = new MarketingTemplate(){
    	    @Override
    	    public boolean onSale(final Item item) {
    	        return false;
    	    }
    	    @Override 
    	    public Season season(final LocalDate when)
    	    {
    	    	return Season.Fall;
    	    }
    	};
    	
    	Set<InventoryNeeded> mySet = new HashSet();
    	Item item = new StockedItem(shouldHave, false, 1, mySet);
    	final InventoryDatabase db = new FakeDatabase() {
    		@Override
    		public int onHand(Item item)
    		{
    			return onHand;
    		}
    		@Override
    		public List<Item> stockItems()
    		{
    			return Collections.singletonList(item);
    		}
    		
    	};
    	final InventoryManager im = new AceInventoryManager(db, mt);
    	
    	final LocalDate today = LocalDate.now();
    	
    	//when 
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	
    	assertEquals(0, actual.size());
    	
    }
    
    
    /**
     * One item, no specialness, we have more than we need onOrder
     */
    @Test
    public void tooMuchOnOrder()
    {
    	//given
    	int onHand = 1;
    	final int shouldHave = 14;
    	int onOrder = 20;
    	MarketingTemplate mt = new MarketingTemplate(){
    	    @Override
    	    public boolean onSale(final Item item) {
    	        return false;
    	    }
    	    @Override 
    	    public Season season(final LocalDate when)
    	    {
    	    	return Season.Fall;
    	    }
    	};
    	
    	Set<InventoryNeeded> mySet = new HashSet();
    	Item item = new StockedItem(shouldHave, false, 1, mySet);
    	final InventoryDatabase db = new FakeDatabase() {
    		@Override
    		public int onOrder(Item item)
    		{
    			return onOrder;
    		}
    		@Override
    		public List<Item> stockItems()
    		{
    			return Collections.singletonList(item);
    		}
    		public int onHand(Item item)
    		{
    			return onHand;
    		}
    		
    	};
    	final InventoryManager im = new AceInventoryManager(db, mt);
    	
    	final LocalDate today = LocalDate.now();
    	
    	//when 
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	
    	assertEquals(0, actual.size());
    	
    }
    
    /**
     * One item, no specialness and our stock levels are _exactly_ correct between onHand and OnOrder
     */
    @Test
    public void justTheRightAmountOnHand()
    {
    	//given
    	int onHand = 10;
    	int onOrder = 4;
    	final int shouldHave = 14;
    	MarketingInfo mt = new FakeMarketing();
    	Set<InventoryNeeded> iSet= new HashSet();
    	Item item = new StockedItem(shouldHave, false, 1, iSet);
    	final InventoryDatabase db = new FakeDatabase() {
    		@Override
    		public int onHand(Item item)
    		{
    			return onHand;
    		}
    		@Override
    		public int onOrder(Item item)
    		{
    			return onOrder;
    		}
    		@Override
    		public List<Item> stockItems()
    		{
    			return Collections.singletonList(item);
    		}
    	};
    	final InventoryManager im = new AceInventoryManager(db, mt);
    	final LocalDate today = LocalDate.now();
    	
    	//when 
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	
    	assertEquals(0, actual.size());
    }
    
    
    /**
     * One item, no specialness and our stock levels are at 80%, don't order correct between onHand and OnOrder
     */
    @Test
    public void stockAt80DoNotOrder()
    {
    	//given
    	int onHand = 70;
    	int onOrder = 10;
    	final int shouldHave = 100;
    	MarketingInfo mt = new FakeMarketing();
    	Set<InventoryNeeded> iSet= new HashSet();
    	Item item = new StockedItem(shouldHave, false, 1, iSet);
    	final InventoryDatabase db = new FakeDatabase() {
    		@Override
    		public int onHand(Item item)
    		{
    			return onHand;
    		}
    		@Override
    		public int onOrder(Item item)
    		{
    			return onOrder;
    		}
    		@Override
    		public List<Item> stockItems()
    		{
    			return Collections.singletonList(item);
    		}
    	};
    	final InventoryManager im = new AceInventoryManager(db, mt);
    	final LocalDate today = LocalDate.now();
    	
    	//when 
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	
    	assertEquals(0, actual.size());
    }
    
    
    /**
     * Two items, neither seasonal nor on sale, one needs stock, one is exactly correct
     */
    @Test
    public void twoItemsOneNeedsStockOneIsJustRight()
    {
    	//given
    	int onHandItem1 = 14;
    	final int shouldHaveItem1 = 14;
    	
    	int onHandItem2 = 10;
    	 int shouldHaveItem2 = 20;
    	MarketingInfo mt = new FakeMarketing();
    	
    	Set<InventoryNeeded> mySet = new HashSet();
    	Item item1 = new StockedItem(shouldHaveItem1,  false, 1, mySet);
    	Item item2 = new StockedItem(shouldHaveItem2, false, 1, mySet);
    	
    	
    	final InventoryDatabase db = new FakeDatabase() {
    		@Override
    		public int onHand(Item item)
    		{
    			if (item.equals(item1))
    			{
    				return onHandItem1;
    			}
    			else if (item.equals(item2))
    			{
    				return onHandItem2;
    			}
    			else
    			{
    				return 0;
    			}
    		}
    		@Override
    		public List<Item> stockItems()
    		{
    			List<Item> myList = new ArrayList<>();
    			myList.add(item1);
    			myList.add(item2);
    			return myList;
    		}
    	};
    	final InventoryManager im = new AceInventoryManager(db, mt);
    	final LocalDate today = LocalDate.now();
    	
    	//when 
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	
    	assertEquals(1, actual.size());
    	assertEquals(actual.get(0).quantity,shouldHaveItem2 - onHandItem2);
    	
    }
    
    
    /**
     * item needs more stock because it's on sale
     */
    @Test
    public void saleItem()
    {
    	//given
    	int onHandItem1 = 14;
    	final int shouldHaveItem1 = 15;
    	final int onOrderItem1 = 1;
    	Set<InventoryNeeded> mySet = new HashSet();
    	mySet.add(new SaleCalc());
    	Item item1OnSale = new StockedItem(shouldHaveItem1, false, 1, mySet);
    	
    	
    	
    	MarketingInfo mt = new FakeMarketing()
    	{
    	    @Override
    	    public boolean onSale(final Item item) {
    	        if (item.equals(item1OnSale))
    	        	return true;
    	        else
    	        	return false;
    	    }
    	};

    	final InventoryDatabase db = new FakeDatabase() {
    		@Override
    		public int onHand(Item item)
    		{
    			if (item.equals(item1OnSale))
    			{
    				return onHandItem1;
    			}
    			else
    			{
    				return 0;
    			}
    		}
    		@Override
    		public List<Item> stockItems()
    		{
    			List<Item> myList = new ArrayList<>();
    			myList.add(item1OnSale);
    			return myList;
    		}
    		
    		@Override 
    		public int onOrder(Item item)
    		{
    			if (item.equals(item1OnSale))
    			{
    				return onOrderItem1;
    			}
    			else
    				return 0;
    			
    		}
    	};
    	final InventoryManager im = new AceInventoryManager(db, mt);
    	final LocalDate today = LocalDate.now();
    	
    	//when 
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	
    	assertEquals(1, actual.size());
    	assertEquals(saleAmount + shouldHaveItem1 - onHandItem1 - onOrderItem1, actual.get(0).quantity);
    }
    
    
    /**
     * item doesn't need stock, at 80% of saleLevel
     */
    @Test
    public void saleItemNoStock()
    {
    	//given
    	int onHandItem1 = 70;
    	final int shouldHaveItem1 = 80;
    	final int onOrderItem1 = 10;
    	Set<InventoryNeeded> mySet = new HashSet();
    	mySet.add(new SaleCalc());
    	Item item1OnSale = new StockedItem(shouldHaveItem1, false, 1, mySet);
    	
    	
    	
    	MarketingInfo mt = new FakeMarketing()
    	{
    	    @Override
    	    public boolean onSale(final Item item) {
    	        if (item.equals(item1OnSale))
    	        	return true;
    	        else
    	        	return false;
    	    }
    	};

    	final InventoryDatabase db = new FakeDatabase() {
    		@Override
    		public int onHand(Item item)
    		{
    			if (item.equals(item1OnSale))
    			{
    				return onHandItem1;
    			}
    			else
    			{
    				return 0;
    			}
    		}
    		@Override
    		public List<Item> stockItems()
    		{
    			List<Item> myList = new ArrayList<>();
    			myList.add(item1OnSale);
    			return myList;
    		}
    		
    		@Override 
    		public int onOrder(Item item)
    		{
    			if (item.equals(item1OnSale))
    			{
    				return onOrderItem1;
    			}
    			else
    				return 0;
    			
    		}
    	};
    	final InventoryManager im = new AceInventoryManager(db, mt);
    	final LocalDate today = LocalDate.now();
    	
    	//when 
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	
    	assertEquals(0, actual.size());
    }
    
    
    /**
     * Two items, one on sale, sale item is fully stocked, non sale item needs stock
     */
    @Test
    public void twoItemsOneOnSaleOnlyNonSaleNeedsStock()
    {
    	//given
    	
    	final int shouldHaveItem1 = 15;
    	int onHandItem1 = shouldHaveItem1 + saleAmount;
    	
    	
    	final int onHandItem2 = 6;
    	final int onOrderItem2 = 4;
    	final int shouldHaveItem2 = 20;
    	
    	
    	Set <InventoryNeeded> eSet = new HashSet();
    	Set <InventoryNeeded> mSet = new HashSet();
    	mSet.add(new SaleCalc());
    	Item item1OnSale = new StockedItem(shouldHaveItem1, false, 1, mSet);
    	Item item2 = new StockedItem(shouldHaveItem2, false, 1, eSet);
    	MarketingInfo mt = new FakeMarketing()
    	{
    	    @Override
    	    public boolean onSale(final Item item) {
    	        if (item.equals(item1OnSale))
    	        	return true;
    	        else
    	        	return false;
    	    }
    	};
    	
    	
    	
    	final InventoryDatabase db = new FakeDatabase() {
    		@Override
    		public int onHand(Item item)
    		{
    			if (item.equals(item1OnSale))
    			{
    				return onHandItem1;
    			}
    			else if (item.equals(item2))
    			{
    				return onHandItem2;
    			}
    			else
    			{
    				return 0;
    			}
    		}
    		@Override
    		public List<Item> stockItems()
    		{
    			List<Item> myList = new ArrayList<>();
    			myList.add(item1OnSale);
    			myList.add(item2);
    			return myList;
    		}
    		
    		@Override
    		public int onOrder(Item item)
    		{
    			if (item.equals(item2))
    			{
    				return onOrderItem2;
    			}
    			else
    				return 1;
    		}
    	};
    	final InventoryManager im = new AceInventoryManager(db, mt);
    	final LocalDate today = LocalDate.now();
    	
    	//when 
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	
    	assertEquals(1, actual.size());// should have orders
    	assertEquals(actual.get(0).quantity, shouldHaveItem2 - onHandItem2 - onOrderItem2);
    	
    }
 
    
    /**
     * item needs more stock because it's seasonal
     */
    @Test
    public void seasonalItem()
    {
    	//given
    	int onHandItem1 = 14;
    	final int shouldHaveItem1 = 15;
    	final int onOrderItem1 = 1;
    	Set<InventoryNeeded> mySet = new HashSet();
    	mySet.add(new SeasonalCalc(Season.Fall));
    	Item item1OnSale = new StockedItem(shouldHaveItem1, false, 1, mySet);

    	
    	MarketingInfo mt = new FakeMarketing();

    	final InventoryDatabase db = new FakeDatabase() {
    		@Override
    		public int onHand(Item item)
    		{
    			if (item.equals(item1OnSale))
    			{
    				return onHandItem1;
    			}
    			else
    			{
    				return 0;
    			}
    		}
    		@Override
    		public List<Item> stockItems()
    		{
    			List<Item> myList = new ArrayList<>();
    			myList.add(item1OnSale);
    			return myList;
    		}
    		
    		@Override 
    		public int onOrder(Item item)
    		{
    			if (item.equals(item1OnSale))
    			{
    				return onOrderItem1;
    			}
    			else
    				return 0;
    			
    		}
    	};
    	final InventoryManager im = new AceInventoryManager(db, mt);
    	final LocalDate today = LocalDate.now();
    	
    	//when 
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	
    	assertEquals(1, actual.size());
    	assertEquals( shouldHaveItem1*2 - onHandItem1 - onOrderItem1, actual.get(0).quantity);
    }
  
    /**
     * SeasonalItem not in season so order regular amount
     * 
     */
    @Test
    public void seasonalItemNotInSeason()
    {
    	//given
    	int onHandItem1 = 5; 
    	final int shouldHaveItem1 = 20;
    	Season item1Season = Season.Winter;
    	int onOrderItem1 = 5;
    
    	Set<InventoryNeeded> mySet = new HashSet();
    	mySet.add(new SeasonalCalc(item1Season));
    	Item item1Seasonal = new StockedItem(shouldHaveItem1, false, 1, mySet);

    	MarketingInfo mt = new FakeMarketing();
    	
    	
    	
    	final InventoryDatabase db = new FakeDatabase() {
    		@Override
    		public int onHand(Item item)
    		{
    			if (item.equals(item1Seasonal))
    			{
    				return onHandItem1;
    			}
    			else
    			{
    				return 0;
    			}
    		}
    		@Override
    		public List<Item> stockItems()
    		{
    			List<Item> myList = new ArrayList<>();
    			myList.add(item1Seasonal);
    			return myList;
    		}
    		@Override 
    		public int onOrder(Item item)
    		{
    			if (item.equals(item1Seasonal))
    			{
    				return onOrderItem1;
    			}
    			else
    				return 0;
    		}
    	};
    	final InventoryManager im = new AceInventoryManager(db, mt);
    	final LocalDate today = LocalDate.now();
    	
    	//when 
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	assertEquals(1, actual.size());// should 2 have orders
    	assertEquals(shouldHaveItem1 - onHandItem1 - onOrderItem1,actual.get(0).quantity);
    	
    }
    
    /**
     * Item meets normal stock, needs Seasonal
     * 
     */
    @Test
    public void needsSeasonalStock()
    {
    	//given
    	int onHandItem1 = 15; 
    	final int shouldHaveItem1 = 15;
    	Season item1Season = Season.Winter;
    	int onOrderItem1 = 5;
    	
    	Set<InventoryNeeded> mySet = new HashSet();
    	mySet.add(new SeasonalCalc(item1Season));
    	Item item1Seasonal = new StockedItem(shouldHaveItem1, false, 1, mySet);

    	MarketingInfo mt = new FakeMarketing()
    	{
    	    @Override
    	    public Season season(final LocalDate when)
    	    {
    	    	return item1Season;
    	    }
    	};
    	
    	
    	
    	final InventoryDatabase db = new FakeDatabase() {
    		@Override
    		public int onHand(Item item)
    		{
    			if (item.equals(item1Seasonal))
    			{
    				return onHandItem1;
    			}
    			else
    			{
    				return 0;
    			}
    		}
    		@Override
    		public List<Item> stockItems()
    		{
    			List<Item> myList = new ArrayList<>();
    			myList.add(item1Seasonal);
    			return myList;
    		}
    		@Override
    		public int onOrder(Item item)
    		{
    				return onOrderItem1;
    		}
    	};
    	final InventoryManager im = new AceInventoryManager(db, mt);
    	final LocalDate today = LocalDate.now();
    	
    	//when 
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	assertEquals(1, actual.size());// should 2 have orders
    	assertEquals(shouldHaveItem1*2 - onHandItem1-onOrderItem1,actual.get(0).quantity);

    }


 
    //write two tests for an item on sale AND seasonal, make sure it always picks the largest 
    @Test
    public void itemOnSaleAndSeasonal()
    {
    	//given
    	
    	final int shouldHaveItem1 = 15;
    	int onHandItem1 = 5;
    	Season item1Season = Season.Winter;
    	final int onOrderItem1 = 3;
    	Set<InventoryNeeded> mySet = new HashSet();
    	mySet.add(new SaleCalc());
    	mySet.add(new SeasonalCalc(item1Season));
    	Item item1Seasonal = new StockedItem(shouldHaveItem1, false, 1, mySet);

    	MarketingTemplate mt = new MarketingTemplate()
    	{
    	    @Override
    	    public boolean onSale(final Item item) {
    	        	return true;
    	    }
    	    @Override
    	    public Season season(final LocalDate when)
    	    {
    	    	return item1Season;
    	    }
    	    
    	};

    	
    	final InventoryDatabase db = new FakeDatabase() {
    		@Override
    		public int onHand(Item item)
    		{
    				return onHandItem1;
    		}
    		@Override
    		public List<Item> stockItems()
    		{
    			List<Item> myList = new ArrayList<>();
    			myList.add(item1Seasonal);
    			return myList;
    		}
    		@Override 
    		public int onOrder(Item item)
    		{
    			return onOrderItem1;
    		}
    	};
    	final InventoryManager im = new AceInventoryManager(db, mt);
    	final LocalDate today = LocalDate.now();
    	
    	//when 
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	assertEquals(1, actual.size());// should have 1 orders
    	assertEquals(Math.max(shouldHaveItem1 + 20, shouldHaveItem1 * 2) - onHandItem1 - onOrderItem1, actual.get(0).quantity);
    	
    }
    
    //Happy Path for first month only ordering
    @Test
    public void firstMonth()
    {
    	//given
    	
    	final int shouldHaveItem1 = 15;
    	int onHandItem1 = 5;
    	Season item1Season = Season.Winter;
    	final int onOrderItem1 = 3;
    	Set<InventoryNeeded> mySet = new HashSet();
    	mySet.add(new SaleCalc());
    	mySet.add(new SeasonalCalc(item1Season));
    	Item item1Seasonal = new StockedItem(shouldHaveItem1, true, 1, mySet);

    	MarketingTemplate mt = new MarketingTemplate()
    	{
    	    @Override
    	    public boolean onSale(final Item item) {
    	        	return true;
    	    }
    	    @Override
    	    public Season season(final LocalDate when)
    	    {
    	    	return item1Season;
    	    }
    	    
    	};

    	
    	final InventoryDatabase db = new FakeDatabase() {
    		@Override
    		public int onHand(Item item)
    		{
    				return onHandItem1;
    		}
    		@Override
    		public List<Item> stockItems()
    		{
    			List<Item> myList = new ArrayList<>();
    			myList.add(item1Seasonal);
    			return myList;
    		}
    		@Override 
    		public int onOrder(Item item)
    		{
    			return onOrderItem1;
    		}
    	};
    	final InventoryManager im = new AceInventoryManager(db, mt);
    	final LocalDate today =  LocalDate.of(2016, 2, 2);
    	
    	//when 
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	assertEquals(0, actual.size());    	
    }
    
    
    @Test
    public void setStockHigherWhenEmpty()
    {
    	final int shouldHaveItem1 = 15;
    	int onHandItem1 = 0;
    	final int onOrderItem1 = 0;
    	Set<InventoryNeeded> mySet = new HashSet();
    	Item item1 = new StockedItem(shouldHaveItem1, true, 1, mySet);

    	MarketingInfo mt = new FakeMarketing();

    	final InventoryDatabase db = mock(InventoryDatabase.class);
    	when(db.onHand(item1)).thenReturn(onHandItem1);
    	List<Item> myList = new ArrayList<>();
		myList.add(item1);
		when(db.stockItems()).thenReturn(myList);
		when(db.onOrder(item1)).thenReturn(onOrderItem1);
    	
    
    	final InventoryManager im = new AceInventoryManager(db, mt);
    	final LocalDate today =  LocalDate.of(2016, 2, 2);
    	
    	//when 
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	verify(db, times(1)).setRequiredOnHand(item1, (int)Math.ceil(item1.getNormalStock() * .10));
    }
    
    
    @Test
    public void dontSetStockHigherOnOrder()
    {
    	final int shouldHaveItem1 = 15;
    	int onHandItem1 = 0;
    	final int onOrderItem1 = 3;
    	Set<InventoryNeeded> mySet = new HashSet();
    	Item item1 = new StockedItem(shouldHaveItem1, true, 1, mySet);

    	MarketingInfo mt = new FakeMarketing();

    	final InventoryDatabase db = mock(InventoryDatabase.class);
    	when(db.onHand(item1)).thenReturn(onHandItem1);
    	List<Item> myList = new ArrayList<>();
		myList.add(item1);
		when(db.stockItems()).thenReturn(myList);
		when(db.onOrder(item1)).thenReturn(onOrderItem1);
    	
    
    	final InventoryManager im = new AceInventoryManager(db, mt);
    	final LocalDate today =  LocalDate.of(2016, 2, 2);
    	
    	//when 
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	verify(db, never()).setRequiredOnHand(any(StockedItem.class), anyInt());
    }
    
    
    
 
    
    
    
}
