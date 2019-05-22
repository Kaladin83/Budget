package com.example.maratbe.budget;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

/**
 * Class that responsible of swiping the item off
 */
public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {
    private RecyclerItemTouchHelperListener listener;

    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs, RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final View foregroundView;
        if (viewHolder instanceof MainRecycler.RecyclerViewAdapter.MyViewHolder1)
        {
            foregroundView = ((MainRecycler.RecyclerViewAdapter.MyViewHolder1) viewHolder).mainLayout;
        }
        else if (viewHolder instanceof MainRecycler.RecyclerViewAdapter.MyViewHolder2)
        {
            foregroundView = ((MainRecycler.RecyclerViewAdapter.MyViewHolder2) viewHolder).mainLayout;
        }
        else if (viewHolder instanceof  FavouriteColors.ColorRecyclerViewAdapter.MyViewHolder)
        {
            foregroundView = ((FavouriteColors.ColorRecyclerViewAdapter.MyViewHolder)viewHolder).mainLayout;
        }
        else
        {
            foregroundView = ((CategoryPicker.CategoryRecyclerViewAdapter.MyViewHolder)viewHolder).mainLayout;
        }
        getDefaultUIUtil().clearView(foregroundView);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        final View foregroundView;
        if (viewHolder instanceof MainRecycler.RecyclerViewAdapter.MyViewHolder1)
        {
            foregroundView = ((MainRecycler.RecyclerViewAdapter.MyViewHolder1) viewHolder).mainLayout;
        }
        else if (viewHolder instanceof MainRecycler.RecyclerViewAdapter.MyViewHolder2)
        {
            foregroundView = ((MainRecycler.RecyclerViewAdapter.MyViewHolder2) viewHolder).mainLayout;
        }
        else if (viewHolder instanceof  FavouriteColors.ColorRecyclerViewAdapter.MyViewHolder)
        {
            foregroundView = ((FavouriteColors.ColorRecyclerViewAdapter.MyViewHolder)viewHolder).mainLayout;
        }
        else
        {
            foregroundView = ((CategoryPicker.CategoryRecyclerViewAdapter.MyViewHolder)viewHolder).mainLayout;
        }

        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    public interface RecyclerItemTouchHelperListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }
}
