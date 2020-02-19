package com.iguerra94.weathernow.utils.api.retrofit.model;

public class CountryDetails {
    private String country; // Country code

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "CountryDetails{" +
                "country='" + country + '\'' +
                '}';
    }
}
