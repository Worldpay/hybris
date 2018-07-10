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

        final Object responseType = paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        if (responseType == null) {
            throw new WorldpayModelTransformationException("No reply message in Worldpay delete token response");
        }
        if (!(responseType instanceof Reply)) {
            throw new WorldpayModelTransformationException("Reply type from Worldpay not the expected type");
        }

        final Reply intReply = (Reply) responseType;

        final Object response = intReply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrPaymentOptionOrToken().get(0);

        final DeleteTokenResponse deleteTokenResponse = new DeleteTokenResponse();
        if (getServiceResponseTransformerHelper().checkForError(deleteTokenResponse, intReply)) {
            return deleteTokenResponse;
        }
        if (!(response instanceof Ok)) {
            throw new WorldpayModelTransformationException("DeleteTokenResponse did not contain an OK object");
        }
        final Ok okResponse = (Ok) response;
        final Object deleteTokenReceived = okResponse.getCancelReceivedOrVoidReceivedOrCaptureReceivedOrRevokeReceivedOrRefundReceivedOrBackofficeCodeReceivedOrAuthorisationCodeReceivedOrDefenceReceivedOrUpdateTokenReceivedOrDeleteTokenReceivedOrExtendExpiryDateReceivedOrOrderReceivedOrCancelRetryDone().get(0);
        if (deleteTokenReceived instanceof DeleteTokenReceived) {
            deleteTokenResponse.setDeleteTokenResponse(getServiceResponseTransformerHelper().buildDeleteTokenReply((DeleteTokenReceived) deleteTokenReceived));
        }
        return deleteTokenResponse;
    }
}
