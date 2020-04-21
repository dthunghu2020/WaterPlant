package com.hungdt.waterplan.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
    private TextView  txtRate, txtFeedback, txtShare, txtPolicy;
    private CardView btnSaveName;
    private EditText edtUserName;
    private ImageView imgBack;

    String userName;

    private UnifiedNativeAd nativeAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Helper.setColorStatusBar(this, R.color.status_bar);

        initView();

        //Native
        int  random = new Random().nextInt(100);
        if(random<=70){
            Ads.initNativeGg((FrameLayout) findViewById(R.id.flNativeAds),this,true,true);
        }else {
            Ads.initNativeFB((FrameLayout) findViewById(R.id.flNativeAds),this,true,true);
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

        /*btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtUserName.getText().toString().isEmpty()) {
                    Toast.makeText(SettingActivity.this, "You forget enter your name", Toast.LENGTH_SHORT).show();
                } else {
                    DBHelper.getInstance(SettingActivity.this).setUserName(userName, edtUserName.getText().toString());
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(KEY.TYPE_RESULT, KEY.UPDATE);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/

    }

    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));

        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        //adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);
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
}
