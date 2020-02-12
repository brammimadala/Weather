package com.lasys.app.geoweather.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lasys.app.geoweather.R;
import com.lasys.app.geoweather.adapter.WeatherHomeDatesAdapter;
import com.lasys.app.geoweather.constants.ForecasDataReportModel;
import com.lasys.app.geoweather.constants.GPSLatLong;
import com.lasys.app.geoweather.constants.InternetPermission;
import com.lasys.app.geoweather.constants.PrograssDilg;
import com.lasys.app.geoweather.constants.RequestPermission;
import com.lasys.app.geoweather.constants.SharePreference;
import com.lasys.app.geoweather.intrface.AppConstants;
import com.lasys.app.geoweather.model.CurrentWeather.Clouds;
import com.lasys.app.geoweather.model.CurrentWeather.Coord;
import com.lasys.app.geoweather.model.CurrentWeather.Main;
import com.lasys.app.geoweather.model.CurrentWeather.Sys;
import com.lasys.app.geoweather.model.CurrentWeather.Weather;
import com.lasys.app.geoweather.model.CurrentWeather.WeatherCurrentModel;
import com.lasys.app.geoweather.model.CurrentWeather.Wind;
import com.lasys.app.geoweather.model.ForecastWeather.City;
import com.lasys.app.geoweather.model.ForecastWeather.FweatherReport;
import com.lasys.app.geoweather.model.locaddress.AddressModel;
import com.lasys.app.geoweather.network.ApiClient;
import com.lasys.app.geoweather.network.ApiInterface;
import com.lasys.app.geoweather.sqldb.LocationDetailsDB;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherHome extends AppCompatActivity implements View.OnClickListener {
    private TextView location_name, current_temp, min_max_temp, clouds_description, clouds_number, wind_direction, wind_speed, humidity, pressure, sunrise, sunset;
    private ImageView weather_cloud_imv, settings, myPlaces;

    private PrograssDilg prograssDilg;
    private SharePreference sharePreference;

    String message = "No internet connection!";
    int duration = Snackbar.LENGTH_LONG;

    //Forecast Weather Report
    private FweatherReport fweatherReport;
    private City city;
    private com.lasys.app.geoweather.model.ForecastWeather.List forecastrList;
    private List<com.lasys.app.geoweather.model.ForecastWeather.List> ForecastWeatherList;
    private Map<String, Map<String, com.lasys.app.geoweather.model.ForecastWeather.List>> ForcastWheatherMap;

    private WeatherCurrentModel weatherCurrentModel;
    private List<Weather> weatherList;
    private Coord coord;

    private Main main;
    private Wind wind;
    private Clouds clouds;
    private Sys sys;

    private RecyclerView foreCastRecyclerView;
    public ArrayList<ForecasDataReportModel> forecastWeatherdataListReportModel;

    private View _view;
    private static final int Request_user_location_code = 99;

    private LocationDetailsDB locationDetailsDB;
    private SQLiteDatabase sqLiteDatabase;

    String units = "Metric";
    String Appid = "7b47bed1e9e281c15dba959f983b5de9";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_home);

        _view = findViewById(R.id.mainLayout);

        foreCastRecyclerView = findViewById(R.id.foreCastRecyclerView);

        sharePreference = new SharePreference(WeatherHome.this);

        weatherList = new ArrayList<>();
        ForecastWeatherList = new ArrayList<com.lasys.app.geoweather.model.ForecastWeather.List>();
        //ForcastWheatherMap= new HashMap<>();
        ForcastWheatherMap = new LinkedHashMap<>();
        ForcastWheatherMap.clear();

        forecastWeatherdataListReportModel = new ArrayList<>();
        forecastWeatherdataListReportModel.clear();

        myPlaces = findViewById(R.id.iv_places);
        settings = findViewById(R.id.iv_settings);

        location_name = findViewById(R.id.weather_location_name);
        current_temp = findViewById(R.id.weather_current_temp);
        min_max_temp = findViewById(R.id.weather_min_max_temp);
        weather_cloud_imv = findViewById(R.id.weather_cloud_iv);

        clouds_description = findViewById(R.id.weather_clouds_description);
        clouds_number = findViewById(R.id.weather_clouds_number);
        wind_direction = findViewById(R.id.weather_wind_direction);
        wind_speed = findViewById(R.id.weather_wind_speed);
        humidity = findViewById(R.id.weather_humidity);
        pressure = findViewById(R.id.weather_pressure);
        sunrise = findViewById(R.id.weather_sunrise);
        sunset = findViewById(R.id.weather_sunset);


        RequestPermission permission = new RequestPermission(this);
        permission.requestPermission();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            statusCheck();
        }

        //http://api.openweathermap.org/data/2.5/weather?lat=17.41&lon=78.48&units=Metric&APPID=f779ae300c64645672d740bb3e5174ad


        String latitude = sharePreference.getLatitude();

        String longitude = sharePreference.getLongitude();


        if (latitude != null && longitude != null) {
            if (InternetPermission.isOnline(WeatherHome.this)) {
                weathernetworkCalling(sharePreference.getLatitude().trim(), sharePreference.getLongitude().trim(), units, Appid);

                foreCastweathernetworkCalling(sharePreference.getLatitude().trim(), sharePreference.getLongitude().trim(), units, Appid);
            } else {
                showSnackbar(_view, message, duration);
            }
        } else {
            getAddressFirstLocation();
        }


        myPlaces.setOnClickListener(this);
        settings.setOnClickListener(this);
    }

    public void showSnackbar(View view, String message, int duration) {
        Snackbar.make(view, message, duration).show();
    }

    private void weathernetworkCalling(String lat, String longi, String units, String Appid) {
        //String BASE_URL = "http://api.openweathermap.org";
        String BASE_URL = AppConstants.weather_BaseUrl;

        // Progress Dialog showing up_to response getting... START//
        prograssDilg = new PrograssDilg(WeatherHome.this, "Loading...");

        ApiInterface apiService = ApiClient.getClient(BASE_URL).create(ApiInterface.class);
        Call<WeatherCurrentModel> call = apiService.getCurrentWeather(lat, longi, units, Appid);

        call.enqueue(new Callback<WeatherCurrentModel>() {

            //Response Getting
            @Override
            public void onResponse(Call<WeatherCurrentModel> call, Response<WeatherCurrentModel> response) {
                switch (response.code()) {
                    case 200: {
                        if (response.body() != null) {
                            weatherCurrentModel = response.body();
                            coord = weatherCurrentModel.getCoord();
                            main = weatherCurrentModel.getMain();
                            wind = weatherCurrentModel.getWind();
                            clouds = weatherCurrentModel.getClouds();
                            sys = weatherCurrentModel.getSys();

                            weatherList.clear();

                            for (Weather weather : response.body().getWeather()) {
                                weatherList.add(weather);
                            }

                            setData();

                        } else {
                            Toast.makeText(WeatherHome.this, "null response", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                   /* default:
                    {
                        Toast.makeText(WeatherHome.this, "not responding....", Toast.LENGTH_SHORT).show();
                        break;
                    }*/
                }
            }

            //Error Response
            @Override
            public void onFailure(Call<WeatherCurrentModel> call, Throwable t) {
                Toast.makeText(WeatherHome.this, "Error Response from server", Toast.LENGTH_SHORT).show();

                //IF ERROR RESPONSE GET PROGRESS_DIALOG DISMISS//
                prograssDilg.PrograssDilgDismiss();
            }
        });

        prograssDilg.PrograssDilgDismiss();
    }

    // Current Weather
    private void setData() {

        location_name.setText(weatherCurrentModel.getName());
        current_temp.setText("" + main.getTemp() + "\u00b0");
        min_max_temp.setText("" + main.getTempMin() + "\u00b0/ " + main.getTempMax() + "\u00b0");

        Log.i("Name == >", "" + weatherCurrentModel.getName());
        Log.i("Temp == >", "" + main.getTemp());
        Log.i("Min Max == >", "" + main.getTempMin() + "/ " + main.getTempMax());

        Weather weather = weatherList.get(0);
        clouds_description.setText(weather.getDescription());

        if (weather.getIcon() != null) {
            String imageUrl = AppConstants.weather_BaseUrl + "" + "/img/w/" + weather.getIcon();

            Picasso.with(this)
                    .load(imageUrl)
                    .resize(100, 100).noFade().into(weather_cloud_imv);
        }


        clouds_number.setText("" + clouds.getAll());

        wind_direction.setText(getWindDirection(wind.getDeg()));

        wind_speed.setText("" + wind.getSpeed() + " meter/sec");

        humidity.setText(main.getHumidity() + " %");
        pressure.setText(main.getPressure() + " hpa");

        SimpleDateFormat dateFormat = new SimpleDateFormat("h:m:s a");

        sunrise.setText("" + dateFormat.format(new java.util.Date((long) sys.getSunrise() * 1000)));
        sunset.setText("" + dateFormat.format(new java.util.Date((long) sys.getSunset() * 1000)));

    }

    private String getWindDirection(double windDegree) {

        if (windDegree >= 348.76 || windDegree <= 11.25) {
            return "NORTH";
        }
        if (windDegree >= 11.26 && windDegree <= 78.75) {
            return "NORTH-EAST";
        }
        if (windDegree >= 78.76 && windDegree <= 101.25) {
            return "EAST";
        }
        if (windDegree >= 101.26 && windDegree <= 146.25) {
            return "SOUTH-EAST";
        }
        if (windDegree >= 146.26 && windDegree <= 191.25) {
            return "SOUTH";
        }
        if (windDegree >= 191.26 && windDegree <= 236.25) {
            return "SOUTH-WEST";
        }
        if (windDegree >= 236.26 && windDegree <= 281.25) {
            return "WEST";
        }
        if (windDegree >= 281.26 && windDegree <= 348.75) {
            return "NORTH-WEST";
        }

        return "";
    }

    private void foreCastweathernetworkCalling(String lat, String longi, String units, String Appid) {
        //String BASE_URL = "http://api.openweathermap.org";
        String BASE_URL = AppConstants.weather_BaseUrl;

        // Progress Dialog showing up_to response getting... START//
        prograssDilg = new PrograssDilg(WeatherHome.this, "Loading...");

        ApiInterface apiService = ApiClient.getClient(BASE_URL).create(ApiInterface.class);
        Call<FweatherReport> call = apiService.getForeCastWeather(lat, longi, units, Appid);

        call.enqueue(new Callback<FweatherReport>() {

            //Response Getting
            @Override
            public void onResponse(Call<FweatherReport> call, Response<FweatherReport> response) {
                switch (response.code()) {
                    case 200: {
                        if (response.body() != null) {
                            fweatherReport = response.body();

                            city = fweatherReport.getCity();

                            for (com.lasys.app.geoweather.model.ForecastWeather.List forecastrList : fweatherReport.getList()) {
                                String date = forecastrList.getDtTxt().split(" ")[0];
                                Log.i("date ==>", "" + date);

                                if (ForcastWheatherMap.containsKey(date)) {
                                    ForcastWheatherMap.get(date).put(forecastrList.getDtTxt(), forecastrList);
                                } else {
                                    Map<String, com.lasys.app.geoweather.model.ForecastWeather.List> subMap = new LinkedHashMap<>();
                                    subMap.put(forecastrList.getDtTxt(), forecastrList);
                                    ForcastWheatherMap.put(date, subMap);
                                }
                            }

                            forecastWeatherdataListReportModel.clear();
                            for (Map.Entry<String, Map<String, com.lasys.app.geoweather.model.ForecastWeather.List>> w : ForcastWheatherMap.entrySet()) {
                                // Log.i("Key : ",""+w.getKey()+" , "+w.getValue());

                                ForecasDataReportModel forecasDataReportModel = new ForecasDataReportModel();

                                forecasDataReportModel.setDateKey(w.getKey());
                                forecasDataReportModel.setTimeWeatherDataMap(w.getValue());

                                Log.i("ModelClassData===> ", "ModelClassData --- " + w.getKey() + "--" + w.getValue());

                                //Toast.makeText(WeatherActivity.this, ""+w.getKey()+"--"+w.getValue(), Toast.LENGTH_SHORT).show();

                                forecastWeatherdataListReportModel.add(forecasDataReportModel);

                                for (Map.Entry<String, com.lasys.app.geoweather.model.ForecastWeather.List> subw : w.getValue().entrySet()) {
                                    //  Log.i(" sub key : ",""+subw.getKey()+" , "+subw.getValue().getMain().getTemp());
                                }
                            }

                            //Toast.makeText(WeatherActivity.this, ""+ forecastWeatherdataListReportModel.size(), Toast.LENGTH_SHORT).show();

                            //forecastarrayData();

                            forecastRecyclerviewData();

                        }
                        break;
                    }
                    default: {
                        Toast.makeText(WeatherHome.this, "Not Responding....", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }


            }

            //Error Response
            @Override
            public void onFailure(Call<FweatherReport> call, Throwable t) {
                Toast.makeText(WeatherHome.this, "Error Response from server" + t.getMessage(), Toast.LENGTH_LONG).show();

                Log.i("Error Response == > ", "" + t.getMessage());
                //IF ERROR RESPONSE GET PROGRESS_DIALOG DISMISS//
                prograssDilg.PrograssDilgDismiss();
            }
        });

        prograssDilg.PrograssDilgDismiss();
    }

    private void forecastRecyclerviewData() {
        RecyclerView.LayoutManager lm = new LinearLayoutManager(WeatherHome.this, LinearLayoutManager.HORIZONTAL, false);
        foreCastRecyclerView.setLayoutManager(lm);

        WeatherHomeDatesAdapter weatherHomeDatesAdapter = new WeatherHomeDatesAdapter(WeatherHome.this, forecastWeatherdataListReportModel);
        foreCastRecyclerView.setAdapter(weatherHomeDatesAdapter);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_places: {

                Intent intent = new Intent(WeatherHome.this, MapView.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            }
            case R.id.iv_settings: {
                Intent intent = new Intent(WeatherHome.this, MySelectedPlaces.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            }

        }

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WeatherHome.this);

        builder.setTitle("Please confirm");
        builder.setMessage("Are you want to exit the app?");
        builder.setCancelable(true);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        // Create the alert dialog using alert dialog builder
        AlertDialog dialog = builder.create();

        // Finally, display the dialog when user press back button
        dialog.show();
    }

   /* private boolean checkUserLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_user_location_code);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_user_location_code);

            }
            return false;
        } else {

            return true;
        }
    }*/

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    //**
    // Aleart dialog box shows GPSLocation Enabled or not ask user to enable enable it
    // **//
    private void buildAlertMessageNoGps() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final android.app.AlertDialog alert = builder.create();
        alert.show();
    }

   /* @Override
    protected void onStart()
    {
        Toast.makeText(this, "onStart "+weatherList.size(), Toast.LENGTH_LONG).show();


        //Toast.makeText(this, "", Toast.LENGTH_SHORT).show();

        if( !InternetPermission.isOnline(WeatherHome.this))
        {
            showSnackbar(_view, message, duration);
            Toast.makeText(this, "No Internet ", Toast.LENGTH_SHORT).show();
        }
        else if (sharePreference.getLatitude() == null && sharePreference.getLongitude() == null)
        {
            Toast.makeText(this, "Having Latitude and Longitude ", Toast.LENGTH_SHORT).show();

            weathernetworkCalling(sharePreference.getLatitude() ,sharePreference.getLongitude() ,units,Appid);
            foreCastweathernetworkCalling(sharePreference.getLatitude(),sharePreference.getLongitude(),units,Appid);

        }
        else if (weatherList.isEmpty() )
        {
          Toast.makeText(this, "weatherList size is  0  ==  "+weatherList.size(), Toast.LENGTH_SHORT).show();
          getAddressFirstLocation();
        }

        super.onStart();

    }*/

    @Override
    protected void onResume() {

        if (weatherList.isEmpty() && forecastWeatherdataListReportModel.isEmpty()) {

            //Toast.makeText(this, "onResume == "+weatherList.isEmpty(), Toast.LENGTH_SHORT).show();


            if (sharePreference.getLatitude() != null && sharePreference.getLongitude() != null) {


                if (InternetPermission.isOnline(WeatherHome.this)) {


                    weathernetworkCalling(sharePreference.getLatitude().trim(), sharePreference.getLongitude().trim(), units, Appid);

                    foreCastweathernetworkCalling(sharePreference.getLatitude().trim(), sharePreference.getLongitude().trim(), units, Appid);
                } else {
                    showSnackbar(_view, message, duration);
                }
            } else {
                getAddressFirstLocation();
            }
        }


        super.onResume();

    }

    public void getAddressFirstLocation() {
        /*//Toast.makeText(this, "Else Block", Toast.LENGTH_SHORT).show();
        locationDetailsDB = new LocationDetailsDB(this);
        sqLiteDatabase = locationDetailsDB.getWritableDatabase();

        Cursor c = sqLiteDatabase.query(LocationDetailsDB.TABLE_NAME1,null,null,null,null,null,null);
        boolean res = c.moveToFirst();

        if (res)
        {
            sharePreference.setLatitude(c.getString(1));
            sharePreference.setLongitude(c.getString(2));

            weathernetworkCalling(sharePreference.getLatitude().trim() ,sharePreference.getLongitude().trim() ,units,Appid);
            foreCastweathernetworkCalling(sharePreference.getLatitude().trim() ,sharePreference.getLongitude().trim() ,units,Appid);
        }

        else
        {
            GPSLatLong  gpsLatLong = new GPSLatLong(WeatherHome.this);

            // Toast.makeText(this, "Add Location"+ gpsLatLong.getLatitude()+"\n"+gpsLatLong.getLongitude(), Toast.LENGTH_SHORT).show();
            //Intent intent = new Intent(WeatherHome.this,MapView.class);
            //startActivity(intent);

            if ( gpsLatLong.getLatitude() !=null  && gpsLatLong.getLongitude() != null)
            {
                Toast.makeText(this, "No data found Getting Current Location ", Toast.LENGTH_SHORT).show();

                weathernetworkCalling(gpsLatLong.getLatitude().trim() ,gpsLatLong.getLongitude().trim() ,units,Appid);
                foreCastweathernetworkCalling(gpsLatLong.getLatitude().trim() ,gpsLatLong.getLongitude().trim() ,units,Appid);
            }

        }*/


        GPSLatLong gpsLatLong = new GPSLatLong(WeatherHome.this);

        // Toast.makeText(this, "Add Location"+ gpsLatLong.getLatitude()+"\n"+gpsLatLong.getLongitude(), Toast.LENGTH_SHORT).show();
        //Intent intent = new Intent(WeatherHome.this,MapView.class);
        //startActivity(intent);

        if (gpsLatLong.getLatitude() != null && gpsLatLong.getLongitude() != null) {
            //Toast.makeText(this, "No data found Getting Current Location ", Toast.LENGTH_SHORT).show();

            weathernetworkCalling(gpsLatLong.getLatitude().trim(), gpsLatLong.getLongitude().trim(), units, Appid);
            foreCastweathernetworkCalling(gpsLatLong.getLatitude().trim(), gpsLatLong.getLongitude().trim(), units, Appid);
        }

    }
}
