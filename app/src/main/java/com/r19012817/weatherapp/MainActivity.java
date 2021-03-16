package com.r19012817.weatherapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.r19012817.weatherapp.CityQueryPackage.CityRoot;
import com.r19012817.weatherapp.DailyForecastPackage.DailyForecasts;
import com.r19012817.weatherapp.DailyForecastPackage.FiveDayForecastRoot;
import com.r19012817.weatherapp.DailyForecastPackage.Forecast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    ArrayList<Forecast> forecasts = new ArrayList<>();

    RecyclerView rvForecasts;
    RecyclerView.Adapter rvForecastAdapter;
    RecyclerView.LayoutManager rvForecastLayoutManager;

    Button bt_search;
    TextInputEditText tf_city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvForecasts = findViewById(R.id.rv_forecasts);
        rvForecasts.setHasFixedSize(true);

        tf_city = findViewById(R.id.tf_city_name);
        bt_search = findViewById(R.id.bt_search);

        bt_search.setOnClickListener(search_clicked);
    }

    public void citiesParse(String jsonString) {
        Gson gson = new Gson();
        CityRoot[] cities = gson.fromJson(jsonString, CityRoot[].class);

        if (cities.length > 0) {
            getForecasts(cities[0].getKey());
        }
    }

    protected void getForecasts(String cityCode) {
        // Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, NetworkUtils.get5DayForecastURL(cityCode),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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

    protected void gsonParse(String jsonString) {
        try {
            Gson gson = new Gson();
            forecasts.clear();
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

    private View.OnClickListener search_clicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String citySearchValue;
            citySearchValue = tf_city.getText().toString().trim();
            getCities(citySearchValue);
        }
    };

    protected void sendToRecyclerView() {
        rvForecastLayoutManager = new LinearLayoutManager(this);
        rvForecastAdapter = new ForecastAdapter(forecasts, this);
        rvForecasts.setLayoutManager(rvForecastLayoutManager);
        rvForecasts.setAdapter(rvForecastAdapter);
    }
}