package com.worldpay.service.model.payment;

import com.worldpay.enums.payment.storedCredentials.MerchantInitiatedReason;
import com.worldpay.enums.payment.storedCredentials.Usage;
import com.worldpay.service.request.transform.InternalModelTransformer;

import java.io.Serializable;
import java.util.Optional;

public class StoredCredentials implements InternalModelTransformer, Serializable {

    private MerchantInitiatedReason merchantInitiatedReason;
    private String schemeTransactionIdentifier;
    private Usage usage;

    public StoredCredentials() {
    }

    public StoredCredentials(final MerchantInitiatedReason merchantInitiatedReason, final String schemeTransactionIdentifier, final Usage usage) {
        this.merchantInitiatedReason = merchantInitiatedReason;
        this.schemeTransactionIdentifier = schemeTransactionIdentifier;
        this.usage = usage;
    }

    @Override
    public com.worldpay.internal.model.StoredCredentials transformToInternalModel() {
        final com.worldpay.internal.model.StoredCredentials intStoredCredentials = new com.worldpay.internal.model.StoredCredentials();

        Optional.ofNullable(merchantInitiatedReason)
                .map(MerchantInitiatedReason::name)
                .ifPresent(intStoredCredentials::setMerchantInitiatedReason);
        Optional.ofNullable(usage)
                .map(Usage::name)
                .ifPresent(intStoredCredentials::setUsage);

        intStoredCredentials.setSchemeTransactionIdentifier(schemeTransactionIdentifier);
        return intStoredCredentials;
    }

    public void setMerchantInitiatedReason(final MerchantInitiatedReason merchantInitiatedReason) {
        this.merchantInitiatedReason = merchantInitiatedReason;
    }

    public void setSchemeTransactionIdentifier(final String schemeTransactionIdentifier) {
        this.schemeTransactionIdentifier = schemeTransactionIdentifier;
    }

    public void setUsage(final Usage usage) {
        this.usage = usage;
    }

    public MerchantInitiatedReason getMerchantInitiatedReason() {
        return merchantInitiatedReason;
    }

    public String getSchemeTransactionIdentifier() {
        return schemeTransactionIdentifier;
    }

    public Usage getUsage() {
        return usage;
    }

}
