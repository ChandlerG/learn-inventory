package test.com.cjpowered.learn.inventory;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.cjpowered.learn.inventory.InventoryDatabase;
import com.cjpowered.learn.inventory.InventoryManager;
import com.cjpowered.learn.inventory.Item;
import com.cjpowered.learn.inventory.Order;
import com.cjpowered.learn.inventory.SeasonalItem;
import com.cjpowered.learn.inventory.StockedItem;
import com.cjpowered.learn.inventory.ace.AceInventoryManager;
import com.cjpowered.learn.marketing.Season;

/*
 * We need to keep items in stock to prevent back orders. See the README.md
 * for the requirements.
 *
 */

public class InventoryTest {

    @Test
    public void whenNoStockItemsDoNotOrder() {
        // given
        final LocalDate today = LocalDate.now();
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
        final InventoryDatabase db = new DatabaseTemplate(){
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
     * One item, neither seasonal nor sale, needs stock
     */
    @Test
    public void orderEnoughStockNotFirst()
    {
    	//given
    	int onHand = 10;
    	final int shouldHave = 16;
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
    			
    	Item item = new StockedItem(shouldHave, false);
    	final InventoryDatabase db = new DatabaseTemplate() {
    		@Override
    		public int onHand(Item item) {
    			return onHand;
    		}
    		
    		@Override
    		public List<Item> stockItems() {
    			return Collections.singletonList(item);
    			
    		}
    	};
    	
    	final InventoryManager im = new AceInventoryManager(db, mt);
    	final LocalDate today = LocalDate.now();
    	
    	//when
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	assertEquals(1, actual.size());
    	assertEquals(item, actual.get(0).item);
    	assertEquals(shouldHave - onHand,actual.get(0).quantity);    	
    	
    }
    
    
    /**
     * One item, neither seasonal nor on sale, we have more than we need
     */
    @Test
    public void tooMuchOnHand()
    {
    	//given
    	int onHand = 20;
    	final int shouldHave = 14;
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
    	
    	Item item = new StockedItem(shouldHave, false);
    	final InventoryDatabase db = new DatabaseTemplate() {
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
     * One item, neither seasonal nor on sale, and our stock levels are _exactly_ correct
     */
    @Test
    public void justTheRightAmountOnHand()
    {
    	//given
    	int onHand = 14;
    	final int shouldHave = 14;
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
    	
    	Item item = new StockedItem(shouldHave, false);
    	final InventoryDatabase db = new DatabaseTemplate() {
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
     * Two items, neither seasonal nor on sale, one needs stock, one is exactly correct
     */
    @Test
    public void twoItemsOneNeedsStockOneIsJustRight()
    {
    	//given
    	int onHandItem1 = 14;
    	final int shouldHaveItem1 = 14;
    	
    	int onHandItem2 = 13;
    	final int shouldHaveItem2 = 20;
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
    	
    	Item item1 = new StockedItem(shouldHaveItem1,  false);
    	Item item2 = new StockedItem(shouldHaveItem2, false);
    	
    	
    	final InventoryDatabase db = new DatabaseTemplate() {
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
    }
    
    
    /**
     * Two items, both need stock, one on sale
     */
    @Test
    public void twoItemsOneOnSaleBothNeedStock()
    {
    	//given
    	int onHandItem1 = 14;
    	final int shouldHaveItem1 = 15;
    	int onHandItem2 = 13;
    	final int shouldHaveItem2 = 20;
    	
    	Item item1OnSale = new StockedItem(shouldHaveItem1, false);
    	Item item2 = new StockedItem(shouldHaveItem2, false);
    	
    	MarketingTemplate mt = new MarketingTemplate()
    	{
    	    @Override
    	    public boolean onSale(final Item item) {
    	        if (item.equals(item1OnSale))
    	        	return true;
    	        else
    	        	return false;
    	    }
    	    @Override 
    	    public Season season(final LocalDate when)
    	    {
    	    	return Season.Fall;
    	    }
    	};
    	
    	
    	
    	final InventoryDatabase db = new DatabaseTemplate() {
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
    	};
    	final InventoryManager im = new AceInventoryManager(db, mt);
    	final LocalDate today = LocalDate.now();
    	
    	//when 
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	
    	assertEquals(2, actual.size());//both should have orders
    	assertEquals(mt.saleAmount(shouldHaveItem1) - onHandItem1, actual.get(0).quantity);//Iterator okay?? Do I need to specify more?
    	assertEquals(actual.get(1).quantity, shouldHaveItem2 - onHandItem2);
    }
    
    
    
    //this test passed without a red, just from changing the amount of stock. Should I... write the test
    //before changing the number values? How would this fail?
    //and how comprehensive do my tests need to be? Should I exhaust _every_ possibility for two items 
    //in relation to 1 being in stock, 1 being on sale, etc?? That's quite a few possibilities
    /**
     * Two items, one on sale, sale item is fully stocked, non sale item needs stock
     */
    @Test
    public void twoItemsOneOnSaleOnlyNonSaleNeedsStock()
    {
    	//given
    	final int shouldHaveItem1 = 15;
    	int onHandItem2 = 13;
    	final int shouldHaveItem2 = 20;
    	Item item1OnSale = new StockedItem(shouldHaveItem1, false);
    	Item item2 = new StockedItem(shouldHaveItem2, false);
    	MarketingTemplate mt = new MarketingTemplate()
    	{
    	    @Override
    	    public boolean onSale(final Item item) {
    	        if (item.equals(item1OnSale))
    	        	return true;
    	        else
    	        	return false;
    	    }
    	    @Override 
    	    public Season season(final LocalDate when)
    	    {
    	    	return Season.Fall;
    	    }
    	};
    	
    	int onHandItem1 = mt.saleAmount(15); //is it okay for these assignments to be out of order? Is this ugly?
    	
    	final InventoryDatabase db = new DatabaseTemplate() {
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
    	};
    	final InventoryManager im = new AceInventoryManager(db, mt);
    	final LocalDate today = LocalDate.now();
    	
    	//when 
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	
    	assertEquals(1, actual.size());// should have orders
    	assertEquals(actual.get(0).quantity, shouldHaveItem2 - onHandItem2);
    	
    }
 
  
    /**
     * The seasonal item needs stock, but also needs to order above the regular should have because it's seasonal,
     * one item just needs regular stock ordered
     * 
     */
    @Test
    public void twoItemsBothNeedStockOneSeasonal()
    {
    	//given
    	int onHandItem1 = 13; 
    	final int shouldHaveItem1 = 15;
    	Season item1Season = Season.Winter;
    	
    	int onHandItem2 = 13;
    	final int shouldHaveItem2 = 20;
    	
    	Item item1Seasonal = new SeasonalItem(shouldHaveItem1, false, item1Season);
    	Item item2 = new StockedItem(shouldHaveItem2, false);
    	MarketingTemplate mt = new MarketingTemplate()
    	{
    	    @Override
    	    public boolean onSale(final Item item) {
    	        return false;
    	    }
    	    @Override
    	    public Season season(final LocalDate when)
    	    {
    	    	return item1Season;
    	    }
    	};
    	
    	
    	
    	final InventoryDatabase db = new DatabaseTemplate() {
    		@Override
    		public int onHand(Item item)
    		{
    			if (item.equals(item1Seasonal))
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
    			myList.add(item1Seasonal);
    			myList.add(item2);
    			return myList;
    		}
    	};
    	final InventoryManager im = new AceInventoryManager(db, mt);
    	final LocalDate today = LocalDate.now();
    	
    	//when 
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	assertEquals(2, actual.size());// should 2 have orders
    	assertEquals(shouldHaveItem1*2 - onHandItem1,actual.get(0).quantity);
    	assertEquals(shouldHaveItem2 - onHandItem2, actual.get(1).quantity);
    }
    
    /**
     * Two items, the seasonal one is at regular stock levels, but needs to meet
     * seasonal stock levels
     * 
     */
    @Test
    public void twoItemsSeasonalNeedsStock()
    {
    	//given
    	int onHandItem1 = 15; 
    	final int shouldHaveItem1 = 15;
    	Season item1Season = Season.Winter;
    	
    	int onHandItem2 = 13;
    	final int shouldHaveItem2 = 20;
    	
    	Item item1Seasonal = new SeasonalItem(shouldHaveItem1, false, item1Season);
    	Item item2 = new StockedItem(shouldHaveItem2, false);
    	MarketingTemplate mt = new MarketingTemplate()
    	{
    	    @Override
    	    public boolean onSale(final Item item) {
    	        return false;
    	    }
    	    @Override
    	    public Season season(final LocalDate when)
    	    {
    	    	return item1Season;
    	    }
    	};
    	
    	
    	
    	final InventoryDatabase db = new DatabaseTemplate() {
    		@Override
    		public int onHand(Item item)
    		{
    			if (item.equals(item1Seasonal))
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
    			myList.add(item1Seasonal);
    			myList.add(item2);
    			return myList;
    		}
    	};
    	final InventoryManager im = new AceInventoryManager(db, mt);
    	final LocalDate today = LocalDate.now();
    	
    	//when 
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	assertEquals(2, actual.size());// should 2 have orders
    	assertEquals(shouldHaveItem1*2 - onHandItem1,actual.get(0).quantity);
    	assertEquals(shouldHaveItem2 - onHandItem2, actual.get(1).quantity);
    }


    /**
     * Two items, Neither Needs Stock, One is seasonal
     * 
     */
    @Test
    public void twoItemsOneSeasonalNeitherNeedsStock()
    {
    	//given
    	
    	final int shouldHaveItem1 = 15;
    	Season item1Season = Season.Winter;
    	
    	int onHandItem2 = 20;
    	final int shouldHaveItem2 = 20;
    	
    	Item item1Seasonal = new SeasonalItem(shouldHaveItem1, false, item1Season);
    	Item item2 = new StockedItem(shouldHaveItem2, false);
    	MarketingTemplate mt = new MarketingTemplate()
    	{
    	    @Override
    	    public boolean onSale(final Item item) {
    	        return false;
    	    }
    	    @Override
    	    public Season season(final LocalDate when)
    	    {
    	    	return item1Season;
    	    }
    	};
    	int onHandItem1 = mt.seasonalAmount(shouldHaveItem1);
    	
    	
    	
    	final InventoryDatabase db = new DatabaseTemplate() {
    		@Override
    		public int onHand(Item item)
    		{
    			if (item.equals(item1Seasonal))
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
    			myList.add(item1Seasonal);
    			myList.add(item2);
    			return myList;
    		}
    	};
    	final InventoryManager im = new AceInventoryManager(db, mt);
    	final LocalDate today = LocalDate.now();
    	
    	//when 
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	assertEquals(0, actual.size());// should 2 have orders
    	
    }

    //write two tests for an item on sale AND seasonal, make sure it always picks the largest 
    @Test
    public void itemOnSaleAndSeasonal()
    {
    	//given
    	
    	final int shouldHaveItem1 = 15;
    	Season item1Season = Season.Winter;
    	
    	int onHandItem2 = 20;
    	final int shouldHaveItem2 = 20;
    	
    	Item item1Seasonal = new SeasonalItem(shouldHaveItem1, false, item1Season);
    	Item item2 = new StockedItem(shouldHaveItem2, false);
    	MarketingTemplate mt = new MarketingTemplate()
    	{
    	    @Override
    	    public boolean onSale(final Item item) {
    	        if (item.equals(item1Seasonal))
    	        {
    	        	return true;
    	        }
    	        return false;
    	    }
    	    @Override
    	    public Season season(final LocalDate when)
    	    {
    	    	return item1Season;
    	    }
    	};
    	int onHandItem1 = mt.seasonalAmount(shouldHaveItem1);
    	
    	
    	
    	final InventoryDatabase db = new DatabaseTemplate() {
    		@Override
    		public int onHand(Item item)
    		{
    			if (item.equals(item1Seasonal))
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
    			myList.add(item1Seasonal);
    			myList.add(item2);
    			return myList;
    		}
    	};
    	final InventoryManager im = new AceInventoryManager(db, mt);
    	final LocalDate today = LocalDate.now();
    	
    	//when 
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	assertEquals(1, actual.size());// should have 1 orders
    	assertEquals(Math.max(mt.saleAmount(shouldHaveItem1), mt.seasonalAmount(shouldHaveItem1)) - onHandItem1, actual.get(0).quantity);
    	
    }
    
    @Test
    public void notFirstDayDontOrder()
    {
    	//given
    	
    	final int shouldHaveItem1 = 15;
    	Season item1Season = Season.Winter;
    	boolean firstMonth = true;
    	
    	int onHandItem2 = 20;
    	final int shouldHaveItem2 = 20;
    	
    	Item item1Seasonal = new SeasonalItem(shouldHaveItem1, true, item1Season);
    	Item item2 = new StockedItem(shouldHaveItem2, false);
    	MarketingTemplate mt = new MarketingTemplate()
    	{
    	    @Override
    	    public boolean onSale(final Item item) {
    	        if (item.equals(item1Seasonal))
    	        {
    	        	return true;
    	        }
    	        return false;
    	    }
    	    @Override
    	    public Season season(final LocalDate when)
    	    {
    	    	return item1Season;
    	    }
    	};
    	int onHandItem1 = mt.seasonalAmount(shouldHaveItem1);
    	
    	
    	
    	final InventoryDatabase db = new DatabaseTemplate() {
    		@Override
    		public int onHand(Item item)
    		{
    			if (item.equals(item1Seasonal))
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
    			myList.add(item1Seasonal);
    			myList.add(item2);
    			return myList;
    		}
    	};
    	final InventoryManager im = new AceInventoryManager(db, mt);
    	final LocalDate today = LocalDate.of(2017, 1, 2);
    	
    	//when 
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	assertEquals(0, actual.size());// should have 0 orders
    	
    	
    }
}
