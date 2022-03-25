package com.example.caravantest.Activity;

import android.media.MediaRouter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caravantest.Adapter.TimelineAdapter;
import com.example.caravantest.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimelineActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TimelineAdapter timelineAdapter;
    List<String> destinationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        destinationList = new ArrayList<>();

        //TODO: Create a function that takes the name of a destination
        // on a route and input it onto the list
        destinationList.add("New Orleans");
        destinationList.add("Ruston");

        recyclerView = findViewById(R.id.recyclerView);
        timelineAdapter = new TimelineAdapter(destinationList);

        recyclerView.setAdapter(timelineAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP |ItemTouchHelper.DOWN| ItemTouchHelper.START| ItemTouchHelper.END, 0 ) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Collections.swap(destinationList, fromPosition, toPosition);

            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);


            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };
}
