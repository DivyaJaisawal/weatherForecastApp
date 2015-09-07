package com.leftshift.weatherapp;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/*import org.apache.http.impl.client.DefaultHttpClient;*/

/**
 * Created by divya jaisawal on 9/4/2015.
 */
public class WeatherHttpClient {

    private static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    private static String IMG_URL = "http://openweathermap.org/img/w/";
    private static String TAG = "WeatherHttpClient_TAG";

    private static String BASE_FORECAST_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?mode=json&q=";
    private static String WEATHER_FORECAST_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?q=";

    public String getWeatherData(String location, String lang) {
        HttpURLConnection con = null;
        InputStream is = null;

        try {
            String url = BASE_URL + location;
            if (lang != null)
                url = url + "&lang=" + lang;

            Log.d(TAG, "current temp url" + url);
            con = (HttpURLConnection) (new URL(url)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            // Let's read the response
            StringBuffer buffer = new StringBuffer();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = br.readLine()) != null)
                buffer.append(line + "\r\n");

            is.close();
            con.disconnect();

            return buffer.toString();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Throwable t) {
            }
            try {
                con.disconnect();
            } catch (Throwable t) {
            }
        }

        return null;

    }

    /*public String getForecastWeatherData(String location, String lang, String sForecastDayNum) {
        HttpURLConnection con = null ;
        InputStream is = null;
        int forecastDayNum = Integer.parseInt(sForecastDayNum);

        try {

            // Forecast
            String url = BASE_FORECAST_URL + location;
            if (lang != null)+
                url = url + "&lang=" + lang;

            url = url + "&cnt=" + forecastDayNum;
            con = (HttpURLConnection) ( new URL(url)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            // Let's read the response
            StringBuffer buffer1 = new StringBuffer();
            is = con.getInputStream();
            BufferedReader br1 = new BufferedReader(new InputStreamReader(is));
            String line1 = null;
            while (  (line1 = br1.readLine()) != null )
                buffer1.append(line1 + "\r\n");

            is.close();
            con.disconnect();

            System.out.println("Buffer ["+buffer1.toString()+"]");
            return buffer1.toString();
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        finally {
            try { is.close(); } catch(Throwable t) {}
            try { con.disconnect(); } catch(Throwable t) {}
        }

        return null;

    }
    */
    public String getForecastWeatherData(String city, String sForecastDayNum) throws ClientProtocolException, IOException, JSONException {
        HttpClient client = null;
        BufferedReader in = null;
        String data = null;
        // int forecastDayNum = Integer.parseInt(sForecastDayNum);

        // Forecast
        String url = WEATHER_FORECAST_URL;
        if (city != null) {
            //do nothing
        } else {
            //set default city as pune
            city = "pune";
        }
        url = url + city + "&cnt=15" + "&APPID=4ad2ea9c29731ed02f830c4720be4d77";
        Log.d(TAG, url);
        //HttpClient
        try {
            client = client = new DefaultHttpClient();
            StringBuilder stringBuilder = new StringBuilder(url);
            HttpGet get = new HttpGet(stringBuilder.toString());
            HttpResponse r = client.execute(get);
            in = new BufferedReader(new InputStreamReader(r.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String l = "";
            String nl = System.getProperty("line separator");
            while ((l = in.readLine()) != null) {
                sb.append(l + nl);
            }
            in.close();
            data = sb.toString();
            Log.d(TAG, data);
            return data;
        } finally {
            if (in != null) {
                try {
                    in.close();
                    return data;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /*  public byte[] getImage(String code) {
          HttpURLConnection con = null;
          InputStream is = null;
          try {
              con = (HttpURLConnection) (new URL(IMG_URL + code)).openConnection();
              con.setRequestMethod("GET");
              con.setDoInput(true);
              con.setDoOutput(true);
              con.connect();
              Log.d(TAG, IMG_URL);

              // Let's read the response
              is = con.getInputStream();
              byte[] buffer = new byte[1024];
              ByteArrayOutputStream baos = new ByteArrayOutputStream();

              while (is.read(buffer) != -1)
                  baos.write(buffer);

              return baos.toByteArray();
          } catch (Throwable t) {
              t.printStackTrace();
          } finally {
              try {
                  is.close();
              } catch (Throwable t) {
              }
              try {
                  con.disconnect();
              } catch (Throwable t) {
              }
          }

          return null;

      }*/
    public byte[] getImage(String code) {
        HttpClient client = null;
        InputStream is = null;
        String data = null;
        String url = IMG_URL;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (code != null) {

            //do nothing
        } else {
            //set default 501 for  default weather
            code = "05d";
        }
        url = url + code;
        Log.d(TAG, url);
        HttpGet httpget = new HttpGet(url);
        client = new DefaultHttpClient();
        HttpResponse response = null;
        try {
            response = client.execute(httpget);
            String reasonPhrase = response.getStatusLine().getReasonPhrase();
            int statusCode = response.getStatusLine().getStatusCode();
            Log.d(TAG, "statusCode" + statusCode);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            byte[] buffer = new byte[1024];


            while (is.read(buffer) != -1)
                baos.write(buffer);

            return baos.toByteArray();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (is != null) {

                try {
                    is.close();
                    return baos.toByteArray();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        return null;

    }

}