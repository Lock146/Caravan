package com.example.caravan.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caravan.Adapter.GroupListAdapter;
import com.example.caravan.R;

public class GroupListActivity extends AppCompatActivity
{


    private RecyclerView recyclerView;
    private GroupListAdapter groupListAdapter;

     @Override
    protected void onCreate(Bundle savedInstanceState){
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_grouplist);

         recyclerView = findViewById(R.id.recyclerView2);

         groupListAdapter = new GroupListAdapter();

         recyclerView.setLayoutManager(new LinearLayoutManager(this));

         recyclerView.setAdapter(groupListAdapter);


     }



}
