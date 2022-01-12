package com.ryg.chapter_2.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "book_provider.db";
    public static final String BOOK_TABLE_NAME = "book";
    public static final String USER_TABLE_NAME = "user";

    private static final int DB_VERSION = 3;

    public DbOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BOOK_TABLE = "CREATE TABLE IF NOT EXISTS "
                + BOOK_TABLE_NAME + "(_id INTEGER PRIMARY KEY," + "name TEXT)";
        db.execSQL(CREATE_BOOK_TABLE);
        String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS "
                + USER_TABLE_NAME + "(_id INTEGER PRIMARY KEY," + "name TEXT,"
                + "sex INT)";
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
