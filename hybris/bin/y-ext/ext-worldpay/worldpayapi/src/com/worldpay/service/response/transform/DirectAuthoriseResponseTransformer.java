package com.worldpay.service.response.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.*;
import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.model.RedirectReference;
import com.worldpay.service.model.Request3DInfo;
import com.worldpay.service.model.token.TokenReply;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import com.worldpay.service.response.ServiceResponse;

import java.util.List;

/**
 * Specific class for transforming a {@link PaymentService} into a {@link DirectAuthoriseServiceResponse} object
 */
public class DirectAuthoriseResponseTransformer extends AbstractServiceResponseTransformer {

    /**
     * (non-Javadoc)
     *
     * @see com.worldpay.service.response.transform.AbstractServiceResponseTransformer#transform(com.worldpay.internal.model.PaymentService)
     */
    @Override
    public ServiceResponse transform(final PaymentService paymentServiceReply) throws WorldpayModelTransformationException {

        final Object responseType = paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        if (responseType == null) {
            throw new WorldpayModelTransformationException("No reply message in Worldpay response");
        }
        if (!(responseType instanceof Reply)) {
            throw new WorldpayModelTransformationException("Reply type from Worldpay not the expected type");
        }
        final Reply intReply = (Reply) responseType;

        final DirectAuthoriseServiceResponse authResponse = new DirectAuthoriseServiceResponse();
        if (getServiceResponseTransformerHelper().checkForError(authResponse, intReply)) {
            return authResponse;
        }

        final Object response = intReply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrPaymentOptionOrToken().get(0);
        if (response instanceof OrderStatus) {
            final OrderStatus intOrderStatus = (OrderStatus) response;
            authResponse.setOrderCode(intOrderStatus.getOrderCode());

            final List<Object> intOrderStatusElements = intOrderStatus.getReferenceOrBankAccountOrApmEnrichedDataOrErrorOrPaymentOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrOrderModificationOrJournalOrRequestInfoOrFxApprovalRequiredOrZappRTPOrContent();
            for (final Object orderStatusType : intOrderStatusElements) {
                transformOrderStatus(authResponse, intOrderStatus, orderStatusType);
            }
        } else {
            throw new WorldpayModelTransformationException("No order status returned in Worldpay reply message");
        }
        return authResponse;
    }

    private void transformOrderStatus(final DirectAuthoriseServiceResponse authResponse, final OrderStatus intOrderStatus, final Object orderStatusType) throws WorldpayModelTransformationException {
        if (orderStatusType == null) {
            throw new WorldpayModelTransformationException("No order status type returned in Worldpay reply message");
        }
        if (orderStatusType instanceof RequestInfo) {
            final RequestInfo intRequestInfo = (RequestInfo) orderStatusType;
            final Request3DSecure intRequest3dSecure = intRequestInfo.getRequest3DSecure();
            final Request3DInfo req3dInfo = build3DInfo(intRequest3dSecure);
            authResponse.setRequest3DInfo(req3dInfo);
        } else if (orderStatusType instanceof Reference) {
            final Reference intReference = (Reference) orderStatusType;

            authResponse.setRedirectReference(new RedirectReference(intReference.getId(), intReference.getvalue()));
        } else if (orderStatusType instanceof Payment) {
            final Payment intPayment = (Payment) orderStatusType;
            final PaymentReply paymentReply = getServiceResponseTransformerHelper().buildPaymentReply(intPayment);

            authResponse.setPaymentReply(paymentReply);
        } else {
            throw new WorldpayModelTransformationException("Order status type returned in Worldpay reply message is not one of the expected types for direct authorise");
        }

        if (intOrderStatus.getToken() != null) {
            final TokenReply token = getServiceResponseTransformerHelper().buildTokenReply(intOrderStatus.getToken());
            authResponse.setToken(token);
        }

        if (intOrderStatus.getEchoData() != null) {
            authResponse.setEchoData(intOrderStatus.getEchoData().getvalue());
        }
    }

    private Request3DInfo build3DInfo(final Request3DSecure intRequest3dSecure) {
        final Request3DInfo req3dInfo = new Request3DInfo();
        if (intRequest3dSecure != null) {
            final List<Object> valueList = intRequest3dSecure.getPaRequestOrIssuerURLOrMpiRequestOrMpiURLOrIssuerPayloadOrTransactionId3DSOrMajor3DSVersion();
            final String issuerURL = valueList.stream().filter(IssuerURL.class::isInstance).map(IssuerURL.class::cast).findAny().map(IssuerURL::getvalue).orElse(null);
            final String paRequest = valueList.stream().filter(PaRequest.class::isInstance).map(PaRequest.class::cast).findAny().map(PaRequest::getvalue).orElse(null);
            req3dInfo.setIssuerUrl(issuerURL);
            req3dInfo.setPaRequest(paRequest);
        }
        return req3dInfo;

    }
}
