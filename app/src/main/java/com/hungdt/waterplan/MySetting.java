package com.hungdt.waterplan;

import android.content.Context;
import android.content.SharedPreferences;

public class MySetting {
    public static final String SETTINGS = "ggggdfgdfg";
    public static final String KEY_REMOVE_ADS = "ncvdnrcn";
    public static final String KEY_RATE_APP = "yhfghhnrffndxcx";
    public static final String KEY_SUBSCRIPTION = "nrcvfdbnbre";


    public static void setSubscription(Context context, boolean isSub) {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_SUBSCRIPTION, isSub);
        editor.commit();
    }

    public static boolean isSubscription(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        return preferences.getBoolean(KEY_SUBSCRIPTION, false);
    }
    public static int isRateApp(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        return preferences.getInt(KEY_RATE_APP, 0);
    }

    public static boolean putRateApp(Context context, int value) {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_RATE_APP, value);
        return editor.commit();
    }

    public static void putRemoveAds(Context context, boolean removeAds) {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_REMOVE_ADS, removeAds);
        editor.commit();
    }

    public static boolean isRemoveAds(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        return preferences.getBoolean(KEY_REMOVE_ADS, false);
    }


}
