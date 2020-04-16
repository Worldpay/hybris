package com.worldpay.service.model.threeds2;

import java.io.Serializable;

public class RiskData implements Serializable {
    private TransactionRiskData transactionRiskData;

    private ShopperAccountRiskData shopperAccountRiskData;

    private AuthenticationRiskData authenticationRiskData;

    public TransactionRiskData getTransactionRiskData() {
        return transactionRiskData;
    }

    public void setTransactionRiskData(TransactionRiskData transactionRiskData) {
        this.transactionRiskData = transactionRiskData;
    }

    public ShopperAccountRiskData getShopperAccountRiskData() {
        return shopperAccountRiskData;
    }

    public void setShopperAccountRiskData(ShopperAccountRiskData shopperAccountRiskData) {
        this.shopperAccountRiskData = shopperAccountRiskData;
    }

    public AuthenticationRiskData getAuthenticationRiskData() {
        return authenticationRiskData;
    }

    public void setAuthenticationRiskData(AuthenticationRiskData authenticationRiskData) {
        this.authenticationRiskData = authenticationRiskData;
    }

    @Override
    public String toString() {
        return "RiskData{" +
                "transactionRiskData=" + transactionRiskData +
                ", shopperAccountRiskData=" + shopperAccountRiskData +
                ", authenticationRiskData=" + authenticationRiskData +
                '}';
    }
}
