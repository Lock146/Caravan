package com.example.caravan.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caravan.GooglePlaceModel;
import com.example.caravan.R;
import com.example.caravan.StopInfo;

import java.util.ArrayList;
import java.util.List;

public class suggestedStopsAdapter extends RecyclerView.Adapter<suggestedStopsAdapter.ViewHolder>{
    private static final String TAG = "RecyclerAdapter";

    double MILES = 1609.344;
    double route_miles;
    ArrayList<StopInfo> m_suggestions;
    public suggestedStopsAdapter(ArrayList<GooglePlaceModel> suggestions) {
        for(GooglePlaceModel suggestion : suggestions){
            m_suggestions.add(new StopInfo(suggestion, 0));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.suggested_item_layout,parent,false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        route_miles = m_suggestions.get(position).distance()/MILES;
        holder.rowCountTextView.setText(String.valueOf(route_miles));
        holder.textView.setText(m_suggestions.get(position).name());
    }

    @Override
    public int getItemCount() {
        return m_suggestions.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView textView, rowCountTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            rowCountTextView = itemView.findViewById(R.id.rowCountTextView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //Routes.remove(getAdapterPosition());
                    //notifyItemRemoved(getAdapterPosition());
                    return true;
                }
            });
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), m_suggestions.get(getAdapterPosition()).name(), Toast.LENGTH_SHORT).show();
        }
    }
}