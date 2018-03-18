package io.zipcoder;



import jdk.nashorn.internal.objects.Global;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public final class ItemParser {

    private PrintWriter writer ;
    private Map<String,ArrayList<Item>> groceryMap;

    //counts exceptions
    int exception = 0;

    public ItemParser() {

        groceryMap = new HashMap<>();
        //add printWriter in constructor so that there is only ONE
        try {
            writer= new PrintWriter("/Users/karoushafennimore/Dev/PainfulAfternoon/src/main/resources/errors.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String printGroceryList(){

        StringBuilder sb = new StringBuilder();
        //for every new entry - put the Key and how many times you saw it
        for (Map.Entry<String , ArrayList<Item> > item : groceryMap.entrySet()) {
            sb.append(String.format("\nname: %9s", item.getKey().substring(0, 1).toUpperCase() + item.getKey().substring(1)));
            sb.append("\t\t\tseen:  " + item.getValue().size() + "  times\n" + "===============" + "\t\t\t" + "===============\n");

            ArrayList<Double> tempList = uniquePrices(item);

            for(int i = 0; i < tempList.size(); i++) {
                sb.append(String.format("Price: %8s", tempList.get(i)));
                sb.append("\t\t\tseen:  " + priceCount(item.getValue(), tempList.get(i)) + "  times\n---------------" + "\t\t\t" + "---------------\n");
            }
        }
        sb.append("\nErrors" + "\t\t\t\t\tseen:  " + exception + "  times\n");

        return sb.toString();
    }



    public ArrayList<Double> uniquePrices(Map.Entry<String , ArrayList<Item> > item) {

        //returning unique Prices from the array list
        ArrayList<Double> uniquePrice = new ArrayList<>();
        //loop through and check if the price has been repeated...if it hasn't then add it
        for (int i = 0; i < item.getValue().size(); i++) {
            if (!uniquePrice.contains(item.getValue().get(i).getPrice())) {
                uniquePrice.add(item.getValue().get(i).getPrice());
            }
        }
        //output ArrayList of Doubles
        return uniquePrice;

    }

    public int priceCount(ArrayList<Item> item, Double price) {
        int count = 0;
        //loop through items and look for the price to see how many times it is there
        for(int i = 0; i < item.size(); i++) {
            if(item.get(i).getPrice().equals(price)) {
                count++;
            }
        }
        return count;
    }

    public Map<String, ArrayList<Item>> getMap() {
        return this.groceryMap;
    }

    public ArrayList<String> parseRawDataIntoStringArray(String rawData){
        return splitStringWithRegexPattern("##" , rawData);
    }

    private void incrementList(Map<String, ArrayList<Item>> myMap, Item myItem) {
        //if the Key is already in the Map
        if(myMap.keySet().contains(myItem.getName())) {
            //giving the map the item name and then adding the value to THAT key
            myMap.get(myItem.getName()).add(myItem);
        }else {
            //if the key is not in the map then add it
            myMap.put(myItem.getName(), new ArrayList<Item>());
            //then add the value to that added KEY!
            myMap.get(myItem.getName()).add(myItem);
        }

    }

    public void addItemToList(ArrayList<String> groceryList) {

        for(int i = 0; i < groceryList.size(); i++) {
            try {
                Item tryItem = (parseStringIntoItem(groceryList.get(i)));
                incrementList(groceryMap, tryItem);
            } catch (ItemParseException e) {
                exception++;
                try {
                    printErrorToFile(e);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public Item parseStringIntoItem(String rawItem) throws ItemParseException {
        String itemName = findName(rawItem);
        Double itemPrice = itemPrice(rawItem);
        String itemType = itemType(rawItem);
        String itemExpiration = itemExpiration(rawItem);

        //if item does not have a name or item Price - dont create it bc its irrelevant data.
        if(findName(rawItem) == null || itemPrice(rawItem) == null) {
            throw new ItemParseException();
        }
        return new Item (itemName, itemPrice, itemType, itemExpiration);
    }


    //getting all the values from string for
    private String findName(String rawItem) {
        Pattern namePattern = Pattern.compile("(?<=([Nn][Aa][Mm][Ee][^A-Za-z])).*?(?=[^A-Za-z0])");
        Matcher regex = namePattern.matcher(rawItem);

        if (regex.find()) {
            if (regex.group().length() > 0) {
                return replaceZeros(regex.group().toLowerCase());
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public Double itemPrice(String rawItem) {
        Pattern pricePattern = Pattern.compile("(?<=([Pp][Rr][Ii][Cc][Ee][^A-Za-z])).*?(?=[^0-9.])");
        Matcher regex1 = pricePattern.matcher(rawItem);
        if (regex1.find()) {
            if (regex1.group().length() > 0) {
                return Double.parseDouble(regex1.group().toLowerCase());
            }
        }
        return null;
    }

    public String itemType(String rawItem) {
        Pattern typePattern = Pattern.compile("(?<=([Tt][Yy][Pp][Ee][^A-Za-z])).*?(?=[^A-Za-z0])");
        Matcher regex2 = typePattern.matcher(rawItem);
        if (regex2.find()) {
            return regex2.group().toLowerCase();
        }
        return null;
    }
    public String itemExpiration(String rawItem) {
        Pattern expirationPattern = Pattern.compile("(?<=([Ee][Xx][Pp][Ii][Rr][Aa][Tt][Ii][Oo][Nn][^A-Za-z]))(.)*[^#]");
        Matcher regex3 = expirationPattern.matcher(rawItem);
        if (regex3.find()) {
            return regex3.group();
        }
        return null;
    }

    //this is to catch the 0's in cookie and replace with a o instead
    private String replaceZeros(String rawItem) {

        Pattern pattern = Pattern.compile("[0]");
        Matcher regexName2 = pattern.matcher(rawItem);
        return regexName2.replaceAll("o");
    }

    public ArrayList<String> findKeyValuePairsInRawItemData(String rawItem){
        String stringPattern = "[;|^]";
        ArrayList<String> response = splitStringWithRegexPattern(stringPattern , rawItem);
        return response;
    }

    private ArrayList<String> splitStringWithRegexPattern(String stringPattern, String inputString){
        return new ArrayList<>(Arrays.asList(inputString.split(stringPattern)));
    }

    public void printErrorToFile(ItemParseException e) throws FileNotFoundException {
        //getting entire stack report on the errors that I am getting.
        writer.write(Arrays.stream(e.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n")));

    }

    public void flushExceptionsToFile() {
        writer.close();
    }
}
