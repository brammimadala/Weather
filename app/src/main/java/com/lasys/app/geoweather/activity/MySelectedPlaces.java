package com.lasys.app.geoweather.activity;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.lasys.app.geoweather.R;
import com.lasys.app.geoweather.adapter.MyLocationsAdapter;
import com.lasys.app.geoweather.constants.SharePreference;
import com.lasys.app.geoweather.model.locaddress.AddressModel;
import com.lasys.app.geoweather.sqldb.LocationDetailsDB;

import java.util.ArrayList;


public class MySelectedPlaces extends AppCompatActivity implements View.OnClickListener {

    private ImageView _arrow_back_selectPlaces;
    private ArrayList<AddressModel> addressModelArrayList;

    private LocationDetailsDB locationDetailsDB;
    private SQLiteDatabase sqLiteDatabase;

    SharePreference sharePreference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_selected_places);

        addressModelArrayList = new ArrayList<>();

        _arrow_back_selectPlaces = findViewById(R.id.arrow_back_selectPlaces);
        _arrow_back_selectPlaces.setOnClickListener(this);

        myLocationsRrecycleViewdata();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.arrow_back_selectPlaces: {
                finish();
                break;
            }
        }
    }

    public void myLocationsRrecycleViewdata() {
        locationDetails();

        RecyclerView recyclerView = findViewById(R.id.mySelectedPlaces);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(MySelectedPlaces.this);
        recyclerView.setLayoutManager(lm);

        MyLocationsAdapter expAdapter = new MyLocationsAdapter(MySelectedPlaces.this, addressModelArrayList);
        recyclerView.setAdapter(expAdapter);

    }

    public void locationDetails() {
        locationDetailsDB = new LocationDetailsDB(MySelectedPlaces.this);
        sqLiteDatabase = locationDetailsDB.getWritableDatabase();

        Cursor c = sqLiteDatabase.query(LocationDetailsDB.TABLE_NAME1, null, null, null, null, null, null);

        if (c.moveToFirst()) {
            do {
                int id = c.getInt(0);
                String latitude = c.getString(1);
                String longitude = c.getString(2);
                String addressLine = c.getString(3);
                String city = c.getString(4);
                String state = c.getString(5);
                String country = c.getString(6);
                String postalCode = c.getString(7);

                AddressModel addressModel = new AddressModel();

                addressModel.setSid(id);
                addressModel.setLatitude(latitude);
                addressModel.setLongitude(longitude);
                addressModel.setAddress(addressLine);
                addressModel.setLocality(city);
                addressModel.setAdmin(state);
                addressModel.setCountryName(country);
                addressModel.setPostalCode(postalCode);

                addressModelArrayList.add(addressModel);

            } while (c.moveToNext());

        } else {
            Toast.makeText(MySelectedPlaces.this, "Add New Locations ", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MySelectedPlaces.this, MapView.class);
            startActivity(intent);
            finish();
        }

    }
}
