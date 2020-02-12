package com.lasys.app.geoweather.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lasys.app.geoweather.R;
import com.lasys.app.geoweather.adapter.WeatherForecastTimeAdapter;
import com.lasys.app.geoweather.constants.ForecasDataReportModel;
import com.lasys.app.geoweather.constants.PrograssDilg;
import com.lasys.app.geoweather.intrface.AppConstants;
import com.lasys.app.geoweather.intrface.TimeWeatherOnclickListner;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class WeatherForecast extends AppCompatActivity implements View.OnClickListener
{
    private TextView location_name,current_temp,min_max_temp,clouds_description,clouds_number,wind_direction,wind_speed,humidity,pressure,sunrise,sunset,time_weather_location_date;
    private PrograssDilg prograssDilg ;
    private ImageView weather_cloud_imv ;
    private ImageView back_forecast_weather ;

    Map<String, com.lasys.app.geoweather.model.ForecastWeather.List> weatherTimemap ;
    private Object[] dateTimes;

    private RecyclerView time_horizontalRecyclerView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        back_forecast_weather = findViewById(R.id.back_forecast_weather_iv);

        time_horizontalRecyclerView =  findViewById(R.id.time_horizontalRecyclerView);


        location_name = findViewById(R.id.time_weather_location_name);
        current_temp = findViewById(R.id.time_weather_current_temp);
        min_max_temp = findViewById(R.id.time_weather_min_max_temp);
        weather_cloud_imv = findViewById(R.id.time_weather_cloud_iv);

        time_weather_location_date = findViewById(R.id.time_weather_location_date);



        clouds_description= findViewById(R.id.time_weather_clouds_description);
        clouds_number= findViewById(R.id.time_weather_clouds_number);
        wind_direction = findViewById(R.id.time_weather_wind_direction);
        wind_speed = findViewById(R.id.time_weather_wind_speed);
        humidity = findViewById(R.id.time_weather_humidity);
        pressure = findViewById(R.id.time_weather_pressure);

        Intent intent = getIntent();
        ForecasDataReportModel forecasDataReportModel=(ForecasDataReportModel) intent.getSerializableExtra("list") ;
        forecasDataReportModel.getDateKey();


        weatherTimemap = forecasDataReportModel.getTimeWeatherDataMap();
        dateTimes= weatherTimemap.keySet().toArray();


        Log.i("Response class===>",""+forecasDataReportModel);
        Log.i("Response DateKey ==>",""+forecasDataReportModel.getDateKey());
        Log.i("Response Time ===>",""+forecasDataReportModel.getTimeWeatherDataMap().entrySet());

        forecastRecyclerviewData();

        setData(0);

        back_forecast_weather.setOnClickListener(this);
    }

    //  Weather details set
    private  void  setData(int position)
    {
        String dateKey=dateTimes[position].toString();
        com.lasys.app.geoweather.model.ForecastWeather.List perTimeWeatherReport= weatherTimemap.get(dateKey);



        //location_name.setText();
        current_temp.setText(""+perTimeWeatherReport.getMain().getTemp()+"\u00b0");
        min_max_temp.setText(""+perTimeWeatherReport.getMain().getTempMin()+"\u00b0/ "+perTimeWeatherReport.getMain().getTempMax()+"\u00b0");

        //Log.i("Name == >",""+weatherCurrentModel.getName());
        //Log.i("Temp == >",""+perTimeWeatherReport.getMain().getTemp()+"\u00b0");
        //Log.i("Min Max == >",""+perTimeWeatherReport.getMain().getTempMin()+"\u00b0/ "+perTimeWeatherReport.getMain().getTempMax()+"\u00b0");


        time_weather_location_date.setText(getDateWithFormat(dateKey));

        // Weather weather = weatherList.get(0);
        clouds_description.setText(perTimeWeatherReport.getWeather().get(0).getDescription());

        if (perTimeWeatherReport.getWeather().get(0).getIcon() != null)
        {
            String imageUrl = AppConstants.weather_BaseUrl+""+"/img/w/"+perTimeWeatherReport.getWeather().get(0).getIcon();

            Picasso.with(this)
                    .load(imageUrl)
                    .resize(500,500).noFade().into(weather_cloud_imv);
        }


        clouds_number.setText(""+perTimeWeatherReport.getClouds().getAll());

        wind_direction.setText(getWindDirection(perTimeWeatherReport.getWind().getDeg()));

        wind_speed.setText(""+perTimeWeatherReport.getWind().getSpeed()+" meter/sec");

        humidity.setText(perTimeWeatherReport.getMain().getHumidity()+" %");
        pressure.setText(perTimeWeatherReport.getMain().getPressure()+" hpa");

        //SimpleDateFormat dateFormat=new SimpleDateFormat("h:m:s a");

        //sunrise.setText(""+dateFormat.format(new java.util.Date((long)sys.getSunrise()*1000)));
        //sunset.setText(""+dateFormat.format(new java.util.Date((long)sys.getSunset()*1000)));

    }

    private String getWindDirection(double windDegree)
    {

        if (windDegree >= 348.76 ||  windDegree <= 11.25 )
        {
            return "NORTH";
        }
        if (windDegree >= 11.26 && windDegree <= 78.75 )
        {
            return "NORTH-EAST";
        }
        if (windDegree >= 78.76 && windDegree <= 101.25 )
        {
            return "EAST";
        }
        if (windDegree >= 101.26 && windDegree <= 146.25 )
        {
            return "SOUTH-EAST";
        }
        if (windDegree >= 146.26  && windDegree <= 191.25 )
        {
            return "SOUTH";
        }
        if (windDegree >= 191.26  && windDegree <= 236.25 )
        {
            return "SOUTH-WEST";
        }
        if (windDegree >= 236.26 && windDegree <= 281.25 )
        {
            return "WEST";
        }
        if (windDegree >= 281.26 && windDegree <= 348.75 )
        {
            return "NORTH-WEST";
        }

        return "" ;
    }

    public String getDateWithFormat(String date)
    {
        try {
            SimpleDateFormat dateFrmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat dateFrmt1 = new SimpleDateFormat("E, dd MMM");
            Date d1=dateFrmt.parse(date);

            return dateFrmt1.format(d1);

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void forecastRecyclerviewData()
    {
        RecyclerView.LayoutManager lm = new LinearLayoutManager(WeatherForecast.this, LinearLayoutManager.HORIZONTAL,false);
        time_horizontalRecyclerView.setLayoutManager(lm);

        WeatherForecastTimeAdapter weatherForecastDatesAdapter = new WeatherForecastTimeAdapter(WeatherForecast.this,weatherTimemap );
        time_horizontalRecyclerView.setAdapter(weatherForecastDatesAdapter);

        weatherForecastDatesAdapter.setOnItemClickListner(new TimeWeatherOnclickListner()
        {
            @Override
            public void OnItemClick(int position)
            {
                setData(position);
            }
        });

    }


    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.back_forecast_weather_iv:
            {
                finish();
                break;
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        finish();
        super.onBackPressed();
    }
}
