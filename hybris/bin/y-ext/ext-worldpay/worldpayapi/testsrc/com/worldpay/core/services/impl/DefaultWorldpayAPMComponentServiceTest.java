package com.worldpay.core.services.impl;

import com.worldpay.core.dao.WorldpayAPMComponentDao;
import com.worldpay.model.WorldpayAPMComponentModel;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.service.apm.APMAvailabilityService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayAPMComponentServiceTest {

    @InjectMocks
    private DefaultWorldpayAPMComponentService testObj;

    @Mock
    private CartService cartServiceMock;
    @Mock
    private CatalogVersionService catalogVersionServiceMock;
    @Mock
    private APMAvailabilityService apmAvailabilityServiceMock;
    @Mock
    private WorldpayAPMComponentDao worldpayAPMComponentDaoMock;

    @Mock
    private CartModel cartModelMock;
    @Mock
    private CatalogVersionModel catalogVersionModelMock;
    @Mock
    private WorldpayAPMComponentModel worldpayAPMComponentModelMock;
    @Mock
    private WorldpayAPMConfigurationModel worldpayAPMConfigurationModelMock;

    @Before
    public void setUp() {
        when(catalogVersionServiceMock.getSessionCatalogVersions()).thenReturn(List.of(catalogVersionModelMock));
        when(worldpayAPMComponentDaoMock.findAllApmComponents(List.of(catalogVersionModelMock))).thenReturn(List.of(worldpayAPMComponentModelMock));
        when(worldpayAPMComponentModelMock.getApmConfiguration()).thenReturn(worldpayAPMConfigurationModelMock);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
    }

    @Test
    public void getAllAvailableWorldpayAPMComponents_WhenDaoDoesNotFindAnyComponent_ShouldReturnAnEmptyList() {
        when(worldpayAPMComponentDaoMock.findAllApmComponents(List.of(catalogVersionModelMock))).thenReturn(Collections.emptyList());

        final List<WorldpayAPMComponentModel> result = testObj.getAllAvailableWorldpayAPMComponents();

        assertThat(result).isEmpty();
    }

    @Test
    public void getAllAvailableWorldpayAPMComponents_WhenApmConfigIsNull_ShouldReturnAnEmptyList() {
        when(worldpayAPMComponentModelMock.getApmConfiguration()).thenReturn(null);

        final List<WorldpayAPMComponentModel> result = testObj.getAllAvailableWorldpayAPMComponents();

        assertThat(result).isEmpty();
    }

    @Test
    public void getAllAvailableWorldpayAPMComponents_WhenApmIsNotAvailable_ShouldReturnAnEmptyList() {
        when(apmAvailabilityServiceMock.isAvailable(worldpayAPMConfigurationModelMock, cartModelMock)).thenReturn(Boolean.FALSE);

        final List<WorldpayAPMComponentModel> result = testObj.getAllAvailableWorldpayAPMComponents();

        assertThat(result).isEmpty();
    }

    @Test
    public void getAllAvailableWorldpayAPMComponents_WhenDaoFindComponentsAndApmConfigIsNotNullAndApmIsAvailable_ShouldReturnAListOfComponents() {
        when(apmAvailabilityServiceMock.isAvailable(worldpayAPMConfigurationModelMock, cartModelMock)).thenReturn(Boolean.TRUE);

        final List<WorldpayAPMComponentModel> result = testObj.getAllAvailableWorldpayAPMComponents();

        assertThat(result).isEqualTo(List.of(worldpayAPMComponentModelMock));
    }
}
