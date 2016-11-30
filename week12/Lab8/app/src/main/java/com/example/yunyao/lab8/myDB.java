package com.example.yunyao.lab8;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yunyao on 2016/11/21.
 */
public class myDB extends SQLiteOpenHelper {
    private static final String DB_NAME = "lab8";
    private static final String TABLE_NAME = "Content";
    private static final int DB_VERSION = 1;

    public myDB(Context context) {
        //第一个参数是上下文对象，用于创建数据库，第二个参数是数据库名称，第三个参数是游标，第四个参数是版本
        super(context, DB_NAME+".db", null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //建立表
        String CREATE_TABLE = "create table if not exists "
                + TABLE_NAME
                + "(name text primary key, birthday text, gift text, number text, flag TINYINT)";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //删除表
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}