package com.hungdt.waterplan.view.adater;

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
import com.hungdt.waterplan.model.Remind;

import java.util.List;

public class RemindAdapter extends RecyclerView.Adapter<RemindAdapter.RemindHolder> {

    private LayoutInflater layoutInflater;
    private List<Remind> reminds;

    private OnRemindItemClickListener onRemindItemClickListener;

    public RemindAdapter(Context context, List<Remind> reminds) {
        layoutInflater = LayoutInflater.from(context);
        this.reminds = reminds;
    }

    @NonNull
    @Override
    public RemindHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.remind_adapter,parent,false);
        return new RemindHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RemindHolder holder, final int position) {
        Remind remind = reminds.get(position);

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
            }
        });

        if(remind.getRemindType().equals(layoutInflater.getContext().getResources().getString(R.string.water))){
            holder.llDate.setBackgroundColor(layoutInflater.getContext().getResources().getColor(R.color.colorWater));
            holder.txtTime.setBackgroundColor(layoutInflater.getContext().getResources().getColor(R.color.colorWater));
            holder.imgTypeOfCare.setImageDrawable(layoutInflater.getContext().getResources().getDrawable(R.drawable.ic_water));
        }else if (remind.getRemindType().equals(layoutInflater.getContext().getResources().getString(R.string.fertilize))){
            holder.llDate.setBackgroundColor(layoutInflater.getContext().getResources().getColor(R.color.colorFertilizer));
            holder.txtTime.setBackgroundColor(layoutInflater.getContext().getResources().getColor(R.color.colorFertilizer));
            holder.imgTypeOfCare.setImageDrawable(layoutInflater.getContext().getResources().getDrawable(R.drawable.ic_fertilizer));
        }else if (remind.getRemindType().equals(layoutInflater.getContext().getResources().getString(R.string.spray))){
            holder.llDate.setBackgroundColor(layoutInflater.getContext().getResources().getColor(R.color.colorSpray));
            holder.txtTime.setBackgroundColor(layoutInflater.getContext().getResources().getColor(R.color.colorSpray));
            holder.imgTypeOfCare.setImageDrawable(layoutInflater.getContext().getResources().getDrawable(R.drawable.ic_spray));
        }else if (remind.getRemindType().equals(layoutInflater.getContext().getResources().getString(R.string.prune))){
            holder.llDate.setBackgroundColor(layoutInflater.getContext().getResources().getColor(R.color.colorPrune));
            holder.txtTime.setBackgroundColor(layoutInflater.getContext().getResources().getColor(R.color.colorPrune));
            holder.imgTypeOfCare.setImageDrawable(layoutInflater.getContext().getResources().getDrawable(R.drawable.ic_prune));
        }

        holder.txtDayCare.setText("Every "+remind.getCareCycle()+" days");

        holder.txtTime.setText(remind.getRemindTime());


        holder.llDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRemindItemClickListener.OnItemDateTimeClicked(holder.getAdapterPosition());
            }
        });

        holder.txtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRemindItemClickListener.OnItemDateTimeClicked(holder.getAdapterPosition());
            }
        });

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRemindItemClickListener.OnItemDeleteClicked(holder.getAdapterPosition());
            }
        });


    }

    @Override
    public int getItemCount() {
        return reminds.size();
    }

    class RemindHolder extends RecyclerView.ViewHolder {
        private ImageView imgTypeOfCare, imgDelete;
        private LinearLayout llDate;
        private TextView  txtDayCare, txtTime;
        public RemindHolder(@NonNull View itemView) {
            super(itemView);
            imgTypeOfCare = itemView.findViewById(R.id.imgTypeOfRemind);
            imgDelete = itemView.findViewById(R.id.imgDelete);
            llDate = itemView.findViewById(R.id.llDate);
            txtDayCare = itemView.findViewById(R.id.txtCareCircle);
            txtTime = itemView.findViewById(R.id.txtTime);
        }
    }

    public void setOnRemindItemClickListener(OnRemindItemClickListener onRemindItemClickListener){
        this.onRemindItemClickListener = onRemindItemClickListener;
    }

    public interface OnRemindItemClickListener{
        void OnItemDateTimeClicked(int position);
        void OnItemDeleteClicked(int position);
    }
}
