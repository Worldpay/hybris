package com.worldpay.service.response.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.DeleteTokenReceived;
import com.worldpay.internal.model.Ok;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.Reply;
import com.worldpay.service.response.DeleteTokenResponse;
import com.worldpay.service.response.ServiceResponse;

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
        final ServiceResponseTransformerHelper responseTransformerHelper = ServiceResponseTransformerHelper.getInstance();

        final Object response = intReply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrPaymentOptionOrToken().get(0);

        final DeleteTokenResponse deleteTokenResponse = new DeleteTokenResponse();
        if (responseTransformerHelper.checkForError(deleteTokenResponse, intReply)) {
            return deleteTokenResponse;
        }
        if (!(response instanceof Ok)) {
            throw new WorldpayModelTransformationException("DeleteTokenResponse did not contain an OK object");
        }
        final Ok okResponse = (Ok) response;
        final Object deleteTokenReceived = okResponse.getCancelReceivedOrVoidReceivedOrCaptureReceivedOrRevokeReceivedOrRefundReceivedOrBackofficeCodeReceivedOrAuthorisationCodeReceivedOrDefenceReceivedOrUpdateTokenReceivedOrDeleteTokenReceivedOrExtendExpiryDateReceivedOrOrderReceived().get(0);
        if (deleteTokenReceived instanceof DeleteTokenReceived) {
            deleteTokenResponse.setDeleteTokenResponse(responseTransformerHelper.buildDeleteTokenReply((DeleteTokenReceived) deleteTokenReceived));
        }
        return deleteTokenResponse;
    }
}
