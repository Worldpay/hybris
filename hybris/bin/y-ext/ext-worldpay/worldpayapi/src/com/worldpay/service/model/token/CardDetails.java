package com.worldpay.service.model.token;

import com.worldpay.internal.helper.InternalModelObject;
import com.worldpay.internal.model.*;
import com.worldpay.service.model.Address;
import com.worldpay.service.model.Date;
import com.worldpay.service.request.transform.InternalModelTransformer;

import java.io.Serializable;

public class CardDetails implements InternalModelTransformer, Serializable {

    private String cardHolderName;
    private String cvcNumber;
    private String cardBrand;
    private String cardSubBrand;
    private String issuerCountryCode;
    private String cardNumber;
    private Date expiryDate;
    private Address cardAddress;

    @Override
    public InternalModelObject transformToInternalModel() {
        final com.worldpay.internal.model.CardDetails intCardDetails = new com.worldpay.internal.model.CardDetails();

        if (cvcNumber != null) {
            final Cvc intCvc = new Cvc();
            intCvc.setvalue(cvcNumber);
            intCardDetails.setCvc(intCvc);
        }

        if (cardAddress != null) {
            final CardAddress intCardAddress = new CardAddress();
            intCardAddress.setAddress((com.worldpay.internal.model.Address) cardAddress.transformToInternalModel());
            intCardDetails.setCardAddress(intCardAddress);
        }
        if (cardHolderName != null) {
            final CardHolderName intCardHolderName = new CardHolderName();
            intCardHolderName.setvalue(cardHolderName);
            intCardDetails.setCardHolderName(intCardHolderName);
        }

        if (expiryDate != null) {
            final ExpiryDate intExpiryDate = new ExpiryDate();
            intExpiryDate.setDate((com.worldpay.internal.model.Date) expiryDate.transformToInternalModel());
            intCardDetails.setExpiryDate(intExpiryDate);
        }

        if(cardNumber != null) {
            final Derived intDerived = new Derived();
            intDerived.setObfuscatedPAN(cardNumber);
            intDerived.setIssuerCountryCode(issuerCountryCode);
            intDerived.setCardSubBrand(cardSubBrand);
            intDerived.setCardBrand(cardBrand);
            intCardDetails.setDerived(intDerived);
        }

        return intCardDetails;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getIssuerCountryCode() {
        return issuerCountryCode;
    }

    public void setIssuerCountryCode(String issuerCountryCode) {
        this.issuerCountryCode = issuerCountryCode;
    }

    public String getCardSubBrand() {
        return cardSubBrand;
    }

    public void setCardSubBrand(String cardSubBrand) {
        this.cardSubBrand = cardSubBrand;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(final Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(final String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCvcNumber() {
        return cvcNumber;
    }

    public void setCvcNumber(final String cvcNumber) {
        this.cvcNumber = cvcNumber;
    }

    public Address getCardAddress() {
        return cardAddress;
    }

    public void setCardAddress(final Address cardAddress) {
        this.cardAddress = cardAddress;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public void setCardBrand(final String cardBrand) {
        this.cardBrand = cardBrand;
    }
}
