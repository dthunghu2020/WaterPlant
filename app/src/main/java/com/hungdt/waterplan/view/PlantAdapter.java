package com.hungdt.waterplan.view;

import android.annotation.SuppressLint;
import android.content.Context;
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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.PlanHolder> {

    private LayoutInflater layoutInflater;
    private List<Plant> plants;

    public PlantAdapter(Context context, List<Plant> plants) {
        layoutInflater = LayoutInflater.from(context);
        this.plants = plants;
    }

    @NonNull
    @Override
    public PlanHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.plant_adapter,parent,false);
        return new PlanHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PlanHolder holder, int position) {
        Plant plant = plants.get(position);
        //holder.imgPlanAvatar
        holder.txtPlanName.setText(plant.getPlantName());


        if(!plant.getReminds().get(position).getRemindType().equals(layoutInflater.getContext().getResources().getString(R.string.water))){
            holder.llPlanWater.setVisibility(View.GONE);
        }else {
            holder.txtPlanWater.setText(plant.getReminds().get(position).getCareCycle()+" days");
        }

        if(!plant.getReminds().get(position).getRemindType().equals(layoutInflater.getContext().getResources().getString(R.string.fertilize))){
            holder.llPlanFertilizer.setVisibility(View.GONE);
        }else {
            holder.txtPlanFertilizer.setText(plant.getReminds().get(position).getCareCycle()+" days");
        }

        if(!plant.getReminds().get(position).getRemindType().equals(layoutInflater.getContext().getResources().getString(R.string.spray))){
            holder.llSpray.setVisibility(View.GONE);
        }else {
            holder.txtSpray.setText(plant.getReminds().get(position).getCareCycle()+" days");
        }

        if(!plant.getReminds().get(position).getRemindType().equals(layoutInflater.getContext().getResources().getString(R.string.prune))){
            holder.llPrune.setVisibility(View.GONE);
        }else {
            holder.txtPrune.setText(plant.getReminds().get(position).getCareCycle()+" days");
        }
    }

    @Override
    public int getItemCount() {
        return plants.size();
    }

    class PlanHolder extends RecyclerView.ViewHolder {
        private ImageView imgPlanAvatar;
        private TextView txtPlanName, txtPlanWater, txtPlanFertilizer, txtPrune, txtSpray;
        private LinearLayout llPlanWater,llPlanFertilizer,llPrune,llSpray;

        public PlanHolder(@NonNull View itemView) {
            super(itemView);
            imgPlanAvatar = itemView.findViewById(R.id.imgPlanAvatar);
            txtPlanName = itemView.findViewById(R.id.txtPlanName);
            txtPlanWater = itemView.findViewById(R.id.txtPlanWater);
            txtPlanFertilizer = itemView.findViewById(R.id.txtPlanFertilizer);
            txtPrune = itemView.findViewById(R.id.txtPrune);
            txtSpray = itemView.findViewById(R.id.txtSpray);
            llPlanWater = itemView.findViewById(R.id.llPlanWater);
            llPlanFertilizer = itemView.findViewById(R.id.llPlanFertilizer);
            llPrune = itemView.findViewById(R.id.llPrune);
            llSpray = itemView.findViewById(R.id.llSpray);
        }
    }
}
