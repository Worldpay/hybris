package com.worldpay.service.response.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.OrderStatus;
import com.worldpay.internal.model.Payment;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.Reply;
import com.worldpay.service.response.SecondThreeDSecurePaymentServiceResponse;
import com.worldpay.service.response.ServiceResponse;
import org.apache.commons.collections4.CollectionUtils;

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

        if (CollectionUtils.isEmpty(paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()) || paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0) == null) {
            throw new WorldpayModelTransformationException("No reply message in Worldpay response");
        }
        final Object responseType = paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        if (!(responseType instanceof Reply)) {
            throw new WorldpayModelTransformationException("Reply type from Worldpay not the expected type");
        }
        final Reply intReply = (Reply) responseType;

        final SecondThreeDSecurePaymentServiceResponse response = new SecondThreeDSecurePaymentServiceResponse();
        if (getServiceResponseTransformerHelper().checkForError(response, intReply)) {
            return response;
        }

        response.setMerchantCode(paymentServiceReply.getMerchantCode());
        response.setVersion(paymentServiceReply.getVersion());

        final List<Object> replyAttribute = intReply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrPaymentOptionOrToken();
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
