package com.worldpay.worldpayaddonbackoffice.renderers;

import java.util.List;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListColumn;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.widgets.common.WidgetComponentRenderer;
import com.worldpay.model.WorldpayAavResponseModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.omsbackoffice.renderers.NestedAttributeUtils;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zkoss.zul.Listcell;

@UnitTest
@ExtendWith(MockitoExtension.class)
public class WorldpayNullSafeCellRendererTest {

    private static final String QUALIFIER = "aavResponse.aavAddressResultCode";

    @InjectMocks
    private WorldpayNullSafeCellRenderer testObj;
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
    @Mock
    private NestedAttributeUtils nestedAttributeUtilsMock;

    @BeforeEach
    public void setUp() {
        when(columnConfigurationMock.getQualifier()).thenReturn(QUALIFIER);
    }

    @Test
    public void render_ShouldNotRender_WhenAavResponseIsNull() {
        final PaymentTransactionEntryModel paymentTransactionEntryModel = new PaymentTransactionEntryModel();
        when(nestedAttributeUtilsMock.splitQualifier(anyString())).thenReturn(List.of("String"));

        testObj.render(listCellMock, columnConfigurationMock, paymentTransactionEntryModel, dataTypeMock, widgetInstanceManagerMock);

        verifyNoInteractions(defaultListCellRenderer);
    }

    @Test
    public void render_ShouldNotRender_WhenAavAddressResultCodeIsNull() {
        when(nestedAttributeUtilsMock.splitQualifier(anyString())).thenReturn(List.of("String"));

        final PaymentTransactionEntryModel paymentTransactionEntryModel = new PaymentTransactionEntryModel();
        final WorldpayAavResponseModel worldpayAavResponseModel = new WorldpayAavResponseModel();
        paymentTransactionEntryModel.setAavResponse(worldpayAavResponseModel);

        testObj.render(listCellMock, columnConfigurationMock, paymentTransactionEntryModel, dataTypeMock, widgetInstanceManagerMock);

        verifyNoInteractions(defaultListCellRenderer);
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
