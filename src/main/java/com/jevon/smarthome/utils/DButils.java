package com.jevon.smarthome.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jevon.smarthome.db.MySql;

public class DButils {

    private Context mContext;

    MySql mySql;
    SQLiteDatabase db;

    public DButils(Context context) {
        mContext = context;
        mySql = new MySql(mContext);
        db = mySql.getWritableDatabase();
    }

    public Cursor read() {
        Cursor cursor = db.query("Devices", null, null,
                null, null, null, null);
        return cursor;
    }

    public int read(String selection, String[] selectionargs) {
        Cursor cursor = db.query("Devices", null, selection,
                selectionargs, null, null, null);
        return cursor.getCount();
    }

    public long write(String name, Integer device, Integer io, Integer id) {
        ContentValues values = new ContentValues();
        values.put("Name", name);
        values.put("Device", device);
        values.put("Io", io);
        values.put("Id", id);
        return db.insert("Devices", null, values);
    }

    public void delete(String whereClause, String[] whereArgs) {
        db.delete("Devices", whereClause, whereArgs);
    }

    public void updata(String name, Integer id) {
        ContentValues values = new ContentValues();
        values.put("Name", name);
        db.update("Devices", values, "Id=" + id, null);


    }
}
