package com.worldpay.facades.payment.direct.impl;

import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.data.ACHDirectDebitAdditionalAuthInfo;
import com.worldpay.data.PaymentReply;
import com.worldpay.data.Request3DInfo;
import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.order.impl.WorldpayB2BAcceleratorCheckoutFacadeDecorator;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.TransactionStatus;
import com.worldpay.service.payment.WorldpayDirectOrderService;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.services.B2BOrderService;
import de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayB2BDirectOrderFacadeTest {

    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperId";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String ORDER_CODE = "orderCode";
    private static final String PA_RESPONSE = "paResponse";

    @Spy
    @InjectMocks
    private DefaultWorldpayB2BDirectOrderFacade testObj;

    @Mock
    private WorldpayDirectOrderService worldpayDirectOrderServiceMock;
    @Mock
    private CartService cartServiceMock;
    @Mock
    private WorldpayB2BAcceleratorCheckoutFacadeDecorator checkoutFacadeMock;
    @Mock
    private WorldpayCartService worldpayCartServiceMock;
    @Mock
    private B2BOrderService b2BOrderServiceMock;

    @Mock
    private ACHDirectDebitAdditionalAuthInfo additionalAuthInfoMock;
    @Mock
    private CartModel cartModelMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private OrderData orderDataMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    private DirectAuthoriseServiceResponse directAuthoriseServiceResponseMock, directAuthoriseServiceResponse3dSecureMock;
    @Mock
    private PaymentReply paymentReplyMock;
    @Mock
    private Request3DInfo request3DInfoMock;

    @Before
    public void setUp() throws WorldpayException {
        doReturn(checkoutFacadeMock).when(testObj).getB2BCheckoutFacade();
        when(worldpayDirectOrderServiceMock.authoriseRecurringPayment(cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(worldpayDirectOrderServiceMock.authoriseRecurringPayment(orderModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(worldpayDirectOrderServiceMock.authorise3DSecure(WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE)).thenReturn(directAuthoriseServiceResponse3dSecureMock);
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        lenient().when(cartModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(orderModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        lenient().when(directAuthoriseServiceResponse3dSecureMock.getRequest3DInfo()).thenReturn(request3DInfoMock);
        when(directAuthoriseServiceResponse3dSecureMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);
        when(worldpayCartServiceMock.getAuthenticatedShopperId(cartModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);
        when(b2BOrderServiceMock.getOrderForCode(ORDER_CODE)).thenReturn(orderModelMock);
    }

    @Test
    public void authoriseRecurringPaymentShouldNotPlaceOrder() throws WorldpayException, InvalidCartException {
        final DirectResponseData result = testObj.authoriseRecurringPayment(worldpayAdditionalInfoDataMock);

        verify(checkoutFacadeMock, never()).placeOrder(any());

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

    @Test
    public void handleACHAuthorisedResponse_ShouldSetOrderDataAndStatus() throws InvalidCartException {
        final DirectResponseData response = new DirectResponseData();
        when(checkoutFacadeMock.placeOrder(any(PlaceOrderData.class))).thenReturn(orderDataMock);

        testObj.handleACHAuthorisedResponse(response);

        assertEquals(orderDataMock, response.getOrderData());
        assertEquals(TransactionStatus.AUTHORISED, response.getTransactionStatus());
    }

    @Test
    public void authoriseACHDirectDebit_ShouldReturnDirectResponseData() throws Exception {
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(worldpayDirectOrderServiceMock.authoriseACHDirectDebit(cartModelMock, additionalAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        DirectResponseData expectedResponse = new DirectResponseData();
        doReturn(expectedResponse).when(testObj).handleACHDirectDebitResponse(directAuthoriseServiceResponseMock, cartModelMock);

        DirectResponseData result = testObj.authoriseACHDirectDebit(worldpayAdditionalInfoDataMock, additionalAuthInfoMock);

        assertSame(expectedResponse, result);
        verify(worldpayDirectOrderServiceMock).authoriseACHDirectDebit(cartModelMock, additionalAuthInfoMock, worldpayAdditionalInfoDataMock);
    }

    @Test(expected = WorldpayConfigurationException.class)
    public void authoriseACHDirectDebit_ShouldThrowWorldpayConfigurationException() throws Exception {
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(worldpayDirectOrderServiceMock.authoriseACHDirectDebit(cartModelMock, additionalAuthInfoMock, worldpayAdditionalInfoDataMock))
                .thenThrow(new WorldpayConfigurationException("No config"));

        testObj.authoriseACHDirectDebit(worldpayAdditionalInfoDataMock, additionalAuthInfoMock);
    }

    @Test(expected = InvalidCartException.class)
    public void authoriseACHDirectDebit_ShouldThrowInvalidCartException() throws Exception {
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(cartModelMock.getCode()).thenReturn("014124");
        doThrow(new InvalidCartException("Invalid cart")).when(worldpayDirectOrderServiceMock).authoriseACHDirectDebit(cartModelMock, additionalAuthInfoMock, worldpayAdditionalInfoDataMock);


        testObj.authoriseACHDirectDebit(worldpayAdditionalInfoDataMock, additionalAuthInfoMock);
    }

    @Test
    public void handleACHDirectDebitResponse_ShouldProcessACHResponse() throws Exception {
        DirectResponseData expected = new DirectResponseData();
        doReturn(true).when(testObj).shouldProcessACHResponse(directAuthoriseServiceResponseMock);
        doReturn(expected).when(testObj).processACHDirectDebitDirectResponse(directAuthoriseServiceResponseMock, cartModelMock);

        DirectResponseData result = testObj.handleACHDirectDebitResponse(directAuthoriseServiceResponseMock, cartModelMock);

        assertSame(expected, result);
    }

    @Test
    public void handleACHDirectDebitResponse_ShouldHandleErrorOnServiceResponse() throws Exception {
        DirectResponseData expected = new DirectResponseData();
        doReturn(false).when(testObj).shouldProcessACHResponse(directAuthoriseServiceResponseMock);
        doReturn(expected).when(testObj).handleErrorOnServiceResponse(directAuthoriseServiceResponseMock);

        DirectResponseData result = testObj.handleACHDirectDebitResponse(directAuthoriseServiceResponseMock, cartModelMock);

        assertSame(expected, result);
    }

}
