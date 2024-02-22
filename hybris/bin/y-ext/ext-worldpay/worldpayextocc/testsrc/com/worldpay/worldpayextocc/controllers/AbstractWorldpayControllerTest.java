package com.worldpay.worldpayextocc.controllers;

import com.worldpay.data.Browser;
import com.worldpay.dto.BrowserInfoWsDTO;
import com.worldpay.dto.order.PlaceOrderResponseWsDTO;
import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.TransactionStatus;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

import static org.apache.http.HttpHeaders.USER_AGENT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AbstractWorldpayControllerTest {

    private static final String SESSION_ID = "1508416";
    private static final String ISSUER_URL = "issuerURL";
    private static final String RETURN_CODE = "returnCode";
    private static final String PA_REQUEST = "paRequest";
    private static final String JAVAX_SERVLET_REQUEST_SSL_SESSION_ID = "javax.servlet.request.ssl_session_id";
    private static final String TOKEN_VALUE = "1111";
    private static final String TIME_ZONE = "timeZone";
    private static final String LANGUAGE = "language";
    private static final int SCREEN_HEIGHT = 1080;
    private static final int SCREEN_WIDTH = 1200;
    private static final int COLOR_DEPTH = 24;
    private static final String CVC = "cvc";
    private static final String USER_AGENT_HEADER = "userAgentHeader";

    @InjectMocks
    private AbstractWorldpayController testObj;

    @Mock
    private CheckoutFacade checkoutFacadeMock;
    @Mock
    private OAuth2AuthenticationDetails oAuth2AuthenticationDetailsMock;
    @Mock
    private Authentication authenticationMock;
    @Mock
    private WorldpayAdditionalInfoFacade worldpayAdditionalInfoFacadeMock;
    @Mock
    private DataMapper dataMapperMock;

    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private CartData cartDataMock;
    @Mock
    private BrowserInfoWsDTO browserInfoWsDTOMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    private Browser broserMock;

    @Test
    public void shouldReturnGetSessionIdFromRequestWhenAuthenticationIsNull() {
        when(requestMock.getAttribute(JAVAX_SERVLET_REQUEST_SSL_SESSION_ID)).thenReturn(SESSION_ID);

        final String result = testObj.getSessionId(requestMock);

        assertEquals(SESSION_ID, result);
    }

    @Test
    public void shouldReturnGetSessionIdFromRequestWhenDetailsAreNull() {
        SecurityContextHolder.getContext().setAuthentication(authenticationMock);
        when(authenticationMock.getDetails()).thenReturn(null);
        when(requestMock.getAttribute(JAVAX_SERVLET_REQUEST_SSL_SESSION_ID)).thenReturn(SESSION_ID);

        final String result = testObj.getSessionId(requestMock);

        assertEquals(SESSION_ID, result);
    }

    @Test
    public void shouldReturnGetSessionIdFromAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(authenticationMock);
        when(authenticationMock.getDetails()).thenReturn(oAuth2AuthenticationDetailsMock);
        when(oAuth2AuthenticationDetailsMock.getTokenValue()).thenReturn(TOKEN_VALUE);

        final String result = testObj.getSessionId(requestMock);

        assertEquals(SESSION_ID, result);
    }

    @Test
    public void handleDirectResponseShouldReturnPlaceOrderResponseWsDTO() {
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);

        final DirectResponseData directResponseData = new DirectResponseData();
        directResponseData.setIssuerURL(ISSUER_URL);
        directResponseData.setPaRequest(PA_REQUEST);
        directResponseData.setTransactionStatus(TransactionStatus.AUTHENTICATION_REQUIRED);
        directResponseData.setReturnCode(RETURN_CODE);


        final PlaceOrderResponseWsDTO result = testObj.handleDirectResponse(directResponseData, FieldSetLevelHelper.DEFAULT_LEVEL);

        assertEquals(TransactionStatus.AUTHENTICATION_REQUIRED, result.getTransactionStatus());
        assertEquals(RETURN_CODE, result.getReturnCode());
    }

    @Test
    public void createWorldpayAdditionalInfo_shouldReturnWorldpayAdditionalInfoData() {
        when(worldpayAdditionalInfoFacadeMock.createWorldpayAdditionalInfoData(requestMock)).thenReturn(worldpayAdditionalInfoDataMock);
        when(browserInfoWsDTOMock.getJavaEnabled()).thenReturn(Boolean.TRUE);
        when(browserInfoWsDTOMock.getJavascriptEnabled()).thenReturn(Boolean.TRUE);
        when(browserInfoWsDTOMock.getScreenHeight()).thenReturn(SCREEN_HEIGHT);
        when(browserInfoWsDTOMock.getScreenWidth()).thenReturn(SCREEN_WIDTH);
        when(browserInfoWsDTOMock.getColorDepth()).thenReturn(COLOR_DEPTH);
        when(browserInfoWsDTOMock.getTimeZone()).thenReturn(TIME_ZONE);
        when(browserInfoWsDTOMock.getLanguage()).thenReturn(LANGUAGE);
        when(requestMock.getAttribute(JAVAX_SERVLET_REQUEST_SSL_SESSION_ID)).thenReturn(SESSION_ID);

        final WorldpayAdditionalInfoData result = testObj.createWorldpayAdditionalInfo(requestMock, CVC, browserInfoWsDTOMock);

        verify(result).setSessionId(SESSION_ID);
        verify(result).setSecurityCode(CVC);
        verify(result).setJavaEnabled(Boolean.TRUE);
        verify(result).setJavascriptEnabled(Boolean.TRUE);
        verify(result).setTimeZone(TIME_ZONE);
        verify(result).setScreenHeight(SCREEN_HEIGHT);
        verify(result).setScreenWidth(SCREEN_WIDTH);
        verify(result).setLanguage(LANGUAGE);
        verify(result).setColorDepth(COLOR_DEPTH);
    }

}
