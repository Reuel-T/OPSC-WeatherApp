package com.r19012817.weatherapp;

import android.util.Log;

import com.google.gson.Gson;
import com.r19012817.weatherapp.CityQueryPackage.CityRoot;
import com.r19012817.weatherapp.DailyForecastPackage.DailyForecasts;
import com.r19012817.weatherapp.DailyForecastPackage.FiveDayForecastRoot;
import com.r19012817.weatherapp.DailyForecastPackage.Forecast;

import java.util.ArrayList;
import java.util.List;

public class JSONParse {
    public static ArrayList<Forecast> ParseForecasts(String JSONResponse) {
        ArrayList<Forecast> forecasts = new ArrayList<>();
        try {
            Gson gson = new Gson();
            FiveDayForecastRoot rootObj = gson.fromJson(JSONResponse, FiveDayForecastRoot.class);
            List<DailyForecasts> dailyForecastsList = rootObj.getDailyForecasts();

            for (DailyForecasts fc : dailyForecastsList) {
                Forecast newForecast = new Forecast();
                newForecast.setDate(fc.getDate());
                newForecast.setMaxTemp(String.valueOf(fc.getTemperature().getMaximum().getValue()));
                newForecast.setMinTemp(String.valueOf(fc.getTemperature().getMinimum().getValue()));
                newForecast.setImageURL(String.format("https://developer.accuweather.com/sites/default/files/%02d-s.png", fc.getDay().getIcon()));
                forecasts.add(newForecast);
            }
            return forecasts;
        } catch (Exception e) {
            Log.i("gsonParseError", e.getMessage());
            return forecasts;
        }
    }

    public static CityRoot[] ParseCities(String JSONResponse) {
        Gson gson = new Gson();
        return gson.fromJson(JSONResponse, CityRoot[].class);
    }

    public static CityRoot ParseCity(String JSONResponse) {
        Gson gson = new Gson();
        return gson.fromJson(JSONResponse, CityRoot.class);
    }

}
