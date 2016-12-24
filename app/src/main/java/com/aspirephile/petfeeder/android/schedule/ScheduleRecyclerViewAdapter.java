package com.aspirephile.petfeeder.android.schedule;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aspirephile.petfeeder.R;
import com.aspirephile.petfeeder.android.schedule.ScheduleContent.Item;
import com.aspirephile.petfeeder.android.schedule.ScheduleListFragment.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Item} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ScheduleRecyclerViewAdapter extends RecyclerView.Adapter<ScheduleRecyclerViewAdapter.ViewHolder> {

    private final List<Item> mValues;
    private final OnListFragmentInteractionListener mListener;

    public ScheduleRecyclerViewAdapter(List<Item> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ScheduleRecyclerViewAdapter.ViewHolder holder, int position) {
        Item item = mValues.get(position);
        holder.mItem = item;
        holder.timestampView.setText(item.timestamp);
        holder.titleView.setText(item.title);
        holder.quantityView.setText(item.quantity);

        holder.repeatModeView.setText(item.repeatMode);
        if (item.repeatMode.equals("DAILY")) {
            holder.repeatModeView.setBackgroundColor(Color.LTGRAY );
        } else if (item.repeatMode.equals("WEEKLY")) {
            holder.repeatModeView.setBackgroundColor(Color.DKGRAY);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView timestampView, titleView, quantityView, repeatModeView;
        public Item mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            timestampView = (TextView) view.findViewById(R.id.timestamp);
            titleView = (TextView) view.findViewById(R.id.title);
            quantityView = (TextView) view.findViewById(R.id.quantity);
            repeatModeView = (TextView) view.findViewById(R.id.repeatMode);
        }
    }
}
