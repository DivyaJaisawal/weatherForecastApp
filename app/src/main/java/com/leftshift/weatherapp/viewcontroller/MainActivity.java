package com.leftshift.weatherapp.viewcontroller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.leftshift.weatherapp.JSONWeatherParser;
import com.leftshift.weatherapp.R;
import com.leftshift.weatherapp.WeatherHttpClient;
import com.leftshift.weatherapp.adapter.DailyForecastPageAdapter;
import com.leftshift.weatherapp.model.Weather;
import com.leftshift.weatherapp.model.WeatherForecast;

import org.json.JSONException;

import java.io.IOException;

public class MainActivity extends FragmentActivity {


    private TextView cityText;
    private TextView condDescr;
    private TextView temp;
    private TextView unitTemp;
    ProgressBar progressBarForcast;
    private ImageView imgView;
    private static String forecastDaysNum = "15";
    private static String TAG = "MainActivity";
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        String lang = "en";
        String city = getIntent().getStringExtra("city");
        cityText = (TextView) findViewById(R.id.cityText);
        temp = (TextView) findViewById(R.id.temp);
        unitTemp = (TextView) findViewById(R.id.unittemp);
        // for  degree celsius symbol
        unitTemp.setText(" \u2103");
        condDescr = (TextView) findViewById(R.id.skydesc);
        pager = (ViewPager) findViewById(R.id.pager);
        imgView = (ImageView) findViewById(R.id.condIcon);
        progressBarForcast = (ProgressBar) findViewById(R.id.progressBarForecast);
        progressBarForcast.setVisibility(View.VISIBLE);
        ConnectivityManager connec = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connec != null && (
                (connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) ||
                        (connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED))) {
            //You are connected, do online task.
            JSONWeatherTask task = new JSONWeatherTask();
            task.execute(new String[]{city, lang});

            JSONForecastWeatherTask task1 = new JSONForecastWeatherTask();
            task1.execute(new String[]{city, lang, forecastDaysNum});

        } else if (connec != null && (
                (connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.DISCONNECTED) ||
                        (connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.DISCONNECTED))) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle(R.string.title);
            alertDialog.setMessage(R.string.content);
            alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {


                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    finish();

                }
            });
            alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                }
            });

            alertDialog.show();
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public boolean onCreateOptionsMenu(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            //do something
        } else if (id == R.id.action_refresh) {
            //do something
        } else if (id == R.id.action_share) {
            //do something
        } else if (id == R.id.action_donate) {
            //do something
        }
        return super.onOptionsItemSelected(item);
    }

    private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... params) {

            Weather weather = new Weather();
            String data = null;
            try {
                data = ((new WeatherHttpClient()).getWeatherData(params[0], params[1]));
                weather = JSONWeatherParser.getWeather(data);
                System.out.println("Weather [" + weather + "]");
                // Let's retrieve the icon
                weather.iconData = ((new WeatherHttpClient()).getImage(weather.currentCondition.getIcon()));

                Log.d(TAG, "IconData" + weather.currentCondition.getIcon());
            } catch (JSONException e) {
                Log.d(TAG, "JSONException");
                e.printStackTrace();
            }
            return weather;
        }


        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            if (weather.iconData != null && weather.iconData.length > 0) {

               /* Bitmap img = BitmapFactory.decodeByteArray(weather.iconData, 0, weather.iconData.length);*/
                Bitmap img = BitmapFactory.decodeByteArray(weather.iconData, 0, weather.iconData.length);
                Log.d(TAG, "img" + weather.iconData.length);
                Log.d(TAG, "img" + weather.iconData);
                imgView.setImageBitmap(img);
            }

            condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
            temp.setText("" + Math.round((weather.temperature.getTemp() - 275.15)));
            cityText.setText(weather.location.getCity() + "," + weather.location.getCountry());
        }

    }

    private class JSONForecastWeatherTask extends AsyncTask<String, Void, WeatherForecast> {

        @Override
        protected WeatherForecast doInBackground(String... params) {

            String data = null;
            WeatherForecast forecast = new WeatherForecast();
            try {
                data = ((new WeatherHttpClient()).getForecastWeatherData(params[0], params[1]));
                forecast = JSONWeatherParser.getForecastWeather(data);
                Log.d(TAG, "Weather [" + forecast + "]");
                // Let's retrieve the icon
                // weather.iconData = ( (new WeatherHttpClient()).getImage(weather.currentCondition.getIcon()));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return forecast;
        }

        @Override
        protected void onPostExecute(WeatherForecast forecastWeather) {
            super.onPostExecute(forecastWeather);

            DailyForecastPageAdapter adapter = new DailyForecastPageAdapter(Integer.parseInt(forecastDaysNum), getSupportFragmentManager(), forecastWeather);

            pager.setAdapter(adapter);
            progressBarForcast.setVisibility(View.GONE);
        }
    }
}
