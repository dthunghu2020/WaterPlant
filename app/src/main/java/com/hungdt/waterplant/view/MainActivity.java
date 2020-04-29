package com.hungdt.waterplant.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.hungdt.waterplant.Ads;
import com.hungdt.waterplant.Helper;
import com.hungdt.waterplant.KEY;
import com.hungdt.waterplant.MySetting;
import com.hungdt.waterplant.PlantWorker;
import com.hungdt.waterplant.R;
import com.hungdt.waterplant.database.DBHelper;
import com.hungdt.waterplant.dataset.Constant;
import com.hungdt.waterplant.model.Plant;
import com.hungdt.waterplant.view.adater.PlantAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BillingProcessor.IBillingHandler {
    private ConstraintLayout clWater, clFertilizer, clPrune, clSpray;
    private TextView txtUserName, txtNumberWater, txtNumberPrune, txtNumberSpray, txtNumberFertilize, txtPruneNoti, txtWaterNoti, txtFertilizerNoti, txtSprayNoti, txtPlant, txtGems, txtGarden;
    private ImageView imgPlus, imgMenu, imgToolBar;
    private RecyclerView rcvPlan;
    private PlantAdapter plantAdapter;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private LinearLayout llEmpty;

    private static final int REQUEST_CODE_ADD_PLANT = 100;
    private static final int REQUEST_CODE_EDIT_PLANT = 101;
    private static final int REQUEST_CODE_SETTING = 102;
    private static final int REQUEST_CODE_VIP = 103;

    private int positionSave;
    private int numberOfPlant = 0;
    private int numberWaterNoti = 0;
    private int numberPruneNoti = 0;
    private int numberSprayNoti = 0;
    private int numberFetilizerNoti = 0;
    private int gemReward = 0;
    private int googlePercentage;
    private String typeOfCare = KEY.TYPE_CREATE;
    private boolean readyToPurchase = false;
    private boolean openVipActivity = false;
    private boolean rewardedVideoCompleted = false;
    boolean isRewardGarden = true;

    final Calendar calendar = Calendar.getInstance();

    private List<Plant> plants = new ArrayList<>();

    private RewardedVideoAd videoAds;

    private BillingProcessor bp;

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Helper.setColorStatusBar(this, R.color.status_bar);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        initView();

        //Remote Config
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            //Lấy được data
                            boolean updated = task.getResult();
                            Log.e("123123", "Config params updated: " + updated);
                        } else {
                            Log.e("123123", "Config params FAIL");
                        }
                    }
                });
        MySetting.putConfigGgFb(MainActivity.this, mFirebaseRemoteConfig.getLong("config_gg_fb"));
        googlePercentage = (int) MySetting.getConfigGgFb(MainActivity.this);
        /*Log.e("123123", "onCreate: mFirebaseRemoteConfig " + mFirebaseRemoteConfig.getLong("config_gg_fb"));
        Log.e("123123", "onCreate: MySetting " + MySetting.getConfigGgFb(this));
        Log.e("123123", "onCreate: googlePercentage " + googlePercentage);*/
        final MediaPlayer mpClick = MediaPlayer.create(this, R.raw.click);
        final MediaPlayer mpDone = MediaPlayer.create(this, R.raw.done_2);
        final MediaPlayer mSuccess = MediaPlayer.create(this, R.raw.success);

        //Video
        videoAds = MobileAds.getRewardedVideoAdInstance(this);
        videoAds.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewarded(RewardItem reward) {
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
            }

            @Override
            public void onRewardedVideoAdClosed() {
                loadVideoAds();
                if (rewardedVideoCompleted && isRewardGarden) {
                    openAddPlantActivity();
                    mSuccess.start();
                }
                if (!isRewardGarden) {
                    openGemRewardedDialog();
                    mSuccess.start();
                }
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int errorCode) {
            }

            @Override
            public void onRewardedVideoAdLoaded() {
            }

            @Override
            public void onRewardedVideoAdOpened() {
            }

            @Override
            public void onRewardedVideoStarted() {
            }

            @Override
            public void onRewardedVideoCompleted() {
                if (isRewardGarden) {
                    int count = numberOfPlant + 1;
                    DBHelper.getInstance(MainActivity.this).setNumberOfPlant("" + numberOfPlant, "" + count);
                    numberOfPlant += 1;
                    rewardedVideoCompleted = true;
                    txtGarden.setText(plants.size() + "/" + DBHelper.getInstance(MainActivity.this).getNumberOfPlant());
                } else {
                    int gems = Integer.parseInt(DBHelper.getInstance(MainActivity.this).getGems());
                    gemReward = new Random().nextInt(4) + 1;
                    int plusGem = gemReward + gems;
                    DBHelper.getInstance(MainActivity.this).setNumberGem("" + gems, "" + plusGem);
                    txtGems.setText(DBHelper.getInstance(MainActivity.this).getGems());
                    drawerLayout.closeDrawers();
                }
            }
        });
        loadVideoAds();
        //Mo man VIP
        if (!openVipActivity) {
            Intent intent = new Intent(this, VipActivity.class);
            startActivity(intent);
            openVipActivity = true;
        }


        //Chuc nang
        try {
            bp = BillingProcessor.newBillingProcessor(this, getString(R.string.BASE64), this); // doesn't bind
            bp.initialize(); // binds
        } catch (Exception e) {
            e.printStackTrace();
        }

        String checkUserData = DBHelper.getInstance(this).getUserName();
        if (checkUserData.isEmpty()) {
            DBHelper.getInstance(this).createUserData("Planter", "On", "On", "3", "1", "0");
        }
        numberOfPlant = Integer.parseInt(DBHelper.getInstance(MainActivity.this).getNumberOfPlant());

        //Notification
        Data data = new Data.Builder()
                .putString(KEY.KEY_TASK_DESC, "Send name plant so show") //có thể put nhiều .putString!!!
                //có thể put nhiều .putString!!!
                .build();
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build();

        final PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder
                (PlantWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setInputData(data)
                .build();

        switch (DBHelper.getInstance(this).getNumberWorker()) {
            case "0":
                break;
            case "1":
                WorkManager.getInstance(MainActivity.this).enqueue(periodicWorkRequest);
                DBHelper.getInstance(this).setNumberWorker("1", "2");
                break;
            case "2":
                WorkManager.getInstance(MainActivity.this).enqueue(periodicWorkRequest);
                DBHelper.getInstance(this).setNumberWorker("2", "0");
                break;
        }

        /*WorkManager.getInstance(MainActivity.this).getWorkInfoByIdLiveData(periodicWorkRequest.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        *//*if (workInfo != null) {
                            txtText.setText("");
                            if (workInfo.getState().isFinished()) {
                                Data data = workInfo.getOutputData();
                                String output = data.getString(PlantWorker.KEY_TASK_OUTPUT);
                                txtText.append(output + "\n");
                            }
                            String status = workInfo.getState().name();
                            txtText.append(status + "\n");
                        }*//*
                    }
                });*/

        //toolbar
        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!drawerLayout.isDrawerOpen(Gravity.LEFT)) drawerLayout.openDrawer(Gravity.LEFT);
                else drawerLayout.closeDrawer(Gravity.RIGHT);
            }
        });
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        ConstraintLayout clDailyReward = hView.findViewById(R.id.clDailyReward);
        if (Build.VERSION.SDK_INT < 24) {
            clDailyReward.setVisibility(View.GONE);
            findViewById(R.id.cvGems).setVisibility(View.GONE);
        }
        clDailyReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRewardGarden = false;
                openVideoAdsDialog();
            }
        });


        //Native
        int random = new Random().nextInt(100);
        if (random <= 70) {
            Ads.initNativeGg((FrameLayout) findViewById(R.id.flNativeAds), this, true, true);
            Ads.initNativeGg((FrameLayout) hView.findViewById(R.id.flMenuNativeAds), this, true, true);
        } else {
            Ads.initNativeGg((FrameLayout) findViewById(R.id.flNativeAds), this, true, true);
            Ads.initNativeGg((FrameLayout) hView.findViewById(R.id.flMenuNativeAds), this, true, true);
            //Ads.initNativeFB((FrameLayout) findViewById(R.id.flNativeAds), this, true, true);
            //Ads.initNativeFB((FrameLayout)hView.findViewById(R.id.flMenuNativeAds), this, true, true);
        }

        plants.addAll(DBHelper.getInstance(this).getAllPlant());
        plantAdapter = new PlantAdapter(this, plants);
        rcvPlan.setLayoutManager(new LinearLayoutManager(this));
        rcvPlan.setAdapter(plantAdapter);
        rcvPlan.setFocusable(false);
        Helper.setColorStatusBar(this, R.color.status_bar);

        txtUserName.setText(DBHelper.getInstance(this).getUserName());
        txtGems.setText(DBHelper.getInstance(this).getGems());
        txtGarden.setText(plants.size() + "/" + DBHelper.getInstance(this).getNumberOfPlant());
        viewEmpty();

        //setting notifi
        setNotiView();

        imgPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeOfCare.equals(KEY.TYPE_CREATE)) {
                    if (numberOfPlant == plants.size()) {
                        if (Build.VERSION.SDK_INT >= 24) {
                            isRewardGarden = true;
                            openVideoAdsDialog();
                        } else {
                            int count = numberOfPlant + 1;
                            DBHelper.getInstance(MainActivity.this).setNumberOfPlant("" + numberOfPlant, "" + count);
                            numberOfPlant += 1;
                            openAddPlantActivity();
                            setNotiView();
                        }

                    } else {
                        openAddPlantActivity();
                        setNotiView();
                    }
                    mpClick.start();
                } else {
                    if (typeOfCare.equals(KEY.TYPE_WATER)) {
                        for (int i = 0; i < plants.size(); i++) {
                            if (plants.get(i).isTicked()) {
                                DBHelper.getInstance(MainActivity.this).refreshRemind(plants.get(i).getPlantID(), getInstantDateTime(), KEY.TYPE_WATER);
                                plants.get(i).setTicked(!plants.get(i).isTicked());
                                updateView(i);
                                setNotiView();

                            }
                        }
                    }
                    if (typeOfCare.equals(KEY.TYPE_FERTILIZER)) {
                        for (int i = 0; i < plants.size(); i++) {
                            if (plants.get(i).isTicked()) {
                                DBHelper.getInstance(MainActivity.this).refreshRemind(plants.get(i).getPlantID(), getInstantDateTime(), KEY.TYPE_FERTILIZER);
                                plants.get(i).setTicked(!plants.get(i).isTicked());
                                updateView(i);
                                setNotiView();

                            }
                        }
                    }
                    if (typeOfCare.equals(KEY.TYPE_SPRAY)) {
                        for (int i = 0; i < plants.size(); i++) {
                            if (plants.get(i).isTicked()) {
                                DBHelper.getInstance(MainActivity.this).refreshRemind(plants.get(i).getPlantID(), getInstantDateTime(), KEY.TYPE_SPRAY);
                                plants.get(i).setTicked(!plants.get(i).isTicked());
                                updateView(i);
                                setNotiView();
                            }
                        }
                    }
                    if (typeOfCare.equals(KEY.TYPE_PRUNE)) {
                        for (int i = 0; i < plants.size(); i++) {
                            if (plants.get(i).isTicked()) {
                                DBHelper.getInstance(MainActivity.this).refreshRemind(plants.get(i).getPlantID(), getInstantDateTime(), KEY.TYPE_PRUNE);
                                plants.get(i).setTicked(!plants.get(i).isTicked());
                                updateView(i);
                                setNotiView();
                            }
                        }
                    }
                    txtPlant.setText("My Plant");
                    imgPlus.setImageDrawable(getDrawable(R.drawable.ic_add_plant));
                    imgToolBar.setBackgroundResource(R.drawable.img_water);
                    visibleViewCare();
                    typeOfCare = KEY.TYPE_CREATE;
                    plantAdapter.disableCheckBox();
                    plantAdapter.notifyDataSetChanged();
                    mpDone.start();
                }
            }
        });

        clWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtPlant.setText("Watering");
                typeOfCare = KEY.TYPE_WATER;
                invisibleViewCare();
                imgPlus.setImageDrawable(getDrawable(R.drawable.ic_watering_button));
                plantAdapter.enableCheckBox(typeOfCare);
                plantAdapter.notifyDataSetChanged();
                mpClick.start();
            }
        });

        clFertilizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtPlant.setText("Fertilize");
                typeOfCare = KEY.TYPE_FERTILIZER;
                imgToolBar.setBackgroundResource(R.drawable.img_ferti);
                invisibleViewCare();
                imgPlus.setImageDrawable(getDrawable(R.drawable.ic_fertilize_button));
                plantAdapter.enableCheckBox(typeOfCare);
                plantAdapter.notifyDataSetChanged();
                mpClick.start();
            }
        });

        clPrune.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtPlant.setText("Prune");
                typeOfCare = KEY.TYPE_PRUNE;
                imgToolBar.setBackgroundResource(R.drawable.img_prunes);
                invisibleViewCare();
                imgPlus.setImageDrawable(getDrawable(R.drawable.ic_prune_button));
                plantAdapter.enableCheckBox(typeOfCare);
                plantAdapter.notifyDataSetChanged();
                mpClick.start();
            }
        });

        clSpray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtPlant.setText("Spray");
                typeOfCare = KEY.TYPE_SPRAY;
                imgToolBar.setBackgroundResource(R.drawable.img_sprays);
                invisibleViewCare();
                imgPlus.setImageDrawable(getDrawable(R.drawable.ic_spray_button));
                plantAdapter.enableCheckBox(typeOfCare);
                plantAdapter.notifyDataSetChanged();
                mpClick.start();
            }
        });

        plantAdapter.setOnPlantItemClickListener(new PlantAdapter.OnPlantItemClickListener() {

            @Override
            public void OnItemClicked(int position, boolean checkBox) {
                if (!checkBox) {
                    positionSave = position;
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(MainActivity.this, EditPlanActivity.class);
                    bundle.putSerializable(KEY.PLANT, plants.get(position));
                    intent.putExtra(KEY.TYPE, KEY.TYPE_EDIT);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, REQUEST_CODE_EDIT_PLANT);
                }
            }
        });
    }


    private void openAddPlantActivity() {
        Intent intent = new Intent(MainActivity.this, EditPlanActivity.class);
        intent.putExtra(KEY.TYPE, KEY.TYPE_CREATE);
        startActivityForResult(intent, REQUEST_CODE_ADD_PLANT);
    }

    private void viewEmpty() {
        if (plants.size() > 0) {
            llEmpty.setVisibility(View.GONE);
        } else {
            llEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void loadVideoAds() {
        AdRequest adRequest = null;
        if (ConsentInformation.getInstance(this).getConsentStatus().toString().equals(ConsentStatus.PERSONALIZED) ||
                !ConsentInformation.getInstance(this).isRequestLocationInEeaOrUnknown()) {
            adRequest = new AdRequest.Builder().build();
        } else {
            adRequest = new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, Ads.getNonPersonalizedAdsBundle())
                    .build();
        }

        videoAds.loadAd(getString(R.string.VIDEO_G), adRequest);
        /*mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());*/
    }

    @SuppressLint("SetTextI18n")
    private void openVideoAdsDialog() {
        final Dialog morePlaceDialog = new Dialog(MainActivity.this);
        morePlaceDialog.setContentView(R.layout.video_ads_dialog);

        TextView txtTitle = morePlaceDialog.findViewById(R.id.txtTitle);
        TextView txtBody = morePlaceDialog.findViewById(R.id.txtBody);
        Button btnYes = morePlaceDialog.findViewById(R.id.btnYes);
        Button btnNoThanks = morePlaceDialog.findViewById(R.id.btnBack);

        if (isRewardGarden) {
            txtTitle.setText("More place in Garden");
            txtBody.setText("Do you want to watch a video to get more place in the Garden!?");
        } else {
            txtTitle.setText("Daily Reward");
            txtBody.setText("What an ads video to get more GEM!?");
        }

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoAds.isLoaded()) {
                    videoAds.show();
                    morePlaceDialog.dismiss();
                }
            }
        });
        btnNoThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                morePlaceDialog.dismiss();
            }
        });
        Objects.requireNonNull(morePlaceDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        morePlaceDialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void openGemRewardedDialog() {
        final Dialog gemRewardedDialog = new Dialog(MainActivity.this);
        gemRewardedDialog.setContentView(R.layout.reward_success_dialog);

        TextView txtGemRewarded = gemRewardedDialog.findViewById(R.id.txtGemRewarded);
        Button btnOk = gemRewardedDialog.findViewById(R.id.btnOk);
        ImageView imgBack = gemRewardedDialog.findViewById(R.id.imgBack);

        txtGemRewarded.setText("x " + gemReward);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gemRewardedDialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gemRewardedDialog.dismiss();
            }
        });
        Objects.requireNonNull(gemRewardedDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        gemRewardedDialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void setNotiView() {
        numberFetilizerNoti = 0;
        numberWaterNoti = 0;
        numberPruneNoti = 0;
        numberSprayNoti = 0;
        Date date;
        for (int i = 0; i < plants.size(); i++) {
            Plant plant = plants.get(i);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfDate = new SimpleDateFormat(Constant.getDateFormat());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfDT = new SimpleDateFormat(Constant.getDateTimeFormat());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfDay = new SimpleDateFormat(Constant.getDayFormat());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfMonth = new SimpleDateFormat(Constant.getMonthFormat());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfYear = new SimpleDateFormat(Constant.getYearFormat());

            int countDayInstance = 0;

            try {
                date = sdfDate.parse(getInstantDate());
                if (date != null) {
                    countDayInstance = countND(Integer.parseInt(sdfYear.format(date)),
                            Integer.parseInt(sdfMonth.format(date)),
                            Integer.parseInt(sdfDay.format(date)));
                }
                for (int j = 0; j < plant.getReminds().size(); j++) {
                    date = sdfDT.parse(plant.getReminds().get(j).getRemindCreateDT());
                    if (date != null) {
                        int dayCheck = countND(Integer.parseInt(sdfYear.format(date)),
                                Integer.parseInt(sdfMonth.format(date)),
                                Integer.parseInt(sdfDay.format(date)))
                                + Integer.parseInt(plant.getReminds().get(j).getCareCycle());
                        if (countDayInstance >= dayCheck) {
                            switch (plant.getReminds().get(j).getRemindType()) {
                                case KEY.TYPE_WATER:
                                    numberWaterNoti += 1;
                                    break;
                                case KEY.TYPE_SPRAY:
                                    numberSprayNoti += 1;
                                    break;
                                case KEY.TYPE_PRUNE:
                                    numberPruneNoti += 1;
                                    break;
                                case KEY.TYPE_FERTILIZER:
                                    numberFetilizerNoti += 1;
                                    break;
                            }
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        txtNumberWater.setText("" + numberWaterNoti);
        txtWaterNoti.setText("" + numberWaterNoti);
        txtNumberPrune.setText("" + numberPruneNoti);
        txtPruneNoti.setText("" + numberPruneNoti);
        txtNumberSpray.setText("" + numberSprayNoti);
        txtSprayNoti.setText("" + numberSprayNoti);
        txtNumberFertilize.setText("" + numberFetilizerNoti);
        txtFertilizerNoti.setText("" + numberFetilizerNoti);
    }

    private void invisibleViewCare() {
        clWater.setVisibility(View.GONE);
        clFertilizer.setVisibility(View.GONE);
        clSpray.setVisibility(View.GONE);
        clPrune.setVisibility(View.GONE);
    }

    private void visibleViewCare() {
        clWater.setVisibility(View.VISIBLE);
        clFertilizer.setVisibility(View.VISIBLE);
        clSpray.setVisibility(View.VISIBLE);
        clPrune.setVisibility(View.VISIBLE);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (!bp.handleActivityResult(requestCode, resultCode, data)) {
                super.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (requestCode == REQUEST_CODE_ADD_PLANT) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                String typeResult = data.getStringExtra(KEY.TYPE_RESULT);
                assert typeResult != null;
                if (typeResult.equals(KEY.CREATE)) {
                    Plant plant = DBHelper.getInstance(this).getLastPlant();
                    plants.add(plant);
                    plantAdapter.notifyItemChanged(plants.size() - 1);
                    setNotiView();
                    viewEmpty();
                    txtGarden.setText(plants.size() + "/" + DBHelper.getInstance(this).getNumberOfPlant());
                }
            }
        }
        if (requestCode == REQUEST_CODE_VIP) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                String typeResult = data.getStringExtra(KEY.TYPE_RESULT);
                assert typeResult != null;
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
                    setNotiView();
                    txtGarden.setText(plants.size() + "/" + DBHelper.getInstance(this).getNumberOfPlant());
                }
                if (typeResult.equals(KEY.UPDATE)) {
                    updateView(positionSave);
                    setNotiView();
                }
                viewEmpty();
            }
        }
        if (requestCode == REQUEST_CODE_SETTING) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                String typeResult = data.getStringExtra(KEY.TYPE_RESULT);
                assert typeResult != null;
                if (typeResult.equals(KEY.UPDATE)) {
                    setNotiView();
                    txtUserName.setText(DBHelper.getInstance(MainActivity.this).getUserName());
                }
            }
        }
    }

    private void updateView(int position) {
        Plant plant = DBHelper.getInstance(this).getOnePlant(plants.get(position).getPlantID());
        plants.set(position, plant);
        plantAdapter.notifyItemChanged(position);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (typeOfCare.equals(KEY.TYPE_CREATE)) {
                openExitAppDialog();
            }
            if (!typeOfCare.equals(KEY.TYPE_CREATE)) {
                setDefaultCheck();
                visibleViewCare();
                typeOfCare = KEY.TYPE_CREATE;
                txtPlant.setText("My Plant");
                imgPlus.setImageDrawable(getDrawable(R.drawable.ic_add_plant));
                imgToolBar.setBackgroundResource(R.drawable.img_water);
                plantAdapter.disableCheckBox();
                plantAdapter.notifyDataSetChanged();
            }
        }

    }

    private void openExitAppDialog() {
        final BottomSheetDialog exitDialog = new BottomSheetDialog(this);
        exitDialog.setContentView(R.layout.exit_app_dialog);

        int random = new Random().nextInt(100);
        if (random <= 70) {
            Ads.initNativeGg((FrameLayout) exitDialog.findViewById(R.id.flNativeAds), this, false, true);
        } else {
            Ads.initNativeGg((FrameLayout) exitDialog.findViewById(R.id.flNativeAds), this, false, true);
            //Ads.initNativeFB((FrameLayout) findViewById(R.id.flNativeAds), this, true, true);
        }
        Button btnYes = exitDialog.findViewById(R.id.btnYes);
        Button btnCancel = exitDialog.findViewById(R.id.btnCancel);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
                finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
            }
        });
        exitDialog.show();
    }

    private void setDefaultCheck() {
        for (int i = 0; i < plants.size(); i++) {
            if (plants.get(i).isTicked()) {
                plants.get(i).setTicked(!plants.get(i).isTicked());
            }
        }
    }

    private String getInstantDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(Constant.getDateTimeFormat(), Locale.US);
        return sdf.format(calendar.getTime());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_setting:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SETTING);
                break;
            case R.id.nav_upgradeToVIP:
                try {
                    Intent intentVip = new Intent(MainActivity.this, VipActivity.class);
                    startActivity(intentVip);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.nav_remove_add:
                try {
                    removeAds();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.nav_rate_us:
                startActivity(new Intent(MainActivity.this, RateAppActivity.class));
                break;
            case R.id.nav_feedback_dev:
                Helper.feedback(this);
                break;
            case R.id.nav_share:
                Helper.shareApp(this);
                break;
            case R.id.nav_policy:
                startActivity(new Intent(MainActivity.this, PolicyActivity.class));
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void removeAds() {
        try {
            if (readyToPurchase) {
                bp.subscribe(this, getString(R.string.ID_REMOVE_ADS));
            } else {
                Toast.makeText(this, "Billing not initialized", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        imgToolBar = findViewById(R.id.imgToolBar);
        txtGems = findViewById(R.id.txtGems);
        txtGarden = findViewById(R.id.txtGarden);
        txtNumberWater = findViewById(R.id.txtNumberWater);
        txtNumberPrune = findViewById(R.id.txtNumberPrune);
        txtNumberSpray = findViewById(R.id.txtNumberSpray);
        txtNumberFertilize = findViewById(R.id.txtNumberFertilize);
        txtWaterNoti = findViewById(R.id.txtWaterNoti);
        txtFertilizerNoti = findViewById(R.id.txtFertilizerNoti);
        txtSprayNoti = findViewById(R.id.txtSprayNoti);
        txtPruneNoti = findViewById(R.id.txtPruneNoti);
        txtUserName = findViewById(R.id.txtUserName);
        drawerLayout = findViewById(R.id.draw_layout);
        navigationView = findViewById(R.id.nav_view);
        imgMenu = findViewById(R.id.imgMenu);
        txtPlant = findViewById(R.id.txtPlant);
        imgPlus = findViewById(R.id.imgPlus);
        clWater = findViewById(R.id.clWater);
        clFertilizer = findViewById(R.id.clFertilizer);
        clPrune = findViewById(R.id.clPrune);
        clSpray = findViewById(R.id.clSpray);
        rcvPlan = findViewById(R.id.rcvPlan);
        llEmpty = findViewById(R.id.llEmpty);
    }

    private String getInstantDate() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(Constant.getDateFormat());
        return sdf.format(calendar.getTime());
    }

    private void checkRemoveAds() {
        try {
            if (bp.isSubscribed(getString(R.string.ID_REMOVE_ADS))) {
                MySetting.putRemoveAds(this, true);
            } else {
                MySetting.putRemoveAds(this, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Toast.makeText(this, "Thank you for your purchased!", Toast.LENGTH_SHORT).show();
        checkRemoveAds();
    }

    @Override
    public void onPurchaseHistoryRestored() {
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Toast.makeText(this, "You have declined payment", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBillingInitialized() {
        readyToPurchase = true;
    }

    @Override
    protected void onDestroy() {
        videoAds.destroy(this);
        super.onDestroy();
        if (bp != null) {
            bp.release();
        }
    }

    @Override
    protected void onPause() {
        videoAds.pause(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        videoAds.resume(this);
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (nMgr != null) {
            nMgr.cancelAll();
        }
        super.onResume();
    }

    private int countND(int year, int month, int day) {
        if (month < 3) {
            year--;
            month += 12;
        }
        return 365 * year + year / 4 - year / 100 + year / 400 + (153 * month - 457) / 5 + day - 306;
    }
}
