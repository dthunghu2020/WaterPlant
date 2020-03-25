package com.hungdt.waterplan.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

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
    public static final String COLUMN_REMIND_DATE_TIME = "REMIND_DATE_TIME";
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
            + COLUMN_REMIND_DATE_TIME + " TEXT NOT NULL, "
            + COLUMN_REMIND_CARE_CYCLE + " TEXT NOT NULL" + ");";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /*public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }*/

    public void addPlan(String planName, String planAvatar, String planNote) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PLANT_NAME, planName);
        values.put(COLUMN_PLANT_IMAGE, planAvatar);
        values.put(COLUMN_PLANT_NOTE, planNote);
        database.insert(TABLE_PLANT, null, values);
        database.close();
    }

    public void updatePlan(String planID, String planName, String planAvatar, String planNote) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PLANT_ID, planID);
        values.put(COLUMN_PLANT_NAME, planName);
        values.put(COLUMN_PLANT_IMAGE, planAvatar);
        values.put(COLUMN_PLANT_NOTE, planNote);
        db.update(TABLE_PLANT, values, COLUMN_PLANT_ID + "='" + planID + "'", null);
        db.close();
    }

    public void deletePlan(String planID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PLANT_ID, planID);
        db.delete(TABLE_PLANT, COLUMN_PLANT_ID + "='" + planID + "'", new String[]{});

        db.close();
    }

    public void addRemind(String planID, String remindType, String remindDateTime, String careCycle) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_REMIND_PLANT_ID, planID);
        values.put(COLUMN_REMIND_TYPE, remindType);
        values.put(COLUMN_REMIND_DATE_TIME, remindDateTime);
        values.put(COLUMN_REMIND_CARE_CYCLE, careCycle);
        database.insert(TABLE_REMIND, null, values);
        database.close();
    }

    public void updateRemind(String remindID, String remindDateTime, String careCycle) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_REMIND_DATE_TIME, remindDateTime);
        values.put(COLUMN_REMIND_CARE_CYCLE, careCycle);
        db.update(TABLE_PLANT, values, COLUMN_REMIND_ID + "='" + remindID + "'", null);
        db.close();
    }

    public void deleteOneRemind(String remindID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_REMIND_ID, remindID);
        db.delete(TABLE_REMIND, COLUMN_REMIND_ID + "='" + remindID + "'", new String[]{});

        db.close();
    }

    public void deletePlanRemind(String planID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_REMIND_PLANT_ID, planID);
        db.delete(TABLE_REMIND, COLUMN_REMIND_PLANT_ID + "='" + planID + "'", new String[]{});

        db.close();
    }

    public int getLastPlanID() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(String.format("SELECT * FROM '%s';", TABLE_PLANT), null);

        cursor.moveToLast();
        int lastID = cursor.getColumnIndex(COLUMN_PLANT_ID);

        cursor.close();
        db.close();

        return lastID;
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
