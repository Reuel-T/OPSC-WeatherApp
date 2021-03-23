package com.r19012817.weatherapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.r19012817.weatherapp.CityQueryPackage.CityRoot;
import com.r19012817.weatherapp.DailyForecastPackage.Forecast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Forecast> forecasts = new ArrayList<>();

    RecyclerView rvForecasts;
    RecyclerView.Adapter rvForecastAdapter;
    RecyclerView.LayoutManager rvForecastLayoutManager;

    Button bt_search;
    Button bt_get_location;
    TextInputEditText tf_city;
    TextView tv_city_name;

    int REQUEST_CODE = 777;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvForecasts = findViewById(R.id.rv_forecasts);
        rvForecasts.setHasFixedSize(true);

        tf_city = findViewById(R.id.tf_city_name);
        tv_city_name = findViewById(R.id.tv_city_name);

        bt_search = findViewById(R.id.bt_search);
        bt_get_location = findViewById(R.id.bt_get_location);

        bt_search.setOnClickListener(search_clicked);
        bt_get_location.setOnClickListener(get_location_clicked);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private View.OnClickListener search_clicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String citySearchValue;
            citySearchValue = tf_city.getText().toString().trim();
            getCities(citySearchValue);
        }
    };

    private View.OnClickListener get_location_clicked = new View.OnClickListener() {
        @SuppressLint("MissingPermission")
        @Override
        public void onClick(View view) {
            boolean hasPermissions = checkPermissions(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (hasPermissions) {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    double lat = location.getLatitude();
                                    double lon = location.getLongitude();
                                    getGPSLocation(lat, lon);
                                } else {
                                    Toast.makeText(MainActivity.this, "EH?", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_CODE);
            }
        }
    };

    public void citiesParse(String JSONResponse) {
        try {
            CityRoot[] cities = JSONParse.ParseCities(JSONResponse);
            if (cities.length > 0) {
                getForecasts(cities[0].getKey());
                tv_city_name.setText(cities[0].getEnglishName());
            }
        } catch (Exception e) {
            Log.i("gsonParseError", e.getMessage());
        }
    }

    protected void getGPSLocation(double latitude, double longitude) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, NetworkUtils.getGeopositionURL(latitude, longitude),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        CityRoot city = JSONParse.ParseCity(response);
                        if (city.getKey() != null) {
                            getForecasts(city.getKey());
                            tv_city_name.setText(city.getEnglishName());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    protected void getForecasts(String cityCode) {
        // Formulate the request and handle the response.

        StringRequest stringRequest = new StringRequest(Request.Method.GET, NetworkUtils.get5DayForecastURL(cityCode),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        forecasts = JSONParse.ParseForecasts(response);
                        sendToRecyclerView();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    protected void getCities(String searchTerm) {
        // Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, NetworkUtils.getCitiesURL(searchTerm),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        citiesParse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }


    protected void sendToRecyclerView() {
        rvForecastLayoutManager = new LinearLayoutManager(this);
        rvForecastAdapter = new ForecastAdapter(forecasts, this);
        rvForecasts.setLayoutManager(rvForecastLayoutManager);
        rvForecasts.setAdapter(rvForecastAdapter);
    }

    protected boolean checkPermissions(String Permission) {
        // has the permission.
        //TODO GET LOCATION
        return ContextCompat.checkSelfPermission(this, Permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 777: {
                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0

                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted.
                    //request location here

                } else {
                    // permission denied.
                    // tell the user the action is cancelled

                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setMessage("DENIED");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                return;
            }
        }
    }


}