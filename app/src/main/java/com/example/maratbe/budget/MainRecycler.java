package com.example.maratbe.budget;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.maratbe.budget.Objects.Category;
import com.example.maratbe.budget.Objects.Item;
import com.example.maratbe.budget.Objects.ItemDrawer;
import com.example.maratbe.budget.Objects.Statistics;
import com.example.maratbe.budget.interfaces.Constants;
import com.example.maratbe.budget.interfaces.OnUpdateMainActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainRecycler extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, OnUpdateMainActivity, Constants {
    private View mainView;
    private MainActivity mainActivity;
    private RecyclerViewAdapter parentItemAdapter;
    private LinearLayout mainLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.recycler, container, false);

        mainLayout = mainView.findViewById(R.id.main_recycler_layout);
        createRecycler();
        return mainView;
    }

    public void setMainActivityInstance(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
    }


   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/


    public void populateListOfItems(ArrayList<String> matches) {
        String newCategoryName = "", categoryName = "";
        boolean categoryFound = false;
        boolean amountFound = false;
        double amount = 0;

        for (String input: matches) {
            String[] distinctWords = input.split(" ");
            if (!categoryFound)
            {
                int numOfWords = distinctWords.length - 1;
                // checking if input is found in sub categories
                if (numOfWords > 0)
                {
                    categoryName =  Utils.findInput(MainActivity.getListOfSubCategories(), distinctWords, numOfWords);
                    if (categoryName.equals(""))
                    {
                        // checking if input is found in categories
                        categoryName =  Utils.findInput(MainActivity.getListOfCategories(), distinctWords, numOfWords);
                        if (!categoryName.equals(""))
                        {
                            //parentName is empty if category is not a parent category, otherwise creating new sub category with parentName+other
                            newCategoryName = Utils.isParent(categoryName)? categoryName + " other": categoryName;
                            categoryFound = true;
                        }
                    }
                    else
                    {
                        categoryFound = true;
                    }
                }
            }

            if (!amountFound)
            {
                for (String word: distinctWords)
                {
                    if(word.matches("([0-9]*[.])?[0-9]*"))
                    {
                        amount = Double.parseDouble(word);
                        amountFound = true;
                        break;
                    }
                }
            }
            if (categoryFound && amountFound)
            {
                addItem(newCategoryName, categoryName, amount);
                break;
            }
        }
        showItems();
    }

    private void addItem(String newCategoryName, String categoryName, double amount) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
        if (!newCategoryName.equals(""))
        {
            if (Utils.findCategoryIndex(newCategoryName, MainActivity.getListOfCombinedCategories()) == -1)
            {
                Category newCat = new Category(newCategoryName, categoryName, Color.WHITE);
                MainActivity.getDbInstance().insertCategory(newCat);
            }

            categoryName = newCategoryName;
        }
        Item item = new Item(categoryName, dateFormat.format(new Date()), MainActivity.getCurrentPayDate(), amount, "");
        MainActivity.setLastAddedItem(item);
        MainActivity.getListOfItems().add(item);
        MainActivity.getDbInstance().insertItem(item);
        Utils.calculateSums(item, getString(R.string.total_sum));
    }

    public void createRecycler()
    {
        parentItemAdapter = new RecyclerViewAdapter();
        LinearLayoutManager pLinearLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView recyclerView = mainView.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(parentItemAdapter);
        recyclerView.setLayoutManager(pLinearLayoutManager);
        recyclerView.setBackgroundColor(Color.WHITE);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        ItemTouchHelper.SimpleCallback parentSwiper = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(parentSwiper).attachToRecyclerView(recyclerView);
    }

    public void showItems()
    {
        parentItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        ArrayList<Item> deletedItems = new ArrayList<>();
        Item deletedItem = MainActivity.getListOfCombinedItems().get(position).getItem();
        if(MainActivity.getListOfCombinedItems().get(position).getLevel() < ITEM_LVL)
        {
            findDeletedItems(deletedItem, deletedItems);
        }

        Snackbar snackbar = Snackbar
                .make(mainLayout, "The item was removed", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", view -> {
            if (deletedItems.size() > 0)
            {
                restoreDeletedItems(deletedItems);
            }
            else
            {
                deletedItem.setAmount(deletedItem.getAmount() * -1);
                MainActivity.getDbInstance().insertItem(deletedItem);
                MainActivity.getDbInstance().fetchAllItems(deletedItem.getPayDate());

                Utils.calculateSums(deletedItem, getString(R.string.total_sum));
                MainActivity.getDbInstance().fetchMonthlyStatistics(deletedItem.getPayDate());
            }

            showItems();
        });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
        Utils.removeFromItems(deletedItem, deletedItems);
        showItems();
    }

    private void findDeletedItems(Item deletedItem, ArrayList<Item> deletedItems) {
        ArrayList<Category> listOfChildren = Utils.getCategoryChildren(deletedItem.getCategory());
        if (listOfChildren.size() == 0)
        {
            listOfChildren.add(new Category(deletedItem.getCategory(), "", Color.WHITE));
        }
        for (int i = 0; i < listOfChildren.size(); i++)
        {
            for (int j = 0; j < MainActivity.getListOfItems().size(); j++) {
                if(MainActivity.getListOfItems().get(j).getCategory().equals(listOfChildren.get(i).getName()))
                {
                    deletedItems.add(MainActivity.getListOfItems().get(j));
                }
            }
        }
    }

    private void restoreDeletedItems(ArrayList<Item> deletedItems) {
        for (Item item: deletedItems) {
            MainActivity.getDbInstance().insertItem(item);
            MainActivity.getDbInstance().fetchAllItems(item.getPayDate());
            item.setAmount(item.getAmount() * -1);
            Utils.calculateSums(item, getString(R.string.total_sum));
            MainActivity.getDbInstance().fetchMonthlyStatistics(item.getPayDate());
            //listOfItems.set(deletedItemsMap.get(item.getCategory()), item);
        }
    }

    @Override
    public void refreshItemRecyclerView() {
        showItems();
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
        public class MyViewHolder extends RecyclerView.ViewHolder {
            FrameLayout objectLayout;
            RelativeLayout mainLayout, backgroundLayout, mainRowLayout;
            TextView dateTxt, arrowTxt;

            private MyViewHolder(View v) {
                super(v);
                objectLayout = v.findViewById(R.id.object_layout);
                mainLayout = v.findViewById(R.id.main_layout);
                mainRowLayout = v.findViewById(R.id.main_row_layout);
                backgroundLayout = v.findViewById(R.id.background_layout);
                dateTxt = v.findViewById(R.id.date_added_txt);
                arrowTxt = v.findViewById(R.id.arrow_txt);
            }
        }
        public class MyViewHolder1 extends MyViewHolder {
            RelativeLayout categorySumsLayout;
            TextView categoryTxt, categorySumTxt, categoryAverageTxt;
            View separator;

            private MyViewHolder1(View v) {
                super(v);
                categoryTxt = v.findViewById(R.id.category_txt);
                categorySumsLayout = v.findViewById(R.id.category_sums_layout);
                categorySumTxt = v.findViewById(R.id.category_total_txt);
                categoryAverageTxt = v.findViewById(R.id.category_average_txt);
                separator = v.findViewById(R.id.separator);
            }
        }

        public class MyViewHolder2 extends MyViewHolder {
            RelativeLayout descriptionLayout;
            TextView amountTxt;
            Button okButton, cancelButton;
            EditText descriptionEdit;

            private MyViewHolder2(View v) {
                super(v);
                cancelButton = v.findViewById(R.id.cancel_btn);
                okButton = v.findViewById(R.id.ok_btn);
                descriptionEdit = v.findViewById(R.id.description_edit);
                descriptionLayout = v.findViewById(R.id.description_layout);
                amountTxt = v.findViewById(R.id.amount_txt);
            }
        }

        public RecyclerViewAdapter() {

        }

        @Override
        public int getItemViewType(int position) {
            return MainActivity.getListOfCombinedItems().get(position).getLevel() == ITEM_LVL? 2:  1;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 1) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.populator_of_category, parent, false);

                return new MyViewHolder1(v);
            } else {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.populator_of_item, parent, false);
                return new MyViewHolder2(v);
            }
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            int height = 45;
            int margins = 10;
            FrameLayout.LayoutParams fParams;
            int parentLine = 45;
            int subTotalLine = 25;
            int subCategoryWidth = (int)(MainActivity.getScreenWidth() *0.85);

            int statIndex = Utils.findInStatistics(MainActivity.getListOfCombinedItems().get(position).getItem().getCategory());
            Statistics stats = MainActivity.getListOfStatistics().get(statIndex);
            if (holder instanceof MyViewHolder1)
            {
                MyViewHolder1 holder1 = (MyViewHolder1) holder;
                height = createCategoryRow(holder1, position, height, parentLine + subTotalLine + margins);

                int color = Utils.findColor(MainActivity.getListOfCombinedItems().get(position).getItem().getCategory());
                holder1.categoryTxt.setText(MainActivity.getListOfCombinedItems().get(position).getItem().getCategory());
                holder1.categoryTxt.setBackground(Utils.createBorder(20, color, 1));
                holder1.categorySumsLayout.setPadding(15,0,15,0);
                holder1.categorySumsLayout.setBackground(Utils.createBorder(15, Constants.GRAY_3, 1));
                holder1.categorySumTxt.setText(getString(R.string.category_total, String.valueOf(stats.getSum())));
                holder1.categoryAverageTxt.setText(getString(R.string.category_avrage, String.valueOf(stats.getMean())));

                if (MainActivity.getListOfCombinedItems().get(position).getLevel() == SUB_CATEGORY_LVL)
                {
                    holder1.arrowTxt.setVisibility(View.VISIBLE);

                    RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(subCategoryWidth, subTotalLine *MainActivity.getLogicalDensity());
                    rParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    rParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    rParams.setMargins(0, 5*MainActivity.getLogicalDensity(), 15*MainActivity.getLogicalDensity(), 5*MainActivity.getLogicalDensity());
                    holder1.categorySumsLayout.setLayoutParams(rParams);
                }
                else
                {
                    holder1.arrowTxt.setVisibility(View.GONE);

                    RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(MainActivity.getScreenWidth(), subTotalLine *MainActivity.getLogicalDensity());
                    rParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    rParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    rParams.setMargins(15*MainActivity.getLogicalDensity(), 5*MainActivity.getLogicalDensity(), 15*MainActivity.getLogicalDensity(), 5*MainActivity.getLogicalDensity());
                    holder1.categorySumsLayout.setLayoutParams(rParams);
                }
            }
            else
            {
                MyViewHolder2 holder2 = (MyViewHolder2) holder;
                height = createItemRow(holder2, position, height, parentLine + subTotalLine + margins);
                holder2.descriptionEdit.setText(MainActivity.getListOfCombinedItems().get(position).getItem().getDescription());
                holder2.descriptionEdit.setBackground(Utils.createBorder(10, Color.TRANSPARENT, 1));
                holder2.descriptionEdit.setPadding(10,5,10,5);
                holder2.descriptionEdit.addTextChangedListener(new MyTextWatcher(holder2, MainActivity.getLogicalDensity(), subCategoryWidth));
                holder2.amountTxt.setText(String.valueOf(MainActivity.getListOfCombinedItems().get(position).getItem().getAmount()));
                holder2.amountTxt.setTextColor(MainActivity.getListOfCombinedItems().get(position).getItem().getDate().equals
                        (MainActivity.getLastAddedItem().getDate())? GREEN_2: Color.BLACK);
                holder2.okButton.setBackground(ContextCompat.getDrawable(mainView.getContext(), R.drawable.check));
                holder2.okButton.setOnClickListener(v -> {
                    if (holder2.descriptionEdit.getText().toString().equals(""))
                    {
                        // update description  in table and refresh
                    }
                });
                holder2.cancelButton.setBackground(ContextCompat.getDrawable(mainView.getContext(), R.drawable.close_red));
                holder2.cancelButton.setOnClickListener(v -> holder2.descriptionEdit.setText(""));

                RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(subCategoryWidth, parentLine *MainActivity.getLogicalDensity());
                rParams.setMargins(35*MainActivity.getLogicalDensity(), 0, 10*MainActivity.getLogicalDensity(), 0);
                holder2.mainRowLayout.setLayoutParams(rParams);
            }

            fParams = new FrameLayout.LayoutParams(MainActivity.getScreenWidth(), (int) Math.ceil(height * MainActivity.getLogicalDensity()));
            fParams.gravity = Gravity.TOP;
            LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams((int)(MainActivity.getScreenWidth()*0.9), (int) Math.ceil(subTotalLine * MainActivity.getLogicalDensity()));
            lParams.gravity = Gravity.CENTER;
            lParams.setMargins(10,(int) Math.ceil(8 * MainActivity.getLogicalDensity()),10,(int) Math.ceil(11 * MainActivity.getLogicalDensity()));
            holder.mainLayout.setLayoutParams(fParams);
            holder.mainLayout.setGravity(Gravity.CENTER);
            holder.mainLayout.setVerticalGravity(Gravity.TOP);
            holder.objectLayout.setLayoutParams(fParams);
            holder.backgroundLayout.setLayoutParams(fParams);

            holder.dateTxt.setText(Utils.displayDate(MainActivity.getListOfCombinedItems().get(position).getItem().getDate()));
            holder.dateTxt.setTextColor(MainActivity.getListOfCombinedItems().get(position).getItem().getDate().equals(MainActivity.getLastAddedItem().getDate())? GREEN_2: Color.BLACK);

            holder.mainRowLayout.setOnClickListener(view -> {
                MainActivity.setListOfParentItems(Utils.getParentStatistics());
                addToCombinedItems(position);
                notifyDataSetChanged();
            });
        }

        public void addToCombinedItems(int position) {
            String date = "", subCategoryName = "", parentName;
            int selectedParent, selectedSub;
            ArrayList<Item> listOfItems = new ArrayList<>();
            ArrayList<Item> listOfSubs;

            boolean isItemChosen = MainActivity.getListOfCombinedItems().get(position).getLevel() == ITEM_LVL;
            boolean isItemExpanded = MainActivity.getListOfCombinedItems().get(position).isExtended();
            boolean isCategoryExpanded = MainActivity.getListOfCombinedItems().get(position).getLevel() == CATEGORY_LVL && MainActivity.getListOfCombinedItems().get(position).isExtended();
            boolean isSubCategoryExpanded = MainActivity.getListOfCombinedItems().get(position).getLevel() == SUB_CATEGORY_LVL && MainActivity.getListOfCombinedItems().get(position).isExtended();
            //category which has subCategories has been selected
            if(Utils.isParent(MainActivity.getListOfCombinedItems().get(position).getItem().getCategory()))
            {
                parentName = MainActivity.getListOfCombinedItems().get(position).getItem().getCategory();
            }
            else
            {
                subCategoryName = MainActivity.getListOfCombinedItems().get(position).getItem().getCategory();
                parentName = Utils.findParent(subCategoryName);
                listOfItems = Utils.sortList(Utils.getItemsOfCategories(subCategoryName));
                date = MainActivity.getListOfCombinedItems().get(position).getItem().getDate();
            }

            listOfSubs = Utils.sortList(Utils.getItemsOfCategories(parentName));
            MainActivity.setListOfCombinedItems(new ArrayList<>());

            selectedParent = addCategoriesToCombinedItems(parentName.equals("")? subCategoryName: parentName, isCategoryExpanded);

            if (!isCategoryExpanded)
            {
                selectedSub = addSubCategoriesToCombinedItems(listOfSubs, subCategoryName, isSubCategoryExpanded, selectedParent);
                if (!isSubCategoryExpanded)
                {
                    addItemsToCombinedItems(listOfItems, date, isItemChosen, isItemExpanded, selectedSub == -1? selectedParent: selectedSub);
                }
            }
        }

        private int addCategoriesToCombinedItems(String parentName, boolean isCategoryExpanded) {
            int selectedParent = -1;
            for (int i = 0; i < MainActivity.getListOfParentItems().size(); i++ ) {
                if (MainActivity.getListOfParentItems().get(i).getCategory().equals(parentName) && !isCategoryExpanded) {
                    MainActivity.getListOfCombinedItems().add(new ItemDrawer(MainActivity.getListOfParentItems().get(i), BLUE_5, true, CATEGORY_LVL));
                    selectedParent = i;
                } else {
                    MainActivity.getListOfCombinedItems().add(new ItemDrawer(MainActivity.getListOfParentItems().get(i), Color.WHITE, false, CATEGORY_LVL));
                }
            }
            return selectedParent;
        }

        private int addSubCategoriesToCombinedItems(ArrayList<Item> listOfSubs, String subCategoryName, boolean isSubCategoryExpanded, int selectedParent) {
            int selectedSub = -1;
            for (int i = 0; i < listOfSubs.size(); i++) {
                if (subCategoryName.equals(listOfSubs.get(i).getCategory()) && !isSubCategoryExpanded) {
                    MainActivity.getListOfCombinedItems().add(selectedParent+ 1 + i, new ItemDrawer(listOfSubs.get(i), BLUE_5, true, SUB_CATEGORY_LVL));
                    selectedSub = selectedParent+ 1 + i;
                }
                else {
                    MainActivity.getListOfCombinedItems().add(selectedParent+ 1 + i, new ItemDrawer(listOfSubs.get(i), BLUE_5, false, SUB_CATEGORY_LVL));
                }
            }
            return selectedSub;
        }

        private void addItemsToCombinedItems(ArrayList<Item> listOfItems, String date, boolean isItemChosen, boolean isItemExpanded, int selectedSub) {
            for (int i = 0; i < listOfItems.size(); i++) {
                if (date.equals(listOfItems.get(i).getDate()))
                {
                    if (isItemChosen && !isItemExpanded)
                    {
                        MainActivity.getListOfCombinedItems().add(selectedSub + 1 + i, new ItemDrawer(listOfItems.get(i), BLUE_5, true, ITEM_LVL));
                    }
                    else
                    {
                        MainActivity.getListOfCombinedItems().add(selectedSub + 1 + i, new ItemDrawer(listOfItems.get(i), BLUE_5, false, ITEM_LVL));
                    }
                }
                else
                {
                    MainActivity.getListOfCombinedItems().add(selectedSub + 1 + i, new ItemDrawer(listOfItems.get(i), BLUE_5, false, ITEM_LVL));
                }
            }
        }

        @Override
        public int getItemCount() {
            return MainActivity.getListOfCombinedItems().size();
        }

