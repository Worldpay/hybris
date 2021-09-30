package com.worldpay.converters.populators.internal.model.threeds2;

import com.worldpay.internal.model.*;
import com.worldpay.data.Date;
import com.worldpay.data.threeds2.ShopperAccountRiskData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.ShopperAccountRiskData} with the information of a {@link ShopperAccountRiskData}
 */
public class ShopperAccountRiskDataPopulator implements Populator<ShopperAccountRiskData, com.worldpay.internal.model.ShopperAccountRiskData> {

    protected final Converter<Date, com.worldpay.internal.model.Date> internalDateConverter;

    public ShopperAccountRiskDataPopulator(final Converter<Date, com.worldpay.internal.model.Date> internalDateConverter) {
        this.internalDateConverter = internalDateConverter;
    }

    /**
     * Populates the data from the {@link ShopperAccountRiskData} to a {@link com.worldpay.internal.model.ShopperAccountRiskData}
     *
     * @param source a {@link ShopperAccountRiskData} from Worldpay
     * @param target a {@link com.worldpay.internal.model.ShopperAccountRiskData} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final ShopperAccountRiskData source, final com.worldpay.internal.model.ShopperAccountRiskData target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target must not be null!");

        target.setTransactionsAttemptedLastDay(source.getTransactionsAttemptedLastDay());
        target.setTransactionsAttemptedLastYear(source.getTransactionsAttemptedLastYear());
        target.setPurchasesCompletedLastSixMonths(source.getPurchasesCompletedLastSixMonths());
        target.setAddCardAttemptsLastDay(source.getAddCardAttemptsLastDay());
        target.setPreviousSuspiciousActivity(source.getPreviousSuspiciousActivity());
        target.setShippingNameMatchesAccountName(source.getShippingNameMatchesAccountName());
        target.setShopperAccountAgeIndicator(source.getShopperAccountAgeIndicator());
        target.setShopperAccountChangeIndicator(source.getShopperAccountChangeIndicator());
        target.setShopperAccountPasswordChangeIndicator(source.getShopperAccountPasswordChangeIndicator());
        target.setShopperAccountShippingAddressUsageIndicator(source.getShopperAccountShippingAddressUsageIndicator());
        target.setShopperAccountPaymentAccountIndicator(source.getShopperAccountPaymentAccountIndicator());

        Optional.ofNullable(source.getShopperAccountCreationDate()).ifPresent(date -> {
            final ShopperAccountCreationDate accountCreationDate = new ShopperAccountCreationDate();
            accountCreationDate.setDate(internalDateConverter.convert(date));
            target.setShopperAccountCreationDate(accountCreationDate);
        });

        Optional.ofNullable(source.getShopperAccountShippingAddressFirstUseDate()).ifPresent(date -> {
            final ShopperAccountShippingAddressFirstUseDate accountShippingAddressFirstUseDate = new ShopperAccountShippingAddressFirstUseDate();
            accountShippingAddressFirstUseDate.setDate(internalDateConverter.convert(date));
            target.setShopperAccountShippingAddressFirstUseDate(accountShippingAddressFirstUseDate);
        });

        Optional.ofNullable(source.getShopperAccountModificationDate()).ifPresent(date -> {
            final ShopperAccountModificationDate accountModificationDate = new ShopperAccountModificationDate();
            accountModificationDate.setDate(internalDateConverter.convert(date));
            target.setShopperAccountModificationDate(accountModificationDate);
        });

        Optional.ofNullable(source.getShopperAccountPaymentAccountFirstUseDate()).ifPresent(date -> {
            final ShopperAccountPaymentAccountFirstUseDate shopperAccountPaymentAccountFirstUseDate = new ShopperAccountPaymentAccountFirstUseDate();
            shopperAccountPaymentAccountFirstUseDate.setDate(internalDateConverter.convert(date));
            target.setShopperAccountPaymentAccountFirstUseDate(shopperAccountPaymentAccountFirstUseDate);
        });

        Optional.ofNullable(source.getShopperAccountPasswordChangeDate()).ifPresent(date -> {
            final ShopperAccountPasswordChangeDate shopperAccountPasswordChangeDate = new ShopperAccountPasswordChangeDate();
            shopperAccountPasswordChangeDate.setDate(internalDateConverter.convert(date));
            target.setShopperAccountPasswordChangeDate(shopperAccountPasswordChangeDate);
        });
    }
}
