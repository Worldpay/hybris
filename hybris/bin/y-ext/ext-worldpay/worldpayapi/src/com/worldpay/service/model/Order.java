package com.worldpay.service.model;

import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.helper.InternalModelObject;
import com.worldpay.internal.model.*;
import com.worldpay.service.model.payment.PayAsOrder;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.request.transform.InternalModelTransformer;

import java.io.Serializable;
import java.util.List;

/**
 * POJO representation of an order
 */
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

    /**
     * Constructor with full list of fields
     * @param orderCode
     * @param description
     * @param amount
     */
    public Order(String orderCode, String description, Amount amount) {
        super(orderCode, description, amount);
    }

    @Override
    public InternalModelObject transformToInternalModel() throws WorldpayModelTransformationException {
        com.worldpay.internal.model.Order intOrder = new com.worldpay.internal.model.Order();
        List<Object> childElements = intOrder.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrInfo3DSecureOrSession();

        if (getOrderCode() != null) {
            intOrder.setOrderCode(getOrderCode());
        }
        if (getDescription() != null) {
            Description intDescription = new Description();
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
            OrderContent intOrderContent = new OrderContent();
            intOrderContent.setvalue(orderContent);
            childElements.add(intOrderContent);
        }
        if (paymentMethodMask != null) {
            childElements.add(paymentMethodMask.transformToInternalModel());
        } else if (paymentDetails != null) {
            childElements.add(paymentDetails.transformToInternalModel());
        } else if (payAsOrder != null) {
            childElements.add(payAsOrder.transformToInternalModel());
        }
        if (shopper != null) {
            childElements.add(shopper.transformToInternalModel());
        }
        if (shippingAddress != null) {
            ShippingAddress intShippingAddress = new ShippingAddress();
            intShippingAddress.setAddress((com.worldpay.internal.model.Address) shippingAddress.transformToInternalModel());
            childElements.add(intShippingAddress);
        }
        if (billingAddress != null) {
            BillingAddress intBillingAddress = new BillingAddress();
            intBillingAddress.setAddress((com.worldpay.internal.model.Address) billingAddress.transformToInternalModel());
            childElements.add(intBillingAddress);
        }
        if (statementNarrative != null) {
            StatementNarrative intStatementNarrative = new StatementNarrative();
            intStatementNarrative.setvalue(statementNarrative);
            childElements.add(intStatementNarrative);
        }
        if (echoData != null) {
            EchoData intEchoData = new EchoData();
            intEchoData.setvalue(echoData);
            childElements.add(intEchoData);
        }
        if (tokenRequest != null) {
            childElements.add(tokenRequest.transformToInternalModel());
        }
        if (paResponse != null) {
            Info3DSecure intInfo3dSecure = new Info3DSecure();
            PaResponse intPaResponse = new PaResponse();
            intPaResponse.setvalue(getPaResponse());
            intInfo3dSecure.getPaResponseOrMpiProviderOrMpiResponseOrXidOrCavvOrEciOrAttemptedAuthenticationOrCompletedAuthenticationOrThreeDSVersionOrMerchantNameOrDsTransactionId().add(intPaResponse);
            childElements.add(intInfo3dSecure);
        }
        if (session != null) {
            childElements.add(session.transformToInternalModel());
        }

        if(orderLines != null) {
            childElements.add(orderLines.transformToInternalModel());
        }

        if(dynamicInteractionType != null) {
            final com.worldpay.internal.model.DynamicInteractionType internalDynamicInteractionType = new com.worldpay.internal.model.DynamicInteractionType();
            internalDynamicInteractionType.setType(this.dynamicInteractionType.name());
            childElements.add(internalDynamicInteractionType);
        }

        return intOrder;
    }

    public String getInstallationId() {
        return installationId;
    }

    public void setInstallationId(String installationId) {
        this.installationId = installationId;
    }

    public String getOrderContent() {
        return orderContent;
    }

    public void setOrderContent(String orderContent) {
        this.orderContent = orderContent;
    }

    public PaymentMethodMask getPaymentMethodMask() {
        return paymentMethodMask;
    }

    public void setPaymentMethodMask(PaymentMethodMask paymentMethodMask) {
        this.paymentMethodMask = paymentMethodMask;
    }

    public PaymentDetails getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(PaymentDetails paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    public PayAsOrder getPayAsOrder() {
        return payAsOrder;
    }

    public void setPayAsOrder(PayAsOrder payAsOrder) {
        this.payAsOrder = payAsOrder;
    }

    public Shopper getShopper() {
        return shopper;
    }

    public void setShopper(Shopper shopper) {
        this.shopper = shopper;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getStatementNarrative() {
        return statementNarrative;
    }

    public void setStatementNarrative(String statementNarrative) {
        this.statementNarrative = statementNarrative;
    }

    public String getEchoData() {
        return echoData;
    }

    public void setEchoData(String echoData) {
        this.echoData = echoData;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public TokenRequest getTokenRequest() {
        return tokenRequest;
    }

    public void setTokenRequest(TokenRequest tokenRequest) {
        this.tokenRequest = tokenRequest;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getPaResponse() {
        return paResponse;
    }

    public void setPaResponse(String paResponse) {
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
}
