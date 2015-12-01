package com.closestudios.bro.util;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.closestudios.bro.R;

import java.util.Random;

/**
 * Created by closestudios on 11/24/15.
 */
public class BroViewAdapter extends RecyclerView.Adapter<BroViewAdapter.ViewHolder> {

    BroViewBase[] mDataset;
    int colorOffset;

    int[] colors = {R.color.light_green, R.color.dark_blue, R.color.brown, R.color.red_orange, R.color.dark_green};


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tvHeader;
        public TextView tvDetails;
        public FrameLayout flBackdrop;
        public ViewHolder(View v) {
            super(v);
            tvHeader = (TextView)v.findViewById(R.id.tvHeader);
            tvDetails = (TextView)v.findViewById(R.id.tvDetails);
            flBackdrop = (FrameLayout)v.findViewById(R.id.flBackdrop);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public BroViewAdapter() {
        colorOffset = new Random().nextInt(20);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BroViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_bro, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.flBackdrop.setBackgroundColor(holder.flBackdrop.getContext().getResources().getColor(colors[position + colorOffset % colors.length]));

        holder.tvHeader.setText(mDataset[position].getHeader());
        holder.tvDetails.setText(mDataset[position].getDetails());

    }

    @Override
    public int getItemCount() {
        if(mDataset == null) {
            return 0;
        }
        return mDataset.length;
    }

    public void setBros(BroViewBase[] bros) {
        mDataset = bros;
        notifyDataSetChanged();
    }

}