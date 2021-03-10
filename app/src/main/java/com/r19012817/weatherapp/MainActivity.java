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

    String url = "https://dataservice.accuweather.com/forecasts/v1/daily/5day/305605?apikey=EalVwMsEVu1qH0ANG4KAGrrttH3G7QQk&metric=true";
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvForecasts = findViewById(R.id.rv_forecasts);
        rvForecasts.setHasFixedSize(true);

        //makes the request
        getForecasts();

    }

    protected void getForecasts() {
        // Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        jsonString = response;
                        gsonParse(response);
                        Log.i("5 Day Forecast Response", response);
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