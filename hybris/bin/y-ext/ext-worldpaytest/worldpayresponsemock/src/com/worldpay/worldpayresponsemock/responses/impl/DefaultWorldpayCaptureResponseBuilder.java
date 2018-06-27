package com.worldpay.worldpayresponsemock.responses.impl;

import com.worldpay.internal.model.*;
import com.worldpay.worldpayresponsemock.responses.WorldpayCaptureResponseBuilder;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayCaptureResponseBuilder implements WorldpayCaptureResponseBuilder {

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentService buildCaptureResponse(PaymentService request) {
        final Modify modify = (Modify) request.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        final OrderModification orderModification = (OrderModification) modify.
                getOrderModificationOrBatchModificationOrAccountBatchModificationOrFuturePayAgreementModificationOrPaymentTokenUpdateOrPaymentTokenDelete().get(0);
        final Capture captureRequest = (Capture) orderModification.
                getCancelOrCaptureOrRefundOrRevokeOrAddBackOfficeCodeOrAuthoriseOrIncreaseAuthorisationOrCancelOrRefundOrDefendOrShopperWebformRefundDetailsOrExtendExpiryDateOrCancelRefund().get(0);

        final CaptureReceived capture = getCaptureReceived(orderModification.getOrderCode(), captureRequest.getAmount());

        final Ok ok = new Ok();
        ok.getCancelReceivedOrVoidReceivedOrCaptureReceivedOrRevokeReceivedOrRefundReceivedOrBackofficeCodeReceivedOrAuthorisationCodeReceivedOrDefenceReceivedOrUpdateTokenReceivedOrDeleteTokenReceivedOrExtendExpiryDateReceivedOrOrderReceived().add(capture);

        final Reply reply = new Reply();
        reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrPaymentOptionOrToken().add(ok);

        final PaymentService paymentService = new PaymentService();
        paymentService.setMerchantCode(request.getMerchantCode());
        paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(reply);
        return paymentService;
    }

    private CaptureReceived getCaptureReceived(final String orderCode, final Amount amount) {
        CaptureReceived capture = new CaptureReceived();
        capture.setOrderCode(orderCode);
        capture.setAmount(amount);
        return capture;
    }
}
