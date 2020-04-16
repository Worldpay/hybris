package com.worldpay.facades.impl;

import com.worldpay.core.services.APMConfigurationLookupService;
import com.worldpay.core.services.WorldpayBankConfigurationLookupService;
import com.worldpay.facades.BankConfigurationData;
import com.worldpay.facades.WorldpayBankConfigurationFacade;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.model.WorldpayBankConfigurationModel;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;
import java.util.Optional;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayBankConfigurationFacade implements WorldpayBankConfigurationFacade {

    private final APMConfigurationLookupService apmConfigurationLookupService;
    private final WorldpayBankConfigurationLookupService worldpayBankConfigurationLookupService;
    private final Converter<WorldpayBankConfigurationModel, BankConfigurationData> bankConfigurationModelBankConfigurationDataConverter;

    public DefaultWorldpayBankConfigurationFacade(final APMConfigurationLookupService apmConfigurationLookupService,
                                                  final WorldpayBankConfigurationLookupService worldpayBankConfigurationLookupService,
                                                  final Converter<WorldpayBankConfigurationModel, BankConfigurationData> bankConfigurationModelBankConfigurationDataConverter) {
        this.apmConfigurationLookupService = apmConfigurationLookupService;
        this.worldpayBankConfigurationLookupService = worldpayBankConfigurationLookupService;
        this.bankConfigurationModelBankConfigurationDataConverter = bankConfigurationModelBankConfigurationDataConverter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BankConfigurationData> getBankConfigurationForAPMCode(final String apmCode) {
        final List<WorldpayBankConfigurationModel> activeBankConfigurationsForCode = worldpayBankConfigurationLookupService.getActiveBankConfigurationsForCode(apmCode);
        return Converters.convertAll(activeBankConfigurationsForCode, bankConfigurationModelBankConfigurationDataConverter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBankTransferApm(final String paymentMethod) {
        return Optional.ofNullable(apmConfigurationLookupService.getAPMConfigurationForCode(paymentMethod))
                .map(WorldpayAPMConfigurationModel::getBank)
                .orElse(false);
    }
}
