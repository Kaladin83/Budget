package com.example.maratbe.budget.interfaces;

/**
 * Interface that allows other classes to update CategoryPicker class gui
 */

public interface OnUpdateCategoryPicker {

    void updateColorField(int color);

    void restoreColorOption(Integer deleteColor, int deletedIndex);

    void switchTab();
}
