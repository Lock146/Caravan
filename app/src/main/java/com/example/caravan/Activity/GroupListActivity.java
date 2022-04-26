package com.example.caravan.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caravan.Adapter.GroupListAdapter;
import com.example.caravan.R;

import java.util.ArrayList;
import java.util.List;

public class GroupListActivity extends AppCompatActivity
{


    private RecyclerView recyclerView;
    private GroupListAdapter groupListAdapter;

    private List<String> groupMember;

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


         groupMember.add("Cameron");
         groupMember.add("Luke");
         groupMember.add("Colby");
         groupMember.add("Kyler");
         groupMember.add("Cameron");
         groupMember.add("Luke");
         groupMember.add("Colby");
         groupMember.add("Kyler");

         groupMember.add("Cameron");
         groupMember.add("Luke");
         groupMember.add("Colby");
         groupMember.add("Kyler");


     }



}
