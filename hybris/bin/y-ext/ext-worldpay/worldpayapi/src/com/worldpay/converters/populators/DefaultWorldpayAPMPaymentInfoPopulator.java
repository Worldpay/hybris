package com.worldpay.converters.populators;

import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.Optional;

/**
 * Populates the APM attributes from {@link WorldpayAPMPaymentInfoModel} to {@link CCPaymentInfoData}
 */
public class DefaultWorldpayAPMPaymentInfoPopulator implements Populator<WorldpayAPMPaymentInfoModel, CCPaymentInfoData> {

    protected final Converter<AddressModel, AddressData> addressConverter;

    public DefaultWorldpayAPMPaymentInfoPopulator(final Converter<AddressModel, AddressData> addressConverter) {
        this.addressConverter = addressConverter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final WorldpayAPMPaymentInfoModel source, final CCPaymentInfoData target) throws ConversionException {
        target.setId(source.getPk().toString());
        target.setIsAPM(Boolean.TRUE);
        target.setSubscriptionId(source.getSubscriptionId());
        target.setCardNumber(source.getObfuscatedCardNumber());
        populateExpiryDate(source, target);
        target.setSaved(source.isSaved());
        Optional.ofNullable(source.getBillingAddress())
                .map(addressConverter::convert)
                .ifPresent(target::setBillingAddress);
        Optional.ofNullable(source.getApmConfiguration())
                .ifPresent(apmConfig -> {
                    target.setApmName(apmConfig.getName());
                    target.setCardType(apmConfig.getCode());
                });
    }

    protected void populateExpiryDate(final WorldpayAPMPaymentInfoModel source, final CCPaymentInfoData target) {
        final boolean shouldDeriveExpiry = source.getExpiryDate() != null &&
                (StringUtils.isBlank(source.getExpiryYear()) || StringUtils.isBlank(source.getExpiryMonth()));
        if (shouldDeriveExpiry) {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(source.getExpiryDate());
            target.setExpiryMonth(String.valueOf(calendar.get(Calendar.MONTH)));
            target.setExpiryYear(String.valueOf(calendar.get(Calendar.YEAR)));
            return;
        }
        target.setExpiryMonth(source.getExpiryMonth());
        target.setExpiryYear(source.getExpiryYear());
    }
}
