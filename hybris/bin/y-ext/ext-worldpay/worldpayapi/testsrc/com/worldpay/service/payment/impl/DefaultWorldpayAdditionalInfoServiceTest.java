package com.worldpay.service.payment.impl;

import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.strategy.WorldpayCustomerIpAddressStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.session.SessionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.USER_AGENT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayAdditionalInfoServiceTest {

    private static final String SESSION_ID = "sessionId";
    private static final String CUSTOMER_IP = "customerIp";
    private static final String ACCEPT_HEADER_VALUE = "acceptHeader";
    private static final String USER_AGENT_VALUE = "userAgentValue";
    private static final String WORLDPAY_ADDITIONAL_DATA_SESSION_ID = "worldpay_additional_data_session_id";

    @InjectMocks
    private DefaultWorldpayAdditionalInfoService testObj;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HttpServletRequest httpServletRequestMock;

    @Mock
    private WorldpayCustomerIpAddressStrategy worldpayCustomerIpAddressStrategyMock;
    @Mock
    private SessionService sessionServiceMock;

    @Test
    public void createWorldpayAdditionalInfoDataShouldSetSessionIdFromHttpRequest() {
        when(httpServletRequestMock.getSession().getId()).thenReturn(SESSION_ID);

        final WorldpayAdditionalInfoData result = testObj.createWorldpayAdditionalInfoData(httpServletRequestMock);

        assertEquals(SESSION_ID, result.getSessionId());
    }

    @Test
    public void createWorldpayAdditionalInfoDataShouldSetCustomerIpFromHttpRequest() {
        when(worldpayCustomerIpAddressStrategyMock.getCustomerIp(httpServletRequestMock)).thenReturn(CUSTOMER_IP);

        final WorldpayAdditionalInfoData result = testObj.createWorldpayAdditionalInfoData(httpServletRequestMock);

        assertEquals(CUSTOMER_IP, result.getCustomerIPAddress());
    }

    @Test
    public void createWorldpayAdditionalInfoDataShouldSetAcceptHeaderFromHttpRequest() {
        when(httpServletRequestMock.getHeader(ACCEPT)).thenReturn(ACCEPT_HEADER_VALUE);

        final WorldpayAdditionalInfoData result = testObj.createWorldpayAdditionalInfoData(httpServletRequestMock);

        assertEquals(ACCEPT_HEADER_VALUE, result.getAcceptHeader());
    }

    @Test
    public void createWorldpayAdditionalInfoDataShouldSetUserAgentFromHttpRequest() {
        when(httpServletRequestMock.getHeader(USER_AGENT)).thenReturn(USER_AGENT_VALUE);

        final WorldpayAdditionalInfoData result = testObj.createWorldpayAdditionalInfoData(httpServletRequestMock);

        assertEquals(USER_AGENT_VALUE, result.getUserAgentHeader());
    }

    @Test
    public void createWorldpayAdditionalInfoData_ShouldStoreInSessionTheRequestSessionId() {
        when(httpServletRequestMock.getSession().getId()).thenReturn(SESSION_ID);

        testObj.createWorldpayAdditionalInfoData(httpServletRequestMock);

        verify(sessionServiceMock).setAttribute(WORLDPAY_ADDITIONAL_DATA_SESSION_ID, SESSION_ID);
    }
}
