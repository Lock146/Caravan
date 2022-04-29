package com.example.caravan.Adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.caravan.Database;
import com.example.caravan.MemberInfo;
import com.example.caravan.R;
//import com.squareup.picasso.Picasso;
import com.example.caravan.databinding.RowItemBinding;

import java.util.List;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {

    //List<String> groupMembers;
    List<MemberInfo> groupMember;

    public GroupListAdapter(List<MemberInfo> groupMember) {
        this.groupMember = groupMember;
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
        holder.setData(groupMember.get(position));
    }

    @Override
    public int getItemCount() {
        return groupMember.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView2;
        TextView textView,blank;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView2 = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            blank = itemView.findViewById(R.id.rowCountTextView);

            itemView.setOnClickListener(this);
        }

        void setData(MemberInfo memberInfo){
            textView.setText(memberInfo.getMemberName());
            Glide.with(itemView).load(memberInfo.getProfilePicture()).into(imageView2);
            blank.setText("");
        }

        @Override
        public void onClick(View view) 
        {
            Toast.makeText(view.getContext(), "clicked", Toast.LENGTH_SHORT).show();
        }
    }
}










