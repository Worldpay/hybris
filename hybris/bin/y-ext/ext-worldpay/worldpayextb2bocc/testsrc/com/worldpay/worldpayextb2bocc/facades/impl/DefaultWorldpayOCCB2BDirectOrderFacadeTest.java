package com.worldpay.worldpayextb2bocc.facades.impl;

import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.TransactionStatus;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.order.InvalidCartException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayOCCB2BDirectOrderFacadeTest {

    private static final String FAILED_TO_PLACE_ORDER = "Failed to place Order";

    @InjectMocks
    private DefaultWorldpayOCCB2BDirectOrderFacade testObj;

    @Mock
    private AcceleratorCheckoutFacade acceleratorCheckoutFacadeMock;

    @Mock
    private DirectResponseData responseMock;
    @Mock
    private OrderData orderDataMock;


    @Test
    public void testHandleAuthorisedResponse_setsOrderDataAndStatus() throws InvalidCartException {
        when(acceleratorCheckoutFacadeMock.placeOrder()).thenReturn(orderDataMock);

        testObj.handleAuthorisedResponse(responseMock);

        verify(acceleratorCheckoutFacadeMock).placeOrder();
        verify(responseMock).setOrderData(orderDataMock);
        verify(responseMock).setTransactionStatus(TransactionStatus.AUTHORISED);
    }

    @Test
    public void testHandleAuthorisedResponse_shouldThrowAnException() throws InvalidCartException {
        doThrow(new InvalidCartException(FAILED_TO_PLACE_ORDER))
                .when(acceleratorCheckoutFacadeMock).placeOrder();

        final InvalidCartException exception = assertThrows(InvalidCartException.class,
                () -> testObj.handleAuthorisedResponse(responseMock));

        assertThat(exception.getMessage()).isEqualTo(FAILED_TO_PLACE_ORDER);

    }


}
