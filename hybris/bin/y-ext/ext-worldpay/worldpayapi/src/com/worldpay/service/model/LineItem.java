package com.worldpay.service.model;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.helper.InternalModelObject;
import com.worldpay.internal.model.Discount;
import com.worldpay.internal.model.Physical;
import com.worldpay.internal.model.Reference;
import com.worldpay.internal.model.ShippingFee;
import com.worldpay.service.request.transform.InternalModelTransformer;

import java.io.Serializable;

public class LineItem implements InternalModelTransformer, Serializable {
    private String name;
    private String quantity;
    private String quantityUnit;
    private String unitPrice;
    private String taxRate;
    private String totalAmount;
    private String totalTaxAmount;
    private double totalTaxAmountValue;
    private String totalDiscountAmount;
    private LineItemReference lineItemReference;
    private LINE_ITEM_TYPE lineItemType;

    public enum LINE_ITEM_TYPE {PHYSICAL, DISCOUNT, SHIPPING_FEE}

    @Override
    public InternalModelObject transformToInternalModel() throws WorldpayModelTransformationException {
        final com.worldpay.internal.model.LineItem intLineItem = new com.worldpay.internal.model.LineItem();

        intLineItem.setName(name);
        intLineItem.setQuantity(quantity);
        intLineItem.setQuantityUnit(quantityUnit);
        intLineItem.setUnitPrice(unitPrice);
        intLineItem.setTaxRate(taxRate);
        intLineItem.setTotalAmount(totalAmount);
        intLineItem.setTotalTaxAmount(totalTaxAmount);
        intLineItem.setTotalDiscountAmount(totalDiscountAmount);

        if (lineItemReference != null) {
            intLineItem.setReference((Reference) lineItemReference.transformToInternalModel());
        }

        intLineItem.getPhysicalOrDiscountOrShippingFeeOrDigitalOrGiftCardOrSalesTaxTypeOrStoreCreditOrSurcharge().add(getIntLineItemType());
        return intLineItem;
    }

    private InternalModelObject getIntLineItemType() {
        InternalModelObject intLineItemType = null;
        switch (lineItemType) {
            case DISCOUNT:
                intLineItemType = new Discount();
                break;
            case PHYSICAL:
                intLineItemType = new Physical();
                break;
            case SHIPPING_FEE:
                intLineItemType = new ShippingFee();
                break;
        }
        return intLineItemType;
    }

    public LineItemReference getLineItemReference() {
        return lineItemReference;
    }

    public void setLineItemReference(final LineItemReference lineItemReference) {
        this.lineItemReference = lineItemReference;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(final String quantity) {
        this.quantity = quantity;
    }

    public String getQuantityUnit() {
        return quantityUnit;
    }

    public void setQuantityUnit(final String quantityUnit) {
        this.quantityUnit = quantityUnit;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(final String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(final String taxRate) {
        this.taxRate = taxRate;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(final String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getTotalTaxAmount() {
        return totalTaxAmount;
    }

    public void setTotalTaxAmount(final String totalTaxAmount) {
        this.totalTaxAmount = totalTaxAmount;
    }

    public String getTotalDiscountAmount() {
        return totalDiscountAmount;
    }

    public void setTotalDiscountAmount(final String totalDiscountAmount) {
        this.totalDiscountAmount = totalDiscountAmount;
    }

    public LINE_ITEM_TYPE getLineItemType() {
        return lineItemType;
    }

    public void setLineItemType(final LINE_ITEM_TYPE lineItemType) {
        this.lineItemType = lineItemType;
    }

    public double getTotalTaxAmountValue() {
        return totalTaxAmountValue;
    }

    public void setTotalTaxAmountValue(double totalTaxAmountValue) {
        this.totalTaxAmountValue = totalTaxAmountValue;
    }
}
