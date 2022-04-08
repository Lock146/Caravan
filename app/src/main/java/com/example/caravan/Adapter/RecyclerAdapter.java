package com.example.caravan.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caravan.R;
import com.example.caravan.RouteInfo;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{
    private static final String TAG = "RecyclerAdapter";


    double MILES = 1609.344;
    double route_miles;
    List<String> Routes;
    List<Double> Miles;
    List<RouteInfo> Route;
    public RecyclerAdapter(List<RouteInfo> route) {//List<String> routes, List<Double> miles) {
        //Routes = routes;
        //Miles = miles;
        Route = route;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_item,parent,false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //holder.rowCountTextView.setText(String.valueOf(position));

        //route_miles = Miles.get(position)/MILES;
        route_miles = Route.get(position).getRouteMeters()/MILES;
        holder.rowCountTextView.setText(String.valueOf(route_miles));
        holder.textView.setText(Route.get(position).getRouteName());//Routes.get(position));

    }

    @Override
    public int getItemCount() {
        return Route.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView textView, rowCountTextView;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            rowCountTextView = itemView.findViewById(R.id.rowCountTextView);


            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //Routes.remove(getAdapterPosition());
                    //notifyItemRemoved(getAdapterPosition());
                    return true;
                }
            });


        }

        @Override
        public void onClick(View view) {
            //Routes.remove(getAdapterPosition());
            //notifyItemRemoved(getAdapterPosition());
            Toast.makeText(view.getContext(), Route.get(getAdapterPosition()).getRouteName(), Toast.LENGTH_SHORT).show();
        }
    }
}





