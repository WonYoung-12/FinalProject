package com.example.kwy2868.finalproject.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.kwy2868.finalproject.Model.Chart;

/**
 * Created by kwy2868 on 2017-07-19.
 */

public class ChartDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "chartDB.db";
    private static final String DATABASE_TABLE = "chart";

    // 자동으로 증가되게 해주자.
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_PET_NAME = "petName";
    private static final String COLUMN_USER_ID = "userId";
    private static final String COLUMN_TREATMENT_DATE = "treatmentDate";
    private static final String COLUMN_RE_TREATMENT_DATE = "reTreatmentDate";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";

    public ChartDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "create table if not exists " + DATABASE_TABLE + " (" + COLUMN_ID
                + " integer primary key autoincrement, " + COLUMN_PET_NAME + " text, "
                + COLUMN_USER_ID + " BIGINT, " + COLUMN_TREATMENT_DATE + " text, " + COLUMN_RE_TREATMENT_DATE + " text, "
                + COLUMN_TITLE + " text, " + COLUMN_DESCRIPTION + " text" + ")";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + DATABASE_TABLE);
        onCreate(sqLiteDatabase);
    }

    public void addChart(Chart chart){
        ContentValues values = new ContentValues();
        values.put(COLUMN_PET_NAME, chart.getPetName());
        values.put(COLUMN_USER_ID, chart.getUserId());
        values.put(COLUMN_TREATMENT_DATE, chart.getTreatmentDate());
        values.put(COLUMN_RE_TREATMENT_DATE, chart.getReTreatmentDate());
        values.put(COLUMN_TITLE, chart.getTitle());
        values.put(COLUMN_DESCRIPTION, chart.getDescription());

        SQLiteDatabase database = this.getWritableDatabase();
        database.insert(DATABASE_TABLE, null, values);
        database.close();
        Log.d("차트 추가", "성공");
    }

    public Cursor findAll(){
        String query = "select * from " + DATABASE_TABLE;
        SQLiteDatabase database = this.getWritableDatabase();

        return database.rawQuery(query, null);
    }
}
