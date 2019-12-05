package com.worldpay.service.request;

import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.service.model.*;
import com.worldpay.service.model.payment.Payment;
import com.worldpay.service.model.threeds2.Additional3DSData;
import com.worldpay.service.model.threeds2.RiskData;

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
                '}';
    }

    public interface ParamMerchantInfo {
        ParamOrderInfo withMerchantInfo(MerchantInfo merchantInfo);
    }

    public interface ParamOrderInfo {
        ParamPayment withOrderInfo(BasicOrderInfo orderInfo);
    }

    public interface ParamPayment {
        ParamShopper withPayment(Payment payment);
    }

    public interface ParamShopper {
        ParamShippingAddress withShopper(Shopper shopper);
    }

    public interface ParamShippingAddress {
        ParamBillingAddress withShippingAddress(Address shippingAddress);
    }

    public interface ParamBillingAddress {
        ParamStatementNarrative withBillingAddress(Address billingAddress);
    }

    public interface ParamStatementNarrative {
        ParamDynamicInteractionType withStatementNarrative(String statementNarrative);
    }

    public interface ParamDynamicInteractionType {
        AuthoriseRequestParametersCreator withDynamicInteractionType(DynamicInteractionType dynamicInteractionType);
    }

    public interface AuthoriseRequestParametersCreator {
        AuthoriseRequestParametersCreator withOrderLines(OrderLines orderLines);
        AuthoriseRequestParametersCreator withAdditional3DSData(Additional3DSData additional3DSData);
        AuthoriseRequestParametersCreator withPaRes(String paRes);
        AuthoriseRequestParametersCreator withRiskData(RiskData riskData);

        AuthoriseRequestParameters build();
    }

    public static class AuthoriseRequestParametersBuilder implements ParamDynamicInteractionType, ParamShippingAddress, ParamBillingAddress,
            ParamStatementNarrative, ParamShopper, ParamPayment, ParamMerchantInfo, ParamOrderInfo, AuthoriseRequestParametersCreator {

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

        private AuthoriseRequestParametersBuilder() {
        }

        /**
         * Getting the instance method
         *
         * @return
         */
        public static ParamMerchantInfo getInstance() {
            return new AuthoriseRequestParametersBuilder();
        }

        @Override
        public ParamOrderInfo withMerchantInfo(final MerchantInfo merchantInfo) {
            this.merchantInfo = merchantInfo;
            return this;
        }

        @Override
        public ParamPayment withOrderInfo(final BasicOrderInfo orderInfo) {
            this.orderInfo = orderInfo;
            return this;
        }

        @Override
        public ParamShopper withPayment(final Payment payment) {
            this.payment = payment;
            return this;
        }

        @Override
        public ParamShippingAddress withShopper(final Shopper shopper) {
            this.shopper = shopper;
            return this;
        }

        @Override
        public ParamBillingAddress withShippingAddress(final Address shippingAddress) {
            this.shippingAddress = shippingAddress;
            return this;
        }

        @Override
        public ParamStatementNarrative withBillingAddress(final Address billingAddress) {
            this.billingAddress = billingAddress;
            return this;
        }

        @Override
        public ParamDynamicInteractionType withStatementNarrative(final String statementNarrative) {
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
        public AuthoriseRequestParameters build() {
            AuthoriseRequestParameters parameters = new AuthoriseRequestParameters();

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

            return parameters;
        }
    }
}
