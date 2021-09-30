package com.worldpay.security;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import org.apache.logging.log4j.util.Strings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayCommerceRedirectStrategyTest {

    private static final String EXPRESS_CHECKOUT_ENABLED = "expressCheckoutEnabled";
    private static final String EXPRESS_CHECKOUT_ENABLED_TRUE = "True";
    private static final String DEFAULT_URL = "defaultUrl";
    private static final String EXPRESS_CHECKOUT_URL = "expressCheckoutUrl";

    @InjectMocks
    private WorldpayCommerceRedirectStrategy testObj;

    @Mock
    private CheckoutFlowFacade checkoutFlowFacadeMock;

    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private HttpServletResponse responseMock;

    @Before
    public void setUp() {
        testObj = new WorldpayCommerceRedirectStrategy(checkoutFlowFacadeMock, EXPRESS_CHECKOUT_URL);

        when(checkoutFlowFacadeMock.isExpressCheckoutEnabledForStore()).thenReturn(Boolean.TRUE);
        when(requestMock.getParameter(EXPRESS_CHECKOUT_ENABLED)).thenReturn(EXPRESS_CHECKOUT_ENABLED_TRUE);
    }

    @Test
    public void sendRedirect_WhenExpressCheckoutIsNotEnabledForStore_ShouldNotChangeUrl() throws IOException {
        when(checkoutFlowFacadeMock.isExpressCheckoutEnabledForStore()).thenReturn(Boolean.FALSE);

        testObj.sendRedirect(requestMock, responseMock, DEFAULT_URL);

        assertThat(testObj.determineRedirectUrl(requestMock, DEFAULT_URL)).isEqualTo(DEFAULT_URL);
    }

    @Test
    public void sendRedirect_WhenHasNotExpressCheckoutEnabledParameter_ShouldNotChangeUrl() throws IOException {
        when(requestMock.getParameter(EXPRESS_CHECKOUT_ENABLED)).thenReturn(Strings.EMPTY);

        testObj.sendRedirect(requestMock, responseMock, DEFAULT_URL);

        assertThat(testObj.determineRedirectUrl(requestMock, DEFAULT_URL)).isEqualTo(DEFAULT_URL);
    }

    @Test
    public void sendRedirect_WhenExpressCheckoutIsNotEnabledForStoreAndHasExpressCheckoutEnabledParameter_ShouldChangeUrl() throws IOException {
        testObj.sendRedirect(requestMock, responseMock, DEFAULT_URL);

        assertThat(testObj.determineRedirectUrl(requestMock, DEFAULT_URL)).isEqualTo(EXPRESS_CHECKOUT_URL);
    }
}
