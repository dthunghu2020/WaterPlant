package com.hungdt.waterplan.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.material.navigation.NavigationView;
import com.hungdt.waterplan.Ads;
import com.hungdt.waterplan.Helper;
import com.hungdt.waterplan.KEY;
import com.hungdt.waterplan.MySetting;
import com.hungdt.waterplan.PlantWorker;
import com.hungdt.waterplan.R;
import com.hungdt.waterplan.database.DBHelper;
import com.hungdt.waterplan.dataset.Constant;
import com.hungdt.waterplan.model.Plant;
import com.hungdt.waterplan.view.adater.PlantAdapter;

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
    private TextView txtUserName, txtNumberWater, txtNumberPrune, txtNumberSpray, txtNumberFertilize, txtPruneNoti, txtWaterNoti, txtFertilizerNoti, txtSprayNoti;
    private RelativeLayout toolBar;
    private ImageView imgPlus, imgMenu;
    private RecyclerView rcvPlan;
    private PlantAdapter plantAdapter;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

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
    private String typeOfCare = KEY.TYPE_CREATE;


    final Calendar calendar = Calendar.getInstance();

    private List<Plant> plants = new ArrayList<>();

    private InterstitialAd ggInterstitialAd;
    private RewardedVideoAd mRewardedVideoAd;

    private BillingProcessor bp;

    private boolean readyToPurchase = false;
    private boolean openVipActivity = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Helper.setColorStatusBar(this, R.color.status_bar);

        initView();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        //Inter
        initGgInterstitialAd();

        //Video
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewarded(RewardItem reward) {
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
            }

            @Override
            public void onRewardedVideoAdClosed() {
                loadRewardedVideoAd();
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
                int count = numberOfPlant + 1;
                DBHelper.getInstance(MainActivity.this).setNumberOfPlant("" + numberOfPlant, "" + count);
                numberOfPlant += 1;
            }
        });
        loadRewardedVideoAd();

        //Native
        int random = new Random().nextInt(100);
        if (random <= 70) {
            Ads.initNativeGg((FrameLayout) findViewById(R.id.flNativeAds), this, true, true);
        } else {
            Ads.initNativeFB((FrameLayout) findViewById(R.id.flNativeAds), this, true, true);
        }

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
            DBHelper.getInstance(this).createUserData("Planter", "On", "On", "3");
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
        WorkManager.getInstance(MainActivity.this).enqueue(periodicWorkRequest);
        WorkManager.getInstance(MainActivity.this).getWorkInfoByIdLiveData(periodicWorkRequest.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        /*if (workInfo != null) {
                            txtText.setText("");
                            if (workInfo.getState().isFinished()) {
                                Data data = workInfo.getOutputData();
                                String output = data.getString(PlantWorker.KEY_TASK_OUTPUT);
                                txtText.append(output + "\n");
                            }
                            String status = workInfo.getState().name();
                            txtText.append(status + "\n");
                        }*/
                    }
                });

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

        plants.addAll(DBHelper.getInstance(this).getAllPlant());
        plantAdapter = new PlantAdapter(this, plants);
        rcvPlan.setLayoutManager(new LinearLayoutManager(this));
        rcvPlan.setAdapter(plantAdapter);
        Helper.setColorStatusBar(this, R.color.status_bar);

        String username = DBHelper.getInstance(this).getUserName();
        txtUserName.setText(username);

        //setting notifi
        setNotiView();

        imgPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberOfPlant == plants.size()) {
                    openMorePlaceDialog();
                } else {
                    if (typeOfCare.equals(KEY.TYPE_CREATE)) {
                        Intent intent = new Intent(MainActivity.this, EditPlanActivity.class);
                        intent.putExtra(KEY.TYPE, KEY.TYPE_CREATE);
                        startActivityForResult(intent, REQUEST_CODE_ADD_PLANT);
                        setNotiView();
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
                        imgPlus.setImageDrawable(getDrawable(R.drawable.ic_add_plant));
                        toolBar.setBackgroundResource(R.drawable.img_water);
                        visibleViewCare();
                        typeOfCare = KEY.TYPE_CREATE;
                        plantAdapter.disableCheckBox();
                        plantAdapter.notifyDataSetChanged();
                        ggInterstitialAd.show();
                    }
                }

            }
        });

        clWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeOfCare = KEY.TYPE_WATER;
                invisibleViewCare();
                imgPlus.setImageDrawable(getDrawable(R.drawable.ic_watering));
                plantAdapter.enableCheckBox(typeOfCare);
                plantAdapter.notifyDataSetChanged();
            }
        });

        clFertilizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeOfCare = KEY.TYPE_FERTILIZER;
                toolBar.setBackgroundResource(R.drawable.img_ferti);
                invisibleViewCare();
                imgPlus.setImageDrawable(getDrawable(R.drawable.ic_fertilize));
                plantAdapter.enableCheckBox(typeOfCare);
                plantAdapter.notifyDataSetChanged();
            }
        });

        clPrune.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeOfCare = KEY.TYPE_PRUNE;
                toolBar.setBackgroundResource(R.drawable.img_prunes);
                invisibleViewCare();
                imgPlus.setImageDrawable(getDrawable(R.drawable.ic_prune));
                plantAdapter.enableCheckBox(typeOfCare);
                plantAdapter.notifyDataSetChanged();
            }
        });

        clSpray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeOfCare = KEY.TYPE_SPRAY;
                toolBar.setBackgroundResource(R.drawable.img_sprays);
                invisibleViewCare();
                imgPlus.setImageDrawable(getDrawable(R.drawable.ic_spray));
                plantAdapter.enableCheckBox(typeOfCare);
                plantAdapter.notifyDataSetChanged();
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

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }

    private void openMorePlaceDialog() {
        final Dialog morePlaceDialog = new Dialog(MainActivity.this);
        morePlaceDialog.setContentView(R.layout.more_plant_dialog);

        Button btnYes = morePlaceDialog.findViewById(R.id.btnYes);
        Button btnNoThanks = morePlaceDialog.findViewById(R.id.btnNoThanks);


        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRewardedVideoAd.isLoaded()) {
                    mRewardedVideoAd.show();
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
                }
            }
        }
        if (requestCode == REQUEST_CODE_VIP) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                String typeResult = data.getStringExtra(KEY.TYPE_RESULT);
                assert typeResult != null;
                if (typeResult.equals(KEY.CREATE)) {
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
                    setNotiView();
                }
                if (typeResult.equals(KEY.UPDATE)) {
                    updateView(positionSave);
                    setNotiView();
                }
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
                super.onBackPressed();
            }
            if (!typeOfCare.equals(KEY.TYPE_CREATE)) {
                setDefaultCheck();
                visibleViewCare();
                typeOfCare = KEY.TYPE_CREATE;
                imgPlus.setImageDrawable(getDrawable(R.drawable.ic_add_plant));
                toolBar.setBackgroundResource(R.drawable.img_water);
                plantAdapter.disableCheckBox();
                plantAdapter.notifyDataSetChanged();
            }
        }

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
        toolBar = findViewById(R.id.toolBar);
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
        imgPlus = findViewById(R.id.imgPlus);
        clWater = findViewById(R.id.clWater);
        clFertilizer = findViewById(R.id.clFertilizer);
        clPrune = findViewById(R.id.clPrune);
        clSpray = findViewById(R.id.clSpray);
        rcvPlan = findViewById(R.id.rcvPlan);
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
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
        if (bp != null) {
            bp.release();
        }
    }

    @Override
    protected void onPause() {
        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        mRewardedVideoAd.resume(this);
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
