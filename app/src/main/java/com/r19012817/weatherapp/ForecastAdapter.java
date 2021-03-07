package com.r19012817.weatherapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

    ArrayList<Forecast> forecasts;

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

    public ForecastAdapter(ArrayList<Forecast> forecasts) {
        this.forecasts = forecasts;
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
        new ImageLoadTask(fc.getImageURL(), holder.imv_icon).execute();
        holder.tv_date.setText(fc.getDate());
        holder.tv_temperature.setText(String.format("%s°C \\ %s°C", fc.getMaxTemp(), fc.getMinTemp()));
    }

    @Override
    public int getItemCount() {
        return forecasts.size();
    }
}
