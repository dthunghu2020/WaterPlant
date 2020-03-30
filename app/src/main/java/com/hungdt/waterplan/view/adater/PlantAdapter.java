package com.hungdt.waterplan.view.adater;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hungdt.waterplan.R;
import com.hungdt.waterplan.model.Plant;
import com.hungdt.waterplan.model.Remind;

import java.util.ArrayList;
import java.util.List;

public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.PlanHolder> {
    private LayoutInflater layoutInflater;
    private List<Plant> plants;
    private List<Remind> reminds = new ArrayList<>();
    private OnPlantItemClickListener onPlantItemClickListener;
    private Boolean cbVisible = false;

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
    public void onBindViewHolder(@NonNull final PlanHolder holder, final int position) {

        Glide
                .with(layoutInflater.getContext())
                .load(plants.get(position).getPlantImage())
                .placeholder(R.drawable.tree_default)
                .into(holder.imgPlantAvatar);

        holder.txtPlantName.setText(plants.get(position).getPlantName());
        reminds = plants.get(position).getReminds();
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
                if (reminds.get(i).getRemindType().equals(layoutInflater.getContext().getResources().getString(R.string.prune))) {
                    holder.llPrune.setVisibility(View.VISIBLE);
                    holder.txtPlantPrune.setText(reminds.get(i).getCareCycle() + " days");
                }
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cbVisible){
                    plants.get(position).setTicked(!plants.get(position).isTicked());
                    holder.cbPlant.setChecked(plants.get(position).isTicked());
                }
                onPlantItemClickListener.OnItemClicked(holder.getAdapterPosition(),cbVisible);
            }
        });
    }

    private void setVisibleGone(PlanHolder holder) {
        if (!cbVisible) {
            holder.cbPlant.setVisibility(View.GONE);
        } else {
            holder.cbPlant.setVisibility(View.VISIBLE);
            holder.cbPlant.setChecked(false);
        }
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
        private CheckBox cbPlant;
        private ImageView imgPlantAvatar;
        private TextView txtPlantName, txtPlantWater, txtPlantFertilizer, txtPlantPrune, txtPlantSpray;
        private LinearLayout llWater, llFertilizer, llPrune, llSpray;

        public PlanHolder(@NonNull View itemView) {
            super(itemView);
            cbPlant = itemView.findViewById(R.id.cbPlant);
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

    public void setOnPlantItemClickListener(OnPlantItemClickListener onPlantItemClickListener) {
        this.onPlantItemClickListener = onPlantItemClickListener;
    }

    public interface OnPlantItemClickListener {
        void OnItemClicked(int position, boolean checkBox);
    }

    public void enableCheckBox() {
        cbVisible = true;
    }

    public void disableCheckBox() {
        cbVisible = false;
    }

}
