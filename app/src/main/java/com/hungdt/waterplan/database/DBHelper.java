package com.hungdt.waterplan.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hungdt.waterplan.model.Event;
import com.hungdt.waterplan.model.Plant;
import com.hungdt.waterplan.model.Remind;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static DBHelper instance;

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "WaterPlan.db";

    public static final String TABLE_PLANT = "TB_PLAN";
    public static final String COLUMN_PLANT_ID = "PLAN_ID";
    public static final String COLUMN_PLANT_NAME = "PLAN_NAME";
    public static final String COLUMN_PLANT_IMAGE = "PLAN_AVATAR";
    public static final String COLUMN_PLANT_NOTE = "PLAN_NOTE";

    public static final String TABLE_REMIND = "TB_REMIND";
    public static final String COLUMN_REMIND_PLANT_ID = "PLANT_ID";
    public static final String COLUMN_REMIND_ID = "REMIND_ID";
    public static final String COLUMN_REMIND_TYPE = "REMIND_TYPE";
    public static final String COLUMN_REMIND_CREATE_DATE_TIME = "REMIND_CREATE_DATE_TIME";
    public static final String COLUMN_REMIND_TIME = "REMIND_TIME";
    public static final String COLUMN_REMIND_CARE_CYCLE = "REMIND_CARE_CYCLE";

    public static final String TABLE_EVENT = "TB_EVENT";
    public static final String COLUMN_EVENT_ID = "EVENT_ID";
    public static final String COLUMN_EVENT_PLANT_ID = "PLANT_ID";
    public static final String COLUMN_EVENT_NAME = "EVENT_NAME";
    public static final String COLUMN_EVENT_DATE = "EVENT_DATE";
    public static final String COLUMN_EVENT_POSITION = "EVENT_POSITION";

    public static final String TABLE_USER_DATA = "TB_USER";
    public static final String COLUMN_USER_NAME = "USER_NAME";
    public static final String COLUMN_USER_REMIND_NOTIFICATION = "USER_RM_NOTI";
    public static final String COLUMN_USER_EVERY_DAY_NOTIFICATION= "USER_EVERY_DAY_NOTI";

    public static final String SQL_CREATE_TABLE_PLAN = "CREATE TABLE " + TABLE_PLANT + "("
            + COLUMN_PLANT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_PLANT_NAME + " TEXT NOT NULL, "
            + COLUMN_PLANT_IMAGE + " TEXT, "
            + COLUMN_PLANT_NOTE + " TEXT " + ");";

    public static final String SQL_CREATE_TABLE_REMIND = "CREATE TABLE " + TABLE_REMIND + "("
            + COLUMN_REMIND_PLANT_ID + " INTEGER NOT NULL, "
            + COLUMN_REMIND_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_REMIND_TYPE + " TEXT NOT NULL, "
            + COLUMN_REMIND_CREATE_DATE_TIME + " TEXT NOT NULL, "
            + COLUMN_REMIND_TIME + " TEXT NOT NULL, "
            + COLUMN_REMIND_CARE_CYCLE + " TEXT NOT NULL" + ");";

    public static final String SQL_CREATE_TABLE_EVENT = "CREATE TABLE " + TABLE_EVENT + "("
            + COLUMN_EVENT_PLANT_ID + " INTEGER NOT NULL, "
            + COLUMN_EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_EVENT_NAME + " TEXT NOT NULL, "
            + COLUMN_EVENT_DATE + " TEXT NOT NULL, "
            + COLUMN_EVENT_POSITION + " TEXT NOT NULL " + ");";

    public static final String SQL_CREATE_TABLE_USER_DATA = "CREATE TABLE " + TABLE_USER_DATA + "("
            + COLUMN_USER_NAME + " TEXT NOT NULL, "
            + COLUMN_USER_REMIND_NOTIFICATION + " TEXT NOT NULL, "
            + COLUMN_USER_EVERY_DAY_NOTIFICATION + " TEXT NOT NULL " + ");";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }

    public void addPlan(String planName, String planAvatar, String planNote) {
        SQLiteDatabase database = instance.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PLANT_NAME, planName);
        values.put(COLUMN_PLANT_IMAGE, planAvatar);
        values.put(COLUMN_PLANT_NOTE, planNote);
        database.insert(TABLE_PLANT, null, values);
        database.close();
    }

    public void updatePlan(String planID, String planName, String planAvatar, String planNote) {

        SQLiteDatabase db = instance.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PLANT_ID, planID);
        values.put(COLUMN_PLANT_NAME, planName);
        values.put(COLUMN_PLANT_IMAGE, planAvatar);
        values.put(COLUMN_PLANT_NOTE, planNote);
        db.update(TABLE_PLANT, values, COLUMN_PLANT_ID + "='" + planID + "'", null);
        db.close();
    }

    public void deletePlan(String planID) {
        SQLiteDatabase db = instance.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PLANT_ID, planID);
        db.delete(TABLE_PLANT, COLUMN_PLANT_ID + "='" + planID + "'", new String[]{});

        db.close();
    }

    public void addRemind(String planID, String remindType, String remindCreateDateTime, String remindTime, String careCycle) {
        SQLiteDatabase database = instance.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_REMIND_PLANT_ID, planID);
        values.put(COLUMN_REMIND_TYPE, remindType);
        values.put(COLUMN_REMIND_CREATE_DATE_TIME, remindCreateDateTime);
        values.put(COLUMN_REMIND_TIME, remindTime);
        values.put(COLUMN_REMIND_CARE_CYCLE, careCycle);
        database.insert(TABLE_REMIND, null, values);
        database.close();
    }

    public void addEvent(String planID, String eventName, String eventDate, String eventPosition) {
        SQLiteDatabase database = instance.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_EVENT_PLANT_ID, planID);
        values.put(COLUMN_EVENT_NAME, eventName);
        values.put(COLUMN_EVENT_DATE, eventDate);
        values.put(COLUMN_EVENT_POSITION, eventPosition);
        database.insert(TABLE_EVENT, null, values);
        database.close();
    }

    public void updateRemind(String remindID, String remindCreateDateTime, String remindTime, String careCycle) {
        SQLiteDatabase db = instance.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_REMIND_CREATE_DATE_TIME, remindCreateDateTime);
        values.put(COLUMN_REMIND_TIME, remindTime);
        values.put(COLUMN_REMIND_CARE_CYCLE, careCycle);
        db.update(TABLE_REMIND, values, COLUMN_REMIND_ID + "='" + remindID + "'", null);
        db.close();
    }

    public void updateEvent(String eventID, String eventName, String eventDate) {
        SQLiteDatabase db = instance.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_EVENT_NAME, eventName);
        values.put(COLUMN_EVENT_DATE, eventDate);
        db.update(TABLE_EVENT, values, COLUMN_EVENT_ID + "='" + eventID + "'", null);
        db.close();
    }

    public void createUserData(String userName, String rmNoti,String eNoti) {
        SQLiteDatabase database = instance.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, userName);
        values.put(COLUMN_USER_REMIND_NOTIFICATION, rmNoti);
        values.put(COLUMN_USER_EVERY_DAY_NOTIFICATION, eNoti);
        database.insert(TABLE_USER_DATA, null, values);
        database.close();
    }
    public void setRemindNotification(String old , String rmNoti) {
        SQLiteDatabase db = instance.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_REMIND_NOTIFICATION, rmNoti);
        db.update(TABLE_USER_DATA, values, COLUMN_USER_REMIND_NOTIFICATION + "='" + old + "'", null);
        db.close();
    }
    public void setEveryDayNotification(String old , String eNoti) {
        SQLiteDatabase db = instance.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EVERY_DAY_NOTIFICATION, eNoti);
        db.update(TABLE_USER_DATA, values, COLUMN_USER_REMIND_NOTIFICATION + "='" + old + "'", null);
        db.close();
    }

    public void setUserName(String oldName, String name) {
        SQLiteDatabase db = instance.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, name);
        db.update(TABLE_USER_DATA, values, COLUMN_USER_NAME + "='" + oldName + "'", null);
        db.close();
    }

    public void refreshRemind(String plantID, String dateTime, String type) {
        SQLiteDatabase db = instance.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_REMIND_CREATE_DATE_TIME, dateTime);
        db.update(TABLE_REMIND, values, COLUMN_REMIND_PLANT_ID + "='" + plantID + "' AND " + COLUMN_REMIND_TYPE + "='" + type + "'", null);
    }

    public void deleteOneRemind(String remindID) {
        SQLiteDatabase db = instance.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_REMIND_ID, remindID);
        db.delete(TABLE_REMIND, COLUMN_REMIND_ID + "='" + remindID + "'", new String[]{});

        db.close();
    }
    public void deleteOneEvent(String eventID) {
        SQLiteDatabase db = instance.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_EVENT_ID, eventID);
        db.delete(TABLE_EVENT, COLUMN_EVENT_ID + "='" + eventID + "'", new String[]{});

        db.close();
    }

    public void deletePlantRemind(String planID) {
        SQLiteDatabase db = instance.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_REMIND_PLANT_ID, planID);
        db.delete(TABLE_REMIND, COLUMN_REMIND_PLANT_ID + "='" + planID + "'", new String[]{});

        db.close();
    }
    public void deletePlantEvent(String planID) {
        SQLiteDatabase db = instance.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_EVENT_PLANT_ID, planID);
        db.delete(TABLE_EVENT, COLUMN_EVENT_PLANT_ID + "='" + planID + "'", new String[]{});

        db.close();
    }

    public String getLastPlanID() {
        SQLiteDatabase db = instance.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PLANT + " ORDER BY " + COLUMN_PLANT_ID + " DESC LIMIT 1", null);
        String lastID = "";
        while (cursor.moveToNext()) {
            lastID = cursor.getString(0);
        }
        cursor.close();
        db.close();

        return lastID;
    }

    public Plant getOnePlant(String plantID) {
        SQLiteDatabase db = instance.getWritableDatabase();

        Cursor cursor = db.rawQuery(String.format("SELECT * FROM '%s';", TABLE_PLANT), null);
        Plant plant = null;
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                if (cursor.getString(cursor.getColumnIndex(COLUMN_PLANT_ID)).equals(plantID)) {
                    String plantImage = cursor.getString(cursor.getColumnIndex(COLUMN_PLANT_IMAGE));
                    String plantName = cursor.getString(cursor.getColumnIndex(COLUMN_PLANT_NAME));
                    String plantNote = cursor.getString(cursor.getColumnIndex(COLUMN_PLANT_NOTE));
                    List<Remind> reminds = getAllPlantRemind(plantID);
                    List<Event> events = getAllPlantEvent(plantID);
                    plant = new Plant(plantID, plantImage, plantName, plantNote, reminds,events);
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return plant;
    }



    public String getRemindNotification() {
        SQLiteDatabase db = instance.getWritableDatabase();

        Cursor cursor = db.rawQuery(String.format("SELECT * FROM '%s';", TABLE_USER_DATA), null);
        String rmNoti ="";
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                rmNoti = cursor.getString(cursor.getColumnIndex(COLUMN_USER_REMIND_NOTIFICATION));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return rmNoti;
    }

    public String getEveryDayNotification() {
        SQLiteDatabase db = instance.getWritableDatabase();

        Cursor cursor = db.rawQuery(String.format("SELECT * FROM '%s';", TABLE_USER_DATA), null);
        String eNoti ="";
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                eNoti = cursor.getString(cursor.getColumnIndex(COLUMN_USER_EVERY_DAY_NOTIFICATION));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return eNoti;
    }

    public String getUserName() {
        SQLiteDatabase db = instance.getWritableDatabase();

        Cursor cursor = db.rawQuery(String.format("SELECT * FROM '%s';", TABLE_USER_DATA), null);
        String name ="";
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                name = cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return name;
    }

    public List<Plant> getAllPlant() {
        SQLiteDatabase db = instance.getWritableDatabase();

        Cursor cursor = db.rawQuery(String.format("SELECT * FROM '%s';", TABLE_PLANT), null);
        List<Plant> plants = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String plantID = cursor.getString(cursor.getColumnIndex(COLUMN_PLANT_ID));
                String plantImage = cursor.getString(cursor.getColumnIndex(COLUMN_PLANT_IMAGE));
                String plantName = cursor.getString(cursor.getColumnIndex(COLUMN_PLANT_NAME));
                String plantNote = cursor.getString(cursor.getColumnIndex(COLUMN_PLANT_NOTE));
                List<Remind> reminds = getAllPlantRemind(plantID);
                List<Event> events = getAllPlantEvent(plantID);
                plants.add(new Plant(plantID, plantImage, plantName, plantNote, reminds,events));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return plants;
    }
    public List<Remind> getAllRemind() {
        SQLiteDatabase db = instance.getWritableDatabase();

        Cursor cursor = db.rawQuery(String.format("SELECT * FROM '%s';", TABLE_REMIND), null);
        List<Remind> reminds = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String remindID = cursor.getString(cursor.getColumnIndex(COLUMN_REMIND_ID));
                String remindType = cursor.getString(cursor.getColumnIndex(COLUMN_REMIND_TYPE));
                String remindCreateDateTime = cursor.getString(cursor.getColumnIndex(COLUMN_REMIND_CREATE_DATE_TIME));
                String remindTime = cursor.getString(cursor.getColumnIndex(COLUMN_REMIND_TIME));
                String careCycle = cursor.getString(cursor.getColumnIndex(COLUMN_REMIND_CARE_CYCLE));
                reminds.add(new Remind(remindID, remindType, remindCreateDateTime, remindTime, careCycle));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return reminds;
    }


    public List<Remind> getAllPlantRemind(String plantID) {
        SQLiteDatabase db = instance.getWritableDatabase();

        Cursor cursor = db.rawQuery(String.format("SELECT * FROM '%s';", TABLE_REMIND), null);
        List<Remind> reminds = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                if (cursor.getString(cursor.getColumnIndex(COLUMN_REMIND_PLANT_ID)).equals(plantID)) {
                    String remindID = cursor.getString(cursor.getColumnIndex(COLUMN_REMIND_ID));
                    String remindType = cursor.getString(cursor.getColumnIndex(COLUMN_REMIND_TYPE));
                    String remindCreateDateTime = cursor.getString(cursor.getColumnIndex(COLUMN_REMIND_CREATE_DATE_TIME));
                    String remindTime = cursor.getString(cursor.getColumnIndex(COLUMN_REMIND_TIME));
                    String careCycle = cursor.getString(cursor.getColumnIndex(COLUMN_REMIND_CARE_CYCLE));
                    reminds.add(new Remind(remindID, remindType, remindCreateDateTime, remindTime, careCycle));
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();

        return reminds;
    }

    private List<Event> getAllPlantEvent(String plantID) {
        SQLiteDatabase db = instance.getWritableDatabase();

        Cursor cursor = db.rawQuery(String.format("SELECT * FROM '%s';", TABLE_EVENT), null);
        List<Event> events = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                if (cursor.getString(cursor.getColumnIndex(COLUMN_EVENT_PLANT_ID)).equals(plantID)) {
                    String eventId = cursor.getString(cursor.getColumnIndex(COLUMN_EVENT_ID));
                    String eventName = cursor.getString(cursor.getColumnIndex(COLUMN_EVENT_NAME));
                    String eventDate = cursor.getString(cursor.getColumnIndex(COLUMN_EVENT_DATE));
                    String eventPosition = cursor.getString(cursor.getColumnIndex(COLUMN_EVENT_POSITION));
                    events.add(new Event(eventId, eventName, eventDate, eventPosition));
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();

        return events;
    }

    public Plant getLastPlant() {
        SQLiteDatabase db = instance.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PLANT + " ORDER BY " + COLUMN_PLANT_ID + " DESC LIMIT 1", null);
        Plant plant = null;
        while (cursor.moveToNext()) {
            String plantID = cursor.getString(cursor.getColumnIndex(COLUMN_PLANT_ID));
            String plantName = cursor.getString(cursor.getColumnIndex(COLUMN_PLANT_NAME));
            String plantImage = cursor.getString(cursor.getColumnIndex(COLUMN_PLANT_IMAGE));
            String plantNote = cursor.getString(cursor.getColumnIndex(COLUMN_PLANT_NOTE));
            List<Remind> reminds = getAllPlantRemind(plantID);
            List<Event> events = getAllPlantEvent(plantID);
            plant = new Plant(plantID, plantImage, plantName, plantNote, reminds,events);
        }


        cursor.close();
        db.close();

        return plant;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_PLAN);
        db.execSQL(SQL_CREATE_TABLE_REMIND);
        db.execSQL(SQL_CREATE_TABLE_USER_DATA);
        db.execSQL(SQL_CREATE_TABLE_EVENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLANT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMIND);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);

        onCreate(db);
    }



}
