package com.worldpay.core.dao.impl;

import com.worldpay.core.dao.WorldpayAPMComponentDao;
import com.worldpay.model.WorldpayAPMComponentModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.servicelayer.daos.impl.AbstractCMSItemDao;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayAPMComponentDao extends AbstractCMSItemDao implements WorldpayAPMComponentDao {

    private static final String QUERY = "SELECT {wc." + ItemModel.PK + "} " +
        "FROM {" + WorldpayAPMComponentModel._TYPECODE + " AS wc} " +
        "WHERE {wc." + CMSItemModel.CATALOGVERSION + "} IN (?catalogVersions)";

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WorldpayAPMComponentModel> findAllApmComponents(final Collection<CatalogVersionModel> catalogVersionModels) {
        final Map<String, Object> params = Map.of("catalogVersions", catalogVersionModels);

        final SearchResult<WorldpayAPMComponentModel> searchResult = getFlexibleSearchService().search(QUERY, params);
        return searchResult.getResult();
    }
}
