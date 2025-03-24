package com.worldpay.service.response.transform;

import com.worldpay.data.ExemptionResponseInfo;
import com.worldpay.data.RedirectReference;
import com.worldpay.data.Request3DInfo;
import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.*;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import com.worldpay.service.response.ServiceResponse;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;
import java.util.Optional;

/**
 * Specific class for transforming a {@link PaymentService} into a {@link DirectAuthoriseServiceResponse} object
 */
public class DirectAuthoriseResponseTransformer extends AbstractServiceResponseTransformer {

    protected final Converter<ExemptionResponse, ExemptionResponseInfo> exemptionResponseReverseConverter;

    public DirectAuthoriseResponseTransformer(final ServiceResponseTransformerHelper serviceResponseTransformerHelper,
                                              final Converter<ExemptionResponse, ExemptionResponseInfo> exemptionResponseReverseConverter) {
        super(serviceResponseTransformerHelper);
        this.exemptionResponseReverseConverter = exemptionResponseReverseConverter;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.worldpay.service.response.transform.AbstractServiceResponseTransformer#transform(com.worldpay.internal.model.PaymentService)
     */
    @Override
    public ServiceResponse transform(final PaymentService paymentServiceReply) throws WorldpayModelTransformationException {

        final Reply intReply = paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()
            .stream()
            .filter(Reply.class::isInstance)
            .map(Reply.class::cast)
            .findAny()
            .orElseThrow(() -> new WorldpayModelTransformationException("Reply has no reply message or the reply type is not the expected one"));

        final DirectAuthoriseServiceResponse authResponse = new DirectAuthoriseServiceResponse();
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

            final List<Object> intOrderStatusElements = intOrderStatus.getReferenceOrBankAccountOrApmEnrichedDataOrErrorOrPaymentOrQrCodeOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrExemptionResponseOrInstalmentPlanOrRetryDetailsOrOrderModificationOrJournalOrRequestInfoOrChallengeRequiredOrFxApprovalRequiredOrPbbaRTPOrContentOrJournalTypeDetailOrTokenOrDateOrEchoDataOrPayAsOrderUseNewOrderCodeOrSelectedSchemeOrAuthenticateResponse();
            for (final Object orderStatusType : intOrderStatusElements) {
                transformOrderStatus(authResponse, intOrderStatus, orderStatusType);
            }

        return authResponse;
    }

    private void transformOrderStatus(final DirectAuthoriseServiceResponse authResponse, final OrderStatus intOrderStatus, final Object orderStatusType) throws WorldpayModelTransformationException {
        if (orderStatusType == null) {
            throw new WorldpayModelTransformationException("No order status type returned in Worldpay reply message");
        }
        final List<Object> intOrderStatuses = intOrderStatus.getReferenceOrBankAccountOrApmEnrichedDataOrErrorOrPaymentOrQrCodeOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrExemptionResponseOrInstalmentPlanOrRetryDetailsOrOrderModificationOrJournalOrRequestInfoOrChallengeRequiredOrFxApprovalRequiredOrPbbaRTPOrContentOrJournalTypeDetailOrTokenOrDateOrEchoDataOrPayAsOrderUseNewOrderCodeOrSelectedSchemeOrAuthenticateResponse();

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
                .map(reference -> {
                    final RedirectReference redirectReference = new RedirectReference();
                    redirectReference.setValue(reference.getvalue());
                    redirectReference.setId(reference.getId());
                    return redirectReference;
                })
                .ifPresent(authResponse::setRedirectReference);

        intOrderStatuses.stream()
                .filter(Payment.class::isInstance)
                .map(Payment.class::cast)
                .findAny()
                .map(serviceResponseTransformerHelper::buildPaymentReply)
                .ifPresent(authResponse::setPaymentReply);

        intOrderStatuses.stream()
                .filter(Token.class::isInstance)
                .map(Token.class::cast)
                .findAny()
                .map(serviceResponseTransformerHelper::buildTokenReply)
                .ifPresent(authResponse::setToken);

        intOrderStatuses.stream()
                .filter(EchoData.class::isInstance)
                .map(EchoData.class::cast)
                .findAny()
                .map(EchoData::getvalue)
                .ifPresent(authResponse::setEchoData);

        intOrderStatuses.stream()
            .filter(ExemptionResponse.class::isInstance)
            .map(ExemptionResponse.class::cast)
            .findAny()
            .map(this::buildExemptionResponseInfo)
            .ifPresent(exemptionResponseInfo -> {
                authResponse.setExemptionResponseInfo(exemptionResponseInfo);
                Optional.ofNullable(authResponse.getPaymentReply())
                    .ifPresent(paymentReply -> paymentReply.setExemptionResponseInfo(exemptionResponseInfo));
            });
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
            req3dInfo.setIssuerUrl(intRequest3dSecure.getMpiURL());
            req3dInfo.setPaRequest(intRequest3dSecure.getMpiRequest());
        }
        return req3dInfo;

    }

    private ExemptionResponseInfo buildExemptionResponseInfo(final ExemptionResponse intExemptionResponse) {
        return exemptionResponseReverseConverter.convert(intExemptionResponse);
    }

}
