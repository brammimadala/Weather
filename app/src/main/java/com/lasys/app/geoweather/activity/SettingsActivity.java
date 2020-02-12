package com.lasys.app.geoweather.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.lasys.app.geoweather.R;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView _arrow_back_settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        _arrow_back_settings = findViewById(R.id.arrow_back_settings);
        _arrow_back_settings.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.arrow_back_settings: {
                finish();
                break;
            }
        }
    }
}
