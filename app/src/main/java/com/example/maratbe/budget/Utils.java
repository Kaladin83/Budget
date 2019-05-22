package com.example.maratbe.budget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.maratbe.budget.Objects.Category;
import com.example.maratbe.budget.Objects.Item;
import com.example.maratbe.budget.Objects.ItemDrawer;
import com.example.maratbe.budget.Objects.Statistics;
import com.example.maratbe.budget.interfaces.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Utilities. All functionality that is common to multiple classes
 */

public class Utils implements Constants {

    public static Drawable createBorder(int radius, int color, int stroke) {
        GradientDrawable gd;
        gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius(radius);
        gd.setStroke(stroke, BLACK_2);
        return gd;
    }

    public static void closeKeyboard(EditText edit, Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
    }

    public static void openKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static int findColor(String category) {
        int color = Color.parseColor("#FFFFFF");
        for (Category cat: MainActivity.getListOfCombinedCategories())
        {
            if (cat.getName().equals(category))
            {
                color = cat.getColor();
                break;
            }
        }
        return color;
    }

    public static float findSumOfCategory(String category)
    {
        for (Statistics s: MainActivity.getListOfStatistics()) {
            if (s.getCategory().equals(category))
            {
                return (float) s.getSum();
            }
        }
        return 0;
    }

    public static boolean validateTextInput(String string) {
        return string.matches("[a-zA-Z]+");
    }

    public static void calculateSums(Item item, String total)
    {
        boolean categoryFound = false;
        boolean overallFound = false;
        Statistics totalStat = new Statistics();
        int parentStatisticsIndex, parentCategoryIndex;
        for (Statistics stat: MainActivity.getListOfStatistics()) {
            if (item.getCategory().equals(stat.getCategory()))
            {
                parentCategoryIndex = findParentInStatistics(item.getCategory());
                if (parentCategoryIndex > -1)
                {
                    Statistics statFather = MainActivity.getListOfStatistics().get(parentCategoryIndex);
                    updateSum(item, statFather);
                }
                updateSum(item, stat);
                categoryFound = true;

            }
            if (stat.getCategory().equals(total))
            {
                totalStat = new Statistics(stat.getPayDate(), stat.getSum(), stat.getCategory());
                overallFound = true;
            }
        }

        if (!categoryFound)
        {
            parentStatisticsIndex = findParentInStatistics(item.getCategory());
            parentCategoryIndex = findParentIndexInSubCategories(item.getCategory());

            if(parentCategoryIndex > -1)
            {
                if (parentStatisticsIndex == -1)
                {
                    Statistics s = new Statistics(item.getPayDate(), item.getAmount(), MainActivity.getListOfCategories().get(parentCategoryIndex).getName());
                    addSum(s);
                }
                else
                {
                    Statistics statFather = MainActivity.getListOfStatistics().get(parentStatisticsIndex);
                    updateSum(item, statFather);
                }
            }
            Statistics s = new Statistics(item.getPayDate(), item.getAmount(), item.getCategory());
            addSum(s);
        }

        if (overallFound)
        {
            updateSum(item, totalStat);
        }
        else
        {
            Statistics s = new Statistics(item.getPayDate(), item.getAmount(), total);
            addSum(s);
        }
    }

    private static void updateSum(Item item, Statistics stat) {
        stat.setSum(stat.getSum()+ item.getAmount());
        MainActivity.getDbInstance().updateStatistics(stat);
    }

    private static void addSum(Statistics s) {
        MainActivity.getDbInstance().insertStatistics(s);
    }

    public static int findParentIndexInSubCategories(String category) {
        String parent = findParent(category);
        return findCategoryIndex(parent, MainActivity.getListOfCategories());
    }

    public static int findParentInStatistics(String category) {
        String parent = findParent(category);
        int returnIndex = -1;
        for (int i = 0; i < MainActivity.getListOfStatistics().size(); i++) {
            if (MainActivity.getListOfStatistics().get(i).getCategory().equals(parent)) {
                returnIndex =  i;
            }
        }
        return returnIndex;
    }

    public static int findCategoryIndex(String category, ArrayList<Category> list) {
        for (int i = 0; i < list.size(); i++)
        {
            if(list.get(i).getName().equals(category))
            {
                return i;
            }
        }
        return -1;
    }

