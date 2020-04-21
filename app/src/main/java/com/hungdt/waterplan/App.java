package com.hungdt.waterplan;

import android.app.Application;

import com.facebook.ads.AudienceNetworkAds;

public class App extends Application {
        @Override
        public void onCreate() {
            super.onCreate();
            AudienceNetworkAds.initialize(this);
        }
}
