package com.worldpay.service.request.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.service.request.ServiceRequest;

/**
 * Interface defining the methods that must be implemented by a ServiceRequestTransformer
 * <p/>
 * <p>ServiceRequestTransformers must be able to turn the request object given to them into an instance of {@link PaymentService} which is the common object to be
 * translated into xml and then sent to Worldpay</p>
 */
public interface ServiceRequestTransformer {

    /**
     * Transform the provided request into a PaymentService that can be sent to Worldpay
     *
     * @param request request to be transformed
     * @return an instance of {@link PaymentService} to be sent to Worldpay
     * @throws WorldpayModelTransformationException if there are issues while transforming the request into the internal model object
     */
    PaymentService transform(ServiceRequest request) throws WorldpayModelTransformationException;
}
