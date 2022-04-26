package com.example.caravan.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caravan.R;

import java.util.List;

public class GroupListAdapter  extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {

    List<String> groupMembers;

    public GroupListAdapter(List<String> groupMembers) {
        this.groupMembers = groupMembers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_item, parent, false );
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(groupMembers.get(position));
        holder.blank.setText("");

        holder.imageView.setImageResource(R.mipmap.ic_emoji_13_round);
    }

    @Override
    public int getItemCount() {
        return groupMembers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView textView,blank;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            imageView = itemView.findViewById(R.id.imageView);

            textView = itemView.findViewById(R.id.textView);
            blank = itemView.findViewById(R.id.rowCountTextView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) 
        {
            Toast.makeText(view.getContext(), "clicked", Toast.LENGTH_SHORT).show();
        }
    }
}










