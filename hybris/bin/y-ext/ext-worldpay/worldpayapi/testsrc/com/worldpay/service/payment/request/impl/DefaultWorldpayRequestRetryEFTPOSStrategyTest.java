package com.worldpay.service.payment.request.impl;

import com.worldpay.data.ErrorDetail;
import com.worldpay.data.Order;
import com.worldpay.data.PaymentDetails;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.request.DirectAuthoriseServiceRequest;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayRequestRetryEFTPOSStrategyTest {

    private static final String PAYMENT_DETAILS_ERROR_CODE = "7";
    private static final String ROUTING_MID = "EFTPOS_DUMMY";
    private static final String NEW_ORDER_CODE = "newOrderCode";

    @InjectMocks
    private DefaultWorldpayRequestRetryEFTPOSStrategy testObj;

    @Mock
    private WorldpayOrderService worldpayOrderServiceMock;
    @Mock
    private CartService cartServiceMock;
    @Mock
    private CartModel cartMock;
    @Mock
    private DirectAuthoriseServiceRequest requestMock;
    @Mock
    private DirectAuthoriseServiceResponse responseMock;
    @Mock
    private ErrorDetail errorDetailMock;
    @Mock
    private Order orderMock;
    @Mock
    private PaymentDetails paymentDetailsMock;

    @Test
    public void isRequestToBeRetried_shouldReturnTrue_WhenResponseContainsAPaymentDetailsErrorCodeAndRequestContainsRoutingMID() {
        when(responseMock.isError()).thenReturn(true);
        when(responseMock.getErrorDetail()).thenReturn(errorDetailMock);
        when(errorDetailMock.getCode()).thenReturn(PAYMENT_DETAILS_ERROR_CODE);
        when(requestMock.getOrder()).thenReturn(orderMock);
        when(orderMock.getPaymentDetails()).thenReturn(paymentDetailsMock);
        when(paymentDetailsMock.getRoutingMID()).thenReturn(ROUTING_MID);

        assertTrue(testObj.isRequestToBeRetried(requestMock, responseMock));
    }

    @Test
    public void isRequestToBeRetried_shouldReturnFalse_WhenResponseContainsAPaymentDetailsErrorCodeAndRequestDoesNotContainRoutingMID() {
        when(responseMock.isError()).thenReturn(true);
        when(responseMock.getErrorDetail()).thenReturn(errorDetailMock);
        when(errorDetailMock.getCode()).thenReturn(PAYMENT_DETAILS_ERROR_CODE);
        when(requestMock.getOrder()).thenReturn(orderMock);
        when(orderMock.getPaymentDetails()).thenReturn(paymentDetailsMock);

        assertFalse(testObj.isRequestToBeRetried(requestMock, responseMock));
    }

    @Test
    public void isRequestToBeRetried_shouldReturnFalse_WhenResponseContainsAPaymentDetailsErrorCodeAndRequestDoesNotContainOrder() {
        when(responseMock.isError()).thenReturn(true);
        when(responseMock.getErrorDetail()).thenReturn(errorDetailMock);
        when(errorDetailMock.getCode()).thenReturn(PAYMENT_DETAILS_ERROR_CODE);

        assertFalse(testObj.isRequestToBeRetried(requestMock, responseMock));
    }

    @Test
    public void isRequestToBeRetried_shouldReturnFalse_WhenResponseContainsANonPaymentDetailsErrorCode() {
        when(responseMock.isError()).thenReturn(true);
        when(responseMock.getErrorDetail()).thenReturn(errorDetailMock);
        when(errorDetailMock.getCode()).thenReturn("error");

        assertFalse(testObj.isRequestToBeRetried(requestMock, responseMock));
    }

    @Test
    public void isRequestToBeRetried_shouldReturnFalse_WhenResponseContainsANullErrorCode() {
        when(responseMock.isError()).thenReturn(true);
        when(responseMock.getErrorDetail()).thenReturn(errorDetailMock);

        assertFalse(testObj.isRequestToBeRetried(requestMock, responseMock));
    }

    @Test
    public void isRequestToBeRetried_shouldReturnFalse_WhenResponseContainsNoError() {
        assertFalse(testObj.isRequestToBeRetried(requestMock, responseMock));
    }

    @Test
    public void getDirectAuthoriseServiceRequestToRetry_shouldSetRoutingMIDToNullAndGenerateNewOrderCode_whenInvoked() {
        when(cartServiceMock.getSessionCart()).thenReturn(cartMock);
        when(requestMock.getOrder()).thenReturn(orderMock);
        when(orderMock.getPaymentDetails()).thenReturn(paymentDetailsMock);
        when(worldpayOrderServiceMock.generateWorldpayOrderCode(cartMock)).thenReturn(NEW_ORDER_CODE);

        testObj.getDirectAuthoriseServiceRequestToRetry(requestMock, responseMock);

        verify(cartServiceMock).getSessionCart();
        verify(paymentDetailsMock).setRoutingMID(null);
        verify(worldpayOrderServiceMock).generateWorldpayOrderCode(cartMock);
    }

}
