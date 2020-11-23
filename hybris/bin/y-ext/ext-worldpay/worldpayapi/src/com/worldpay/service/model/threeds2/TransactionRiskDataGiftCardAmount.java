package com.worldpay.service.model.threeds2;

import com.worldpay.service.model.Amount;
import com.worldpay.service.request.transform.InternalModelTransformer;

import java.io.Serializable;

public class TransactionRiskDataGiftCardAmount implements InternalModelTransformer, Serializable {

    private Amount amount;

    @Override
    public com.worldpay.internal.model.TransactionRiskDataGiftCardAmount transformToInternalModel() {
        final com.worldpay.internal.model.TransactionRiskDataGiftCardAmount intTransactionRiskDataGiftCardAmount = new com.worldpay.internal.model.TransactionRiskDataGiftCardAmount();
        intTransactionRiskDataGiftCardAmount.setAmount(amount.transformToInternalModel());
        return intTransactionRiskDataGiftCardAmount;
    }

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
