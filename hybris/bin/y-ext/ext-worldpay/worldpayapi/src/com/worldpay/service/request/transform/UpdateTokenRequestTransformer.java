package com.worldpay.service.request.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.Modify;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.PaymentTokenUpdate;
import com.worldpay.service.request.ServiceRequest;
import com.worldpay.service.request.UpdateTokenServiceRequest;

public class UpdateTokenRequestTransformer implements ServiceRequestTransformer {

    @Override
    public PaymentService transform(final ServiceRequest request) throws WorldpayModelTransformationException {
        if (request == null) {
            throw new WorldpayModelTransformationException("Request provided to update token is invalid.");
        }

        final UpdateTokenServiceRequest updateTokenRequest = (UpdateTokenServiceRequest) request;

        final PaymentService paymentService = new PaymentService();
        paymentService.setMerchantCode(updateTokenRequest.getMerchantInfo().getMerchantCode());
        paymentService.setVersion(updateTokenRequest.getWorldpayConfig().getVersion());

        final PaymentTokenUpdate updateToken = (PaymentTokenUpdate) updateTokenRequest.getUpdateTokenRequest().transformToInternalModel();

        final Modify modify = new Modify();
        modify.getOrderModificationOrBatchModificationOrAccountBatchModificationOrFuturePayAgreementModificationOrPaymentTokenUpdateOrPaymentTokenDelete().add(updateToken);
        paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(modify);

        return paymentService;
    }
}
