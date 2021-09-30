package com.worldpay.converters.populators.internal.model.threeds2;

import com.worldpay.data.threeds2.AuthenticationRiskData;
import com.worldpay.data.threeds2.RiskData;
import com.worldpay.data.threeds2.ShopperAccountRiskData;
import com.worldpay.data.threeds2.TransactionRiskData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.RiskData} with the information of a {@link RiskData}
 */
public class RiskDataPopulator implements Populator<RiskData, com.worldpay.internal.model.RiskData> {

    protected final Converter<AuthenticationRiskData, com.worldpay.internal.model.AuthenticationRiskData> internalAuthenticationRiskDataConverter;
    protected final Converter<ShopperAccountRiskData, com.worldpay.internal.model.ShopperAccountRiskData> internalShopperAccountRiskDataConverter;
    protected final Converter<TransactionRiskData, com.worldpay.internal.model.TransactionRiskData> internalTransactionRiskDataConverter;

    public RiskDataPopulator(final Converter<AuthenticationRiskData, com.worldpay.internal.model.AuthenticationRiskData> internalAuthenticationRiskDataConverter,
                                final Converter<ShopperAccountRiskData, com.worldpay.internal.model.ShopperAccountRiskData> internalShopperAccountRiskDataConverter,
                                final Converter<TransactionRiskData, com.worldpay.internal.model.TransactionRiskData> internalTransactionRiskDataConverter) {

        this.internalAuthenticationRiskDataConverter = internalAuthenticationRiskDataConverter;
        this.internalShopperAccountRiskDataConverter = internalShopperAccountRiskDataConverter;
        this.internalTransactionRiskDataConverter = internalTransactionRiskDataConverter;
    }

    /**
     * Populates the data from the {@link RiskData} to a {@link com.worldpay.internal.model.RiskData}
     *
     * @param source a {@link RiskData} from Worldpay
     * @param target a {@link com.worldpay.internal.model.RiskData} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final RiskData source, final com.worldpay.internal.model.RiskData target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target must not be null!");

        Optional.ofNullable(source.getAuthenticationRiskData())
            .map(internalAuthenticationRiskDataConverter::convert)
            .ifPresent(target::setAuthenticationRiskData);

        Optional.ofNullable(source.getShopperAccountRiskData())
            .map(internalShopperAccountRiskDataConverter::convert)
            .ifPresent(target::setShopperAccountRiskData);

        Optional.ofNullable(source.getTransactionRiskData())
            .map(internalTransactionRiskDataConverter::convert)
            .ifPresent(target::setTransactionRiskData);
    }
}
