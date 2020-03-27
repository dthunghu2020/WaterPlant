package com.hungdt.waterplan.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.hungdt.waterplan.R;
import com.hungdt.waterplan.database.DBHelper;
import com.hungdt.waterplan.model.Plant;
import com.hungdt.waterplan.view.adater.PlantAdapter;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private ImageView imgSetting;
    private CircleImageView imgPlus, imgWater, imgFertilizer, imgCut, imgSpray;
    private RecyclerView rcvPlan;
    private PlantAdapter plantAdapter;

    private static final int REQUEST_CODE_ADD_PLANT = 100;
    private List<Plant> plants = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initView();

        plants.addAll(DBHelper.getInstance(this).getAllPlant());
        plantAdapter = new PlantAdapter(this,plants);
        rcvPlan.setLayoutManager(new LinearLayoutManager(this));
        rcvPlan.setAdapter(plantAdapter);

        imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
            }
        });

        imgPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AddPlanActivity.class);
                startActivityForResult(intent,REQUEST_CODE_ADD_PLANT);
            }
        });

        imgWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
            }
        });

        imgFertilizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
            }
        });

        imgCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
            }
        });

        imgSpray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ADD_PLANT){
            //DBHelper.getInstance(this).getLastPlant();
        }
    }

    private void initView() {
        imgSetting = findViewById(R.id.imgSetting);
        imgPlus = findViewById(R.id.imgPlus);
        imgWater = findViewById(R.id.imgWater);
        imgFertilizer = findViewById(R.id.imgFertilizer);
        imgCut = findViewById(R.id.imgCut);
        imgSpray = findViewById(R.id.imgSpray);
        rcvPlan = findViewById(R.id.rcvPlan);
    }
}
