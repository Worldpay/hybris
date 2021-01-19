
package com.worldpay.service.response.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.service.http.ServiceReply;
import com.worldpay.service.response.ServiceResponse;

/**
 * Interface defining the methods that must be implemented by a ServiceResponseTransformer
 * <p/>
 * <p>ServiceResponseTransformers must be able to turn the response object given to them into an instance of {@link ServiceResponse}</p>
 */
public interface ServiceResponseTransformer {

    /**
     * Transform a {@link ServiceReply} reply from Worldpay into a {@link ServiceResponse} that can be utilised by calling classes
     *
     * @param reply the reply from Worldpay to be transformed
     * @return an instance of {@link ServiceResponse} representing the reply
     * @throws WorldpayModelTransformationException if there are any issues while transforming the reply
     */
    ServiceResponse transform(ServiceReply reply) throws WorldpayModelTransformationException;
}
