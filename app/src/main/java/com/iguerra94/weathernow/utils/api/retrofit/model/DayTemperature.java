package com.iguerra94.weathernow.utils.api.retrofit.model;

public class DayTemperature {
    private Double temp; // Temperature
    private Double temp_min; // Minimum temperature at the moment of calculation
    private Double temp_max; // Maximum temperature at the moment of calculation

    public Double getTemp() {
        return temp;
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }

    public Double getTempMin() {
        return temp_min;
    }

    public void setTempMin(Double temp_min) {
        this.temp_min = temp_min;
    }

    public Double getTempMax() {
        return temp_max;
    }

    public void setTempMax(Double temp_max) {
        this.temp_max = temp_max;
    }

    @Override
    public String toString() {
        return "DayTemperature{" +
                "temp=" + temp +
                ", temp_min=" + temp_min +
                ", temp_max=" + temp_max +
                '}';
    }
}
