package com.worldpay.worldpayextocc.controllers;

import com.worldpay.dto.order.PlaceOrderResponseWsDTO;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.TransactionStatus;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AbstractWorldpayControllerTest {

    private static final String SESSION_ID = "1508416";
    private static final String ISSUER_URL = "issuerURL";
    private static final String RETURN_CODE = "returnCode";
    private static final String PA_REQUEST = "paRequest";
    private static final String JAVAX_SERVLET_REQUEST_SSL_SESSION_ID = "javax.servlet.request.ssl_session_id";
    public static final String TOKEN_VALUE = "1111";

    @InjectMocks
    private AbstractWorldpayController testObj;

    @Mock
    private CheckoutFacade checkoutFacadeMock;
    @Mock
    private OAuth2AuthenticationDetails oAuth2AuthenticationDetailsMock;
    @Mock
    private Authentication authenticationMock;

    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private CartData cartDataMock;

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

}