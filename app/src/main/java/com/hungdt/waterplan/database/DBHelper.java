package com.hungdt.waterplan.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hungdt.waterplan.model.Plant;
import com.hungdt.waterplan.model.Remind;

import java.util.ArrayList;
import java.util.Collection;
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

    public void updateRemind(String remindID, String remindCreateDateTime, String remindTime, String careCycle) {
        SQLiteDatabase db = instance.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_REMIND_CREATE_DATE_TIME, remindCreateDateTime);
        values.put(COLUMN_REMIND_TIME, remindTime);
        values.put(COLUMN_REMIND_CARE_CYCLE, careCycle);
        db.update(TABLE_REMIND, values, COLUMN_REMIND_ID + "='" + remindID + "'", null);
        db.close();
    }

    public void refreshRemind(String plantID, String dateTime, String type) {
        //todo
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

    public void deletePlanRemind(String planID) {
        SQLiteDatabase db = instance.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_REMIND_PLANT_ID, planID);
        db.delete(TABLE_REMIND, COLUMN_REMIND_PLANT_ID + "='" + planID + "'", new String[]{});

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
                    plant = new Plant(plantID, plantImage, plantName, plantNote, reminds);
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return plant;
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
                plants.add(new Plant(plantID, plantImage, plantName, plantNote, reminds));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return plants;
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
            plant = new Plant(plantID, plantImage, plantName, plantNote, reminds);
        }


        cursor.close();
        db.close();

        return plant;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_PLAN);
        db.execSQL(SQL_CREATE_TABLE_REMIND);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLANT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMIND);

        onCreate(db);
    }


}
