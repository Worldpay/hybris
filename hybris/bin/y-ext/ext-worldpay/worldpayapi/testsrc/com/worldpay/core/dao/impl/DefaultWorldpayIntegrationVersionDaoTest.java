package com.worldpay.core.dao.impl;

import com.worldpay.model.IntegrationVersionModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayIntegrationVersionDaoTest {

    @Spy
    @InjectMocks
    private DefaultWorldpayIntegrationVersionDao testObj;


    @Mock
    private FlexibleSearchService flexibleSearchServiceMock;
    @Mock
    private SearchResult<Object> searchResultMock;

    @Mock
    private IntegrationVersionModel integrationVersionMock, integrationVersion2Mock, integrationVersion3Mock, integrationVersion4Mock;

    @Test
    public void findByVersionNumber_shouldReturnModel_whenFound() {
        when(flexibleSearchServiceMock.searchUnique(any(FlexibleSearchQuery.class))).thenReturn(integrationVersionMock);

        final IntegrationVersionModel result = testObj.findByVersionNumber("1.0");
        assertEquals(integrationVersionMock, result);
    }

    @Test
    public void findByVersionNumber_shouldReturnNull_whenNotFound() {
        when(flexibleSearchServiceMock.searchUnique(any(FlexibleSearchQuery.class))).thenReturn(null);

        final IntegrationVersionModel result = testObj.findByVersionNumber("notfound");
        assertNull(result);
    }

    @Test
    public void findLastThreeVersions_shouldReturnEmpty_whenNoVersions() {

        when(searchResultMock.getResult()).thenReturn(Collections.emptyList());
        when(flexibleSearchServiceMock.search(any(FlexibleSearchQuery.class))).thenReturn(searchResultMock);

        final List<IntegrationVersionModel> result = testObj.findLastThreeVersions();
        assertTrue(result.isEmpty());
    }

    @Test
    public void findLastThreeVersions_shouldReturnEmpty_whenOnlyOneVersion() {

        when(searchResultMock.getResult()).thenReturn(Collections.singletonList(integrationVersionMock));
        when(flexibleSearchServiceMock.search(any(FlexibleSearchQuery.class))).thenReturn(searchResultMock);

        final List<IntegrationVersionModel> result = testObj.findLastThreeVersions();

        assertTrue(result.isEmpty());
    }

    @Test
    public void findLastThreeVersions_shouldReturnThree_whenFourVersions() {

        when(searchResultMock.getResult()).thenReturn(List.of(integrationVersionMock, integrationVersion2Mock, integrationVersion3Mock, integrationVersion4Mock));
        when(flexibleSearchServiceMock.search(any(FlexibleSearchQuery.class))).thenReturn(searchResultMock);

        final List<IntegrationVersionModel> result = testObj.findLastThreeVersions();

        assertEquals(3, result.size());
        assertEquals(Arrays.asList(integrationVersion2Mock, integrationVersion3Mock, integrationVersion4Mock), result);
    }

    @Test
    public void findLastThreeVersions_shouldReturnLessThanThree_whenTwoVersions() {

        when(searchResultMock.getResult()).thenReturn(List.of(integrationVersionMock, integrationVersion2Mock));
        when(flexibleSearchServiceMock.search(any(FlexibleSearchQuery.class))).thenReturn(searchResultMock);

        final List<IntegrationVersionModel> result = testObj.findLastThreeVersions();

        assertEquals(1, result.size());
        assertEquals(Collections.singletonList(integrationVersion2Mock), result);
    }
}