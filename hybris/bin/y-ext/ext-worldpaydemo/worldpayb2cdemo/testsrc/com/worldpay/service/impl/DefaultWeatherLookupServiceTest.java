package com.worldpay.service.impl;

import atg.taglib.json.util.JSONException;
import atg.taglib.json.util.JSONObject;
import com.worldpay.model.WeatherInformationModel;
import com.worldpay.model.WeatherModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWeatherLookupServiceTest {

    private static final String WEATHER_APPID = "weather.appid";
    private static final String WEATHER_API_VALUE = "weatherApiValue";
    private static final String WEATHER_API_URL = "http://api.openweathermap.org/data/2.5/weather";
    private static final String LAT = "35";
    private static final String LON = "139";
    private static final URI uri = URI.create(String.format("%s?lat=%s&lon=%s&appid=%s", WEATHER_API_URL, LAT, LON, WEATHER_API_VALUE));

    @Spy
    @InjectMocks
    private DefaultWeatherLookupService testObj;
    @Mock
    private ModelService modelServiceMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ConfigurationService configurationServiceMock;
    @Mock
    private WeatherInformationModel weatherInformationMock;
    @Mock
    private WeatherModel weatherMock;

    @Mock
    private RestTemplate restTemplateMock;
    @Mock
    private JSONObject jsonObjectMock;

    @Before
    public void setUp() throws JSONException {
        when(modelServiceMock.create(WeatherInformationModel.class)).thenReturn(weatherInformationMock);
        when(configurationServiceMock.getConfiguration().getString("weather.api.url")).thenReturn(WEATHER_API_URL);
        when(configurationServiceMock.getConfiguration().getString(WEATHER_APPID)).thenReturn(WEATHER_API_VALUE);
        when(restTemplateMock.getForObject(uri, JSONObject.class)).thenReturn(jsonObjectMock);
        when(jsonObjectMock.has("cod")).thenReturn(true);
        when(jsonObjectMock.has("dt")).thenReturn(true);
        when(jsonObjectMock.getLong("dt")).thenReturn(1585243865692L);
    }

    @Test
    public void getWeatherInformation_shouldPopulateAttributes() throws JSONException {

        testObj.getWeatherInformation(LAT, LON);

        verify(testObj).populateWeatherInfo(any(), any());
        verify(weatherInformationMock).setLatitude(LAT);
        verify(weatherInformationMock).setLongitude(LON);
    }

    @Test
    public void getWeatherInformation_WhenError_shouldNotPopulateValuesOnRestClientException() throws JSONException {
        doThrow(RestClientException.class).when(restTemplateMock).getForObject(uri, JSONObject.class);

        testObj.getWeatherInformation(LAT, LON);

        verify(testObj, never()).populateWeatherInfo(any(), any());
        verify(weatherInformationMock).setLatitude(LAT);
        verify(weatherInformationMock).setLongitude(LON);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getWeatherInformation_WhenUrlNull_ShouldThrowException() {
        when(configurationServiceMock.getConfiguration().getString("weather.api.url")).thenReturn("1%");

        testObj.getWeatherInformation(LAT, LON);
    }
}