    public static void fillUpMonthMap(HashMap<String, String> map)
    {
        map.put("01", "JEN");
        map.put("02", "FEB");
        map.put("03", "MAR");
        map.put("04", "APR");
        map.put("05", "MAY");
        map.put("06", "JUN");
        map.put("07", "JUL");
        map.put("08", "AUG");
        map.put("09", "SEP");
        map.put("10", "OCT");
        map.put("11", "NOV");
        map.put("12", "DEC");
    }

    public static int[] getArrayOfCategoryColors() {
        int[] arrayOfColors = new int[MainActivity.getListOfCategories().size()];

        for (int i = 0; i < MainActivity.getListOfCategories().size(); i++) {
            arrayOfColors[i] = MainActivity.getListOfCategories().get(i).getColor();
        }
        return arrayOfColors;
    }

    public static String[] getParentCategories() {
        String[] categories = new String[MainActivity.getListOfCategories().size()];
        int i = 0;
        for (Category cat: MainActivity.getListOfCategories()) {
            if (cat.getParent().equals(""))
            {
                categories[i] = cat.getName();
                i++;
            }
        }
        return Arrays.copyOf(categories, i);
    }

    public static String findInput(ArrayList<Category> list, String[] distinctWords, int numOfWords) {
        String category = "";
        for (int i = 0; i < list.size(); i++)
        {
            Category cat = list.get(i);
            for (int j = 0; j < distinctWords.length - numOfWords + 1; j++)
            {
                String inputCategory = numOfWords == 1? distinctWords[j]: distinctWords[j]+" "+distinctWords[j+1];
                if (cat.getName().equalsIgnoreCase(inputCategory))
                {
                    category = cat.getName();
                    break;
                }
            }
        }
        return category;
    }

    public static ArrayList<Category> getSortedCategoryList() {
        ArrayList<Category> combineSortedList = new ArrayList<>();
        for (int i = 0; i < MainActivity.getListOfCategories().size(); i++)
        {
            combineSortedList.add(MainActivity.getListOfCategories().get(i));
            for (int j = 0; j < MainActivity.getListOfSubCategories().size(); j++)
            {
                if (MainActivity.getListOfCategories().get(i).getName().equals(MainActivity.getListOfSubCategories().get(j).getParent()))
                {
                    combineSortedList.add(MainActivity.getListOfSubCategories().get(j));
                }
            }
        }
        return combineSortedList;
    }

    public static ArrayList<Item> getItemsOfCategories(String category) {
        ArrayList<Item> returnList;

        if (isParent(category))
        {
            returnList = getItemsOfCategoryInStatistics(getListOfSubCategories(category));
        }
        else
        {
            returnList = getItemsOfCategory(category);
        }
        return returnList;
    }

    public static ArrayList<Item> getItemsOfNonParents() {
        ArrayList<Item> returnList = new ArrayList<>();

        for (Statistics stat: MainActivity.getListOfStatistics()) {
            if (!isParent(stat.getCategory()) && !stat.getCategory().equalsIgnoreCase("total"))
            {
                returnList.add(new Item(stat.getCategory(), findMaxDate(stat.getCategory()), stat.getPayDate(), stat.getSum(), ""));
            }
        }
        return returnList;
    }

    private static ArrayList<Item> getItemsOfCategoryInStatistics(ArrayList<Category> list) {
        ArrayList<Item> listOfItems = new ArrayList<>();
        for (Category cats: list)
        {
            for (Statistics stats: MainActivity.getListOfStatistics())
            {
                if (cats.getName().equals(stats.getCategory()))
                {
                    listOfItems.add(new Item(cats.getName(), findMaxDate(stats.getCategory()), stats.getPayDate(), stats.getSum(), ""));
                }
            }
        }
        return listOfItems;
    }

    private static ArrayList<Category> getListOfSubCategories(String parent) {
        ArrayList<Category> cats = new ArrayList<>();
        for(Category cat: MainActivity.getListOfSubCategories())
        {
            if (cat.getParent().equals(parent)) {
                cats.add(cat);
            }
        }
        return cats;
    }

    private static ArrayList<Item> getItemsOfCategory(String category) {
        ArrayList<Item> items = new ArrayList<>();
        for(Item item: MainActivity.getListOfItems())
        {
            if(item.getCategory().equals(category))
            {
                items.add(item);
            }
        }
        return items;
    }

