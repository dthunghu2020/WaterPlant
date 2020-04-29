package com.hungdt.waterplant.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hungdt.waterplant.Ads;
import com.hungdt.waterplant.Helper;
import com.hungdt.waterplant.KEY;
import com.hungdt.waterplant.MySetting;
import com.hungdt.waterplant.R;
import com.hungdt.waterplant.database.DBHelper;
import com.hungdt.waterplant.dataset.Constant;
import com.hungdt.waterplant.model.Event;
import com.hungdt.waterplant.model.Plant;
import com.hungdt.waterplant.model.Remind;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class EditPlanActivity extends AppCompatActivity {

    private ImageView imgBack, imgDeletePlant, imgPlantAvatar, flag1, flag2, flag3;
    private TextView txtTitle, txtDisableWater, txtDisablePrune, txtDisableSpray, txtDisableFertilize, txtWaterCircle, txtWaterTime, txtPruneCircle, txtPruneTime, txtSprayCircle, txtSprayTime, txtFertilizeCircle, txtFertilizeTime, txtEvent1, txtEvent2, txtEvent3, txtEventDay1, txtEventDay2, txtEventDay3, txtTutorial;
    private Button btnSave;
    private LinearLayout llWaterRemind, llWater, llPruneRemind, llPrune, llFertilizeRemind, llFertilize, llSprayRemind, llSpray;
    private EditText edtPlanName, edtPlanNote;
    private Switch swWater, swPrune, swSpray, swFertilization;
    private ConstraintLayout event1, event2, event3;

    private Plant plant;
    private List<Remind> saveRemind = new ArrayList<>();
    private List<Remind> reminds = new ArrayList<>();
    private List<Event> saveEvents = new ArrayList<>();
    private List<Event> events = new ArrayList<>();

    final Calendar calendar = Calendar.getInstance();

    private static final int CAMERA_PERMISSION_CODE = 200;
    private static final int GALLERY_PERMISSION_CODE = 201;
    private static final int CAMERA_REQUEST_CODE = 202;
    private static final int GALLERY_REQUEST_CODE = 203;
    private String currentPhotoPath = "";
    private String type = "";
    private String calenderSave = "";
    private boolean existEvent1 = false;
    private boolean existEvent2 = false;
    private boolean existEvent3 = false;
    private boolean cameraPermission = false;
    private boolean galleryPermission = false;
    private String idRemindWaterSave = "";
    private String idRemindPruneSave = "";
    private String idRemindSpraySave = "";
    private String idRemindFertilizationSave = "";

    private InterstitialAd ggInterstitialAd;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_plant);
        Helper.setColorStatusBar(this, R.color.status_bar);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        initView();

        //Inter
        initGgInterstitialAd();

        //Banner
        int random = new Random().nextInt(100);
        if (random <= 70) {
            Ads.initBanner((LinearLayout) findViewById(R.id.llBanner), this);
        } else {
            Ads.initBannerFB((LinearLayout) findViewById(R.id.llBanner), this);
        }

        calenderSave = getInstantDateTime();

        invisibleRemindData();

        //Get and set Data View
        Intent intent = getIntent();
        type = intent.getStringExtra(KEY.TYPE);
        assert type != null;
        if (type.equals(KEY.TYPE_CREATE)) {
            txtTitle.setText("Add new plant");
            imgDeletePlant.setVisibility(View.GONE);
            llWater.setVisibility(View.INVISIBLE);
            llPrune.setVisibility(View.INVISIBLE);
            llSpray.setVisibility(View.INVISIBLE);
            llFertilize.setVisibility(View.INVISIBLE);

        } else if (type.equals(KEY.TYPE_EDIT)) {
            txtTutorial.setVisibility(View.GONE);
            txtTitle.setText("Edit plant");
            plant = (Plant) intent.getSerializableExtra(KEY.PLANT);
            if (plant != null) {
                saveRemind.addAll(plant.getReminds());
                reminds.addAll(plant.getReminds());
                saveEvents.addAll(plant.getEvents());
                events.addAll(plant.getEvents());
                edtPlanName.setText(plant.getPlantName());
                edtPlanNote.setText(plant.getPlantNote());
                if (!plant.getPlantImage().equals("")) {
                    currentPhotoPath = plant.getPlantImage();
                    Glide
                            .with(this)
                            .load(plant.getPlantImage())
                            .placeholder(R.drawable.ava_default_tree)
                            .into(imgPlantAvatar);
                }
                for (int i = 0; i < reminds.size(); i++) {
                    if (reminds.get(i).getRemindType().equals(KEY.TYPE_WATER)) {
                        txtDisableWater.setVisibility(View.INVISIBLE);
                        llWater.setVisibility(View.VISIBLE);
                        txtWaterCircle.setText("Every " + reminds.get(i).getCareCycle() + " days");
                        txtWaterTime.setText(reminds.get(i).getRemindTime());
                        swWater.setChecked(true);
                    } else if (reminds.get(i).getRemindType().equals(KEY.TYPE_PRUNE)) {
                        txtDisablePrune.setVisibility(View.INVISIBLE);
                        llPrune.setVisibility(View.VISIBLE);
                        txtPruneCircle.setText("Every " + reminds.get(i).getCareCycle() + " days");
                        txtPruneTime.setText(reminds.get(i).getRemindTime());
                        swPrune.setChecked(true);
                    } else if (reminds.get(i).getRemindType().equals(KEY.TYPE_SPRAY)) {
                        txtDisableSpray.setVisibility(View.INVISIBLE);
                        llSpray.setVisibility(View.VISIBLE);
                        txtSprayCircle.setText("Every " + reminds.get(i).getCareCycle() + " days");
                        txtSprayTime.setText(reminds.get(i).getRemindTime());
                        swSpray.setChecked(true);
                    } else if (reminds.get(i).getRemindType().equals(KEY.TYPE_FERTILIZER)) {
                        txtDisableFertilize.setVisibility(View.INVISIBLE);
                        llFertilize.setVisibility(View.VISIBLE);
                        txtFertilizeCircle.setText("Every " + reminds.get(i).getCareCycle() + " days");
                        txtFertilizeTime.setText(reminds.get(i).getRemindTime());
                        swFertilization.setChecked(true);
                    }
                }
                for (int i = 0; i < events.size(); i++) {
                    if (events.get(i).getEventPosition().equals("1")) {
                        existEvent1 = true;
                        flag1.setVisibility(View.INVISIBLE);
                        txtEvent1.setVisibility(View.VISIBLE);
                        txtEventDay1.setVisibility(View.VISIBLE);
                        txtEvent1.setText(events.get(i).getEventName());
                        txtEventDay1.setText(events.get(i).getEventDate());
                    } else if (events.get(i).getEventPosition().equals("2")) {
                        existEvent2 = true;
                        flag2.setVisibility(View.INVISIBLE);
                        txtEvent2.setVisibility(View.VISIBLE);
                        txtEventDay2.setVisibility(View.VISIBLE);
                        txtEvent2.setText(events.get(i).getEventName());
                        txtEventDay2.setText(events.get(i).getEventDate());
                    } else if (events.get(i).getEventPosition().equals("3")) {
                        existEvent3 = true;
                        flag3.setVisibility(View.INVISIBLE);
                        txtEvent3.setVisibility(View.VISIBLE);
                        txtEventDay3.setVisibility(View.VISIBLE);
                        txtEvent3.setText(events.get(i).getEventName());
                        txtEventDay3.setText(events.get(i).getEventDate());
                    }
                }
            }
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imgDeletePlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeletePlantDialog();
            }
        });

        llWaterRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (swWater.isChecked()) {
                    int position = 0;
                    for (int i = 0; i < reminds.size(); i++) {
                        if (reminds.get(i).getRemindType().equals(KEY.TYPE_WATER)) {
                            position = i;
                        }
                    }
                    openEditRemind(reminds.get(position).getRemindType(), reminds.get(position), position);
                }
            }
        });
        llPruneRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (swPrune.isChecked()) {
                    int position = 0;
                    for (int i = 0; i < reminds.size(); i++) {
                        if (reminds.get(i).getRemindType().equals(KEY.TYPE_PRUNE)) {
                            position = i;
                        }
                    }
                    openEditRemind(reminds.get(position).getRemindType(), reminds.get(position), position);
                }
            }
        });
        llSprayRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (swSpray.isChecked()) {
                    int position = 0;
                    for (int i = 0; i < reminds.size(); i++) {
                        if (reminds.get(i).getRemindType().equals(KEY.TYPE_SPRAY)) {
                            position = i;
                        }
                    }
                    openEditRemind(reminds.get(position).getRemindType(), reminds.get(position), position);
                }
            }
        });
        llFertilizeRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (swFertilization.isChecked()) {
                    int position = 0;
                    for (int i = 0; i < reminds.size(); i++) {
                        if (reminds.get(i).getRemindType().equals(KEY.TYPE_FERTILIZER)) {
                            position = i;
                        }
                    }
                    openEditRemind(reminds.get(position).getRemindType(), reminds.get(position), position);
                }
            }
        });

        swWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (swWater.isChecked()) {
                    llWater.setVisibility(View.VISIBLE);
                    txtDisableWater.setVisibility(View.INVISIBLE);
                    if (type.equals(KEY.TYPE_CREATE)) {
                        reminds.add(new Remind("0", KEY.TYPE_WATER, getInstantDateTime(), "07:00", "5"));
                    } else {
                        reminds.add(new Remind(idRemindWaterSave, KEY.TYPE_WATER, getInstantDateTime(), "07:00", "5"));
                    }

                    txtWaterCircle.setText("Every 5 days");
                    txtWaterTime.setText("07:00");
                } else {
                    int position = 0;
                    for (int i = 0; i < reminds.size(); i++) {
                        if (reminds.get(i).getRemindType().equals(KEY.TYPE_WATER)) {
                            position = i;
                            idRemindWaterSave = reminds.get(i).getRemindID();
                        }
                    }
                    reminds.remove(position);
                    llWater.setVisibility(View.INVISIBLE);
                    txtDisableWater.setVisibility(View.VISIBLE);
                }
            }
        });

        swPrune.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (swPrune.isChecked()) {
                    if (type.equals(KEY.TYPE_CREATE)) {
                        reminds.add(new Remind("0", KEY.TYPE_PRUNE, getInstantDateTime(), "07:00", "5"));
                    } else {
                        reminds.add(new Remind(idRemindPruneSave, KEY.TYPE_PRUNE, getInstantDateTime(), "07:00", "5"));
                    }
                    llPrune.setVisibility(View.VISIBLE);
                    txtDisablePrune.setVisibility(View.INVISIBLE);
                    txtPruneCircle.setText("Every 5 days");
                    txtPruneTime.setText("07:00");
                } else {
                    int position = 0;
                    for (int i = 0; i < reminds.size(); i++) {
                        if (reminds.get(i).getRemindType().equals(KEY.TYPE_PRUNE)) {
                            position = i;
                            idRemindPruneSave = reminds.get(i).getRemindID();
                        }
                    }
                    reminds.remove(position);
                    llPrune.setVisibility(View.INVISIBLE);
                    txtDisablePrune.setVisibility(View.VISIBLE);
                }
            }
        });

        swSpray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (swSpray.isChecked()) {
                    if (type.equals(KEY.TYPE_CREATE)) {
                        reminds.add(new Remind("0", KEY.TYPE_SPRAY, getInstantDateTime(), "07:00", "5"));
                    } else {
                        reminds.add(new Remind(idRemindSpraySave, KEY.TYPE_SPRAY, getInstantDateTime(), "07:00", "5"));
                    }
                    llSpray.setVisibility(View.VISIBLE);
                    txtDisableSpray.setVisibility(View.INVISIBLE);
                    txtSprayCircle.setText("Every 5 days");
                    txtSprayTime.setText("07:00");
                } else {
                    int position = 0;
                    for (int i = 0; i < reminds.size(); i++) {
                        if (reminds.get(i).getRemindType().equals(KEY.TYPE_SPRAY)) {
                            position = i;
                            idRemindSpraySave = reminds.get(i).getRemindID();
                        }
                    }
                    reminds.remove(position);
                    llSpray.setVisibility(View.INVISIBLE);
                    txtDisableSpray.setVisibility(View.VISIBLE);
                }
            }
        });

        swFertilization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (swFertilization.isChecked()) {
                    if (type.equals(KEY.TYPE_CREATE)) {
                        reminds.add(new Remind("0", KEY.TYPE_FERTILIZER, getInstantDateTime(), "07:00", "5"));
                    } else {
                        reminds.add(new Remind(idRemindFertilizationSave, KEY.TYPE_FERTILIZER, getInstantDateTime(), "07:00", "5"));
                    }
                    llFertilize.setVisibility(View.VISIBLE);
                    txtDisableFertilize.setVisibility(View.INVISIBLE);
                    txtFertilizeCircle.setText("Every 5 days");
                    txtFertilizeTime.setText("07:00");
                } else {
                    int position = 0;
                    for (int i = 0; i < reminds.size(); i++) {
                        if (reminds.get(i).getRemindType().equals(KEY.TYPE_FERTILIZER)) {
                            position = i;
                            idRemindFertilizationSave = reminds.get(i).getRemindID();
                        }
                    }
                    reminds.remove(position);
                    llFertilize.setVisibility(View.INVISIBLE);
                    txtDisableFertilize.setVisibility(View.VISIBLE);
                }
            }
        });

        event1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (existEvent1) {
                    openEventDialog(1, KEY.UPDATE);
                } else {
                    openEventDialog(1, KEY.CREATE);
                }
            }
        });

        event2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (existEvent2) {
                    openEventDialog(2, KEY.UPDATE);
                } else {
                    openEventDialog(2, KEY.CREATE);
                }
            }
        });

        event3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (existEvent3) {
                    openEventDialog(3, KEY.UPDATE);
                } else {
                    openEventDialog(3, KEY.CREATE);
                }
            }
        });


        final MediaPlayer mpClick = MediaPlayer.create(this, R.raw.click);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPlanName.getText().toString().isEmpty()) {
                    Toast.makeText(EditPlanActivity.this, "Give the plant a name!", Toast.LENGTH_SHORT).show();
                } else {
                    if (type.equals(KEY.TYPE_CREATE)) {
                        if (edtPlanNote.getText().toString().isEmpty()) {
                            DBHelper.getInstance(EditPlanActivity.this).addPlant(edtPlanName.getText().toString(), currentPhotoPath, "");
                        } else {
                            DBHelper.getInstance(EditPlanActivity.this).addPlant(edtPlanName.getText().toString(), currentPhotoPath, edtPlanNote.getText().toString());
                        }
                        if (reminds != null) {
                            String plantID = DBHelper.getInstance(EditPlanActivity.this).getLastPlanID();
                            for (int i = 0; i < reminds.size(); i++) {
                                Remind remind = reminds.get(i);
                                DBHelper.getInstance(EditPlanActivity.this).addRemind(plantID, remind.getRemindType(), remind.getRemindCreateDT(), remind.getRemindTime(), remind.getCareCycle());
                            }
                            reminds.clear();
                        }
                        if (events != null) {
                            String plantID = DBHelper.getInstance(EditPlanActivity.this).getLastPlanID();
                            for (int i = 0; i < events.size(); i++) {
                                DBHelper.getInstance(EditPlanActivity.this).addEvent(plantID, events.get(i).getEventName(), events.get(i).getEventDate(), events.get(i).getEventPosition());
                            }
                            events.clear();
                        }
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(KEY.TYPE_RESULT, KEY.CREATE);
                        setResult(Activity.RESULT_OK, resultIntent);
                    }
                    if (type.equals(KEY.TYPE_EDIT)) {
                        if (edtPlanNote.getText().toString().isEmpty()) {
                            DBHelper.getInstance(EditPlanActivity.this).updatePlant(plant.getPlantID(), edtPlanName.getText().toString(), currentPhotoPath, "");
                        } else {
                            DBHelper.getInstance(EditPlanActivity.this).updatePlant(plant.getPlantID(), edtPlanName.getText().toString(), currentPhotoPath, edtPlanNote.getText().toString());
                        }
                        if (reminds.size() == 0) {
                            DBHelper.getInstance(EditPlanActivity.this).deletePlantRemind(plant.getPlantID());
                        } else {
                            if (saveRemind.size() == 0) {
                                for (int i = 0; i < reminds.size(); i++) {
                                    DBHelper.getInstance(EditPlanActivity.this)
                                            .addRemind(plant.getPlantID(),
                                                    reminds.get(i).getRemindType(),
                                                    reminds.get(i).getRemindCreateDT(),
                                                    reminds.get(i).getRemindTime(),
                                                    reminds.get(i).getCareCycle());
                                }
                            } else {
                                List<String> idSave = new ArrayList<>();
                                for (int i = 0; i < saveRemind.size(); i++) {
                                    idSave.add(saveRemind.get(i).getRemindID());
                                }

                                for (int i = 0; i < reminds.size(); i++) {
                                    boolean check = true;
                                    for (int j = 0; j < saveRemind.size(); j++) {
                                        if (reminds.get(i).getRemindType().equals(saveRemind.get(j).getRemindType())) {
                                            //nếu trùng thì update
                                            DBHelper.getInstance(EditPlanActivity.this)
                                                    .updateRemind(reminds.get(i).getRemindID(),
                                                            reminds.get(i).getRemindCreateDT(),
                                                            reminds.get(i).getRemindTime(),
                                                            reminds.get(i).getCareCycle());
                                            String id = saveRemind.get(j).getRemindID();
                                            idSave.remove(id);
                                            check = false;
                                        }
                                    }
                                    if (check) {
                                        DBHelper.getInstance(EditPlanActivity.this)
                                                .addRemind(plant.getPlantID(),
                                                        reminds.get(i).getRemindType(),
                                                        reminds.get(i).getRemindCreateDT(),
                                                        reminds.get(i).getRemindTime(),
                                                        reminds.get(i).getCareCycle());
                                    }
                                }
                                if (idSave.size() > 0) {
                                    for (int i = 0; i < idSave.size(); i++) {
                                        DBHelper.getInstance(EditPlanActivity.this).deleteOneRemind(idSave.get(i));
                                    }
                                }
                            }
                            saveRemind.clear();
                            reminds.clear();
                        }
                        if (events.size() == 0) {
                            DBHelper.getInstance(EditPlanActivity.this).deletePlantEvent(plant.getPlantID());
                        } else {
                            if (saveEvents.size() == 0) {
                                for (int i = 0; i < events.size(); i++) {
                                    DBHelper.getInstance(EditPlanActivity.this)
                                            .addEvent(plant.getPlantID(),
                                                    events.get(i).getEventName(),
                                                    events.get(i).getEventDate(),
                                                    events.get(i).getEventPosition());
                                }
                            } else {
                                List<String> idSave = new ArrayList<>();
                                for (int i = 0; i < saveEvents.size(); i++) {
                                    idSave.add(saveEvents.get(i).getEventId());
                                }

                                for (int i = 0; i < events.size(); i++) {
                                    boolean check = true;
                                    for (int j = 0; j < saveEvents.size(); j++) {
                                        if (events.get(i).getEventPosition().equals(saveEvents.get(j).getEventPosition())) {
                                            //nếu trùng thì update
                                            DBHelper.getInstance(EditPlanActivity.this)
                                                    .updateEvent(events.get(i).getEventId(),
                                                            events.get(i).getEventName(),
                                                            events.get(i).getEventDate());
                                            String id = saveEvents.get(j).getEventId();
                                            idSave.remove(id);
                                            check = false;
                                        }
                                    }
                                    if (check) {
                                        DBHelper.getInstance(EditPlanActivity.this)
                                                .addEvent(plant.getPlantID(),
                                                        events.get(i).getEventName(),
                                                        events.get(i).getEventDate(),
                                                        events.get(i).getEventPosition());
                                    }
                                }
                                if (idSave.size() > 0) {
                                    for (int i = 0; i < idSave.size(); i++) {
                                        DBHelper.getInstance(EditPlanActivity.this).deleteOneEvent(idSave.get(i));
                                    }
                                }
                            }
                            saveEvents.clear();
                            events.clear();
                        }
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(KEY.TYPE_RESULT, KEY.UPDATE);
                        setResult(Activity.RESULT_OK, resultIntent);
                    }
                    ggInterstitialAd.show();
                    finish();
                }
                mpClick.start();
            }
        });

        //Click chụp ảnh
        imgPlantAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGetPictureDialog();
            }
        });

    }

    private void invisibleRemindData() {
        llWater.setVisibility(View.INVISIBLE);
        llSpray.setVisibility(View.INVISIBLE);
        llPrune.setVisibility(View.INVISIBLE);
        llFertilize.setVisibility(View.INVISIBLE);
        txtEvent1.setVisibility(View.INVISIBLE);
        txtEvent2.setVisibility(View.INVISIBLE);
        txtEvent3.setVisibility(View.INVISIBLE);
        txtEventDay1.setVisibility(View.INVISIBLE);
        txtEventDay2.setVisibility(View.INVISIBLE);
        txtEventDay3.setVisibility(View.INVISIBLE);
    }

    private void initGgInterstitialAd() {
        try {
            //Inter ads : Quảng cáo xen kẽ
            ggInterstitialAd = new InterstitialAd(this);
            //Truyền ID
            ggInterstitialAd.setAdUnitId(getString(R.string.INTER_G));
            // xử lí action
            ggInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    //loadAds
                    requestNewInterstitial();
                    Log.e("123", "onAdClosed: Load New ADS");
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    Log.e("123", "onAdLoaded: ");
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    //Fail thì load FB
                    /*try {
                        if (isGgPriority) initFbInterstitialAd();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                    Log.e("123", "onAdFailedToLoad: Load FB ADS");
                }

                @Override
                public void onAdOpened() {

                }
            });
            //LoadAds
            requestNewInterstitial();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestNewInterstitial() {
        try {
            //Nếu không có gỡ ứng ụng .
            if (!MySetting.isRemoveAds(this)) {
                AdRequest adRequest = null;
                //Check Yêu cầu sự đồng ý của người dùng ở Châu Âu
                if (ConsentInformation.getInstance(this).getConsentStatus().toString().equals(ConsentStatus.PERSONALIZED) ||
                        !ConsentInformation.getInstance(this).isRequestLocationInEeaOrUnknown()) {
                    adRequest = new AdRequest.Builder().build();
                } else {
                    adRequest = new AdRequest.Builder()
                            .addNetworkExtrasBundle(AdMobAdapter.class, Ads.getNonPersonalizedAdsBundle())
                            .build();
                }
                //Load Ads
                ggInterstitialAd.loadAd(adRequest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void askPermissions(String typePermission) {
        if (typePermission.equals("camera")) {
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
                askStoreForCamera();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            }
        }
        if (typePermission.equals("gallery")) {
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY_PERMISSION_CODE);
            } else {
                openStoreGallery();
            }

        }

    }

    private void openStoreGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    private void askStoreForCamera() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            dispatchTakePictureIntent();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(EditPlanActivity.this, "Permission CAMERA_PERMISSION_CODE Granted", Toast.LENGTH_SHORT).show();
                    // do your work here
                    cameraPermission = true;
                    askStoreForCamera();
                } else if (Build.VERSION.SDK_INT >= 23 && !shouldShowRequestPermissionRationale(permissions[0])) {
                    //Toast.makeText(EditPlanActivity.this, "CAMERA_PERMISSION_CODE Go to Settings and Grant the permission to use this feature.", Toast.LENGTH_SHORT).show();
                    // User selected the Never Ask Again Option
                    //Cái này bật setting trên màn hình app
                    openSettingPermissionDialog();
                    //Cái này sẽ bật tab trên hệ thống.
                    /*Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", getPackageName(), null));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);*/
                } else {
                    //Toast.makeText(EditPlanActivity.this, "Permission CAMERA_PERMISSION_CODE Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case GALLERY_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(EditPlanActivity.this, "Permission GALLERY_PERMISSION_CODE Granted", Toast.LENGTH_SHORT).show();
                    galleryPermission = true;
                    openStoreGallery();
                } else if (Build.VERSION.SDK_INT >= 23 && !shouldShowRequestPermissionRationale(permissions[0])) {
                    //Toast.makeText(EditPlanActivity.this, "GALLERY_PERMISSION_CODE Go to Settings and Grant the permission to use this feature.", Toast.LENGTH_SHORT).show();
                    openSettingPermissionDialog();
                } else {
                    //Toast.makeText(EditPlanActivity.this, "Permission GALLERY_PERMISSION_CODE Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        if (cameraPermission && galleryPermission) {
            dispatchTakePictureIntent();
        }
    }

    private void openSettingPermissionDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.setting_permission_dialog);

        final Button btnYes = dialog.findViewById(R.id.btnYes);
        final Button btnNo = dialog.findViewById(R.id.btnCancel);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                Glide
                        .with(this)
                        .load(currentPhotoPath)
                        .placeholder(R.drawable.ava_default_tree)
                        .into(imgPlantAvatar);
                //
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
            }
        }

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                while (cursor.moveToNext()) {
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String yourRealPath = cursor.getString(columnIndex);
                    currentPhotoPath = yourRealPath;
                    Glide.with(this).load(currentPhotoPath).placeholder(R.drawable.ava_default_tree).into(imgPlantAvatar);
                }
                cursor.close();


            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name

        File image = null;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        if (Build.VERSION.SDK_INT < 24) {
            String imageFileName = "JPEG_" + timeStamp + "_";
            //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = image.getAbsolutePath();
        } else {
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "CameraDemo");

            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    image = null;
                }
            }

            image = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
            currentPhotoPath = image.getAbsolutePath();
        }

        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            Uri fileUri = FileProvider.getUriForFile(
                    this,
                    "com.hungdt.android.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
        }
    }

    private void openDeletePlantDialog() {
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.delete_plant_dialog);

        final Button btnYes = dialog.findViewById(R.id.btnYes);
        final Button btnNo = dialog.findViewById(R.id.btnCancel);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBHelper.getInstance(EditPlanActivity.this).deletePlan(plant.getPlantID());
                if (reminds.size() > 0) {
                    DBHelper.getInstance(EditPlanActivity.this).deletePlantRemind(plant.getPlantID());
                }
                if (events.size() > 0) {
                    DBHelper.getInstance(EditPlanActivity.this).deletePlantEvent(plant.getPlantID());
                }
                Intent resultIntent = new Intent();
                resultIntent.putExtra(KEY.TYPE_RESULT, KEY.DELETE);
                setResult(Activity.RESULT_OK, resultIntent);
                dialog.dismiss();
                finish();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void openGetPictureDialog() {
        final BottomSheetDialog getPictureDialog = new BottomSheetDialog(EditPlanActivity.this);
        getPictureDialog.setContentView(R.layout.get_picture_dialog);

        LinearLayout llCamera = getPictureDialog.findViewById(R.id.llCamera);
        LinearLayout llGallery = getPictureDialog.findViewById(R.id.llGallery);


        llCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String typePermission = "camera";
                askPermissions(typePermission);
                getPictureDialog.dismiss();
            }
        });

        llGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String typePermission = "gallery";
                askPermissions(typePermission);
                getPictureDialog.dismiss();
            }
        });

        getPictureDialog.show();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void openEventDialog(final int eventPosition, final String type) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(EditPlanActivity.this);
        bottomSheetDialog.setContentView(R.layout.event_dialog);

        ImageView imgDeleteEvent = bottomSheetDialog.findViewById(R.id.imgDeleteEvent);
        final EditText edtEventDate = bottomSheetDialog.findViewById(R.id.edtEventDate);
        final EditText edtEventName = bottomSheetDialog.findViewById(R.id.edtEventName);
        final Spinner spinnerEvent = bottomSheetDialog.findViewById(R.id.spinnerEvent);
        final ConstraintLayout clEventName = bottomSheetDialog.findViewById(R.id.clEventName);
        final LinearLayout llEvent = bottomSheetDialog.findViewById(R.id.llEvent);
        Button btnOk = bottomSheetDialog.findViewById(R.id.btnOk);
        Button btnCancel = bottomSheetDialog.findViewById(R.id.btnCancel);

        final ArrayList<String> names = new ArrayList<String>();
        names.add("Garter");
        names.add("Weeding, cultivation");
        names.add("Dust removal");
        names.add("Transplant");
        names.add("");
        if (type.equals(KEY.CREATE)) {
            names.set(names.size() - 1, "");
        } else if (type.equals(KEY.UPDATE)) {
            for (int i = 0; i < events.size(); i++) {
                if (Integer.parseInt(events.get(i).getEventPosition()) == eventPosition) {
                    names.set(names.size() - 1, events.get(i).getEventName());
                }
            }
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, names) {
            @Override
            public int getCount() {
                return names.size() - 1;
            }
        };
        spinnerEvent.setAdapter(arrayAdapter);
        spinnerEvent.setSelection(names.size() - 1);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEvent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                edtEventName.setText(names.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        if (type.equals(KEY.CREATE)) {
            imgDeleteEvent.setVisibility(View.GONE);
        } else if (type.equals(KEY.UPDATE)) {
            for (int i = 0; i < events.size(); i++) {
                if (events.get(i).getEventPosition().equals(String.valueOf(eventPosition))) {
                    edtEventName.setText(events.get(i).getEventName());
                    edtEventDate.setText(events.get(i).getEventDate());
                }
            }
        }

        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdfDate = new SimpleDateFormat(Constant.getDateFormat());
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdfDateTime = new SimpleDateFormat(Constant.getDateTimeFormat());
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdfD = new SimpleDateFormat(Constant.getDayFormat());
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdfM = new SimpleDateFormat(Constant.getMonthFormat());
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdfY = new SimpleDateFormat(Constant.getYearFormat());

        Date date;
        try {
            date = sdfDateTime.parse(getInstantDateTime());
            if (date != null) {
                edtEventDate.setText(sdfDate.format(date));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final DatePickerDialog.OnDateSetListener dateDialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                edtEventDate.setText(sdfDate.format(calendar.getTime()));
                Date dateCalendar;
                try {
                    dateCalendar = sdfDateTime.parse(calenderSave);
                    if (dateCalendar != null) {
                        calendar.set(Calendar.YEAR, Integer.parseInt(sdfY.format(dateCalendar)));
                        calendar.set(Calendar.MONTH, Integer.parseInt(sdfM.format(dateCalendar)) - 1);
                        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(sdfD.format(dateCalendar)));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };

        llEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtil.hideKeyboard(bottomSheetDialog.getContext(), Objects.requireNonNull(bottomSheetDialog.getCurrentFocus()));
            }
        });

        edtEventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIUtil.hideKeyboard(bottomSheetDialog.getContext(), Objects.requireNonNull(bottomSheetDialog.getCurrentFocus()));
                new DatePickerDialog(EditPlanActivity.this, dateDialog, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        edtEventDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        edtEventName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        edtEventName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                View view = getCurrentFocus();
                if (view == null) {
                    view = new View(bottomSheetDialog.getLayoutInflater().getContext());
                }
                in.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        clEventName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(v);
                return true;
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (Objects.requireNonNull(edtEventDate.getText()).toString().isEmpty() ||
                        Objects.requireNonNull(edtEventName.getText()).toString().isEmpty()) {
                    Toast.makeText(EditPlanActivity.this, "Please enter all title!", Toast.LENGTH_SHORT).show();
                } else {
                    if (type.equals(KEY.CREATE)) {
                        events.add(new Event(String.valueOf(eventPosition), edtEventName.getText().toString(), edtEventDate.getText().toString(), String.valueOf(eventPosition)));
                    } else if (type.equals(KEY.UPDATE)) {
                        String idEvent = "";
                        for (int i = 0; i < events.size(); i++) {
                            if (events.get(i).getEventPosition().equals(String.valueOf(eventPosition)))
                                idEvent = events.get(i).getEventId();
                        }
                        Event eventEdited = new Event(idEvent, edtEventName.getText().toString(), edtEventDate.getText().toString(), String.valueOf(eventPosition));
                        for (int i = 0; i < events.size(); i++) {
                            if (events.get(i).getEventPosition().equals(String.valueOf(eventPosition)))
                                events.set(i, eventEdited);
                        }
                    }
                    if (eventPosition == 1) {
                        flag1.setVisibility(View.INVISIBLE);
                        txtEvent1.setVisibility(View.VISIBLE);
                        txtEventDay1.setVisibility(View.VISIBLE);
                        txtEvent1.setText(edtEventName.getText().toString());
                        txtEventDay1.setText(edtEventDate.getText().toString());
                        existEvent1 = true;
                    }
                    if (eventPosition == 2) {
                        flag2.setVisibility(View.INVISIBLE);
                        txtEvent2.setVisibility(View.VISIBLE);
                        txtEventDay2.setVisibility(View.VISIBLE);
                        txtEvent2.setText(edtEventName.getText().toString());
                        txtEventDay2.setText(edtEventDate.getText().toString());
                        existEvent2 = true;
                    }
                    if (eventPosition == 3) {
                        flag3.setVisibility(View.INVISIBLE);
                        txtEvent3.setVisibility(View.VISIBLE);
                        txtEventDay3.setVisibility(View.VISIBLE);
                        txtEvent3.setText(edtEventName.getText().toString());
                        txtEventDay3.setText(edtEventDate.getText().toString());
                        existEvent3 = true;
                    }
                    bottomSheetDialog.dismiss();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        imgDeleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int positionDelete = 0;
                for (int i = 0; i < events.size(); i++) {
                    if (events.get(i).getEventPosition().equals(String.valueOf(eventPosition))) {
                        positionDelete = i;
                    }
                }
                events.remove(positionDelete);
                if (eventPosition == 1) {
                    flag1.setVisibility(View.VISIBLE);
                    txtEvent1.setVisibility(View.INVISIBLE);
                    txtEventDay1.setVisibility(View.INVISIBLE);
                    existEvent1 = false;
                } else if (eventPosition == 2) {
                    flag2.setVisibility(View.VISIBLE);
                    txtEvent2.setVisibility(View.INVISIBLE);
                    txtEventDay2.setVisibility(View.INVISIBLE);
                    existEvent2 = false;
                } else if (eventPosition == 3) {
                    flag3.setVisibility(View.VISIBLE);
                    txtEvent3.setVisibility(View.INVISIBLE);
                    txtEventDay3.setVisibility(View.INVISIBLE);
                    existEvent3 = false;
                }
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }

    private void openEditRemind(final String type, final Remind remind, final int position) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(EditPlanActivity.this);
        bottomSheetDialog.setContentView(R.layout.edit_remind_bottomsheet_dialog);

        final EditText edtLastTime = bottomSheetDialog.findViewById(R.id.edtLastTime);
        final EditText edtRemindDay = bottomSheetDialog.findViewById(R.id.edtRemindDay);
        final EditText edtRemindTime = bottomSheetDialog.findViewById(R.id.edtRemindTime);
        final LinearLayout llRemind = bottomSheetDialog.findViewById(R.id.llRemind);
        ImageView imgTypeRemind = bottomSheetDialog.findViewById(R.id.imgTypeRemind);
        TextView txtTypeRemind = bottomSheetDialog.findViewById(R.id.txtTypeRemind);

        Button btnOk = bottomSheetDialog.findViewById(R.id.btnOk);
        Button btnCancel = bottomSheetDialog.findViewById(R.id.btnCancel);

        if (type.equals(bottomSheetDialog.getContext().getResources().getString(R.string.water))) {
            imgTypeRemind.setBackground(bottomSheetDialog.getContext().getDrawable(R.drawable.ic_watering));
            txtTypeRemind.setText("Watering");
            edtLastTime.setBackground(getDrawable(R.drawable.shape_conner_lightwater));
            edtRemindDay.setBackground(getDrawable(R.drawable.shape_conner_lightwater));
            edtRemindTime.setBackground(getDrawable(R.drawable.shape_conner_lightwater));
        } else if (type.equals(bottomSheetDialog.getContext().getResources().getString(R.string.fertilizer))) {
            imgTypeRemind.setBackground(bottomSheetDialog.getContext().getDrawable(R.drawable.ic_fertilize));
            txtTypeRemind.setText("Fertilize");
            edtLastTime.setBackground(getDrawable(R.drawable.shape_conner_lightfertilize));
            edtRemindDay.setBackground(getDrawable(R.drawable.shape_conner_lightfertilize));
            edtRemindTime.setBackground(getDrawable(R.drawable.shape_conner_lightfertilize));
        } else if (type.equals(bottomSheetDialog.getContext().getResources().getString(R.string.spray))) {
            imgTypeRemind.setBackground(bottomSheetDialog.getContext().getDrawable(R.drawable.ic_spray));
            txtTypeRemind.setText("Spray");
            edtLastTime.setBackground(getDrawable(R.drawable.shape_conner_lightspray));
            edtRemindDay.setBackground(getDrawable(R.drawable.shape_conner_lightspray));
            edtRemindTime.setBackground(getDrawable(R.drawable.shape_conner_lightspray));
        } else if (type.equals(bottomSheetDialog.getContext().getResources().getString(R.string.prune))) {
            imgTypeRemind.setBackground(bottomSheetDialog.getContext().getDrawable(R.drawable.ic_prune));
            txtTypeRemind.setText("Prune");
            edtLastTime.setBackground(getDrawable(R.drawable.shape_conner_lightprune));
            edtRemindDay.setBackground(getDrawable(R.drawable.shape_conner_lightprune));
            edtRemindTime.setBackground(getDrawable(R.drawable.shape_conner_lightprune));
        }

        edtRemindDay.setText(remind.getCareCycle());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfTime = new SimpleDateFormat(Constant.getTimeFormat());
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdfDate = new SimpleDateFormat(Constant.getDateFormat());
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdfDateTime = new SimpleDateFormat(Constant.getDateTimeFormat());
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdfD = new SimpleDateFormat(Constant.getDayFormat());
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdfM = new SimpleDateFormat(Constant.getMonthFormat());
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdfY = new SimpleDateFormat(Constant.getYearFormat());
        Date date;
        try {
            date = sdfTime.parse(remind.getRemindTime());
            if (date != null) {
                edtRemindTime.setText(sdfTime.format(date));
            }
            date = sdfDateTime.parse(remind.getRemindCreateDT());
            if (date != null) {
                edtLastTime.setText(sdfDate.format(date));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final DatePickerDialog.OnDateSetListener dateDialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                edtLastTime.setText(sdfDate.format(calendar.getTime()));
                Date dateCalendar;
                try {
                    dateCalendar = sdfDateTime.parse(calenderSave);
                    if (dateCalendar != null) {
                        calendar.set(Calendar.YEAR, Integer.parseInt(sdfY.format(dateCalendar)));
                        calendar.set(Calendar.MONTH, Integer.parseInt(sdfM.format(dateCalendar)) - 1);
                        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(sdfD.format(dateCalendar)));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };

        llRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtil.hideKeyboard(bottomSheetDialog.getContext(), Objects.requireNonNull(bottomSheetDialog.getCurrentFocus()));
            }
        });

        edtLastTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIUtil.hideKeyboard(bottomSheetDialog.getContext(), Objects.requireNonNull(bottomSheetDialog.getCurrentFocus()));
                new DatePickerDialog(EditPlanActivity.this, dateDialog, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        edtLastTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        edtRemindTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtil.hideKeyboard(bottomSheetDialog.getContext(), Objects.requireNonNull(bottomSheetDialog.getCurrentFocus()));
                showTimeDialog(edtRemindTime);
            }
        });
        edtRemindTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        edtRemindDay.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (Objects.requireNonNull(edtLastTime.getText()).toString().isEmpty() ||
                        Objects.requireNonNull(edtRemindDay.getText()).toString().isEmpty() ||
                        Objects.requireNonNull(edtRemindTime.getText()).toString().isEmpty()) {
                    Toast.makeText(EditPlanActivity.this, "Please enter all title!", Toast.LENGTH_SHORT).show();
                } else {
                    Remind remindEdited = new Remind(remind.getRemindID(), remind.getRemindType(), edtRemindTime.getText().toString() + "-" + edtLastTime.getText().toString(), edtRemindTime.getText().toString(), edtRemindDay.getText().toString());
                    reminds.set(position, remindEdited);
                    if (type.equals(KEY.TYPE_WATER)) {
                        txtWaterCircle.setText("Every " + remindEdited.getCareCycle() + " days");
                        txtWaterTime.setText(remindEdited.getRemindTime());
                    } else if (type.equals(KEY.TYPE_SPRAY)) {
                        txtSprayCircle.setText("Every " + remindEdited.getCareCycle() + " days");
                        txtSprayTime.setText(remindEdited.getRemindTime());
                    } else if (type.equals(KEY.TYPE_PRUNE)) {
                        txtPruneCircle.setText("Every " + remindEdited.getCareCycle() + " days");
                        txtPruneTime.setText(remindEdited.getRemindTime());
                    } else if (type.equals(KEY.TYPE_FERTILIZER)) {
                        txtFertilizeCircle.setText("Every " + remindEdited.getCareCycle() + " days");
                        txtFertilizeTime.setText(remindEdited.getRemindTime());
                    }
                    bottomSheetDialog.dismiss();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }

    private void showTimeDialog(final EditText edtRemindTime) {
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(EditPlanActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
        }, hour, minute, android.text.format.DateFormat.is24HourFormat(EditPlanActivity.this));
        timePickerDialog.show();
    }

    private String getInstantDateTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(Constant.getDateTimeFormat());
        return sdf.format(calendar.getTime());
    }

    private void initView() {
        txtTitle = findViewById(R.id.txtTitle);
        imgBack = findViewById(R.id.imgBack);
        txtDisableWater = findViewById(R.id.txtDisableWater);
        txtTutorial = findViewById(R.id.txtTutorial);
        txtDisableSpray = findViewById(R.id.txtDisableSpray);
        txtDisablePrune = findViewById(R.id.txtDisablePrune);
        txtDisableFertilize = findViewById(R.id.txtDisableFertilize);
        imgDeletePlant = findViewById(R.id.imgDeletePlant);
        imgPlantAvatar = findViewById(R.id.imgPlantAvatar);
        flag1 = findViewById(R.id.flag1);
        flag2 = findViewById(R.id.flag2);
        flag3 = findViewById(R.id.flag3);
        btnSave = findViewById(R.id.btnSave);
        edtPlanName = findViewById(R.id.edtPlanName);
        edtPlanNote = findViewById(R.id.edtPlanNote);
        llSprayRemind = findViewById(R.id.llSprayRemind);
        llSpray = findViewById(R.id.llSpray);
        llWaterRemind = findViewById(R.id.llWaterRemind);
        llWater = findViewById(R.id.llWater);
        llPruneRemind = findViewById(R.id.llPruneRemind);
        llPrune = findViewById(R.id.llPrune);
        llFertilize = findViewById(R.id.llFertilize);
        llFertilizeRemind = findViewById(R.id.llFertilizeRemind);
        txtFertilizeCircle = findViewById(R.id.txtFertilizeCircle);
        txtWaterCircle = findViewById(R.id.txtWaterCircle);
        txtSprayCircle = findViewById(R.id.txtSprayCircle);
        txtPruneCircle = findViewById(R.id.txtPruneCircle);
        txtPruneTime = findViewById(R.id.txtPruneTime);
        txtWaterTime = findViewById(R.id.txtWaterTime);
        txtSprayTime = findViewById(R.id.txtSprayTime);
        txtFertilizeTime = findViewById(R.id.txtFertilizeTime);
        swWater = findViewById(R.id.swWater);
        swPrune = findViewById(R.id.swPrune);
        swSpray = findViewById(R.id.swSpray);
        swFertilization = findViewById(R.id.swFertilization);
        event1 = findViewById(R.id.event1);
        event2 = findViewById(R.id.event2);
        event3 = findViewById(R.id.event3);
        txtEvent1 = findViewById(R.id.txtEvent1);
        txtEvent2 = findViewById(R.id.txtEvent2);
        txtEvent3 = findViewById(R.id.txtEvent3);
        txtEventDay1 = findViewById(R.id.txtEventDay1);
        txtEventDay2 = findViewById(R.id.txtEventDay2);
        txtEventDay3 = findViewById(R.id.txtEventDay3);
    }

    @Override
    public void onBackPressed() {
        openExitDialog();
    }

    private void openExitDialog() {
        final Dialog exitDialog = new Dialog(this);
        exitDialog.setContentView(R.layout.exit_edit_plant_dialog);

        Button btnYes = exitDialog.findViewById(R.id.btnYes);
        Button btnBack = exitDialog.findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
            }
        });
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
                finish();
                ggInterstitialAd.show();
            }
        });
        Objects.requireNonNull(exitDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        exitDialog.show();

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
