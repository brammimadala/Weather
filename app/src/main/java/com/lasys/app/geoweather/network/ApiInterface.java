package com.lasys.app.geoweather.network;


import com.lasys.app.geoweather.model.CurrentWeather.WeatherCurrentModel;
import com.lasys.app.geoweather.model.ForecastWeather.FweatherReport;


import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("/data/2.5/weather")
    retrofit2.Call<WeatherCurrentModel> getCurrentWeather(@Query("lat") String lat, @Query("lon") String lon, @Query("units") String units, @Query("APPID") String appid);


    @GET("/data/2.5/forecast")
    retrofit2.Call<FweatherReport> getForeCastWeather(@Query("lat") String lat, @Query("lon") String lon, @Query("units") String units, @Query("APPID") String appid);


}