    public static ArrayList<Item> getParentStatistics() {
        ArrayList<Item> statItems = new ArrayList<>();
        for (int i = 0; i < MainActivity.getListOfItems().size(); i++)
        {
            int index;
            String catName;
            boolean alreadyExists = false;
            if (Utils.findParentInStatistics(MainActivity.getListOfItems().get(i).getCategory()) > -1)
            {
                index = Utils.findParentInStatistics(MainActivity.getListOfItems().get(i).getCategory());
            }
            else
            {
                index = Utils.findInStatistics(MainActivity.getListOfItems().get(i).getCategory());
            }
            catName = MainActivity.getListOfStatistics().get(index).getCategory();

            if (i == 0)
            {
                add(statItems, index, catName);
            }
            else
            {
                for (int j = 0; j < statItems.size(); j++)
                {
                    if (statItems.get(j).getCategory().equals(catName))
                    {
                        alreadyExists = true;
                        break;
                    }
                }

                if(!alreadyExists)
                {
                    add(statItems, index, catName);
                }
            }
        }
        return statItems;
    }

    private static void add(ArrayList<Item> statItems, int parentIndex, String parentCategory) {
        double sum = MainActivity.getListOfStatistics().get(parentIndex).getSum();
        statItems.add(new Item(parentCategory, findMaxDate(parentCategory), MainActivity.getCurrentPayDate(), sum, ""));
    }

    public static String findMaxDate(String categoryName) {
        String date = "";
        ArrayList<Category> listOfChildren = getListOfSubCategories(categoryName);
        for (Item item: MainActivity.getListOfItems()) {
            for (Category cat: MainActivity.getListOfCombinedCategories()) {
                if (listOfChildren.size() > 0 ) {
                    if (cat.getParent().equals(categoryName) && cat.getName().equals(item.getCategory())){
                        if (date.compareTo(item.getDate()) < 0)
                        {
                            date = item.getDate();
                        }
                    }
                }
                else
                {
                    if (cat.getName().equals(categoryName) && cat.getName().equals(item.getCategory()))
                    {
                        if (date.compareTo(item.getDate()) < 0)
                        {
                            date = item.getDate();
                        }
                    }
                }
            }
        }
        return date;
    }

    public static boolean isParent(String category)
    {
        for (int i = 0; i< MainActivity.getListOfSubCategories().size(); i++)
        {
            if (category.equals(MainActivity.getListOfSubCategories().get(i).getParent()))
            {
                return true;
            }
        }
        return false;
    }

    public static String findParent(String subCategory)
    {
        for (int i = 0; i< MainActivity.getListOfSubCategories().size(); i++)
        {
            if (subCategory.equals(MainActivity.getListOfSubCategories().get(i).getName()))
            {
                return MainActivity.getListOfSubCategories().get(i).getParent();
            }
        }
        return "";
    }

    public static int findInStatistics(String category) {
        for (int i = 0; i< MainActivity.getListOfStatistics().size(); i++) {
            Statistics stat = MainActivity.getListOfStatistics().get(i);
            if (stat.getCategory().equals(category))
            {
                return i;
            }
        }
        return -1;
    }

    public static void removeFromItems(Item item, ArrayList<Item> items){
        if (items.size() > 0)
        {
            MainActivity.getDbInstance().deleteAllItemsInCategory(item);
        }

        MainActivity.getDbInstance().deleteItem(item);
        MainActivity.getDbInstance().fetchAllItems(item.getPayDate());
        item.setAmount(item.getAmount() * -1);
        Utils.calculateSums(item, "total");
        MainActivity.getDbInstance().fetchMonthlyStatistics(item.getPayDate());
    }

    public static ArrayList getCategoryChildren(String category) {
        ArrayList<Category> children = new ArrayList<>();
        for (int i = 0; i< MainActivity.getListOfSubCategories().size(); i++)
        {
            if (category.equals(MainActivity.getListOfSubCategories().get(i).getParent()))
            {
                children.add(MainActivity.getListOfSubCategories().get(i));
            }
        }
        return  children;
    }

    public static String displayDate(String date)
    {
        return date.substring(0,date.length()-3);
    }

    public static ArrayList<Item> sortList(ArrayList<Item> list) {
        for (int i = 0; i < list.size(); i++)
        {
            for (int j = i+i; j < list.size(); j++)
            {
                if (list.get(i).getDate().compareTo(list.get(j).getDate()) < 0)
                {
                    Item tmpItem = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j,tmpItem);
                }
            }
        }
        return list;
    }

    public static boolean isSubCategory(String category) {
        for (Category cat: MainActivity.getListOfSubCategories()) {
            if (cat.getName().equals(category) && !cat.getParent().equals(""))
            {
                return true;
            }
        }
        return false;
    }
}