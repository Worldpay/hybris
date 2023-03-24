package com.worldpay.service.impl;

import com.worldpay.model.IPInformationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultIPLookupServiceTest {

    private static final String API_URL = "url";
    private static final String JSON = "{\"isp\":\"isp_info\",\"lat\":19.19,\"lon\":19.21,\"org\":\"e2y\", \"status\":\"success\"}";
    private static final String JSON_FAIL = "{\"isp\":\"isp_info\",\"lat\":19.19,\"lon\":19.21,\"org\":\"e2y\", \"status\":\"fail\"}";
    private static final String IP = "1.2.3.4";

    @InjectMocks
    private DefaultIPLookupService testObj;

    @Mock
    private ModelService modelService;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private ConfigurationService configurationService;
    @Mock
    private RestTemplate restTemplateMock;

    @Mock
    private Configuration configurationMock;
    @Mock
    private IPInformationModel ipInformationModelMock;

    @Before
    public void setUp() {
        when(modelService.create(IPInformationModel.class)).thenReturn(ipInformationModelMock);
        when(configurationService.getConfiguration()).thenReturn(configurationMock);
        when(configurationService.getConfiguration().getString("ip.api.json.url")).thenReturn(API_URL);
        doReturn(JSON).when(restTemplateMock).getForObject(URI.create(String.format("%s%s", API_URL, IP)), String.class);
    }

    @Test
    public void shouldPopulateIpInformation() {

        testObj.getIPInformation(IP);

        verify(ipInformationModelMock).setIsp("isp_info");
        verify(ipInformationModelMock).setOrg("e2y");
        verify(ipInformationModelMock).setLat("19.19");
        verify(ipInformationModelMock).setLon("19.21");
    }

    @Test
    public void shouldNotPopulateIpInformationOnFail() {
        doReturn(JSON_FAIL).when(restTemplateMock).getForObject(URI.create(String.format("%s%s", API_URL, IP)), String.class);

        testObj.getIPInformation(IP);

        verify(ipInformationModelMock, never()).setIsp("isp_info");
        verify(ipInformationModelMock, never()).setOrg("e2y");
        verify(ipInformationModelMock, never()).setLat("19.19");
        verify(ipInformationModelMock, never()).setLon("19.21");
    }

    @Test
    public void shouldAlwaysPopulateIp() {
        doReturn(JSON_FAIL).when(restTemplateMock).getForObject(URI.create(String.format("%s%s", API_URL, IP)), String.class);

        testObj.getIPInformation(IP);

        verify(ipInformationModelMock).setIp(IP);
    }


}
