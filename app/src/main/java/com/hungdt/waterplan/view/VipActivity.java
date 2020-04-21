package com.hungdt.waterplan.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.hungdt.waterplan.Ads;
import com.hungdt.waterplan.Helper;
import com.hungdt.waterplan.KEY;
import com.hungdt.waterplan.MySetting;
import com.hungdt.waterplan.R;
import com.hungdt.waterplan.model.VipDetail;
import com.hungdt.waterplan.view.adater.DetailVIPAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VipActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    private BillingProcessor billingProcessor;
    private boolean readyToPurchase = false;

    private InterstitialAd ggInterstitialAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip);
        Helper.setColorStatusBar(this, R.color.status_bar);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        //Inter
        initGgInterstitialAd();

        //Banner
        int random = new Random().nextInt(100);
        if(random<=70){
            Ads.initBanner((LinearLayout) findViewById(R.id.llBanner),this);
        }else {
            Ads.initBannerFB((LinearLayout) findViewById(R.id.llBanner),this);
        }

        TextView txtStartNow = findViewById(R.id.txtStartNow);
        ViewPager vpVipDetails = findViewById(R.id.vpVipDetails);
        LinearLayout imgBack = findViewById(R.id.imgBack);

        billingProcessor = BillingProcessor.newBillingProcessor(this, getString(R.string.BASE64), this); // doesn't bind
        billingProcessor.initialize();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        txtStartNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (readyToPurchase) {
                    billingProcessor.subscribe(VipActivity.this, getString(R.string.ID_SUBSCRIPTION));
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to initiate purchase", Toast.LENGTH_SHORT).show();
                }
            }
        });

        List<VipDetail> vipDetails = new ArrayList<>();//Data trueyenf vao
        vipDetails.add(new VipDetail(R.drawable.ic_feature, "Get all feature", "Once & Forever", "Pay for once to use all feature.", "No more locked feature"));
        vipDetails.add(new VipDetail(R.drawable.ic_blockads, "", "No Ads", "Remove all ads that annoy you", "when experience this app."));
        vipDetails.add(new VipDetail(R.drawable.ic_getallfeature, "", "More Tracking Feature", "More activities of taking care", "of your plants."));
        vipDetails.add(new VipDetail(R.drawable.group_296, "Take care of", "Unlimited Plants", "Take care of all your plants", "with unlimited features."));
        DetailVIPAdapter vipAdapter = new DetailVIPAdapter(VipActivity.this, vipDetails);//setAdapter
        vpVipDetails.setAdapter(vipAdapter);
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
                    Log.e("123", "onAdClosed: Load New ADS" );
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    Log.e("123", "onAdLoaded: " );
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
                    Log.e("123", "onAdFailedToLoad: Load FB ADS" );
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
        Toast.makeText(this, "Unable to process billing", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBillingInitialized() {
        readyToPurchase = true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ggInterstitialAd.show();
    }
}
