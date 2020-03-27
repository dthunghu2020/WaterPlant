package com.hungdt.waterplan.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.hungdt.waterplan.R;
import com.hungdt.waterplan.database.DBHelper;
import com.hungdt.waterplan.dataset.Constant;
import com.hungdt.waterplan.model.Remind;
import com.hungdt.waterplan.view.adater.RemindAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddPlanActivity extends AppCompatActivity {

    private ImageView imgBack, imgDeletePlant, planAvatar;
    private TextView txtSave;
    private EditText edtPlanName, edtPlanNote;
    private RecyclerView rcvListCareSchedule;
    private LinearLayout addCareSchedule;

    private RemindAdapter remindAdapter;

    private List<String> checkRemind = new ArrayList<>();
    private List<Remind> reminds = new ArrayList<>();

    final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan);


        initView();
        String[] listCheck = {this.getResources().getString(R.string.water),
                this.getResources().getString(R.string.fertilize),
                this.getResources().getString(R.string.spray),
                this.getResources().getString(R.string.prune)};
        checkRemind.addAll(Arrays.asList(listCheck));
        if (reminds != null) {
            for (int i = 0; i < reminds.size(); i++) {
                if (reminds.get(i).getRemindType().equals(this.getResources().getString(R.string.water))) {
                    checkRemind.remove(this.getResources().getString(R.string.water));
                } else if (reminds.get(i).getRemindType().equals(this.getResources().getString(R.string.fertilize))) {
                    checkRemind.remove(this.getResources().getString(R.string.fertilize));
                } else if (reminds.get(i).getRemindType().equals(this.getResources().getString(R.string.spray))) {
                    checkRemind.remove(this.getResources().getString(R.string.spray));
                } else {
                    checkRemind.remove(this.getResources().getString(R.string.prune));
                }
            }
        }

        remindAdapter = new RemindAdapter(this, reminds);
        rcvListCareSchedule.setLayoutManager(new LinearLayoutManager(this));
        rcvListCareSchedule.setAdapter(remindAdapter);


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPlanActivity.super.onBackPressed();
            }
        });

        imgDeletePlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
            }
        });


        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtPlanName.getText().toString().isEmpty()){
                    Toast.makeText(AddPlanActivity.this, "Give the plant a name!", Toast.LENGTH_SHORT).show();
                }else {
                    if(edtPlanNote.getText().toString().isEmpty()){
                        DBHelper.getInstance(AddPlanActivity.this).addPlan(edtPlanName.getText().toString(), "", "");
                    }else {
                        DBHelper.getInstance(AddPlanActivity.this).addPlan(edtPlanName.getText().toString(), "", edtPlanNote.getText().toString());
                    }
                    if (reminds != null) {
                        String plantID = DBHelper.getInstance(AddPlanActivity.this).getLastPlanID();
                        for (int i = 0; i < reminds.size(); i++) {
                            Remind remind = reminds.get(i);
                            DBHelper.getInstance(AddPlanActivity.this).addRemind(plantID, remind.getRemindType(), remind.getRemindDate(), remind.getRemindTime(), remind.getCareCycle());
                        }
                        reminds.clear();
                    }
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_CANCELED,returnIntent);
                    finish();
                }
            }
        });

        addCareSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChoseTypeDialog();
            }
        });

        remindAdapter.setOnRemindItemClickListener(new RemindAdapter.OnRemindItemClickListener() {
            @Override
            public void OnItemDateTimeClicked(int position) {
                openEditRemindDialog(reminds.get(position), position);
            }

            @Override
            public void OnItemDeleteClicked(int position) {
                checkRemind.add(reminds.get(position).getRemindType());
                reminds.remove(position);
                remindAdapter.notifyDataSetChanged();
                addCareSchedule.setVisibility(View.VISIBLE);
            }
        });
    }

    private void openChoseTypeDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.choose_type_dialog);

        final LinearLayout llWater = dialog.findViewById(R.id.llWater);
        final LinearLayout llFertilizer = dialog.findViewById(R.id.llFertilizer);
        final LinearLayout llSpray = dialog.findViewById(R.id.llSpray);
        final LinearLayout llPrune = dialog.findViewById(R.id.llPrune);

        for (int i = 0; i < reminds.size(); i++) {
            if (reminds.get(i).getRemindType().equals(this.getResources().getString(R.string.water))) {
                llWater.setVisibility(View.GONE);
            } else if (reminds.get(i).getRemindType().equals(this.getResources().getString(R.string.fertilize))) {
                llFertilizer.setVisibility(View.GONE);
            } else if (reminds.get(i).getRemindType().equals(this.getResources().getString(R.string.spray))) {
                llSpray.setVisibility(View.GONE);
            } else {
                llPrune.setVisibility(View.GONE);
            }
        }

        llWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRemind.remove(dialog.getContext().getResources().getString(R.string.water));
                if (checkRemind.size() == 0) {
                    addCareSchedule.setVisibility(View.GONE);
                }
                reminds.add(new Remind("0", dialog.getContext().getResources().getString(R.string.water), getInstantDay(), "07:00", "5"));
                remindAdapter.notifyDataSetChanged();
                dialog.dismiss();

            }
        });

        llFertilizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRemind.remove(dialog.getContext().getResources().getString(R.string.fertilize));
                if (checkRemind.size() == 0) {
                    addCareSchedule.setVisibility(View.GONE);
                }
                reminds.add(new Remind("1", dialog.getContext().getResources().getString(R.string.fertilize), getInstantDay(), "07:00", "5"));
                remindAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        llSpray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRemind.remove(dialog.getContext().getResources().getString(R.string.spray));
                if (checkRemind.size() == 0) {
                    addCareSchedule.setVisibility(View.GONE);
                }
                reminds.add(new Remind("2", dialog.getContext().getResources().getString(R.string.spray), getInstantDay(), "07:00", "5"));
                remindAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        llPrune.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRemind.remove(dialog.getContext().getResources().getString(R.string.prune));
                if (checkRemind.size() == 0) {
                    addCareSchedule.setVisibility(View.GONE);
                }
                reminds.add(new Remind("3", dialog.getContext().getResources().getString(R.string.prune), getInstantDay(), "07:00", "5"));
                remindAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void openEditRemindDialog(final Remind remind, final int position) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.edit_remind_dialog);

        final TextInputEditText edtRemindDay = dialog.findViewById(R.id.edtRemindDay);
        final TextInputEditText edtRemindTime = dialog.findViewById(R.id.edtRemindTime);
        final Button btnEdit = dialog.findViewById(R.id.btnEdit);
        final Button btnBack = dialog.findViewById(R.id.btnBack);

        edtRemindDay.setText(remind.getCareCycle());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(Constant.getTimeFormat());
        Date date;
        try {
            date = sdf.parse(remind.getRemindTime());
            assert date != null;
            edtRemindTime.setText(sdf.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        edtRemindTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog(edtRemindTime);
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtRemindDay.getText().toString().isEmpty() || edtRemindTime.getText().toString().isEmpty()) {
                    Toast.makeText(AddPlanActivity.this, "Please enter all title!", Toast.LENGTH_SHORT).show();
                } else {
                    Remind remindEdited = new Remind(remind.getRemindID(), remind.getRemindType(), remind.getRemindDate(), edtRemindTime.getText().toString(), edtRemindDay.getText().toString());
                    reminds.set(position, remindEdited);
                    remindAdapter.notifyItemChanged(position);
                    dialog.dismiss();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showTimeDialog(final TextInputEditText edtRemindTime) {
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(AddPlanActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String hours = String.valueOf(hourOfDay);
                String minutes = String.valueOf(minute);
                if (hourOfDay < 10) {
                    hours = "0" + hours;
                }
                if (minute < 10) {
                    minutes = "0" + minutes;
                }
                edtRemindTime.setText(hours + ":" + minutes);
            }
        }, hour, minute, android.text.format.DateFormat.is24HourFormat(AddPlanActivity.this));
        timePickerDialog.show();
    }

    private String getInstantDay() {
        SimpleDateFormat sdf = new SimpleDateFormat(Constant.getDateFormat(), Locale.US);
        return sdf.format(calendar.getTime());
    }

    private void initView() {
        imgBack = findViewById(R.id.imgBack);
        imgDeletePlant = findViewById(R.id.imgDeletePlant);
        planAvatar = findViewById(R.id.planAvatar);
        txtSave = findViewById(R.id.txtSave);
        edtPlanName = findViewById(R.id.edtPlanName);
        edtPlanNote = findViewById(R.id.edtPlanNote);
        rcvListCareSchedule = findViewById(R.id.rcvListCareSchedule);
        addCareSchedule = findViewById(R.id.addCareSchedule);
    }
}
