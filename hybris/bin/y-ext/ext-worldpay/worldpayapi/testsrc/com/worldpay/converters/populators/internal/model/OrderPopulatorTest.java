package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.*;
import com.worldpay.data.payment.PayAsOrder;
import com.worldpay.data.threeds2.Additional3DSData;
import com.worldpay.data.threeds2.RiskData;
import com.worldpay.data.token.TokenRequest;
import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.internal.model.BillingAddress;
import com.worldpay.internal.model.CreateToken;
import com.worldpay.internal.model.ExtendedOrderDetail;
import com.worldpay.internal.model.Mandate;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.logging.log4j.util.Strings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OrderPopulatorTest {

    private static final String ORDER_CODE = "orderCode";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String INSTALLATION_ID = "installationId";
    private static final String ORDER_CONTENT = "orderContent";
    private static final String STATEMENT_NARRATIVE = "statementNarrative";
    private static final String ECHO_DATA = "echoData";
    private static final String PA_RESPONSE = "paResponse";
    private static final String DEVICE_SESSION = "deviceSession";
    private static final String MANDATE_TYPE = "mandateType";
    public static final String ORDER_CHANNEL = "orderChannel";
    public static final String CHECKOUT_ID = "checkoutId";

    @InjectMocks
    private OrderPopulator testObj;

    @Mock
    private Converter<BranchSpecificExtension, com.worldpay.internal.model.BranchSpecificExtension> internalBranchSpecificExtensionConverterMock;
    @Mock
    private Converter<TokenRequest, CreateToken> internalTokenRequestConverterMock;
    @Mock
    private Converter<OrderLines, com.worldpay.internal.model.OrderLines> internalOrderLinesConverterMock;
    @Mock
    private Converter<FraudSightData, com.worldpay.internal.model.FraudSightData> internalFraudSightDataConverterMock;
    @Mock
    private PaymentOrderConvertersWrapper paymentOrderConvertersWrapperMock;
    @Mock
    private ThreeDS2OrderConvertersWrapper threeDS2OrderConvertersWrapperMock;
    @Mock
    private BasicOrderConvertersWrapper basicOrderConvertersWrapperMock;
    @Mock
    private RiskEvaluatorConvertersWrapper riskEvaluatorConvertersWrapperMock;
    @Mock
    private Converter<GuaranteedPaymentsData, com.worldpay.internal.model.GuaranteedPaymentsData> internalGuaranteedPaymentsDataConverterMock;

    @Mock
    private Converter<Amount, com.worldpay.internal.model.Amount> internalAmountConverterMock;
    @Mock
    private Converter<Shopper, com.worldpay.internal.model.Shopper> internalShopperConverterMock;
    @Mock
    private Converter<Address, com.worldpay.internal.model.Address> internalAddressConverterMock;
    @Mock
    private Converter<PaymentMethodAttribute, com.worldpay.internal.model.PaymentMethodAttribute> internalPaymentMethodAttributeConverterMock;
    @Mock
    private Converter<Session, com.worldpay.internal.model.Session> internalSessionConverterMock;
    @Mock
    private Converter<RiskData, com.worldpay.internal.model.RiskData> internalRiskDataConverterMock;
    @Mock
    private Converter<Additional3DSData, com.worldpay.internal.model.Additional3DSData> internalAdditional3DSDataConverter;
    @Mock
    private Converter<PaymentMethodMask, com.worldpay.internal.model.PaymentMethodMask> internalPaymentMethodMaskConverterMock;
    @Mock
    private Converter<PaymentDetails, com.worldpay.internal.model.PaymentDetails> internalPaymentDetailsConverterMock;
    @Mock
    private Converter<PayAsOrder, com.worldpay.internal.model.PayAsOrder> internalPayAsOrderConverterMock;
    @Mock
    private Converter<AlternativeShippingAddress, com.worldpay.internal.model.AlternativeShippingAddress> internalAlternativeShippingAddressConverterMock;

    @Mock
    private Order sourceMock;
    @Mock
    private PaymentMethodAttribute paymentMethodAttribute1Mock, paymentMethodAttribute2Mock;
    @Mock
    private com.worldpay.internal.model.PaymentMethodAttribute intPaymentMethodAttribute1Mock, intPaymentMethodAttribute2Mock;
    @Mock
    private Amount amountMock;
    @Mock
    private com.worldpay.internal.model.Amount intAmountMock;
    @Mock
    private PaymentMethodMask paymentMethodMaskMock;
    @Mock
    private com.worldpay.internal.model.PaymentMethodMask intPaymentMethodMaskMock;
    @Mock
    private PaymentDetails paymentDetailsMock;
    @Mock
    private com.worldpay.internal.model.PaymentDetails intPaymentDetailsMock;
    @Mock
    private PayAsOrder payAsOrderMock;
    @Mock
    private com.worldpay.internal.model.PayAsOrder intPayAsOrderMock;
    @Mock
    private Shopper shopperMock;
    @Mock
    private com.worldpay.internal.model.Shopper intShopperMock;
    @Mock
    private Address addressMock;
    @Mock
    private com.worldpay.internal.model.Address intAddressMock;
    @Mock
    private BranchSpecificExtension branchSpecificExtensionMock;
    @Mock
    private com.worldpay.internal.model.BranchSpecificExtension intBranchSpecificExtensionMock;
    @Mock
    private TokenRequest tokenRequestMock;
    @Mock
    private com.worldpay.internal.model.CreateToken createTokenMock;
    @Mock
    private Session sessionMock;
    @Mock
    private com.worldpay.internal.model.Session intSessionMock;
    @Mock
    private OrderLines orderLinesMock;
    @Mock
    private com.worldpay.internal.model.OrderLines intOrderLinesMock;
    @Mock
    private RiskData riskDataMock;
    @Mock
    private com.worldpay.internal.model.RiskData intRiskDataMock;
    @Mock
    private Additional3DSData additional3DSDataMock;
    @Mock
    private com.worldpay.internal.model.Additional3DSData intAdditional3DSDataMock;
    @Mock
    private FraudSightData fraudSightDataMock;
    @Mock
    private com.worldpay.internal.model.FraudSightData intFraudSightDataMock;
    @Mock
    private GuaranteedPaymentsData guaranteedPaymentsDataMock;
    @Mock
    private com.worldpay.internal.model.GuaranteedPaymentsData intGuaranteedPaymentMock;
    @Mock
    private AlternativeShippingAddress alternativeShippingAddressMock;
    @Mock
    private com.worldpay.internal.model.AlternativeShippingAddress intAlternativeShippingAddressMock;

    @Before
    public void setUp() {
        paymentOrderConvertersWrapperMock = new PaymentOrderConvertersWrapper(internalPaymentMethodMaskConverterMock, internalPaymentDetailsConverterMock, internalPayAsOrderConverterMock, internalPaymentMethodAttributeConverterMock);
        threeDS2OrderConvertersWrapperMock = new ThreeDS2OrderConvertersWrapper(internalRiskDataConverterMock, internalAdditional3DSDataConverter);
        basicOrderConvertersWrapperMock = new BasicOrderConvertersWrapper(internalAmountConverterMock, internalShopperConverterMock, internalAddressConverterMock, internalSessionConverterMock, internalAlternativeShippingAddressConverterMock);
        riskEvaluatorConvertersWrapperMock = new RiskEvaluatorConvertersWrapper(internalFraudSightDataConverterMock, internalGuaranteedPaymentsDataConverterMock);

        testObj = new OrderPopulator(internalBranchSpecificExtensionConverterMock, internalTokenRequestConverterMock, internalOrderLinesConverterMock,
            paymentOrderConvertersWrapperMock, threeDS2OrderConvertersWrapperMock, basicOrderConvertersWrapperMock, riskEvaluatorConvertersWrapperMock);

    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.Order());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_WhenGetOrderCodeIsNull_ShouldNotPopulateOrderCode() {
        when(sourceMock.getOrderCode()).thenReturn(null);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getOrderCode()).isNull();
    }

    @Test
    public void populate_WhenGetDescriptionIsNull_ShouldNotPopulateDescription() {
        when(sourceMock.getDescription()).thenReturn(null);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession()).isEmpty();
    }

    @Test
    public void populate_WhenGetAmountIsNull_ShouldNotPopulateAmount() {
        when(sourceMock.getAmount()).thenReturn(null);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession()).isEmpty();
    }

    @Test
    public void populate_WhenGetOrderChannelIsNull_ShouldNotPopulateOrderChannel() {
        when(sourceMock.getOrderChannel()).thenReturn(null);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession()).isEmpty();
    }

    @Test
    public void populate_WhenGetCheckoutIdIsNull_ShouldNotPopulateCheckoutId() {
        when(sourceMock.getCheckoutId()).thenReturn(null);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession()).isEmpty();
    }

    @Test
    public void populate_WhenGetInstallationIdIsNull_ShouldNotPopulateInstallationId() {
        when(sourceMock.getInstallationId()).thenReturn(null);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getInstallationId()).isNull();
    }

    @Test
    public void populate_WhenGetOrderContentIsNull_ShouldNotPopulateOrderContent() {
        when(sourceMock.getOrderContent()).thenReturn(null);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession()).isEmpty();
    }

    @Test
    public void populate_WhenGetPaymentMethodMaskIsNull_ShouldNotPopulatePaymentMethodMask() {
        when(sourceMock.getPaymentMethodMask()).thenReturn(null);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession()).isEmpty();
    }

    @Test
    public void populate_WhenGetPaymentDetailsIsNull_ShouldNotPopulatePaymentDetails() {
        when(sourceMock.getPaymentDetails()).thenReturn(null);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession()).isEmpty();
    }

    @Test
    public void populate_WhenGetPayAsOrderIsNull_ShouldNotPopulatePayAsOrder() {
        when(sourceMock.getPayAsOrder()).thenReturn(null);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession()).isEmpty();
    }

    @Test
    public void populate_WhenAllPaymentRequestAreNotNull_ShouldPopulatePaymentMethodMask() {
        when(sourceMock.getPaymentMethodMask()).thenReturn(paymentMethodMaskMock);
        when(internalPaymentMethodMaskConverterMock.convert(paymentMethodMaskMock)).thenReturn(intPaymentMethodMaskMock);
        when(sourceMock.getPaymentDetails()).thenReturn(paymentDetailsMock);
        when(internalPaymentDetailsConverterMock.convert(paymentDetailsMock)).thenReturn(intPaymentDetailsMock);
        when(sourceMock.getPayAsOrder()).thenReturn(payAsOrderMock);
        when(internalPayAsOrderConverterMock.convert(payAsOrderMock)).thenReturn(intPayAsOrderMock);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession()
            .get(0)).isEqualTo(intPaymentMethodMaskMock);
    }

    @Test
    public void populate_WhenPaymentDetailsAndPayAsOrderAreNotNull_ShouldPopulatePaymentDetails() {
        when(sourceMock.getPaymentDetails()).thenReturn(paymentDetailsMock);
        when(internalPaymentDetailsConverterMock.convert(paymentDetailsMock)).thenReturn(intPaymentDetailsMock);
        when(sourceMock.getPayAsOrder()).thenReturn(payAsOrderMock);
        when(internalPayAsOrderConverterMock.convert(payAsOrderMock)).thenReturn(intPayAsOrderMock);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession()
            .get(0)).isEqualTo(intPaymentDetailsMock);
    }

    @Test
    public void populate_WhenGetShopperIsNull_ShouldNotPopulateShopper() {
        when(sourceMock.getShopper()).thenReturn(null);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession()).isEmpty();
    }

    @Test
    public void populate_WhenGetShippingAddressIsNull_ShouldNotPopulateShippingAddress() {
        when(sourceMock.getShippingAddress()).thenReturn(null);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession()).isEmpty();
    }

    @Test
    public void populate_WhenGetBillingAddressIsNull_ShouldNotPopulateBillingAddress() {
        when(sourceMock.getBillingAddress()).thenReturn(null);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession()).isEmpty();
    }

    @Test
    public void populate_WhenGetMandateTypeIsNull_ShouldNotPopulateMandate() {
        when(sourceMock.getMandateType()).thenReturn(null);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession()).isEmpty();
    }

    @Test
    public void populate_WhenGetBranchSpecificExtensionIsNull_ShouldNotPopulateBranchSpecificExtension() {
        when(sourceMock.getBranchSpecificExtension()).thenReturn(null);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession()).isEmpty();
    }

    @Test
    public void populate_WhenGetPaymentMethodAttributesIsNull_ShouldNotPopulatePaymentMethodAttributes() {
        when(sourceMock.getPaymentMethodAttributes()).thenReturn(null);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession()).isEmpty();
    }

    @Test
    public void populate_WhenGetPaymentMethodAttributesIsEmpty_ShouldNotPopulatePaymentMethodAttributes() {
        when(sourceMock.getPaymentMethodAttributes()).thenReturn(Collections.emptyList());

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession()).isEmpty();
    }

    @Test
    public void populate_WhenGetPaymentMethodAttributesHasOneItem_ShouldPopulateOneItem() {
        final List<PaymentMethodAttribute> paymentMethodAttributes = List.of(paymentMethodAttribute1Mock);
        when(sourceMock.getPaymentMethodAttributes()).thenReturn(paymentMethodAttributes);
        when(internalPaymentMethodAttributeConverterMock.convert(paymentMethodAttribute1Mock)).thenReturn(intPaymentMethodAttribute1Mock);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession().size()).isEqualTo(paymentMethodAttributes.size());
    }

    @Test
    public void populate_WhenGetPaymentMethodAttributesHasMoreThanOneItem_ShouldPopulateAllTheItems() {
        final List<PaymentMethodAttribute> paymentMethodAttributes = List.of(paymentMethodAttribute1Mock, paymentMethodAttribute2Mock);
        when(sourceMock.getPaymentMethodAttributes()).thenReturn(paymentMethodAttributes);
        when(internalPaymentMethodAttributeConverterMock.convert(paymentMethodAttribute1Mock)).thenReturn(intPaymentMethodAttribute1Mock);
        when(internalPaymentMethodAttributeConverterMock.convert(paymentMethodAttribute2Mock)).thenReturn(intPaymentMethodAttribute2Mock);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession().size()).isEqualTo(paymentMethodAttributes.size());
    }

    @Test
    public void populate_WhenGetStatementNarrativeIsNull_ShouldNotPopulateStatementNarrative() {
        when(sourceMock.getStatementNarrative()).thenReturn(null);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession().size()).isZero();
    }

    @Test
    public void populate_WhenGetEchoDataIsNull_ShouldNotPopulateEchoData() {
        when(sourceMock.getEchoData()).thenReturn(null);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession().size()).isZero();
    }

    @Test
    public void populate_WhenGetTokenRequestIsNull_ShouldNotPopulateTokenRequest() {
        when(sourceMock.getTokenRequest()).thenReturn(null);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession().size()).isZero();
    }

    @Test
    public void populate_WhenGetPaResponseIsNull_ShouldNotPopulatePaResponse() {
        when(sourceMock.getPaResponse()).thenReturn(null);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession().size()).isZero();
    }

    @Test
    public void populate_WhenGetSessionIsNull_ShouldNotPopulateSession() {
        when(sourceMock.getSession()).thenReturn(null);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession().size()).isZero();
    }

    @Test
    public void populate_WhenGetOrderLinesIsNull_ShouldNotPopulateOrderLines() {
        when(sourceMock.getOrderLines()).thenReturn(null);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession().size()).isZero();
    }

    @Test
    public void populate_WhenGetDynamicInteractionTypeIsNull_ShouldNotPopulateDynamicInteractionType() {
        when(sourceMock.getDynamicInteractionType()).thenReturn(null);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession().size()).isZero();
    }

    @Test
    public void populate_WhenGetRiskDataIsNull_ShouldNotPopulateRiskData() {
        when(sourceMock.getRiskData()).thenReturn(null);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession().size()).isZero();
    }

    @Test
    public void populate_WhenGetAdditional3DSDataIsNull_ShouldNotPopulateAdditional3DSData() {
        when(sourceMock.getAdditional3DSData()).thenReturn(null);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession().size()).isZero();
    }

    @Test
    public void populate_WhenGetFraudSightDataIsNull_ShouldNotPopulateFraudSightData() {
        when(sourceMock.getFraudSightData()).thenReturn(null);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession().size()).isZero();
    }

    @Test
    public void populate_WhenGetDeviceSessionIsNull_ShouldNotPopulateDeviceSession() {
        when(sourceMock.getDeviceSession()).thenReturn(null);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession().size()).isZero();
    }

    @Test
    public void populate_WhenGetDeviceSessionIsBlank_ShouldNotPopulateDeviceSession() {
        when(sourceMock.getDeviceSession()).thenReturn(Strings.EMPTY);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession().size()).isZero();
    }

    @Test
    public void populate_WhenSourceAndTargetAreNotNullAndAllTheFieldsAreNotNull_ShouldPopulate() {
        when(sourceMock.getOrderCode()).thenReturn(ORDER_CODE);
        when(sourceMock.getDescription()).thenReturn(DESCRIPTION);
        when(sourceMock.getAmount()).thenReturn(amountMock);
        when(internalAmountConverterMock.convert(amountMock)).thenReturn(intAmountMock);
        when(sourceMock.getInstallationId()).thenReturn(INSTALLATION_ID);
        when(sourceMock.getOrderContent()).thenReturn(ORDER_CONTENT);
        when(sourceMock.getPayAsOrder()).thenReturn(payAsOrderMock);
        when(internalPayAsOrderConverterMock.convert(payAsOrderMock)).thenReturn(intPayAsOrderMock);
        when(sourceMock.getShopper()).thenReturn(shopperMock);
        when(internalShopperConverterMock.convert(shopperMock)).thenReturn(intShopperMock);
        when(sourceMock.getShippingAddress()).thenReturn(addressMock);
        when(sourceMock.getBillingAddress()).thenReturn(addressMock);
        when(internalAddressConverterMock.convert(addressMock)).thenReturn(intAddressMock);
        when(sourceMock.getMandateType()).thenReturn(MANDATE_TYPE);
        when(sourceMock.getBranchSpecificExtension()).thenReturn(branchSpecificExtensionMock);
        when(internalBranchSpecificExtensionConverterMock.convert(branchSpecificExtensionMock)).thenReturn(intBranchSpecificExtensionMock);
        final List<PaymentMethodAttribute> paymentMethodAttributes = List.of(paymentMethodAttribute1Mock, paymentMethodAttribute2Mock);
        when(sourceMock.getPaymentMethodAttributes()).thenReturn(paymentMethodAttributes);
        when(internalPaymentMethodAttributeConverterMock.convert(paymentMethodAttribute1Mock)).thenReturn(intPaymentMethodAttribute1Mock);
        when(internalPaymentMethodAttributeConverterMock.convert(paymentMethodAttribute2Mock)).thenReturn(intPaymentMethodAttribute2Mock);
        when(sourceMock.getStatementNarrative()).thenReturn(STATEMENT_NARRATIVE);
        when(sourceMock.getEchoData()).thenReturn(ECHO_DATA);
        when(sourceMock.getTokenRequest()).thenReturn(tokenRequestMock);
        when(internalTokenRequestConverterMock.convert(tokenRequestMock)).thenReturn(createTokenMock);
        when(sourceMock.getPaResponse()).thenReturn(PA_RESPONSE);
        when(sourceMock.getSession()).thenReturn(sessionMock);
        when(internalSessionConverterMock.convert(sessionMock)).thenReturn(intSessionMock);
        when(sourceMock.getOrderLines()).thenReturn(orderLinesMock);
        when(internalOrderLinesConverterMock.convert(orderLinesMock)).thenReturn(intOrderLinesMock);
        when(sourceMock.getDynamicInteractionType()).thenReturn(DynamicInteractionType.PRESENT);
        when(sourceMock.getRiskData()).thenReturn(riskDataMock);
        when(internalRiskDataConverterMock.convert(riskDataMock)).thenReturn(intRiskDataMock);
        when(sourceMock.getAdditional3DSData()).thenReturn(additional3DSDataMock);
        when(internalAdditional3DSDataConverter.convert(additional3DSDataMock)).thenReturn(intAdditional3DSDataMock);
        when(sourceMock.getFraudSightData()).thenReturn(fraudSightDataMock);
        when(internalFraudSightDataConverterMock.convert(fraudSightDataMock)).thenReturn(intFraudSightDataMock);
        when(sourceMock.getDeviceSession()).thenReturn(DEVICE_SESSION);
        when(sourceMock.getGuaranteedPaymentsData()).thenReturn(guaranteedPaymentsDataMock);
        when(internalGuaranteedPaymentsDataConverterMock.convert(guaranteedPaymentsDataMock)).thenReturn(intGuaranteedPaymentMock);
        when(sourceMock.getOrderChannel()).thenReturn(ORDER_CHANNEL);
        when(sourceMock.getCheckoutId()).thenReturn(CHECKOUT_ID);
        when(sourceMock.getAlternativeShippingAddress()).thenReturn(alternativeShippingAddressMock);
        when(internalAlternativeShippingAddressConverterMock.convert(alternativeShippingAddressMock)).thenReturn(intAlternativeShippingAddressMock);

        final com.worldpay.internal.model.Order target = new com.worldpay.internal.model.Order();
        testObj.populate(sourceMock, target);

        assertThat(target.getOrderCode()).isEqualTo(ORDER_CODE);
        assertThat(target.getInstallationId()).isEqualTo(INSTALLATION_ID);

        final List<Object> objectList = target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession();
        final BillingAddress intBillingAddress = objectList.stream()
            .filter(BillingAddress.class::isInstance)
            .map(BillingAddress.class::cast)
            .findFirst()
            .orElse(null);
        final ExtendedOrderDetail intExtendedOrderDetail = objectList.stream()
                .filter(ExtendedOrderDetail.class::isInstance)
                .map(ExtendedOrderDetail.class::cast)
                .findFirst()
                .orElse(null);
        final Mandate intMandate = objectList.stream()
            .filter(Mandate.class::isInstance)
            .map(Mandate.class::cast)
            .findFirst()
            .orElse(null);

        assertThat(objectList)
            .hasSize(26)
            .containsSequence(intBillingAddress, intBranchSpecificExtensionMock, intExtendedOrderDetail, intMandate);
    }
}
