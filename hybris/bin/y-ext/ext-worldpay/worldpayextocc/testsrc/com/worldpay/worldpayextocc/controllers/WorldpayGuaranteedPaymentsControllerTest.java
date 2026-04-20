package com.worldpay.worldpayextocc.controllers;

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
public class WorldpayGuaranteedPaymentsControllerTest {

    public WorldpayGuaranteedPaymentsControllerTest() {
        super();
    }

    @InjectMocks
    private WorldpayGuaranteedPaymentsController testObj;

    @Mock
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacadeMock;

    @Test
    public void isGuaranteedPaymentsEnabled_ShouldReturnTrue_WhenFacadeReturnsTrue() {
        when(worldpayPaymentCheckoutFacadeMock.isGPEnabled()).thenReturn(true);

        boolean result = testObj.isGuaranteedPaymentsEnabled();

        assertTrue(result);
        verify(worldpayPaymentCheckoutFacadeMock).isGPEnabled();
    }

    @Test
    public void isGuaranteedPaymentsEnabled_ShouldReturnFalse_WhenFacadeReturnsFalse() {
        when(worldpayPaymentCheckoutFacadeMock.isGPEnabled()).thenReturn(false);

        boolean result = testObj.isGuaranteedPaymentsEnabled();

        assertFalse(result);
        verify(worldpayPaymentCheckoutFacadeMock).isGPEnabled();
    }

}
