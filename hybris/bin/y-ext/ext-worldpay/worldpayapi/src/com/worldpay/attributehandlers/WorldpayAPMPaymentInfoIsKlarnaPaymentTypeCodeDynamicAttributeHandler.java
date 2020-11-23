package com.worldpay.attributehandlers;

import com.worldpay.klarna.WorldpayKlarnaUtils;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;

public class WorldpayAPMPaymentInfoIsKlarnaPaymentTypeCodeDynamicAttributeHandler implements DynamicAttributeHandler<Boolean, WorldpayAPMPaymentInfoModel> {
    private final WorldpayKlarnaUtils worldpayKlarnaUtils;

    public WorldpayAPMPaymentInfoIsKlarnaPaymentTypeCodeDynamicAttributeHandler(final WorldpayKlarnaUtils worldpayKlarnaUtils) {
        this.worldpayKlarnaUtils = worldpayKlarnaUtils;
    }

    @Override
    public Boolean get(final WorldpayAPMPaymentInfoModel model) {
        return worldpayKlarnaUtils.isKlarnaPaymentType(model.getApmConfiguration().getCode());
    }

    @Override
    public void set(final WorldpayAPMPaymentInfoModel model, final Boolean paymentTypeCode) {
        throw new UnsupportedOperationException("Attribute isKlarna is not writable");
    }
}
