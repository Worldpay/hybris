package com.worldpay.worldpayresponsemock.responses.impl;

import com.worldpay.internal.model.Order;
import com.worldpay.internal.model.OrderStatus;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.Reference;
import com.worldpay.internal.model.Reply;
import com.worldpay.internal.model.Submit;
import com.worldpay.worldpayresponsemock.constants.WorldpayresponsemockConstants;
import com.worldpay.worldpayresponsemock.responses.WorldpayResponseBuilder;
import org.joda.time.DateTime;

import javax.servlet.http.HttpServletRequest;

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
    public PaymentService buildRedirectResponse(PaymentService request, HttpServletRequest httpServletRequest) {
        final PaymentService paymentService = new PaymentService();
        paymentService.setMerchantCode(request.getMerchantCode());
        Reply reply = new Reply();
        OrderStatus orderStatus = new OrderStatus();
        final Submit submit = (Submit) request.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        final Order order = (Order) submit.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreate().get(0);
        orderStatus.setOrderCode(order.getOrderCode());
        Reference reference = new Reference();
        reference.setId(DateTime.now().toString());
        reference.setvalue(buildStoreFrontHopResponseEndpoint(httpServletRequest));
        orderStatus.
                getReferenceOrBankAccountOrApmEnrichedDataOrErrorOrPaymentOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrOrderModificationOrJournalOrRequestInfoOrFxApprovalRequiredOrZappRTPOrContent().
                add(reference);
        reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrPaymentOptionOrToken().add(orderStatus);
        paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(reply);
        return paymentService;
    }

    private String buildStoreFrontHopResponseEndpoint(final HttpServletRequest request) {
        final String serverName = request.getServerName();
        return WorldpayresponsemockConstants.HTTPS + SCHEME_SEPARATOR + serverName + PROTOCOL_SEPARATOR + SECURE_PORT_NUMBER + "/worldpayresponsemock/redirect?";
    }
}

