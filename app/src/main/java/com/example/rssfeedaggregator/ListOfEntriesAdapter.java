package com.example.rssfeedaggregator;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rssfeedaggregator.network.Entry;

import java.util.List;

public class ListOfEntriesAdapter extends RecyclerView.Adapter<ListOfEntriesAdapter.ViewHolder>{
    private static String TAG = ListOfEntriesAdapter.class.getCanonicalName();
    private final List<Entry> mItems;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ViewHolder(TextView v) {
            super(v);
            mTextView = v;
        }
    }

    ListOfEntriesAdapter(List<Entry> dataSet) {
        mItems = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListOfEntriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.entry_text_view, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mItems.get(position).toString());

        if ((position % 2) == 0) {
            holder.mTextView.setBackgroundResource(R.color.yellow_gold);
        } else {
            holder.mTextView.setBackgroundResource(R.color.yellow_green);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount Size : "+ mItems.size());
        return mItems.size();
    }
}
