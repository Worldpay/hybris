package com.worldpay.service.model;

import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.helper.InternalModelObject;
import com.worldpay.internal.model.*;
import com.worldpay.service.model.payment.PayAsOrder;
import com.worldpay.service.model.threeds2.Additional3DSData;
import com.worldpay.service.model.threeds2.RiskData;
import com.worldpay.service.model.threeds2.RiskDateData;
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
    private Additional3DSData additional3DSData;
    private RiskData riskData;

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
    public InternalModelObject transformToInternalModel() throws WorldpayModelTransformationException {
        com.worldpay.internal.model.Order intOrder = new com.worldpay.internal.model.Order();
        List<Object> childElements = intOrder.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrDeviceSessionOrInfo3DSecureOrSession();

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
            final ShippingAddress intShippingAddress = new ShippingAddress();
            intShippingAddress.setAddress((com.worldpay.internal.model.Address) shippingAddress.transformToInternalModel());
            childElements.add(intShippingAddress);
        }
        if (billingAddress != null) {
            final BillingAddress intBillingAddress = new BillingAddress();
            intBillingAddress.setAddress((com.worldpay.internal.model.Address) billingAddress.transformToInternalModel());
            childElements.add(intBillingAddress);
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

        if (dynamicInteractionType != null) {
            final com.worldpay.internal.model.DynamicInteractionType internalDynamicInteractionType = new com.worldpay.internal.model.DynamicInteractionType();
            internalDynamicInteractionType.setType(dynamicInteractionType.name());
            childElements.add(internalDynamicInteractionType);
        }

        populateRiskData(childElements);

        if (additional3DSData != null) {
            final com.worldpay.internal.model.Additional3DSData internalAdditional3DSData = new com.worldpay.internal.model.Additional3DSData();
            internalAdditional3DSData.setDfReferenceId(additional3DSData.getDfReferenceId());
            if (additional3DSData.getChallengeWindowSize() != null) {
                internalAdditional3DSData.setChallengeWindowSize(additional3DSData.getChallengeWindowSize().toString());
            }

            if (additional3DSData.getChallengePreference() != null) {
                internalAdditional3DSData.setChallengePreference(additional3DSData.getChallengePreference().toString());
            }
            childElements.add(internalAdditional3DSData);
        }

        return intOrder;
    }

    private void populateRiskData(final List<Object> childElements) {
        if (riskData != null) {
            final com.worldpay.internal.model.RiskData internalRiskData = new com.worldpay.internal.model.RiskData();
            populateAuthenticationRiskData(internalRiskData);
            populateShopperAccountRiskData(internalRiskData);
            populateTransactionRiskData(internalRiskData);
            childElements.add(internalRiskData);
        }
    }

    private void populateTransactionRiskData(final com.worldpay.internal.model.RiskData internalRiskData) {
        if (riskData.getTransactionRiskData() != null) {
            final com.worldpay.service.model.threeds2.TransactionRiskData transactionRiskData = riskData.getTransactionRiskData();
            final TransactionRiskData internalTransactionRiskData = new TransactionRiskData();
            internalTransactionRiskData.setDeliveryEmailAddress(transactionRiskData.getDeliveryEmailAddress());
            internalTransactionRiskData.setDeliveryTimeframe(transactionRiskData.getDeliveryTimeframe());
            internalTransactionRiskData.setGiftCardCount(transactionRiskData.getGiftCardCount());
            internalTransactionRiskData.setPreOrderPurchase(transactionRiskData.getPreOrderPurchase());
            internalTransactionRiskData.setReorderingPreviousPurchases(transactionRiskData.getReorderingPreviousPurchases());
            internalTransactionRiskData.setShippingMethod(transactionRiskData.getShippingMethod());
            populateTransactionRiskDataGiftCardAmount(transactionRiskData, internalTransactionRiskData);
            populateTransactionRiskDataPreOrderDate(transactionRiskData, internalTransactionRiskData);
            internalRiskData.setTransactionRiskData(internalTransactionRiskData);
        }
    }

    private void populateTransactionRiskDataPreOrderDate(final com.worldpay.service.model.threeds2.TransactionRiskData transactionRiskData, final TransactionRiskData internalTransactionRiskData) {
        if (transactionRiskData.getTransactionRiskDataPreOrderDate() != null) {
            final TransactionRiskDataPreOrderDate internalTransactionRiskDataPreOrderDate = new TransactionRiskDataPreOrderDate();
            internalTransactionRiskDataPreOrderDate.setDate(createInternalDate(transactionRiskData.getTransactionRiskDataPreOrderDate().getDate()));
            internalTransactionRiskData.setTransactionRiskDataPreOrderDate(internalTransactionRiskDataPreOrderDate);
        }
    }

    private void populateTransactionRiskDataGiftCardAmount(final com.worldpay.service.model.threeds2.TransactionRiskData transactionRiskData, final TransactionRiskData internalTransactionRiskData) {
        if (transactionRiskData.getTransactionRiskDataGiftCardAmount() != null && transactionRiskData.getTransactionRiskDataGiftCardAmount().getAmount() != null) {
            final Amount amount = transactionRiskData.getTransactionRiskDataGiftCardAmount().getAmount();
            final TransactionRiskDataGiftCardAmount internalTransactionRiskDataGiftCardAmount = new TransactionRiskDataGiftCardAmount();
            populateAmount(amount, internalTransactionRiskDataGiftCardAmount);
            internalTransactionRiskData.setTransactionRiskDataGiftCardAmount(internalTransactionRiskDataGiftCardAmount);
        }
    }

    private void populateAmount(final Amount amount, final TransactionRiskDataGiftCardAmount internalTransactionRiskDataGiftCardAmount) {
        final com.worldpay.internal.model.Amount internalAmount = new com.worldpay.internal.model.Amount();
        internalAmount.setValue(amount.getValue());
        internalAmount.setCurrencyCode(amount.getCurrencyCode());
        internalAmount.setDebitCreditIndicator(amount.getDebitCreditIndicator() != null ? amount.getDebitCreditIndicator().getCode() : null);
        internalAmount.setExponent(amount.getExponent());
        internalTransactionRiskDataGiftCardAmount.setAmount(internalAmount);
    }

    private void populateShopperAccountRiskData(final com.worldpay.internal.model.RiskData internalRiskData) {
        if (riskData.getShopperAccountRiskData() != null) {
            final com.worldpay.service.model.threeds2.ShopperAccountRiskData shopperAccountRiskData = riskData.getShopperAccountRiskData();
            final ShopperAccountRiskData internalShopperAccountRiskData = new ShopperAccountRiskData();
            internalShopperAccountRiskData.setTransactionsAttemptedLastDay(shopperAccountRiskData.getTransactionsAttemptedLastDay());
            internalShopperAccountRiskData.setTransactionsAttemptedLastYear(shopperAccountRiskData.getTransactionsAttemptedLastYear());
            internalShopperAccountRiskData.setPurchasesCompletedLastSixMonths(shopperAccountRiskData.getPurchasesCompletedLastSixMonths());
            internalShopperAccountRiskData.setAddCardAttemptsLastDay(shopperAccountRiskData.getAddCardAttemptsLastDay());
            internalShopperAccountRiskData.setPreviousSuspiciousActivity(shopperAccountRiskData.getPreviousSuspiciousActivity());
            internalShopperAccountRiskData.setShippingNameMatchesAccountName(shopperAccountRiskData.getShippingNameMatchesAccountName());
            internalShopperAccountRiskData.setShopperAccountAgeIndicator(shopperAccountRiskData.getShopperAccountAgeIndicator());
            internalShopperAccountRiskData.setShopperAccountChangeIndicator(shopperAccountRiskData.getShopperAccountChangeIndicator());
            internalShopperAccountRiskData.setShopperAccountPasswordChangeIndicator(shopperAccountRiskData.getShopperAccountPasswordChangeIndicator());
            internalShopperAccountRiskData.setShopperAccountShippingAddressUsageIndicator(shopperAccountRiskData.getShopperAccountShippingAddressUsageIndicator());
            internalShopperAccountRiskData.setShopperAccountPaymentAccountIndicator(shopperAccountRiskData.getShopperAccountPaymentAccountIndicator());
            populateShopperAccountCreationDate(shopperAccountRiskData, internalShopperAccountRiskData);
            populateShopperAccountModificationDate(shopperAccountRiskData, internalShopperAccountRiskData);
            populateShopperAccountPasswordChangeDate(shopperAccountRiskData, internalShopperAccountRiskData);
            populateShopperAccountShippingAddressFirstUseDate(shopperAccountRiskData, internalShopperAccountRiskData);
            populateShopperAccountPaymentAccountFirstUseDate(shopperAccountRiskData, internalShopperAccountRiskData);
            internalRiskData.setShopperAccountRiskData(internalShopperAccountRiskData);
        }
    }

    private void populateShopperAccountPaymentAccountFirstUseDate(final com.worldpay.service.model.threeds2.ShopperAccountRiskData shopperAccountRiskData, final ShopperAccountRiskData internalShopperAccountRiskData) {
        if (shopperAccountRiskData.getShopperAccountPaymentAccountFirstUseDate() != null) {
            final ShopperAccountPaymentAccountFirstUseDate internalShopperAccountPaymentAccountFirstUseDate = new ShopperAccountPaymentAccountFirstUseDate();
            internalShopperAccountPaymentAccountFirstUseDate.setDate(createInternalDate(shopperAccountRiskData.getShopperAccountPaymentAccountFirstUseDate().getDate()));
            internalShopperAccountRiskData.setShopperAccountPaymentAccountFirstUseDate(internalShopperAccountPaymentAccountFirstUseDate);
        }
    }

    private void populateShopperAccountShippingAddressFirstUseDate(final com.worldpay.service.model.threeds2.ShopperAccountRiskData shopperAccountRiskData, final ShopperAccountRiskData internalShopperAccountRiskData) {
        if (shopperAccountRiskData.getShopperAccountShippingAddressFirstUseDate() != null) {
            final ShopperAccountShippingAddressFirstUseDate internalShopperAccountShippingAddressFirstUseDate = new ShopperAccountShippingAddressFirstUseDate();
            internalShopperAccountShippingAddressFirstUseDate.setDate(createInternalDate(shopperAccountRiskData.getShopperAccountShippingAddressFirstUseDate().getDate()));
            internalShopperAccountRiskData.setShopperAccountShippingAddressFirstUseDate(internalShopperAccountShippingAddressFirstUseDate);
        }
    }

    private void populateShopperAccountPasswordChangeDate(final com.worldpay.service.model.threeds2.ShopperAccountRiskData shopperAccountRiskData, final ShopperAccountRiskData internalShopperAccountRiskData) {
        if (shopperAccountRiskData.getShopperAccountPasswordChangeDate() != null) {
            ShopperAccountPasswordChangeDate internalShopperAccountPasswordChangeDate = new ShopperAccountPasswordChangeDate();
            internalShopperAccountPasswordChangeDate.setDate(createInternalDate(shopperAccountRiskData.getShopperAccountPasswordChangeDate().getDate()));
            internalShopperAccountRiskData.setShopperAccountPasswordChangeDate(internalShopperAccountPasswordChangeDate);
        }
    }

    private void populateShopperAccountModificationDate(final com.worldpay.service.model.threeds2.ShopperAccountRiskData shopperAccountRiskData, final ShopperAccountRiskData internalShopperAccountRiskData) {
        if (shopperAccountRiskData.getShopperAccountModificationDate() != null) {
            final ShopperAccountModificationDate internalShopperAccountModificationDate = new ShopperAccountModificationDate();
            internalShopperAccountModificationDate.setDate(createInternalDate(shopperAccountRiskData.getShopperAccountModificationDate().getDate()));
            internalShopperAccountRiskData.setShopperAccountModificationDate(internalShopperAccountModificationDate);
        }
    }

    private void populateShopperAccountCreationDate(final com.worldpay.service.model.threeds2.ShopperAccountRiskData shopperAccountRiskData, final ShopperAccountRiskData internalShopperAccountRiskData) {
        if (shopperAccountRiskData.getShopperAccountCreationDate() != null) {
            final ShopperAccountCreationDate internalShopperAccountCreationDate = new ShopperAccountCreationDate();
            internalShopperAccountCreationDate.setDate(createInternalDate(shopperAccountRiskData.getShopperAccountCreationDate().getDate()));
            internalShopperAccountRiskData.setShopperAccountCreationDate(internalShopperAccountCreationDate);
        }
    }

    private void populateAuthenticationRiskData(final com.worldpay.internal.model.RiskData internalRiskData) {
        if (riskData.getAuthenticationRiskData() != null) {
            final AuthenticationRiskData internalAuthenticationRiskData = new AuthenticationRiskData();
            internalAuthenticationRiskData.setAuthenticationMethod(riskData.getAuthenticationRiskData().getAuthenticationMethod());
            if (riskData.getAuthenticationRiskData().getAuthenticationTimestamp() != null) {
                final RiskDateData authenticationTimestamp = riskData.getAuthenticationRiskData().getAuthenticationTimestamp();
                final AuthenticationTimestamp internalAuthenticationTimestamp = new AuthenticationTimestamp();
                internalAuthenticationTimestamp.setDate(createInternalDate(authenticationTimestamp.getDate()));
                internalAuthenticationRiskData.setAuthenticationTimestamp(internalAuthenticationTimestamp);
            }
            internalRiskData.setAuthenticationRiskData(internalAuthenticationRiskData);
        }
    }

    private com.worldpay.internal.model.Date createInternalDate(final Date date) {
        final com.worldpay.internal.model.Date internalDate = new com.worldpay.internal.model.Date();
        if (date != null) {
            internalDate.setDayOfMonth(date.getDayOfMonth());
            internalDate.setMonth(date.getMonth());
            internalDate.setYear(date.getYear());
            internalDate.setHour(date.getHour());
            internalDate.setMinute(date.getMinute());
            internalDate.setSecond(date.getSecond());
        }
        return internalDate;
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
}
