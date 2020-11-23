package com.worldpay.worldpayaddonbackoffice.renderers;

import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListColumn;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.widgets.common.WidgetComponentRenderer;
import com.worldpay.model.WorldpayAavResponseModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.omsbackoffice.renderers.NestedAttributeUtils;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zul.Listcell;

import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayNullSafeCellRendererTest {

    private static final String QUALIFIER = "aavResponse.aavAddressResultCode";

    @InjectMocks
    private final WorldpayNullSafeCellRenderer testObj = new WorldpayNullSafeCellRenderer();
    @Mock
    private WidgetComponentRenderer<Listcell, ListColumn, Object> defaultListCellRenderer;
    @Mock
    private WidgetInstanceManager widgetInstanceManagerMock;
    @Mock
    private Listcell listCellMock;
    @Mock
    private ListColumn columnConfigurationMock;
    @Mock
    private DataType dataTypeMock;

    @Before
    public void setUp() {
        when(columnConfigurationMock.getQualifier()).thenReturn(QUALIFIER);

        testObj.setNestedAttributeUtils(new NestedAttributeUtils());
    }

    @Test
    public void render_ShouldNotRender_WhenAavResponseIsNull() {
        final PaymentTransactionEntryModel paymentTransactionEntryModel = new PaymentTransactionEntryModel();

        testObj.render(listCellMock, columnConfigurationMock, paymentTransactionEntryModel, dataTypeMock, widgetInstanceManagerMock);

        verifyZeroInteractions(defaultListCellRenderer);
    }

    @Test
    public void render_ShouldNotRender_WhenAavAddressResultCodeIsNull() {
        final PaymentTransactionEntryModel paymentTransactionEntryModel = new PaymentTransactionEntryModel();
        final WorldpayAavResponseModel worldpayAavResponseModel = new WorldpayAavResponseModel();
        paymentTransactionEntryModel.setAavResponse(worldpayAavResponseModel);

        testObj.render(listCellMock, columnConfigurationMock, paymentTransactionEntryModel, dataTypeMock, widgetInstanceManagerMock);

        verifyZeroInteractions(defaultListCellRenderer);
    }

    @Test
    public void render_shouldRenderWhenAavAddressResultCodeIsNotNull() {
        final PaymentTransactionEntryModel paymentTransactionEntryModel = new PaymentTransactionEntryModel();
        final WorldpayAavResponseModel worldpayAavResponseModel = new WorldpayAavResponseModel();
        worldpayAavResponseModel.setAavAddressResultCode("B");
        paymentTransactionEntryModel.setAavResponse(worldpayAavResponseModel);

        testObj.render(listCellMock, columnConfigurationMock, paymentTransactionEntryModel, dataTypeMock, widgetInstanceManagerMock);

        verify(defaultListCellRenderer).render(listCellMock, columnConfigurationMock, paymentTransactionEntryModel, dataTypeMock, widgetInstanceManagerMock);
    }
}
