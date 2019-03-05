package com.worldpay.service.model.applepay;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.helper.InternalModelObject;
import com.worldpay.service.request.transform.InternalModelTransformer;

import java.io.Serializable;

public class Header implements InternalModelTransformer, Serializable {

    private String ephemeralPublicKey;
    private String publicKeyHash;
    private String transactionId;
    private String applicationData;

    public Header(final String ephemeralPublicKey, final String publicKeyHash, final String transactionId, final String applicationData) {
        this.ephemeralPublicKey = ephemeralPublicKey;
        this.publicKeyHash = publicKeyHash;
        this.transactionId = transactionId;
        this.applicationData = applicationData;
    }

    @Override
    public InternalModelObject transformToInternalModel() throws WorldpayModelTransformationException {
        com.worldpay.internal.model.Header intHeader = new com.worldpay.internal.model.Header();
        intHeader.setApplicationData(applicationData);
        intHeader.setEphemeralPublicKey(ephemeralPublicKey);
        intHeader.setPublicKeyHash(publicKeyHash);
        intHeader.setTransactionId(transactionId);
        return intHeader;
    }
}
