package com.worldpay.worldpayextb2bocc.controllers;

import com.worldpay.facades.order.impl.WorldpayB2BAcceleratorCheckoutFacadeDecorator;
import com.worldpay.worldpayextb2bocc.exceptions.WorldpayInvalidPaymentInfoException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayCartPaymentsControllerTest {

    private static final String VALID_ID = "validId";
    private static final String INVALID_ID = "invalidId";

    @Spy
    @InjectMocks
    private WorldpayCartPaymentsController testObj;

    @Mock
    private WorldpayB2BAcceleratorCheckoutFacadeDecorator worldpayB2BAcceleratorCheckoutFacadeDecoratorMock;
    @Mock
    private CartFacade cartFacadeCommercewebservicesMock;

    @Mock
    private CartData cartDataMock;

    @Test
    public void setPaymentDetailsInternal_ShouldReturnCartData_WhenPaymentDetailsAreValid() throws Exception {
        when(worldpayB2BAcceleratorCheckoutFacadeDecoratorMock.setPaymentDetails(VALID_ID)).thenReturn(true);
        when(cartFacadeCommercewebservicesMock.getSessionCart()).thenReturn(cartDataMock);

        final CartData result = testObj.setPaymentDetailsInternal(VALID_ID);

        assertEquals(cartDataMock, result);
        verify(worldpayB2BAcceleratorCheckoutFacadeDecoratorMock).setPaymentDetails(VALID_ID);
        verify(cartFacadeCommercewebservicesMock).getSessionCart();
    }

    @Test
    public void setPaymentDetailsInternal_ShouldThrowException_WhenPaymentDetailsAreInvalid() {
        when(worldpayB2BAcceleratorCheckoutFacadeDecoratorMock.setPaymentDetails(INVALID_ID)).thenReturn(false);

        assertThrows(WorldpayInvalidPaymentInfoException.class, () -> {
            testObj.setPaymentDetailsInternal(INVALID_ID);
        });
        verify(worldpayB2BAcceleratorCheckoutFacadeDecoratorMock).setPaymentDetails(INVALID_ID);
        verifyNoInteractions(cartFacadeCommercewebservicesMock);
    }


    @Test
    public void replaceCartPaymentDetails_ShouldPropagateException() throws Exception {

        doThrow(new WorldpayInvalidPaymentInfoException(INVALID_ID))
                .when(testObj).setPaymentDetailsInternal(INVALID_ID);

        assertThrows(WorldpayInvalidPaymentInfoException.class, () -> {
            testObj.replaceCartPaymentDetails(INVALID_ID);
        });
        verify(testObj).setPaymentDetailsInternal(INVALID_ID);
    }
}
