package io.zipcoder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class ItemParserTest {

    private String rawSingleItem =    "naMe:Milk;price:3.23;type:Food;expiration:1/25/2016##";

    private String rawSingleItemIrregularSeperatorSample = "naMe:MiLK;price:3.23;type:Food^expiration:1/11/2016##";

    private String rawBrokenSingleItem =    "naMe:;price:3.23;type:Food;expiration:1/25/2016##";

    private String rawMultipleItems = "naMe:Milk;price:3.23;type:Food;expiration:1/25/2016##"
                                      +"naME:BreaD;price:1.23;type:Food;expiration:1/02/2016##"
                                      +"NAMe:BrEAD;price:1.23;type:Food;expiration:2/25/2016##";
    private ItemParser itemParser;

    @Before
    public void setUp(){

        itemParser = new ItemParser();
    }

    @Test
    public void parseRawDataIntoStringArrayTest(){
        Integer expectedArraySize = 3;
        ArrayList<String> items = itemParser.parseRawDataIntoStringArray(rawMultipleItems);
        Integer actualArraySize = items.size();
        assertEquals(expectedArraySize, actualArraySize);
    }

    @Test
    public void parseStringIntoItemTest() throws ItemParseException{
        Item expected = new Item("milk", 3.23, "food","1/25/2016");
        Item actual = itemParser.parseStringIntoItem(rawSingleItem);
        assertEquals(expected.toString(), actual.toString());
    }

    @Test(expected = ItemParseException.class)
    public void parseBrokenStringIntoItemTest() throws ItemParseException{
        itemParser.parseStringIntoItem(rawBrokenSingleItem);
    }

    @Test
    public void findKeyValuePairsInRawItemDataTest(){
        Integer expected = 4;
        Integer actual = itemParser.findKeyValuePairsInRawItemData(rawSingleItem).size();
        assertEquals(expected, actual);
    }

    @Test
    public void findKeyValuePairsInRawItemDataTestIrregular(){
        Integer expected = 4;
        Integer actual = itemParser.findKeyValuePairsInRawItemData(rawSingleItemIrregularSeperatorSample).size();
        assertEquals(expected, actual);
    }

    @Test
    public void addItemToListTest() {
        //Given
        ArrayList<String> testList;
        //When
        testList = itemParser.parseRawDataIntoStringArray(rawMultipleItems);
        itemParser.addItemToList(testList);
        //SIZE IS TWO BECAUSE BREAD IS REPEATED TWICE AND ITS A UNIQUE KEY!!!
        int expected = 2;
        int actual = itemParser.getMap().size();
        //Then
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void addItemToListTest2() {
        //Given
        ArrayList<String> testList;
        //When
        testList = itemParser.parseRawDataIntoStringArray(rawMultipleItems);
        itemParser.addItemToList(testList);
        String expected = "[bread, milk]";
        String actual = itemParser.getMap().keySet().toString();
        //Then
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void addItemToListTest3() {
        //Given
        ArrayList<String> testList;
        //When
        testList = itemParser.parseRawDataIntoStringArray(rawMultipleItems);
        itemParser.addItemToList(testList);
        boolean expected = true;
        boolean actual = itemParser.getMap().keySet().contains("bread");
        System.out.println(itemParser.printGroceryList());
        //Then
        Assert.assertEquals(expected, actual);
    }


}
