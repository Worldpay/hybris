package com.worldpay.facades.payment.hosted.impl;

import com.google.common.collect.ImmutableMap;
import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.core.services.APMConfigurationLookupService;
import com.worldpay.core.services.OrderInquiryService;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.merchant.strategies.WorldpayOrderInfoStrategy;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.service.model.AuthorisedStatus;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.payment.WorldpayRedirectOrderService;
import com.worldpay.service.response.OrderInquiryServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static com.worldpay.facades.payment.hosted.impl.DefaultWorldpayHostedOrderFacade.WORLDPAY_MERCHANT_CODE;
import static junit.framework.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayHostedOrderFacadeTest {

    private static final String MERCHANT_CODE = "merchantCode";
    private static final ImmutableMap<String, String> WORLDPAY_RESPONSE = ImmutableMap.of("responseKey", "responseValue");
    private static final String WORLDPAY_ORDER_CODE = "orderCode";

    @InjectMocks
    private DefaultWorldpayHostedOrderFacade testObj;

    @Mock
    private WorldpayRedirectOrderService worldpayRedirectOrderServiceMock;
    @Mock
    private WorldpayMerchantInfoService worldpayMerchantInfoServiceMock;
    @Mock
    private CartService cartServiceMock;
    @Mock
    private WorldpayOrderInfoStrategy worldpayOrderInfoStrategyMock;
    @Mock
    private CartModel cartModelMock;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock
    private AdditionalAuthInfo additionalAuthInfoMock;
    @Mock
    private SessionService sessionServiceMock;
    @Mock
    private WorldpayMerchantConfigData worldpayMerchantConfigDataMock;
    @Mock
    private RedirectAuthoriseResult redirectAuthoriseResultMock;
    @Mock
    private WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacadeMock;
    @Mock
    private OrderInquiryService orderInquiryServiceMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private PaymentReply paymentReplyMock;
    @Mock
    private APMConfigurationLookupService apmConfigurationLookupService;
    @Mock
    private WorldpayAPMConfigurationModel worldpayAPMConfigurationModel;
    @Mock
    private OrderInquiryServiceResponse orderInquiryServiceResponseMock;

    @Before
    public void setUp() throws WorldpayConfigurationException {
        when(sessionServiceMock.getAttribute(WORLDPAY_MERCHANT_CODE)).thenReturn(MERCHANT_CODE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(orderInquiryServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
    }

    @Test
    public void shouldDoRedirectAuthorise() throws WorldpayException {
        when(worldpayMerchantInfoServiceMock.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        when(worldpayMerchantConfigDataFacadeMock.getCurrentSiteMerchantConfigData()).thenReturn(worldpayMerchantConfigDataMock);

        testObj.redirectAuthorise(additionalAuthInfoMock);

        verify(worldpayOrderInfoStrategyMock).populateAdditionalAuthInfo(additionalAuthInfoMock, worldpayMerchantConfigDataMock);
        verify(worldpayRedirectOrderServiceMock).redirectAuthorise(merchantInfoMock, cartModelMock, additionalAuthInfoMock);
    }

    @Test
    public void shouldCompleteRedirect() {
        testObj.completeRedirectAuthorise(redirectAuthoriseResultMock);

        verify(worldpayRedirectOrderServiceMock).completeRedirectAuthorise(redirectAuthoriseResultMock, MERCHANT_CODE, cartModelMock);
    }

    @Test
    public void shouldReturnTrueWhenRedirectResponseIsValid() throws WorldpayConfigurationException {
        when(worldpayMerchantInfoServiceMock.getMerchantInfoByCode(MERCHANT_CODE)).thenReturn(merchantInfoMock);
        when(worldpayRedirectOrderServiceMock.validateRedirectResponse(eq(merchantInfoMock), eq(WORLDPAY_RESPONSE))).thenReturn(true);

        assertTrue(testObj.validateRedirectResponse(WORLDPAY_RESPONSE));
    }

    @Test
    public void shouldReturnFalseWhenRedirectResponseIsNotValid() throws WorldpayConfigurationException {
        when(worldpayRedirectOrderServiceMock.validateRedirectResponse(eq(merchantInfoMock), eq(WORLDPAY_RESPONSE))).thenReturn(false);

        assertFalse(testObj.validateRedirectResponse(WORLDPAY_RESPONSE));
    }

    @Test
    public void shouldInquiryPaymentStatusRetrievingOrderCodeFromCartForAPM() throws WorldpayException {
        when(worldpayMerchantInfoServiceMock.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        when(cartModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(orderInquiryServiceMock.inquireOrder(merchantInfoMock, WORLDPAY_ORDER_CODE)).thenReturn(orderInquiryServiceResponseMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);
        when(paymentReplyMock.getAmount().getValue()).thenReturn("12345");
        when(paymentReplyMock.getAmount().getExponent()).thenReturn("2");
        when(paymentReplyMock.getMethodCode()).thenReturn("paymentTypeCode");
        when(apmConfigurationLookupService.getAPMConfigurationForCode("paymentTypeCode")).thenReturn(worldpayAPMConfigurationModel);

        final RedirectAuthoriseResult result = testObj.inquiryPaymentStatus();

        assertEquals(AuthorisedStatus.AUTHORISED.getCode(), result.getPaymentStatus());
        assertEquals(BigDecimal.valueOf(123.45D), result.getPaymentAmount());
        assertTrue(result.getPending());
    }

    @Test
    public void shouldInquiryPaymentStatusRetrievingOrderCodeFromCartForNotAPM() throws WorldpayException {
        when(worldpayMerchantInfoServiceMock.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        when(cartModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(orderInquiryServiceMock.inquireOrder(merchantInfoMock, WORLDPAY_ORDER_CODE)).thenReturn(orderInquiryServiceResponseMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);
        when(paymentReplyMock.getAmount().getValue()).thenReturn("12345");
        when(paymentReplyMock.getAmount().getExponent()).thenReturn("2");
        when(paymentReplyMock.getMethodCode()).thenReturn("paymentTypeCode");
        when(apmConfigurationLookupService.getAPMConfigurationForCode("paymentTypeCode")).thenReturn(null);

        final RedirectAuthoriseResult result = testObj.inquiryPaymentStatus();

        assertEquals(AuthorisedStatus.AUTHORISED.getCode(), result.getPaymentStatus());
        assertEquals(BigDecimal.valueOf(123.45D), result.getPaymentAmount());
        assertFalse(result.getPending());
    }
}
