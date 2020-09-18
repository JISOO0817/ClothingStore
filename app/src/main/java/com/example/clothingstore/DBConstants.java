package com.example.clothingstore;

public class DBConstants {

    public static final String DB_NAME = "ITEMS_DB";

    public static final int DB_VERSION = 1;

    public static final String TABLE_NAME = "ITEMS_TABLE";
    public static final String C_ID = "ID";
    public static final String C_PID = "PID";
    public static final String C_NAME = "NAME";
    public static final String C_PRICE_EACH = "PRICE_EACH";
    public static final String C_PRICE = "PRICE";
    public static final String C_QUANTITY = "QUANTITY";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + C_ID + " INTEGER PRIMARY KEY,"
            + C_PID + " TEXT,"
            + C_NAME + " TEXT,"
            + C_PRICE_EACH + " TEXT,"
            + C_PRICE + " TEXT,"
            + C_QUANTITY + " TEXT"
            + ")";






}
