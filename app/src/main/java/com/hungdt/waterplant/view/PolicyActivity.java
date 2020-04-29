package com.hungdt.waterplant.view;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.hungdt.waterplant.Helper;
import com.hungdt.waterplant.R;

/*import com.icebear.newsdk.R;
import com.icebear.newsdk.utils.Ads;
import com.icebear.newsdk.utils.Helper;*/


public class PolicyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);
        Helper.setColorStatusBar(this, R.color.status_bar);
        //Ads.initBanner((LinearLayout)findViewById(R.id.lnNative), this);

        initView();
    }

    private void initView() {
        try {
            WebView wvHome = (WebView) findViewById(R.id.webView);
            wvHome.loadUrl("file:///android_asset/index.html");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
