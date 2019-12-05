package com.worldpay.service.model;

import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.service.model.payment.Card;

import java.io.Serializable;

/**
 * POJO representation of the payment returned in a reply message
 */
public class PaymentReply implements Serializable {

    private String methodCode;
    private Amount amount;
    private AuthorisedStatus authStatus;
    private String cvcResultDescription;
    private String balanceAccountType;
    private Amount balanceAmount;
    private String cardNumber;
    private String returnCode;
    private RiskScore riskScore;
    private String cardHolderName;
    private Card cardDetails;
    private String aavAddressResultCode;
    private String aavCardholderNameResultCode;
    private String aavEmailResultCode;
    private String aavPostcodeResultCode;
    private String aavTelephoneResultCode;
    private String refundReference;
    private String authorisationId;
    private String authorisedBy;
    private String threeDSecureResultDescription;

    public Card getCardDetails() {
        return cardDetails;
    }

    public void setCardDetails(final Card cardDetails) {
        this.cardDetails = cardDetails;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getMethodCode() {
        return methodCode;
    }

    public void setMethodCode(String methodCode) {
        this.methodCode = methodCode;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public AuthorisedStatus getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(AuthorisedStatus authStatus) {
        this.authStatus = authStatus;
    }

    public String getCvcResultDescription() {
        return cvcResultDescription;
    }

    public void setCvcResultDescription(String cvcResultDescription) {
        this.cvcResultDescription = cvcResultDescription;
    }

    public String getBalanceAccountType() {
        return balanceAccountType;
    }

    public void setBalanceAccountType(String balanceAccountType) {
        this.balanceAccountType = balanceAccountType;
    }

    public Amount getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(Amount balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public RiskScore getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(RiskScore riskScore) {
        this.riskScore = riskScore;
    }

    public String getAavAddressResultCode() {
        return aavAddressResultCode;
    }

    public void setAavAddressResultCode(final String aavAddressResultCode) {
        this.aavAddressResultCode = aavAddressResultCode;
    }

    public String getAavCardholderNameResultCode() {
        return aavCardholderNameResultCode;
    }

    public void setAavCardholderNameResultCode(final String aavCardholderNameResultCode) {
        this.aavCardholderNameResultCode = aavCardholderNameResultCode;
    }

    public String getAavEmailResultCode() {
        return aavEmailResultCode;
    }

    public void setAavEmailResultCode(final String aavEmailResultCode) {
        this.aavEmailResultCode = aavEmailResultCode;
    }

    public String getAavPostcodeResultCode() {
        return aavPostcodeResultCode;
    }

    public void setAavPostcodeResultCode(final String aavPostcodeResultCode) {
        this.aavPostcodeResultCode = aavPostcodeResultCode;
    }

    public String getAavTelephoneResultCode() {
        return aavTelephoneResultCode;
    }

    public void setAavTelephoneResultCode(final String aavTelephoneResultCode) {
        this.aavTelephoneResultCode = aavTelephoneResultCode;
    }

    public String getRefundReference() {
        return refundReference;
    }

    public void setRefundReference(final String refundReference) {
        this.refundReference = refundReference;
    }

    public String getAuthorisationId() {
        return authorisationId;
    }

    public void setAuthorisationId(String authorisationId) {
        this.authorisationId = authorisationId;
    }

    public String getAuthorisedBy() {
        return authorisedBy;
    }

    public void setAuthorisedBy(String authorisedBy) {
        this.authorisedBy = authorisedBy;
    }

    public String getThreeDSecureResultDescription() {
        return threeDSecureResultDescription;
    }

    public void setThreeDSecureResultDescription(final String threeDSecureResultDescription) {
        this.threeDSecureResultDescription = threeDSecureResultDescription;
    }

    @Override
    public String toString() {
        return "PaymentReply{" +
                "methodCode='" + methodCode + '\'' +
                ", amount=" + amount +
                ", authStatus=" + authStatus +
                ", cvcResultDescription='" + cvcResultDescription + '\'' +
                ", balanceAccountType='" + balanceAccountType + '\'' +
                ", balanceAmount=" + balanceAmount +
                ", cardNumber='" + cardNumber + '\'' +
                ", returnCode='" + returnCode + '\'' +
                ", riskScore=" + riskScore +
                ", cardHolderName='" + cardHolderName + '\'' +
                ", cardDetails=" + cardDetails +
                ", aavAddressResultCode='" + aavAddressResultCode + '\'' +
                ", aavCardholderNameResultCode='" + aavCardholderNameResultCode + '\'' +
                ", aavEmailResultCode='" + aavEmailResultCode + '\'' +
                ", aavPostcodeResultCode='" + aavPostcodeResultCode + '\'' +
                ", aavTelephoneResultCode='" + aavTelephoneResultCode + '\'' +
                ", refundReference='" + refundReference + '\'' +
                ", authorisationId='" + authorisationId + '\'' +
                ", authorisedBy='" + authorisedBy + '\'' +
                ", threeDSecureResultDescription='" + threeDSecureResultDescription + '\'' +
                '}';
    }
}
