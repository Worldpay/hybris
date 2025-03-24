package com.worldpay.service.response.transform;

import com.worldpay.data.token.UpdateTokenReply;
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

    public UpdateTokenResponseTransformer(final ServiceResponseTransformerHelper serviceResponseTransformerHelper) {
        super(serviceResponseTransformerHelper);
    }

    @Override
    public ServiceResponse transform(final PaymentService paymentService) throws WorldpayModelTransformationException {

        final Reply intReply = paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()
            .stream()
            .filter(Reply.class::isInstance)
            .map(Reply.class::cast)
            .findAny()
            .orElseThrow(() -> new WorldpayModelTransformationException("Update Token Response has no reply message or the reply type is not the expected one"));

        final UpdateTokenResponse updateTokenResponse = new UpdateTokenResponse();
        if (serviceResponseTransformerHelper.checkForError(updateTokenResponse, intReply)) {
            return updateTokenResponse;
        }
        final Ok okResponse = intReply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrCheckCardHolderNameResponseOrEcheckVerificationResponseOrPaymentOptionOrToken()
            .stream()
            .filter(Ok.class::isInstance)
            .map(Ok.class::cast)
            .findAny()
            .orElseThrow(() -> new WorldpayModelTransformationException("UpdateTokenResponse did not contain an OK object"));

        okResponse.getCancelReceivedOrVoidReceivedOrCaptureReceivedOrRevokeReceivedOrRefundReceivedOrBackofficeCodeReceivedOrAuthorisationCodeReceivedOrDefenceReceivedOrUpdateTokenReceivedOrDeleteTokenReceivedOrExtendExpiryDateReceivedOrOrderReceivedOrCancelRetryDoneOrCryptogramReceivedOrVoidSaleReceived()
            .stream()
            .filter(UpdateTokenReceived.class::isInstance)
            .map(UpdateTokenReceived.class::cast)
            .findAny()
            .ifPresent(updateTokenReceived -> updateTokenResponse.setUpdateTokenReply(buildUpdateReplyToken(updateTokenReceived)));

        return updateTokenResponse;
    }

    private UpdateTokenReply buildUpdateReplyToken(final UpdateTokenReceived updateTokenReceived) {
        return serviceResponseTransformerHelper.buildUpdateTokenReply(updateTokenReceived);
    }
}
