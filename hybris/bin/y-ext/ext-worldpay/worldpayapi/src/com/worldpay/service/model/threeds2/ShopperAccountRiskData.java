package com.worldpay.service.model.threeds2;

import java.io.Serializable;

public class ShopperAccountRiskData implements Serializable {
    private String shopperAccountAgeIndicator;

    private String transactionsAttemptedLastDay;

    private String shopperAccountPaymentAccountIndicator;

    private RiskDateData shopperAccountShippingAddressFirstUseDate;

    private RiskDateData shopperAccountModificationDate;

    private String shopperAccountChangeIndicator;

    private String shopperAccountPasswordChangeIndicator;

    private RiskDateData shopperAccountPaymentAccountFirstUseDate;

    private String previousSuspiciousActivity;

    private RiskDateData shopperAccountPasswordChangeDate;

    private String addCardAttemptsLastDay;

    private String transactionsAttemptedLastYear;

    private String purchasesCompletedLastSixMonths;

    private String shopperAccountShippingAddressUsageIndicator;

    private RiskDateData shopperAccountCreationDate;

    private String shippingNameMatchesAccountName;

    public String getShopperAccountAgeIndicator() {
        return shopperAccountAgeIndicator;
    }

    public void setShopperAccountAgeIndicator(final String shopperAccountAgeIndicator) {
        this.shopperAccountAgeIndicator = shopperAccountAgeIndicator;
    }

    public String getTransactionsAttemptedLastDay() {
        return transactionsAttemptedLastDay;
    }

    public void setTransactionsAttemptedLastDay(final String transactionsAttemptedLastDay) {
        this.transactionsAttemptedLastDay = transactionsAttemptedLastDay;
    }

    public String getShopperAccountPaymentAccountIndicator() {
        return shopperAccountPaymentAccountIndicator;
    }

    public void setShopperAccountPaymentAccountIndicator(final String shopperAccountPaymentAccountIndicator) {
        this.shopperAccountPaymentAccountIndicator = shopperAccountPaymentAccountIndicator;
    }

    public RiskDateData getShopperAccountShippingAddressFirstUseDate() {
        return shopperAccountShippingAddressFirstUseDate;
    }

    public void setShopperAccountShippingAddressFirstUseDate(final RiskDateData shopperAccountShippingAddressFirstUseDate) {
        this.shopperAccountShippingAddressFirstUseDate = shopperAccountShippingAddressFirstUseDate;
    }

    public RiskDateData getShopperAccountModificationDate() {
        return shopperAccountModificationDate;
    }

    public void setShopperAccountModificationDate(final RiskDateData shopperAccountModificationDate) {
        this.shopperAccountModificationDate = shopperAccountModificationDate;
    }

    public String getShopperAccountChangeIndicator() {
        return shopperAccountChangeIndicator;
    }

    public void setShopperAccountChangeIndicator(final String shopperAccountChangeIndicator) {
        this.shopperAccountChangeIndicator = shopperAccountChangeIndicator;
    }

    public String getShopperAccountPasswordChangeIndicator() {
        return shopperAccountPasswordChangeIndicator;
    }

    public void setShopperAccountPasswordChangeIndicator(final String shopperAccountPasswordChangeIndicator) {
        this.shopperAccountPasswordChangeIndicator = shopperAccountPasswordChangeIndicator;
    }

    public RiskDateData getShopperAccountPaymentAccountFirstUseDate() {
        return shopperAccountPaymentAccountFirstUseDate;
    }

    public void setShopperAccountPaymentAccountFirstUseDate(final RiskDateData shopperAccountPaymentAccountFirstUseDate) {
        this.shopperAccountPaymentAccountFirstUseDate = shopperAccountPaymentAccountFirstUseDate;
    }

    public String getPreviousSuspiciousActivity() {
        return previousSuspiciousActivity;
    }

    public void setPreviousSuspiciousActivity(final String previousSuspiciousActivity) {
        this.previousSuspiciousActivity = previousSuspiciousActivity;
    }

