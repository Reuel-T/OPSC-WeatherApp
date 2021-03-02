package com.r19012817.weatherapp;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView txvData;
    String jsonString;
    JSONArray fiveDayForecast;
    ArrayList<Forecast> forecasts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //assigns the textview
        txvData = (TextView) findViewById(R.id.txvData);

        //makes the request
        phoneTheAPI();

    }

    protected void phoneTheAPI() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://dataservice.accuweather.com/forecasts/v1/daily/5day/305605?apikey=EalVwMsEVu1qH0ANG4KAGrrttH3G7QQk&metric=true";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        jsonString = response;
                        txvData.setText(jsonString);
                        parseTheJson(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                txvData.setText(error.getMessage());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    protected void parseTheJson(String jsonString) {
        try {
            JSONObject rootWeatherData = new JSONObject(jsonString);
            //get the array of forecasts from the root weather object
            fiveDayForecast = rootWeatherData.getJSONArray("DailyForecasts");

            for (int i = 0; i < fiveDayForecast.length(); i++) {
                Forecast forecast = new Forecast();
                JSONObject dailyWeather = fiveDayForecast.getJSONObject(i);

                //get the date
                String date = dailyWeather.getString("Date");
                forecast.setDate(date);

                //get the temperature object

                JSONObject temperatureObject = dailyWeather.getJSONObject("Temperature");

                //get the minimum temperature
                JSONObject minimumTemperatureObject = temperatureObject.getJSONObject("Minimum");
                String minimumTemperature = minimumTemperatureObject.getString("Value");

                //get the maximum temperature
                JSONObject maximumTemperatureObject = temperatureObject.getJSONObject("Maximum");
                String maximumTemperature = maximumTemperatureObject.getString("Value");

                //add the temperatures to the forecast object
                forecast.setMinTemp(minimumTemperature);
                forecast.setMaxTemp(maximumTemperature);
                //add to the list of forecasts
                forecasts.add(forecast);
            }
            displayData();

        } catch (JSONException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    protected void displayData() {
        String display = "";
        for (Forecast fc : forecasts) {
            display += "Date : " + fc.getDate() + "Min Temp : " + fc.getMinTemp() + "Max Temp : " + fc.getMaxTemp() + "\n";
        }
        txvData.setText(display);
    }
}