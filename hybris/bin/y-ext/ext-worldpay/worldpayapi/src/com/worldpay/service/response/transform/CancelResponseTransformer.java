package com.worldpay.service.response.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.Ok;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.Reply;
import com.worldpay.internal.model.VoidReceived;
import com.worldpay.service.response.CancelServiceResponse;
import com.worldpay.service.response.ServiceResponse;

/**
 * Specific class for transforming a {@link PaymentService} into a {@link CancelServiceResponse} object
 */
public class CancelResponseTransformer extends AbstractServiceResponseTransformer {

    public CancelResponseTransformer(final ServiceResponseTransformerHelper serviceResponseTransformerHelper) {
        super(serviceResponseTransformerHelper);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.worldpay.service.response.transform.AbstractServiceResponseTransformer#transform(com.worldpay.internal.model.PaymentService)
     */
    @Override
    public ServiceResponse transform(final PaymentService reply) throws WorldpayModelTransformationException {
        final CancelServiceResponse response = new CancelServiceResponse();

        final Reply intReply = reply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()
            .stream()
            .filter(Reply.class::isInstance)
            .map(Reply.class::cast)
            .findAny()
            .orElseThrow(() -> new WorldpayModelTransformationException("Reply has no reply message or the reply type is not the expected one"));

        if (serviceResponseTransformerHelper.checkForError(response, intReply)) {
            return response;
        }

        final Ok intOk = intReply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrCheckCardHolderNameResponseOrEcheckVerificationResponseOrPaymentOptionOrToken()
            .stream()
            .filter(Ok.class::isInstance)
            .map(Ok.class::cast)
            .findAny()
            .orElseThrow(() -> new WorldpayModelTransformationException("No ok status returned in Worldpay reply message"));

        final VoidReceived receivedType = intOk.getCancelReceivedOrVoidReceivedOrCaptureReceivedOrRevokeReceivedOrRefundReceivedOrBackofficeCodeReceivedOrAuthorisationCodeReceivedOrDefenceReceivedOrUpdateTokenReceivedOrDeleteTokenReceivedOrExtendExpiryDateReceivedOrOrderReceivedOrCancelRetryDoneOrCryptogramReceivedOrVoidSaleReceived()
            .stream()
            .filter(VoidReceived.class::isInstance)
            .map(VoidReceived.class::cast)
            .findAny()
            .orElseThrow(() -> new WorldpayModelTransformationException("Ok received type returned in Worldpay reply message is not one of the expected types for cancel"));

        response.setOrderCode(receivedType.getOrderCode());

        return response;
    }
}
