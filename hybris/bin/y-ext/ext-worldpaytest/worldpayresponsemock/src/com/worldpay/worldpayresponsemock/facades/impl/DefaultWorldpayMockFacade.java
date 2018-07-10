package com.worldpay.worldpayresponsemock.facades.impl;

import com.worldpay.exception.WorldpayException;
import com.worldpay.internal.model.*;
import com.worldpay.service.marshalling.PaymentServiceMarshaller;
import com.worldpay.worldpayresponsemock.facades.WorldpayMockFacade;
import com.worldpay.worldpayresponsemock.responses.WorldpayCaptureResponseBuilder;
import com.worldpay.worldpayresponsemock.responses.WorldpayDirectAuthoriseResponseBuilder;
import com.worldpay.worldpayresponsemock.responses.WorldpayResponseBuilder;
import com.worldpay.worldpayresponsemock.responses.WorldpayTokenCreateResponseBuilder;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Facade to mock responses of Worldpay facade
 */
public class DefaultWorldpayMockFacade implements WorldpayMockFacade {

    private WorldpayResponseBuilder worldpayResponseBuilder;
    private WorldpayCaptureResponseBuilder worldpayCaptureResponseBuilder;
    private WorldpayDirectAuthoriseResponseBuilder worldpayDirectAuthoriseResponseBuilder;
    private WorldpayTokenCreateResponseBuilder worldpayTokenCreateResponseBuilder;
    private PaymentServiceMarshaller paymentServiceMarshaller;

    @Override
    public String buildResponse(PaymentService paymentServiceRequest, HttpServletRequest request) throws WorldpayException {
        if (requestContainsCapture(paymentServiceRequest)) {
            return paymentServiceMarshaller.marshal(worldpayCaptureResponseBuilder.buildCaptureResponse(paymentServiceRequest));
        }
        if (requestContainsSubmitOrderWithPaymentDetails(paymentServiceRequest)) {
            return paymentServiceMarshaller.marshal(worldpayDirectAuthoriseResponseBuilder.buildDirectResponse(paymentServiceRequest));
        }
        if (requestContainsSubmitOrderWithPaymentMethodMask(paymentServiceRequest)) {
            return paymentServiceMarshaller.marshal(worldpayResponseBuilder.buildRedirectResponse(paymentServiceRequest, request));
        }
        if (requestContainsTokenCreate(paymentServiceRequest)) {
            return paymentServiceMarshaller.marshal(worldpayTokenCreateResponseBuilder.buildTokenResponse(paymentServiceRequest));
        }
        return null;
    }

    protected boolean requestContainsTokenCreate(final PaymentService paymentServiceRequest) {
        if (paymentServiceRequest.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0) instanceof Submit) {
            Submit submit = (Submit) paymentServiceRequest.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
            final Object possibleTokenCreate = submit.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreate().get(0);
            return possibleTokenCreate instanceof PaymentTokenCreate;
        }
        return false;

    }

    private boolean requestContainsSubmitOrderWithPaymentMethodMask(PaymentService paymentServiceRequest) {
        if (paymentServiceRequest.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0) instanceof Submit) {
            Submit submit = (Submit) paymentServiceRequest.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
            final Object possibleOrder = submit.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreate().get(0);
            if (possibleOrder instanceof Order) {
                final List<Object> orderElements = ((Order) possibleOrder).getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrInfo3DSecureOrSession();
                return orderElements.stream().anyMatch(PaymentMethodMask.class::isInstance);
            }
        }
        return false;
    }

    private boolean requestContainsCapture(PaymentService request) {
        if (request.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0) instanceof Modify) {
            Modify modify = (Modify) request.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
            if (modify.getOrderModificationOrBatchModificationOrAccountBatchModificationOrFuturePayAgreementModificationOrPaymentTokenUpdateOrPaymentTokenDelete().get(0) instanceof OrderModification) {
                OrderModification orderModification = (OrderModification) modify.getOrderModificationOrBatchModificationOrAccountBatchModificationOrFuturePayAgreementModificationOrPaymentTokenUpdateOrPaymentTokenDelete().get(0);
                return orderModification.getCancelOrCaptureOrRefundOrRevokeOrAddBackOfficeCodeOrAuthoriseOrIncreaseAuthorisationOrCancelOrRefundOrDefendOrShopperWebformRefundDetailsOrExtendExpiryDateOrCancelRefundOrCancelRetry().get(0) instanceof Capture;
            }
        }
        return false;
    }

    private boolean requestContainsSubmitOrderWithPaymentDetails(PaymentService request) {
        if (request.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0) instanceof Submit) {
            Submit submit = (Submit) request.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
            final Object possibleOrder = submit.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreate().get(0);
            if (possibleOrder instanceof Order) {
                final List<Object> orderElements = ((Order) possibleOrder).getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrInfo3DSecureOrSession();
                return orderElements.stream().anyMatch(PaymentDetails.class::isInstance);
            }
        }
        return false;
    }

    @Required
    public void setWorldpayResponseBuilder(final WorldpayResponseBuilder worldpayResponseBuilder) {
        this.worldpayResponseBuilder = worldpayResponseBuilder;
    }

    @Required
    public void setWorldpayCaptureResponseBuilder(final WorldpayCaptureResponseBuilder worldpayCaptureResponseBuilder) {
        this.worldpayCaptureResponseBuilder = worldpayCaptureResponseBuilder;
    }

    @Required
    public void setWorldpayDirectAuthoriseResponseBuilder(final WorldpayDirectAuthoriseResponseBuilder worldpayDirectAuthoriseResponseBuilder) {
        this.worldpayDirectAuthoriseResponseBuilder = worldpayDirectAuthoriseResponseBuilder;
    }

    @Required
    public void setWorldpayTokenCreateResponseBuilder(final WorldpayTokenCreateResponseBuilder worldpayTokenCreateResponseBuilder) {
        this.worldpayTokenCreateResponseBuilder = worldpayTokenCreateResponseBuilder;
    }

    @Required
    public void setPaymentServiceMarshaller(final PaymentServiceMarshaller paymentServiceMarshaller) {
        this.paymentServiceMarshaller = paymentServiceMarshaller;
    }
}
