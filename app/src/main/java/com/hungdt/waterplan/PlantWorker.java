package com.hungdt.waterplan;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.hungdt.waterplan.database.DBHelper;
import com.hungdt.waterplan.dataset.Constant;
import com.hungdt.waterplan.model.Remind;
import com.hungdt.waterplan.view.MainActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class PlantWorker extends Worker {
    public static final String KEY_TASK_OUTPUT = "key_output";
    public static final String channelID = "channelID";
    public static final String channelName = "Channel Plant";

    Random rd = new Random();
    Calendar calendar = Calendar.getInstance();
    private List<Remind> reminds = new ArrayList<>();

    public PlantWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfDT = new SimpleDateFormat(Constant.getDateTimeFormat());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfTime = new SimpleDateFormat(Constant.getTimeFormat());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfHour = new SimpleDateFormat(Constant.getHourFormat());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfMinute = new SimpleDateFormat(Constant.getMinuteFormat());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfDay = new SimpleDateFormat(Constant.getDayFormat());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfMonth = new SimpleDateFormat(Constant.getMonthFormat());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfYear = new SimpleDateFormat(Constant.getYearFormat());
        Data data1 = null;
        int hourInstance = 0;
        int minuteInstance = 0;
        int countDayInstance = 0;
        Date dateInstance;
        Date date;
        String instanceTime = sdfDT.format(calendar.getTime());
        try {
            dateInstance = sdfDT.parse(instanceTime);
            if (dateInstance != null) {
                hourInstance = Integer.parseInt(sdfHour.format(dateInstance));
                minuteInstance = Integer.parseInt(sdfMinute.format(dateInstance));
                countDayInstance = countDay(Integer.parseInt(sdfYear.format(dateInstance)),
                        Integer.parseInt(sdfMonth.format(dateInstance)),
                        Integer.parseInt(sdfDay.format(dateInstance)));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (DBHelper.getInstance(getApplicationContext()).getRemindNotification().equals("On")) {
            Data data = getInputData();
            reminds = DBHelper.getInstance(getApplicationContext()).getAllRemind();
            try {

                //kiểm tra lịch nhắc nhở
                for (int i = 0; i < reminds.size(); i++) {
                    date = sdfDT.parse(reminds.get(i).getRemindCreateDT());
                    if (date != null) {
                        int dayCheck = countDay(Integer.parseInt(sdfYear.format(date)),
                                Integer.parseInt(sdfMonth.format(date)),
                                Integer.parseInt(sdfDay.format(date)))
                                + Integer.parseInt(reminds.get(i).getCareCycle());
                        // kiểm tra ngày hiện tại và ngày check
                        if (countDayInstance >= dayCheck) {
                            //phút nhỏ hơn 45
                            if (minuteInstance < 45 && Integer.parseInt(sdfHour.format(date)) == hourInstance) {
                                int nbCheck = minuteInstance - Integer.parseInt(sdfMinute.format(date)) + 15;
                                if (nbCheck >= 0 && nbCheck <= 15) {
                                    sendNotification(i);
                                }
                            }
                            //phút lớn hơn 45 cần check lệch giờ
                            if (minuteInstance >= 45) {
                                if (Integer.parseInt(sdfHour.format(date)) == hourInstance) {
                                    int nbCheck = minuteInstance - Integer.parseInt(sdfMinute.format(date)) + 15;
                                    if (nbCheck > 0 && nbCheck <= 15) {
                                        sendNotification(i);
                                    }
                                }
                                if (Integer.parseInt(sdfHour.format(date)) == hourInstance + 1) {
                                    int nbCheck = 60 - minuteInstance + Integer.parseInt(sdfMinute.format(date));
                                    if (nbCheck >= 0 && nbCheck <= 15) {
                                        sendNotification(i);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        if (hourInstance == 13 && minuteInstance > 45) {
            DBHelper.getInstance(getApplicationContext()).setEveryDayNotification("Off", "On");
        }

        if (DBHelper.getInstance(getApplicationContext()).getEveryDayNotification().equals("On")) {
            calendar.set(Calendar.HOUR_OF_DAY, 14);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getApplicationContext(), AlertReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intent, 0);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            DBHelper.getInstance(getApplicationContext()).setEveryDayNotification("On", "Off");
        }

        //setting phần hiển thị
        data1 = new Data.Builder()
                .putString(KEY_TASK_OUTPUT, "Task Finished!")
                .build();


        return Result.success(data1);
    }

    private void sendNotification(int i) {
        int random;
        switch (reminds.get(i).getRemindType()) {
            case KEY.TYPE_WATER:
                random = rd.nextInt(3);
                switch (random) {
                    case 0:
                        displayNotification("Water Plant", "Your plants are thirsty! Give them some water now!", i);
                        break;
                    case 1:
                        displayNotification("Water Plant", "Your plants are waiting for watering today!", i);
                        break;
                    case 2:
                        displayNotification("Water Plant", "You need to water your plants today!", i);
                        break;
                }
                break;
            case KEY.TYPE_PRUNE:
                random = rd.nextInt(3);
                switch (random) {
                    case 0:
                        displayNotification("Water Plant", "You need to prune your plants today!", i);
                        break;
                    case 1:
                        displayNotification("Water Plant", "Someone need to be pruned today!", i);
                        break;
                    case 2:
                        displayNotification("Water Plant", "Don't forget to prune your plants today!", i);
                        break;
                }
                break;
            case KEY.TYPE_SPRAY:
                random = rd.nextInt(2);
                switch (random) {
                    case 0:
                        displayNotification("Water Plant", "Don't forget bug spray today!", i);
                        break;
                    case 1:
                        displayNotification("Water Plant", "You have a bug spray plan today", i);
                        break;
                }
                break;
            case KEY.TYPE_FERTILIZER:
                random = rd.nextInt(2);
                switch (random) {
                    case 0:
                        displayNotification("Water Plant", "You need to fertilize your plants today!", i);
                        break;
                    case 1:
                        displayNotification("Water Plant", "Fertilize time! Don't let your babies be hungry!", i);
                        break;
                }
                break;
        }

    }

    private void displayNotification(String task, String desc, int i) {

        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Uri soundUri = Uri.parse(
                    "android.resource://" +
                            getApplicationContext().getPackageName() +
                            "/" +
                            R.raw.noti);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();
            NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setSound(soundUri, audioAttributes);
            manager.createNotificationChannel(channel);
        }

        //Phần hiển thị notifi
        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(),i,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle(task)
                .setContentText(desc)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);


        manager.notify(i, builder.build());
    }

    private int countDay(int year, int month, int day) {
        if (month < 3) {
            year--;
            month += 12;
        }
        return 365 * year + year / 4 - year / 100 + year / 400 + (153 * month - 457) / 5 + day - 306;
    }
}
