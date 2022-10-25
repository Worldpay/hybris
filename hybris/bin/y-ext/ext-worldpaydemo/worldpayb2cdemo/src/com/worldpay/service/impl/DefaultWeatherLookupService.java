package com.worldpay.service.impl;

import atg.taglib.json.util.JSONArray;
import atg.taglib.json.util.JSONException;
import atg.taglib.json.util.JSONObject;
import com.google.common.base.Preconditions;
import com.worldpay.model.WeatherInformationModel;
import com.worldpay.model.WeatherModel;
import com.worldpay.service.WeatherLookupService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * {@inheritDoc}
 */
public class DefaultWeatherLookupService implements WeatherLookupService {

    private static final Logger LOG = LogManager.getLogger(DefaultWeatherLookupService.class);

    private static final String WEATHER_URL = "weather.api.url";
    private static final String WEATHER_APP_ID = "weather.appid";

    protected final ConfigurationService configurationService;
    protected final RestTemplate restTemplate;
    protected final ModelService modelService;

    public DefaultWeatherLookupService(final ConfigurationService configurationService, final RestTemplate restTemplate, final ModelService modelService) {
        this.configurationService = configurationService;
        this.restTemplate = restTemplate;
        this.modelService = modelService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WeatherInformationModel getWeatherInformation(final String lat, final String lon) {
        final WeatherInformationModel weatherInfo = modelService.create(WeatherInformationModel.class);
        try {
            final URI url = generateUrl(lat, lon);
            Preconditions.checkArgument(url != null, "The generated url cannot be null");

            final JSONObject json = restTemplate.getForObject(url, JSONObject.class);

            if (json != null) {
                populateWeatherInfo(weatherInfo, json);
            }
        } catch (final RestClientException ex) {
            LOG.warn("There was an error getting the weather info from the API", ex);
        } catch (final JSONException ex) {
            LOG.warn(MessageFormat.format("JsonException when populating weather info for lat {0} and lon {1} - probably invalid json", lat, lon), ex);
        }
        weatherInfo.setLatitude(lat != null ? lat : "undefined");
        weatherInfo.setLongitude(lon != null ? lon : "undefined");

        modelService.save(weatherInfo);
        return weatherInfo;
    }

    private URI generateUrl(final String lat, final String lon) {
        final String weatherApiUrl = configurationService.getConfiguration().getString(WEATHER_URL);
        final String appId = configurationService.getConfiguration().getString(WEATHER_APP_ID);

        try {
            final URIBuilder b = new URIBuilder(weatherApiUrl);
            b.addParameter("lat", lat);
            b.addParameter("lon", lon);
            b.addParameter("appid", appId);
            return b.build();
        } catch (final URISyntaxException e) {
            LOG.error("Error when building URI", e);
            return null;
        }
    }

    protected void populateWeatherInfo(final WeatherInformationModel weatherInfo, final JSONObject json) throws JSONException {
        setStringAttribute("cod", json, weatherInfo);
        setStringAttribute("id", json, weatherInfo);
        setStringAttribute("name", json, weatherInfo);
        setDateAttribute("dt", json, weatherInfo);

        populateClouds(weatherInfo, json);
        populateRain(weatherInfo, json);
        populateSnow(weatherInfo, json);
        populateWind(weatherInfo, json);
        populateMain(weatherInfo, json);
        populateWeather(weatherInfo, json);
        populateSys(weatherInfo, json);
    }

    private void populateSys(final WeatherInformationModel weatherInfo, final JSONObject json) throws JSONException {
        if (json.has("sys")) {
            final JSONObject sys = json.getJSONObject("sys");
            setStringAttribute("country", sys, weatherInfo);
            setDateAttribute("sunrise", sys, weatherInfo);
            setDateAttribute("sunset", sys, weatherInfo);
        }
    }

    private void populateWeather(final WeatherInformationModel weatherInfo, final JSONObject json) throws JSONException {
        if (json.has("weather")) {
            final JSONArray weather = json.getJSONArray("weather");
            weatherInfo.setWeather(getWeathers(weather));
        }
    }

    private void populateMain(final WeatherInformationModel weatherInfo, final JSONObject json) throws JSONException {
        if (json.has("main")) {
            final JSONObject main = json.getJSONObject("main");
            setStringAttribute("temp", main, weatherInfo);
            setStringAttribute("humidity", main, weatherInfo);
            setStringAttribute("pressure", main, weatherInfo);
            setStringAttribute("temp_min", main, weatherInfo);
            setStringAttribute("temp_max", main, weatherInfo);
            setStringAttribute("sea_level", main, weatherInfo);
            setStringAttribute("grnd_level", main, weatherInfo);
        }
    }

    private void populateClouds(final WeatherInformationModel weatherInfo, final JSONObject json) throws JSONException {
        if (json.has("clouds")) {
            final JSONObject clouds = json.getJSONObject("clouds");
            weatherInfo.setClouds(clouds.getString("all"));
        }
    }

    private void populateRain(final WeatherInformationModel weatherInfo, final JSONObject json) throws JSONException {
        if (json.has("rain")) {
            final JSONObject rain = json.getJSONObject("rain");
            weatherInfo.setRain(rain.getString("3h"));
        }
    }

    private void populateSnow(final WeatherInformationModel weatherInfo, final JSONObject json) throws JSONException {
        if (json.has("snow")) {
            final JSONObject snow = json.getJSONObject("snow");
            weatherInfo.setSnow(snow.getString("3h"));
        }
    }

    private void populateWind(final WeatherInformationModel weatherInfo, final JSONObject json) throws JSONException {
        if (json.has("wind")) {
            final JSONObject wind = json.getJSONObject("wind");
            setStringAttribute("speed", wind, weatherInfo);
            setStringAttribute("deg", wind, weatherInfo);
        }
    }

    private void setStringAttribute(final String attribute, final JSONObject json, final WeatherInformationModel weatherInfo) throws JSONException {
        if (json.has(attribute)) {
            weatherInfo.setProperty(attribute, String.valueOf(json.get(attribute)));
        }
    }

    private void setDateAttribute(final String attribute, final JSONObject json, final WeatherInformationModel weatherInfo) throws JSONException {
        if (json.has(attribute)) {
            weatherInfo.setProperty(attribute, Date.from(Instant.ofEpochSecond(json.getLong(attribute))));
        }
    }

    private void setStringAttribute(final String attribute, final JSONObject json, final WeatherModel weather) throws JSONException {
        if (json.has(attribute)) {
            weather.setProperty(attribute, String.valueOf(json.get(attribute)));
        }
    }

    private List<WeatherModel> getWeathers(final JSONArray weathers) {
        final List<WeatherModel> weatherList = new ArrayList<>();

        weathers.forEach(object -> {
            final WeatherModel model = modelService.create(WeatherModel.class);
            try {
                final JSONObject weather = (JSONObject) object;
                setStringAttribute("id", weather, model);
                setStringAttribute("description", weather, model);
                setStringAttribute("main", weather, model);
                setStringAttribute("icon", weather, model);

                modelService.save(model);
            } catch (final JSONException | ModelSavingException ex) {
                LOG.warn("Not able to populate weather model.", ex);
            }
            weatherList.add(model);
        });

        return weatherList;
    }
}
