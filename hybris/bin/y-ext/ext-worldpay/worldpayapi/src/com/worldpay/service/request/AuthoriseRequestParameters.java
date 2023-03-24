package com.worldpay.service.request;

import com.worldpay.data.*;
import com.worldpay.data.payment.Payment;
import com.worldpay.data.payment.StoredCredentials;
import com.worldpay.data.threeds2.Additional3DSData;
import com.worldpay.data.threeds2.RiskData;
import com.worldpay.data.token.TokenRequest;
import com.worldpay.enums.PaymentAction;
import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.service.model.payment.PaymentType;

import java.util.List;

public final class AuthoriseRequestParameters {
    private MerchantInfo merchantInfo;
    private BasicOrderInfo orderInfo;
    private Payment payment;
    private Shopper shopper;
    private Address shippingAddress;
    private Address billingAddress;
    private String statementNarrative;
    private DynamicInteractionType dynamicInteractionType;
    private OrderLines orderLines;
    private Additional3DSData additional3DSData;
    private RiskData riskData;
    private String paRes;
    private String authenticatedShopperId;
    private TokenRequest tokenRequest;
    private StoredCredentials storedCredentials;
    private List<PaymentType> includedPTs;
    private List<PaymentType> excludedPTs;
    private String installationId;
    private String orderContent;
    private List<PaymentMethodAttribute> paymentMethodAttributes;
    private FraudSightData fraudSightData;
    private String deviceSession;
    private PaymentAction action;
    private BranchSpecificExtension branchSpecificExtension;
    private String mandateType;
    private GuaranteedPaymentsData guaranteedPaymentsData;
    private String checkoutId;

    private AuthoriseRequestParameters() {
    }

    public MerchantInfo getMerchantInfo() {
        return merchantInfo;
    }

    public BasicOrderInfo getOrderInfo() {
        return orderInfo;
    }

    public Payment getPayment() {
        return payment;
    }

