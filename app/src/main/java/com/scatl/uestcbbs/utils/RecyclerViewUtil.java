package com.scatl.uestcbbs.utils;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionButton;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/17 10:57
 */
public class RecyclerViewUtil {
    private static final int FAB_VISIBILITY_TOGGLE_DIFF = 100;
    private static int totalScrollInDir;

    //自动隐藏fab
    public static void autoHideFab(RecyclerView recyclerView, final FloatingActionButton button){
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && totalScrollInDir < 0 || dy < 0 && totalScrollInDir > 0) {
                    totalScrollInDir = 0;
                }
                totalScrollInDir += dy;

                if (-totalScrollInDir >= FAB_VISIBILITY_TOGGLE_DIFF && button.isHidden()) {
                    button.show(true);
                } else if (totalScrollInDir >= FAB_VISIBILITY_TOGGLE_DIFF && !button.isHidden()) {
                    button.hide(true);
                }
            }
        });
    }

    public static void autoHideFab(NestedScrollView nestedScrollView, FloatingActionButton button) {
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

            }
        });
    }

}
