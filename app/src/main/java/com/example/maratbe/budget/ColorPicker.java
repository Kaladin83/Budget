package com.example.maratbe.budget;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.maratbe.budget.interfaces.OnUpdateCategoryPicker;
import com.example.maratbe.budget.interfaces.OnUpdateFavouriteColor;

/**
 * Class that allows the user to create its own color
 */

public class ColorPicker extends Fragment implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    private static OnUpdateCategoryPicker updateCategoryPickerListener;
    private static OnUpdateFavouriteColor updateColorPickerListener;
    private int red = 0, green = 0, blue = 0, selectedColor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.color_picker, container, false);

        //updateCategoryPickerListener = (MainActivity)this.getActivity();
        SeekBar redSeekBar = mainView.findViewById(R.id.red_bar);
        SeekBar greenSeekBar = mainView.findViewById(R.id.green_bar);
        SeekBar blueSeekBar = mainView.findViewById(R.id.blue_bar);
        redSeekBar.setOnSeekBarChangeListener(this);
        greenSeekBar.setOnSeekBarChangeListener(this);
        blueSeekBar.setOnSeekBarChangeListener(this);

        Button updateColorBtn = mainView.findViewById(R.id.update_color_btn);
        updateColorBtn.setOnClickListener(this);
        updateColorBtn.setBackground(Utils.createBorder(10, ContextCompat.getColor(getContext(), R.color.toolBar), 1));

        return mainView;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId())
        {
            case R.id.red_bar:
                red = i; break;
            case R.id.green_bar:
                green = i; break;
            default:
                blue = i;
        }
        selectedColor = Color.rgb(red, green, blue);
        updateCategoryPickerListener.updateColorField(selectedColor);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.update_color_btn)
        {
            for (Integer c: MainActivity.getListOfColors()) {
                if (c == selectedColor)
                {
                    Toast.makeText(getActivity(), "A color already in the favourites list", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            MainActivity.getListOfColors().add(selectedColor);
           // DbHandler.populateColor(MainActivity.getDbInstance(), color);
            MainActivity.getDbInstance().insertColor(selectedColor);
            updateColorPickerListener.refreshColorRecyclerView();
            updateCategoryPickerListener.switchTab();
            Toast.makeText(this.getContext(), "The color was added", Toast.LENGTH_SHORT).show();
        }
    }

    public static void setFavouriteColorsListener(FavouriteColors activity)
    {
        updateColorPickerListener = activity;
    }

    public static void setCategoryPickerListener(CategoryPicker activity) {
        updateCategoryPickerListener = activity;
    }
}
