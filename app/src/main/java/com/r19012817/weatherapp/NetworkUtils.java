package com.r19012817.weatherapp;

import android.net.Uri;
import android.util.Log;

import java.net.URL;

public class NetworkUtils {

    //Paths
    private static final String WEATHERBASE_URL = "https://dataservice.accuweather.com/";
    private static final String FORECASTS_5 = "forecasts/v1/daily/5day/";
    private static final String SEARCH_CITIES = "locations/v1/cities/search";

    //Parameters
    private static final String PARAM_METRIC = "metric";
    private static final String PARAM_APIKEY = "apikey";
    private static final String PARAM_QUERY = "q";

    //Values
    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private static final String SEARCH = "search";


    //Generates a request URL for a 5 day forecast based on the given city code
    public static String get5DayForecastURL(String cityCode) {
        Uri uri = Uri.parse(WEATHERBASE_URL).buildUpon()                                            //The base URL for all AccuWeather API calls
                .appendEncodedPath(FORECASTS_5)                                                     //The path for the 5 day forecast
                .appendEncodedPath(cityCode)                                                        //The code for the selected city
                .appendQueryParameter(PARAM_APIKEY, BuildConfig.ACCUWEATHER_API_KEY)                //The API key
                .appendQueryParameter(PARAM_METRIC, TRUE).build();                                  //Sets the return values to metric

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (Exception e) {
            Log.e("5_DAY_REQUEST", e.getMessage(), e);
        }

        Log.i("5_DAY_URL", url.toString());

        return url.toString();
    }

    public static String getCitiesURL(String query) {

        Uri uri = Uri.parse(WEATHERBASE_URL).buildUpon()                                            //The base URL for all AccuWeather API calls
                .appendEncodedPath(SEARCH_CITIES)                                                   //The search cities part of the URL
                .appendQueryParameter(PARAM_APIKEY, BuildConfig.ACCUWEATHER_API_KEY)                //The API Key
                .appendQueryParameter(PARAM_QUERY, query).build();                                  //The Search Query

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (Exception e) {
            Log.e("SEARCH_CITY_REQUEST", e.getMessage(), e);
        }

        Log.i("SEARCH_CITY_REQUEST_URL", url.toString());

        return url.toString();
    }
}
