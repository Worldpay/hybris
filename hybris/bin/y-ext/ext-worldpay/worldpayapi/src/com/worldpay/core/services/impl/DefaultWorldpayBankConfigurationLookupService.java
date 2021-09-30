package com.worldpay.core.services.impl;

import com.google.common.collect.ImmutableMap;
import com.worldpay.core.services.APMConfigurationLookupService;
import com.worldpay.core.services.WorldpayBankConfigurationLookupService;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.model.WorldpayBankConfigurationModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayBankConfigurationLookupService implements WorldpayBankConfigurationLookupService {

    protected final APMConfigurationLookupService apmConfigurationLookupService;
    protected final GenericDao<WorldpayBankConfigurationModel> worldpayBankConfigurationGenericDao;

    public DefaultWorldpayBankConfigurationLookupService(final APMConfigurationLookupService apmConfigurationLookupService,
                                                         final GenericDao<WorldpayBankConfigurationModel> worldpayBankConfigurationGenericDao) {
        this.apmConfigurationLookupService = apmConfigurationLookupService;
        this.worldpayBankConfigurationGenericDao = worldpayBankConfigurationGenericDao;
    }

    /**
     * {@inheritDoc}
     *
     * @param apmCode string representing the APM code
     * @return
     */
    @Override
    public List<WorldpayBankConfigurationModel> getActiveBankConfigurationsForCode(final String apmCode) {
        final WorldpayAPMConfigurationModel apmConfigurationForCode = apmConfigurationLookupService.getAPMConfigurationForCode(apmCode);
        final Map<String, Object> queryParams = new HashMap<>();
        queryParams.put(WorldpayBankConfigurationModel.APM, apmConfigurationForCode);
        queryParams.put(WorldpayBankConfigurationModel.ACTIVE, Boolean.TRUE);
        return worldpayBankConfigurationGenericDao.find(queryParams);
    }

    /**
     * {@inheritDoc}
     *
     * @param bankCode string representing the bank code
     * @return WorldpayBankConfigurationModel
     */
    @Override
    public WorldpayBankConfigurationModel getBankConfigurationForBankCode(final String bankCode) {
        if (isBlank(bankCode)) {
            return null;
        }
        final List<WorldpayBankConfigurationModel> bankList = worldpayBankConfigurationGenericDao.find(ImmutableMap.of(WorldpayBankConfigurationModel.CODE, bankCode));
        return bankList.get(0);
    }
}
