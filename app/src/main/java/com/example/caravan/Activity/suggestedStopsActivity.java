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

import com.example.caravan.Adapter.suggestedStopsAdapter;
import com.example.caravan.Constant.Constants;
import com.example.caravan.Database;
import com.example.caravan.GooglePlaceModel;
import com.example.caravan.R;
import com.example.caravan.StopInfo;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class suggestedStopsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private suggestedStopsAdapter suggestedStopsAdapters;
    private ArrayList<StopInfo> CurrentSuggestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routetimeline);
        Intent intent = getIntent();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setFitsSystemWindows(true);

        CurrentSuggestions = Database.get_instance().get_suggested_stops();
        suggestedStopsAdapters = new suggestedStopsAdapter(CurrentSuggestions);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(suggestedStopsAdapters);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onBackPressed(){
//        Intent result = new Intent(Intent.ACTION_GET_CONTENT);
//        result.putParcelableArrayListExtra(Constants.KEY_STOPS, CurrentSuggestions);
//        setResult(RESULT_OK, result);
        super.onBackPressed();
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN  | ItemTouchHelper.START | ItemTouchHelper.END | ItemTouchHelper.LEFT , ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Collections.swap(CurrentSuggestions,fromPosition,toPosition);

            recyclerView.getAdapter().notifyItemMoved(fromPosition,toPosition);

            return false;
        }
        StopInfo deletedRoute;

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            deletedRoute = CurrentSuggestions.get(position);

            CurrentSuggestions.remove(position);

            suggestedStopsAdapters.notifyItemRemoved(position);
            Snackbar.make(recyclerView, deletedRoute.getName(), Snackbar.LENGTH_LONG)
                    .setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CurrentSuggestions.add(position, deletedRoute);

                            suggestedStopsAdapters.notifyItemInserted(position);
                        }
                    }).show();
        }
    };
}

