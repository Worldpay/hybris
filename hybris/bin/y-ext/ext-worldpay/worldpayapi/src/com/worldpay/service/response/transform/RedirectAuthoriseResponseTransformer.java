package com.worldpay.service.response.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.OrderStatus;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.Reference;
import com.worldpay.internal.model.Reply;
import com.worldpay.service.model.RedirectReference;
import com.worldpay.service.model.token.TokenReply;
import com.worldpay.service.response.RedirectAuthoriseServiceResponse;
import com.worldpay.service.response.ServiceResponse;

/**
 * Specific class for transforming a {@link PaymentService} into a {@link RedirectAuthoriseServiceResponse} object
 */
public class RedirectAuthoriseResponseTransformer extends AbstractServiceResponseTransformer {

    /**
     * (non-Javadoc)
     *
     * @see com.worldpay.service.response.transform.AbstractServiceResponseTransformer#transform(com.worldpay.internal.model.PaymentService)
     */
    @Override
    public ServiceResponse transform(final PaymentService paymentServiceReply) throws WorldpayModelTransformationException {
        final RedirectAuthoriseServiceResponse authResponse = new RedirectAuthoriseServiceResponse();

        final Object responseType = paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        if (responseType == null) {
            throw new WorldpayModelTransformationException("No reply message in Worldpay response");
        }
        if (!(responseType instanceof Reply)) {
            throw new WorldpayModelTransformationException("Reply type from Worldpay not the expected type");
        }
        final Reply intReply = (Reply) responseType;
        if (getServiceResponseTransformerHelper().checkForError(authResponse, intReply)) {
            return authResponse;
        }

        final OrderStatus intOrderStatus = (OrderStatus) intReply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrPaymentOptionOrToken().get(0);
        if (intOrderStatus == null) {
            throw new WorldpayModelTransformationException("No order status returned in Worldpay reply message");
        }
        authResponse.setOrderCode(intOrderStatus.getOrderCode());

        if (intOrderStatus.getToken() != null) {
            final TokenReply token = getServiceResponseTransformerHelper().buildTokenReply(intOrderStatus.getToken());
            authResponse.setToken(token);
        }

        final Object orderStatusType = intOrderStatus.getReferenceOrBankAccountOrApmEnrichedDataOrErrorOrPaymentOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrOrderModificationOrJournalOrRequestInfoOrFxApprovalRequiredOrZappRTPOrContent().get(0);
        if (orderStatusType instanceof Reference) {
            Reference intReference = (Reference) orderStatusType;

            authResponse.setRedirectReference(new RedirectReference(intReference.getId(), intReference.getvalue()));
        } else {
            throw new WorldpayModelTransformationException("Order status type returned in Worldpay reply message is not one of the expected types for redirect authorise");
        }

        return authResponse;
    }
}
