package com.worldpay.core.dao.impl;

import com.worldpay.core.dao.WorldpayAPMComponentDao;
import com.worldpay.model.WorldpayAPMComponentModel;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.servicelayer.daos.impl.AbstractCMSItemDao;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.*;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayAPMComponentDao extends AbstractCMSItemDao implements WorldpayAPMComponentDao {

    private static final String QUERY_ALL = "SELECT {wc." + ItemModel.PK + "} " +
        "FROM {" + WorldpayAPMComponentModel._TYPECODE + " AS wc} " +
        "WHERE {wc." + CMSItemModel.CATALOGVERSION + "} IN (?catalogVersions)";

    private static final String QUERY_BY_CODE = "SELECT {wc." + ItemModel.PK + "} " +
            "FROM {" + WorldpayAPMComponentModel._TYPECODE + " AS wc " +
            "JOIN " + WorldpayAPMConfigurationModel._TYPECODE + " AS apm " +
            "ON {wc." + WorldpayAPMComponentModel.APMCONFIGURATION + "} = {apm." + WorldpayAPMConfigurationModel.PK + "}} " +
            "WHERE {wc." + CMSItemModel.CATALOGVERSION + "} IN (?catalogVersions) " +
            "AND {apm." + WorldpayAPMConfigurationModel.CODE + "} = ?" + WorldpayAPMConfigurationModel.CODE;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WorldpayAPMComponentModel> findAllApmComponents(final Collection<CatalogVersionModel> catalogVersionModels) {
        final Map<String, Object> params = Map.of("catalogVersions", catalogVersionModels);

        final SearchResult<WorldpayAPMComponentModel> searchResult = getFlexibleSearchService().search(QUERY_ALL, params);
        return searchResult.getResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorldpayAPMComponentModel findApmComponentByCode(final Collection<CatalogVersionModel> catalogVersionModels, final String apmCode) throws ModelNotFoundException, AmbiguousIdentifierException {
        final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(QUERY_BY_CODE);
        final Map<String, Object> params = new HashMap<>();
        params.put("catalogVersions", catalogVersionModels);
        params.put(WorldpayAPMConfigurationModel.CODE, apmCode);
        fQuery.addQueryParameters(params);
        fQuery.addQueryParameters(Collections.singletonMap(WorldpayAPMComponentModel.TYPECODE, apmCode));
        fQuery.setResultClassList(Collections.singletonList(CartModel.class));
        return searchUnique(fQuery);
    }
}
