package com.example.maratbe.budget;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maratbe.budget.Objects.Category;
import com.example.maratbe.budget.interfaces.Constants;
import com.example.maratbe.budget.interfaces.OnUpdateCategoryPicker;
import com.example.maratbe.budget.interfaces.OnUpdateMainActivity;

/**
 * Class that gives to user the tools to add new category, and manage them.
 */
public class CategoryPicker extends Fragment implements
        View.OnClickListener, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, View.OnTouchListener, OnUpdateCategoryPicker, Constants {
    int entireHeight, catRecyclerWidth, tabsWidth, addCatLayoutHeight, addCatLayoutWidth, editCatWidth, addCatButtonWidth, compAddCatHeight, entireHeaderHeight,
            colorWidth, arrowWidth, betweenLayoutsMargin, updateLayoutWidth, betweenColorsMargin, bigLayoutMarginWidth;
    private int selectedRecyclePosition = -1, newSelectedColor = 0;
    private View mainView;
    private static OnUpdateMainActivity listener;
    private FavouriteColors favoriteColors;
    private EditText addCategoryEdit;
    private CategoryRecyclerViewAdapter categoryAdapter;
    private ViewPager pager;
    private TextView newColorTxt, currentColorTxt;
    private LinearLayout mainLayout;
    private LinearLayout.LayoutParams lParams;
    private CategoryPicker categoryPicker;
    private ManageCategoryDialog manageCategoryDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.category_picker, container, false);

        categoryPicker = new CategoryPicker();
        categoryPicker = this;

        defineDimensions();
        assignFields();
        populateCategories();

        return mainView;
    }

    public static void setListener(MainRecycler activity)
    {
        listener = activity;
    }


    private void defineDimensions() {
        editCatWidth = MainActivity.getScreenWidth() / 2;
        entireHeight = (int)(MainActivity.getScreenHeight() / 2.2);
        catRecyclerWidth = (int)(MainActivity.getScreenWidth() * 0.41);
        tabsWidth = (int)(MainActivity.getScreenWidth() * 0.52);
        addCatLayoutHeight = (MainActivity.getScreenHeight() - entireHeight)/5;
        betweenLayoutsMargin = addCatLayoutHeight / 2;

        addCatLayoutWidth = MainActivity.getScreenWidth() - 30;
        addCatButtonWidth = MainActivity.getScreenWidth() / 3;
        compAddCatHeight = MainActivity.getScreenHeight() / 16;
        entireHeaderHeight = MainActivity.getScreenHeight() / 16;
        colorWidth = MainActivity.getScreenWidth() / 8;
        arrowWidth = colorWidth / 2;
        updateLayoutWidth = (int)(addCatLayoutWidth * 0.8);
        betweenColorsMargin = (updateLayoutWidth - (2*colorWidth + 2*arrowWidth)) / 10;
        bigLayoutMarginWidth = (MainActivity.getScreenWidth() - addCatLayoutWidth) / 2;
    }

    private void populateCategories() {

    }

    private void assignFields() {
        mainLayout = mainView.findViewById(R.id.main_layout);
        mainLayout.setOnTouchListener(this);

        lParams = new LinearLayout.LayoutParams(addCatLayoutWidth, addCatLayoutHeight);
        lParams.setMargins(bigLayoutMarginWidth,betweenLayoutsMargin,0,betweenLayoutsMargin);
        LinearLayout addCategoryLayout = mainView.findViewById(R.id.add_category_layout);
        addCategoryLayout.setLayoutParams(lParams);
        addCategoryLayout.setBackground(Utils.createBorder(10, Color.WHITE, 1));
        lParams = new LinearLayout.LayoutParams(addCatLayoutWidth, entireHeight);
        lParams.setMargins((int)(bigLayoutMarginWidth*1.5), 0, 0, 0);
        LinearLayout entireLayout = mainView.findViewById(R.id.combo_layout);
        entireLayout.setLayoutParams(lParams);
        entireLayout.setOnTouchListener(this);
        lParams = new LinearLayout.LayoutParams(catRecyclerWidth, entireHeight - 5) ;
        lParams.setMargins(2, 0, 0, 0);
        LinearLayout recyclerLayout = mainView.findViewById(R.id.recycle_layout) ;
        recyclerLayout.setBackground(Utils.createBorder(10, Color.TRANSPARENT, 1));
        recyclerLayout.setLayoutParams(lParams);
        lParams = new LinearLayout.LayoutParams(tabsWidth, entireHeight - 5) ;
        lParams.setMargins(bigLayoutMarginWidth+5, 0, 0, 0);
        LinearLayout tabsLayout = mainView.findViewById(R.id.tabs_layout) ;
        tabsLayout.setLayoutParams(lParams);
        tabsLayout.setBackground(Utils.createBorder(10, Color.TRANSPARENT, 1));
        lParams = new LinearLayout.LayoutParams(updateLayoutWidth, addCatLayoutHeight) ;
        lParams.setMargins((MainActivity.getScreenWidth() - updateLayoutWidth)/2, betweenLayoutsMargin/2, 0, 0);
        LinearLayout updateLayout = mainView.findViewById(R.id.update_layout) ;
        updateLayout.setLayoutParams(lParams);
        updateLayout.setBackground(Utils.createBorder(10, Color.TRANSPARENT, 1));

        lParams = new LinearLayout.LayoutParams(addCatButtonWidth, compAddCatHeight) ;
        lParams.gravity = Gravity.CENTER_VERTICAL;
        lParams.setMargins(bigLayoutMarginWidth+5, 0, 0, 0);
        Button addCategoryBtn = mainView.findViewById(R.id.add_category_btn);
        addCategoryBtn.setBackground(Utils.createBorder(10, ContextCompat.getColor(getContext(), R.color.toolBar), 1));
        addCategoryBtn.setLayoutParams(lParams);
        addCategoryBtn.setOnClickListener(this);
        lParams = new LinearLayout.LayoutParams((int)(colorWidth*1.5), compAddCatHeight) ;
        lParams.gravity = Gravity.CENTER_VERTICAL;
        lParams.setMargins(betweenColorsMargin*3, 0, 0, 0);
        Button updateBtn = mainView.findViewById(R.id.update_btn);
        updateBtn.setLayoutParams(lParams);
        updateBtn.setBackground(Utils.createBorder(10, ContextCompat.getColor(getContext(), R.color.toolBar), 1));
        updateBtn.setOnClickListener(this);

        lParams = new LinearLayout.LayoutParams(editCatWidth, compAddCatHeight) ;
        lParams.gravity = Gravity.CENTER_VERTICAL;
        lParams.setMargins(bigLayoutMarginWidth*4, 0, 0, 0);
        addCategoryEdit = mainView.findViewById(R.id.category_edit);
        addCategoryEdit.setLayoutParams(lParams);
        addCategoryEdit.setBackground(Utils.createBorder(10, Color.WHITE, 1));
        addCategoryEdit.clearFocus();

        lParams = new LinearLayout.LayoutParams(catRecyclerWidth, entireHeaderHeight) ;
        TextView categoryHeaderTxt = mainView.findViewById(R.id.category_header_txt);
        categoryHeaderTxt.setLayoutParams(lParams);
        lParams = new LinearLayout.LayoutParams(colorWidth, compAddCatHeight) ;
        lParams.gravity = Gravity.CENTER_VERTICAL;
        lParams.setMargins(betweenColorsMargin, 0, 0, 0);
        newColorTxt = mainView.findViewById(R.id.new_color_txt) ;
        newColorTxt.setLayoutParams(lParams);
        newColorTxt.setBackground(Utils.createBorder(15, Color.TRANSPARENT, 1));
        currentColorTxt = mainView.findViewById(R.id.current_color_txt) ;
        currentColorTxt.setLayoutParams(lParams);
        currentColorTxt.setBackground(Utils.createBorder(15, Color.TRANSPARENT, 1));
        lParams = new LinearLayout.LayoutParams(arrowWidth,  (int)(compAddCatHeight*0.7)) ;
        lParams.gravity = Gravity.CENTER_VERTICAL;
        lParams.setMargins(betweenColorsMargin, 0, 0, 0);
        TextView arrowTxt = mainView.findViewById(R.id.arrow_txt) ;
        arrowTxt.setLayoutParams(lParams);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        lParams = new LinearLayout.LayoutParams(catRecyclerWidth - 5, entireHeight - entireHeaderHeight - 8) ;
        lParams.setMargins(1, 0, 0, 0);
        categoryAdapter = new CategoryRecyclerViewAdapter();
        RecyclerView categoryRecyclerView = mainView.findViewById(R.id.category_recycler_view);
        categoryRecyclerView.setLayoutParams(lParams);
        categoryRecyclerView.setAdapter(categoryAdapter);
        categoryRecyclerView.setLayoutManager(layoutManager);
        categoryRecyclerView.addItemDecoration(new DividerItemDecoration(categoryRecyclerView.getContext(), layoutManager.getOrientation()));

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(categoryRecyclerView);

        createTabs();
        showCategories();
    }

    private void createTabs() {
        lParams = new LinearLayout.LayoutParams(tabsWidth - 5, entireHeaderHeight);
        lParams.setMargins(2,2,0,0);
        TabLayout tabLayout = mainView.findViewById(R.id.tab_layout);
        tabLayout.setLayoutParams(lParams);
        tabLayout.addTab(tabLayout.newTab().setText("Color Picker"));
        tabLayout.addTab(tabLayout.newTab().setText("Favourites"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setSelectedTabIndicatorColor(BLUE_1);
        tabLayout.setTabTextColors(BLACK_2, Color.BLACK);

        lParams = new LinearLayout.LayoutParams(tabsWidth - 5, entireHeight - 130);
        lParams.setMargins(2,2,0,2);
        pager = mainView.findViewById(R.id.pager);
        pager.setLayoutParams(lParams);
        final PagerAdapter adapter = new PagerAdapter
                (getChildFragmentManager(), tabLayout.getTabCount());
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void showCategories() {
        categoryAdapter.notifyDataSetChanged();
    }

    private void addCategory() {
        if (addCategoryEdit.getText().length() == 0)
        {
            Toast.makeText(getContext(), "The category name must not be blank", Toast.LENGTH_LONG).show();
            return;
        }

        for (Category c: MainActivity.getListOfCategories()) {
            if (c.getName().equals(addCategoryEdit.getText().toString()))
            {
                Toast.makeText(getContext(), "The category already in the list", Toast.LENGTH_LONG).show();
                return;
            }
        }
        manageCategoryDialog = new ManageCategoryDialog(getActivity(), (int) (MainActivity.getScreenWidth() / 1.3), (int)(MainActivity.getScreenHeight() / 2.5), "subCategory")  {
            @Override
            public void okButtonPressed(String categoryName) {
                int color = Utils.findColor(categoryName);
                Category category = new Category(addCategoryEdit.getText().toString(), categoryName, color);

                MainActivity.getDbInstance().insertCategory(category);
                addCategoryEdit.setText("");
                Utils.closeKeyboard(addCategoryEdit, categoryPicker.getActivity());
                showCategories();
                Toast.makeText(categoryPicker.getContext(), "The category: "+ category.getName()+" was added", Toast.LENGTH_SHORT).show();
                manageCategoryDialog.dismiss();
            }

            @Override
            public void cancelButtonPressed() {
                manageCategoryDialog.dismiss();
            }
        };
        manageCategoryDialog.show();
    }

    private void updateCategoryColor() {
        if (selectedRecyclePosition == -1)
        {
            Toast.makeText(getContext(), "A category must be selected", Toast.LENGTH_LONG).show();
            return;
        }

        Category category = MainActivity.getListOfCombinedCategories().get(selectedRecyclePosition);
        category.setColor(newSelectedColor);
        currentColorTxt.setBackgroundColor(newSelectedColor);
        MainActivity.getDbInstance().updateCategoryColor(category);
        showCategories();
        listener.refreshItemRecyclerView();
        Toast.makeText(getContext(), "The color of "+ category.getName()+" was changed", Toast.LENGTH_SHORT).show();
    }

    private void updateCategoryName(String oldName, int position, String newName) {
        int newColor = Utils.findColor(newName);
        Category category = MainActivity.getListOfCategories().get(position);
        category.setColor(newColor);
        category.setName(newName);
        MainActivity.getDbInstance().updateItemCategory(newName, oldName);
        MainActivity.getDbInstance().deleteCategory(category);
        MainActivity.getDbInstance().fetchAllCategories();
        MainActivity.getDbInstance().fetchAllItems(MainActivity.getCurrentPayDate());
        MainActivity.getDbInstance().fetchMonthlyStatistics(MainActivity.getCurrentPayDate());
       /* DbHandler.removeCategory(MainActivity.getDbInstance(), category);
        DbHandler.fetchListOfCategories(MainActivity.getDbInstance());
        DbHandler.fetchListOfItems(MainActivity.getDbInstance(), MainActivity.getCurrentPayDate());*/
        showCategories();
        listener.refreshItemRecyclerView();
        Toast.makeText(getContext(), oldName+" was renamed to "+newName, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.add_category_btn:
                addCategory();
                break;
            case R.id.update_btn:
                updateCategoryColor();
                break;
        }

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CategoryPicker.CategoryRecyclerViewAdapter.MyViewHolder) {
            final Category deletedItem = MainActivity.getListOfCombinedCategories().get(position);
            final int deletedIndex = position;

            if (MainActivity.getDbInstance().findItemsByCategory(deletedItem.getName()) == 1)
            {
                manageCategoryDialog = new ManageCategoryDialog(getActivity(), (int) (MainActivity.getScreenWidth() / 1.3), (int)(MainActivity.getScreenHeight() / 2.5), "delete") {
                    @Override
                    public void okButtonPressed(String categoryName) {
                        updateCategoryName(deletedItem.getName(), deletedIndex, categoryName);
                        manageCategoryDialog.dismiss();
                    }

                    @Override
                    public void cancelButtonPressed() {
                        showCategories();
                        manageCategoryDialog.dismiss();
                    }
                };
                manageCategoryDialog.show();
            }
            else
            {
                categoryAdapter.removeItem(position);
                restoreOption(null, deletedItem, deletedIndex, "The category was removed from list");
                MainActivity.getDbInstance().deleteCategory(deletedItem);
                showCategories();
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Utils.closeKeyboard(addCategoryEdit, getActivity());
        return false;
    }

    @Override
    public void updateColorField(int color) {
        newSelectedColor = color;
        newColorTxt.setBackgroundColor(color);
    }

    @Override
    public void restoreColorOption(Integer deleteColor_, int deletedIndex_) {
        restoreOption(deleteColor_, null, deletedIndex_, "The color removed from list");
    }

    @Override
    public void switchTab() {
        pager.setCurrentItem(1);
    }

    public void restoreCategory(Category deleteCategory, int deletedIndex)
    {
        categoryAdapter.restoreItem(deleteCategory, deletedIndex);
        //DbHandler.populateCategory(MainActivity.getDbInstance(), deleteCategory);
        MainActivity.getDbInstance().insertCategory(deleteCategory);
        showCategories();
    }

    private void restoreOption(Integer deleteColor_, Category deleteCategory_, int deletedIndex_, String message) {
        final Integer deleteColor = deleteColor_;
        final int deletedIndex = deletedIndex_;
        final Category deleteCategory = deleteCategory_;

        Snackbar snackbar = Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", view -> {
            if (deleteColor != null)
            {
                favoriteColors.restoreDeletedColor(deleteColor, deletedIndex);
                //pager.setCurrentItem(1);
                //TabLayout.Tab tab = tabLayout.getTabAt(1);
                // tab.select();
            }
            else
            {
                restoreCategory(deleteCategory, deletedIndex);
            }
        });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        private PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            ColorPicker colorPicker;
            switch (position) {
                case 0:
                    colorPicker = new ColorPicker();
                    ColorPicker.setCategoryPickerListener(categoryPicker);
                    return colorPicker;
                case 1:
                    favoriteColors = new FavouriteColors();
                    ColorPicker.setFavouriteColorsListener(favoriteColors);
                    FavouriteColors.setCategoryPickerListener(categoryPicker);
                    return favoriteColors;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }

    class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.MyViewHolder> {

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public RelativeLayout mainLayout;
            private TextView colorTxt, colorSubTxt;
            private TextView categoryTxt;

            public MyViewHolder(View v) {
                super(v);
                mainLayout = v.findViewById(R.id.main_category_list_layout);
                colorTxt = v.findViewById(R.id.color_category_txt);
                colorSubTxt = v.findViewById(R.id.color_category_sub_txt);
                categoryTxt = v.findViewById(R.id.text_txt);
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.category_list, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position_) {
            final int position = position_;
            holder.mainLayout.setId(position);
            Category category = MainActivity.getListOfCombinedCategories().get(position);
            int color = category.getColor();
            holder.categoryTxt.setText(category.getName());

            int width = category.getParent().equals("")? (int) Math.ceil(30 * MainActivity.getLogicalDensity()): (int) Math.ceil(14 * MainActivity.getLogicalDensity());

            LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(width, (int) Math.ceil(20 * MainActivity.getLogicalDensity()));
            lParams.setMarginEnd(6);
            holder.colorTxt.setLayoutParams(lParams);
            holder.colorTxt.setBackground(Utils.createBorder(15, category.getParent().equals("")? color :Utils.findColor(category.getParent()), 1));
            lParams = new LinearLayout.LayoutParams(width, (int) Math.ceil(20 * MainActivity.getLogicalDensity()));
            holder.colorSubTxt.setVisibility(category.getParent().equals("")? View.GONE: View.VISIBLE );
            holder.colorSubTxt.setLayoutParams(lParams);
            holder.colorSubTxt.setBackground(Utils.createBorder(15, color, 1));


            if(selectedRecyclePosition == position)
            {
                holder.mainLayout.setBackgroundColor(GRAY_2);
            }
            else
            {
                holder.mainLayout.setBackgroundColor(Color.WHITE);
            }

            holder.mainLayout.setClickable(true);
            holder.mainLayout.setOnClickListener( view -> {
                String cat = ((TextView)((RelativeLayout)view).getChildAt(0)).getText().toString();
                int color_ = Utils.findColor(cat);
                currentColorTxt.setBackgroundColor(color_);
                selectedRecyclePosition = position;
                notifyDataSetChanged();
            });
        }

        @Override
        public int getItemCount() {
            return MainActivity.getListOfCombinedCategories().size();
        }

        public void removeItem(int position) {
            int categoryIndex = Utils.findCategoryIndex(MainActivity.getListOfCombinedCategories().get(position).getName(), MainActivity.getListOfCategories());
            if (categoryIndex > -1)
            {
                MainActivity.getListOfCategories().remove(categoryIndex);
            }
            else
            {
                categoryIndex = Utils.findCategoryIndex(MainActivity.getListOfCombinedCategories().get(position).getName(), MainActivity.getListOfSubCategories());
                MainActivity.getListOfSubCategories().remove(categoryIndex);
            }

            MainActivity.getListOfCombinedCategories().remove(position);
        }

        public void restoreItem(Category category, int position) {

            MainActivity.getListOfCombinedCategories().add(position, category);

            int categoryIndex = Utils.findCategoryIndex(category.getName(), MainActivity.getListOfCategories());
            if (categoryIndex == -1)
            {
                MainActivity.getListOfCategories().add(categoryIndex, category);
            }
            else
            {
                categoryIndex = Utils.findCategoryIndex(category.getName(), MainActivity.getListOfSubCategories());
                MainActivity.getListOfSubCategories().add(categoryIndex, category);
            }

            notifyItemInserted(position);
        }
    }
}

