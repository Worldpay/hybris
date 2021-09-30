package com.worldpay.core.services.impl;

import com.worldpay.core.dao.WorldpayAPMConfigurationDao;
import com.worldpay.core.services.APMConfigurationLookupService;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.service.model.payment.PaymentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * {@inheritDoc}
 */
public class DefaultAPMConfigurationLookupService implements APMConfigurationLookupService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAPMConfigurationLookupService.class);

    protected final WorldpayAPMConfigurationDao worldpayAPMConfigurationDao;

    public DefaultAPMConfigurationLookupService(final WorldpayAPMConfigurationDao worldpayAPMConfigurationDao) {
        this.worldpayAPMConfigurationDao = worldpayAPMConfigurationDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorldpayAPMConfigurationModel getAPMConfigurationForCode(final String paymentTypeCode) {
        final Map<String, String> queryParams = Collections.singletonMap(WorldpayAPMConfigurationModel.CODE, paymentTypeCode);
        final List<WorldpayAPMConfigurationModel> worldpayAPMConfigurationModels = worldpayAPMConfigurationDao.find(queryParams);
        if (worldpayAPMConfigurationModels.isEmpty()) {
            LOG.error("Could not find unique WorldpayAlternativePaymentMethod with code [{}]", paymentTypeCode);
            return null;
        } else {
            final WorldpayAPMConfigurationModel worldpayAPMConfigurationModel = worldpayAPMConfigurationModels.get(0);
            if (worldpayAPMConfigurationModels.size() > 1) {
                LOG.error("Multiple WorldpayAPMConfiguration with code [{}], returning first [{}]", paymentTypeCode, worldpayAPMConfigurationModel.getCode());
                return worldpayAPMConfigurationModel;
            } else {
                return worldpayAPMConfigurationModel;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getAllApmPaymentTypeCodes() {
        final Set<String> apmPaymentTypesCodes = new HashSet<>();
        for (final WorldpayAPMConfigurationModel worldpayAPMConfiguration : worldpayAPMConfigurationDao.find()) {
            final PaymentType paymentType = PaymentType.getPaymentType(worldpayAPMConfiguration.getCode());
            if (paymentType != null) {
                apmPaymentTypesCodes.add(paymentType.getMethodCode());
            }
        }
        return apmPaymentTypesCodes;
    }

}
