package com.worldpay.service.model;

import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.*;
import com.worldpay.service.model.payment.PayAsOrder;
import com.worldpay.service.model.threeds2.Additional3DSData;
import com.worldpay.service.model.threeds2.RiskData;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.request.transform.InternalModelTransformer;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.List;

/**
 * POJO representation of an order
 */
@SuppressWarnings("squid:S3776")
public class Order extends BasicOrderInfo implements InternalModelTransformer, Serializable {

    private String installationId;
    private String orderContent;
    private PaymentMethodMask paymentMethodMask;
    private PaymentDetails paymentDetails;
    private PayAsOrder payAsOrder;
    private Shopper shopper;
    private Address shippingAddress;
    private Address billingAddress;
    private String statementNarrative;
    private String echoData;
    private TokenRequest tokenRequest;
    private Session session;
    private String paResponse;
    private OrderLines orderLines;
    private DynamicInteractionType dynamicInteractionType;
    private Additional3DSData additional3DSData;
    private RiskData riskData;
    private List<PaymentMethodAttribute> paymentMethodAttributes;

    /**
     * Constructor with full list of fields
     *
     * @param orderCode
     * @param description
     * @param amount
     */
    public Order(final String orderCode, final String description, final Amount amount) {
        super(orderCode, description, amount);
    }

    @Override
    public com.worldpay.internal.model.Order transformToInternalModel() throws WorldpayModelTransformationException {
        var intOrder = new com.worldpay.internal.model.Order();
        final List<Object> childElements = intOrder.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrInfo3DSecureOrSession();

        if (getOrderCode() != null) {
            intOrder.setOrderCode(getOrderCode());
        }

        if (getDescription() != null) {
            final Description intDescription = new Description();
            intDescription.setvalue(getDescription());
            childElements.add(intDescription);
        }

        if (getAmount() != null) {
            childElements.add(getAmount().transformToInternalModel());
        }

        if (installationId != null) {
            intOrder.setInstallationId(installationId);
        }

        if (orderContent != null) {
            final OrderContent intOrderContent = new OrderContent();
            intOrderContent.setvalue(orderContent);
            childElements.add(intOrderContent);
        }

        populatePaymentRequestDetails(childElements);

        if (shopper != null) {
            childElements.add(shopper.transformToInternalModel());
        }

        if (shippingAddress != null) {
            final ShippingAddress intShippingAddress = new ShippingAddress();
            intShippingAddress.setAddress((com.worldpay.internal.model.Address) shippingAddress.transformToInternalModel());
            childElements.add(intShippingAddress);
        }

        if (billingAddress != null) {
            final BillingAddress intBillingAddress = new BillingAddress();
            intBillingAddress.setAddress((com.worldpay.internal.model.Address) billingAddress.transformToInternalModel());
            childElements.add(intBillingAddress);
        }

        if (CollectionUtils.isNotEmpty(paymentMethodAttributes)) {
            paymentMethodAttributes.stream()
                .map(PaymentMethodAttribute::transformToInternalModel)
                .forEach(childElements::add);
        }

        if (statementNarrative != null) {
            final StatementNarrative intStatementNarrative = new StatementNarrative();
            intStatementNarrative.setvalue(statementNarrative);
            childElements.add(intStatementNarrative);
        }

        if (echoData != null) {
            final EchoData intEchoData = new EchoData();
            intEchoData.setvalue(echoData);
            childElements.add(intEchoData);
        }

        if (tokenRequest != null) {
            childElements.add(tokenRequest.transformToInternalModel());
        }

        if (paResponse != null) {
            final Info3DSecure intInfo3dSecure = new Info3DSecure();
            final PaResponse intPaResponse = new PaResponse();
            intPaResponse.setvalue(getPaResponse());
            intInfo3dSecure.getPaResponseOrMpiProviderOrMpiResponseOrAttemptedAuthenticationOrCompletedAuthenticationOrThreeDSVersionOrMerchantNameOrXidOrDsTransactionIdOrCavvOrEci().add(intPaResponse);
            childElements.add(intInfo3dSecure);
        }

        if (session != null) {
            childElements.add(session.transformToInternalModel());
        }

        if (orderLines != null) {
            childElements.add(orderLines.transformToInternalModel());
        }

        populateDynamicInteractionType(childElements);

        if (riskData != null) {
            childElements.add(riskData.transformToInternalModel());
        }

        if (additional3DSData != null) {
            childElements.add(additional3DSData.transformToInternalModel());
        }
        return intOrder;
    }

