package com.worldpay.service.response.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.OrderStatus;
import com.worldpay.internal.model.Payment;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.Reply;
import com.worldpay.service.response.SecondThreeDSecurePaymentServiceResponse;
import com.worldpay.service.response.ServiceResponse;

import java.util.List;

/**
 * Specific class for transforming a {@link PaymentService} into a {@link SecondThreeDSecurePaymentServiceResponse} object
 */
public class SecondThreeDSecurePaymentResponseTransformer extends AbstractServiceResponseTransformer {

    /**
     * (non-Javadoc)
     *
     * @see AbstractServiceResponseTransformer#transform(PaymentService)
     */
    @Override
    public ServiceResponse transform(final PaymentService paymentServiceReply) throws WorldpayModelTransformationException {

        if (paymentServiceReply == null) {
            throw new WorldpayModelTransformationException("The Worldpay response is null");
        }

        final Reply intReply = paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()
            .stream()
            .filter(Reply.class::isInstance)
            .map(Reply.class::cast)
            .findAny()
            .orElseThrow(() -> new WorldpayModelTransformationException("Reply has no reply message or the reply type is not the expected one"));

        final SecondThreeDSecurePaymentServiceResponse response = new SecondThreeDSecurePaymentServiceResponse();
        if (getServiceResponseTransformerHelper().checkForError(response, intReply)) {
            return response;
        }

        response.setMerchantCode(paymentServiceReply.getMerchantCode());
        response.setVersion(paymentServiceReply.getVersion());

        final List<Object> replyAttribute = intReply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrEcheckVerificationResponseOrPaymentOptionOrToken();
        final OrderStatus orderStatus = replyAttribute.stream()
                .filter(OrderStatus.class::isInstance)
                .map(OrderStatus.class::cast)
                .findAny()
                .orElseThrow(() -> new WorldpayModelTransformationException("No order status returned in Worldpay reply message"));

        final List<Object> orderStatusAttributes = orderStatus.getReferenceOrBankAccountOrApmEnrichedDataOrErrorOrPaymentOrQrCodeOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrExemptionResponseOrOrderModificationOrJournalOrRequestInfoOrChallengeRequiredOrFxApprovalRequiredOrPbbaRTPOrContentOrJournalTypeDetailOrTokenOrDateOrEchoDataOrPayAsOrderUseNewOrderCodeOrAuthenticateResponse();

        final Payment payment = orderStatusAttributes.stream()
                .filter(Payment.class::isInstance)
                .map(Payment.class::cast)
                .findAny()
                .orElseThrow(() -> new WorldpayModelTransformationException("No payment or threedsecureresult returned in Worldpay orderstatus message"));

        response.setPaymentReply(getServiceResponseTransformerHelper().buildPaymentReply(payment));

        return response;
    }
}
