package com.hungdt.waterplan.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hungdt.waterplan.Helper;
import com.hungdt.waterplan.MySetting;
import com.hungdt.waterplan.R;
/*import com.icebear.newsdk.R;
import com.icebear.newsdk.utils.Ads;
import com.icebear.newsdk.utils.Helper;
import com.icebear.newsdk.utils.MySetting;*/

public class RateAppActivity extends AppCompatActivity {
    LinearLayout lnRateApp, lnClose;
    TextView tvExit;

    ImageView imgStarLight1, imgStarLight2, imgStarLight3, imgStarLight4, imgStarLight5;
    boolean isFromMain = false;
    boolean isEnableClick = true;
    boolean isRate = true;

    CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_rate_app);
        Helper.setColorStatusBar(this, R.color.status_bar);

        //Ads.initNativeGgFb((LinearLayout)findViewById(R.id.lnNative), this, true);

        try {
            if (getIntent().hasExtra("isRate")) {
                isRate = getIntent().getBooleanExtra("isRate", true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (getIntent().hasExtra("isExit")) {
                isFromMain = getIntent().getBooleanExtra("isExit", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            initView();
            if (!isFromMain) setData();
            click();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (timer != null) {
                timer.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void click() {
        imgStarLight1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEnableClick) {
                    try {
                        if (timer != null) {
                            timer.cancel();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    isEnableClick = false;
                    MySetting.putRateApp(RateAppActivity.this, 1);
                    setData();
                    try {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Helper.feedback(RateAppActivity.this);
                                finish();
                                isEnableClick = true;
                            }
                        }, 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        imgStarLight2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEnableClick) {
                    try {
                        if (timer != null) {
                            timer.cancel();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    isEnableClick = false;
                    MySetting.putRateApp(RateAppActivity.this, 2);
                    setData();
                    try {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Helper.feedback(RateAppActivity.this);
                                finish();
                                isEnableClick = true;
                            }
                        }, 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        imgStarLight3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEnableClick) {
                    try {
                        if (timer != null) {
                            timer.cancel();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    isEnableClick = false;
                    MySetting.putRateApp(RateAppActivity.this, 3);
                    setData();
                    try {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Helper.feedback(RateAppActivity.this);
                                finish();
                                isEnableClick = true;
                            }
                        }, 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        imgStarLight4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEnableClick) {
                    try {
                        if (timer != null) {
                            timer.cancel();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    isEnableClick = false;
                    MySetting.putRateApp(RateAppActivity.this, 4);
                    setData();
                    try {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Helper.callPlayStore(RateAppActivity.this, getPackageName());
                                finish();
                                isEnableClick = true;
                            }
                        }, 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        imgStarLight5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEnableClick) {
                    try {
                        if (timer != null) {
                            timer.cancel();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    isEnableClick = false;
                    MySetting.putRateApp(RateAppActivity.this, 5);
                    setData();
                    try {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Helper.callPlayStore(RateAppActivity.this, getPackageName());
                                finish();
                                isEnableClick = true;
                            }
                        }, 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        lnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        });
    }

    private void setData() {
        if (MySetting.isRateApp(this) == 0) {
            imgStarLight1.setImageResource(R.drawable.ic_star_un_selected);
            imgStarLight2.setImageResource(R.drawable.ic_star_un_selected);
            imgStarLight3.setImageResource(R.drawable.ic_star_un_selected);
            imgStarLight4.setImageResource(R.drawable.ic_star_un_selected);
            imgStarLight5.setImageResource(R.drawable.ic_star_un_selected);
        } else if (MySetting.isRateApp(this) == 1) {
            imgStarLight1.setImageResource(R.drawable.ic_star_selected);
            imgStarLight2.setImageResource(R.drawable.ic_star_un_selected);
            imgStarLight3.setImageResource(R.drawable.ic_star_un_selected);
            imgStarLight4.setImageResource(R.drawable.ic_star_un_selected);
            imgStarLight5.setImageResource(R.drawable.ic_star_un_selected);
        } else if (MySetting.isRateApp(this) == 2) {
            imgStarLight1.setImageResource(R.drawable.ic_star_selected);
            imgStarLight2.setImageResource(R.drawable.ic_star_selected);
            imgStarLight3.setImageResource(R.drawable.ic_star_un_selected);
            imgStarLight4.setImageResource(R.drawable.ic_star_un_selected);
            imgStarLight5.setImageResource(R.drawable.ic_star_un_selected);
        } else if (MySetting.isRateApp(this) == 3) {
            imgStarLight1.setImageResource(R.drawable.ic_star_selected);
            imgStarLight2.setImageResource(R.drawable.ic_star_selected);
            imgStarLight3.setImageResource(R.drawable.ic_star_selected);
            imgStarLight4.setImageResource(R.drawable.ic_star_un_selected);
            imgStarLight5.setImageResource(R.drawable.ic_star_un_selected);
        } else if (MySetting.isRateApp(this) == 4) {
            imgStarLight1.setImageResource(R.drawable.ic_star_selected);
            imgStarLight2.setImageResource(R.drawable.ic_star_selected);
            imgStarLight3.setImageResource(R.drawable.ic_star_selected);
            imgStarLight4.setImageResource(R.drawable.ic_star_selected);
            imgStarLight5.setImageResource(R.drawable.ic_star_un_selected);
        } else if (MySetting.isRateApp(this) == 5) {
            imgStarLight1.setImageResource(R.drawable.ic_star_selected);
            imgStarLight2.setImageResource(R.drawable.ic_star_selected);
            imgStarLight3.setImageResource(R.drawable.ic_star_selected);
            imgStarLight4.setImageResource(R.drawable.ic_star_selected);
            imgStarLight5.setImageResource(R.drawable.ic_star_selected);
        }


    }

    private void closeActivity() {
        finish();
        try {
            if (isFromMain) {
                sendBroadcast(new Intent("exitFromRateApp"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (isFromMain) super.onBackPressed();
    }

    private void initView() {
        lnRateApp = (LinearLayout) findViewById(R.id.lnDialogRateApp);
        imgStarLight1 = (ImageView) findViewById(R.id.imgStarLight1Rate);
        imgStarLight2 = (ImageView) findViewById(R.id.imgStarLight2Rate);
        imgStarLight3 = (ImageView) findViewById(R.id.imgStarLight3Rate);
        imgStarLight4 = (ImageView) findViewById(R.id.imgStarLight4Rate);
        imgStarLight5 = (ImageView) findViewById(R.id.imgStarLight5Rate);
        lnClose = (LinearLayout) findViewById(R.id.lnExitRateApp);
        tvExit = (TextView) findViewById(R.id.tvExitRateApp);
        tvExit.setVisibility(View.GONE);
        lnClose = (LinearLayout) findViewById(R.id.lnExitRateApp);
        tvExit = (TextView) findViewById(R.id.tvExitRateApp);
        tvExit.setVisibility(View.GONE);

        if (isFromMain) {
            tvExit.setVisibility(View.VISIBLE);
        }

    }
}