    private void populatePaymentRequestDetails(final List<Object> childElements) throws WorldpayModelTransformationException {
        if (paymentMethodMask != null) {
            childElements.add(paymentMethodMask.transformToInternalModel());
        } else if (paymentDetails != null) {
            childElements.add(paymentDetails.transformToInternalModel());
        } else if (payAsOrder != null) {
            childElements.add(payAsOrder.transformToInternalModel());
        }
    }

    private void populateDynamicInteractionType(final List<Object> childElements) {
        if (dynamicInteractionType != null) {
            final var internalDynamicInteractionType = new com.worldpay.internal.model.DynamicInteractionType();
            internalDynamicInteractionType.setType(dynamicInteractionType.name());
            childElements.add(internalDynamicInteractionType);
        }
    }

    public String getInstallationId() {
        return installationId;
    }

    public void setInstallationId(final String installationId) {
        this.installationId = installationId;
    }

    public String getOrderContent() {
        return orderContent;
    }

    public void setOrderContent(final String orderContent) {
        this.orderContent = orderContent;
    }

    public PaymentMethodMask getPaymentMethodMask() {
        return paymentMethodMask;
    }

    public void setPaymentMethodMask(final PaymentMethodMask paymentMethodMask) {
        this.paymentMethodMask = paymentMethodMask;
    }

    public PaymentDetails getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(final PaymentDetails paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    public PayAsOrder getPayAsOrder() {
        return payAsOrder;
    }

    public void setPayAsOrder(final PayAsOrder payAsOrder) {
        this.payAsOrder = payAsOrder;
    }

    public Shopper getShopper() {
        return shopper;
    }

    public void setShopper(final Shopper shopper) {
        this.shopper = shopper;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(final Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getStatementNarrative() {
        return statementNarrative;
    }

    public void setStatementNarrative(final String statementNarrative) {
        this.statementNarrative = statementNarrative;
    }

    public String getEchoData() {
        return echoData;
    }

    public void setEchoData(final String echoData) {
        this.echoData = echoData;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(final Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    public TokenRequest getTokenRequest() {
        return tokenRequest;
    }

    public void setTokenRequest(final TokenRequest tokenRequest) {
        this.tokenRequest = tokenRequest;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(final Session session) {
        this.session = session;
    }

    public String getPaResponse() {
        return paResponse;
    }

    public void setPaResponse(final String paResponse) {
        this.paResponse = paResponse;
    }

    public OrderLines getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(final OrderLines orderLines) {
        this.orderLines = orderLines;
    }

    public void setDynamicInteractionType(final DynamicInteractionType dynamicInteractionType) {
        this.dynamicInteractionType = dynamicInteractionType;
    }

    public DynamicInteractionType getDynamicInteractionType() {
        return dynamicInteractionType;
    }

    public Additional3DSData getAdditional3DSData() {
        return additional3DSData;
    }

    public void setAdditional3DSData(final Additional3DSData additional3DSData) {
        this.additional3DSData = additional3DSData;
    }

    public RiskData getRiskData() {
        return riskData;
    }

    public void setRiskData(final RiskData riskData) {
        this.riskData = riskData;
    }

    public List<PaymentMethodAttribute> getPaymentMethodAttributes() {
        return paymentMethodAttributes;
    }

    public void setPaymentMethodAttributes(List<PaymentMethodAttribute> paymentMethodAttributes) {
        this.paymentMethodAttributes = paymentMethodAttributes;
    }
}
