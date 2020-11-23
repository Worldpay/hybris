package com.worldpay.service.model.threeds2;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.service.request.transform.InternalModelTransformer;

import java.io.Serializable;
import java.util.Optional;

public class RiskData implements InternalModelTransformer, Serializable {

    private TransactionRiskData transactionRiskData;
    private ShopperAccountRiskData shopperAccountRiskData;
    private AuthenticationRiskData authenticationRiskData;

    @Override
    public com.worldpay.internal.model.RiskData transformToInternalModel() throws WorldpayModelTransformationException {
        final var internalRiskData = new com.worldpay.internal.model.RiskData();
        Optional.ofNullable(authenticationRiskData)
            .map(AuthenticationRiskData::transformToInternalModel)
            .ifPresent(internalRiskData::setAuthenticationRiskData);

        Optional.ofNullable(shopperAccountRiskData)
            .map(ShopperAccountRiskData::transformToInternalModel)
            .ifPresent(internalRiskData::setShopperAccountRiskData);

        Optional.ofNullable(transactionRiskData)
            .map(TransactionRiskData::transformToInternalModel)
            .ifPresent(internalRiskData::setTransactionRiskData);

        return internalRiskData;
    }

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
