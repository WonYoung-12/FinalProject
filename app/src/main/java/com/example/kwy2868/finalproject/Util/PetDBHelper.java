package com.example.kwy2868.finalproject.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.kwy2868.finalproject.Model.Pet;

/**
 * Created by kwy2868 on 2017-08-12.
 */

public class PetDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "petDB.db";
    private static final String DATABASE_TABLE = "pet";

    // 자동으로 증가되게 해주자.
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_AGE = "age";
    private static final String COLUMN_SPECIES = "species";
    private static final String COLUMN_USER_ID = "userId";

    public PetDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "create table if not exists " + DATABASE_TABLE + " (" + COLUMN_ID
                + " integer primary key autoincrement, " + COLUMN_NAME + " text, " + COLUMN_AGE + " integer, "
                + COLUMN_SPECIES + " text,"
                + COLUMN_USER_ID + " BIGINT" + ")";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + DATABASE_TABLE);
        onCreate(sqLiteDatabase);
    }

    public void addPet(Pet pet){
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, pet.getName());
        values.put(COLUMN_AGE, pet.getAge());
        values.put(COLUMN_SPECIES, pet.getSpecies());
        values.put(COLUMN_USER_ID, pet.getUserId());

        SQLiteDatabase database = this.getWritableDatabase();
        database.insert(DATABASE_TABLE, null, values);
        database.close();
    }

    public Cursor findAll() {
        String query = "select * from " + DATABASE_TABLE;
        SQLiteDatabase database = this.getWritableDatabase();

        return database.rawQuery(query, null);
    }
}
