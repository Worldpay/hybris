package com.worldpay.service.model;

import java.io.Serializable;

/**
 * POJO representation of an AccountTx returned in a notification message
 */
public class AccountTransaction implements Serializable {

    private String accountType;
    private String batchId;
    private Amount amount;

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
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
        return "AccountTransaction [accountType=" + accountType + ", batchId=" + batchId + ", amount=" + amount + "]";
    }
}
