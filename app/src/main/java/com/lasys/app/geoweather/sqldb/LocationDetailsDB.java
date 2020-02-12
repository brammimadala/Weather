package com.lasys.app.geoweather.sqldb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocationDetailsDB extends SQLiteOpenHelper
{
    public static final String DB_NAME = "MY_ADDRESSES_DB";
    public static final int VERSION = 1;

    public static final String TABLE_NAME1 = "MY_LOCATIONS";



    public static final String TABLE1_COL1 ="IDNO";
    public static final String TABLE1_COL2 ="LATITUDE";      //latitude
    public static final String TABLE1_COL3 ="LONGITUDE";     //longitude
    public static final String TABLE1_COL4 ="ADDRESS";       //addressLine
    public static final String TABLE1_COL5 ="CITY";          //TOWN OR CITY
    public static final String TABLE1_COL6 ="STATE";         //ADMIN
    public static final String TABLE1_COL7 ="COUNTRY";       //countryName
    public static final String TABLE1_COL8 ="POSTAL_CODE";    //postalCode


    public LocationDetailsDB(Context context)
    {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String qry1 = "create table "+TABLE_NAME1+"("
                +TABLE1_COL1+" INTEGER "+" PRIMARY KEY " + "AUTOINCREMENT,"
                +TABLE1_COL2+" TEXT,"
                +TABLE1_COL3+" TEXT,"
                +TABLE1_COL4+" TEXT,"
                +TABLE1_COL5+" TEXT,"
                +TABLE1_COL6+" TEXT,"
                +TABLE1_COL7+" TEXT,"
                +TABLE1_COL8+" INTEGER)";
        db.execSQL(qry1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {

    }
}
