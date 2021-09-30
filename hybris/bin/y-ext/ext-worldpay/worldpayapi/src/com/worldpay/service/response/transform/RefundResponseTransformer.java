package com.worldpay.service.response.transform;

import com.worldpay.data.Amount;
import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.Ok;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.RefundReceived;
import com.worldpay.internal.model.Reply;
import com.worldpay.service.response.RefundServiceResponse;
import com.worldpay.service.response.ServiceResponse;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Specific class for transforming a {@link PaymentService} into an {@link RefundServiceResponse} object
 */
public class RefundResponseTransformer extends AbstractServiceResponseTransformer {

    protected final Converter<com.worldpay.internal.model.Amount, Amount> internalAmountReverseConverter;

    public RefundResponseTransformer(final Converter<com.worldpay.internal.model.Amount, Amount> internalAmountReverseConverter) {
        this.internalAmountReverseConverter = internalAmountReverseConverter;
    }

    /* (non-Javadoc)
     * @see AbstractServiceResponseTransformer#transform(com.worldpay.internal.model.PaymentService)
     */
    @Override
    public ServiceResponse transform(final PaymentService reply) throws WorldpayModelTransformationException {
        final RefundServiceResponse refundResponse = new RefundServiceResponse();

        final Reply intReply = reply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()
            .stream()
            .filter(Reply.class::isInstance)
            .map(Reply.class::cast)
            .findAny()
            .orElseThrow(() -> new WorldpayModelTransformationException("Reply has no reply message or the reply type is not the expected one"));

        if (getServiceResponseTransformerHelper().checkForError(refundResponse, intReply)) {
            return refundResponse;
        }

        final Ok intOk = intReply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrPaymentOptionOrToken()
            .stream()
            .filter(Ok.class::isInstance)
            .map(Ok.class::cast)
            .findAny()
            .orElseThrow(() -> new WorldpayModelTransformationException("No ok status returned in Worldpay reply message"));

        final RefundReceived intRefundReceived = intOk.getCancelReceivedOrVoidReceivedOrCaptureReceivedOrRevokeReceivedOrRefundReceivedOrBackofficeCodeReceivedOrAuthorisationCodeReceivedOrDefenceReceivedOrUpdateTokenReceivedOrDeleteTokenReceivedOrExtendExpiryDateReceivedOrOrderReceivedOrCancelRetryDoneOrVoidSaleReceived()
            .stream()
            .filter(RefundReceived.class::isInstance)
            .map(RefundReceived.class::cast)
            .findAny()
            .orElseThrow(() -> new WorldpayModelTransformationException("Ok received type returned in Worldpay reply message is not one of the expected types for refund"));

        refundResponse.setOrderCode(intRefundReceived.getOrderCode());
        refundResponse.setAmount(internalAmountReverseConverter.convert(intRefundReceived.getAmount()));
        return refundResponse;
    }
}
