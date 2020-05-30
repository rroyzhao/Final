package com.neusoft.zhaorui.afinal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.SQLException;

public class DataBase extends SQLiteOpenHelper {
    // 创建类
    private static final String DB_NAME = "Final.db";
    private static final String TABLE_NAME = "Info";
    private static final int DB_VERSION = 1;

    public DataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DataBase(Context context) {
        this(context, DB_NAME, null, DB_VERSION);
    }

    // 创建数据库: 可直接执行创建数据库的 SQl 语句
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE if not exists "
                + TABLE_NAME
                + "(name TEXT,"
                + "sex TEXT,"
                + "nation TEXT,"
                + "birthday TEXT,"
                + "address TEXT,"
                + "number TEXT)";
        try {
            sqLiteDatabase.execSQL(CREATE_TABLE);
        } catch (SQLException e) {

        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}