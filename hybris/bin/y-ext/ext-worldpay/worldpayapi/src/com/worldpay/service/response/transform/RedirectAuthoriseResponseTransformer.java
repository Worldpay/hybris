package com.worldpay.service.response.transform;

import com.worldpay.data.RedirectReference;
import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.*;
import com.worldpay.service.response.RedirectAuthoriseServiceResponse;
import com.worldpay.service.response.ServiceResponse;

import java.util.List;

/**
 * Specific class for transforming a {@link PaymentService} into a {@link RedirectAuthoriseServiceResponse} object
 */
public class RedirectAuthoriseResponseTransformer extends AbstractServiceResponseTransformer {

    public RedirectAuthoriseResponseTransformer(final ServiceResponseTransformerHelper serviceResponseTransformerHelper) {
        super(serviceResponseTransformerHelper);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.worldpay.service.response.transform.AbstractServiceResponseTransformer#transform(com.worldpay.internal.model.PaymentService)
     */
    @Override
    public ServiceResponse transform(final PaymentService paymentServiceReply) throws WorldpayModelTransformationException {
        final RedirectAuthoriseServiceResponse authResponse = new RedirectAuthoriseServiceResponse();

        final Reply intReply = paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()
            .stream()
            .filter(Reply.class::isInstance)
            .map(Reply.class::cast)
            .findAny()
            .orElseThrow(() -> new WorldpayModelTransformationException("Reply has no reply message or the reply type is not the expected one"));

        if (serviceResponseTransformerHelper.checkForError(authResponse, intReply)) {
            return authResponse;
        }

        final OrderStatus intOrderStatus = intReply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrCheckCardHolderNameResponseOrEcheckVerificationResponseOrPaymentOptionOrToken()
            .stream()
            .filter(OrderStatus.class::isInstance)
            .map(OrderStatus.class::cast)
            .findAny()
            .orElseThrow(() -> new WorldpayModelTransformationException("No order status returned in Worldpay reply message"));

        authResponse.setOrderCode(intOrderStatus.getOrderCode());

        final List<Object> intOrderData = intOrderStatus.getReferenceOrBankAccountOrApmEnrichedDataOrErrorOrPaymentOrQrCodeOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrExemptionResponseOrInstalmentPlanOrRetryDetailsOrOrderModificationOrJournalOrRequestInfoOrChallengeRequiredOrFxApprovalRequiredOrPbbaRTPOrContentOrJournalTypeDetailOrTokenOrDateOrEchoDataOrPayAsOrderUseNewOrderCodeOrSelectedSchemeOrAuthenticateResponse();

        intOrderData.stream()
            .filter(Token.class::isInstance)
            .map(Token.class::cast)
            .findAny()
            .map(serviceResponseTransformerHelper::buildTokenReply)
            .ifPresent(authResponse::setToken);

        final Reference reference = intOrderData.stream()
            .filter(Reference.class::isInstance)
            .map(Reference.class::cast)
            .findAny()
            .orElseThrow(() -> new WorldpayModelTransformationException("Order status type returned in Worldpay reply message is not one of the expected types for redirect authorise"));


        final RedirectReference redirectReference = new RedirectReference();
        redirectReference.setValue(reference.getvalue());
        redirectReference.setId(reference.getId());
        authResponse.setRedirectReference(redirectReference);

        return authResponse;
    }
}
