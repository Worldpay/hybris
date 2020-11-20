package com.worldpay.service.model.threeds2;

import com.worldpay.internal.model.TransactionRiskDataPreOrderDate;
import com.worldpay.service.model.Date;
import com.worldpay.service.request.transform.InternalModelTransformer;

import java.io.Serializable;
import java.util.Optional;

public class TransactionRiskData implements InternalModelTransformer, Serializable {

    private String deliveryTimeframe;
    private String deliveryEmailAddress;
    private String preOrderPurchase;
    private String shippingMethod;
    private String giftCardCount;
    private String reorderingPreviousPurchases;

    private Date transactionRiskDataPreOrderDate;
    private TransactionRiskDataGiftCardAmount transactionRiskDataGiftCardAmount;

    @Override
    public com.worldpay.internal.model.TransactionRiskData transformToInternalModel() {
        final var intTransactionRiskData = new com.worldpay.internal.model.TransactionRiskData();
        intTransactionRiskData.setDeliveryEmailAddress(deliveryEmailAddress);
        intTransactionRiskData.setDeliveryTimeframe(deliveryTimeframe);
        intTransactionRiskData.setGiftCardCount(giftCardCount);
        intTransactionRiskData.setPreOrderPurchase(preOrderPurchase);
        intTransactionRiskData.setReorderingPreviousPurchases(reorderingPreviousPurchases);
        intTransactionRiskData.setShippingMethod(shippingMethod);

        Optional.ofNullable(transactionRiskDataPreOrderDate)
            .map(Date::transformToInternalModel)
            .map(this::createTransactionRiskDataPreorderDate)
            .ifPresent(intTransactionRiskData::setTransactionRiskDataPreOrderDate);

        Optional.ofNullable(transactionRiskDataGiftCardAmount)
            .map(TransactionRiskDataGiftCardAmount::transformToInternalModel)
            .ifPresent(intTransactionRiskData::setTransactionRiskDataGiftCardAmount);

        return intTransactionRiskData;
    }

    private TransactionRiskDataPreOrderDate createTransactionRiskDataPreorderDate(final com.worldpay.internal.model.Date transactionDataPreorderDate) {
        final TransactionRiskDataPreOrderDate intTransactionRiskDataPreOrderDate = new TransactionRiskDataPreOrderDate();
        intTransactionRiskDataPreOrderDate.setDate(transactionDataPreorderDate);
        return intTransactionRiskDataPreOrderDate;
    }

    public String getDeliveryTimeframe() {
        return deliveryTimeframe;
    }

    public void setDeliveryTimeframe(final String deliveryTimeframe) {
        this.deliveryTimeframe = deliveryTimeframe;
    }

    public String getDeliveryEmailAddress() {
        return deliveryEmailAddress;
    }

    public void setDeliveryEmailAddress(final String deliveryEmailAddress) {
        this.deliveryEmailAddress = deliveryEmailAddress;
    }

    public String getPreOrderPurchase() {
        return preOrderPurchase;
    }

    public void setPreOrderPurchase(final String preOrderPurchase) {
        this.preOrderPurchase = preOrderPurchase;
    }

    public String getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(final String shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public Date getTransactionRiskDataPreOrderDate() {
        return transactionRiskDataPreOrderDate;
    }

    public void setTransactionRiskDataPreOrderDate(final Date transactionRiskDataPreOrderDate) {
        this.transactionRiskDataPreOrderDate = transactionRiskDataPreOrderDate;
    }

    public String getGiftCardCount() {
        return giftCardCount;
    }

    public void setGiftCardCount(final String giftCardCount) {
        this.giftCardCount = giftCardCount;
    }

    public TransactionRiskDataGiftCardAmount getTransactionRiskDataGiftCardAmount() {
        return transactionRiskDataGiftCardAmount;
    }

    public void setTransactionRiskDataGiftCardAmount(final TransactionRiskDataGiftCardAmount transactionRiskDataGiftCardAmount) {
        this.transactionRiskDataGiftCardAmount = transactionRiskDataGiftCardAmount;
    }

    public String getReorderingPreviousPurchases() {
        return reorderingPreviousPurchases;
    }

    public void setReorderingPreviousPurchases(final String reorderingPreviousPurchases) {
        this.reorderingPreviousPurchases = reorderingPreviousPurchases;
    }

    @Override
    public String toString() {
        return "TransactionRiskData{" +
                "deliveryTimeframe='" + deliveryTimeframe + '\'' +
                ", deliveryEmailAddress='" + deliveryEmailAddress + '\'' +
                ", preOrderPurchase='" + preOrderPurchase + '\'' +
                ", shippingMethod='" + shippingMethod + '\'' +
                ", transactionRiskDataPreOrderDate=" + transactionRiskDataPreOrderDate +
                ", giftCardCount='" + giftCardCount + '\'' +
                ", transactionRiskDataGiftCardAmount=" + transactionRiskDataGiftCardAmount +
                ", reorderingPreviousPurchases='" + reorderingPreviousPurchases + '\'' +
                '}';
    }


}
