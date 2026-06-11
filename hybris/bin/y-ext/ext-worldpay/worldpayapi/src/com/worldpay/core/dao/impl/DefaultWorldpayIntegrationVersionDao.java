package com.worldpay.core.dao.impl;

import java.util.Collections;
import java.util.List;

import com.worldpay.core.dao.WorldpayIntegrationVersionDao;
import com.worldpay.model.IntegrationVersionModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayIntegrationVersionDao extends AbstractItemDao implements WorldpayIntegrationVersionDao {

    protected static final String QUERY_BY_VERSION = "SELECT {" + IntegrationVersionModel.PK + "} " +
            "FROM {" + IntegrationVersionModel._TYPECODE + "}" +
            " WHERE {" + IntegrationVersionModel.VERSIONNUMBER + "} = ?versionNumber";
    protected static final String QUERY_ALL_VERSIONS_ORDER_BY_DATE = "SELECT {" + IntegrationVersionModel.PK + "} FROM " +
            "{" + IntegrationVersionModel._TYPECODE + "} " +
            "ORDER BY {" + IntegrationVersionModel.DATE + "} DESC";
    protected static final String PARAM_VERSION_NUMBER = "versionNumber";

    /**
     * {@inheritDoc}
     */
    @Override
    public IntegrationVersionModel findByVersionNumber(final String versionNumber) {
        final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(QUERY_BY_VERSION);
        fQuery.addQueryParameters(Collections.singletonMap(PARAM_VERSION_NUMBER, versionNumber));
        fQuery.setResultClassList(Collections.singletonList(IntegrationVersionModel.class));
        return searchUnique(fQuery);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IntegrationVersionModel> findLastThreeVersions() {
        final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(QUERY_ALL_VERSIONS_ORDER_BY_DATE);

        final SearchResult<IntegrationVersionModel> searchResult = getFlexibleSearchService().search(fQuery);

        return searchResult.getResult()
                .stream()
                .skip(1)
                .limit(3)
                .toList();
    }

}
