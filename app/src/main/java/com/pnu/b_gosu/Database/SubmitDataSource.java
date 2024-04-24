package com.pnu.b_gosu.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SubmitDataSource {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public SubmitDataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // 데이터 추가
    public long insertSubmitData(int field1, int field2, int field3, int field4, int field5,
                                 int field6, int field7, int field8, int field9) {
        ContentValues values = new ContentValues();
        values.put("field1", field1);
        values.put("field2", field2);
        values.put("field3", field3);
        values.put("field4", field4);
        values.put("field5", field5);
        values.put("field6", field6);
        values.put("field7", field7);
        values.put("field8", field8);
        values.put("field9", field9);
        //ct_tag 1          7번     1
        //ct_tag 2          6_2     2
        //ct_tag 3          6_4     3
        //ct_tag 4          6_1     4
        //r_tag3            1번     5
        //r_tag4            2번     6
        //r_tag5            3번     7
        //r_tag3_1          4번     8
        //s_tag             5번     9

        return database.insert("submit", null, values);
    }
    public long insertCharacter(int characterId) {

        database.delete("character", null, null);

        ContentValues values = new ContentValues();
        values.put("character_id", characterId);
        return database.insert("character", null, values);
    }

    // 데이터 조회
    public Cursor getAllSubmitData() {
        return database.query("submit", null, null, null, null, null, null);
    }
    public Cursor getAllCharacters() {
        return database.query("character", null, null, null, null, null, null);
    }


    // 필요한 다른 데이터베이스 조작 메서드를 추가할 수 있습니다.
}
