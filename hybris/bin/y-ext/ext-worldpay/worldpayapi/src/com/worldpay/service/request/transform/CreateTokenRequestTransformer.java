package com.worldpay.service.request.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.PaymentTokenCreate;
import com.worldpay.internal.model.Submit;
import com.worldpay.service.request.CreateTokenServiceRequest;
import com.worldpay.service.request.ServiceRequest;

public class CreateTokenRequestTransformer implements ServiceRequestTransformer {

    /**
     * (non-Javadoc)
     *
     * @see ServiceRequestTransformer#transform(ServiceRequest)
     */
    @Override
    public PaymentService transform(final ServiceRequest request) throws WorldpayModelTransformationException {
        if (request == null) {
            throw new WorldpayModelTransformationException("Request provided to create token is invalid.");
        }
        final CreateTokenServiceRequest tokenRequest = (CreateTokenServiceRequest) request;

        final PaymentService paymentService = new PaymentService();
        paymentService.setMerchantCode(tokenRequest.getMerchantInfo().getMerchantCode());
        paymentService.setVersion(tokenRequest.getWorldpayConfig().getVersion());

        final PaymentTokenCreate paymentTokenCreate = (PaymentTokenCreate) tokenRequest.getCardTokenRequest().transformToInternalModel();
        final Submit submit = new Submit();
        submit.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreate().add(paymentTokenCreate);
        paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(submit);
        return paymentService;
    }
}
