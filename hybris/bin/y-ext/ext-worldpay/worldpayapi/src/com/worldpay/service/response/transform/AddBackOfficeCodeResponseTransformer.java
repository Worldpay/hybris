package com.worldpay.service.response.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.BackofficeCodeReceived;
import com.worldpay.internal.model.Ok;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.Reply;
import com.worldpay.service.response.AddBackOfficeCodeServiceResponse;
import com.worldpay.service.response.ServiceResponse;

/**
 * Specific class for transforming a {@link PaymentService} into an {@link AddBackOfficeCodeServiceResponse} object
 */
public class AddBackOfficeCodeResponseTransformer extends AbstractServiceResponseTransformer {

    /**
     * (non-Javadoc)
     *
     * @see com.worldpay.service.response.transform.AbstractServiceResponseTransformer#transform(com.worldpay.internal.model.PaymentService)
     */
    @Override
    public ServiceResponse transform(final PaymentService reply) throws WorldpayModelTransformationException {
        final AddBackOfficeCodeServiceResponse addBackOfficeCodeResponse = new AddBackOfficeCodeServiceResponse();

        final Reply intReply = reply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()
            .stream()
            .filter(Reply.class::isInstance)
            .map(Reply.class::cast)
            .findAny()
            .orElseThrow(() -> new WorldpayModelTransformationException("Reply has no reply message or the reply type is not the expected one"));

        if (getServiceResponseTransformerHelper().checkForError(addBackOfficeCodeResponse, intReply)) {
            return addBackOfficeCodeResponse;
        }

        final Ok intOk = intReply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrEcheckVerificationResponseOrPaymentOptionOrToken()
            .stream()
            .filter(Ok.class::isInstance)
            .map(Ok.class::cast)
            .findAny()
            .orElseThrow(() -> new WorldpayModelTransformationException("No ok status returned in Worldpay reply message"));

        final BackofficeCodeReceived intBackOfficeCodeReceived = intOk.getCancelReceivedOrVoidReceivedOrCaptureReceivedOrRevokeReceivedOrRefundReceivedOrBackofficeCodeReceivedOrAuthorisationCodeReceivedOrDefenceReceivedOrUpdateTokenReceivedOrDeleteTokenReceivedOrExtendExpiryDateReceivedOrOrderReceivedOrCancelRetryDoneOrVoidSaleReceived()
            .stream()
            .filter(BackofficeCodeReceived.class::isInstance)
            .map(BackofficeCodeReceived.class::cast)
            .findAny()
            .orElseThrow(() -> new WorldpayModelTransformationException("Ok received type returned in Worldpay reply message is not one of the expected types for add back office code"));

        addBackOfficeCodeResponse.setOrderCode(intBackOfficeCodeReceived.getOrderCode());
        addBackOfficeCodeResponse.setBackOfficeCode(intBackOfficeCodeReceived.getBackOfficeCode());

        return addBackOfficeCodeResponse;
    }
}
