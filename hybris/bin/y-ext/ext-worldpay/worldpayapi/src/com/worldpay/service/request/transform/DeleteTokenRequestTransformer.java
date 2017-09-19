package com.worldpay.service.request.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.Modify;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.PaymentTokenDelete;
import com.worldpay.service.request.DeleteTokenServiceRequest;
import com.worldpay.service.request.ServiceRequest;

public class DeleteTokenRequestTransformer implements ServiceRequestTransformer {
    @Override
    public PaymentService transform(ServiceRequest request) throws WorldpayModelTransformationException {
        if (request == null) {
            throw new WorldpayModelTransformationException("Request provided to delete token is invalid.");
        }

        final DeleteTokenServiceRequest deleteTokenRequest = (DeleteTokenServiceRequest) request;

        final PaymentService paymentService = new PaymentService();
        paymentService.setMerchantCode(deleteTokenRequest.getMerchantInfo().getMerchantCode());
        paymentService.setVersion(deleteTokenRequest.getWorldpayConfig().getVersion());

        final PaymentTokenDelete deleteToken = (PaymentTokenDelete) deleteTokenRequest.getDeleteTokenRequest().transformToInternalModel();

        final Modify modify = new Modify();
        modify.getOrderModificationOrBatchModificationOrAccountBatchModificationOrFuturePayAgreementModificationOrPaymentTokenUpdateOrPaymentTokenDelete().add(deleteToken);
        paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(modify);

        return paymentService;
    }
}
