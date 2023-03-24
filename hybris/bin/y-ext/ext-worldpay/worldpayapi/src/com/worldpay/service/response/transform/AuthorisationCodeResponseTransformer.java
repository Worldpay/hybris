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
    public ServiceResponse transform(final PaymentService reply) throws WorldpayModelTransformationException {
        final AuthorisationCodeServiceResponse authorisationCodeResponse = new AuthorisationCodeServiceResponse();

        final Reply intReply = reply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()
            .stream()
            .filter(Reply.class::isInstance)
            .map(Reply.class::cast)
            .findAny()
            .orElseThrow(() -> new WorldpayModelTransformationException("Reply has no reply message or the reply type is not the expected one"));

        if (getServiceResponseTransformerHelper().checkForError(authorisationCodeResponse, intReply)) {
            return authorisationCodeResponse;
        }

        final Ok intOk = intReply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrEcheckVerificationResponseOrPaymentOptionOrToken()
            .stream()
            .filter(Ok.class::isInstance)
            .map(Ok.class::cast)
            .findAny()
            .orElseThrow(() -> new WorldpayModelTransformationException("No ok status returned in Worldpay reply message"));


        final AuthorisationCodeReceived intAuthorisationCodeReceived = intOk.getCancelReceivedOrVoidReceivedOrCaptureReceivedOrRevokeReceivedOrRefundReceivedOrBackofficeCodeReceivedOrAuthorisationCodeReceivedOrDefenceReceivedOrUpdateTokenReceivedOrDeleteTokenReceivedOrExtendExpiryDateReceivedOrOrderReceivedOrCancelRetryDoneOrVoidSaleReceived()
            .stream()
            .filter(AuthorisationCodeReceived.class::isInstance)
            .map(AuthorisationCodeReceived.class::cast)
            .findAny()
            .orElseThrow(() -> new WorldpayModelTransformationException("Ok received type returned in Worldpay reply message is not one of the expected types for authorisation code"));

        authorisationCodeResponse.setOrderCode(intAuthorisationCodeReceived.getOrderCode());
        authorisationCodeResponse.setAuthorisationCode(intAuthorisationCodeReceived.getAuthorisationCode());

        return authorisationCodeResponse;
    }
}
