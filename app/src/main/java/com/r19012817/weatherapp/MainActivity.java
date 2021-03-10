package com.r19012817.weatherapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.r19012817.weatherapp.CityQueryPackage.CityRoot;
import com.r19012817.weatherapp.DailyForecastPackage.DailyForecasts;
import com.r19012817.weatherapp.DailyForecastPackage.FiveDayForecastRoot;
import com.r19012817.weatherapp.DailyForecastPackage.Forecast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String jsonString;
    ArrayList<Forecast> forecasts = new ArrayList<>();
    RecyclerView rvForecasts;
    RecyclerView.Adapter rvForecastAdapter;
    RecyclerView.LayoutManager rvForecastLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvForecasts = findViewById(R.id.rv_forecasts);
        rvForecasts.setHasFixedSize(true);


        String cityJson = "[{\"Version\":1,\"Key\":\"298891\",\"Type\":\"City\",\"Rank\":45,\"LocalizedName\":\"Stanger\",\"EnglishName\":\"Stanger\",\"PrimaryPostalCode\":\"\",\"Region\":{\"ID\":\"AFR\",\"LocalizedName\":\"Africa\",\"EnglishName\":\"Africa\"},\"Country\":{\"ID\":\"ZA\",\"LocalizedName\":\"South Africa\",\"EnglishName\":\"South Africa\"},\"AdministrativeArea\":{\"ID\":\"NL\",\"LocalizedName\":\"Kwazulu-Natal\",\"EnglishName\":\"Kwazulu-Natal\",\"Level\":1,\"LocalizedType\":\"Province\",\"EnglishType\":\"Province\",\"CountryID\":\"ZA\"},\"TimeZone\":{\"Code\":\"SAST\",\"Name\":\"Africa/Johannesburg\",\"GmtOffset\":2,\"IsDaylightSaving\":false,\"NextOffsetChange\":null},\"GeoPosition\":{\"Latitude\":-29.347,\"Longitude\":31.294,\"Elevation\":{\"Metric\":{\"Value\":68,\"Unit\":\"m\",\"UnitType\":5},\"Imperial\":{\"Value\":223,\"Unit\":\"ft\",\"UnitType\":0}}},\"IsAlias\":false,\"SupplementalAdminAreas\":[{\"Level\":2,\"LocalizedName\":\"Ilembe\",\"EnglishName\":\"Ilembe\"}],\"DataSets\":[\"AirQualityCurrentConditions\",\"AirQualityForecasts\"]},{\"Version\":1,\"Key\":\"52378\",\"Type\":\"City\",\"Rank\":85,\"LocalizedName\":\"Stanger\",\"EnglishName\":\"Stanger\",\"PrimaryPostalCode\":\"T0E\",\"Region\":{\"ID\":\"NAM\",\"LocalizedName\":\"North America\",\"EnglishName\":\"North America\"},\"Country\":{\"ID\":\"CA\",\"LocalizedName\":\"Canada\",\"EnglishName\":\"Canada\"},\"AdministrativeArea\":{\"ID\":\"AB\",\"LocalizedName\":\"Alberta\",\"EnglishName\":\"Alberta\",\"Level\":1,\"LocalizedType\":\"Province\",\"EnglishType\":\"Province\",\"CountryID\":\"CA\"},\"TimeZone\":{\"Code\":\"MST\",\"Name\":\"America/Edmonton\",\"GmtOffset\":-7,\"IsDaylightSaving\":false,\"NextOffsetChange\":\"2021-03-14T09:00:00Z\"},\"GeoPosition\":{\"Latitude\":53.789,\"Longitude\":-114.814,\"Elevation\":{\"Metric\":{\"Value\":749,\"Unit\":\"m\",\"UnitType\":5},\"Imperial\":{\"Value\":2456,\"Unit\":\"ft\",\"UnitType\":0}}},\"IsAlias\":false,\"SupplementalAdminAreas\":[],\"DataSets\":[\"AirQualityCurrentConditions\",\"AirQualityForecasts\",\"Alerts\",\"ForecastConfidence\",\"FutureRadar\",\"MinuteCast\",\"Radar\"]}]";

        citiesParse(cityJson);

        //makes the request
        getForecasts();

    }

    public void citiesParse(String jsonString) {
        Gson gson = new Gson();
        CityRoot[] cities = gson.fromJson(jsonString, CityRoot[].class);
    }

    protected void getForecasts() {
        // Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, NetworkUtils.get5DayForecastURL("305605"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        jsonString = response;
                        gsonParse(response);
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

    protected void gsonParse(String jsonString) {
        try {
            Gson gson = new Gson();
            FiveDayForecastRoot rootObj = gson.fromJson(jsonString, FiveDayForecastRoot.class);
            List<DailyForecasts> dailyForecastsList = rootObj.getDailyForecasts();

            for (DailyForecasts fc : dailyForecastsList) {
                Forecast newForecast = new Forecast();
                newForecast.setDate(fc.getDate());
                newForecast.setMaxTemp(String.valueOf(fc.getTemperature().getMaximum().getValue()));
                newForecast.setMinTemp(String.valueOf(fc.getTemperature().getMinimum().getValue()));
                newForecast.setImageURL(String.format("https://developer.accuweather.com/sites/default/files/%02d-s.png", fc.getDay().getIcon()));
                forecasts.add(newForecast);
            }
            sendToRecyclerView();
        } catch (Exception e) {
            Log.i("gsonParseError", e.getMessage());
        }
    }

    protected void sendToRecyclerView() {
        rvForecastLayoutManager = new LinearLayoutManager(this);
        rvForecastAdapter = new ForecastAdapter(forecasts, this);
        rvForecasts.setLayoutManager(rvForecastLayoutManager);
        rvForecasts.setAdapter(rvForecastAdapter);
    }
}