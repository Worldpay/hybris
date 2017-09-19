package com.worldpay.service.request.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.helper.InternalModelObject;

/**
 * Interface to provide functionality to transform the existing object into an internal model representation
 */
public interface InternalModelTransformer {

    /**
     * Transform the existing object into an internal model object representation. If the object is a complex type then encapsulated objects may need to have this
     * method invoked recursively to generate the full model
     *
     * @return An {@link InternalModelObject} representation of this external facing model object
     * @throws WorldpayModelTransformationException if there has been some issue transforming the object
     */
    InternalModelObject transformToInternalModel() throws WorldpayModelTransformationException;
}
