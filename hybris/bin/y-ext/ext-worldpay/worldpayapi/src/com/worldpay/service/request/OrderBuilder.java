package com.worldpay.service.request;

import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.service.model.*;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.model.payment.StoredCredentials;
import com.worldpay.service.model.threeds2.Additional3DSData;
import com.worldpay.service.model.threeds2.RiskData;
import com.worldpay.service.model.token.TokenRequest;
import org.apache.commons.collections4.CollectionUtils;

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

    public Order build() {
        final Order order = new Order(orderInfo.getOrderCode(), orderInfo.getDescription(), orderInfo.getAmount());
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

        return order;
    }

    protected void addStoredCredentials(final Order reqOrder, final StoredCredentials storedCredentials1) {
        final PaymentMethodMask pmm = Optional.ofNullable(reqOrder.getPaymentMethodMask()).orElseGet(PaymentMethodMask::new);
        pmm.setStoredCredentials(storedCredentials1);
        reqOrder.setPaymentMethodMask(pmm);
    }

    protected void addPaymentTypesExcluded(final Order reqOrder, final List<PaymentType> excludedPTs) {
        final PaymentMethodMask pmm = Optional.ofNullable(reqOrder.getPaymentMethodMask()).orElseGet(PaymentMethodMask::new);
        CollectionUtils.emptyIfNull(excludedPTs).forEach(pmm::addExclude);
        reqOrder.setPaymentMethodMask(pmm);
    }

    protected void addPaymentTypesIncluded(final Order reqOrder, final List<PaymentType> includedPTs) {
        final PaymentMethodMask pmm = Optional.ofNullable(reqOrder.getPaymentMethodMask()).orElseGet(PaymentMethodMask::new);
        CollectionUtils.emptyIfNull(includedPTs).forEach(pmm::addInclude);
        reqOrder.setPaymentMethodMask(pmm);
    }
}

