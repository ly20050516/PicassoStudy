package com.ly.justsoso.enjoypictures.ui;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by LY on 6/9/2016.
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration{

    int space;
    public SpacesItemDecoration(int space){
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;
        if(parent.getChildAdapterPosition(view) == 0){
            outRect.top = space;
        }
    }
}
