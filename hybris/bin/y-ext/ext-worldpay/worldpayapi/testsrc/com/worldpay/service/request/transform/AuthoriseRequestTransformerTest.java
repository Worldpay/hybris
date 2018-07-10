package com.worldpay.service.request.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.CreateToken;
import com.worldpay.internal.model.Description;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.Submit;
import com.worldpay.service.model.*;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.request.AuthoriseServiceRequest;
import com.worldpay.service.request.RedirectAuthoriseServiceRequest;
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
public class AuthoriseRequestTransformerTest {

    private static final String WORLDPAY_CONFIG_VERSION = "worldpay.config.version";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Shopper";
    private static final String SHOPPER_ADDRESS_1 = "Shopper Address1";
    private static final String SHOPPER_ADDRESS_2 = "Shopper Address2";
    private static final String SHOPPER_ADDRESS_3 = "Shopper Address3";
    private static final String POSTAL_CODE = "postalCode";
    private static final String CITY = "city";
    private static final String GB = "GB";
    private static final String ORDER_CONTENT = "orderContent";
    private static final String SHOPPER_EMAIL_ADDRESS = "jshopper@myprovider.com";
    private static final String TOKEN_REASON = "tokenReason";
    private static final String TOKEN_REFERENCE = "tokenReference";
    private static final String STATEMENT_NARRATIVE_TEXT = "STATEMENT NARRATIVE TEXT";
    private static final String AUTH_SHOPPER_ID = "authShopperID";

    @InjectMocks
    private AuthoriseRequestTransformer testObj;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ConfigurationService configurationServiceMock;

    @Before
    public void setUp() throws Exception {
        when(configurationServiceMock.getConfiguration().getString(WORLDPAY_CONFIG_VERSION)).thenReturn("1.4");
    }

    /**
     * Test method for {@link com.worldpay.service.request.transform.AuthoriseRequestTransformer#transform(com.worldpay.service.request.ServiceRequest)}.
     */
    @Test
    public void testTransformAuthoriseRequestToPaymentService() throws WorldpayModelTransformationException {
        final MerchantInfo merchantInfo = new MerchantInfo("MERCHANT_CODE", "MERCHANT_PASSWORD");
        final BasicOrderInfo orderInfo = new BasicOrderInfo("DS1347889928107_3", "Your Order & Order desc", new Amount("100", "EUR", "2"));
        final Address shippingAddress = new Address(FIRST_NAME, LAST_NAME, SHOPPER_ADDRESS_1, SHOPPER_ADDRESS_2, SHOPPER_ADDRESS_3, POSTAL_CODE, CITY, GB);
        final Address billingAddress = new Address(FIRST_NAME, LAST_NAME, SHOPPER_ADDRESS_1, SHOPPER_ADDRESS_2, SHOPPER_ADDRESS_3, POSTAL_CODE, CITY, GB);
        final List<PaymentType> includedPTs = singletonList(ONLINE);
        final Shopper shopper = new Shopper(SHOPPER_EMAIL_ADDRESS, AUTH_SHOPPER_ID, null, null);
        final AuthoriseServiceRequest request = RedirectAuthoriseServiceRequest.createRedirectAuthoriseRequest(merchantInfo, orderInfo, ORDER_CONTENT, null,
                includedPTs, null, shopper, shippingAddress, billingAddress, STATEMENT_NARRATIVE_TEXT);

        final PaymentService result = testObj.transform(request);

        final List<Object> submitList = result.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify();
        final Submit submit = (Submit) submitList.get(0);
        final List<Object> orderList = submit.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreate();
        com.worldpay.internal.model.Order intOrder = (com.worldpay.internal.model.Order) orderList.get(0);

        final List<Object> orderElements = intOrder.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrInfo3DSecureOrSession();
        final String description = ((Description) orderElements.stream().filter(Description.class::isInstance).findFirst().get()).getvalue();
        assertEquals("Incorrect description", "Your Order & Order desc", description);

        final String orderCode = intOrder.getOrderCode();
        assertFalse(orderElements.stream().anyMatch(CreateToken.class::isInstance));
        assertEquals("Incorrect orderCode", "DS1347889928107_3", orderCode);
        assertEquals(merchantInfo.getMerchantCode(), result.getMerchantCode());
    }

    /**
     * Test method for {@link com.worldpay.service.request.transform.AuthoriseRequestTransformer#transform(com.worldpay.service.request.ServiceRequest)}.
     */
    @Test
    public void testTransformAuthoriseRequestToPaymentServiceWithTokenRequest() throws WorldpayModelTransformationException {
        final MerchantInfo merchantInfo = new MerchantInfo("MERCHANT_CODE", "MERCHANT_PASSWORD");
        final BasicOrderInfo orderInfo = new BasicOrderInfo("DS1347889928107_3", "Your Order & Order desc", new Amount("100", "EUR", "2"));
        final Address shippingAddress = new Address(FIRST_NAME, LAST_NAME, SHOPPER_ADDRESS_1, SHOPPER_ADDRESS_2, SHOPPER_ADDRESS_3, POSTAL_CODE, CITY, GB);
        final Address billingAddress = new Address(FIRST_NAME, LAST_NAME, SHOPPER_ADDRESS_1, SHOPPER_ADDRESS_2, SHOPPER_ADDRESS_3, POSTAL_CODE, CITY, GB);
        final List<PaymentType> includedPTs = singletonList(ONLINE);
        final TokenRequest tokenRequest = new TokenRequest(TOKEN_REFERENCE, TOKEN_REASON);
        final Shopper shopper = new Shopper(SHOPPER_EMAIL_ADDRESS, AUTH_SHOPPER_ID, null, null);
        final AuthoriseServiceRequest request = RedirectAuthoriseServiceRequest.createTokenAndRedirectAuthoriseRequest(merchantInfo, orderInfo, ORDER_CONTENT, null,
                includedPTs, null, shopper, shippingAddress, billingAddress, STATEMENT_NARRATIVE_TEXT, tokenRequest);

        final PaymentService result = testObj.transform(request);

        final List<Object> submitList = result.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify();
        final Submit submit = (Submit) submitList.get(0);
        final List<Object> orderList = submit.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreate();
        com.worldpay.internal.model.Order intOrder = (com.worldpay.internal.model.Order) orderList.get(0);

        final List<Object> orderElements = intOrder.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrInfo3DSecureOrSession();
        final String description = ((Description) orderElements.stream().filter(Description.class::isInstance).findFirst().get()).getvalue();
        assertEquals("Incorrect description", "Your Order & Order desc", description);

        final String orderCode = intOrder.getOrderCode();
        assertTrue(orderElements.stream().anyMatch(CreateToken.class::isInstance));

        final CreateToken createToken = ((CreateToken) orderElements.stream().filter(CreateToken.class::isInstance).findFirst().get());
        assertEquals(TOKEN_REFERENCE, createToken.getTokenEventReference());
        assertEquals(TOKEN_REASON, createToken.getTokenReason().getvalue());

        final com.worldpay.internal.model.Shopper intShopper = ((com.worldpay.internal.model.Shopper) orderElements.stream().filter(com.worldpay.internal.model.Shopper.class::isInstance).findFirst().get());
        assertEquals(AUTH_SHOPPER_ID, intShopper.getAuthenticatedShopperID());

        assertEquals("Incorrect orderCode", "DS1347889928107_3", orderCode);
        assertEquals(merchantInfo.getMerchantCode(), result.getMerchantCode());
    }
}
