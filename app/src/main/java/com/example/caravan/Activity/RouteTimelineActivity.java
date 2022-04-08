package com.example.caravan.Activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caravan.Adapter.RouteTimelineAdapter;
import com.example.caravan.R;
import com.example.caravan.RouteInfo;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RouteTimelineActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    RouteTimelineAdapter routeTimelineAdapter;

    //List<String> Routes;
    //List<Double> Miles;

    List<RouteInfo> CurrentRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routetimeline);


        //Routes = new ArrayList<>();
        //Miles = new ArrayList<>();

        CurrentRoute = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        //recyclerAdapter = new RecyclerAdapter(Routes,Miles);
        routeTimelineAdapter = new RouteTimelineAdapter(CurrentRoute);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(routeTimelineAdapter);


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);


        CurrentRoute.add(new RouteInfo("Ruston",1609.344));
        CurrentRoute.add(new RouteInfo("New Orleans", 160934.000));


       /* Routes.add("Ruston");
        Miles.add(1609.344);
        Routes.add("New Orleans");
        Miles.add(160934.000);
        Routes.add("Disney World");
        Miles.add(32186.98);
        Routes.add("Chicago");
        Miles.add(22530.88);
        Routes.add("London");
        Miles.add(320259.00);
        Routes.add("Houston");
        Miles.add(85285.22);
        */




        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN  | ItemTouchHelper.START | ItemTouchHelper.END | ItemTouchHelper.LEFT , ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            //Collections.swap(Routes,fromPosition,toPosition);
            //Collections.swap(Miles,fromPosition,toPosition);
            Collections.swap(CurrentRoute,fromPosition,toPosition);

            recyclerView.getAdapter().notifyItemMoved(fromPosition,toPosition);


            return false;
        }


        //String deletedRoute = null;
        //double deletedMiles = 0.00;
        RouteInfo deletedRoute;

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();



            //deletedRoute = Routes.get(position);
            //deletedMiles = Miles.get(position);

            deletedRoute = CurrentRoute.get(position);


            //Routes.remove(position);
            //Miles.remove(position);

            CurrentRoute.remove(position);

            routeTimelineAdapter.notifyItemRemoved(position);
            Snackbar.make(recyclerView, deletedRoute.getRouteName(), Snackbar.LENGTH_LONG)
                    .setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //Routes.add(position, deletedRoute);
                            //Miles.add(position, deletedMiles);
                            CurrentRoute.add(position, deletedRoute);

                            routeTimelineAdapter.notifyItemInserted(position);
                        }
                    }).show();

            }
        };
    };

