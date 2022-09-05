package com.worldpay.service.request.validation.impl;

import com.worldpay.data.*;
import com.worldpay.data.Address;
import com.worldpay.data.Amount;
import com.worldpay.data.Order;
import com.worldpay.data.Shopper;
import com.worldpay.data.token.TokenRequest;
import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.exception.WorldpayValidationException;
import com.worldpay.internal.model.*;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.request.AuthoriseRequestParameters;
import com.worldpay.service.request.AuthoriseServiceRequest;
import com.worldpay.service.request.RedirectAuthoriseServiceRequest;
import com.worldpay.service.request.transform.AuthoriseRequestTransformer;
import com.worldpay.service.request.validation.WorldpayXMLValidator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static com.worldpay.service.model.payment.PaymentType.ONLINE;
import static java.util.Collections.singletonList;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayXMLValidatorTest {
    private static final String WORLDPAY_CONFIG_VERSION = "worldpay.config.version";

    private static final String STATEMENT_NARRATIVE_TEXT = "STATEMENT NARRATIVE TEXT";
    private static final String EMAIL_ADDRESS = "jshopper@myprovider.com";
    private static final String TOKEN_REFERENCE = "tokenReference";
    private static final String TOKEN_REASON = "tokenReason";
    private static final String AUTH_SHOPPER_ID = "authShopperId";
    private static final String ORDER_CODE = "DS1347889928107_3";
    private static final String MERCHANT_CODE = "MERCHANT_CODE";
    private static final String MERCHANT_PASSWORD = "MERCHANT_PASSWORD";
    private static final String YOUR_ORDER_ORDER_DESC = "Your Order & Order desc";
    private static final String VALUE = "100";
    private static final String EUR = "EUR";
    private static final String EXPONENT = "2";
    private static final String CITY = "city";
    private static final String GB = "GB";
    private static final String NAME = "John";
    private static final String SHOPPER = "Shopper";
    private static final String SHOPPER_ADDRESS_1 = "Shopper Address1";
    private static final String SHOPPER_ADDRESS_2 = "Shopper Address2";
    private static final String SHOPPER_ADDRESS_3 = "Shopper Address3";
    private static final String POSTAL_CODE = "Postal code";
    private static final String VERSION = "1.4";
    private static final String ORDER_CONTENT = "orderContent";

    private static final WorldpayXMLValidator VALIDATOR = new DefaultWorldpayXMLValidator();
    public static final String CODE = "code";

    private MerchantInfo merchantInfo;
    private BasicOrderInfo basicOrderInfo;
    private Address shippingAddress;
    private Address billingAddress;

    @InjectMocks
    private AuthoriseRequestTransformer testObj;

    @Mock(answer = RETURNS_DEEP_STUBS)
    private ConfigurationService configurationServiceMock;
    @Mock
    private Converter<Order, com.worldpay.internal.model.Order> internalOrderConverterMock;


    @Before
    public void setUp() {
        final MerchantInfo merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantCode(MERCHANT_CODE);
        merchantInfo.setMerchantPassword(MERCHANT_PASSWORD);
        this.merchantInfo = merchantInfo;

        final Amount amount = new Amount();
        amount.setValue(VALUE);
        amount.setCurrencyCode(EUR);
        amount.setExponent(EXPONENT);

        final BasicOrderInfo basicOrderInfo = new BasicOrderInfo();
        basicOrderInfo.setAmount(amount);
        basicOrderInfo.setOrderCode(ORDER_CODE);
        basicOrderInfo.setDescription(YOUR_ORDER_ORDER_DESC);
        this.basicOrderInfo = basicOrderInfo;

        final Address address = new Address();
        address.setFirstName(NAME);
        address.setLastName(SHOPPER);
        address.setAddress1(SHOPPER_ADDRESS_1);
        address.setAddress2(SHOPPER_ADDRESS_2);
        address.setAddress3(SHOPPER_ADDRESS_3);
        address.setPostalCode(POSTAL_CODE);
        address.setCity(CITY);
        address.setCountryCode(GB);
        shippingAddress = address;
        billingAddress = address;

        when(configurationServiceMock.getConfiguration().getString(WORLDPAY_CONFIG_VERSION)).thenReturn(VERSION);
    }

    @Test
    public void testValidate() throws WorldpayValidationException, WorldpayModelTransformationException {
        final List<PaymentType> includedPTs = singletonList(ONLINE);
        final Shopper shopper = new Shopper();
        shopper.setShopperEmailAddress(EMAIL_ADDRESS);
        shopper.setAuthenticatedShopperID(AUTH_SHOPPER_ID);
        final AuthoriseRequestParameters authoriseRequestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(basicOrderInfo)
            .withOrderContent(ORDER_CONTENT)
            .withIncludedPTs(includedPTs)
            .withShopper(shopper)
            .withShippingAddress(shippingAddress)
            .withBillingAddress(billingAddress)
            .withStatementNarrative(STATEMENT_NARRATIVE_TEXT)
            .build();
        final AuthoriseServiceRequest request = RedirectAuthoriseServiceRequest.createRedirectAuthoriseRequest(authoriseRequestParameters);

        when(internalOrderConverterMock.convert(request.getOrder())).thenReturn(createOrderWithMandatoryFields());

        final PaymentService paymentService = testObj.transform(request);

        VALIDATOR.validate(paymentService);
    }

    @Test
    public void testValidateXMLWithCreateTokenWithShopperScope() throws WorldpayValidationException, WorldpayModelTransformationException {
        final List<PaymentType> includedPTs = singletonList(ONLINE);
        final Shopper shopper = new Shopper();
        shopper.setShopperEmailAddress(EMAIL_ADDRESS);
        shopper.setAuthenticatedShopperID(AUTH_SHOPPER_ID);
        final TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setTokenEventReference(TOKEN_REFERENCE);
        tokenRequest.setTokenReason(TOKEN_REASON);
        tokenRequest.setMerchantToken(false);
        final AuthoriseRequestParameters authoriseRequestParameters = getAuthoriseRequestParameters(includedPTs, shopper, tokenRequest);
        final AuthoriseServiceRequest request = RedirectAuthoriseServiceRequest.createRedirectAuthoriseRequest(authoriseRequestParameters);

        when(internalOrderConverterMock.convert(request.getOrder())).thenReturn(createOrderWithMandatoryFields());

        final PaymentService paymentService = testObj.transform(request);

        VALIDATOR.validate(paymentService);
    }

    @Test
    public void testValidateXMLWithCreateTokenWithMerchantScope() throws WorldpayValidationException, WorldpayModelTransformationException {
        final List<PaymentType> includedPTs = singletonList(ONLINE);
        final Shopper shopper = new Shopper();
        shopper.setShopperEmailAddress(EMAIL_ADDRESS);
        shopper.setAuthenticatedShopperID(AUTH_SHOPPER_ID);
        final TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setTokenEventReference(TOKEN_REFERENCE);
        tokenRequest.setTokenReason(TOKEN_REASON);
        tokenRequest.setMerchantToken(true);
        final AuthoriseRequestParameters authoriseRequestParameters = getAuthoriseRequestParameters(includedPTs, shopper, tokenRequest);
        final AuthoriseServiceRequest request = RedirectAuthoriseServiceRequest.createRedirectAuthoriseRequest(authoriseRequestParameters);

        when(internalOrderConverterMock.convert(request.getOrder())).thenReturn(createOrderWithMandatoryFields());

        final PaymentService paymentService = testObj.transform(request);

        VALIDATOR.validate(paymentService);
    }

    protected AuthoriseRequestParameters getAuthoriseRequestParameters(final List<PaymentType> includedPTs, final Shopper shopper, final TokenRequest tokenRequest) {
        return AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(basicOrderInfo)
            .withOrderContent(ORDER_CONTENT)
            .withIncludedPTs(includedPTs)
            .withShopper(shopper)
            .withShippingAddress(shippingAddress)
            .withBillingAddress(billingAddress)
            .withTokenRequest(tokenRequest)
            .withStatementNarrative(STATEMENT_NARRATIVE_TEXT)
            .build();
    }

    private com.worldpay.internal.model.Order createOrderWithMandatoryFields() {
        final com.worldpay.internal.model.Order intOrder = new com.worldpay.internal.model.Order();

        intOrder.setOrderCode(ORDER_CODE);
        intOrder.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession().add(new Description());
        intOrder.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession().add(createAmount());
        intOrder.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession().add(createRisk());
        intOrder.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession().add(createOrderContent());
        intOrder.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession().add(createOrderChannel());
        intOrder.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession().add(createCheckoutId());
        intOrder.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession().add(createPaymentMethodMask());

        return intOrder;
    }

    private com.worldpay.internal.model.Amount createAmount() {
        final com.worldpay.internal.model.Amount amount = new com.worldpay.internal.model.Amount();
        amount.setCurrencyCode(EUR);
        amount.setValue(VALUE);
        amount.setExponent(EXPONENT);

        return amount;
    }

    private com.worldpay.internal.model.Risk createRisk() {
        final com.worldpay.internal.model.Risk risk = new com.worldpay.internal.model.Risk();
        risk.setAvsLevel(EUR);
        risk.setCvcLevel(VALUE);
        risk.setMultiply(EXPONENT);

        return risk;
    }

    private com.worldpay.internal.model.OrderContent createOrderContent() {
        final com.worldpay.internal.model.OrderContent orderContent = new com.worldpay.internal.model.OrderContent();
        orderContent.setvalue(VALUE);

        return orderContent;
    }

    private com.worldpay.internal.model.OrderChannel createOrderChannel() {
        final com.worldpay.internal.model.OrderChannel orderChannel = new com.worldpay.internal.model.OrderChannel();
        orderChannel.setValue("WEB");

        return orderChannel;
    }

    private com.worldpay.internal.model.CheckoutId createCheckoutId() {
        final com.worldpay.internal.model.CheckoutId checkoutId = new com.worldpay.internal.model.CheckoutId();
        checkoutId.setvalue(VALUE);

        return checkoutId;
    }

    private com.worldpay.internal.model.PaymentMethodMask createPaymentMethodMask() {
        final com.worldpay.internal.model.PaymentMethodMask paymentMethodMask = new com.worldpay.internal.model.PaymentMethodMask();
        final com.worldpay.internal.model.Include include = new Include();

        include.setCode(CODE);
        paymentMethodMask.getStoredCredentialsOrIncludeOrExclude().add(include);

        return paymentMethodMask;
    }

}
