package com.r19012817.weatherapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.r19012817.weatherapp.DailyForecastPackage.Forecast;

import java.util.ArrayList;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

    ArrayList<Forecast> forecasts;
    Context ctx;

    public static class ForecastViewHolder extends RecyclerView.ViewHolder {

        public ImageView imv_icon;
        public TextView tv_date;
        public TextView tv_temperature;

        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            imv_icon = itemView.findViewById(R.id.imv_icon);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_temperature = itemView.findViewById(R.id.tv_temperatures);
        }
    }

    public ForecastAdapter(ArrayList<Forecast> forecasts, Context context) {
        this.forecasts = forecasts;
        this.ctx = context;
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_item, parent, false);
        ForecastViewHolder forecastViewHolder = new ForecastViewHolder(view);
        return forecastViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        Forecast fc = forecasts.get(position);
        //holder.imv_icon.setImageBitmap(getBitmapFromURL(fc.getImageURL()));
        //new ImageLoadTask(fc.getImageURL(), holder.imv_icon).execute();
        // Retrieves an image specified by the URL, displays it in the UI.

        String url = fc.getImageURL();

        ImageRequest request = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        holder.imv_icon.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(ctx).addToRequestQueue(request);

        try {
            holder.tv_date.setText(fc.getDate());
            holder.tv_temperature.setText(String.format("%s°C \\ %s°C", fc.getMaxTemp(), fc.getMinTemp()));
        } catch (Exception e) {
            Log.i("TIME_ERROR", e.getMessage());
        }


    }

    @Override
    public int getItemCount() {
        return forecasts.size();
    }
}
