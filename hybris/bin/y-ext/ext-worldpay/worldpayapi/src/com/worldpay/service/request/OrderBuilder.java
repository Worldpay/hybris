package com.worldpay.service.request;

import com.worldpay.data.*;
import com.worldpay.data.payment.StoredCredentials;
import com.worldpay.data.threeds2.Additional3DSData;
import com.worldpay.data.threeds2.RiskData;
import com.worldpay.data.token.TokenRequest;
import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.service.model.payment.PaymentType;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderBuilder {

    private PaymentDetails paymentDetails;
    private BasicOrderInfo orderInfo;
    private Shopper shopper;
    private Address shippingAddress;
    private Address billingAddress;
    private String statementNarrative;
    private DynamicInteractionType dynamicInteractionType;
    private String echoData;
    private TokenRequest tokenRequest;
    private OrderLines orderLines;
    private RiskData riskData;
    private Additional3DSData additional3DSData;
    private String installationId;
    private String orderContent;
    private List<PaymentType> includedPaymentMethods;
    private List<PaymentType> excludedPaymentMethods;
    private StoredCredentials storedCredentials;
    private List<PaymentMethodAttribute> paymentMethodAttributes;
    private FraudSightData fraudSightData;
    private String deviceSession;
    private BranchSpecificExtension branchSpecificExtension;
    private String mandateType;
    private GuaranteedPaymentsData guaranteedPaymentsData;
    private String checkoutId;

    public OrderBuilder withOrderContent(String orderContent) {
        this.orderContent = orderContent;
        return this;
    }

    public OrderBuilder withInstallationId(String installationId) {
        this.installationId = installationId;
        return this;
    }

    public OrderBuilder withExcludedPaymentMethods(List<PaymentType> excludedPaymentMethods) {
        this.excludedPaymentMethods = excludedPaymentMethods;
        return this;
    }

    public OrderBuilder withIncludedPaymentMethods(List<PaymentType> includedPaymentMethods) {
        this.includedPaymentMethods = includedPaymentMethods;
        return this;
    }

    public OrderBuilder withOrderInfo(final BasicOrderInfo orderInfo) {
        this.orderInfo = orderInfo;
        return this;
    }

    OrderBuilder withPaymentDetails(final PaymentDetails paymentDetails) {
        this.paymentDetails = paymentDetails;
        return this;
    }

    OrderBuilder withShopper(final Shopper shopper) {
        this.shopper = shopper;
        return this;
    }

    OrderBuilder withShippingAddress(final Address shippingAddress) {
        this.shippingAddress = shippingAddress;
        return this;
    }

    OrderBuilder withBillingAddress(final Address billingAddress) {
        this.billingAddress = billingAddress;
        return this;
    }

    OrderBuilder withStatementNarrative(final String statementNarrative) {
        this.statementNarrative = statementNarrative;
        return this;
    }

    OrderBuilder withDynamicInteractionType(final DynamicInteractionType dynamicInteractionType) {
        this.dynamicInteractionType = dynamicInteractionType;
        return this;
    }

    OrderBuilder withEchoData(final String echoData) {
        this.echoData = echoData;
        return this;
    }

    OrderBuilder withTokenRequest(final TokenRequest tokenRequest) {
        this.tokenRequest = tokenRequest;
        return this;
    }

    OrderBuilder withOrderLines(final OrderLines orderLines) {
        this.orderLines = orderLines;
        return this;
    }

    OrderBuilder withRiskData(final RiskData riskData) {
        this.riskData = riskData;
        return this;
    }

    OrderBuilder withAdditional3DSData(final Additional3DSData additional3DSData) {
        this.additional3DSData = additional3DSData;
        return this;
    }

    OrderBuilder withStoredCredentials(final StoredCredentials storedCredentials) {
        this.storedCredentials = storedCredentials;
        return this;
    }

    OrderBuilder withPaymentMethodAttribute(final List<PaymentMethodAttribute> paymentMethodAttributes) {
        this.paymentMethodAttributes = paymentMethodAttributes;
        return this;
    }

    OrderBuilder withFraudSightAttribute(final FraudSightData fraudSightData) {
        this.fraudSightData = fraudSightData;
        return this;
    }

    OrderBuilder withDeviceSession(final String deviceSession) {
        this.deviceSession = deviceSession;
        return this;
    }

    OrderBuilder withLevel23Data(final BranchSpecificExtension branchSpecificExtension) {
        this.branchSpecificExtension = branchSpecificExtension;
        return this;
    }

    OrderBuilder withMandateType(final String mandateType) {
        this.mandateType = mandateType;
        return this;
    }

    OrderBuilder whitGuaranteedPaymentsAttribute(final GuaranteedPaymentsData guaranteedPaymentsData) {
        this.guaranteedPaymentsData = guaranteedPaymentsData;
        return this;
    }

    OrderBuilder whitCheckoutId(final String checkoutId) {
        this.checkoutId = checkoutId;
        return this;
    }

    public Order build() {
        final Order order = new Order();
        order.setOrderCode(orderInfo.getOrderCode());
        order.setDescription(orderInfo.getDescription());
        order.setAmount(orderInfo.getAmount());
        order.setOrderChannel(orderInfo.getOrderChannel());
        order.setPaymentDetails(paymentDetails);
        order.setShopper(shopper);
        order.setShippingAddress(shippingAddress);
        order.setDynamicInteractionType(dynamicInteractionType);

        Optional.ofNullable(echoData).ifPresent(order::setEchoData);
        Optional.ofNullable(tokenRequest).ifPresent(order::setTokenRequest);
        Optional.ofNullable(orderLines).ifPresent(order::setOrderLines);
        Optional.ofNullable(billingAddress).ifPresent(order::setBillingAddress);
        Optional.ofNullable(statementNarrative).ifPresent(order::setStatementNarrative);
        Optional.ofNullable(riskData).ifPresent(order::setRiskData);
        Optional.ofNullable(additional3DSData).ifPresent(order::setAdditional3DSData);
        Optional.ofNullable(installationId).ifPresent(order::setInstallationId);
        Optional.ofNullable(orderContent).ifPresent(order::setOrderContent);
        Optional.ofNullable(includedPaymentMethods).ifPresent(includedPTs -> addPaymentTypesIncluded(order, includedPTs));
        Optional.ofNullable(excludedPaymentMethods).ifPresent(excludedPTs -> addPaymentTypesExcluded(order, excludedPTs));
        Optional.ofNullable(storedCredentials).ifPresent(credentials -> addStoredCredentials(order, credentials));
        Optional.ofNullable(paymentMethodAttributes).ifPresent(order::setPaymentMethodAttributes);
        Optional.ofNullable(fraudSightData).ifPresent(order::setFraudSightData);
        Optional.ofNullable(deviceSession).ifPresent(order::setDeviceSession);
        Optional.ofNullable(branchSpecificExtension).ifPresent(order::setBranchSpecificExtension);
        Optional.ofNullable(mandateType).ifPresent(order::setMandateType);
        Optional.ofNullable(guaranteedPaymentsData).ifPresent(order::setGuaranteedPaymentsData);
        Optional.ofNullable(checkoutId).ifPresent(order::setCheckoutId);

        return order;
    }

    protected void addStoredCredentials(final Order reqOrder, final StoredCredentials storedCredentials1) {
        final PaymentMethodMask pmm = Optional.ofNullable(reqOrder.getPaymentMethodMask()).orElseGet(PaymentMethodMask::new);
        pmm.setStoredCredentials(storedCredentials1);
        reqOrder.setPaymentMethodMask(pmm);
    }

    protected void addPaymentTypesExcluded(final Order reqOrder, final List<PaymentType> excludedPTs) {
        final PaymentMethodMask pmm = Optional.ofNullable(reqOrder.getPaymentMethodMask()).orElseGet(PaymentMethodMask::new);
        CollectionUtils.emptyIfNull(excludedPTs).forEach(paymentType -> addExclude(paymentType, pmm));
        reqOrder.setPaymentMethodMask(pmm);
    }

    protected void addPaymentTypesIncluded(final Order reqOrder, final List<PaymentType> includedPTs) {
        final PaymentMethodMask pmm = Optional.ofNullable(reqOrder.getPaymentMethodMask()).orElseGet(PaymentMethodMask::new);
        CollectionUtils.emptyIfNull(includedPTs).forEach(paymentType -> addInclude(paymentType, pmm));
        reqOrder.setPaymentMethodMask(pmm);
    }

    /**
     * Add an item to the list of payment methods that are included
     *
     * @param paymentType PaymentType to be included
     */
    private void addInclude(PaymentType paymentType, PaymentMethodMask pmm) {

        if (pmm.getIncludes() == null) {
            pmm.setIncludes(new ArrayList<>());
        }
        pmm.getIncludes().add(paymentType.getMethodCode());
    }

    /**
     * Add an item to the list of payment methods that are excluded
     *
     * @param paymentType PaymentType to be excluded
     */
    private void addExclude(PaymentType paymentType, PaymentMethodMask pmm) {

        if (pmm.getExcludes() == null) {
            pmm.setExcludes(new ArrayList<>());
        }
        pmm.getExcludes().add(paymentType.getMethodCode());
    }
}

