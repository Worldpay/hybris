package com.worldpay.service.response.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.DeleteTokenReceived;
import com.worldpay.internal.model.Ok;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.Reply;
import com.worldpay.service.response.DeleteTokenResponse;
import com.worldpay.service.response.ServiceResponse;

/**
 * Transformer/Converter class that transforms a DeleteTokenResponseTransformer (XML model) into a ServiceResponse (abstraction)
 * to be handled in an easier way.
 */
public class DeleteTokenResponseTransformer extends AbstractServiceResponseTransformer {

    @Override
    public ServiceResponse transform(final PaymentService paymentService) throws WorldpayModelTransformationException {

        final Reply intReply = paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()
            .stream()
            .filter(Reply.class::isInstance)
            .map(Reply.class::cast)
            .findAny()
            .orElseThrow(() -> new WorldpayModelTransformationException("Reply has no reply message or the reply type is not the expected one"));

        final DeleteTokenResponse deleteTokenResponse = new DeleteTokenResponse();
        if (getServiceResponseTransformerHelper().checkForError(deleteTokenResponse, intReply)) {
            return deleteTokenResponse;
        }

        final Ok okResponse = intReply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrEcheckVerificationResponseOrPaymentOptionOrToken()
            .stream()
            .filter(Ok.class::isInstance)
            .map(Ok.class::cast)
            .findAny()
            .orElseThrow(() -> new WorldpayModelTransformationException("DeleteTokenResponse did not contain an OK object"));

        okResponse.getCancelReceivedOrVoidReceivedOrCaptureReceivedOrRevokeReceivedOrRefundReceivedOrBackofficeCodeReceivedOrAuthorisationCodeReceivedOrDefenceReceivedOrUpdateTokenReceivedOrDeleteTokenReceivedOrExtendExpiryDateReceivedOrOrderReceivedOrCancelRetryDoneOrVoidSaleReceived()
            .stream()
            .filter(DeleteTokenReceived.class::isInstance)
            .map(DeleteTokenReceived.class::cast)
            .findAny()
            .ifPresent(deleteTokenReceived -> deleteTokenResponse.setDeleteTokenResponse(getServiceResponseTransformerHelper().buildDeleteTokenReply(deleteTokenReceived)));

        return deleteTokenResponse;
    }
}
