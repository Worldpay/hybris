package com.worldpay.service.model;

import java.io.Serializable;

/**
 * POJO representation of the basic order information
 */
public class BasicOrderInfo implements Serializable {

    private String orderCode;
    private String description;
    private Amount amount;


    /**
     * Constructor with full list of fields
     *
     * @param orderCode
     * @param description
     * @param amount
     */
    public BasicOrderInfo(String orderCode, String description, Amount amount) {
        this.orderCode = orderCode;
        this.description = description;
        this.amount = amount;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    /**
     * (non-Javadoc)
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "BasicOrderInfo [orderCode=" + orderCode + ", description=" + description + ", amount=" + amount + "]";
    }
}
