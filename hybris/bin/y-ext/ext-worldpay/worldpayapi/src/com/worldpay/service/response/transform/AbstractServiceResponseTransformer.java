package com.worldpay.service.response.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.service.http.ServiceReply;
import com.worldpay.service.response.ServiceResponse;

import java.util.Optional;

/**
 * Abstract implementation of the ServiceResponseTransformer to extract cookie information
 */
public abstract class AbstractServiceResponseTransformer implements ServiceResponseTransformer {

    protected final ServiceResponseTransformerHelper serviceResponseTransformerHelper;

    protected AbstractServiceResponseTransformer(final ServiceResponseTransformerHelper serviceResponseTransformerHelper) {
        this.serviceResponseTransformerHelper = serviceResponseTransformerHelper;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.worldpay.service.response.transform.ServiceResponseTransformer#transform(com.worldpay.service.http.ServiceReply)
     */
    @Override
    public ServiceResponse transform(final ServiceReply reply) throws WorldpayModelTransformationException {
        final ServiceResponse response = transform(reply.getPaymentService());
        Optional.ofNullable(response)
            .ifPresent(serviceResponse -> serviceResponse.setCookie(reply.getCookie()));
        return response;
    }

    public abstract ServiceResponse transform(PaymentService paymentService) throws WorldpayModelTransformationException;

}
