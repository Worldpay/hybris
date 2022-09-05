package com.worldpay.service.response.transform;

import com.worldpay.data.Amount;
import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.CaptureReceived;
import com.worldpay.internal.model.Ok;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.Reply;
import com.worldpay.service.response.CaptureServiceResponse;
import com.worldpay.service.response.ServiceResponse;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Specific class for transforming a {@link PaymentService} into a {@link CaptureServiceResponse} object
 */
public class CaptureResponseTransformer extends AbstractServiceResponseTransformer {

    protected final Converter<com.worldpay.internal.model.Amount, Amount> internalAmountReverseConverter;

    public CaptureResponseTransformer(final Converter<com.worldpay.internal.model.Amount, Amount> internalAmountReverseConverter) {
        this.internalAmountReverseConverter = internalAmountReverseConverter;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.worldpay.service.response.transform.AbstractServiceResponseTransformer#transform(com.worldpay.internal.model.PaymentService)
     */
    @Override
    public ServiceResponse transform(final PaymentService reply) throws WorldpayModelTransformationException {
        final CaptureServiceResponse captureResponse = new CaptureServiceResponse();

        final Reply intReply = reply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()
            .stream()
            .filter(Reply.class::isInstance)
            .map(Reply.class::cast)
            .findAny()
            .orElseThrow(() -> new WorldpayModelTransformationException("Reply has no reply message or the reply type is not the expected one"));

        if (getServiceResponseTransformerHelper().checkForError(captureResponse, intReply)) {
            return captureResponse;
        }

        final Ok intOk = intReply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrEcheckVerificationResponseOrPaymentOptionOrToken()
            .stream()
            .filter(Ok.class::isInstance)
            .map(Ok.class::cast)
            .findAny()
            .orElseThrow(() -> new WorldpayModelTransformationException("No ok status returned in Worldpay reply message"));

        final CaptureReceived intCaptureReceived = intOk.getCancelReceivedOrVoidReceivedOrCaptureReceivedOrRevokeReceivedOrRefundReceivedOrBackofficeCodeReceivedOrAuthorisationCodeReceivedOrDefenceReceivedOrUpdateTokenReceivedOrDeleteTokenReceivedOrExtendExpiryDateReceivedOrOrderReceivedOrCancelRetryDoneOrVoidSaleReceived()
            .stream()
            .filter(CaptureReceived.class::isInstance)
            .map(CaptureReceived.class::cast)
            .findAny()
            .orElseThrow(() -> new WorldpayModelTransformationException("Ok received type returned in Worldpay reply message is not one of the expected types for capture"));

        captureResponse.setOrderCode(intCaptureReceived.getOrderCode());
        captureResponse.setAmount(internalAmountReverseConverter.convert(intCaptureReceived.getAmount()));

        return captureResponse;
    }
}
