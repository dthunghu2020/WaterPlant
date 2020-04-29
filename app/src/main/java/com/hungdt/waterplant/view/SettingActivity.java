package com.hungdt.waterplant.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
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

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.hungdt.waterplant.Ads;
import com.hungdt.waterplant.Helper;
import com.hungdt.waterplant.KEY;
import com.hungdt.waterplant.MySetting;
import com.hungdt.waterplant.R;
import com.hungdt.waterplant.database.DBHelper;

import java.util.Random;

public class SettingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, BillingProcessor.IBillingHandler {
    private BillingProcessor billingProcessor;
    private Switch aSwitch;
    private TextView txtRate, txtFeedback, txtShare, txtPolicy, txtNumberGem;
    private Button btnSaveName;
    private EditText edtUserName;
    private ImageView imgBack,imgRemoveAds;
    private CardView btnUpVip;

    String userName;
    private boolean readyToPurchase = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Helper.setColorStatusBar(this, R.color.status_bar);

        initView();

        //Khai báo chức năng
        try {
            billingProcessor = BillingProcessor.newBillingProcessor(this, getString(R.string.BASE64), this); // doesn't bind
            billingProcessor.initialize(); // binds
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Native
        int random = new Random().nextInt(100);
        if (random <= 70) {
            Ads.initNativeGg((FrameLayout) findViewById(R.id.flNativeAds), this, false, true);
        } else {
            Ads.initNativeFB((FrameLayout) findViewById(R.id.flNativeAds), this, false, true);
        }

        userName = DBHelper.getInstance(this).getUserName();
        edtUserName.setText(userName);
        txtNumberGem.setText(DBHelper.getInstance(this).getGems());

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        billingProcessor = BillingProcessor.newBillingProcessor(this, getString(R.string.BASE64), this); // doesn't bind
        billingProcessor.initialize();

        btnUpVip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT < 24) {
                    Toast.makeText(SettingActivity.this, "This feature will coming soon!", Toast.LENGTH_SHORT).show();
                }else {
                    if (Integer.parseInt(DBHelper.getInstance(SettingActivity.this).getGems()) < 500) {
                        Toast.makeText(SettingActivity.this, "Your GEM is not enough to Premium", Toast.LENGTH_SHORT).show();
                    } else {
                        if (readyToPurchase) {
                            billingProcessor.subscribe(SettingActivity.this, getString(R.string.ID_SUBSCRIPTION));
                        } else {
                            Toast.makeText(getApplicationContext(), "Unable to initiate purchase", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        final MediaPlayer mpDone2 = MediaPlayer.create(this, R.raw.done_2);
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
                        mpDone2.start();
                    }
                }
            }
        });

        imgRemoveAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    removeAds();
                } catch (Exception e) {
                    e.printStackTrace();
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

        if (DBHelper.getInstance(this).

                getRemindNotification().

                equals("Off")) {
            aSwitch.setChecked(false);
        } else if (DBHelper.getInstance(this).

                getRemindNotification().

                equals("On")) {
            aSwitch.setChecked(true);
        }

        aSwitch.setOnCheckedChangeListener(this);

    }

    private void initView() {
        btnUpVip = findViewById(R.id.btnUpVip);
        aSwitch = findViewById(R.id.swNotification);
        edtUserName = findViewById(R.id.edtUserName);
        imgBack = findViewById(R.id.imgBack);
        imgRemoveAds = findViewById(R.id.imgRemoveAds);
        btnSaveName = findViewById(R.id.btnSaveName);
        txtNumberGem = findViewById(R.id.txtNumberGem);
        txtRate = findViewById(R.id.txtRate);
        txtFeedback = findViewById(R.id.txtFeedback);
        txtShare = findViewById(R.id.txtShare);
        txtPolicy = findViewById(R.id.txtPolicy);
    }

    private void removeAds() {
        try {
            if (readyToPurchase) {
                billingProcessor.subscribe(this, getString(R.string.ID_REMOVE_ADS));
            } else {
                Toast.makeText(this, "Billing not initialized", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (!billingProcessor.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        if (billingProcessor != null) {
            billingProcessor.release();
        }
        super.onDestroy();
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        try {
            Toast.makeText(this, "Thanks for your Purchased!", Toast.LENGTH_SHORT).show();
            MySetting.setSubscription(this, true);
            MySetting.putRemoveAds(this, true);
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
