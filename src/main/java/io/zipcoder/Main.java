package io.zipcoder;

import org.apache.commons.io.IOUtils;

import java.util.ArrayList;
import java.util.Map;


public class Main {

    public static ItemParser itemParser = new ItemParser();

    public String readRawDataToString() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();

        return IOUtils.toString(classLoader.getResourceAsStream("RawData.txt"));
    }

    public static void main(String[] args) throws Exception {
        String output = (new Main()).readRawDataToString();
        ArrayList<String> temp = itemParser.parseRawDataIntoStringArray(output);
        itemParser.addItemToList(temp);
        System.out.println(itemParser.printGroceryList());
        //close the file when you're done writing to it!
        itemParser.flushExceptionsToFile();

        // TODO: parse the data in output into items, and display to console.
    }
}

