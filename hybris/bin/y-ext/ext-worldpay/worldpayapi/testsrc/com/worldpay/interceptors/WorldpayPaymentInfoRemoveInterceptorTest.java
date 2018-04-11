package com.worldpay.interceptors;

import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.payment.WorldpayDirectOrderService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

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
    }

    @Test
    public void shouldDeleteTokenInWorldpay() throws Exception {
        when(creditCardPaymentInfoModelMock.getMerchantId()).thenReturn(MERCHANT_ID);
        when(worldpayMerchantInfoServiceMock.getMerchantInfoByCode(MERCHANT_ID)).thenReturn(merchantInfoMock);

        testObj.onRemove(creditCardPaymentInfoModelMock, interceptorContextMock);

        verify(worldpayDirectOrderServiceMock).deleteToken(merchantInfoMock, creditCardPaymentInfoModelMock);
    }

    @Test
    public void shouldNotDeleteTokenNotAttachedToUser() throws Exception {
        when(creditCardPaymentInfoModelMock.getMerchantId()).thenReturn(MERCHANT_ID);
        when(creditCardPaymentInfoModelMock.getUser()).thenReturn(null);
        when(worldpayMerchantInfoServiceMock.getMerchantInfoByCode(MERCHANT_ID)).thenReturn(merchantInfoMock);

        testObj.onRemove(creditCardPaymentInfoModelMock, interceptorContextMock);

        verifyZeroInteractions(worldpayDirectOrderServiceMock);
    }

    @Test
    public void shouldNotDeleteTokenForDuplicatePaymentInfo() throws Exception {
        when(creditCardPaymentInfoModelMock.getMerchantId()).thenReturn(MERCHANT_ID);
        when(creditCardPaymentInfoModelMock.getDuplicate()).thenReturn(true);
        when(worldpayMerchantInfoServiceMock.getMerchantInfoByCode(MERCHANT_ID)).thenReturn(merchantInfoMock);

        testObj.onRemove(creditCardPaymentInfoModelMock, interceptorContextMock);

        verifyZeroInteractions(worldpayDirectOrderServiceMock);
    }
    
    @Test
    public void shouldNotDeleteTokenIfNormalPaymentInfo() throws Exception {
        testObj.onRemove(paymentInfoModelMock, interceptorContextMock);

        verifyZeroInteractions(worldpayDirectOrderServiceMock);
        verifyZeroInteractions(worldpayMerchantInfoServiceMock);
    }

    @Test
    public void shouldNotDeleteTokenIfNoSubscriptionId() throws Exception {
        when(creditCardPaymentInfoModelMock.getSubscriptionId()).thenReturn(null);

        testObj.onRemove(creditCardPaymentInfoModelMock, interceptorContextMock);

        verifyZeroInteractions(worldpayDirectOrderServiceMock);
        verifyZeroInteractions(worldpayMerchantInfoServiceMock);
    }
}
