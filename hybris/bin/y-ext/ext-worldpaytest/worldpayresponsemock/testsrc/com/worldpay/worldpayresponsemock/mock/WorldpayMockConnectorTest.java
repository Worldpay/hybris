package com.worldpay.worldpayresponsemock.mock;

import com.worldpay.exception.WorldpayException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayMockConnectorTest {

    @InjectMocks
    private WorldpayMockConnector testObj;

    private static final int SERVER_PORT = 80;
    private static final String HTTP_SCHEME = "http";
    private static final String SERVER_NAME = "serverName";
    private static final String CONTEXT_ROOT = "/contextRoot";
    private static final String SOME_RESPONSE = "some_response";
    private static final String ENDPOINT_POSTFIX = "/endpoint-postfix";
    private static final String NOTIFICATION_EXTENSION_CONTEXT_ROOT = "worldpaynotifications.webroot";
    private static final String WORLDPAYRESPONSEMOCK_ORDER_NOTIFICATION_ENDPOINT = "worldpayresponsemock.order.notification.endpoint";
    private static final String NOTIFICATION_ENDPOINT_URL = HTTP_SCHEME + "://" + SERVER_NAME + ":" + SERVER_PORT + CONTEXT_ROOT + ENDPOINT_POSTFIX;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ConfigurationService configurationServiceMock;
    @Mock
    private RestTemplate worldpayRestTemplateMock;
    @Mock
    private HttpServletRequest requestMock;

    @Before
    public void setUp() {
        when(configurationServiceMock.getConfiguration().getString(WORLDPAYRESPONSEMOCK_ORDER_NOTIFICATION_ENDPOINT)).thenReturn(ENDPOINT_POSTFIX);
        when(configurationServiceMock.getConfiguration().getString(NOTIFICATION_EXTENSION_CONTEXT_ROOT)).thenReturn(CONTEXT_ROOT);
        when(requestMock.getServerPort()).thenReturn(SERVER_PORT);
        when(requestMock.getServerName()).thenReturn(SERVER_NAME);
        when(requestMock.getScheme()).thenReturn(HTTP_SCHEME);
    }

    @Test
    public void sendResponseShouldPostNotificationMessageToStorefront() throws WorldpayException {

        testObj.sendResponse(requestMock, SOME_RESPONSE);

        verify(worldpayRestTemplateMock).postForObject(NOTIFICATION_ENDPOINT_URL, SOME_RESPONSE, String.class);
    }
}
