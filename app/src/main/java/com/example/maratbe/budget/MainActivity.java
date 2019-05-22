package com.example.maratbe.budget;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.maratbe.budget.Objects.Category;
import com.example.maratbe.budget.Objects.Item;
import com.example.maratbe.budget.Objects.ItemDrawer;
import com.example.maratbe.budget.Objects.Statistics;
import com.example.maratbe.budget.interfaces.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Constants {
    private static DBHelper dbInstance;
    private CategoryPicker categoryPicker;
    private Charts charts;
    private MainRecycler mainRecycler;
    private Edit edit;
    private LinearLayout mainLayout;
    private MainActivity mainActivity;
    private ViewPager pager;
    private List activities;
    private static PackageManager packageManger;
    private Button categoriesButton, chartsButton, voiceButton, editButton, syncButton;
    private static int screenWidth, screenHeight, currentPayDate, logicalDensity;
    private static Item lastAddedItem;
    private static ArrayList<Item> listOfItems = new ArrayList<>();
    private static ArrayList<Item> listOfParentItems = new ArrayList<>();
    private static ArrayList<Item> listOfSubItems = new ArrayList<>();
    private static ArrayList<Category> listOfCategories = new ArrayList<>();
    private static ArrayList<Category> listOfSubCategories = new ArrayList<>();
    private static ArrayList<Category> listOfCombinedCategories = new ArrayList<>();
    private static ArrayList<Statistics> listOfStatistics = new ArrayList<>();
    private static ArrayList<Integer> listOfMonths = new ArrayList<>();
    private static ArrayList<Integer> listOfColors = new ArrayList<>();
    private static ArrayList<ItemDrawer> listOfCombinedItems = new ArrayList<>();

    public static PackageManager getPackageManagerInstance()
    {
        return packageManger;
    }

    public static DBHelper getDbInstance()
    {
        return dbInstance;
    }

    public static Item getLastAddedItem(){
        return lastAddedItem;
    }

    public static void setLastAddedItem(Item item){
        lastAddedItem = item;
    }

    public static ArrayList<Integer> getListOfMonths()
    {
        return listOfMonths;
    }

    public static void setListOfMonths(ArrayList<Integer> list)
    {
        listOfMonths = new ArrayList<>(list);
    }
    public static ArrayList<Item> getListOfItems()
    {
        return listOfItems;
    }

    public static void setListOfParentItems(ArrayList<Item> list)
    {
        listOfParentItems = new ArrayList<>(list);
        Utils.sortList(listOfParentItems);
    }

    public static ArrayList<Item> getListOfParentItems()
    {
        return listOfParentItems;
    }

    public static void setListOfSubItems(ArrayList<Item> list)
    {
        listOfSubItems = new ArrayList<>(list);
    }

    public static ArrayList<Item> getListOfSubItems()
    {
        return listOfSubItems;
    }

    public static void setListOfItems(ArrayList<Item> list)
    {
        listOfItems = new ArrayList<>(list);
    }

    public static ArrayList<ItemDrawer> getListOfCombinedItems()
    {
        return listOfCombinedItems;
    }

    public static void setListOfCombinedItems(ArrayList<ItemDrawer> list)
    {
        listOfCombinedItems = new ArrayList<>(list);
    }

    public static void setInitialListOfCombinedItems(ArrayList<Item> list)
    {
        for (Item item: list) {
            listOfCombinedItems.add(new ItemDrawer(item, Color.WHITE, false, CATEGORY_LVL));
        }
    }

    public static ArrayList<Category> getListOfCategories()
    {
        return listOfCategories;
    }

    public static void setListOfCategories(ArrayList<Category> list)
    {
        listOfCategories = new ArrayList<>(list);
    }

    public static ArrayList<Category> getListOfSubCategories()
    {
        return listOfSubCategories;
    }

    public static void setListOfSubCategories(ArrayList<Category> list)
    {
        listOfSubCategories = new ArrayList<>(list);
    }

    public static ArrayList<Category> getListOfCombinedCategories()
    {
        return listOfCombinedCategories;
    }

    public static void setListOfCombinedCategories(ArrayList<Category> list)
    {
        listOfCombinedCategories = new ArrayList<>(list);
    }

    public static ArrayList<Integer> getListOfColors()
    {
        return listOfColors;
    }

    public static void setListOfColors(ArrayList<Integer> colors)
    {
        listOfColors = new ArrayList<>(colors);
    }

    public static ArrayList<Statistics> getListOfStatistics()
    {
        return listOfStatistics;
    }

    public static void setListOfStatistics(ArrayList<Statistics> list)
    {
        listOfStatistics = new ArrayList<>(list);
    }

    public static int getScreenHeight()
    {
        return screenHeight;
    }

    public static int getScreenWidth()
    {
        return screenWidth;
    }

    public static int getLogicalDensity()
    {
        return logicalDensity;
    }

    public static int getCurrentPayDate()
    {
        return currentPayDate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;
        packageManger = getPackageManager();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM", Locale.US);
        currentPayDate = Integer.parseInt(dateFormat.format(new Date()).replaceAll("/", ""));
        setDimensions();
        dbInstance = new DBHelper(this);

        PackageManager packageManger = getPackageManager();
        activities = packageManger.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
       // deleteItems();
        //populateInitialCategories();
        LinearLayout menuLayout = (LinearLayout) findViewById(R.id.main_menu_layout);
        menuLayout.setBackgroundColor(GRAY_2);
        fetchData();
        findLastAddedItem();
        createButtons();
        createPager();
    }

    private void createPager() {
        pager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setCurrentItem(2);
        changeSelection(false, false, true, false);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position)
                {
                    case 0:
                        changeSelection(true, false, false, false); break;
                    case 1:
                        changeSelection(false, true, false,false); break;
                    case 2:
                        changeSelection(false, false, true, false); break;
                    case 3:
                        changeSelection(false, false, false, true); break;
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void changeSelection(boolean b, boolean b1, boolean b2, boolean b3) {
        categoriesButton.setSelected(b);
        chartsButton.setSelected(b1);
        voiceButton.setSelected(b2);
        editButton.setSelected(b3);
    }

    private void deleteItems()
    {
        dbInstance.deleteAllMonthlyItems(currentPayDate);
        dbInstance.clearAllStatistics();
    }

    private void populateInitialCategories() {
        dbInstance.clearAllColors();
        dbInstance.insertColor(BLUE_3);
        dbInstance.insertColor(BLUE_4);
        dbInstance.insertColor(RED_4);
        dbInstance.insertColor(RED_5);
        dbInstance.insertColor(GREEN_3);
        dbInstance.insertColor(GREEN_4);
        dbInstance.insertColor(YELLOW_3);
        dbInstance.insertColor(YELLOW_4);
        dbInstance.insertColor(PURPLE_3);
        dbInstance.insertColor(PURPLE_4);

        dbInstance.clearAllCategories();
        dbInstance.insertCategory(new Category("home", "", BLUE_3));
        dbInstance.insertCategory(new Category("food", "", BLUE_4));
        dbInstance.insertCategory(new Category("cafes", "", RED_4));
        dbInstance.insertCategory(new Category("clothes", "", YELLOW_4));
        dbInstance.insertCategory(new Category("car", "", GREEN_4));
        dbInstance.insertCategory(new Category("other", "", PURPLE_4));
    }

    private void fetchData() {
        dbInstance.fetchAllCategories();
        dbInstance.fetchAllItems(currentPayDate);
        dbInstance.fetchAllColors();
        dbInstance.fetchMonthlyStatistics(currentPayDate);
    }

    public void findLastAddedItem() {
        String date = "";
        for (Item item: listOfItems) {
            if (date.compareTo(item.getDate()) < 0)
            {
                lastAddedItem = item;
            }
        }
    }

    public void setDimensions()
    {
        int navigationBarHeight = 0;
        int resourceId = this.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navigationBarHeight = this.getResources().getDimensionPixelSize(resourceId);
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels - navigationBarHeight;
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        logicalDensity = (int) metrics.density;
    }

    public boolean hasNavBar (Resources resources)
    {
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        return id > 0 && resources.getBoolean(id);
    }

    private void createButtons() {
        categoriesButton = (Button) findViewById(R.id.categories_btn);
        categoriesButton.setOnClickListener(this);
        chartsButton = (Button) findViewById(R.id.charts_btn);
        chartsButton.setOnClickListener(this);
        voiceButton = (Button) findViewById(R.id.voice_btn);
        voiceButton.setOnClickListener(this);
        editButton = (Button) findViewById(R.id.edit_btn);
        editButton.setOnClickListener(this);
        syncButton = (Button) findViewById(R.id.sync_btn);
        syncButton.setOnClickListener(this);

//        FrameLayout categoryButton = (FrameLayout) findViewById(R.id.categories_layout);
//        categoryButton.setClickable(true);
//        categoryButton.setOnClickListener(this);
//        FrameLayout chartButton = (FrameLayout) findViewById(R.id.charts_layout);
//        chartButton.setOnClickListener(this);
//        FrameLayout writeButton = (FrameLayout) findViewById(R.id.write_layout);
//        writeButton.setOnClickListener(this);
//        categoryButton.setBackground(Utils.createBorder(20, ContextCompat.getColor(this, R.color.toolBar), 1));
//        chartButton.setBackground(Utils.createBorder(20, ContextCompat.getColor(this, R.color.toolBar), 1));
//        writeButton.setBackground(Utils.createBorder(20, ContextCompat.getColor(this, R.color.toolBar), 1));

    }

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    public void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speech recognition demo");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            mainRecycler.populateListOfItems(matches);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.categories_btn:
                pager.setCurrentItem(0);break;
            case R.id.charts_btn:
                pager.setCurrentItem(1);break;
            case R.id.voice_btn:
                if (pager.getCurrentItem() == 2)
                {
                    if (activities.size() != 0) {
                        startVoiceRecognitionActivity();
                    }
                }
                else
                {
                    pager.setCurrentItem(2);
                }
                break;
            case R.id.edit_btn:
                pager.setCurrentItem(3);break;
        }
    }

    class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    categoryPicker = new CategoryPicker();
                    CategoryPicker.setListener(mainRecycler);
                    return categoryPicker;
                case 1:
                    charts = new Charts();
                    //ColorPicker.setFavouriteColorsListener(favoriteColors);
                    return charts;
                case 2:
                    mainRecycler = new MainRecycler();
                    return mainRecycler;
                case 3:
                    edit = new Edit();
                    return edit;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}