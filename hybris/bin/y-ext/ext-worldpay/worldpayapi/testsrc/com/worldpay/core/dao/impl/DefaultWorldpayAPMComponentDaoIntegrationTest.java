package com.worldpay.core.dao.impl;

import com.worldpay.core.dao.WorldpayAPMComponentDao;
import com.worldpay.model.WorldpayAPMComponentModel;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
public class DefaultWorldpayAPMComponentDaoIntegrationTest extends ServicelayerTransactionalTest {

    private static final String CATALOG_WITH_COMPONENTS = "testCatalog1";
    private static final String CATALOG_WITHOUT_COMPONENTS = "testCatalog2";
    private static final String CATALOG_VERSION = "Online";

    @Resource(name = "worldpayAPMComponentDao")
    private WorldpayAPMComponentDao testObj;

    @Resource(name = "catalogVersionService")
    private CatalogVersionService catalogVersionService;

    @Before
    public void setUp() throws ImpExException {
        importCsv("/test/integration/worldpayAPMComponentTest.impex", "utf-8");
    }

    @Test
    public void findAllApmComponents_WhenComponentsAreInTheListOfCatalogs_ShouldReturnComponents() {
        final CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion(CATALOG_WITH_COMPONENTS, CATALOG_VERSION);

        final List<WorldpayAPMComponentModel> result = testObj.findAllApmComponents(List.of(catalogVersionModel));

        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void findAllApmComponents_WhenComponentsAreNotInTheListOfCatalogs_ShouldNotReturnComponents() {
        final CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion(CATALOG_WITHOUT_COMPONENTS, "Online");

        final List<WorldpayAPMComponentModel> result = testObj.findAllApmComponents(List.of(catalogVersionModel));

        assertThat(result).isEmpty();
    }
}
