package com.example.maratbe.budget;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.maratbe.budget.Objects.Category;
import com.example.maratbe.budget.Objects.Item;
import com.example.maratbe.budget.Objects.Statistics;

import java.util.ArrayList;

/**
 * Class that handles all database queries
 */

public class DBHelper extends SQLiteOpenHelper {
    public static SQLiteDatabase dbInstance;
    public static final String DATABASE_NAME = "MyDBName.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 3);
        dbInstance = this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        dbInstance = db;
                // ITEM table
        db.execSQL(
                "CREATE TABLE ITEM (CATEGORY TEXT, AMOUNT NUMBER, DATE TEXT, PAY_DATE NUMBER, CURRENCY TEXT, DESCRIPTION TEXT, " +
                        "PRIMARY KEY (CATEGORY, AMOUNT, DATE));");
        db.execSQL("CREATE INDEX ITEM_CATEGORY_IND ON ITEM (CATEGORY);");
        db.execSQL("CREATE INDEX ITEM_PAY_DATE_IND ON ITEM (PAY_DATE);");
                // STATISTICS table
        db.execSQL(
                "CREATE TABLE STATISTICS (CATEGORY TEXT, SUM NUMBER, PAY_DATE NUMBER, " +
                        "PRIMARY KEY (PAY_DATE, CATEGORY));");
        db.execSQL("CREATE INDEX STAT_CATEGORY_IND ON ITEM (CATEGORY);");
        db.execSQL("CREATE INDEX STAT_PAY_DATE_IND ON ITEM (PAY_DATE);");
               // COLOR table
        db.execSQL(
                "CREATE TABLE COLOR (NAME NUMBER, PRIMARY KEY(NAME));");
               // CATEGORY Table
        db.execSQL(
                "CREATE TABLE CATEGORY (NAME TEXT, PARENT TEXT, COLOR NUMBER, PRIMARY KEY (NAME));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS ITEM");
        db.execSQL("DROP TABLE IF EXISTS STATISTICS");
        db.execSQL("DROP TABLE IF EXISTS COLOR");
        db.execSQL("DROP TABLE IF EXISTS CATEGORY");
        onCreate(db);
    }

    public void insertItem(Item item)
    {
        Object[] data = new Object[]{item.getCategory(), item.getDate(), item.getPayDate(), item.getAmount(), item.getCurrency()};
        dbInstance.execSQL("INSERT INTO ITEM (CATEGORY, DATE , PAY_DATE, AMOUNT, CURRENCY, DESCRIPTION) VALUES(?,?,?,?,?,?);",  data);
    }

    public void insertStatistics(Statistics stat)
    {
        Object[] data = new Object[]{stat.getCategory(), stat.getPayDate(), stat.getSum()};
        dbInstance.execSQL("INSERT INTO STATISTICS (CATEGORY, PAY_DATE, SUM) VALUES(?,?,?);",  data);

        fetchMonthlyStatistics(stat.getPayDate());
    }

    public void insertColor(Integer color)
    {
        Object[] data = new Object[]{color};
        dbInstance.execSQL("INSERT INTO COLOR (NAME) VALUES(?);",  data);
    }

    public void insertCategory(Category category)
    {
        Object[] data = new Object[]{category.getName(), category.getParent(), category.getColor()};
        dbInstance.execSQL("INSERT INTO CATEGORY (NAME, PARENT, COLOR) VALUES(?,?,?);",  data);
        fetchAllCategories();
    }

    public void deleteAllItemsInCategory(Item item)
    {
        Object[] data = new Object[]{item.getPayDate(), item.getCategory()};
        dbInstance.execSQL("DELETE FROM ITEM WHERE PAY_DATE = ? AND CATEGORY IN " +
                "(SELECT NAME FROM CATEGORY WHERE PARENT = ?)", data);

        dbInstance.execSQL("DELETE FROM STATISTICS WHERE PAY_DATE = ? AND CATEGORY IN " +
                "(SELECT NAME FROM CATEGORY WHERE PARENT = ?)", data);
    }

    public void deleteAllMonthlyItems(int month)
    {
        Object[] data = new Object[]{month};
        dbInstance.execSQL("DELETE FROM ITEM WHERE PAY_DATE = ?",  data);
    }

    public void deleteItem(Item item)
    {
        Object[] data = new Object[]{item.getDate(), item.getCategory(), item.getAmount()};
        dbInstance.execSQL("DELETE FROM ITEM WHERE DATE = ? AND CATEGORY = ? AND AMOUNT = ?",  data);
    }

    public void deleteStatistics(int payDate, String category)
    {
        Object[] data = new Object[]{payDate, category};
        dbInstance.execSQL("DELETE FROM STATISTICS WHERE PAY_DATE = ? AND CATEGORY = ?",  data);

    }

    public void deleteMonthlyStatistics(int payDate)
    {
        Object[] data = new Object[]{payDate};
        dbInstance.execSQL("DELETE FROM STATISTICS WHERE PAY_DATE = ?",  data);
    }

    public void deleteCategory(Category category)
    {
        Object[] data = new Object[]{category.getName()};
        dbInstance.execSQL("DELETE FROM CATEGORY WHERE NAME = ?",  data);
        fetchAllCategories();
    }

    public void deleteColor(int color)
    {
        Object[] data = new Object[]{color};
        dbInstance.execSQL("DELETE FROM COLOR WHERE NAME = ?",  data);
    }

    public void clearAllStatistics()
    {
        dbInstance.execSQL("DELETE FROM STATISTICS");
    }

    public void clearAllColors()
    {
        dbInstance.execSQL("DELETE FROM COLOR");
    }

    public void clearAllCategories()
    {
        dbInstance.execSQL("DELETE FROM CATEGORY");
    }

    public void updateCategoryColor(Category category)
    {
        Object[] data = new Object[]{category.getColor(), category.getName()};
        dbInstance.execSQL("UPDATE CATEGORY SET COLOR = ? WHERE NAME = ?",  data);
    }

    public void updateItemCategory(String newName,String oldName)
    {
        Object[] data = new Object[]{newName, oldName};
        dbInstance.execSQL("UPDATE ITEM SET CATEGORY = ? WHERE CATEGORY = ?",  data);
    }

    public void updateItemCategory(int payDate, double sum, String category)
    {
        Object[] data = new Object[]{payDate, sum, category};
        dbInstance.execSQL("UPDATE STATISTICS SET SUM = ? WHERE CATEGORY = ? AND PAY_DATE = ?",  data);
    }

    public void updateStatistics(Statistics stats)
    {
        if (stats.getSum() == 0)
        {
            deleteStatistics(stats.getPayDate(), stats.getCategory());
        }
        else
        {
            updateStatistics(stats.getPayDate(), stats.getSum(), stats.getCategory());
        }

        fetchMonthlyStatistics(stats.getPayDate());
    }

    private void updateStatistics(int payDate, double sum, String category) {
        Object[] data = new Object[]{sum, category, payDate};
        dbInstance.execSQL("UPDATE STATISTICS SET SUM = ? WHERE CATEGORY = ? AND PAY_DATE = ?",  data);
    }

    public void fetchAllCategories()
    {
        ArrayList<Category> list = new ArrayList<>();
        Cursor res =  dbInstance.rawQuery( "SELECT NAME, PARENT, COLOR FROM CATEGORY", null);
        res.moveToFirst();

        while(!res.isAfterLast()){
            String name = res.getString(res.getColumnIndex("NAME"));
            String parent = res.getString(res.getColumnIndex("PARENT"));
            int color = res.getInt(res.getColumnIndex("COLOR"));
            list.add(new Category(name, parent, color));
            res.moveToNext();
        }
        res.close();
        populateLists(list);
    }

    public void fetchAllColors()
    {
        ArrayList<Integer> list = new ArrayList<>();
        Cursor res =  dbInstance.rawQuery( "SELECT NAME FROM COLOR", null);
        res.moveToFirst();

        while(!res.isAfterLast()){
            list.add(res.getInt(res.getColumnIndex("NAME")));
            res.moveToNext();
        }
        res.close();
        MainActivity.setListOfColors(list);
    }

    public void fetchAllMonthsFromStatistics()
    {
        ArrayList<Integer> list = new ArrayList<>();
        Cursor res =  dbInstance.rawQuery( "SELECT DISTINCT PAY_DATE FROM STATISTICS", null);
        res.moveToFirst();

        while(!res.isAfterLast()){
            list.add(res.getInt(res.getColumnIndex("PAY_DATE")));
            res.moveToNext();
        }
        res.close();
        MainActivity.setListOfMonths(list);
    }

    public void fetchAllItems(int payDate)
    {
        ArrayList<Item> list = new ArrayList<>();
        Cursor res =  dbInstance.rawQuery( "SELECT CATEGORY, AMOUNT, DATE, DESCRIPTION FROM ITEM WHERE PAY_DATE = ?",
                new String[]{String.valueOf(payDate)});
        res.moveToFirst();

        while(!res.isAfterLast()){
            String category = res.getString(res.getColumnIndex("CATEGORY"));
            double amount = res.getDouble(res.getColumnIndex("AMOUNT"));
            String date = res.getString(res.getColumnIndex("DATE"));
            String description = res.getString(res.getColumnIndex("DESCRIPTION"));
            list.add(new Item(category, date, payDate, amount, description));
            res.moveToNext();
        }
        res.close();
        MainActivity.setListOfItems(list);
    }

    public void fetchMonthlyStatistics(int payDate)
    {
        ArrayList<Statistics> list = new ArrayList<>();
        dbInstance = this.getReadableDatabase();
        Cursor res =  dbInstance.rawQuery( "SELECT CATEGORY, SUM FROM STATISTICS WHERE PAY_DATE = ?",
                new String[]{String.valueOf(payDate)});
        res.moveToFirst();

        while(!res.isAfterLast()){
            String category = res.getString(res.getColumnIndex("CATEGORY"));
            double sum = res.getDouble(res.getColumnIndex("SUM"));
            list.add(new Statistics(payDate, sum, category));
            res.moveToNext();
        }
        res.close();
        populateMonthlyStatistics(list);
    }

    public double fetchCategoryAverage(String category)
    {
        double average = 0;
        dbInstance = this.getReadableDatabase();
        Cursor res =  dbInstance.rawQuery( "SELECT AVG(SUM) AVERAGE FROM STATISTICS WHERE CATEGORY = ?", new String[]{category});
        res.moveToFirst();

        while(!res.isAfterLast()){
            average = res.getDouble(res.getColumnIndex("AVERAGE"));
            res.moveToNext();
        }
        res.close();
        return average;
    }

    public int findItemsByCategory(String category)
    {
        int result = 0;
        dbInstance = this.getReadableDatabase();
        Cursor res =  dbInstance.rawQuery( "SELECT 1 FROM ITEM WHERE CATEGORY = ?", new String[]{category});
        res.moveToFirst();

        while(!res.isAfterLast()){
            result = 1;
            res.moveToNext();
        }
        res.close();
        return result;
    }

    private void populateMonthlyStatistics(ArrayList<Statistics> list) {
        MainActivity.setListOfStatistics(list);
        for (Statistics s: MainActivity.getListOfStatistics()) {
            s.setMean(fetchCategoryAverage(s.getCategory()));
        }

        if (list.size() > 0) {
            MainActivity.setListOfParentItems(Utils.getParentStatistics());
            MainActivity.getListOfCombinedItems().clear();
            MainActivity.setInitialListOfCombinedItems(Utils.sortList(MainActivity.getListOfParentItems()));
        }
    }

    private void populateLists(ArrayList<Category> list) {
        ArrayList<Category> subCategories = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.size(); j++) {
                if (list.get(i).getName().equals(list.get(j).getParent()))
                {
                    subCategories.add(list.get(j));
                    list.remove(j);
                    j--;
                }
            }
        }
        MainActivity.setListOfCategories(list);
        MainActivity.setListOfSubCategories(subCategories);
        MainActivity.setListOfCombinedCategories(Utils.getSortedCategoryList());
    }
}
