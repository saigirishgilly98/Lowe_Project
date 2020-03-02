package com.example.loweproject;

/***
 * This is a model to set and get ItemName
 */
public class CountryItem {
    private String countryName;

    public CountryItem(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryName() {
        return countryName;
    }
}