package com.worldpay.worldpayextb2bocc.controllers;


import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayFraudSightControllerTest {

    private static final String WORLDPAY_CONFIG_FRAUDSIGHT_PUBLISHABLE_KEY = "worldpay.config.fraudsight.publishableKey";
    private static final String PUBLIC_KEY = "1234567";

    @InjectMocks
    private WorldpayFraudSightController testObj;

    @Mock
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacadeMock;
    @Mock
    private SiteConfigService siteConfigServiceMock;

    @Test
    public void isFraudSightEnabled_ShouldReturnTrue_WhenFacadeReturnsTrue() {
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);

        boolean result = testObj.isFraudSightEnabled();

        assertTrue(result);
        verify(worldpayPaymentCheckoutFacadeMock).isFSEnabled();
    }

    @Test
    public void isFraudSightEnabled_ShouldReturnFalse_WhenFacadeReturnsFalse() {
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(false);

        boolean result = testObj.isFraudSightEnabled();

        assertFalse(result);
        verify(worldpayPaymentCheckoutFacadeMock).isFSEnabled();
    }

    @Test
    public void getPublicKey_ShouldReturnPublicKey_WhenIsSet() {
        when(siteConfigServiceMock.getProperty(WORLDPAY_CONFIG_FRAUDSIGHT_PUBLISHABLE_KEY)).thenReturn(PUBLIC_KEY);

        String result = testObj.getPublicKey();

        assertEquals(PUBLIC_KEY, result);
        verify(siteConfigServiceMock).getProperty(WORLDPAY_CONFIG_FRAUDSIGHT_PUBLISHABLE_KEY);
    }

    @Test
    public void getPublicKey_ShouldReturEmpty_WhenIsNotSet() {
        when(siteConfigServiceMock.getProperty(WORLDPAY_CONFIG_FRAUDSIGHT_PUBLISHABLE_KEY)).thenReturn(StringUtils.EMPTY);

        String result = testObj.getPublicKey();

        assertTrue(result.isEmpty());
        verify(siteConfigServiceMock).getProperty(WORLDPAY_CONFIG_FRAUDSIGHT_PUBLISHABLE_KEY);
    }

}
