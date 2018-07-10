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
    public ServiceResponse transform(PaymentService reply) throws WorldpayModelTransformationException {
        AddBackOfficeCodeServiceResponse addBackOfficeCodeResponse = new AddBackOfficeCodeServiceResponse();

        Object responseType = reply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        if (responseType == null) {
            throw new WorldpayModelTransformationException("No reply message in Worldpay response");
        }
        if (!(responseType instanceof Reply)) {
            throw new WorldpayModelTransformationException("Reply type from Worldpay not the expected type");
        }
        Reply intReply = (Reply) responseType;
        if (getServiceResponseTransformerHelper().checkForError(addBackOfficeCodeResponse, intReply)) {
            return addBackOfficeCodeResponse;
        }

        Ok intOk = (Ok) intReply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrPaymentOptionOrToken().get(0);
        if (intOk == null) {
            throw new WorldpayModelTransformationException("No ok status returned in Worldpay reply message");
        }
        Object receivedType = intOk.getCancelReceivedOrVoidReceivedOrCaptureReceivedOrRevokeReceivedOrRefundReceivedOrBackofficeCodeReceivedOrAuthorisationCodeReceivedOrDefenceReceivedOrUpdateTokenReceivedOrDeleteTokenReceivedOrExtendExpiryDateReceivedOrOrderReceivedOrCancelRetryDone().get(0);
        if (receivedType instanceof BackofficeCodeReceived) {
            BackofficeCodeReceived intBackOfficeCodeReceived = (BackofficeCodeReceived) receivedType;
            addBackOfficeCodeResponse.setOrderCode(intBackOfficeCodeReceived.getOrderCode());
            addBackOfficeCodeResponse.setBackOfficeCode(intBackOfficeCodeReceived.getBackOfficeCode());
        } else {
            throw new WorldpayModelTransformationException("Ok received type returned in Worldpay reply message is not one of the expected types for add back office code");
        }

        return addBackOfficeCodeResponse;
    }
}
