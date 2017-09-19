package com.worldpay.service.response.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.AuthorisationCodeReceived;
import com.worldpay.internal.model.Ok;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.Reply;
import com.worldpay.service.response.AuthorisationCodeServiceResponse;
import com.worldpay.service.response.ServiceResponse;

/**
 * Specific class for transforming a {@link PaymentService} into an {@link AuthorisationCodeServiceResponse} object
 */
public class AuthorisationCodeResponseTransformer extends AbstractServiceResponseTransformer {

    /**
     * (non-Javadoc)
     *
     * @see com.worldpay.service.response.transform.AbstractServiceResponseTransformer#transform(com.worldpay.internal.model.PaymentService)
     */
    @Override
    public ServiceResponse transform(PaymentService reply) throws WorldpayModelTransformationException {
        AuthorisationCodeServiceResponse authorisationCodeResponse = new AuthorisationCodeServiceResponse();

        Object responseType = reply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        if (responseType == null) {
            throw new WorldpayModelTransformationException("No reply message in Worldpay response");
        }
        if (!(responseType instanceof Reply)) {
            throw new WorldpayModelTransformationException("Reply type from Worldpay not the expected type");
        }
        Reply intReply = (Reply) responseType;
        ServiceResponseTransformerHelper responseTransformerHelper = ServiceResponseTransformerHelper.getInstance();
        if (responseTransformerHelper.checkForError(authorisationCodeResponse, intReply)) {
            return authorisationCodeResponse;
        }

        Ok intOk = (Ok) intReply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrPaymentOptionOrToken().get(0);
        if (intOk == null) {
            throw new WorldpayModelTransformationException("No ok status returned in Worldpay reply message");
        }
        Object receivedType = intOk.getCancelReceivedOrVoidReceivedOrCaptureReceivedOrRevokeReceivedOrRefundReceivedOrBackofficeCodeReceivedOrAuthorisationCodeReceivedOrDefenceReceivedOrUpdateTokenReceivedOrDeleteTokenReceivedOrExtendExpiryDateReceivedOrOrderReceived().get(0);
        if (receivedType instanceof AuthorisationCodeReceived) {
            AuthorisationCodeReceived intAuthorisationCodeReceived = (AuthorisationCodeReceived) receivedType;
            authorisationCodeResponse.setOrderCode(intAuthorisationCodeReceived.getOrderCode());
            authorisationCodeResponse.setAuthorisationCode(intAuthorisationCodeReceived.getAuthorisationCode());
        } else {
            throw new WorldpayModelTransformationException("Ok received type returned in Worldpay reply message is not one of the expected types for authorisation code");
        }

        return authorisationCodeResponse;
    }
}
