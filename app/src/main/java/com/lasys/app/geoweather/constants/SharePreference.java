package com.lasys.app.geoweather.constants;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreference
{
    Context mContext;
    SharedPreferences sharedPreferences ;
    SharedPreferences.Editor preferenceEditor;

    public SharePreference(Context context)
    {
        mContext = context;
        sharedPreferences = mContext.getSharedPreferences(SpConstant.PREFERNCES_Key, Context.MODE_PRIVATE);
    }

    public void setLatitude(String value)
    {
        preferenceEditor = sharedPreferences.edit();
        preferenceEditor.putString(SpConstant.LATITUDE, value);
        preferenceEditor.commit();
    }

    public String getLatitude()
    {
        return sharedPreferences.getString(SpConstant.LATITUDE,null);
    }

    public void setLongitude(String value)
    {
        preferenceEditor = sharedPreferences.edit();
        preferenceEditor.putString(SpConstant.LONGITUDE, value);
        preferenceEditor.commit();
    }

    public String getLongitude()
    {
        return sharedPreferences.getString(SpConstant.LONGITUDE,null);
    }

    public void clearAll()
    {
        SharedPreferences preferences = mContext.getSharedPreferences(SpConstant.PREFERNCES_Key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

}
