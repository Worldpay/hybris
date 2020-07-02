package com.worldpay.converters.populators;

import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class WorldpayCCPaymentInfoPopulator implements Populator<CreditCardPaymentInfoModel, CCPaymentInfoData> {

    @Override
    public void populate(final CreditCardPaymentInfoModel source, final CCPaymentInfoData target) throws ConversionException {
        target.setBin(source.getBin());
    }
}
