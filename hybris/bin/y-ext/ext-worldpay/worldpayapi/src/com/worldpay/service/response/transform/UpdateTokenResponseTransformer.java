package com.worldpay.service.response.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.Ok;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.Reply;
import com.worldpay.internal.model.UpdateTokenReceived;
import com.worldpay.service.response.ServiceResponse;
import com.worldpay.service.response.UpdateTokenResponse;

/**
 * Transformer/Converter class that transforms a UpdateTokenResponseTransformer (XML model) into a ServiceResponse (abstraction)
 * to be handled in an easier way.
 */
public class UpdateTokenResponseTransformer extends AbstractServiceResponseTransformer {

    @Override
    public ServiceResponse transform(final PaymentService paymentService) throws WorldpayModelTransformationException {

        final Object responseType = paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        if (responseType == null) {
            throw new WorldpayModelTransformationException("No reply message in Worldpay update token response");
        }
        if (!(responseType instanceof Reply)) {
            throw new WorldpayModelTransformationException("Reply type from Worldpay not the expected type");
        }

        final Reply intReply = (Reply) responseType;
        final Object response = intReply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrPaymentOptionOrToken().get(0);

        final UpdateTokenResponse updateTokenResponse = new UpdateTokenResponse();
        if (getServiceResponseTransformerHelper().checkForError(updateTokenResponse, intReply)) {
            return updateTokenResponse;
        }
        if (!(response instanceof Ok)) {
            throw new WorldpayModelTransformationException("UpdateTokenResponse did not contain an OK object");
        }
        final Ok okResponse = (Ok) response;
        final Object updateTokenReceived = okResponse.getCancelReceivedOrVoidReceivedOrCaptureReceivedOrRevokeReceivedOrRefundReceivedOrBackofficeCodeReceivedOrAuthorisationCodeReceivedOrDefenceReceivedOrUpdateTokenReceivedOrDeleteTokenReceivedOrExtendExpiryDateReceivedOrOrderReceivedOrCancelRetryDoneOrVoidSaleReceived().get(0);
        if (updateTokenReceived instanceof UpdateTokenReceived) {
            updateTokenResponse.setUpdateTokenReply(getServiceResponseTransformerHelper().buildUpdateTokenReply((UpdateTokenReceived) updateTokenReceived));
        }
        return updateTokenResponse;
    }
}
