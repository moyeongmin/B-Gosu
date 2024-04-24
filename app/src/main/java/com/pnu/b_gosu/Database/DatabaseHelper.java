package com.pnu.b_gosu.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "submit_data.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE_CHARACTER =

            "CREATE TABLE character (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "character_id INTEGER);";



    // 테이블 생성 쿼리
    private static final String CREATE_TABLE_SUBMIT =
            "CREATE TABLE submit (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "field1 INTEGER," +
                    "field2 INTEGER," +
                    "field3 INTEGER," +
                    "field4 INTEGER," +
                    "field5 INTEGER," +
                    "field6 INTEGER," +
                    "field7 INTEGER," +
                    "field8 INTEGER," +
                    "field9 INTEGER);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 테이블 생성
        db.execSQL(CREATE_TABLE_SUBMIT);
        db.execSQL(CREATE_TABLE_CHARACTER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 데이터베이스 업그레이드 시 필요한 작업을 수행합니다.
    }
}