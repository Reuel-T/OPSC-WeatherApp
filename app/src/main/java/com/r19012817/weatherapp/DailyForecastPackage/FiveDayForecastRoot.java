package com.r19012817.weatherapp.DailyForecastPackage;

import java.util.List;

public class FiveDayForecastRoot {
    private com.r19012817.weatherapp.DailyForecastPackage.Headline Headline;
    private List<com.r19012817.weatherapp.DailyForecastPackage.DailyForecasts> DailyForecasts;

    public void setHeadline(Headline Headline) {
        this.Headline = Headline;
    }

    public Headline getHeadline() {
        return this.Headline;
    }

    public void setDailyForecasts(List<DailyForecasts> DailyForecasts) {
        this.DailyForecasts = DailyForecasts;
    }

    public List<DailyForecasts> getDailyForecasts() {
        return this.DailyForecasts;
    }
}