    public Shopper getShopper() {
        return shopper;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public String getStatementNarrative() {
        return statementNarrative;
    }

    public DynamicInteractionType getDynamicInteractionType() {
        return dynamicInteractionType;
    }

    public OrderLines getOrderLines() {
        return orderLines;
    }

    public Additional3DSData getAdditional3DSData() {
        return additional3DSData;
    }

    public String getPaRes() {
        return paRes;
    }

    public RiskData getRiskData() {
        return riskData;
    }

    public String getAuthenticatedShopperId() {
        return authenticatedShopperId;
    }

    public TokenRequest getTokenRequest() {
        return tokenRequest;
    }

    public List<PaymentType> getIncludedPTs() {
        return includedPTs;
    }

    public void setIncludedPTs(final List<PaymentType> includedPTs) {
        this.includedPTs = includedPTs;
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

    public List<PaymentType> getExcludedPTs() {
        return excludedPTs;
    }

    public void setExcludedPTs(final List<PaymentType> excludedPTs) {
        this.excludedPTs = excludedPTs;
    }

    public StoredCredentials getStoredCredentials() {
        return storedCredentials;
    }

    public void setStoredCredentials(StoredCredentials storedCredentials) {
        this.storedCredentials = storedCredentials;
    }

    public List<PaymentMethodAttribute> getPaymentMethodAttributes() {
        return paymentMethodAttributes;
    }

    public void setPaymentMethodAttributes(List<PaymentMethodAttribute> paymentMethodAttributes) {
        this.paymentMethodAttributes = paymentMethodAttributes;
    }

    public FraudSightData getFraudSightData() {
        return fraudSightData;
    }

    public void setFraudSightData(final FraudSightData fraudSightData) {
        this.fraudSightData = fraudSightData;
    }

    public String getDeviceSession() {
        return deviceSession;
    }

    public void setDeviceSession(final String deviceSession) {
        this.deviceSession = deviceSession;
    }

    public PaymentAction getAction() {
        return action;
    }

    public void setAction(final PaymentAction action) {
        this.action = action;
    }

    public BranchSpecificExtension getBranchSpecificExtension() {
        return branchSpecificExtension;
    }

    public void setBranchSpecificExtension(final BranchSpecificExtension branchSpecificExtension) {
        this.branchSpecificExtension = branchSpecificExtension;
    }

    public String getMandateType() {
        return mandateType;
    }

    public void setMandateType(final String mandateType) {
        this.mandateType = mandateType;
    }

    public GuaranteedPaymentsData getGuaranteedPaymentsData() {
        return guaranteedPaymentsData;
    }

    public void setGuaranteedPaymentsData(GuaranteedPaymentsData guaranteedPaymentsData) {
        this.guaranteedPaymentsData = guaranteedPaymentsData;
    }

    public String getCheckoutId() {
        return checkoutId;
    }

    public void setCheckoutId(String checkoutId) {
        this.checkoutId = checkoutId;
    }

    public interface AuthoriseRequestParametersCreator {
        AuthoriseRequestParametersCreator withOrderLines(OrderLines orderLines);

        AuthoriseRequestParametersCreator withAdditional3DSData(Additional3DSData additional3DSData);

        AuthoriseRequestParametersCreator withPaRes(String paRes);

        AuthoriseRequestParametersCreator withRiskData(RiskData riskData);

        AuthoriseRequestParametersCreator withStoredCredentials(StoredCredentials storedCredentials);

        AuthoriseRequestParametersCreator withAuthenticatedShopperId(String authenticatedShopperId);

        AuthoriseRequestParametersCreator withTokenRequest(TokenRequest tokenRequest);

        AuthoriseRequestParametersCreator withDynamicInteractionType(DynamicInteractionType dynamicInteractionType);

        AuthoriseRequestParametersCreator withStatementNarrative(String statementNarrative);

        AuthoriseRequestParametersCreator withBillingAddress(Address billingAddress);

        AuthoriseRequestParametersCreator withShippingAddress(Address shippingAddress);

        AuthoriseRequestParametersCreator withShopper(Shopper shopper);

        AuthoriseRequestParametersCreator withPayment(Payment payment);

        AuthoriseRequestParametersCreator withOrderInfo(BasicOrderInfo orderInfo);

        AuthoriseRequestParametersCreator withMerchantInfo(MerchantInfo merchantInfo);

        AuthoriseRequestParametersCreator withIncludedPTs(List<PaymentType> includedPTs);

        AuthoriseRequestParametersCreator withInstallationId(String installationId);

        AuthoriseRequestParametersCreator withExcludedPTs(List<PaymentType> excludedPTs);

        AuthoriseRequestParametersCreator withOrderContent(String orderContent);

        AuthoriseRequestParametersCreator withPaymentMethodAttributes(List<PaymentMethodAttribute> paymentMethodAttributes);

        AuthoriseRequestParametersCreator withFraudSightData(FraudSightData fraudSightData);

        AuthoriseRequestParametersCreator withDeviceSession(String deviceSession);

        AuthoriseRequestParametersCreator withPaymentDetailsAction(PaymentAction action);

        AuthoriseRequestParametersCreator withLevel23Data(BranchSpecificExtension branchSpecificExtension);

        AuthoriseRequestParametersCreator withMandateType(String mandateType);

        AuthoriseRequestParametersCreator withGuaranteedPaymentsData(GuaranteedPaymentsData guaranteedPaymentsData);

        AuthoriseRequestParametersCreator withCheckoutId(String checkoutId);

        AuthoriseRequestParameters build();
    }

    public static class AuthoriseRequestParametersBuilder implements AuthoriseRequestParametersCreator {

        private MerchantInfo merchantInfo;
        private BasicOrderInfo orderInfo;
        private Payment payment;
        private Shopper shopper;
        private Address shippingAddress;
        private Address billingAddress;
        private String statementNarrative;
        private DynamicInteractionType dynamicInteractionType;
        private OrderLines orderLines;
        private Additional3DSData additional3DSData;
        private RiskData riskData;
        private String paRes;
        private StoredCredentials storedCredentials;
        private String authenticatedShopperId;
        private TokenRequest tokenRequest;
        private List<PaymentType> includedPTs;
        private String installationId;
        private List<PaymentType> excludedPTs;
        private String orderContent;
        private List<PaymentMethodAttribute> paymentMethodAttributes;
        private FraudSightData fraudSightData;
        private String deviceSession;
        private PaymentAction action;
        private BranchSpecificExtension branchSpecificExtension;
        private String mandateType;
        private GuaranteedPaymentsData guaranteedPaymentsData;
        private String checkoutId;

        private AuthoriseRequestParametersBuilder() {
        }

        /**
         * Getting the instance method
         *
         * @return
         */
        public static AuthoriseRequestParametersCreator getInstance() {
            return new AuthoriseRequestParametersBuilder();
        }

        @Override
        public AuthoriseRequestParametersCreator withMerchantInfo(final MerchantInfo merchantInfo) {
            this.merchantInfo = merchantInfo;
            return this;
        }

        @Override
        public AuthoriseRequestParametersCreator withIncludedPTs(final List<PaymentType> includedPTs) {
            this.includedPTs = includedPTs;
            return this;
        }

        @Override
        public AuthoriseRequestParametersCreator withOrderInfo(final BasicOrderInfo orderInfo) {
            this.orderInfo = orderInfo;
            return this;
        }

        @Override
        public AuthoriseRequestParametersCreator withPayment(final Payment payment) {
            this.payment = payment;
            return this;
        }

        @Override
        public AuthoriseRequestParametersCreator withShopper(final Shopper shopper) {
            this.shopper = shopper;
            return this;
        }

        @Override
        public AuthoriseRequestParametersCreator withShippingAddress(final Address shippingAddress) {
            this.shippingAddress = shippingAddress;
            return this;
        }

        @Override
        public AuthoriseRequestParametersCreator withBillingAddress(final Address billingAddress) {
            this.billingAddress = billingAddress;
            return this;
        }

        @Override
        public AuthoriseRequestParametersCreator withStatementNarrative(final String statementNarrative) {
            this.statementNarrative = statementNarrative;
            return this;
        }

        @Override
        public AuthoriseRequestParametersCreator withDynamicInteractionType(final DynamicInteractionType dynamicInteractionType) {
            this.dynamicInteractionType = dynamicInteractionType;
            return this;
        }

        @Override
        public AuthoriseRequestParametersCreator withOrderLines(final OrderLines orderLines) {
            this.orderLines = orderLines;
            return this;
        }

        @Override
        public AuthoriseRequestParametersCreator withAdditional3DSData(final Additional3DSData additional3DSData) {
            this.additional3DSData = additional3DSData;
            return this;
        }

        @Override
        public AuthoriseRequestParametersCreator withPaRes(final String paRes) {
            this.paRes = paRes;
            return this;
        }

        @Override
        public AuthoriseRequestParametersCreator withRiskData(final RiskData riskData) {
            this.riskData = riskData;
            return this;
        }

        @Override
        public AuthoriseRequestParametersCreator withStoredCredentials(final StoredCredentials storedCredentials) {
            this.storedCredentials = storedCredentials;
            return this;
        }

        @Override
        public AuthoriseRequestParametersCreator withAuthenticatedShopperId(final String authenticatedShopperId) {
            this.authenticatedShopperId = authenticatedShopperId;
            return this;
        }

        @Override
        public AuthoriseRequestParametersCreator withTokenRequest(final TokenRequest tokenRequest) {
            this.tokenRequest = tokenRequest;
            return this;
        }

        @Override
        public AuthoriseRequestParametersCreator withInstallationId(final String installationId) {
            this.installationId = installationId;
            return this;
        }

        @Override
        public AuthoriseRequestParametersCreator withExcludedPTs(final List<PaymentType> excludedPTs) {
            this.excludedPTs = excludedPTs;
            return this;
        }

        @Override
        public AuthoriseRequestParametersCreator withOrderContent(final String orderContent) {
            this.orderContent = orderContent;
            return this;
        }

        @Override
        public AuthoriseRequestParametersCreator withPaymentMethodAttributes(final List<PaymentMethodAttribute> paymentMethodAttributes) {
            this.paymentMethodAttributes = paymentMethodAttributes;
            return this;
        }

        @Override
        public AuthoriseRequestParametersCreator withFraudSightData(final FraudSightData fraudSightData) {
            this.fraudSightData = fraudSightData;
            return this;
        }

        @Override
        public AuthoriseRequestParametersCreator withDeviceSession(final String deviceSession) {
            this.deviceSession = deviceSession;
            return this;
        }

        @Override
        public AuthoriseRequestParametersCreator withPaymentDetailsAction(final PaymentAction action) {
            this.action = action;
            return this;
        }

        @Override
        public AuthoriseRequestParametersCreator withLevel23Data(final BranchSpecificExtension branchSpecificExtension) {
            this.branchSpecificExtension = branchSpecificExtension;
            return this;
        }

        @Override
        public AuthoriseRequestParametersCreator withMandateType(final String mandateType) {
            this.mandateType = mandateType;
            return this;
        }

        @Override
        public AuthoriseRequestParametersCreator withGuaranteedPaymentsData(final GuaranteedPaymentsData guaranteedPaymentsData) {
            this.guaranteedPaymentsData = guaranteedPaymentsData;
            return this;
        }

        @Override
        public AuthoriseRequestParametersCreator withCheckoutId(final String checkoutId) {
            this.checkoutId = checkoutId;
            return this;
        }

        @Override
        public AuthoriseRequestParameters build() {
            final AuthoriseRequestParameters parameters = new AuthoriseRequestParameters();

            parameters.merchantInfo = merchantInfo;
            parameters.orderInfo = orderInfo;
            parameters.payment = payment;
            parameters.shopper = shopper;
            parameters.shippingAddress = shippingAddress;
            parameters.billingAddress = billingAddress;
            parameters.statementNarrative = statementNarrative;
            parameters.dynamicInteractionType = dynamicInteractionType;
            parameters.orderLines = orderLines;
            parameters.additional3DSData = additional3DSData;
            parameters.paRes = paRes;
            parameters.riskData = riskData;
            parameters.storedCredentials = storedCredentials;
            parameters.tokenRequest = tokenRequest;
            parameters.authenticatedShopperId = authenticatedShopperId;
            parameters.includedPTs = includedPTs;
            parameters.installationId = installationId;
            parameters.excludedPTs = excludedPTs;
            parameters.orderContent = orderContent;
            parameters.paymentMethodAttributes = paymentMethodAttributes;
            parameters.fraudSightData = fraudSightData;
            parameters.deviceSession = deviceSession;
            parameters.action = action;
            parameters.branchSpecificExtension = branchSpecificExtension;
            parameters.mandateType = mandateType;
            parameters.guaranteedPaymentsData = guaranteedPaymentsData;
            parameters.checkoutId = checkoutId;

            return parameters;
        }
    }

    @Override
    public String toString() {
        return "AuthoriseRequestParameters{" +
            "merchantInfo=" + merchantInfo +
            ", orderInfo=" + orderInfo +
            ", payment=" + payment +
            ", shopper=" + shopper +
            ", shippingAddress=" + shippingAddress +
            ", billingAddress=" + billingAddress +
            ", statementNarrative='" + statementNarrative + '\'' +
            ", dynamicInteractionType=" + dynamicInteractionType +
            ", orderLines=" + orderLines +
            ", additional3DSData=" + additional3DSData +
            ", riskData=" + riskData +
            ", paRes='" + paRes + '\'' +
            ", authenticatedShopperId='" + authenticatedShopperId + '\'' +
            ", tokenRequest=" + tokenRequest +
            ", storedCredentials=" + storedCredentials +
            ", includedPTs=" + includedPTs +
            ", excludedPTs=" + excludedPTs +
            ", installationId='" + installationId + '\'' +
            ", orderContent='" + orderContent + '\'' +
            ", paymentMethodAttributes=" + paymentMethodAttributes +
            ", fraudSightData='" + fraudSightData + '\'' +
            ", deviceSession='" + deviceSession + '\'' +
            ", level23data='" + branchSpecificExtension + '\'' +
            ", mandateType='" + mandateType + '\'' +
            ", guaranteedPaymentsData='" + guaranteedPaymentsData + '\'' +
            ", checkoutId='" + checkoutId + '\'' +
            ", action=" + action.name() +
            '}';
    }
}
