package com.example.caravan.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caravan.Adapter.RouteTimelineAdapter;
import com.example.caravan.Constant.Constants;
import com.example.caravan.R;
import com.example.caravan.StopInfo;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RouteTimelineActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RouteTimelineAdapter routeTimelineAdapter;
    private ArrayList<StopInfo> CurrentRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routetimeline);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras.containsKey(Constants.KEY_STOPS)) {
            CurrentRoute = extras.getParcelableArrayList(Constants.KEY_STOPS);
        }
        else{
            CurrentRoute = new ArrayList<>();
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setFitsSystemWindows(true);
        routeTimelineAdapter = new RouteTimelineAdapter(CurrentRoute);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(routeTimelineAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onBackPressed(){
        Intent result = new Intent(Intent.ACTION_GET_CONTENT);
        result.putParcelableArrayListExtra(Constants.KEY_STOPS, CurrentRoute);
        setResult(RESULT_OK, result);
        super.onBackPressed();
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN  | ItemTouchHelper.START | ItemTouchHelper.END | ItemTouchHelper.LEFT , ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Collections.swap(CurrentRoute,fromPosition,toPosition);

            recyclerView.getAdapter().notifyItemMoved(fromPosition,toPosition);

            return false;
        }
        StopInfo deletedRoute;

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            deletedRoute = CurrentRoute.get(position);

            CurrentRoute.remove(position);

            routeTimelineAdapter.notifyItemRemoved(position);
            Snackbar.make(recyclerView, deletedRoute.name(), Snackbar.LENGTH_LONG)
                    .setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CurrentRoute.add(position, deletedRoute);

                            routeTimelineAdapter.notifyItemInserted(position);
                        }
                    }).show();
            }
        };
    }