    public RiskDateData getShopperAccountPasswordChangeDate() {
        return shopperAccountPasswordChangeDate;
    }

    public void setShopperAccountPasswordChangeDate(final RiskDateData shopperAccountPasswordChangeDate) {
        this.shopperAccountPasswordChangeDate = shopperAccountPasswordChangeDate;
    }

    public String getAddCardAttemptsLastDay() {
        return addCardAttemptsLastDay;
    }

    public void setAddCardAttemptsLastDay(final String addCardAttemptsLastDay) {
        this.addCardAttemptsLastDay = addCardAttemptsLastDay;
    }

    public String getTransactionsAttemptedLastYear() {
        return transactionsAttemptedLastYear;
    }

    public void setTransactionsAttemptedLastYear(final String transactionsAttemptedLastYear) {
        this.transactionsAttemptedLastYear = transactionsAttemptedLastYear;
    }

    public String getPurchasesCompletedLastSixMonths() {
        return purchasesCompletedLastSixMonths;
    }

    public void setPurchasesCompletedLastSixMonths(final String purchasesCompletedLastSixMonths) {
        this.purchasesCompletedLastSixMonths = purchasesCompletedLastSixMonths;
    }

    public String getShopperAccountShippingAddressUsageIndicator() {
        return shopperAccountShippingAddressUsageIndicator;
    }

    public void setShopperAccountShippingAddressUsageIndicator(final String shopperAccountShippingAddressUsageIndicator) {
        this.shopperAccountShippingAddressUsageIndicator = shopperAccountShippingAddressUsageIndicator;
    }

    public RiskDateData getShopperAccountCreationDate() {
        return shopperAccountCreationDate;
    }

    public void setShopperAccountCreationDate(final RiskDateData shopperAccountCreationDate) {
        this.shopperAccountCreationDate = shopperAccountCreationDate;
    }

    public String getShippingNameMatchesAccountName() {
        return shippingNameMatchesAccountName;
    }

    public void setShippingNameMatchesAccountName(final String shippingNameMatchesAccountName) {
        this.shippingNameMatchesAccountName = shippingNameMatchesAccountName;
    }

    @Override
    public String toString() {
        return "ShopperAccountRiskData{" +
                "shopperAccountAgeIndicator='" + shopperAccountAgeIndicator + '\'' +
                ", transactionsAttemptedLastDay='" + transactionsAttemptedLastDay + '\'' +
                ", shopperAccountPaymentAccountIndicator='" + shopperAccountPaymentAccountIndicator + '\'' +
                ", shopperAccountShippingAddressFirstUseDate=" + shopperAccountShippingAddressFirstUseDate +
                ", shopperAccountModificationDate=" + shopperAccountModificationDate +
                ", shopperAccountChangeIndicator='" + shopperAccountChangeIndicator + '\'' +
                ", shopperAccountPasswordChangeIndicator='" + shopperAccountPasswordChangeIndicator + '\'' +
                ", shopperAccountPaymentAccountFirstUseDate=" + shopperAccountPaymentAccountFirstUseDate +
                ", previousSuspiciousActivity='" + previousSuspiciousActivity + '\'' +
                ", shopperAccountPasswordChangeDate=" + shopperAccountPasswordChangeDate +
                ", addCardAttemptsLastDay='" + addCardAttemptsLastDay + '\'' +
                ", transactionsAttemptedLastYear='" + transactionsAttemptedLastYear + '\'' +
                ", purchasesCompletedLastSixMonths='" + purchasesCompletedLastSixMonths + '\'' +
                ", shopperAccountShippingAddressUsageIndicator='" + shopperAccountShippingAddressUsageIndicator + '\'' +
                ", shopperAccountCreationDate=" + shopperAccountCreationDate +
                ", shippingNameMatchesAccountName='" + shippingNameMatchesAccountName + '\'' +
                '}';
    }
}
