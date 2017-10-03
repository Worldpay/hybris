package com.worldpay.service.request;

import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.MerchantInfo;

/**
 * This class represents the details that must be passed to a call to {@link WorldpayServiceGateway#refund(RefundServiceRequest) refund()} in the
 * WorldpayServiceGateway
 * <p/>
 * <p>On top of the standard parameters it provides the amount that needs to be sent</p>
 */
public class RefundServiceRequest extends AbstractServiceRequest {

    private Amount amount;
    private String reference;
    private Boolean shopperWebformRefund;

    protected RefundServiceRequest(final MerchantInfo merchantInfo, final String orderCode) {
        super(merchantInfo, orderCode);
    }

    /**
     * Static convenience method for creating an instance of the RefundServiceRequest
     *
     * @param merch     merchantInfo to be used in the Worldpay call
     * @param orderCode orderCode to be used in the Worldpay call
     * @param amount    amount to be used in the Worldpay call
     * @return new instance of the RefundServiceRequest initialised with input parameters
     */
    public static RefundServiceRequest createRefundRequest(final MerchantInfo merch,
                                                           final String orderCode, final Amount amount,
                                                           final String reference, final Boolean shopperWebformRefund) {
        if (merch == null || orderCode == null || amount == null) {
            throw new IllegalArgumentException("WorldpayConfig, MerchantInfo, Order Code and Amount cannot be null");
        }
        RefundServiceRequest refundRequest = new RefundServiceRequest(merch, orderCode);
        refundRequest.setAmount(amount);
        refundRequest.setReference(reference);
        refundRequest.setShopperWebformRefund(shopperWebformRefund);
        return refundRequest;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public Boolean getShopperWebformRefund() {
        return shopperWebformRefund;
    }

    public void setShopperWebformRefund(final Boolean shopperWebformRefund) {
        this.shopperWebformRefund = shopperWebformRefund;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }
}
