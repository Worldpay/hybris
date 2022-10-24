package com.worldpay.worldpayaddonbackoffice.renderers;

import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListColumn;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;
import com.hybris.cockpitng.dataaccess.services.PropertyValueService;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.labels.LabelService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zul.Listcell;

import java.math.BigDecimal;

import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PaymentTransactionAmountRendererTest {

    private static final String AMOUNT = "amount";
    private static final String QUALIFIER = "qualifier";
    private static final String DATA_TYPE_CODE = "dataTypeCode";
    private static final String PAYMENT_TRANSACTION = "PaymentTransaction";

    @InjectMocks
    private PaymentTransactionAmountRenderer testObj;

    @Mock
    private WidgetInstanceManager widgetInstanceManagerMock;
    @Mock
    private Listcell listCellMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private PaymentTransactionModel objectMock;
    @Mock
    private ListColumn columnConfigurationMock;
    @Mock
    private DataType dataTypeMock;
    @Mock
    private TypeFacade typeFacadeMock;
    @Mock
    private PermissionFacade permissionFacadeMock;
    @Mock
    private PropertyValueService propertyValueServiceMock;
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryModelMock;
    @Mock
    private CurrencyModel currencyModelMock;
    @Mock
    private LabelService labelServiceMock;

    @Before
    public void setUp() throws Exception {
        when(columnConfigurationMock.getQualifier()).thenReturn(QUALIFIER);
        when(dataTypeMock.getCode()).thenReturn(DATA_TYPE_CODE);
        when(typeFacadeMock.load(PAYMENT_TRANSACTION)).thenReturn(dataTypeMock);
        when(permissionFacadeMock.canReadProperty(DATA_TYPE_CODE, QUALIFIER)).thenReturn(true);
        when(propertyValueServiceMock.readValue(objectMock, QUALIFIER)).thenReturn(BigDecimal.TEN);
        when(objectMock.getEntries()).thenReturn(singletonList(paymentTransactionEntryModelMock));
        when(paymentTransactionEntryModelMock.getCurrency()).thenReturn(currencyModelMock);
        when(currencyModelMock.getDigits()).thenReturn(2);
        when(labelServiceMock.getObjectLabel(any())).thenReturn(AMOUNT);
    }

    @Test
    public void shouldSetLabel() {
        testObj.render(listCellMock, columnConfigurationMock, objectMock, dataTypeMock, widgetInstanceManagerMock);

        verify(listCellMock).setLabel(AMOUNT);
    }

    @Test
    public void shouldNotSetLabelWhenExceptionThrown() throws Exception {
        doThrow(new TypeNotFoundException("Something went wrong")).when(typeFacadeMock).load(PAYMENT_TRANSACTION);

        testObj.render(listCellMock, columnConfigurationMock, objectMock, dataTypeMock, widgetInstanceManagerMock);

        verify(listCellMock, never()).setLabel(AMOUNT);
    }

    @Test
    public void shouldNotSetLabelWhenPropertyIsNotReadable() {
        when(permissionFacadeMock.canReadProperty(DATA_TYPE_CODE, QUALIFIER)).thenReturn(false);

        testObj.render(listCellMock, columnConfigurationMock, objectMock, dataTypeMock, widgetInstanceManagerMock);

        verify(listCellMock, never()).setLabel(AMOUNT);
    }

    @Test
    public void shouldSetLabelToAuthorisedAmountIfAmountIsBlank() {
        when(labelServiceMock.getObjectLabel(any())).thenReturn("");

        testObj.render(listCellMock, columnConfigurationMock, objectMock, dataTypeMock, widgetInstanceManagerMock);

        verify(listCellMock).setLabel(BigDecimal.TEN.toString());
    }

    @Test
    public void shouldSetLabelToEmptyStringIfAuthorisedAmountIsNull() {
        when(propertyValueServiceMock.readValue(objectMock, QUALIFIER)).thenReturn(null);

        testObj.render(listCellMock, columnConfigurationMock, objectMock, dataTypeMock, widgetInstanceManagerMock);

        verify(listCellMock).setLabel("");
    }
}
