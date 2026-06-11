package com.worldpay.order.converters.populator;

import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.time.TimeService;


import java.util.Optional;

/**
 * Populator from AbstractOrderModel to AbstractOrderData
 */
public class WorldpayAbstractOrderPopulator implements Populator<AbstractOrderModel, AbstractOrderData>{


    protected final Converter<AddressModel, AddressData> addressConverter;

    protected WorldpayAbstractOrderPopulator(final Converter<AddressModel, AddressData> addressConverter) {
        this.addressConverter = addressConverter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final AbstractOrderModel source, final AbstractOrderData target) throws ConversionException {
        target.setWorldpayOrderCode(source.getWorldpayOrderCode());
        target.setWorldpayDeclineCode(source.getWorldpayDeclineCode());
        final PaymentInfoModel paymentInfo = source.getPaymentInfo();
        if (paymentInfo != null && isNotCreditCard(paymentInfo) && paymentInfo.getBillingAddress() != null) {
            // In the OrderConfirmationPage (b2c), orderData.PaymentInfo is "required" to be a CCPaymentInfoData, and cannot be null.
            final CCPaymentInfoData ccPaymentInfoData = new CCPaymentInfoData();
            ccPaymentInfoData.setBillingAddress(addressConverter.convert(paymentInfo.getBillingAddress()));
            Optional.ofNullable(paymentInfo.getPk())
                    .map(Object::toString)
                    .ifPresent(ccPaymentInfoData::setId);
            target.setPaymentInfo(ccPaymentInfoData);
        }
    }

    protected boolean isNotCreditCard(final PaymentInfoModel paymentInfo) {
        return !(paymentInfo instanceof CreditCardPaymentInfoModel);
    }


}
