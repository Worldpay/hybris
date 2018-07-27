package com.worldpay.core.services;

import com.worldpay.model.WorldpayBankConfigurationModel;

import java.util.List;

/**
 * Service providing lookup functionality for Bank configurations.
 *
 * @spring.bean worldpayBankConfigurationLookupService
 */
public interface WorldpayBankConfigurationLookupService {

    /**
     * Returns {@link WorldpayBankConfigurationModel} related to the APM code
     *
     * @param apmCode string representing the APM code
     * @return the list of active banks configured for the APM
     */
    List<WorldpayBankConfigurationModel> getActiveBankConfigurationsForCode(final String apmCode);

    /**
     * Returns {@link WorldpayBankConfigurationModel} related to the bank  code
     *
     * @param bankCode string representing the bank code
     * @return the bank with the given code
     */
    WorldpayBankConfigurationModel getBankConfigurationForBankCode(final String bankCode);
}
