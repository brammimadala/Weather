package com.lasys.app.geoweather.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lasys.app.geoweather.R;
import com.lasys.app.geoweather.activity.WeatherForecast;
import com.lasys.app.geoweather.activity.WeatherHome;
import com.lasys.app.geoweather.constants.ForecasDataReportModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherHomeDatesAdapter extends RecyclerView.Adapter<WeatherHomeDatesAdapter.MyHolder>
{
    private Context context;
    //private Map<String,Map<String,com.example.bramm.fpodemo.model.ForecastWeather.List>> mainHashmap;
    //private Object[] dates ;

    private ArrayList<ForecasDataReportModel> marrayListdata  ;

    public WeatherHomeDatesAdapter(WeatherHome weatherHome, ArrayList forecastWeatherdataList)
    {
        this.context = weatherHome;
        this.marrayListdata = forecastWeatherdataList;

    }



  /* public WeatherForecastDatesAdapter(Context context, List<com.example.bramm.fpodemo.constants.ForecastWeatherData> hashMap)
    {
        this.context = context;
        this.mainHashmap = hashMap;
        this.dates=mainHashmap.keySet().toArray();
    }*/



    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater li = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.forecast_dates_cardstyle,parent,false);

        MyHolder myHolder = new MyHolder(v);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position)
    {
        //String dateKey = dates[position].toString();
        //holder.forecast_date.setText(dateKey);
        //holder.forecast_date.setText(getDateWithFormat(dateKey));
        //holder.forecast_date.setText();

        ForecasDataReportModel  ff =  marrayListdata.get(position);

        holder.forecast_date.setText(getDateWithFormat(ff.getDateKey()));


        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ForecasDataReportModel  ff =  marrayListdata.get(position);


               // Map<String,com.example.bramm.fpodemo.model.ForecastWeather.List> m_map = ff.getTimeWeatherDataMap();

                //Bundle bundle = new Bundle();

                Intent intent = new Intent(context, WeatherForecast.class);
                intent.putExtra("list",ff);
                //intent.putExtras(bundle);

                context.startActivity(intent);


                /*for (Map.Entry<String,com.example.bramm.fpodemo.model.ForecastWeather.List> value : m_map.entrySet())
                {
                   // Toast.makeText(context, ""+value.getKey(), Toast.LENGTH_SHORT).show();

                }*/

            }
        });

       // com.example.bramm.fpodemo.model.ForecastWeather.List temp = mainHashmap.get(dateKey).entrySet().iterator().next().getValue();
      //  holder.forecast_date_max_min_temp.setText(temp.getMain().getTempMax()+" / "+temp.getMain().getTempMin());


       /* if (temp.getWeather().get(0).getIcon() != null)
        {
            String imageUrl = AppConstants.weather_BaseUrl+""+"/img/w/"+temp.getWeather().get(0).getIcon();
            Picasso.with(context)
                    .load(imageUrl)
                    .resize(500,500).noFade().into(holder.forecast_date_image);
        }*/


    }

    @Override
    public int getItemCount()
    {
        return marrayListdata.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder
    {
        ImageView forecast_date_image;
        TextView forecast_date,forecast_date_max_min_temp;
        LinearLayout forecastDateLayout;

        public MyHolder(View itemView)
        {
            super(itemView);

            //forecast_date_image     = itemView.findViewById(R.id.forecast_date_image);
            forecast_date           = itemView.findViewById(R.id.forecast_date);
            forecastDateLayout           = itemView.findViewById(R.id.linear_Layout_forcast_date);
            //forecast_date_max_min_temp   = itemView.findViewById(R.id.forecast_date_max_min_temp);
        }
    }


    public String getDateWithFormat(String date)
    {
        try {
            SimpleDateFormat dateFrmt = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFrmt1 = new SimpleDateFormat("E\ndd\nMMM");
            Date d1=dateFrmt.parse(date);

            return dateFrmt1.format(d1);

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


}
