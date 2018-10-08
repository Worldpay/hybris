package com.worldpay.facades.payment.direct.impl;

import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.exception.WorldpayException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.TransactionStatus;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.model.Request3DInfo;
import com.worldpay.service.payment.WorldpayDirectOrderService;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import com.worldpay.strategy.WorldpayAuthenticatedShopperIdStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.b2b.services.B2BOrderService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayB2BDirectOrderFacadeTest {

    private static final String MERCHANT_CODE = "merchantCode";
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
    private MerchantInfo merchantInfoMock;
    @Mock
    private CartService cartServiceMock;
    @Mock
    private WorldpayMerchantInfoService worldpayMerchantInfoServiceMock;
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
    private WorldpayAuthenticatedShopperIdStrategy worldpayAuthenticatedShopperIdStrategyMock;
    @Mock
    private UserModel userModelMock;
    @Mock
    private B2BOrderService b2BOrderServiceMock;

    @Rule
    @SuppressWarnings("PMD.MemberScope")
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws WorldpayException {
        when(worldpayDirectOrderServiceMock.authoriseRecurringPayment(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(worldpayDirectOrderServiceMock.authoriseRecurringPayment(merchantInfoMock, orderModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(worldpayDirectOrderServiceMock.authorise3DSecure(merchantInfoMock, WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE)).thenReturn(directAuthoriseServiceResponse3dSecureMock);
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(cartModelMock.getUser()).thenReturn(userModelMock);
        when(cartModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(orderModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(worldpayMerchantInfoServiceMock.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        when(merchantInfoMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(directAuthoriseServiceResponse3dSecureMock.getRequest3DInfo()).thenReturn(request3DInfoMock);
        when(directAuthoriseServiceResponse3dSecureMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);
        when(worldpayAuthenticatedShopperIdStrategyMock.getAuthenticatedShopperId(userModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);
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
        verify(worldpayDirectOrderServiceMock).authoriseRecurringPayment(merchantInfoMock, orderModelMock, worldpayAdditionalInfoDataMock);
        assertEquals(TransactionStatus.AUTHORISED, result.getTransactionStatus());
    }

    @Test
    public void authorise3DSecureOnOrderShouldAuthoriseOrderForCode() throws WorldpayException, InvalidCartException {
        final DirectResponseData result = testObj.authorise3DSecureOnOrder(ORDER_CODE, PA_RESPONSE, worldpayAdditionalInfoDataMock);
        verify(worldpayDirectOrderServiceMock).authorise3DSecure(merchantInfoMock, WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE);
        assertEquals(TransactionStatus.AUTHORISED, result.getTransactionStatus());
    }
}