//        private void removeItem(Item item, int position) {
//
//            //listOfParentItems.remove(position);
//            //notifyItemRemoved(position);
//        }
//
//        private void restoreItem(Item item, int position) {
//
//            listOfParentItems.add(position, item);
//            listOfItems.add(position, item);
//            notifyItemInserted(position);
//        }

        private int createCategoryRow(MyViewHolder1 holder, int position, int smallHeight, int largeHeight) {
            if (position == -1)
            {
                return smallHeight;
            }
            ItemDrawer id = MainActivity.getListOfCombinedItems().get(position);
            int height = id.isExtended()? largeHeight: smallHeight;

            holder.mainLayout.setBackgroundColor(id.getBackground());
            holder.categorySumsLayout.setVisibility(id.isExtended()? View.VISIBLE: View.GONE);
            //    holder.separator.setVisibility(id.isExtended()? View.VISIBLE: View.GONE);
           return height;
        }

        private int createItemRow(MyViewHolder2 holder, int position, int smallHeight, int largeHeight) {
            if (position == -1)
            {
                return smallHeight;
            }
            ItemDrawer id = MainActivity.getListOfCombinedItems().get(position);
            int height = id.isExtended()? largeHeight: smallHeight;

            holder.mainLayout.setBackgroundColor(id.getBackground());
            holder.descriptionLayout.setVisibility(id.isExtended()? View.VISIBLE: View.GONE);
            return height;
        }

    }

    private class MyTextWatcher implements TextWatcher {
        private RecyclerViewAdapter.MyViewHolder2 holder2;
        private int logicalDensity, subCategoryWidth;
        public MyTextWatcher(RecyclerViewAdapter.MyViewHolder2 holder, int logicalDensity, int subCategoryWidth) {
            this.subCategoryWidth = subCategoryWidth;
            this.logicalDensity = logicalDensity;
            holder2 = holder;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().equals("") || count != before)
            {
                RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(MainActivity.getScreenWidth() - (120*logicalDensity), LinearLayout.LayoutParams.WRAP_CONTENT);
                rParams.setMarginStart(20 * logicalDensity);
                holder2.descriptionEdit.setLayoutParams(rParams);
                holder2.okButton.setVisibility(View.VISIBLE);
                holder2.cancelButton.setVisibility(View.VISIBLE);
            }
            else
            {
                RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(subCategoryWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
                rParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                rParams.setMarginStart(20 * logicalDensity);
                holder2.descriptionEdit.setLayoutParams(rParams);
                holder2.okButton.setVisibility(View.GONE);
                holder2.cancelButton.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}