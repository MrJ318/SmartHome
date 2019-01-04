package com.jevon.smarthome.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jevon.smarthome.utils.Jlog;

public class MySql extends SQLiteOpenHelper {


    public MySql(Context context) {
        super(context, "EspushDevice", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table Devices (Name text,Device integer,Io integer,Id integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
