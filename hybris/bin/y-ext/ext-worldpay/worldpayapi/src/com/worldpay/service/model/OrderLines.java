package com.worldpay.service.model;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.helper.InternalModelObject;
import com.worldpay.service.request.transform.InternalModelTransformer;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.List;

public class OrderLines implements InternalModelTransformer, Serializable {
    private static final Logger LOG = Logger.getLogger(OrderLines.class);

    private String orderTaxAmount;
    private String termsURL;
    private List<LineItem> lineItems;

    public OrderLines(final String orderTaxAmount, final String termsURL, final List<LineItem> lineItems) {
        this.orderTaxAmount = orderTaxAmount;
        this.termsURL = termsURL;
        this.lineItems = lineItems;
    }

    @Override
    public InternalModelObject transformToInternalModel() throws WorldpayModelTransformationException {
        final com.worldpay.internal.model.OrderLines intOrderLines = new com.worldpay.internal.model.OrderLines();
        intOrderLines.setOrderTaxAmount(orderTaxAmount);
        intOrderLines.setTermsURL(termsURL);

        if (lineItems != null && !lineItems.isEmpty()) {
            final List<com.worldpay.internal.model.LineItem> intLineItems = intOrderLines.getLineItem();
            lineItems.forEach(lineItem -> {
                try {
                    intLineItems.add((com.worldpay.internal.model.LineItem) lineItem.transformToInternalModel());
                } catch (final WorldpayModelTransformationException e) {
                    LOG.error("Error during transformation of line items", e);
                }
            });
        }

        return intOrderLines;
    }

    public String getOrderTaxAmount() {
        return orderTaxAmount;
    }

    public String getTermsURL() {
        return termsURL;
    }

    public List<LineItem> getLineItems() {
        return lineItems;
    }
}
