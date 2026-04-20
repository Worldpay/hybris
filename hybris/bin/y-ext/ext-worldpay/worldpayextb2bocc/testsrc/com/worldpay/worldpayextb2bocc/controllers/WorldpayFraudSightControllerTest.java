package com.worldpay.worldpayextb2bocc.controllers;


import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayFraudSightControllerTest {

    @InjectMocks
    private WorldpayFraudSightController testObj;

    @Mock
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacadeMock;

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

}
