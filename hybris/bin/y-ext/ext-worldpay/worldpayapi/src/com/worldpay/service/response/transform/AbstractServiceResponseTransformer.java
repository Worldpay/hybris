package com.worldpay.service.response.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.service.http.ServiceReply;
import com.worldpay.service.response.ServiceResponse;

/**
 * Abstract implementation of the ServiceResponseTransformer to extract cookie information
 */
public abstract class AbstractServiceResponseTransformer implements ServiceResponseTransformer {

    /**
     * (non-Javadoc)
     *
     * @see com.worldpay.service.response.transform.ServiceResponseTransformer#transform(com.worldpay.service.http.ServiceReply)
     */
    @Override
    public ServiceResponse transform(ServiceReply reply) throws WorldpayModelTransformationException {
        ServiceResponse response = transform(reply.getPaymentService());
        if (response != null) {
            response.setCookie(reply.getCookie());
        }
        return response;
    }

    public abstract ServiceResponse transform(PaymentService paymentService) throws WorldpayModelTransformationException;
}
