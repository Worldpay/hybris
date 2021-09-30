package com.worldpay.core.dao.impl;

import com.worldpay.model.WorldpayAPMComponentModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayAPMComponentDaoTest {

    private static final String QUERY = "SELECT {wc." + ItemModel.PK + "} " +
        "FROM {" + WorldpayAPMComponentModel._TYPECODE + " AS wc} " +
        "WHERE {wc." + CMSItemModel.CATALOGVERSION + "} IN (?catalogVersions)";
    private static final String CATALOG_VERSIONS_KEY = "catalogVersions";

    @InjectMocks
    private DefaultWorldpayAPMComponentDao testObj;

    @Mock
    private FlexibleSearchService flexibleSearchServiceMock;
    @Mock
    private SearchResult<Object> searchResultMock;

    @Mock
    private WorldpayAPMComponentModel apmComponentModel1Mock, apmComponentModel2Mock;
    @Mock
    private CatalogVersionModel catalogVersionModel1Mock, catalogVersionModel2Mock;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;
    @Captor
    private ArgumentCaptor<Map<String, Object>> mapArgumentCaptor;

    @Before
    public void setUp() {
        when(flexibleSearchServiceMock.search(stringArgumentCaptor.capture(), mapArgumentCaptor.capture())).thenReturn(searchResultMock);
    }

    @Test
    public void findAllAvailableApmComponents_WhenThereAreComponentsForTheGivenCatalogs_ShouldReturnAListOfComponents() {
        when(searchResultMock.getResult()).thenReturn(List.of(apmComponentModel1Mock, apmComponentModel2Mock));

        final List<WorldpayAPMComponentModel> result = testObj.findAllApmComponents(List.of(catalogVersionModel1Mock, catalogVersionModel2Mock));

        assertThat(result).isEqualTo(List.of(apmComponentModel1Mock, apmComponentModel2Mock));
        assertThat(stringArgumentCaptor.getValue()).isEqualTo(QUERY);
        assertThat(mapArgumentCaptor.getValue()).containsEntry(CATALOG_VERSIONS_KEY, List.of(catalogVersionModel1Mock, catalogVersionModel2Mock));
    }

    @Test
    public void findAllAvailableApmComponents_WhenThereAreNotComponentsForTheGivenCatalogs_ShouldReturnAnEmptyList() {
        when(searchResultMock.getResult()).thenReturn(Collections.emptyList());

        final List<WorldpayAPMComponentModel> result = testObj.findAllApmComponents(List.of(catalogVersionModel1Mock, catalogVersionModel2Mock));

        assertThat(result).isEmpty();
        assertThat(stringArgumentCaptor.getValue()).isEqualTo(QUERY);
        assertThat(mapArgumentCaptor.getValue()).containsEntry(CATALOG_VERSIONS_KEY, List.of(catalogVersionModel1Mock, catalogVersionModel2Mock));
    }
}
