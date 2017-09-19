package com.worldpay.worldpayresponsemock.merchant.impl;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.merchant.impl.DefaultWorldpayMerchantInfoService;
import com.worldpay.worldpayresponsemock.merchant.WorldpayResponseMockMerchantInfoService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayResponseMockMerchantInfoService extends DefaultWorldpayMerchantInfoService implements WorldpayResponseMockMerchantInfoService {

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getAllMerchantCodes(final String siteUid) {
        return getSessionService().executeInLocalView(new SessionExecutionBody() {
            @Override
            public Object execute() {
                getBaseSiteService().setCurrentBaseSite(siteUid, false);
                final Map<String, WorldpayMerchantConfigData> merchantConfiguration = getWorldpayMerchantConfigDataService().getMerchantConfiguration();
                // Obtains the merchant codes and returns it as a list
                return merchantConfiguration.values().stream().map(WorldpayMerchantConfigData::getCode).collect(Collectors.toSet());
            }
        });
    }
}
