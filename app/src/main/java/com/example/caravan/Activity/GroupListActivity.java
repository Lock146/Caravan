package com.example.caravan.Activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caravan.Adapter.GroupListAdapter;
import com.example.caravan.MemberInfo;
import com.example.caravan.R;
import com.google.maps.android.quadtree.PointQuadTree;

import java.util.ArrayList;
import java.util.List;

public class GroupListActivity extends AppCompatActivity
{


    private RecyclerView recyclerView;
    private GroupListAdapter groupListAdapter;


    private List<MemberInfo> groupMember;

     @Override
    protected void onCreate(Bundle savedInstanceState){
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_grouplist);

         groupMember = new ArrayList<>();

         recyclerView = findViewById(R.id.recyclerView2);
         groupListAdapter = new GroupListAdapter(groupMember);
         recyclerView.setLayoutManager(new LinearLayoutManager(this));
         recyclerView.setAdapter(groupListAdapter);

         DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
         recyclerView.addItemDecoration(dividerItemDecoration);



         groupMember.add(new MemberInfo("Cameron",R.mipmap.ic_unfathomable_round));
         groupMember.add(new MemberInfo("Cameron",R.mipmap.ic_fish_avatar_round));
         groupMember.add(new MemberInfo("Cameron",R.mipmap.ic_unfathomable_round));
         groupMember.add(new MemberInfo("Cameron",R.mipmap.ic_unfathomable_round));
         groupMember.add(new MemberInfo("Cameron",R.mipmap.ic_unfathomable_round));
         groupMember.add(new MemberInfo("Cameron",R.mipmap.ic_unfathomable_round));
         groupMember.add(new MemberInfo("Cameron",R.mipmap.ic_unfathomable_round));
         groupMember.add(new MemberInfo("Cameron",R.mipmap.ic_unfathomable_round));
         groupMember.add(new MemberInfo("Cameron",R.mipmap.ic_unfathomable_round));


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
     }


     ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
         @Override
         public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
             return false;
         }

         @Override
         public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
         {
             int position = viewHolder.getAdapterPosition();
             if (direction == ItemTouchHelper.RIGHT) {
                 groupMember.remove(position);
                 groupListAdapter.notifyItemRemoved(position);

            }
         }
     };


}
