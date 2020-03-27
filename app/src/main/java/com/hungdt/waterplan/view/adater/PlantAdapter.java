package com.hungdt.waterplan.view.adater;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hungdt.waterplan.R;
import com.hungdt.waterplan.model.Plant;
import com.hungdt.waterplan.model.Remind;

import java.util.ArrayList;
import java.util.List;

public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.PlanHolder> {

    private LayoutInflater layoutInflater;
    private List<Plant> plants;
    private List<Remind> reminds = new ArrayList<>();

    public PlantAdapter(Context context, List<Plant> plants) {
        layoutInflater = LayoutInflater.from(context);
        this.plants = plants;
    }

    @NonNull
    @Override
    public PlanHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.plant_adapter, parent, false);
        return new PlanHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PlanHolder holder, int position) {
        Plant plant = plants.get(position);
        //holder.imgPlanAvatar
        holder.txtPlantName.setText(plant.getPlantName());
        reminds = plant.getReminds();
        setVisibleGone(holder);
        if (reminds != null) {
            for (int i = 0; i < reminds.size(); i++) {
                if (reminds.get(i).getRemindType().equals(layoutInflater.getContext().getResources().getString(R.string.water))) {
                    holder.llWater.setVisibility(View.VISIBLE);
                    holder.txtPlantWater.setText(reminds.get(i).getCareCycle() + " days");
                }
                if (reminds.get(i).getRemindType().equals(layoutInflater.getContext().getResources().getString(R.string.fertilize))) {
                    holder.llFertilizer.setVisibility(View.VISIBLE);
                    holder.txtPlantFertilizer.setText(reminds.get(i).getCareCycle() + " days");
                }
                if (reminds.get(i).getRemindType().equals(layoutInflater.getContext().getResources().getString(R.string.spray))) {
                    holder.llSpray.setVisibility(View.VISIBLE);
                    holder.txtPlantSpray.setText(reminds.get(i).getCareCycle() + " days");
                }
                if (reminds.get(i).getRemindType().equals(layoutInflater.getContext().getResources().getString(R.string.prune                   ))) {
                    holder.llPrune.setVisibility(View.VISIBLE);
                    holder.txtPlantPrune.setText(reminds.get(i).getCareCycle() + " days");
                }
            }
        }
    }

    private void setVisibleGone(PlanHolder holder) {
        holder.llWater.setVisibility(View.GONE);
        holder.llFertilizer.setVisibility(View.GONE);
        holder.llSpray.setVisibility(View.GONE);
        holder.llPrune.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return plants.size();
    }

    class PlanHolder extends RecyclerView.ViewHolder {
        private ImageView imgPlantAvatar;
        private TextView txtPlantName, txtPlantWater, txtPlantFertilizer, txtPlantPrune, txtPlantSpray;
        private LinearLayout llWater, llFertilizer, llPrune, llSpray;

        public PlanHolder(@NonNull View itemView) {
            super(itemView);
            imgPlantAvatar = itemView.findViewById(R.id.imgPlantAvatar);
            txtPlantName = itemView.findViewById(R.id.txtPlanName);
            txtPlantWater = itemView.findViewById(R.id.txtWater);
            txtPlantFertilizer = itemView.findViewById(R.id.txtFertilizer);
            txtPlantPrune = itemView.findViewById(R.id.txtPrune);
            txtPlantSpray = itemView.findViewById(R.id.txtSpray);
            llWater = itemView.findViewById(R.id.llWater);
            llFertilizer = itemView.findViewById(R.id.llFertilizer);
            llPrune = itemView.findViewById(R.id.llPrune);
            llSpray = itemView.findViewById(R.id.llSpray);
        }
    }
}
