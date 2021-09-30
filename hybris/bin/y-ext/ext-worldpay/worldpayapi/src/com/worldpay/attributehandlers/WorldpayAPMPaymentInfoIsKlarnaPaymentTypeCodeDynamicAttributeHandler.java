package com.worldpay.attributehandlers;

import com.worldpay.service.payment.WorldpayKlarnaService;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;

public class WorldpayAPMPaymentInfoIsKlarnaPaymentTypeCodeDynamicAttributeHandler implements DynamicAttributeHandler<Boolean, WorldpayAPMPaymentInfoModel> {

    private final WorldpayKlarnaService worldpayKlarnaService;

    public WorldpayAPMPaymentInfoIsKlarnaPaymentTypeCodeDynamicAttributeHandler(final WorldpayKlarnaService worldpayKlarnaService) {
        this.worldpayKlarnaService = worldpayKlarnaService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean get(final WorldpayAPMPaymentInfoModel model) {
        return worldpayKlarnaService.isKlarnaPaymentType(model.getApmConfiguration().getCode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(final WorldpayAPMPaymentInfoModel model, final Boolean paymentTypeCode) {
        throw new UnsupportedOperationException("Attribute isKlarna is not writable");
    }
}
