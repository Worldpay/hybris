package com.worldpay.service.model.threeds2;

import com.worldpay.service.model.Amount;

import java.io.Serializable;

public class TransactionRiskDataGiftCardAmount implements Serializable {

    private Amount amount;

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(final Amount amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "TransactionRiskDataGiftCardAmount{" +
                "amount=" + amount +
                '}';
    }
}
