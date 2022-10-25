package com.worldpay.service;

import com.worldpay.model.WeatherInformationModel;

/**
 * Service to get weather information
 */
public interface WeatherLookupService {

    /**
     *  Method to call weather service and return the WeatherInformationModel
     * @param lat
     * @param lon
     * @return
     */
    WeatherInformationModel getWeatherInformation(final String lat, final String lon);
}
