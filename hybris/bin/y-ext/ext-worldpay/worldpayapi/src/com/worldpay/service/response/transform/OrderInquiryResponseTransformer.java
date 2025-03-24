package com.worldpay.service.response.transform;

import com.worldpay.data.PaymentReply;
import com.worldpay.data.RedirectReference;
import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.*;
import com.worldpay.service.response.OrderInquiryServiceResponse;
import com.worldpay.service.response.ServiceResponse;

import java.util.List;

/**
 * Specific class for transforming a {@link PaymentService} into an {@link OrderInquiryServiceResponse} object
 */
public class OrderInquiryResponseTransformer extends AbstractServiceResponseTransformer {

    public OrderInquiryResponseTransformer(final ServiceResponseTransformerHelper serviceResponseTransformerHelper) {
        super(serviceResponseTransformerHelper);
    }

    /* (non-Javadoc)
     * @see AbstractServiceResponseTransformer#transform(com.worldpay.internal.model.PaymentService)
     */
    @Override
    public ServiceResponse transform(final PaymentService reply) throws WorldpayModelTransformationException {
        final OrderInquiryServiceResponse orderInquiryResponse = new OrderInquiryServiceResponse();

        final Reply intReply = reply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()
            .stream()
            .filter(Reply.class::isInstance)
            .map(Reply.class::cast)
            .findAny()
            .orElseThrow(() -> new WorldpayModelTransformationException("Reply has no reply message or the reply type is not the expected one"));

        if (serviceResponseTransformerHelper.checkForError(orderInquiryResponse, intReply)) {
            return orderInquiryResponse;
        }

        final OrderStatus intOrderStatus = intReply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrCheckCardHolderNameResponseOrEcheckVerificationResponseOrPaymentOptionOrToken()
            .stream()
            .filter(OrderStatus.class::isInstance)
            .map(OrderStatus.class::cast)
            .findAny()
            .orElseThrow(() -> new WorldpayModelTransformationException("No order status returned in Worldpay reply message"));

        orderInquiryResponse.setOrderCode(intOrderStatus.getOrderCode());

        final List<Object> orderStatusElements = intOrderStatus.getReferenceOrBankAccountOrApmEnrichedDataOrErrorOrPaymentOrQrCodeOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrExemptionResponseOrInstalmentPlanOrRetryDetailsOrOrderModificationOrJournalOrRequestInfoOrChallengeRequiredOrFxApprovalRequiredOrPbbaRTPOrContentOrJournalTypeDetailOrTokenOrDateOrEchoDataOrPayAsOrderUseNewOrderCodeOrSelectedSchemeOrAuthenticateResponse();
        for (final Object orderStatusElement : orderStatusElements) {
            if (orderStatusElement instanceof Payment intPayment) {
                final PaymentReply paymentReply = serviceResponseTransformerHelper.buildPaymentReply(intPayment);
                orderInquiryResponse.setPaymentReply(paymentReply);
            } else if (orderStatusElement instanceof Reference intReference) {
                final RedirectReference reference = new RedirectReference();
                reference.setId(intReference.getId());
                reference.setValue(intReference.getvalue());
                orderInquiryResponse.setReference(reference);
            }
        }
        final Object orderStatusType = orderStatusElements.get(0);
        if (orderStatusType == null) {
            throw new WorldpayModelTransformationException("No order status type returned in Worldpay reply message");
        }
        if (orderStatusType instanceof Payment intPayment) {
            final PaymentReply paymentReply = serviceResponseTransformerHelper.buildPaymentReply(intPayment);

            orderInquiryResponse.setPaymentReply(paymentReply);
        } else {
            throw new WorldpayModelTransformationException("Order status type returned in Worldpay reply message is not one of the expected types for order inquiry");
        }

        return orderInquiryResponse;
    }
}
