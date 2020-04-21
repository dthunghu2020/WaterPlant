package com.hungdt.waterplan.view.adater;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hungdt.waterplan.KEY;
import com.hungdt.waterplan.R;
import com.hungdt.waterplan.dataset.Constant;
import com.hungdt.waterplan.model.Event;
import com.hungdt.waterplan.model.Plant;
import com.hungdt.waterplan.model.Remind;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.PlanHolder> {
    private LayoutInflater layoutInflater;
    private List<Plant> plants;
    private List<Remind> reminds = new ArrayList<>();
    private List<Event> events = new ArrayList<>();
    private OnPlantItemClickListener onPlantItemClickListener;
    private Boolean cbVisible = false;

    private String typeOfCare = null;

    Calendar calendar = Calendar.getInstance();

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
                .placeholder(R.drawable.ava_default_tree)
                .into(holder.imgPlantAvatar);

        holder.txtPlantName.setText(plants.get(position).getPlantName());
        reminds = plants.get(position).getReminds();
        events = plants.get(position).getEvents();
        setVisibleGone(holder);

        if (reminds != null) {
            Date date;
            int numberOfDay;
            int remindCircle;
            int numberDayCreate = 0;
            int numberDayInStance = 0;

            @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdfDateTime = new SimpleDateFormat(Constant.getDateTimeFormat());
            @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdfDay = new SimpleDateFormat(Constant.getDayFormat());
            @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdfMonth = new SimpleDateFormat(Constant.getMonthFormat());
            @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdfYear = new SimpleDateFormat(Constant.getYearFormat());
            try {
                date = sdfDateTime.parse(getInstantDateTime());
                assert date != null;
                numberDayInStance = countNumberOfDateTime(Integer.parseInt(sdfYear.format(date)), Integer.parseInt(sdfMonth.format(date)), Integer.parseInt(sdfDay.format(date)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < reminds.size(); i++) {
                if (reminds.get(i).getRemindType().equals(layoutInflater.getContext().getResources().getString(R.string.water))) {
                    holder.clWater.setVisibility(View.VISIBLE);
                    if (typeOfCare!=null){
                        if(typeOfCare.equals(KEY.TYPE_WATER)){
                            holder.clWaterCB.setVisibility(View.VISIBLE);
                        }
                    }
                    try {
                        date = sdfDateTime.parse(reminds.get(i).getRemindCreateDT());
                        assert date != null;
                        numberDayCreate = countNumberOfDateTime(Integer.parseInt(sdfYear.format(date)), Integer.parseInt(sdfMonth.format(date)), Integer.parseInt(sdfDay.format(date)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    remindCircle = Integer.parseInt(reminds.get(i).getCareCycle());
                    numberOfDay = numberDayCreate + remindCircle - numberDayInStance;
                    holder.pbWater.setMax(remindCircle);
                    holder.pbWaterCB.setMax(remindCircle);
                    if (numberOfDay > 0) {
                        holder.pbWater.setProgress(numberOfDay);
                        holder.pbWaterCB.setProgress(numberOfDay);
                    } else {
                        holder.pbWater.setProgress(0);
                        holder.pbWaterCB.setProgress(0);
                    }
                    holder.txtPlantWater.setText(reminds.get(i).getCareCycle() + " days");
                    holder.txtPlantWaterCB.setText(reminds.get(i).getCareCycle() + " days");
                }
                if (reminds.get(i).getRemindType().equals(layoutInflater.getContext().getResources().getString(R.string.fertilizer))) {
                    holder.clFertilizer.setVisibility(View.VISIBLE);
                    if (typeOfCare!=null){
                        if(typeOfCare.equals(KEY.TYPE_FERTILIZER)){
                            holder.clFertilizerCB.setVisibility(View.VISIBLE);
                        }
                    }
                    try {
                        date = sdfDateTime.parse(reminds.get(i).getRemindCreateDT());
                        assert date != null;
                        numberDayCreate = countNumberOfDateTime(Integer.parseInt(sdfYear.format(date)), Integer.parseInt(sdfMonth.format(date)), Integer.parseInt(sdfDay.format(date)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    remindCircle = Integer.parseInt(reminds.get(i).getCareCycle());
                    numberOfDay = numberDayCreate + remindCircle - numberDayInStance;
                    holder.pbFertilizer.setMax(remindCircle);
                    holder.pbFertilizerCB.setMax(remindCircle);
                    if (numberOfDay > 0) {
                        holder.pbFertilizer.setProgress(numberOfDay);
                        holder.pbFertilizerCB.setProgress(numberOfDay);
                    } else {
                        holder.pbFertilizer.setProgress(0);
                        holder.pbFertilizerCB.setProgress(0);
                    }
                    holder.txtPlantFertilizer.setText(reminds.get(i).getCareCycle() + " days");
                    holder.txtPlantFertilizerCB.setText(reminds.get(i).getCareCycle() + " days");
                }
                if (reminds.get(i).getRemindType().equals(layoutInflater.getContext().getResources().getString(R.string.prune))) {
                    holder.clPrune.setVisibility(View.VISIBLE);
                    if (typeOfCare!=null){
                        if(typeOfCare.equals(KEY.TYPE_PRUNE)){
                            holder.clPruneCB.setVisibility(View.VISIBLE);
                        }
                    }
                    try {
                        date = sdfDateTime.parse(reminds.get(i).getRemindCreateDT());
                        assert date != null;
                        numberDayCreate = countNumberOfDateTime(Integer.parseInt(sdfYear.format(date)), Integer.parseInt(sdfMonth.format(date)), Integer.parseInt(sdfDay.format(date)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    remindCircle = Integer.parseInt(reminds.get(i).getCareCycle());
                    numberOfDay = numberDayCreate + remindCircle - numberDayInStance;
                    holder.pbPrune.setMax(remindCircle);
                    holder.pbPruneCB.setMax(remindCircle);
                    if (numberOfDay > 0) {
                        holder.pbPrune.setProgress(numberOfDay);
                        holder.pbPruneCB.setProgress(numberOfDay);
                    } else {
                        holder.pbPrune.setProgress(0);
                        holder.pbPruneCB.setProgress(0);
                    }
                    holder.txtPlantPrune.setText(reminds.get(i).getCareCycle() + " days");
                    holder.txtPlantPruneCB.setText(reminds.get(i).getCareCycle() + " days");
                }
                if (reminds.get(i).getRemindType().equals(layoutInflater.getContext().getResources().getString(R.string.spray))) {
                    holder.clSpray.setVisibility(View.VISIBLE);
                    if (typeOfCare!=null){
                        if(typeOfCare.equals(KEY.TYPE_SPRAY)){
                            holder.clSprayCB.setVisibility(View.VISIBLE);
                        }
                    }
                    try {
                        date = sdfDateTime.parse(reminds.get(i).getRemindCreateDT());
                        assert date != null;
                        numberDayCreate = countNumberOfDateTime(Integer.parseInt(sdfYear.format(date)), Integer.parseInt(sdfMonth.format(date)), Integer.parseInt(sdfDay.format(date)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    remindCircle = Integer.parseInt(reminds.get(i).getCareCycle());
                    numberOfDay = numberDayCreate + remindCircle - numberDayInStance;
                    holder.pbSpray.setMax(remindCircle);
                    holder.pbSprayCB.setMax(remindCircle);
                    if (numberOfDay > 0) {
                        holder.pbSpray.setProgress(numberOfDay);
                        holder.pbSprayCB.setProgress(numberOfDay);
                    } else {
                        holder.pbSpray.setProgress(0);
                        holder.pbSprayCB.setProgress(0);
                    }
                    holder.txtPlantSpray.setText(reminds.get(i).getCareCycle() + " days");
                    holder.txtPlantSprayCB.setText(reminds.get(i).getCareCycle() + " days");
                }

            }
        }
        if (typeOfCare != null) {
            holder.lineEvent.setVisibility(View.GONE);
            holder.eventView.setVisibility(View.GONE);
        }
        if (events.size() != 0) {
            if(typeOfCare==null){
                holder.lineEvent.setVisibility(View.VISIBLE);
                holder.eventView.setVisibility(View.VISIBLE);
            }
            holder.event1.setVisibility(View.GONE);
            holder.event2.setVisibility(View.GONE);
            holder.event3.setVisibility(View.GONE);
            for (int i = 0; i < events.size(); i++) {
                if (events.get(i).getEventPosition().equals("1")) {
                    holder.event1.setVisibility(View.VISIBLE);
                    holder.txtEvent1.setText(events.get(i).getEventName());
                    holder.txtEventDay1.setText(events.get(i).getEventDate());
                } else if (events.get(i).getEventPosition().equals("2")) {
                    holder.event2.setVisibility(View.VISIBLE);
                    holder.txtEvent2.setText(events.get(i).getEventName());
                    holder.txtEventDay2.setText(events.get(i).getEventDate());
                } else if (events.get(i).getEventPosition().equals("3")) {
                    holder.event3.setVisibility(View.VISIBLE);
                    holder.txtEvent3.setText(events.get(i).getEventName());
                    holder.txtEventDay3.setText(events.get(i).getEventDate());
                }
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbVisible) {
                    plants.get(position).setTicked(!plants.get(position).isTicked());
                    holder.cbPlant.setChecked(plants.get(position).isTicked());
                }
                onPlantItemClickListener.OnItemClicked(holder.getAdapterPosition(), cbVisible);
            }
        });
    }

    private void setVisibleGone(PlanHolder holder) {
        if (!cbVisible) {
            holder.llCheckBox.setVisibility(View.INVISIBLE);
            holder.llShow.setVisibility(View.VISIBLE);
            holder.cbPlant.setVisibility(View.GONE);
        } else {
            holder.llCheckBox.setVisibility(View.VISIBLE);
            holder.llShow.setVisibility(View.INVISIBLE);
            holder.cbPlant.setVisibility(View.VISIBLE);
            holder.cbPlant.setChecked(false);
        }
        holder.lineEvent.setVisibility(View.GONE);
        holder.eventView.setVisibility(View.GONE);
        holder.clWater.setVisibility(View.GONE);
        holder.clFertilizer.setVisibility(View.GONE);
        holder.clSpray.setVisibility(View.GONE);
        holder.clPrune.setVisibility(View.GONE);
        holder.clWaterCB.setVisibility(View.GONE);
        holder.clFertilizerCB.setVisibility(View.GONE);
        holder.clSprayCB.setVisibility(View.GONE);
        holder.clPruneCB.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return plants.size();
    }

    class PlanHolder extends RecyclerView.ViewHolder {
        private CheckBox cbPlant;
        private ImageView imgPlantAvatar, lineEvent;
        private LinearLayout eventView, event1, event2, event3, llShow, llCheckBox;
        private ProgressBar pbWater, pbFertilizer, pbPrune, pbSpray, pbWaterCB, pbFertilizerCB, pbPruneCB, pbSprayCB;
        private TextView txtPlantName, txtPlantWater, txtPlantFertilizer, txtPlantPrune, txtPlantSpray, txtPlantWaterCB, txtPlantFertilizerCB, txtPlantPruneCB, txtPlantSprayCB, txtEvent1, txtEventDay1, txtEvent2, txtEventDay2, txtEvent3, txtEventDay3;
        private ConstraintLayout clWater, clFertilizer, clPrune, clSpray, clWaterCB, clFertilizerCB, clPruneCB, clSprayCB;

        public PlanHolder(@NonNull View itemView) {
            super(itemView);
            llShow = itemView.findViewById(R.id.LLShow);
            llCheckBox = itemView.findViewById(R.id.LLCheckBox);
            cbPlant = itemView.findViewById(R.id.cbPlant);
            lineEvent = itemView.findViewById(R.id.lineEvent);
            eventView = itemView.findViewById(R.id.eventView);
            event1 = itemView.findViewById(R.id.event1);
            event2 = itemView.findViewById(R.id.event2);
            event3 = itemView.findViewById(R.id.event3);
            txtEvent1 = itemView.findViewById(R.id.txtEvent1);
            txtEventDay1 = itemView.findViewById(R.id.txtEventDay1);
            txtEvent2 = itemView.findViewById(R.id.txtEvent2);
            txtEventDay2 = itemView.findViewById(R.id.txtEventDay2);
            txtEventDay3 = itemView.findViewById(R.id.txtEventDay3);
            txtEvent3 = itemView.findViewById(R.id.txtEvent3);
            imgPlantAvatar = itemView.findViewById(R.id.imgPlantAvatar);
            pbWater = itemView.findViewById(R.id.pbWater);
            pbFertilizer = itemView.findViewById(R.id.pbFertilizer);
            pbPrune = itemView.findViewById(R.id.pbPrune);
            pbSpray = itemView.findViewById(R.id.pbSpray);
            pbWaterCB = itemView.findViewById(R.id.pbWaterCB);
            pbFertilizerCB = itemView.findViewById(R.id.pbFertilizerCB);
            pbPruneCB = itemView.findViewById(R.id.pbPruneCB);
            pbSprayCB = itemView.findViewById(R.id.pbSprayCB);
            txtPlantName = itemView.findViewById(R.id.txtPlanName);
            txtPlantWater = itemView.findViewById(R.id.txtWater);
            txtPlantFertilizer = itemView.findViewById(R.id.txtFertilizer);
            txtPlantPrune = itemView.findViewById(R.id.txtPrune);
            txtPlantSpray = itemView.findViewById(R.id.txtSpray);
            txtPlantWaterCB = itemView.findViewById(R.id.txtWaterCB);
            txtPlantFertilizerCB = itemView.findViewById(R.id.txtFertilizerCB);
            txtPlantPruneCB = itemView.findViewById(R.id.txtPruneCB);
            txtPlantSprayCB = itemView.findViewById(R.id.txtSprayCB);
            clWater = itemView.findViewById(R.id.clWater);
            clFertilizer = itemView.findViewById(R.id.clFertilizer);
            clPrune = itemView.findViewById(R.id.clPrune);
            clSpray = itemView.findViewById(R.id.clSpray);
            clWaterCB = itemView.findViewById(R.id.clWaterCB);
            clFertilizerCB = itemView.findViewById(R.id.clFertilizerCB);
            clPruneCB = itemView.findViewById(R.id.clPruneCB);
            clSprayCB = itemView.findViewById(R.id.clSprayCB);
        }
    }

    private String getInstantDateTime() {
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf = new SimpleDateFormat(Constant.getDateTimeFormat(), Locale.US);
        return sdf.format(calendar.getTime());
    }

    private int countNumberOfDateTime(int year, int month, int day) {
        if (month < 3) {
            year--;
            month += 12;
        }
        return 365 * year + year / 4 - year / 100 + year / 400 + (153 * month - 457) / 5 + day - 306;
    }

    public void setOnPlantItemClickListener(OnPlantItemClickListener onPlantItemClickListener) {
        this.onPlantItemClickListener = onPlantItemClickListener;
    }

    public interface OnPlantItemClickListener {
        void OnItemClicked(int position, boolean checkBox);
    }

    public void enableCheckBox(String type) {
        cbVisible = true;
        typeOfCare = type;
    }

    public void disableCheckBox() {
        cbVisible = false;
        typeOfCare = null;
    }

}
