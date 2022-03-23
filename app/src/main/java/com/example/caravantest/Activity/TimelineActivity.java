package com.example.caravantest.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caravantest.Adapter.TimelineAdapter;
import com.example.caravantest.R;

public class TimelineActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TimelineAdapter timelineAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        recyclerView = findViewById(R.id.recyclerView);
        timelineAdapter = new TimelineAdapter();

        recyclerView.setAdapter(timelineAdapter);
    }
}
