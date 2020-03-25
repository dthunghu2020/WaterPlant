package com.hungdt.waterplan.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hungdt.waterplan.R;
import com.hungdt.waterplan.model.Remind;

import java.util.List;

public class RemindAdapter extends RecyclerView.Adapter<RemindAdapter.RemindHolder> {

    private LayoutInflater layoutInflater;
    private List<Remind> reminds;

    public RemindAdapter(Context context, List<Remind> reminds) {
        layoutInflater = LayoutInflater.from(context);
        this.reminds = reminds;
    }

    @NonNull
    @Override
    public RemindHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.care_schedule_adapter,parent,false);
        return new RemindHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RemindHolder holder, int position) {
        Remind remind = reminds.get(position);

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
            }
        });

        if(remind.getRemindType().equals(layoutInflater.getContext().getResources().getString(R.string.water))){
            holder.imgTypeOfCare.setImageDrawable(layoutInflater.getContext().getResources().getDrawable(R.drawable.ic_water));
        }else if (remind.getRemindType().equals(layoutInflater.getContext().getResources().getString(R.string.fertilize))){
            holder.imgTypeOfCare.setImageDrawable(layoutInflater.getContext().getResources().getDrawable(R.drawable.ic_fertilizer));
        }else if (remind.getRemindType().equals(layoutInflater.getContext().getResources().getString(R.string.spray))){
            holder.imgTypeOfCare.setImageDrawable(layoutInflater.getContext().getResources().getDrawable(R.drawable.ic_spray));
        }else if (remind.getRemindType().equals(layoutInflater.getContext().getResources().getString(R.string.prune))){
            holder.imgTypeOfCare.setImageDrawable(layoutInflater.getContext().getResources().getDrawable(R.drawable.ic_prune));
        }
        /*holder.txtDayCare.setText();
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        edtDate.setText(sdf.format(myCalendar.getTime()));

        String myFormat2 = "HH:mm";
        SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat2, Locale.US);
        edtTime.setText(sdf2.format(myCalendar.getTime()));
        holder.txtTime.setText();*/
    }

    @Override
    public int getItemCount() {
        return reminds.size();
    }

    class RemindHolder extends RecyclerView.ViewHolder {
        private ImageView imgTypeOfCare, imgDelete;
        private TextView  txtDayCare, txtTime;
        public RemindHolder(@NonNull View itemView) {
            super(itemView);
            imgTypeOfCare = itemView.findViewById(R.id.imgTypeOfCare);
            imgDelete = itemView.findViewById(R.id.imgDelete);
            txtDayCare = itemView.findViewById(R.id.txtDayCare);
            txtTime = itemView.findViewById(R.id.txtTime);
        }
    }
}
