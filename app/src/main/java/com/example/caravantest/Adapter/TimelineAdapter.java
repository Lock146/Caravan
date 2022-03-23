package com.example.caravantest.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caravantest.R;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {
    private static final String TAG = "TimelineAdapter";
    int count = 0;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.i(TAG, "onCreateViewHolder: " + count++);


        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.activity_timelinerow,parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //TODO: Change textView to be able to present the name of the destination in the list
        holder.textView.setText(String.valueOf(position));

        //TODO: Change rowCount to be able to present miles to the destination
        holder.rowCount.setText(String.valueOf(position));
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView, rowCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView2);
            textView = itemView.findViewById(R.id.textView2);
            rowCount = itemView.findViewById(R.id.textView3);
        }
    }
}
