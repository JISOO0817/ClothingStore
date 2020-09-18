package com.example.clothingstore;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {


    public DBHelper(@Nullable Context context) {
        super(context, DBConstants.TABLE_NAME, null, DBConstants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //테이블 생성

        db.execSQL(DBConstants.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + DBConstants.TABLE_NAME);

        //테이블 재 생성
        onCreate(db);
    }

    public long insertData(String itemId, String productId, String title, String priceEach, String price, String quantity){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(DBConstants.C_ID,itemId);
        values.put(DBConstants.C_PID,productId);
        values.put(DBConstants.C_NAME,title);
        values.put(DBConstants.C_PRICE_EACH,priceEach);
        values.put(DBConstants.C_PRICE,price);
        values.put(DBConstants.C_QUANTITY,quantity);

        long id = db.insert(DBConstants.TABLE_NAME,null,values);

        db.close();

        return id;


    }


}
