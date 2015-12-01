package com.closestudios.bro.util;

import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by closestudios on 11/24/15.
 */
public class TabGroup implements View.OnClickListener {
    FrameLayout flBack;
    View vBottom;
    TabGroupSelectedListener listener;

    public interface TabGroupSelectedListener {
        void onTabSelected(TabGroup tabGroup);
    }

    public TabGroup(FrameLayout flBack, View vBottom) {
        this.flBack = flBack;
        this.vBottom = vBottom;
        this.flBack.setOnClickListener(this);
    }

    public void setListener(TabGroupSelectedListener listener) {
        this.listener = listener;
    }

    public void setActive(boolean active) {
        vBottom.setVisibility(active ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == flBack.getId()) {
            if(listener != null) {
                listener.onTabSelected(this);
            }
        }
    }
}
