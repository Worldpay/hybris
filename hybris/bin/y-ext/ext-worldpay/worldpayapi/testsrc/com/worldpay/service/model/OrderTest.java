package com.worldpay.service.model;

import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.Description;
import com.worldpay.internal.model.Info3DSecure;
import com.worldpay.internal.model.PaResponse;
import com.worldpay.service.model.payment.PayAsOrder;
import com.worldpay.service.model.threeds2.Additional3DSData;
import com.worldpay.service.model.threeds2.RiskData;
import com.worldpay.service.model.threeds2.TransactionRiskData;
import com.worldpay.service.model.threeds2.TransactionRiskDataGiftCardAmount;
import com.worldpay.service.model.token.TokenRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OrderTest {

    private Order order;

    @Mock
    private Shopper shopperMock;
    @Mock
    private Amount amountMock;
    @Mock
    private com.worldpay.internal.model.Shopper intShopperMock;
    @Mock
    private Address shippingAddressMock, billingAddressMock;
    @Mock
    private com.worldpay.internal.model.Address intShippingAddressMock, intBillingAddressMock;
    @Mock
    private PaymentMethodAttribute paymentMethodAttributeMock1, paymentMethodAttributeMock2;
    @Mock
    private com.worldpay.internal.model.PaymentMethodAttribute intPaymentMethodAttributeMock1, intPaymentMethodAttributeMock2;
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
    private Additional3DSData additional3DSDataMock;
    @Mock
    private RiskData riskDataMock;
    @Mock
    private TransactionRiskData transactionRiskDataMock;
    @Mock
    private TransactionRiskDataGiftCardAmount transactionRiskDataGiftCardAmountMock;
    @Mock
    private com.worldpay.internal.model.Amount intAmountMock;
    @Mock
    private com.worldpay.internal.model.Additional3DSData intAdditional3DSDataMock;

    @Before
    public void setUp() throws Exception {
        order = new Order("orderCode", "description", amountMock);

        when(amountMock.transformToInternalModel()).thenReturn(intAmountMock);
        when(shopperMock.transformToInternalModel()).thenReturn(intShopperMock);
        when(shippingAddressMock.transformToInternalModel()).thenReturn(intShippingAddressMock);
        when(billingAddressMock.transformToInternalModel()).thenReturn(intBillingAddressMock);
        when(paymentMethodAttributeMock1.transformToInternalModel()).thenReturn(intPaymentMethodAttributeMock1);
        when(paymentMethodAttributeMock2.transformToInternalModel()).thenReturn(intPaymentMethodAttributeMock2);
        when(tokenRequestMock.transformToInternalModel()).thenReturn(createTokenMock);
        when(sessionMock.transformToInternalModel()).thenReturn(intSessionMock);
        when(orderLinesMock.transformToInternalModel()).thenReturn(intOrderLinesMock);
        when(paymentMethodMaskMock.transformToInternalModel()).thenReturn(intPaymentMethodMaskMock);
        when(paymentDetailsMock.transformToInternalModel()).thenReturn(intPaymentDetailsMock);
        when(payAsOrderMock.transformToInternalModel()).thenReturn(intPayAsOrderMock);
        when(additional3DSDataMock.transformToInternalModel()).thenReturn(intAdditional3DSDataMock);
    }

    @Test
    public void transformToInternalModel_shouldSetOrderCodeDescriptionAndAmount() throws WorldpayModelTransformationException {
        var result = order.transformToInternalModel();

        assertThat(result.getOrderCode()).isEqualTo("orderCode");
        var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();

        final Description intDescription = orderChildElements.stream().filter(Description.class::isInstance).map(Description.class::cast).findAny().orElseThrow();
        assertThat(intDescription.getvalue()).isEqualTo("description");

        assertThat(orderChildElements.stream().filter(com.worldpay.internal.model.Amount.class::isInstance).map(com.worldpay.internal.model.Amount.class::cast).collect(Collectors.toList())).hasSize(1);
        final var intAmount = orderChildElements.stream().filter(com.worldpay.internal.model.Amount.class::isInstance).map(com.worldpay.internal.model.Amount.class::cast).findAny().orElseThrow();
        assertThat(intAmount).isEqualTo(intAmountMock);
    }

    @Test
    public void transformToInternalModel_shouldSetInstallationId() throws WorldpayModelTransformationException {
        order.setInstallationId("installationId");
        var result = order.transformToInternalModel();

        assertThat(result.getInstallationId()).isEqualToIgnoringCase("installationId");
    }

    @Test
    public void transformToInternalModel_shouldNotSetInstallationId_WhenEmpty() throws WorldpayModelTransformationException {
        order.setInstallationId(null);

        final var result = order.transformToInternalModel();

        assertThat(result.getInstallationId()).isNullOrEmpty();
    }

    @Test
    public void transformToInternalModel_shouldSetOrderContent() throws WorldpayModelTransformationException {
        order.setOrderContent("orderContent");

        final var result = order.transformToInternalModel();

        final var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        assertThat(orderChildElements.stream().filter(com.worldpay.internal.model.OrderContent.class::isInstance).map(com.worldpay.internal.model.OrderContent.class::cast).collect(Collectors.toList())).hasSize(1);
        final var intOrderContent = orderChildElements.stream().filter(com.worldpay.internal.model.OrderContent.class::isInstance).map(com.worldpay.internal.model.OrderContent.class::cast).findAny().orElseThrow();
        assertThat(intOrderContent.getvalue()).isEqualToIgnoringCase("orderContent");
    }

    @Test
    public void transformToInternalModel_shouldNotSetOrderContent_WhenEmpty() throws WorldpayModelTransformationException {
        order.setOrderContent(null);

        var result = order.transformToInternalModel();

        final var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        final boolean emptyOrderContent = orderChildElements.stream().noneMatch(com.worldpay.internal.model.OrderContent.class::isInstance);
        assertThat(emptyOrderContent).isTrue();
    }

    @Test
    public void transformToInternalModel_shouldSetShopper() throws WorldpayModelTransformationException {
        order.setShopper(shopperMock);

        var result = order.transformToInternalModel();

        final var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        assertThat(orderChildElements.stream().filter(com.worldpay.internal.model.Shopper.class::isInstance).map(com.worldpay.internal.model.Shopper.class::cast).collect(Collectors.toList())).hasSize(1);
        final var intShopper = orderChildElements.stream().filter(com.worldpay.internal.model.Shopper.class::isInstance).map(com.worldpay.internal.model.Shopper.class::cast).findAny().orElseThrow();
        assertThat(intShopper).isEqualTo(intShopperMock);
    }

    @Test
    public void transformToInternalModel_shouldNotSetShopper_WhenEmpty() throws WorldpayModelTransformationException {
        order.setShopper(null);

        var result = order.transformToInternalModel();

        final var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        final boolean emptyShopper = orderChildElements.stream().noneMatch(com.worldpay.internal.model.Shopper.class::isInstance);
        assertThat(emptyShopper).isTrue();
    }

    @Test
    public void transformToInternalModel_shouldSetShippingAddress() throws WorldpayModelTransformationException {
        order.setShippingAddress(shippingAddressMock);

        var result = order.transformToInternalModel();

        final var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        assertThat(orderChildElements.stream().filter(com.worldpay.internal.model.ShippingAddress.class::isInstance).map(com.worldpay.internal.model.ShippingAddress.class::cast).collect(Collectors.toList())).hasSize(1);
        final var intShippingAddress = orderChildElements.stream().filter(com.worldpay.internal.model.ShippingAddress.class::isInstance).map(com.worldpay.internal.model.ShippingAddress.class::cast).findAny().orElseThrow();
        assertThat(intShippingAddress.getAddress()).isEqualTo(intShippingAddressMock);
    }


    @Test
    public void transformToInternalModel_shouldNotSetShippingAddress_WhenEmpty() throws WorldpayModelTransformationException {
        order.setShippingAddress(null);

        var result = order.transformToInternalModel();

        final var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        final boolean emptyShippingAddress = orderChildElements.stream().noneMatch(com.worldpay.internal.model.ShippingAddress.class::isInstance);
        assertThat(emptyShippingAddress).isTrue();
    }

    @Test
    public void transformToInternalModel_shouldSetBillingAddress() throws WorldpayModelTransformationException {
        order.setBillingAddress(billingAddressMock);

        var result = order.transformToInternalModel();

        final var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        assertThat(orderChildElements.stream().filter(com.worldpay.internal.model.BillingAddress.class::isInstance).map(com.worldpay.internal.model.BillingAddress.class::cast).collect(Collectors.toList())).hasSize(1);
        final var intBillingAddress = orderChildElements.stream().filter(com.worldpay.internal.model.BillingAddress.class::isInstance).map(com.worldpay.internal.model.BillingAddress.class::cast).findAny().orElseThrow();
        assertThat(intBillingAddress.getAddress()).isEqualTo(intBillingAddressMock);
    }

    @Test
    public void transformToInternalModel_shouldNotSetBillingAddress_WhenEmpty() throws WorldpayModelTransformationException {
        order.setBillingAddress(null);

        var result = order.transformToInternalModel();

        final var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        final boolean emptyBillingAddress = orderChildElements.stream().noneMatch(com.worldpay.internal.model.BillingAddress.class::isInstance);
        assertThat(emptyBillingAddress).isTrue();
    }

    @Test
    public void transformToInternalModel_shouldAddPaymentMethodAttributes() throws WorldpayModelTransformationException {
        order.setPaymentMethodAttributes(List.of(paymentMethodAttributeMock1, paymentMethodAttributeMock2));

        var result = order.transformToInternalModel();

        final var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        assertThat(orderChildElements.stream().filter(com.worldpay.internal.model.PaymentMethodAttribute.class::isInstance).map(com.worldpay.internal.model.PaymentMethodAttribute.class::cast).collect(Collectors.toList())).hasSize(2);
        final var intPaymentMethodAttributes = orderChildElements.stream().filter(com.worldpay.internal.model.PaymentMethodAttribute.class::isInstance).map(com.worldpay.internal.model.PaymentMethodAttribute.class::cast).collect(Collectors.toList());
        assertThat(intPaymentMethodAttributes).containsOnly(intPaymentMethodAttributeMock1, intPaymentMethodAttributeMock2);
    }

    @Test
    public void transformToInternalModel_shouldNotPaymentMethodAttributes_WhenEmpty() throws WorldpayModelTransformationException {
        order.setPaymentMethodAttributes(Collections.emptyList());

        var result = order.transformToInternalModel();

        final var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        final boolean emptyPaymentMethodAttributes = orderChildElements.stream().noneMatch(com.worldpay.internal.model.PaymentMethodAttribute.class::isInstance);
        assertThat(emptyPaymentMethodAttributes).isTrue();
    }

    @Test
    public void transformToInternalModel_shouldAddStatementNarrative() throws WorldpayModelTransformationException {
        order.setStatementNarrative("statementNarrative");

        var result = order.transformToInternalModel();

        final var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        assertThat(orderChildElements.stream().filter(com.worldpay.internal.model.StatementNarrative.class::isInstance).map(com.worldpay.internal.model.StatementNarrative.class::cast).collect(Collectors.toList())).hasSize(1);
        final var intStatementNarrative = orderChildElements.stream().filter(com.worldpay.internal.model.StatementNarrative.class::isInstance).map(com.worldpay.internal.model.StatementNarrative.class::cast).findAny().orElseThrow();
        assertThat(intStatementNarrative.getvalue()).isEqualToIgnoringCase("statementNarrative");
    }

    @Test
    public void transformToInternalModel_shouldNotAddStatementNarrative_WhenEmpty() throws WorldpayModelTransformationException {
        order.setStatementNarrative(null);

        var result = order.transformToInternalModel();

        final var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        final boolean emptyStatementNarrative = orderChildElements.stream().noneMatch(com.worldpay.internal.model.StatementNarrative.class::isInstance);
        assertThat(emptyStatementNarrative).isTrue();
    }

    @Test
    public void transformToInternalModel_shouldAddEchoData() throws WorldpayModelTransformationException {
        order.setEchoData("echoData");

        var result = order.transformToInternalModel();

        final var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        assertThat(orderChildElements.stream().filter(com.worldpay.internal.model.EchoData.class::isInstance).map(com.worldpay.internal.model.EchoData.class::cast).collect(Collectors.toList())).hasSize(1);
        final var intEchoData = orderChildElements.stream().filter(com.worldpay.internal.model.EchoData.class::isInstance).map(com.worldpay.internal.model.EchoData.class::cast).findAny().orElseThrow();
        assertThat(intEchoData.getvalue()).isEqualToIgnoringCase("echoData");
    }

    @Test
    public void transformToInternalModel_shouldNotAddEchoData_WhenEmpty() throws WorldpayModelTransformationException {
        order.setEchoData(null);

        var result = order.transformToInternalModel();

        final var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        final boolean emtpyEchoData = orderChildElements.stream().noneMatch(com.worldpay.internal.model.EchoData.class::isInstance);
        assertThat(emtpyEchoData).isTrue();
    }

    @Test
    public void transformToInternalModel_shouldAddCreateTokenRequest() throws WorldpayModelTransformationException {
        order.setTokenRequest(tokenRequestMock);

        var result = order.transformToInternalModel();

        final var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        assertThat(orderChildElements.stream().filter(com.worldpay.internal.model.CreateToken.class::isInstance).map(com.worldpay.internal.model.CreateToken.class::cast).collect(Collectors.toList())).hasSize(1);
        final var intCreateToken = orderChildElements.stream().filter(com.worldpay.internal.model.CreateToken.class::isInstance).map(com.worldpay.internal.model.CreateToken.class::cast).findAny().orElseThrow();
        assertThat(intCreateToken).isEqualTo(createTokenMock);
    }

    @Test
    public void transformToInternalModel_shouldNotAddCreateTokenRequest_WhenEmpty() throws WorldpayModelTransformationException {
        order.setTokenRequest(null);

        var result = order.transformToInternalModel();

        final var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        final boolean emptyCreateToken = orderChildElements.stream().noneMatch(com.worldpay.internal.model.CreateToken.class::isInstance);
        assertThat(emptyCreateToken).isTrue();
    }

    @Test
    public void transformToInternalModel_shouldAddPaResponseInsideInfo3dSecure() throws WorldpayModelTransformationException {
        order.setPaResponse("paResponse");

        var result = order.transformToInternalModel();

        final var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        assertThat(orderChildElements.stream().filter(com.worldpay.internal.model.Info3DSecure.class::isInstance).map(com.worldpay.internal.model.Info3DSecure.class::cast).collect(Collectors.toList())).hasSize(1);
        final var intInfo3DSecure = orderChildElements.stream().filter(com.worldpay.internal.model.Info3DSecure.class::isInstance).map(com.worldpay.internal.model.Info3DSecure.class::cast).findAny().orElseThrow();
        final var threeDSecureInfoChildElements = intInfo3DSecure.getPaResponseOrMpiProviderOrMpiResponseOrAttemptedAuthenticationOrCompletedAuthenticationOrThreeDSVersionOrMerchantNameOrXidOrDsTransactionIdOrCavvOrEci();
        assertThat(threeDSecureInfoChildElements.stream().filter(com.worldpay.internal.model.PaResponse.class::isInstance).map(com.worldpay.internal.model.PaResponse.class::cast).collect(Collectors.toList())).hasSize(1);
        final PaResponse paResponse = threeDSecureInfoChildElements.stream().filter(PaResponse.class::isInstance).map(PaResponse.class::cast).findAny().orElseThrow();
        assertThat(paResponse.getvalue()).isEqualTo("paResponse");
    }

    @Test
    public void transformToInternalModel_shouldNotAddPaResponseInsideInfo3dSecure_WhenEmpty() throws WorldpayModelTransformationException {
        order.setPaResponse(null);

        var result = order.transformToInternalModel();

        final var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        final boolean emptyInfo3DSecure = orderChildElements.stream().noneMatch(Info3DSecure.class::isInstance);
        assertThat(emptyInfo3DSecure).isTrue();
    }

    @Test
    public void transformToInternalModel_shouldAddSession() throws WorldpayModelTransformationException {
        order.setSession(sessionMock);

        var result = order.transformToInternalModel();

        final var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        assertThat(orderChildElements.stream().filter(com.worldpay.internal.model.Session.class::isInstance).map(com.worldpay.internal.model.Session.class::cast).collect(Collectors.toList())).hasSize(1);
        final var intSession = orderChildElements.stream().filter(com.worldpay.internal.model.Session.class::isInstance).map(com.worldpay.internal.model.Session.class::cast).findAny().orElseThrow();
        assertThat(intSession).isEqualTo(intSessionMock);
    }

    @Test
    public void transformToInternalModel_shouldNotAddSession_WhenEmpty() throws WorldpayModelTransformationException {
        order.setSession(null);

        var result = order.transformToInternalModel();

        final var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        final boolean emptySession = orderChildElements.stream().noneMatch(com.worldpay.internal.model.Session.class::isInstance);
        assertThat(emptySession).isTrue();
    }

    @Test
    public void transformToInternalModel_shouldAddOrderLines() throws WorldpayModelTransformationException {
        order.setOrderLines(orderLinesMock);

        var result = order.transformToInternalModel();

        final var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        assertThat(orderChildElements.stream().filter(com.worldpay.internal.model.OrderLines.class::isInstance).map(com.worldpay.internal.model.OrderLines.class::cast).collect(Collectors.toList())).hasSize(1);
        final var intOrderLines = orderChildElements.stream().filter(com.worldpay.internal.model.OrderLines.class::isInstance).map(com.worldpay.internal.model.OrderLines.class::cast).findAny().orElseThrow();
        assertThat(intOrderLines).isEqualTo(intOrderLinesMock);
    }

    @Test
    public void transformToInternalModel_shouldNotAddOrderLines_WhenEmpty() throws WorldpayModelTransformationException {
        order.setOrderLines(null);

        var result = order.transformToInternalModel();

        final var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        final boolean emptyOrderLines = orderChildElements.stream().noneMatch(com.worldpay.internal.model.OrderLines.class::isInstance);
        assertThat(emptyOrderLines).isTrue();
    }

    @Test
    public void transformToInternalModel_shouldAddDynamicInteractionType() throws WorldpayModelTransformationException {
        order.setDynamicInteractionType(DynamicInteractionType.ECOMMERCE);

        var result = order.transformToInternalModel();

        final var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        assertThat(orderChildElements.stream().filter(com.worldpay.internal.model.DynamicInteractionType.class::isInstance).map(com.worldpay.internal.model.DynamicInteractionType.class::cast).collect(Collectors.toList())).hasSize(1);
        final var dynamicInteractionType = orderChildElements.stream().filter(com.worldpay.internal.model.DynamicInteractionType.class::isInstance).map(com.worldpay.internal.model.DynamicInteractionType.class::cast).findAny().orElseThrow();
        assertThat(dynamicInteractionType.getType()).isEqualTo(DynamicInteractionType.ECOMMERCE.name());
    }

    @Test
    public void transformToInternalModel_shouldNotAddDynamicInteractionType_WhenEmpty() throws WorldpayModelTransformationException {
        order.setDynamicInteractionType(null);

        var result = order.transformToInternalModel();

        final var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        final boolean emptyDynamicInteractionType = orderChildElements.stream().noneMatch(com.worldpay.internal.model.DynamicInteractionType.class::isInstance);
        assertThat(emptyDynamicInteractionType).isTrue();
    }

    @Test
    public void transformToInternalModel_shouldAddPaymentMethodMask() throws WorldpayModelTransformationException {
        order.setPaymentMethodMask(paymentMethodMaskMock);

        var result = order.transformToInternalModel();

        final var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        assertThat(orderChildElements.stream().filter(com.worldpay.internal.model.PaymentMethodMask.class::isInstance).map(com.worldpay.internal.model.PaymentMethodMask.class::cast).collect(Collectors.toList())).hasSize(1);
        final var intPaymentMethodMask = orderChildElements.stream().filter(com.worldpay.internal.model.PaymentMethodMask.class::isInstance).map(com.worldpay.internal.model.PaymentMethodMask.class::cast).findAny().orElseThrow();
        assertThat(intPaymentMethodMask).isEqualTo(intPaymentMethodMaskMock);
    }

    @Test
    public void transformToInternalModel_shouldNotAddPaymentMethodMask_WhenEmpty() throws WorldpayModelTransformationException {
        order.setPaymentMethodMask(null);

        var result = order.transformToInternalModel();

        final var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        final boolean emptyPaymentMethodMask = orderChildElements.stream().noneMatch(com.worldpay.internal.model.PaymentMethodMask.class::isInstance);
        assertThat(emptyPaymentMethodMask).isTrue();
    }

    @Test
    public void transformToInternalModel_shouldAddPaymentDetails() throws WorldpayModelTransformationException {
        order.setPaymentDetails(paymentDetailsMock);

        var result = order.transformToInternalModel();

        final var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        assertThat(orderChildElements.stream().filter(com.worldpay.internal.model.PaymentDetails.class::isInstance).map(com.worldpay.internal.model.PaymentDetails.class::cast).collect(Collectors.toList())).hasSize(1);
        final var intPaymentDetails = orderChildElements.stream().filter(com.worldpay.internal.model.PaymentDetails.class::isInstance).map(com.worldpay.internal.model.PaymentDetails.class::cast).findAny().orElseThrow();
        assertThat(intPaymentDetails).isEqualTo(intPaymentDetailsMock);
    }

    @Test
    public void transformToInternalModel_shouldNotAddPaymentDetails_WhenEmpty() throws WorldpayModelTransformationException {
        order.setPaymentDetails(null);

        var result = order.transformToInternalModel();

        final var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        final boolean emptyPaymentDetails = orderChildElements.stream().noneMatch(com.worldpay.internal.model.PaymentDetails.class::isInstance);
        assertThat(emptyPaymentDetails).isTrue();
    }

    @Test
    public void transformToInternalModel_shouldAddPayAsOrder() throws WorldpayModelTransformationException {
        order.setPayAsOrder(payAsOrderMock);

        var result = order.transformToInternalModel();

        final var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        assertThat(orderChildElements.stream().filter(com.worldpay.internal.model.PayAsOrder.class::isInstance).map(com.worldpay.internal.model.PayAsOrder.class::cast).collect(Collectors.toList())).hasSize(1);
        final var intPaymentDetails = orderChildElements.stream().filter(com.worldpay.internal.model.PayAsOrder.class::isInstance).map(com.worldpay.internal.model.PayAsOrder.class::cast).findAny().orElseThrow();
        assertThat(intPaymentDetails).isEqualTo(intPayAsOrderMock);
    }

    @Test
    public void transformToInternalModel_shouldNotAddPayAsOrder_WhenEmpty() throws WorldpayModelTransformationException {
        order.setPayAsOrder(null);

        var result = order.transformToInternalModel();

        final var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        final boolean emptyPayAsOrder = orderChildElements.stream().noneMatch(com.worldpay.internal.model.PayAsOrder.class::isInstance);
        assertThat(emptyPayAsOrder).isTrue();
    }

    @Test
    public void transformToInternalModel_shouldPopulateAdditional3DSData() throws WorldpayModelTransformationException {
        order.setAdditional3DSData(additional3DSDataMock);

        var result = order.transformToInternalModel();

        final var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        assertThat(orderChildElements.stream().filter(com.worldpay.internal.model.Additional3DSData.class::isInstance).map(com.worldpay.internal.model.Additional3DSData.class::cast).collect(Collectors.toList())).hasSize(1);
        final var intAdditional3DSData = orderChildElements.stream().filter(com.worldpay.internal.model.Additional3DSData.class::isInstance).map(com.worldpay.internal.model.Additional3DSData.class::cast).findAny().orElseThrow();
        assertThat(intAdditional3DSData).isEqualTo(intAdditional3DSDataMock);
    }

    @Test
    public void transformToInternalModel_shouldNotAddAdditional3DSData_WhenEmpty() throws WorldpayModelTransformationException {
        order.setAdditional3DSData(null);

        var result = order.transformToInternalModel();

        final var orderChildElements = result.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        final boolean emptyAdditional3DSData = orderChildElements.stream().noneMatch(com.worldpay.internal.model.Additional3DSData.class::isInstance);
        assertThat(emptyAdditional3DSData).isTrue();
    }
}
