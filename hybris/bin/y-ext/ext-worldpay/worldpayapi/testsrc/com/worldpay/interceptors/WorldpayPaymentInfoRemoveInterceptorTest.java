package com.worldpay.interceptors;

import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.data.MerchantInfo;
import com.worldpay.service.payment.WorldpayDirectOrderService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayPaymentInfoRemoveInterceptorTest {

    private static final String MERCHANT_ID = "merchantId";
    private static final String SUBSCRIPTION_ID = "subscriptionId";

    @InjectMocks
    private WorldpayPaymentInfoRemoveInterceptor testObj;

    @Mock
    private WorldpayDirectOrderService worldpayDirectOrderServiceMock;
    @Mock
    private WorldpayMerchantInfoService worldpayMerchantInfoServiceMock;

    @Mock
    private CreditCardPaymentInfoModel creditCardPaymentInfoModelMock;
    @Mock
    private WorldpayAPMPaymentInfoModel worldpayAPMPaymentInfoModelMock;
    @Mock
    private PaymentInfoModel paymentInfoModelMock;
    @Mock
    private InterceptorContext interceptorContextMock;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock
    private UserModel userModelMock;

    @Before
    public void setUp() {
        when(creditCardPaymentInfoModelMock.getUser()).thenReturn(userModelMock);
        when(creditCardPaymentInfoModelMock.getSubscriptionId()).thenReturn(SUBSCRIPTION_ID);
        when(worldpayAPMPaymentInfoModelMock.getUser()).thenReturn(userModelMock);
        when(worldpayAPMPaymentInfoModelMock.getSubscriptionId()).thenReturn(SUBSCRIPTION_ID);

    }

    @Test
    public void onRemove_WhenIsCreditCartPaymentInfo_shouldDeleteTokenInWorldpay() throws Exception {
        when(creditCardPaymentInfoModelMock.getMerchantId()).thenReturn(MERCHANT_ID);
        when(worldpayMerchantInfoServiceMock.getMerchantInfoByCode(MERCHANT_ID)).thenReturn(merchantInfoMock);

        testObj.onRemove(creditCardPaymentInfoModelMock, interceptorContextMock);

        verify(worldpayDirectOrderServiceMock).deleteToken(merchantInfoMock, creditCardPaymentInfoModelMock, SUBSCRIPTION_ID);
    }

    @Test
    public void onRemove_WhenIsAPMPaymentInfo_shouldDeleteTokenInWorldpay() throws Exception {
        when(worldpayAPMPaymentInfoModelMock.getMerchantId()).thenReturn(MERCHANT_ID);
        when(worldpayMerchantInfoServiceMock.getMerchantInfoByCode(MERCHANT_ID)).thenReturn(merchantInfoMock);

        testObj.onRemove(worldpayAPMPaymentInfoModelMock, interceptorContextMock);

        verify(worldpayDirectOrderServiceMock).deleteToken(merchantInfoMock, worldpayAPMPaymentInfoModelMock, SUBSCRIPTION_ID);
    }


    @Test
    public void onRemove_WhenCCPaymentInfoTokenIsNotAttachedToUser_ShouldNotDeleteIt() throws Exception {
        when(creditCardPaymentInfoModelMock.getMerchantId()).thenReturn(MERCHANT_ID);
        when(creditCardPaymentInfoModelMock.getUser()).thenReturn(null);
        when(worldpayMerchantInfoServiceMock.getMerchantInfoByCode(MERCHANT_ID)).thenReturn(merchantInfoMock);

        testObj.onRemove(creditCardPaymentInfoModelMock, interceptorContextMock);

        verifyZeroInteractions(worldpayDirectOrderServiceMock);
    }

    @Test
    public void onRemove_WhenAPMPaymentInfoTokenIsNotAttachedToUser_ShouldNotDeleteIt() throws Exception {
        when(worldpayAPMPaymentInfoModelMock.getMerchantId()).thenReturn(MERCHANT_ID);
        when(worldpayAPMPaymentInfoModelMock.getUser()).thenReturn(null);
        when(worldpayMerchantInfoServiceMock.getMerchantInfoByCode(MERCHANT_ID)).thenReturn(merchantInfoMock);

        testObj.onRemove(worldpayAPMPaymentInfoModelMock, interceptorContextMock);

        verifyZeroInteractions(worldpayDirectOrderServiceMock);
    }

    @Test
    public void onRemove_WhenDuplicateCCPaymentInfo_shouldNotDeleteToken() throws Exception {
        when(creditCardPaymentInfoModelMock.getMerchantId()).thenReturn(MERCHANT_ID);
        when(creditCardPaymentInfoModelMock.getDuplicate()).thenReturn(true);
        when(worldpayMerchantInfoServiceMock.getMerchantInfoByCode(MERCHANT_ID)).thenReturn(merchantInfoMock);

        testObj.onRemove(creditCardPaymentInfoModelMock, interceptorContextMock);

        verifyZeroInteractions(worldpayDirectOrderServiceMock);
    }

    @Test
    public void onRemove_WhenDuplicateAPMPaymentInfo_shouldNotDeleteToken() throws Exception {
        when(worldpayAPMPaymentInfoModelMock.getMerchantId()).thenReturn(MERCHANT_ID);
        when(worldpayAPMPaymentInfoModelMock.getDuplicate()).thenReturn(true);
        when(worldpayMerchantInfoServiceMock.getMerchantInfoByCode(MERCHANT_ID)).thenReturn(merchantInfoMock);

        testObj.onRemove(worldpayAPMPaymentInfoModelMock, interceptorContextMock);

        verifyZeroInteractions(worldpayDirectOrderServiceMock);
    }

    @Test
    public void onRemove_WhenNormalPaymentInfo_ShouldNotDeleteToken() {
        testObj.onRemove(paymentInfoModelMock, interceptorContextMock);

        verifyZeroInteractions(worldpayDirectOrderServiceMock);
        verifyZeroInteractions(worldpayMerchantInfoServiceMock);
    }

    @Test
    public void onRemove_WhenNoSubscriptionIdForCCPaymentInfo_shouldNotDeleteToken() {
        when(creditCardPaymentInfoModelMock.getSubscriptionId()).thenReturn(null);

        testObj.onRemove(creditCardPaymentInfoModelMock, interceptorContextMock);

        verifyZeroInteractions(worldpayDirectOrderServiceMock);
        verifyZeroInteractions(worldpayMerchantInfoServiceMock);
    }

    @Test
    public void onRemove_WhenNoSubscriptionIdForAPMPaymentInfo_shouldNotDeleteToken() {
        when(worldpayAPMPaymentInfoModelMock.getSubscriptionId()).thenReturn(null);

        testObj.onRemove(worldpayAPMPaymentInfoModelMock, interceptorContextMock);

        verifyZeroInteractions(worldpayDirectOrderServiceMock);
        verifyZeroInteractions(worldpayMerchantInfoServiceMock);
    }
}
