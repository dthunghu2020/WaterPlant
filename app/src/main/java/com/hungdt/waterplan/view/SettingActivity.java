package com.hungdt.waterplan.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.hungdt.waterplan.Ads;
import com.hungdt.waterplan.Helper;
import com.hungdt.waterplan.KEY;
import com.hungdt.waterplan.R;
import com.hungdt.waterplan.database.DBHelper;

import java.util.Random;

public class SettingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private Switch aSwitch;
    private TextView txtRate, txtFeedback, txtShare, txtPolicy;
    private CardView btnSaveName;
    private EditText edtUserName;
    private ImageView imgBack;

    String userName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Helper.setColorStatusBar(this, R.color.status_bar);

        initView();

        //Native
        int random = new Random().nextInt(100);
        if (random <= 70) {
            Ads.initNativeGg((FrameLayout) findViewById(R.id.flNativeAds), this, false, true);
        } else {
            Ads.initNativeFB((FrameLayout) findViewById(R.id.flNativeAds), this, false, true);
        }

        userName = DBHelper.getInstance(this).getUserName();
        edtUserName.setText(userName);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnSaveName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtUserName.getText().toString().isEmpty()) {
                    Toast.makeText(SettingActivity.this, "You forget enter your name", Toast.LENGTH_SHORT).show();
                } else {
                    if (!edtUserName.getText().toString().equals(userName)) {
                        DBHelper.getInstance(SettingActivity.this).setUserName(userName, edtUserName.getText().toString());
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(KEY.TYPE_RESULT, KEY.UPDATE);
                        setResult(Activity.RESULT_OK, resultIntent);
                        userName = edtUserName.getText().toString();
                        Toast.makeText(SettingActivity.this, "Change Name Success!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        txtRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this, RateAppActivity.class));
            }
        });

        txtFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.feedback(SettingActivity.this);
            }
        });

        txtShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.shareApp(SettingActivity.this);
            }
        });

        txtPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this, PolicyActivity.class));
            }
        });

        if (DBHelper.getInstance(this).getRemindNotification().equals("Off")) {
            aSwitch.setChecked(false);
        } else if (DBHelper.getInstance(this).getRemindNotification().equals("On")) {
            aSwitch.setChecked(true);
        }

        aSwitch.setOnCheckedChangeListener(this);

    }

    private void initView() {
        aSwitch = findViewById(R.id.swNotification);
        edtUserName = findViewById(R.id.edtUserName);
        imgBack = findViewById(R.id.imgBack);
        btnSaveName = findViewById(R.id.btnSaveName);
        txtRate = findViewById(R.id.txtRate);
        txtFeedback = findViewById(R.id.txtFeedback);
        txtShare = findViewById(R.id.txtShare);
        txtPolicy = findViewById(R.id.txtPolicy);
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (aSwitch.isChecked()) {
            DBHelper.getInstance(this).setRemindNotification("Off", "On");
            Toast.makeText(this, "Notification is On", Toast.LENGTH_SHORT).show();
        } else {
            DBHelper.getInstance(this).setRemindNotification("On", "Off");
            Toast.makeText(this, "Notification is Off", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
}
