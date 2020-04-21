package com.hungdt.waterplan;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.hungdt.waterplan.R;
import com.hungdt.waterplan.dataset.Constant;
/*import com.icebear.newsdk.R;*/

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class Helper {

    public static boolean copyToClipboard(Context context, String text) {
        try {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
                    .getSystemService(context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("copy text", text);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show();
            return true;
        } catch (Exception e) {
            Toast.makeText(context, "Copied to clipboard fail!!!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public static void copyToClipboard(String copyText, Context context) {
        try {
            int sdk = Build.VERSION.SDK_INT;
            if (sdk < Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager)
                        context.getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(copyText);
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                        context.getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData
                        .newPlainText("Your OTP", copyText);
                clipboard.setPrimaryClip(clip);
            }
            Toast.makeText(context, "copied", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
            Log.d("xxxxxx", "error: copyToClipboard");
        }

    }

    public static boolean isToday(long time) {
        boolean isToday = false;
        String today = "", timeRecorder = "";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat(Constant.FORMAT_ONLY_DATE());
        Date date = new Date();
        today = format.format(date);

        Date date1 = new Date(time);
        timeRecorder = format.format(date1);
        if (timeRecorder.equals(today)) {
            isToday = true;
        } else {
            isToday = false;
        }
        return isToday;
    }



    public static void shareContentText(Context context, String content){
        int applicationNameId = context.getApplicationInfo().labelRes;
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, context.getString(applicationNameId));
        String text = content;

        i.putExtra(Intent.EXTRA_TEXT, text +"");
        context.startActivity(Intent.createChooser(i, "Share code"));
    }

    public static void shareApp(Context context){
        int applicationNameId = context.getApplicationInfo().labelRes;
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, context.getString(applicationNameId));
        String text = "";
        String link = context.getString(R.string.EMAIL_DEVELOPER) + context.getPackageName();
        i.putExtra(Intent.EXTRA_TEXT, text + " " + link);
        context.startActivity(Intent.createChooser(i, "Share Appp"));
    }

    public static void shareText(Context context, String contentText){
        int applicationNameId = context.getApplicationInfo().labelRes;
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, context.getString(applicationNameId));
        i.putExtra(Intent.EXTRA_TEXT, contentText);
        context.startActivity(Intent.createChooser(i, "Share content"));
    }

    // check conneted internet
    public static boolean isConnectedInternet(Context context) {
        boolean isConnectedWifi = false, isConnected3G = false;
        // kiem  tra co ket noi internet hoac wifi khong
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        try {
            if (manager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI) {
                isConnectedWifi = (manager.getActiveNetworkInfo() != null && manager.getActiveNetworkInfo()
                        .isConnected());
            }
            if (manager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_MOBILE) {
                isConnected3G = (manager.getActiveNetworkInfo() != null && manager.getActiveNetworkInfo()
                        .isConnected());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("hieptb", (isConnectedWifi || isConnected3G) + "");
        return (isConnectedWifi || isConnected3G);
    }

    // set status bar color
    public static void setColorStatusBar(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(activity, color));
        }
    }

    public static void callPublisherPlayStore(Context context, String publisherName) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:" + publisherName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.PLAY_STORE_DEV_URL) + "" + publisherName)));
        }
    }

    public static void feedback(Context context) {
        context.startActivity(createEmailIntent(context));
    }

    public static Intent createEmailIntent(Context context) {
        String toEmail = context.getString(R.string.EMAIL_DEVELOPER);

        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(
                    context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String subject = "FeedBack from Android";
        String message = "Content : ";

        Intent sendTo = new Intent(Intent.ACTION_SENDTO);
        String uriText = "mailto:" + Uri.encode(toEmail) +
                "?subject=" + Uri.encode(subject) +
                "&body=" + Uri.encode(message);
        Uri uri = Uri.parse(uriText);
        sendTo.setData(uri);
        List<ResolveInfo> resolveInfos =
                context.getPackageManager().queryIntentActivities(sendTo, 0);

        // Emulators may not like this check...
        if (!resolveInfos.isEmpty()) {
            return sendTo;
        }

        Intent send = new Intent(Intent.ACTION_SEND);

        send.setType("text/plain");
        send.putExtra(Intent.EXTRA_EMAIL,
                new String[]{toEmail});
        send.putExtra(Intent.EXTRA_SUBJECT, subject);
        send.putExtra(Intent.EXTRA_TEXT, message);

        return Intent.createChooser(send, "send feedback for developer");
    }


    public static boolean isAppInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        try {
            Intent i = manager.getLaunchIntentForPackage(packageName);
            if (i == null) {
                return false;
            }
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(i);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void callPlayStore(Context context, String packageName) {
        if (packageName.contains("https://")) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(packageName)));
        } else {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
            }
        }
    }


    // format size
    public static String getFileSize(long size) {
        if (size <= 0)
            return "0 B/s";
        final String[] units = new String[]{"B/s", "KB/s", "MB/s", "GB/s", "TB/s"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    // format size
    public static String getFileSize_2(long size) {
        if (size <= 0)
            return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    // format size
    public static String getFileSize_3(long size) {
        if (size <= 0)
            return "0 Hz";
        final String[] units = new String[]{"KHz", "MHz", "GHz", "THz"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1000));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1000, digitGroups)) + " " + units[digitGroups];
    }

}
