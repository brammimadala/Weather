package com.lasys.app.geoweather.constants;

import com.lasys.app.geoweather.model.ForecastWeather.List;

import java.io.Serializable;
import java.util.Map;

public class ForecasDataReportModel implements Serializable
{
   public  String dateKey ;
   public  Map<String,com.lasys.app.geoweather.model.ForecastWeather.List> timeWeatherDataMap ;

    public String getDateKey()
    {
        return dateKey;
    }

    public void setDateKey(String dateKey)
    {
        this.dateKey = dateKey;
    }

    public Map<String, com.lasys.app.geoweather.model.ForecastWeather.List> getTimeWeatherDataMap()
    {
        return timeWeatherDataMap;
    }

    public void setTimeWeatherDataMap(Map<String, List> timeWeatherDataMap)
    {
        this.timeWeatherDataMap = timeWeatherDataMap;
    }

}
