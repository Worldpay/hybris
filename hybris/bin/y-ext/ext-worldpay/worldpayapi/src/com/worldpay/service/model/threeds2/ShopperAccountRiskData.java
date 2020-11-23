package com.worldpay.service.model.threeds2;

import com.worldpay.internal.model.*;
import com.worldpay.service.model.Date;
import com.worldpay.service.request.transform.InternalModelTransformer;

import java.io.Serializable;
import java.util.Optional;

public class ShopperAccountRiskData implements InternalModelTransformer, Serializable {

    private String shopperAccountAgeIndicator;
    private String transactionsAttemptedLastDay;
    private String shopperAccountPaymentAccountIndicator;
    private String shopperAccountChangeIndicator;
    private String shopperAccountPasswordChangeIndicator;
    private String previousSuspiciousActivity;
    private String addCardAttemptsLastDay;
    private String transactionsAttemptedLastYear;
    private String purchasesCompletedLastSixMonths;
    private String shopperAccountShippingAddressUsageIndicator;
    private String shippingNameMatchesAccountName;

    private Date shopperAccountCreationDate;
    private Date shopperAccountShippingAddressFirstUseDate;
    private Date shopperAccountModificationDate;
    private Date shopperAccountPaymentAccountFirstUseDate;
    private Date shopperAccountPasswordChangeDate;

    @Override
    public com.worldpay.internal.model.ShopperAccountRiskData transformToInternalModel() {
        final com.worldpay.internal.model.ShopperAccountRiskData internalShopperAccountRiskData = new com.worldpay.internal.model.ShopperAccountRiskData();
        internalShopperAccountRiskData.setTransactionsAttemptedLastDay(transactionsAttemptedLastDay);
        internalShopperAccountRiskData.setTransactionsAttemptedLastYear(transactionsAttemptedLastYear);
        internalShopperAccountRiskData.setPurchasesCompletedLastSixMonths(purchasesCompletedLastSixMonths);
        internalShopperAccountRiskData.setAddCardAttemptsLastDay(addCardAttemptsLastDay);
        internalShopperAccountRiskData.setPreviousSuspiciousActivity(previousSuspiciousActivity);
        internalShopperAccountRiskData.setShippingNameMatchesAccountName(shippingNameMatchesAccountName);
        internalShopperAccountRiskData.setShopperAccountAgeIndicator(shopperAccountAgeIndicator);
        internalShopperAccountRiskData.setShopperAccountChangeIndicator(shopperAccountChangeIndicator);
        internalShopperAccountRiskData.setShopperAccountPasswordChangeIndicator(shopperAccountPasswordChangeIndicator);
        internalShopperAccountRiskData.setShopperAccountShippingAddressUsageIndicator(shopperAccountShippingAddressUsageIndicator);
        internalShopperAccountRiskData.setShopperAccountPaymentAccountIndicator(shopperAccountPaymentAccountIndicator);

        Optional.ofNullable(shopperAccountCreationDate)
            .map(this::createShopperAccountCreationDate)
            .ifPresent(internalShopperAccountRiskData::setShopperAccountCreationDate);

        Optional.ofNullable(shopperAccountShippingAddressFirstUseDate)
            .map(this::createShopperAccountShippingAddressFirstUseDate)
            .ifPresent(internalShopperAccountRiskData::setShopperAccountShippingAddressFirstUseDate);

        Optional.ofNullable(shopperAccountModificationDate)
            .map(this::createShopperAccountModificationDate)
            .ifPresent(internalShopperAccountRiskData::setShopperAccountModificationDate);

        Optional.ofNullable(shopperAccountPaymentAccountFirstUseDate)
            .map(this::createShopperAccountPaymentAccountFirstUseDate)
            .ifPresent(internalShopperAccountRiskData::setShopperAccountPaymentAccountFirstUseDate);

        Optional.ofNullable(shopperAccountPasswordChangeDate)
            .map(this::createShopperAccountPasswordChangeDate)
            .ifPresent(internalShopperAccountRiskData::setShopperAccountPasswordChangeDate);

        return internalShopperAccountRiskData;
    }

    private ShopperAccountCreationDate createShopperAccountCreationDate(final Date shopperAccountCreationDate) {
        final ShopperAccountCreationDate internalShopperAccountCreationDate = new ShopperAccountCreationDate();
        internalShopperAccountCreationDate.setDate(shopperAccountCreationDate.transformToInternalModel());
        return internalShopperAccountCreationDate;
    }

    private ShopperAccountModificationDate createShopperAccountModificationDate(final Date shopperAccountModificationDate) {
        final ShopperAccountModificationDate internalShopperAccountModificationDate = new ShopperAccountModificationDate();
        internalShopperAccountModificationDate.setDate(shopperAccountModificationDate.transformToInternalModel());
        return internalShopperAccountModificationDate;
    }

    private ShopperAccountPasswordChangeDate createShopperAccountPasswordChangeDate(final Date shopperAccountPasswordChangeDate) {
        final ShopperAccountPasswordChangeDate internalShopperAccountPasswordChangeDate = new ShopperAccountPasswordChangeDate();
        internalShopperAccountPasswordChangeDate.setDate(shopperAccountPasswordChangeDate.transformToInternalModel());
        return internalShopperAccountPasswordChangeDate;
    }

    private ShopperAccountShippingAddressFirstUseDate createShopperAccountShippingAddressFirstUseDate(final Date shopperAccountShippingAddressFirstUseDate) {
        final ShopperAccountShippingAddressFirstUseDate internalShopperAccountShippingAddressFirstUseDate = new ShopperAccountShippingAddressFirstUseDate();
        internalShopperAccountShippingAddressFirstUseDate.setDate(shopperAccountShippingAddressFirstUseDate.transformToInternalModel());
        return internalShopperAccountShippingAddressFirstUseDate;
    }

