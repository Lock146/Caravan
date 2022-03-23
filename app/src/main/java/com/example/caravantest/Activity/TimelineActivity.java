package com.example.caravantest.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caravantest.Adapter.TimelineAdapter;
import com.example.caravantest.R;

import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TimelineAdapter timelineAdapter;
    List<String> destiList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        destiList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        timelineAdapter = new TimelineAdapter(destiList);

        recyclerView.setAdapter(timelineAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        //TODO: Create a function that takes the name of a destination
        // on a route and input it onto the list
        destiList.add("New Orleans");
        destiList.add("Ruston");
    }
}
