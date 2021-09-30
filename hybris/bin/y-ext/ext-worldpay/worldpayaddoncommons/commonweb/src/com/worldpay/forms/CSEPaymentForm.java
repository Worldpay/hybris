package com.worldpay.forms;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class CSEPaymentForm {

    private boolean termsCheck;
    private boolean saveInAccount;
    private String cseToken;
    private String cvc;
    private String securityCode;
    private String nameOnCard;
    private String expiryYear;
    private String expiryMonth;
    private String referenceId;
    private String windowSizePreference;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;
    private String deviceSession;
    private boolean dobRequired;

    public String getCseToken() {
        return cseToken;
    }

    public void setCseToken(final String cseToken) {
        this.cseToken = cseToken;
    }

    public boolean isTermsCheck() {
        return termsCheck;
    }

    public void setTermsCheck(final boolean termsCheck) {
        this.termsCheck = termsCheck;
    }

    public boolean isSaveInAccount() {
        return saveInAccount;
    }

    public void setSaveInAccount(final boolean saveInAccount) {
        this.saveInAccount = saveInAccount;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(final String cvc) {
        this.cvc = cvc;
    }

    public String getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(final String expiryYear) {
        this.expiryYear = expiryYear;
    }

    public String getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(final String expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public String getNameOnCard() {
        return nameOnCard;
    }

    public void setNameOnCard(final String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(final String referenceId) {
        this.referenceId = referenceId;
    }

    public String getWindowSizePreference() {
        return windowSizePreference;
    }

    public void setWindowSizePreference(final String windowSizePreference) {
        this.windowSizePreference = windowSizePreference;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(final String securityCode) {
        this.securityCode = securityCode;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(final Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDeviceSession() {
        return deviceSession;
    }

    public void setDeviceSession(final String deviceSession) {
        this.deviceSession = deviceSession;
    }

    public boolean isDobRequired() {
        return dobRequired;
    }

    public void setDobRequired(final boolean dobRequired) {
        this.dobRequired = dobRequired;
    }
}
