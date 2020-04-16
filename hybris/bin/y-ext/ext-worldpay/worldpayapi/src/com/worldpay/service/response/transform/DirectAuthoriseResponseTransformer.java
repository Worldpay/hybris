package com.worldpay.service.response.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.*;
import com.worldpay.service.model.RedirectReference;
import com.worldpay.service.model.Request3DInfo;
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

            final List<Object> intOrderStatusElements = intOrderStatus.getReferenceOrBankAccountOrApmEnrichedDataOrErrorOrPaymentOrQrCodeOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrExemptionResponseOrOrderModificationOrJournalOrRequestInfoOrChallengeRequiredOrFxApprovalRequiredOrPbbaRTPOrContentOrJournalTypeDetailOrTokenOrDateOrEchoDataOrPayAsOrderUseNewOrderCodeOrAuthenticateResponse();
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
        final List<Object> intOrderStatuses = intOrderStatus.getReferenceOrBankAccountOrApmEnrichedDataOrErrorOrPaymentOrQrCodeOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrExemptionResponseOrOrderModificationOrJournalOrRequestInfoOrChallengeRequiredOrFxApprovalRequiredOrPbbaRTPOrContentOrJournalTypeDetailOrTokenOrDateOrEchoDataOrPayAsOrderUseNewOrderCodeOrAuthenticateResponse();

        intOrderStatuses.stream()
                .filter(ChallengeRequired.class::isInstance)
                .map(ChallengeRequired.class::cast)
                .findAny()
                .map(ChallengeRequired::getThreeDSChallengeDetails)
                .map(this::build3DInfoForChallenge)
                .ifPresent(authResponse::setRequest3DInfo);

        intOrderStatuses.stream()
                .filter(RequestInfo.class::isInstance)
                .map(RequestInfo.class::cast)
                .findAny()
                .map(RequestInfo::getRequest3DSecure)
                .map(this::build3DInfo)
                .ifPresent(authResponse::setRequest3DInfo);

        intOrderStatuses.stream()
                .filter(Reference.class::isInstance)
                .map(Reference.class::cast)
                .findAny()
                .map(reference -> new RedirectReference(reference.getId(), reference.getvalue()))
                .ifPresent(authResponse::setRedirectReference);

        intOrderStatuses.stream()
                .filter(Payment.class::isInstance)
                .map(Payment.class::cast)
                .findAny()
                .map(getServiceResponseTransformerHelper()::buildPaymentReply)
                .ifPresent(authResponse::setPaymentReply);

        intOrderStatuses.stream()
                .filter(Token.class::isInstance)
                .map(Token.class::cast)
                .findAny()
                .map(getServiceResponseTransformerHelper()::buildTokenReply)
                .ifPresent(authResponse::setToken);

        intOrderStatuses.stream()
                .filter(EchoData.class::isInstance)
                .map(EchoData.class::cast)
                .findAny()
                .map(EchoData::getvalue)
                .ifPresent(authResponse::setEchoData);
    }

    private Request3DInfo build3DInfoForChallenge(final ThreeDSChallengeDetails threeDSChallengeDetails) {
        final Request3DInfo req3dInfo = new Request3DInfo();
        if (threeDSChallengeDetails != null) {
            req3dInfo.setMajor3DSVersion(threeDSChallengeDetails.getThreeDSVersion().getvalue());
            req3dInfo.setIssuerUrl(threeDSChallengeDetails.getAcsURL());
            req3dInfo.setIssuerPayload(threeDSChallengeDetails.getPayload());
            req3dInfo.setTransactionId3DS(threeDSChallengeDetails.getTransactionId3DS());
        }
        return req3dInfo;

    }

    private Request3DInfo build3DInfo(final Request3DSecure intRequest3dSecure) {
        final Request3DInfo req3dInfo = new Request3DInfo();
        if (intRequest3dSecure != null) {
            final List<Object> valueList = intRequest3dSecure.getPaRequestOrIssuerURLOrMpiRequestOrMpiURL();
            final String issuerURL = valueList.stream().filter(IssuerURL.class::isInstance).map(IssuerURL.class::cast).findAny().map(IssuerURL::getvalue).orElse(null);
            final String paRequest = valueList.stream().filter(PaRequest.class::isInstance).map(PaRequest.class::cast).findAny().map(PaRequest::getvalue).orElse(null);
            req3dInfo.setIssuerUrl(issuerURL);
            req3dInfo.setPaRequest(paRequest);
        }
        return req3dInfo;

    }
}
