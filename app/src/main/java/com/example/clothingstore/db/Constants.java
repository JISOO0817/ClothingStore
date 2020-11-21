package com.example.clothingstore.db;

public class Constants {


    public static final String DB_NAME = "clothing_db";

    public static final int DB_VERSION = 1;


    public static final String TABLE_NAME = "clothing_table";

    public static final String C_ID = "id";
    public static final String C_PID = "pid";
    public static final String C_NAME = "name";
    public static final String C_PRICE_EACH = "priceEach";
    public static final String C_PRICE="price";
    public static final String C_QUANTITY = "quantity";



    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + C_PID + " TEXT,"
            + C_NAME + " TEXT,"
            + C_PRICE_EACH + " TEXT,"
            + C_PRICE + " TEXT,"
            + C_QUANTITY + " TEXT"
            + ");";

}
