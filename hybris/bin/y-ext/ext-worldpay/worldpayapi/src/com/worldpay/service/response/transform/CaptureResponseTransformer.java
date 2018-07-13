package com.worldpay.service.response.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.CaptureReceived;
import com.worldpay.internal.model.Ok;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.Reply;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.DebitCreditIndicator;
import com.worldpay.service.response.CaptureServiceResponse;
import com.worldpay.service.response.ServiceResponse;

/**
 * Specific class for transforming a {@link PaymentService} into a {@link CaptureServiceResponse} object
 */
public class CaptureResponseTransformer extends AbstractServiceResponseTransformer {

    /** (non-Javadoc)
     * @see com.worldpay.service.response.transform.AbstractServiceResponseTransformer#transform(com.worldpay.internal.model.PaymentService)
     */
    @Override
    public ServiceResponse transform(PaymentService reply) throws WorldpayModelTransformationException {
        CaptureServiceResponse captureResponse = new CaptureServiceResponse();

        Object responseType = reply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        if (responseType == null) {
            throw new WorldpayModelTransformationException("No reply message in Worldpay response");
        }
        if (!(responseType instanceof Reply)) {
            throw new WorldpayModelTransformationException("Reply type from Worldpay not the expected type");
        }
        Reply intReply = (Reply) responseType;
        if (getServiceResponseTransformerHelper().checkForError(captureResponse, intReply)) {
            return captureResponse;
        }

        Ok intOk = (Ok) intReply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrPaymentOptionOrToken().get(0);
        if (intOk == null) {
            throw new WorldpayModelTransformationException("No ok status returned in Worldpay reply message");
        }
        Object receivedType = intOk.getCancelReceivedOrVoidReceivedOrCaptureReceivedOrRevokeReceivedOrRefundReceivedOrBackofficeCodeReceivedOrAuthorisationCodeReceivedOrDefenceReceivedOrUpdateTokenReceivedOrDeleteTokenReceivedOrExtendExpiryDateReceivedOrOrderReceivedOrCancelRetryDone().get(0);
        if (receivedType instanceof CaptureReceived) {
            CaptureReceived intCaptureReceived = (CaptureReceived) receivedType;
            captureResponse.setOrderCode(intCaptureReceived.getOrderCode());

            com.worldpay.internal.model.Amount intAmount = intCaptureReceived.getAmount();
            Amount amount = new Amount(intAmount.getValue(), intAmount.getCurrencyCode(), intAmount.getExponent(), DebitCreditIndicator.getDebitCreditIndicator(intAmount.getDebitCreditIndicator()));
            captureResponse.setAmount(amount);
        } else {
            throw new WorldpayModelTransformationException("Ok received type returned in Worldpay reply message is not one of the expected types for capture");
        }

        return captureResponse;
    }
}