    private ShopperAccountPaymentAccountFirstUseDate createShopperAccountPaymentAccountFirstUseDate(final Date shopperAccountPaymentAccountFirstUseDate) {
        final ShopperAccountPaymentAccountFirstUseDate internalShopperAccountPaymentAccountFirstUseDate = new ShopperAccountPaymentAccountFirstUseDate();
        internalShopperAccountPaymentAccountFirstUseDate.setDate(shopperAccountPaymentAccountFirstUseDate.transformToInternalModel());
        return internalShopperAccountPaymentAccountFirstUseDate;
    }

    public String getShopperAccountAgeIndicator() {
        return shopperAccountAgeIndicator;
    }

    public void setShopperAccountAgeIndicator(String shopperAccountAgeIndicator) {
        this.shopperAccountAgeIndicator = shopperAccountAgeIndicator;
    }

    public String getTransactionsAttemptedLastDay() {
        return transactionsAttemptedLastDay;
    }

    public void setTransactionsAttemptedLastDay(String transactionsAttemptedLastDay) {
        this.transactionsAttemptedLastDay = transactionsAttemptedLastDay;
    }

    public String getShopperAccountPaymentAccountIndicator() {
        return shopperAccountPaymentAccountIndicator;
    }

    public void setShopperAccountPaymentAccountIndicator(String shopperAccountPaymentAccountIndicator) {
        this.shopperAccountPaymentAccountIndicator = shopperAccountPaymentAccountIndicator;
    }

    public String getShopperAccountChangeIndicator() {
        return shopperAccountChangeIndicator;
    }

    public void setShopperAccountChangeIndicator(String shopperAccountChangeIndicator) {
        this.shopperAccountChangeIndicator = shopperAccountChangeIndicator;
    }

    public String getShopperAccountPasswordChangeIndicator() {
        return shopperAccountPasswordChangeIndicator;
    }

    public void setShopperAccountPasswordChangeIndicator(String shopperAccountPasswordChangeIndicator) {
        this.shopperAccountPasswordChangeIndicator = shopperAccountPasswordChangeIndicator;
    }

    public String getPreviousSuspiciousActivity() {
        return previousSuspiciousActivity;
    }

    public void setPreviousSuspiciousActivity(String previousSuspiciousActivity) {
        this.previousSuspiciousActivity = previousSuspiciousActivity;
    }

    public String getAddCardAttemptsLastDay() {
        return addCardAttemptsLastDay;
    }

    public void setAddCardAttemptsLastDay(String addCardAttemptsLastDay) {
        this.addCardAttemptsLastDay = addCardAttemptsLastDay;
    }

    public String getTransactionsAttemptedLastYear() {
        return transactionsAttemptedLastYear;
    }

    public void setTransactionsAttemptedLastYear(String transactionsAttemptedLastYear) {
        this.transactionsAttemptedLastYear = transactionsAttemptedLastYear;
    }

    public String getPurchasesCompletedLastSixMonths() {
        return purchasesCompletedLastSixMonths;
    }

    public void setPurchasesCompletedLastSixMonths(String purchasesCompletedLastSixMonths) {
        this.purchasesCompletedLastSixMonths = purchasesCompletedLastSixMonths;
    }

    public String getShopperAccountShippingAddressUsageIndicator() {
        return shopperAccountShippingAddressUsageIndicator;
    }

    public void setShopperAccountShippingAddressUsageIndicator(String shopperAccountShippingAddressUsageIndicator) {
        this.shopperAccountShippingAddressUsageIndicator = shopperAccountShippingAddressUsageIndicator;
    }

    public String getShippingNameMatchesAccountName() {
        return shippingNameMatchesAccountName;
    }

    public void setShippingNameMatchesAccountName(String shippingNameMatchesAccountName) {
        this.shippingNameMatchesAccountName = shippingNameMatchesAccountName;
    }

    public Date getShopperAccountCreationDate() {
        return shopperAccountCreationDate;
    }

    public void setShopperAccountCreationDate(Date shopperAccountCreationDate) {
        this.shopperAccountCreationDate = shopperAccountCreationDate;
    }

    public Date getShopperAccountShippingAddressFirstUseDate() {
        return shopperAccountShippingAddressFirstUseDate;
    }

    public void setShopperAccountShippingAddressFirstUseDate(Date shopperAccountShippingAddressFirstUseDate) {
        this.shopperAccountShippingAddressFirstUseDate = shopperAccountShippingAddressFirstUseDate;
    }

    public Date getShopperAccountModificationDate() {
        return shopperAccountModificationDate;
    }

    public void setShopperAccountModificationDate(Date shopperAccountModificationDate) {
        this.shopperAccountModificationDate = shopperAccountModificationDate;
    }

    public Date getShopperAccountPaymentAccountFirstUseDate() {
        return shopperAccountPaymentAccountFirstUseDate;
    }

    public void setShopperAccountPaymentAccountFirstUseDate(Date shopperAccountPaymentAccountFirstUseDate) {
        this.shopperAccountPaymentAccountFirstUseDate = shopperAccountPaymentAccountFirstUseDate;
    }

    public Date getShopperAccountPasswordChangeDate() {
        return shopperAccountPasswordChangeDate;
    }

    public void setShopperAccountPasswordChangeDate(Date shopperAccountPasswordChangeDate) {
        this.shopperAccountPasswordChangeDate = shopperAccountPasswordChangeDate;
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
