package com.lasys.app.geoweather.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lasys.app.geoweather.R;
import com.lasys.app.geoweather.activity.WeatherForecast;
import com.lasys.app.geoweather.intrface.AppConstants;
import com.lasys.app.geoweather.intrface.TimeWeatherOnclickListner;
import com.lasys.app.geoweather.model.ForecastWeather.List;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class WeatherForecastTimeAdapter extends RecyclerView.Adapter<WeatherForecastTimeAdapter.MyHolder> implements AppConstants
{
    private Context context;
    private Map<String, com.lasys.app.geoweather.model.ForecastWeather.List> timeMap;
    private Object[] dateTimes;
    private TimeWeatherOnclickListner mListner ;

    public WeatherForecastTimeAdapter(WeatherForecast weatherForecast, Map<String, com.lasys.app.geoweather.model.ForecastWeather.List> weatherTimemap)
    {
        this.context = weatherForecast;
        this.timeMap = weatherTimemap;
        this.dateTimes= timeMap.keySet().toArray();
    }

    public void setOnItemClickListner(TimeWeatherOnclickListner listner)
    {
        mListner = listner ;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater li = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.weather_time_cardstyle,parent,false);

        MyHolder myHolder = new MyHolder(v);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position)
    {
        String dateKey=dateTimes[position].toString();
        List wheatherObj=timeMap.get(dateKey);

        //holder.weatherReport_image.setImageResource();

        //Log.i("datetimes",dateTimes.toString());
        //Log.i("datetimes 1",dateKey);
        holder.weatherReport_time.setText(getDateWithFormat(dateKey));
        holder.weatherReport_temperature.setText(wheatherObj.getMain().getTemp()+"\u00b0");

        if (wheatherObj.getWeather().get(0).getIcon() != null)
        {
            String imageUrl = AppConstants.weather_BaseUrl+""+"/img/w/"+wheatherObj.getWeather().get(0).getIcon();

            Picasso.with(context)
                    .load(imageUrl)
                    .resize(100,100).noFade().into(holder.weatherReport_image);
        }

    }

    @Override
    public int getItemCount()
    {
        return  timeMap.size();
    }

    public  class MyHolder extends RecyclerView.ViewHolder
    {
        ImageView weatherReport_image;
        TextView weatherReport_time,weatherReport_temperature;

        public MyHolder(View itemView)
        {
            super(itemView);

            weatherReport_image   = itemView.findViewById(R.id.weatherReport_image);
            weatherReport_time    = itemView.findViewById(R.id.weatherReport_time);
            weatherReport_temperature    = itemView.findViewById(R.id.weatherReport_temperature);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    int position = getAdapterPosition();

                    if (mListner != null && position != RecyclerView.NO_POSITION )
                    {
                        mListner.OnItemClick(position);
                    }
                }
            });
        }


    }

    public String getDateWithFormat(String date)
    {
        try {
            SimpleDateFormat dateFrmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat dateFrmt1 = new SimpleDateFormat("hh:mm a");
            Date d1=dateFrmt.parse(date);

            return dateFrmt1.format(d1);

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
