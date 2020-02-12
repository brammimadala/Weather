package com.lasys.app.geoweather.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lasys.app.geoweather.R;
import com.lasys.app.geoweather.constants.SharePreference;

public class AddressDetails extends AppCompatActivity implements View.OnClickListener {
    private TextView _ad_city,_ad_state,_ad_country,_ad_postalcode,_tv_address_details ;
    private ImageView _arrow_back ;
    private Button _buttonWeatherDetails ;
    private SharePreference sharePreference ;
    private Bundle bundle ;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_details);

        _tv_address_details = findViewById(R.id.tv_address_details);
        _ad_city = findViewById(R.id.ad_city);
        _ad_state = findViewById(R.id.ad_state);
        _ad_country = findViewById(R.id.ad_country);
        _ad_postalcode = findViewById(R.id.ad_postalcode);
        _arrow_back = findViewById(R.id.arrow_back_address_details);

        _buttonWeatherDetails = findViewById(R.id.getWeatherDetails);

        sharePreference = new SharePreference(AddressDetails.this);


         bundle = getIntent().getExtras();
        if (bundle != null)
        {
            _tv_address_details.setText(bundle.getString("address"));
            _ad_city.setText(bundle.getString("city"));
            _ad_state.setText(bundle.getString("state"));
            _ad_country.setText(bundle.getString("country"));
            _ad_postalcode.setText(bundle.getString("postalcode"));
        }

        _buttonWeatherDetails.setOnClickListener(this);
        _arrow_back.setOnClickListener(this);

    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.getWeatherDetails :
            {
                if (bundle != null)
                {
                   sharePreference.setLatitude(bundle.getString("latitude"));
                   sharePreference.setLongitude(bundle.getString("longitude"));

                    Intent intent = new Intent(AddressDetails.this,WeatherHome.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(this, "Address details are not found...", Toast.LENGTH_SHORT).show();
                }

                break;
            }
            case R.id.arrow_back_address_details :
            {

                finish();
                break;
            }
        }
    }
}
