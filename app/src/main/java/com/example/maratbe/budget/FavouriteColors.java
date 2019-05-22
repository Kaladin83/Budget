package com.example.maratbe.budget;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.maratbe.budget.interfaces.OnUpdateCategoryPicker;
import com.example.maratbe.budget.interfaces.OnUpdateFavouriteColor;

/**
 * Displays a list of favourite colors.
 * The user can choose a color, add a color from color picker or remove an existing color.
 */

public class FavouriteColors extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, View.OnClickListener, OnUpdateFavouriteColor {
    private static OnUpdateCategoryPicker onUpdateCategoryPicker;
    private ColorRecyclerViewAdapter colorAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView =  inflater.inflate(R.layout.favourite_colors, container, false);
        //listener = (CategoryPickerInt)this.getActivity();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
       // coordinatorLayout = mainView.findViewById(R.id.coordinator_layout);
        colorAdapter = new ColorRecyclerViewAdapter();
        RecyclerView colorRecyclerView = mainView.findViewById(R.id.colors_recycler_view);
        colorRecyclerView.setAdapter(colorAdapter);
        colorRecyclerView.setLayoutManager(layoutManager);
        colorRecyclerView.setClickable(true);
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(colorRecyclerView);
        return mainView;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof ColorRecyclerViewAdapter.MyViewHolder) {
           // final Colors deletedItem = MainActivity.getListOfColors().get(position);
            final Integer deletedItem = MainActivity.getListOfColors().get(position);

            colorAdapter.removeItem(position);
            onUpdateCategoryPicker.restoreColorOption(deletedItem, position);
         //   colorAdapter.notifyDataSetChanged();
            //DbHandler.removeColor(MainActivity.getDbInstance(), deletedItem);
            MainActivity.getDbInstance().deleteColor(deletedItem);
            showData();
        }
    }

    private void showData() {
        colorAdapter.notifyDataSetChanged();
    }

    public void restoreDeletedColor(Integer deletedItem, int deletedIndex){
        colorAdapter.restoreItem(deletedItem, deletedIndex);
       // DbHandler.populateColor(MainActivity.getDbInstance(), deletedItem);
        MainActivity.getDbInstance().insertColor(deletedItem);
        showData();
    }

    public static void setCategoryPickerListener(CategoryPicker categoryPicker) {
        onUpdateCategoryPicker = categoryPicker;
    }

    @Override
    public void refreshColorRecyclerView() {
        showData();
    }

    class ColorRecyclerViewAdapter extends RecyclerView.Adapter<ColorRecyclerViewAdapter.MyViewHolder> {
        private int selectedPosition = -1;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public FrameLayout objectLayout;
            public RelativeLayout mainLayout;
            public RelativeLayout backgroundLayout;
            private TextView colorTxt;

            public MyViewHolder(View v) {
                super(v);
                objectLayout = v.findViewById(R.id.color_list_layout);
                mainLayout = v.findViewById(R.id.main_color_layout);
                backgroundLayout = v.findViewById(R.id.background_layout);
                colorTxt = v.findViewById(R.id.color_txt);
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.color_list, parent, false);

            return new MyViewHolder(v);

        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position_) {
            final int position = position_;

            int color = MainActivity.getListOfColors().get(position);
            holder.colorTxt.setBackgroundColor(color);
            if(selectedPosition == position)
            {
                holder.mainLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
            }
            else
            {
                holder.mainLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
            }
            holder.mainLayout.setId(position);
            holder.mainLayout.setClickable(true);
            holder.mainLayout.setOnClickListener(v ->  {
                selectedPosition = position;
                //View view = ((RelativeLayout)v).getChildAt(0);
                int color_ = MainActivity.getListOfColors().get(position);
                onUpdateCategoryPicker.updateColorField(color_);
                //notifyDataSetChanged();
            });
        }

        @Override
        public int getItemCount() {
            return MainActivity.getListOfColors().size();
        }

        public void removeItem(int position) {
            MainActivity.getListOfColors().remove(position);
        }

        public void restoreItem(Integer color, int position) {

            MainActivity.getListOfColors().add(position, color);
            notifyItemInserted(position);
        }
    }
}
