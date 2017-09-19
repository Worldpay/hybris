package com.worldpay.service.model.payment;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.helper.InternalModelObject;
import com.worldpay.service.model.Amount;
import com.worldpay.service.request.transform.InternalModelTransformer;

import java.io.Serializable;

/**
 * POJO representation of pay as order payment
 */
public class PayAsOrder implements InternalModelTransformer, Serializable {

    private String originalMerchantCode;
    private String originalOrderCode;
    private Amount amount;
    private String cvc;

    /**
     * Constructor with full list of fields
     *
     * @param originalMerchantCode
     * @param originalOrderCode
     * @param amount
     * @param cvc
     */
    public PayAsOrder(String originalMerchantCode, String originalOrderCode, Amount amount, String cvc) {
        this.originalMerchantCode = originalMerchantCode;
        this.originalOrderCode = originalOrderCode;
        this.amount = amount;
        this.cvc = cvc;
    }

    @Override
    public InternalModelObject transformToInternalModel() throws WorldpayModelTransformationException {
        com.worldpay.internal.model.PayAsOrder intPayAsOrder = new com.worldpay.internal.model.PayAsOrder();
        if (originalMerchantCode != null) {
            intPayAsOrder.setMerchantCode(originalMerchantCode);
        }
        if (originalOrderCode != null) {
            intPayAsOrder.setOrderCode(originalOrderCode);
        }
        if (amount != null) {
            intPayAsOrder.setAmount((com.worldpay.internal.model.Amount) amount.transformToInternalModel());
        }
        if (cvc != null) {
            intPayAsOrder.setCvc(cvc);
        }

        return intPayAsOrder;
    }

    public String getOriginalMerchantCode() {
        return originalMerchantCode;
    }

    public void setOriginalMerchantCode(String originalMerchantCode) {
        this.originalMerchantCode = originalMerchantCode;
    }

    public String getOriginalOrderCode() {
        return originalOrderCode;
    }

    public void setOriginalOrderCode(String originalOrderCode) {
        this.originalOrderCode = originalOrderCode;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }

    /**
     * (non-Javadoc)
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "PayAsOrder [originalMerchantCode=" + originalMerchantCode + ", originalOrderCode=" + originalOrderCode + ", amount=" + amount + ", cvc=" + cvc
                + "]";
    }
}
