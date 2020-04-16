package com.worldpay.service.request.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.*;
import com.worldpay.service.model.BasicOrderInfo;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.model.Shopper;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.request.AuthoriseServiceRequest;
import com.worldpay.service.request.RedirectAuthoriseServiceRequest;
import com.worldpay.service.request.SecondThreeDSecurePaymentRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static com.worldpay.service.model.payment.PaymentType.ONLINE;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SecondThreeDSecurePaymentRequestTransformerTest {

    private static final String WORLDPAY_CONFIG_VERSION = "worldpay.config.version";
    private static final String SESSION_ID = "SESSION_ID";
    private static final String MERCHANT_INFO_CODE = "merchant_info_code";
    private static final String ORDER_CODE = "order_code";
    private static final String VERSION = "1.4";

    @InjectMocks
    private SecondThreeDSecurePaymentRequestTransformer testObj;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ConfigurationService configurationServiceMock;
    @Mock
    private SecondThreeDSecurePaymentRequest secondThreeDSecurePaymentRequestMock;
    @Mock
    private MerchantInfo merchantInfoMock;

    @Before
    public void setUp() throws Exception {
        when(configurationServiceMock.getConfiguration().getString(WORLDPAY_CONFIG_VERSION)).thenReturn(VERSION);
    }

    @Test(expected = WorldpayModelTransformationException.class)
    public void testTransformSecondThreeDSecurePaymentRequestToPaymentServiceNullMerchantInfo() throws WorldpayModelTransformationException {
        when(secondThreeDSecurePaymentRequestMock.getSessionId()).thenReturn(SESSION_ID);
        when(secondThreeDSecurePaymentRequestMock.getOrderCode()).thenReturn(ORDER_CODE);
        testObj.transform(secondThreeDSecurePaymentRequestMock);
    }

    @Test(expected = WorldpayModelTransformationException.class)
    public void testTransformSecondThreeDSecurePaymentRequestToPaymentServiceNullMerchantInfoCode() throws WorldpayModelTransformationException {
        when(secondThreeDSecurePaymentRequestMock.getMerchantInfo()).thenReturn(merchantInfoMock);
        when(secondThreeDSecurePaymentRequestMock.getSessionId()).thenReturn(SESSION_ID);
        when(secondThreeDSecurePaymentRequestMock.getOrderCode()).thenReturn(ORDER_CODE);
        testObj.transform(secondThreeDSecurePaymentRequestMock);
    }

    @Test(expected = WorldpayModelTransformationException.class)
    public void testTransformSecondThreeDSecurePaymentRequestToPaymentServiceNullOrderCode() throws WorldpayModelTransformationException {
        when(secondThreeDSecurePaymentRequestMock.getMerchantInfo()).thenReturn(merchantInfoMock);
        when(merchantInfoMock.getMerchantCode()).thenReturn(MERCHANT_INFO_CODE);
        when(secondThreeDSecurePaymentRequestMock.getSessionId()).thenReturn(SESSION_ID);
        testObj.transform(secondThreeDSecurePaymentRequestMock);
    }

    @Test(expected = WorldpayModelTransformationException.class)
    public void testTransformSecondThreeDSecurePaymentRequestToPaymentServiceWithBlankSessionId() throws WorldpayModelTransformationException {
        when(secondThreeDSecurePaymentRequestMock.getMerchantInfo()).thenReturn(merchantInfoMock);
        when(merchantInfoMock.getMerchantCode()).thenReturn(MERCHANT_INFO_CODE);
        when(secondThreeDSecurePaymentRequestMock.getOrderCode()).thenReturn(ORDER_CODE);
        testObj.transform(secondThreeDSecurePaymentRequestMock);
    }

    @Test
    public void testTransformSecondThreeDSecurePaymentRequestToPaymentService() throws WorldpayModelTransformationException {
        final MerchantInfo merchantInfo = new MerchantInfo("MERCHANT_CODE", "MERCHANT_PASSWORD");
        when(secondThreeDSecurePaymentRequestMock.getMerchantInfo()).thenReturn(merchantInfo);
        when(merchantInfoMock.getMerchantCode()).thenReturn(MERCHANT_INFO_CODE);
        when(secondThreeDSecurePaymentRequestMock.getOrderCode()).thenReturn(ORDER_CODE);
        when(secondThreeDSecurePaymentRequestMock.getSessionId()).thenReturn(SESSION_ID);

        final PaymentService result = testObj.transform(secondThreeDSecurePaymentRequestMock);

        final List<Object> submitList = result.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify();
        final Submit submit = (Submit) submitList.get(0);
        final List<Object> orderList = submit.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreateOrChallenge();
        final com.worldpay.internal.model.Order intOrder = (com.worldpay.internal.model.Order) orderList.get(0);
        final String orderCode = intOrder.getOrderCode();
        assertEquals("Incorrect orderCode", ORDER_CODE, orderCode);
        assertEquals(merchantInfo.getMerchantCode(), result.getMerchantCode());
        assertEquals(VERSION, result.getVersion());
        final List<Object> orderElements = intOrder.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrDeviceSessionOrInfo3DSecureOrSession();
        final Session session = (Session) orderElements.get(1);
        assertEquals(SESSION_ID, session.getId());
    }

}
