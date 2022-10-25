package com.worldpay.dao.impl;

import com.worldpay.constants.Worldpayb2cdemoConstants;
import com.worldpay.dao.WorldpayBinRangeDao;
import com.worldpay.model.WorldpayBinRangeModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collections;
import java.util.List;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayBinRangeDao implements WorldpayBinRangeDao {

    private FlexibleSearchService flexibleSearchService;
    
    private ConfigurationService configurationService;

    protected static final String QUERY = "SELECT {PK} FROM {WorldpayBinRange} WHERE {CardBinRangeStart} <= ?minBinRange AND {CardBinRangeEnd} >= ?maxBinRange";

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WorldpayBinRangeModel> findBinRanges(final String cardPrefix) {
        final int binRangeCardSize = configurationService.getConfiguration().getInt(Worldpayb2cdemoConstants.BIN_RANGE_CARD_SIZE_PROPERTY,Worldpayb2cdemoConstants.BIN_RANGE_CARD_SIZE_DEFAULT);

        final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(QUERY);
        fQuery.addQueryParameter("minBinRange", StringUtils.rightPad(cardPrefix, binRangeCardSize, "9"));
        fQuery.addQueryParameter("maxBinRange", StringUtils.rightPad(cardPrefix, binRangeCardSize, "0"));
        fQuery.setResultClassList(Collections.singletonList(WorldpayBinRangeModel.class));
        final SearchResult searchResult = flexibleSearchService.search(fQuery);
        return searchResult.getResult();
    }

    @Required
    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    @Required
    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

}
