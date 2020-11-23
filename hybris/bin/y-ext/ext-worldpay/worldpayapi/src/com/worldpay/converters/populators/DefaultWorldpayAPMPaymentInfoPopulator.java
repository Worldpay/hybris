package com.worldpay.converters.populators;

import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class DefaultWorldpayAPMPaymentInfoPopulator implements Populator<WorldpayAPMPaymentInfoModel, CCPaymentInfoData> {

    @Override
    public void populate(final WorldpayAPMPaymentInfoModel source, final CCPaymentInfoData target) throws ConversionException {
        target.setId(source.getPk().toString());
        target.setIsAPM(Boolean.TRUE);
        target.setApmName(source.getApmConfiguration().getName());
        target.setSubscriptionId(source.getSubscriptionId());
        target.setCardNumber(source.getObfuscatedCardNumber());
    }
}
