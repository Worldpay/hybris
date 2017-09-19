package com.worldpay.service.model;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.helper.InternalModelObject;
import com.worldpay.internal.model.Reference;
import com.worldpay.service.request.transform.InternalModelTransformer;

import java.io.Serializable;

public class LineItemReference implements InternalModelTransformer, Serializable {
    private String id;
    private String value;

    public LineItemReference(final String id, final String value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public InternalModelObject transformToInternalModel() throws WorldpayModelTransformationException {
        Reference intReference = new Reference();
        intReference.setId(id);
        intReference.setvalue(value);
        return intReference;
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}
