package com.hungdt.waterplan.view;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hungdt.waterplan.R;
import com.hungdt.waterplan.model.Remind;

import java.util.ArrayList;
import java.util.List;

public class AddPlanActivity extends AppCompatActivity {

    private ImageView imgBack, imgDeletePlant,planAvatar;
    private TextView txtSave;
    private EditText edtPlanName, edtPlanNote;
    private RecyclerView rcvListCareSchedule;
    private LinearLayout addCareSchedule;

    private RemindAdapter remindAdapter;

    private List<Remind> reminds = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan);

        initView();
        remindAdapter = new RemindAdapter(this,reminds);
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
                //todo
            }
        });

        addCareSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
            }
        });
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
