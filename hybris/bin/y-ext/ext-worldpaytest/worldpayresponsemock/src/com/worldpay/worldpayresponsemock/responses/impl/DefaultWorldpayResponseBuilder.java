package com.worldpay.worldpayresponsemock.responses.impl;

import com.worldpay.internal.model.*;
import com.worldpay.worldpayresponsemock.constants.WorldpayresponsemockConstants;
import com.worldpay.worldpayresponsemock.responses.WorldpayResponseBuilder;

import javax.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;

import static com.worldpay.worldpayresponsemock.constants.WorldpayresponsemockConstants.PROTOCOL_SEPARATOR;
import static com.worldpay.worldpayresponsemock.constants.WorldpayresponsemockConstants.SCHEME_SEPARATOR;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayResponseBuilder implements WorldpayResponseBuilder {

    private static final int SECURE_PORT_NUMBER = 9002;

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentService buildRedirectResponse(final PaymentService request, final HttpServletRequest httpServletRequest) {
        final PaymentService paymentService = new PaymentService();
        paymentService.setMerchantCode(request.getMerchantCode());
        final Reply reply = new Reply();
        final OrderStatus orderStatus = new OrderStatus();
        final Submit submit = (Submit) request.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        final Order order = (Order) submit.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreateOrChallenge().get(0);
        orderStatus.setOrderCode(order.getOrderCode());
        final Reference reference = new Reference();
        reference.setId(OffsetDateTime.now().toString());
        reference.setvalue(buildStoreFrontHopResponseEndpoint(httpServletRequest));
        orderStatus.
                getReferenceOrBankAccountOrApmEnrichedDataOrErrorOrPaymentOrQrCodeOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrExemptionResponseOrOrderModificationOrJournalOrRequestInfoOrChallengeRequiredOrFxApprovalRequiredOrPbbaRTPOrContentOrJournalTypeDetailOrTokenOrDateOrEchoDataOrPayAsOrderUseNewOrderCodeOrAuthenticateResponse().
                add(reference);
        reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrEcheckVerificationResponseOrPaymentOptionOrToken().add(orderStatus);
        paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(reply);
        return paymentService;
    }

    private String buildStoreFrontHopResponseEndpoint(final HttpServletRequest request) {
        final String serverName = request.getServerName();
        return WorldpayresponsemockConstants.HTTPS + SCHEME_SEPARATOR + serverName + PROTOCOL_SEPARATOR + SECURE_PORT_NUMBER + "/worldpayresponsemock/redirect?";
    }
}

