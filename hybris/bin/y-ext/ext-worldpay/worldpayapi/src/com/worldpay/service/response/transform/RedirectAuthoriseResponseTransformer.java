package com.worldpay.service.response.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.*;
import com.worldpay.service.model.RedirectReference;
import com.worldpay.service.response.RedirectAuthoriseServiceResponse;
import com.worldpay.service.response.ServiceResponse;

import java.util.List;

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

        final List<Object> intOrderData = intOrderStatus.getReferenceOrBankAccountOrApmEnrichedDataOrErrorOrPaymentOrQrCodeOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrExemptionResponseOrOrderModificationOrJournalOrRequestInfoOrChallengeRequiredOrFxApprovalRequiredOrPbbaRTPOrContentOrJournalTypeDetailOrTokenOrDateOrEchoDataOrPayAsOrderUseNewOrderCodeOrAuthenticateResponse();

        intOrderData.stream()
                .filter(Token.class::isInstance)
                .map(Token.class::cast)
                .findAny()
                .map(getServiceResponseTransformerHelper()::buildTokenReply)
                .ifPresent(authResponse::setToken);

        final Reference reference = intOrderData.stream()
                .filter(Reference.class::isInstance)
                .map(Reference.class::cast)
                .findAny()
                .orElseThrow(() -> new WorldpayModelTransformationException("Order status type returned in Worldpay reply message is not one of the expected types for redirect authorise"));

        final RedirectReference redirectReference = new RedirectReference(reference.getId(), reference.getvalue());
        authResponse.setRedirectReference(redirectReference);

        return authResponse;
    }
}
