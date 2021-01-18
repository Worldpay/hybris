package com.worldpay.service.model;

import com.worldpay.internal.helper.InternalModelObject;
import com.worldpay.internal.model.Reference;
import com.worldpay.service.request.transform.InternalModelTransformer;

import java.io.Serializable;

public class LineItemReference implements InternalModelTransformer, Serializable {
    private final String id;
    private final String value;

    public LineItemReference(final String id, final String value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public InternalModelObject transformToInternalModel() {
        final Reference intReference = new Reference();
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
