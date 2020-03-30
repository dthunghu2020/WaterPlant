package com.hungdt.waterplan.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.hungdt.waterplan.KEY;
import com.hungdt.waterplan.R;
import com.hungdt.waterplan.database.DBHelper;
import com.hungdt.waterplan.model.Plant;
import com.hungdt.waterplan.view.adater.PlantAdapter;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private ImageView imgSetting;
    private CircleImageView imgPlus, imgWater, imgFertilizer, imgPrune, imgSpray;
    private RecyclerView rcvPlan;
    private PlantAdapter plantAdapter;

    private static final int REQUEST_CODE_ADD_PLANT = 100;
    private static final int REQUEST_CODE_EDIT_PLANT = 101;

    private int positionSave;
    private String typeOfCare = KEY.TYPE_CREATE;

    private List<Plant> plants = new ArrayList<>();
    private List<String> cbPlantId = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initView();

        plants.addAll(DBHelper.getInstance(this).getAllPlant());
        plantAdapter = new PlantAdapter(this, plants);
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
                if (typeOfCare.equals(KEY.TYPE_CREATE)) {
                    Intent intent = new Intent(MainActivity.this, AddPlanActivity.class);
                    intent.putExtra(KEY.TYPE, KEY.TYPE_CREATE);
                    startActivityForResult(intent, REQUEST_CODE_ADD_PLANT);
                }
                if (typeOfCare.equals(KEY.TYPE_WATER)) {
                    //for thay đổi dữ liệu care, cập nhật lại check trong plants.
                    for (int i = 0; i < plants.size(); i++) {
                        if (plants.get(i).isTicked()) {
                            //todo cập nhật lại careCircle
                            plants.get(i).setTicked(!plants.get(i).isTicked());
                        }
                    }
                    imgPlus.setImageDrawable(getDrawable(R.drawable.ic_plus));
                    visibleViewCare();
                    typeOfCare = KEY.TYPE_CREATE;
                    plantAdapter.disableCheckBox();
                    plantAdapter.notifyDataSetChanged();
                }
            }
        });

        imgWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeOfCare = KEY.TYPE_WATER;
                invisibleViewCare();
                imgPlus.setImageDrawable(getDrawable(R.drawable.ic_water));
                plantAdapter.enableCheckBox();
                plantAdapter.notifyDataSetChanged();
            }
        });

        imgFertilizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
            }
        });

        imgPrune.setOnClickListener(new View.OnClickListener() {
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

        plantAdapter.setOnPlantItemClickListener(new PlantAdapter.OnPlantItemClickListener() {

            @Override
            public void OnItemClicked(int position, boolean checkBox) {
                if (!checkBox) {
                    positionSave = position;
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(MainActivity.this, AddPlanActivity.class);
                    bundle.putSerializable(KEY.PLANT, plants.get(position));
                    intent.putExtra(KEY.TYPE, KEY.TYPE_EDIT);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, REQUEST_CODE_EDIT_PLANT);
                }
            }
        });
    }

    private void invisibleViewCare() {
        imgWater.setVisibility(View.GONE);
        imgFertilizer.setVisibility(View.GONE);
        imgSpray.setVisibility(View.GONE);
        imgPrune.setVisibility(View.GONE);
    }

    private void visibleViewCare() {
        imgWater.setVisibility(View.VISIBLE);
        imgFertilizer.setVisibility(View.VISIBLE);
        imgSpray.setVisibility(View.VISIBLE);
        imgPrune.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ADD_PLANT) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                String typeResult = data.getStringExtra(KEY.TYPE_RESULT);
                assert typeResult != null;
                if (typeResult.equals(KEY.CREATE)) {
                    Plant plant = DBHelper.getInstance(this).getLastPlant();
                    plants.add(plant);
                    plantAdapter.notifyItemChanged(plants.size() - 1);
                }
            }
        }
        if (requestCode == REQUEST_CODE_EDIT_PLANT) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                String typeResult = data.getStringExtra(KEY.TYPE_RESULT);
                assert typeResult != null;
                if (typeResult.equals(KEY.DELETE)) {
                    plants.remove(positionSave);
                    plantAdapter.notifyDataSetChanged();
                }
                if (typeResult.equals(KEY.UPDATE)) {
                    Plant plant = DBHelper.getInstance(this).getOnePlant(plants.get(positionSave).getPlantID());
                    plants.set(positionSave, plant);
                    plantAdapter.notifyItemChanged(positionSave);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (typeOfCare.equals(KEY.TYPE_CREATE)) {
            super.onBackPressed();
        }
        if (!typeOfCare.equals(KEY.TYPE_CREATE)) {
            setDefaultCheck();
            visibleViewCare();
            typeOfCare = KEY.TYPE_CREATE;
            imgPlus.setImageDrawable(getDrawable(R.drawable.ic_plus));
            plantAdapter.disableCheckBox();
            plantAdapter.notifyDataSetChanged();
        }
    }

    private void setDefaultCheck() {
        for (int i = 0; i < plants.size(); i++) {
            if (plants.get(i).isTicked()) {
                plants.get(i).setTicked(!plants.get(i).isTicked());
            }
        }
    }

    private void initView() {
        imgSetting = findViewById(R.id.imgSetting);
        imgPlus = findViewById(R.id.imgPlus);
        imgWater = findViewById(R.id.imgWater);
        imgFertilizer = findViewById(R.id.imgFertilizer);
        imgPrune = findViewById(R.id.imgPrune);
        imgSpray = findViewById(R.id.imgSpray);
        rcvPlan = findViewById(R.id.rcvPlan);
    }
}
