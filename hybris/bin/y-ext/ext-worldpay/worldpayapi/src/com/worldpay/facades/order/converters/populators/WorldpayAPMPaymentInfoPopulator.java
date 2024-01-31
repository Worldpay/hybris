package com.worldpay.facades.order.converters.populators;

import com.worldpay.facades.order.data.WorldpayAPMPaymentInfoData;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Optional;

/**
 * Populating AbstractOrderData from AbstractOrderModel
 */
public class WorldpayAPMPaymentInfoPopulator implements Populator<AbstractOrderModel, AbstractOrderData> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final AbstractOrderModel source, final AbstractOrderData target) throws ConversionException {
        final PaymentInfoModel paymentInfoModel = source.getPaymentInfo();
        if (isAPM(paymentInfoModel)) {
            final WorldpayAPMPaymentInfoModel worldpayAPMPaymentInfoModel = (WorldpayAPMPaymentInfoModel) paymentInfoModel;
            final WorldpayAPMConfigurationModel worldpayAPMConfigurationModel = worldpayAPMPaymentInfoModel.getApmConfiguration();
            if (worldpayAPMConfigurationModel != null) {
                final WorldpayAPMPaymentInfoData worldpayAPMPaymentInfoData = new WorldpayAPMPaymentInfoData();
                worldpayAPMPaymentInfoData.setName(worldpayAPMConfigurationModel.getName());
                Optional.ofNullable(worldpayAPMPaymentInfoModel.getSubscriptionId())
                    .ifPresent(worldpayAPMPaymentInfoData::setSubscriptionId);
                if (source instanceof CartModel) {
                    worldpayAPMPaymentInfoData.setApmCode(((CartModel)source).getApmCode());
                    worldpayAPMPaymentInfoData.setApmName(((CartModel)source).getApmName());
                }
                target.setWorldpayAPMPaymentInfo(worldpayAPMPaymentInfoData);
            }
        }
    }

    protected boolean isAPM(final PaymentInfoModel paymentInfoModel) {
        return paymentInfoModel instanceof WorldpayAPMPaymentInfoModel;
    }
}
