package com.worldpay.service.response.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.Ok;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.RefundReceived;
import com.worldpay.internal.model.Reply;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.DebitCreditIndicator;
import com.worldpay.service.response.RefundServiceResponse;
import com.worldpay.service.response.ServiceResponse;

/**
 * Specific class for transforming a {@link PaymentService} into an {@link RefundServiceResponse} object
 */
public class RefundResponseTransformer extends AbstractServiceResponseTransformer {

    /* (non-Javadoc)
     * @see AbstractServiceResponseTransformer#transform(com.worldpay.internal.model.PaymentService)
     */
    @Override
    public ServiceResponse transform(PaymentService reply) throws WorldpayModelTransformationException {
        RefundServiceResponse refundResponse = new RefundServiceResponse();

        Object responseType = reply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        if (responseType == null) {
            throw new WorldpayModelTransformationException("No reply message in Worldpay response");
        }
        if (!(responseType instanceof Reply)) {
            throw new WorldpayModelTransformationException("Reply type from Worldpay not the expected type");
        }
        Reply intReply = (Reply) responseType;
        if (getServiceResponseTransformerHelper().checkForError(refundResponse, intReply)) {
            return refundResponse;
        }

        Ok intOk = (Ok) intReply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrPaymentOptionOrToken().get(0);
        if (intOk == null) {
            throw new WorldpayModelTransformationException("No ok status returned in Worldpay reply message");
        }
        Object receivedType = intOk.getCancelReceivedOrVoidReceivedOrCaptureReceivedOrRevokeReceivedOrRefundReceivedOrBackofficeCodeReceivedOrAuthorisationCodeReceivedOrDefenceReceivedOrUpdateTokenReceivedOrDeleteTokenReceivedOrExtendExpiryDateReceivedOrOrderReceived().get(0);
        if (receivedType instanceof RefundReceived) {
            RefundReceived intRefundReceived = (RefundReceived) receivedType;
            refundResponse.setOrderCode(intRefundReceived.getOrderCode());

            com.worldpay.internal.model.Amount intAmount = intRefundReceived.getAmount();
            Amount amount = new Amount(intAmount.getValue(), intAmount.getCurrencyCode(), intAmount.getExponent(), DebitCreditIndicator.getDebitCreditIndicator(intAmount.getDebitCreditIndicator()));
            refundResponse.setAmount(amount);
        } else {
            throw new WorldpayModelTransformationException("Ok received type returned in Worldpay reply message is not one of the expected types for refund");
        }

        return refundResponse;
    }
}
