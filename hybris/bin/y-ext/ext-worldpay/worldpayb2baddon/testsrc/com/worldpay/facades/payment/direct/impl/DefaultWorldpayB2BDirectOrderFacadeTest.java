package com.worldpay.facades.payment.direct.impl;

import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.exception.WorldpayException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.TransactionStatus;
import com.worldpay.data.PaymentReply;
import com.worldpay.data.Request3DInfo;
import com.worldpay.service.payment.WorldpayDirectOrderService;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.b2b.services.B2BOrderService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayB2BDirectOrderFacadeTest {

    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperId";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String ORDER_CODE = "orderCode";
    private static final String PA_RESPONSE = "paResponse";

    @InjectMocks
    private DefaultWorldpayB2BDirectOrderFacade testObj;

    @Mock
    private WorldpayDirectOrderService worldpayDirectOrderServiceMock;
    @Mock
    private CartModel cartModelMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private CartService cartServiceMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    private DirectAuthoriseServiceResponse directAuthoriseServiceResponseMock, directAuthoriseServiceResponse3dSecureMock;
    @Mock
    private AcceleratorCheckoutFacade acceleratorCheckoutFacadeMock;
    @Mock
    private PaymentReply paymentReplyMock;
    @Mock
    private Request3DInfo request3DInfoMock;
    @Mock
    private WorldpayCartService worldpayCartServiceMock;
    @Mock
    private B2BOrderService b2BOrderServiceMock;

    @Before
    public void setUp() throws WorldpayException {
        when(worldpayDirectOrderServiceMock.authoriseRecurringPayment(cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(worldpayDirectOrderServiceMock.authoriseRecurringPayment(orderModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(worldpayDirectOrderServiceMock.authorise3DSecure(WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE)).thenReturn(directAuthoriseServiceResponse3dSecureMock);
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(cartModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(orderModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(directAuthoriseServiceResponse3dSecureMock.getRequest3DInfo()).thenReturn(request3DInfoMock);
        when(directAuthoriseServiceResponse3dSecureMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);
        when(worldpayCartServiceMock.getAuthenticatedShopperId(cartModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);
        when(b2BOrderServiceMock.getOrderForCode(ORDER_CODE)).thenReturn(orderModelMock);
    }

    @Test
    public void authoriseRecurringPaymentShouldNotPlaceOrder() throws WorldpayException, InvalidCartException {
        final DirectResponseData result = testObj.authoriseRecurringPayment(worldpayAdditionalInfoDataMock);

        verify(acceleratorCheckoutFacadeMock, never()).placeOrder();

        assertEquals(TransactionStatus.AUTHORISED, result.getTransactionStatus());
    }

    @Test
    public void authoriseRecurringPaymentShouldAuthoriseOrderForCode() throws WorldpayException, InvalidCartException {
        final DirectResponseData result = testObj.authoriseRecurringPayment(ORDER_CODE, worldpayAdditionalInfoDataMock);
        verify(worldpayDirectOrderServiceMock).authoriseRecurringPayment(orderModelMock, worldpayAdditionalInfoDataMock);
        assertEquals(TransactionStatus.AUTHORISED, result.getTransactionStatus());
    }

    @Test
    public void authorise3DSecureOnOrderShouldAuthoriseOrderForCode() throws WorldpayException, InvalidCartException {
        final DirectResponseData result = testObj.authorise3DSecureOnOrder(ORDER_CODE, PA_RESPONSE, worldpayAdditionalInfoDataMock);
        verify(worldpayDirectOrderServiceMock).authorise3DSecure(WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE);
        assertEquals(TransactionStatus.AUTHORISED, result.getTransactionStatus());
    }
}
