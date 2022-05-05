package com.example.caravan.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caravan.Database;
import com.example.caravan.GooglePlaceModel;
import com.example.caravan.R;
import com.example.caravan.StopInfo;
import com.example.caravan.generated.callback.OnClickListener;

import java.util.ArrayList;
import java.util.List;

public class suggestedStopsAdapter extends RecyclerView.Adapter<suggestedStopsAdapter.ViewHolder>{
    private static final String TAG = "RecyclerAdapter";

    double MILES = 1609.344;
    double route_miles;
    ArrayList<StopInfo> m_suggestions;
    public suggestedStopsAdapter(ArrayList<StopInfo> suggestions) {
        m_suggestions = suggestions;
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
        StopInfo suggestion = m_suggestions.get(position);
        route_miles = suggestion.getDistance()/MILES;
        holder.rowCountTextView.setText(String.valueOf(route_miles));
        holder.textView.setText(suggestion.getName());
        if(Database.get_instance().has_vote_cast(suggestion.getPlaceID())){
            holder.dislike.setClickable(false);
            holder.like.setClickable(false);
            if(Database.get_instance().voted_for(suggestion.getPlaceID())){
               holder.dislike.setBackgroundTintList(ContextCompat.getColorStateList(holder.dislike.getContext(), R.color.midGray));
            }
            else{
                holder.like.setBackgroundTintList(ContextCompat.getColorStateList(holder.dislike.getContext(), R.color.midGray));
            }
        }
        else {
            holder.like.setOnClickListener(trigger -> Database.get_instance().vote_for(suggestion.getPlaceID()));
            holder.dislike.setOnClickListener(trigger -> Database.get_instance().vote_against(suggestion.getPlaceID()));
        }
    }

    @Override
    public int getItemCount() {
        return m_suggestions == null ? 0 : m_suggestions.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView textView, rowCountTextView;
        ImageButton like, dislike;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            rowCountTextView = itemView.findViewById(R.id.rowCountTextView);
            like = itemView.findViewById(R.id.btnLike);
            dislike = itemView.findViewById(R.id.btnDislike);

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
            Toast.makeText(view.getContext(), m_suggestions.get(getAdapterPosition()).getName(), Toast.LENGTH_SHORT).show();
        }
    }
}