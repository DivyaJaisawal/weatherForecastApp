package com.leftshift.weatherapp.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leftshift.weatherapp.R;
import com.leftshift.weatherapp.WeatherHttpClient;
import com.leftshift.weatherapp.model.DayForecast;

/**
 * Created by divya jaisawal on 9/3/2015.
 */
public class DayForecastFragment extends Fragment {

    private DayForecast dayForecast;
    private ImageView iconWeather;
    private static String TAG = "DayForecastFragment_TAG";

    public DayForecastFragment() {
    }

    public void setForecast(DayForecast dayForecast) {
        this.dayForecast = dayForecast;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dayforecast_fragment, container, false);

        TextView tempView = (TextView) v.findViewById(R.id.tempForecast);
        TextView descView = (TextView) v.findViewById(R.id.skydescForecast);
        tempView.setText((int) (dayForecast.forecastTemp.min - 275.15) + "-" + (int) (dayForecast.forecastTemp.max - 275.15));
        Log.d(TAG, "forecastTemp" + (dayForecast.forecastTemp.min - 275.15));
        descView.setText(dayForecast.weather.currentCondition.getDescr());
        iconWeather = (ImageView) v.findViewById(R.id.forCondIcon);
        // Now we retrieve the weather icon
        JSONIconWeatherTask task = new JSONIconWeatherTask();
        task.execute(new String[]{dayForecast.weather.currentCondition.getIcon()});

        return v;
    }


    private class JSONIconWeatherTask extends AsyncTask<String, Void, byte[]> {

        @Override
        protected byte[] doInBackground(String... params) {

            byte[] data = null;

            try {

                // Let's retrieve the icon
                data = ((new WeatherHttpClient()).getImage(params[0]));
                Log.d(TAG, "image" + data);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return data;
        }


        @Override
        protected void onPostExecute(byte[] data) {
            super.onPostExecute(data);

            if (data != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                Bitmap img = BitmapFactory.decodeByteArray(data, 0, data.length, options);
                iconWeather.setImageBitmap(img);
                /*Bitmap img =
                        BitmapFactory.decodeStream(url_value.openConnection().getInputStream());
				iconWeather.setImageBitmap(img);*/
                Log.d(TAG, "TAG" + img);

            }
        }


    }
}
