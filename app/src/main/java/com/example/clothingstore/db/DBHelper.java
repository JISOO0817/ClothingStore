package com.example.clothingstore.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.clothingstore.activities.MarketDetailActivity;
import com.example.clothingstore.models.ModelCartItem;
import com.example.clothingstore.models.ModelChat;
import com.example.clothingstore.models.ModelProduct;

import java.util.ArrayList;

public class DBHelper  extends SQLiteOpenHelper {

    public DBHelper(@Nullable Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(Constants.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);
        onCreate(db);
    }


    public long insertData(String productId, String productName, String priceEach, String totalPrice, String quantity){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        //values.put(Constants.C_ID,itemId);
        values.put(Constants.C_PID,productId);
        values.put(Constants.C_NAME,productName);
        values.put(Constants.C_PRICE_EACH,priceEach);
        values.put(Constants.C_PRICE,totalPrice);
        values.put(Constants.C_QUANTITY,quantity);

        long itemId = db.insert(Constants.TABLE_NAME,null,values);

        db.close();
        return itemId;
    }

    public void delete(String itemId){
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(Constants.TABLE_NAME,Constants.C_ID + " =? ", new String[]{itemId});
        database.close();
    }

    public void deleteAllData(){
        SQLiteDatabase database = this.getReadableDatabase();
        database.execSQL("DELETE FROM " + Constants.TABLE_NAME);
    }

    public ArrayList<ModelCartItem> getAllData() {

        ArrayList<ModelCartItem> cartList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + Constants.TABLE_NAME ;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);

         if(cursor.moveToFirst()){

            do{
                ModelCartItem model = new ModelCartItem(
                        ""+ cursor.getString(cursor.getColumnIndex(Constants.C_ID)),
                        ""+  cursor.getString(cursor.getColumnIndex(Constants.C_PID)),
                        ""+ cursor.getString(cursor.getColumnIndex(Constants.C_NAME)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.C_PRICE_EACH)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.C_PRICE)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.C_QUANTITY))
                );

                cartList.add(model);
            }while (cursor.moveToNext());

         }

        db.close();
        return cartList;

    }


}
