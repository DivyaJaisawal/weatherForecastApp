package com.leftshift.weatherapp.viewcontroller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.leftshift.weatherapp.GPSTracker;
import com.leftshift.weatherapp.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class SelectCityActivity extends Activity implements View.OnClickListener {

    private String TAG = "SelectCityActivity";
    ArrayList<String> cityList;
    Button btnSelectedCity, btnCurrentCity;
    private String city;
    EditText edTxtChooseCity;
    // GPSTracker class
    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);

        //Create your buttons and set their onClickListener to "this"
        btnCurrentCity = (Button) findViewById(R.id.btnCurrentCity);
        btnSelectedCity = (Button) findViewById(R.id.btnSelectedCity);
        edTxtChooseCity = (EditText) findViewById(R.id.edTxtChooseCity);
        btnSelectedCity.setOnClickListener(this);
        btnCurrentCity.setOnClickListener(this);
    }
    //implement the onClick method here

    public void onClick(View v) {
        // Perform action on click
        switch (v.getId()) {
            case R.id.btnSelectedCity: {
                if (edTxtChooseCity.getText().toString().trim().length() > 0) {
                    Intent intent = new Intent(SelectCityActivity.this, MainActivity.class);
                    city = edTxtChooseCity.getText().toString().trim();
                    intent.putExtra("city", city);
                    startActivity(intent);
                } else {
                    new AlertDialog.Builder(SelectCityActivity.this)
                            .setTitle("Please enter a city name")
                            .setMessage("This is mandatory. Without this you can not proceed.")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })

                            .setIcon(R.drawable.actionbar_sun_icn)
                            .show();
                }
            }

            break;
            case R.id.btnCurrentCity:

            {
                // create class object
                gps = new GPSTracker(SelectCityActivity.this);
                // check if GPS enabled
                if (gps.canGetLocation()) {

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    CityAsyncTask cst = new CityAsyncTask(SelectCityActivity.this,
                            latitude, longitude);
                    cst.execute();
                    String cityName = null;
                    try {
                        cityName = cst.get().toString();
                        Log.d(TAG, "cityName-->" + cityName);
                        Intent intent = new Intent(SelectCityActivity.this, MainActivity.class);
                        city = cityName;
                        intent.putExtra("city", city);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "Your Location is -  " + cityName + "\n" +
                                "Lat:" + latitude + "& Long: " + longitude, Toast.LENGTH_LONG).show();

                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    // \n is for new line
                    Log.d(TAG, "Lat and Lon" + latitude + "&" + longitude);

                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }

            }

            break;
        }
    }

    public class CityAsyncTask extends AsyncTask<String, String, String> {
        Activity act;
        double latitude;
        double longitude;

        public CityAsyncTask(Activity act, double latitude, double longitude) {
            // TODO Auto-generated constructor stub
            this.act = act;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            Geocoder geocoder = new Geocoder(act, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude,
                        longitude, 1);
                Log.e("Addresses", "-->" + addresses);
               /* result = addresses.get(0).toString();------> for getting the address*/
                result = addresses.get(0).getSubAdminArea().toString();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

        }
    }
}

