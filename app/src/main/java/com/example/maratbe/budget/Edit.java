package com.example.maratbe.budget;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.maratbe.budget.interfaces.Constants;

/**
 * Class that allows user to add expense by writing, and edit existed expenses
 */

public class Edit  extends Fragment implements View.OnClickListener, Constants {
    private View mainView;
    private int addCategoryLayoutHeight, secondColumnWidth, firstColumnWidth, addItemButtonWidth, verticalMargins, horizontalMargins15, horizontalMargins10;
    private LinearLayout.LayoutParams lParam;
    private Spinner categorySpinner;
    private EditText amountEdit, descriptionEdit;
    private TextView categoryTxt, amountTxt, descriptionTxt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.edit, container, false);

        setDimensions();
        setFields();

        return mainView;
    }

    private void setDimensions() {
        int screenHeight = MainActivity.getScreenHeight() - (int) getResources().getDimension(R.dimen.tool_bar);
        addCategoryLayoutHeight = screenHeight / 3;
        horizontalMargins15 = 15 * MainActivity.getLogicalDensity();
        horizontalMargins10 = 10 * MainActivity.getLogicalDensity();
        firstColumnWidth = 100 * MainActivity.getLogicalDensity();
        addItemButtonWidth = 100* MainActivity.getLogicalDensity();
        secondColumnWidth = MainActivity.getScreenWidth() - firstColumnWidth - horizontalMargins15 * 2 - horizontalMargins10 * 2;
        verticalMargins = addCategoryLayoutHeight / 4 ;
    }

    private void setFields() {
        lParam = new LinearLayout.LayoutParams(MainActivity.getScreenWidth()- horizontalMargins15 * 2, addCategoryLayoutHeight);
        // lParam.setMargins(15 * MainActivity.getLogicalDensity(), verticalMargins, 15 * MainActivity.getLogicalDensity(), verticalMargins);
        lParam.setMargins(horizontalMargins15, verticalMargins, horizontalMargins15, horizontalMargins10);
        LinearLayout addFormLayout = mainView.findViewById(R.id.add_category_form_layout);
        addFormLayout.setLayoutParams(lParam);
        addFormLayout.setBackground(Utils.createBorder(15, Color.TRANSPARENT, 1));

        lParam = new LinearLayout.LayoutParams(firstColumnWidth + secondColumnWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        lParam.setMargins(horizontalMargins10, horizontalMargins10, horizontalMargins10, horizontalMargins10);
        LinearLayout categoryLayout = mainView.findViewById(R.id.category_layout);
        categoryLayout.setLayoutParams(lParam);
        LinearLayout amountLayout = mainView.findViewById(R.id.amount_layout);
        amountLayout.setLayoutParams(lParam);
        LinearLayout descriptionLayout = mainView.findViewById(R.id.description_layout);
        descriptionLayout.setLayoutParams(lParam);

        lParam = new LinearLayout.LayoutParams(firstColumnWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        categoryTxt = mainView.findViewById(R.id.category_txt);
        categoryTxt.setLayoutParams(lParam);
        amountTxt = mainView.findViewById(R.id.amount_txt);
        amountTxt.setLayoutParams(lParam);
        descriptionTxt = mainView.findViewById(R.id.description_txt);
        descriptionTxt.setLayoutParams(lParam);

        lParam = new LinearLayout.LayoutParams(secondColumnWidth, 30* MainActivity.getLogicalDensity());
        categorySpinner = mainView.findViewById(R.id.category_spinner);
        categorySpinner.setLayoutParams(lParam);

        lParam = new LinearLayout.LayoutParams(secondColumnWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        amountEdit = mainView.findViewById(R.id.amount_edit);
        amountEdit.setLayoutParams(lParam);
        amountEdit.setBackground(Utils.createBorder(10, Color.TRANSPARENT, 1));
        descriptionEdit = mainView.findViewById(R.id.description_edit);
        descriptionEdit.setLayoutParams(lParam);
        descriptionEdit.setBackground(Utils.createBorder(10, Color.TRANSPARENT, 1));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
//            case R.id.pie_txt:
//                changeSelection(BLUE_4, View.VISIBLE, View.VISIBLE, Color.WHITE, View.GONE, View.GONE); break;
//            case R.id.bar_txt:
//                changeSelection(Color.WHITE, View.GONE, View.GONE, BLUE_4, View.VISIBLE, View.VISIBLE); break;
//            case R.id.category_radio:
//                changeSelection(true, false);
//            case R.id.subcategory_radio:
//                changeSelection(false, true);break;
        }
    }
}
