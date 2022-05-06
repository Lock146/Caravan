package com.example.caravan.Adapter;


import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caravan.Model.DirectionPlaceModel.DirectionStepModel;
import com.example.caravan.databinding.StepItemLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class DirectionStepAdapter extends RecyclerView.Adapter<DirectionStepAdapter.ViewHolder> {

    private List<DirectionStepModel> m_directionStepModels;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        StepItemLayoutBinding binding = StepItemLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (m_directionStepModels != null) {
            DirectionStepModel stepModel = m_directionStepModels.get(position);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.binding.txtStepHtml.setText(Html.fromHtml(stepModel.getHtmlInstructions(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                holder.binding.txtStepHtml.setText(Html.fromHtml(stepModel.getHtmlInstructions()));
            }

            holder.binding.txtStepTime.setText(stepModel.getDuration().getText());
            holder.binding.txtStepDistance.setText(stepModel.getDistance().getText());
        }

    }

    @Override
    public int getItemCount() {

        if (m_directionStepModels != null)
            return m_directionStepModels.size();
        else
            return 0;
    }

    public void setDirectionStepModels(List<DirectionStepModel> directionStepModels) {
        m_directionStepModels = directionStepModels;
        notifyDataSetChanged();
    }

    public void append_direction_step_models(List<DirectionStepModel> directionStepModels){
        if(m_directionStepModels == null){
            m_directionStepModels = new ArrayList<>();
        }
        m_directionStepModels.addAll(directionStepModels);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private StepItemLayoutBinding binding;

        public ViewHolder(@NonNull StepItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}