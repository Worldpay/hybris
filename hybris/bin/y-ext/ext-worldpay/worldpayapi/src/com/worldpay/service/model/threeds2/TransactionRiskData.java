package com.worldpay.service.model.threeds2;

import java.io.Serializable;

public class TransactionRiskData implements Serializable {
    private String deliveryTimeframe;

    private String deliveryEmailAddress;

    private String preOrderPurchase;

    private String shippingMethod;

    private RiskDateData transactionRiskDataPreOrderDate;

    private String giftCardCount;

    private TransactionRiskDataGiftCardAmount transactionRiskDataGiftCardAmount;

    private String reorderingPreviousPurchases;

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

    public RiskDateData getTransactionRiskDataPreOrderDate() {
        return transactionRiskDataPreOrderDate;
    }

    public void setTransactionRiskDataPreOrderDate(final RiskDateData transactionRiskDataPreOrderDate) {
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
