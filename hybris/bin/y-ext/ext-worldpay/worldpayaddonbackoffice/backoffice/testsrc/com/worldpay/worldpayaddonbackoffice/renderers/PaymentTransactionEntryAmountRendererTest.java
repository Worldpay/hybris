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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zul.Listcell;

import java.math.BigDecimal;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PaymentTransactionEntryAmountRendererTest {

    private static final String QUALIFIER = "qualifier";
    private static final String DATA_TYPE_CODE = "dataTypeCode";
    private static final String AMOUNT = "amount";
    private static final String PAYMENT_TRANSACTION_ENTRY = "PaymentTransactionEntry";

    @InjectMocks
    private PaymentTransactionEntryAmountRenderer testObj;

    @Mock
    private WidgetInstanceManager widgetInstanceManagerMock;
    @Mock
    private Listcell listCellMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private PaymentTransactionEntryModel transactionEntryMock;
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
    private CurrencyModel currencyModelMock;
    @Mock
    private LabelService labelServiceMock;

    @Before
    public void setUp() throws Exception {
        when(columnConfigurationMock.getQualifier()).thenReturn(QUALIFIER);
        when(dataTypeMock.getCode()).thenReturn(DATA_TYPE_CODE);
        when(typeFacadeMock.load(PAYMENT_TRANSACTION_ENTRY)).thenReturn(dataTypeMock);
        when(permissionFacadeMock.canReadProperty(DATA_TYPE_CODE, QUALIFIER)).thenReturn(true);
        when(propertyValueServiceMock.readValue(transactionEntryMock, QUALIFIER)).thenReturn(BigDecimal.TEN);
        when(transactionEntryMock.getCurrency()).thenReturn(currencyModelMock);
        when(currencyModelMock.getDigits()).thenReturn(2);

        when(labelServiceMock.getObjectLabel(any())).thenReturn(AMOUNT);
    }

    @Test
    public void shouldSetLabelWithAmountValue() {
        testObj.render(listCellMock, columnConfigurationMock, transactionEntryMock, dataTypeMock, widgetInstanceManagerMock);

        verify(listCellMock).setLabel(AMOUNT);
    }

    @Test
    public void shouldNotSetLabelWhenExceptionThrown() throws Exception {
        doThrow(new TypeNotFoundException("Something went wrong")).when(typeFacadeMock).load(PAYMENT_TRANSACTION_ENTRY);

        testObj.render(listCellMock, columnConfigurationMock, transactionEntryMock, dataTypeMock, widgetInstanceManagerMock);

        verify(listCellMock, never()).setLabel(AMOUNT);
    }

    @Test
    public void shouldNotSetLabelWhenPropertyIsNotReadable() {
        when(permissionFacadeMock.canReadProperty(DATA_TYPE_CODE, QUALIFIER)).thenReturn(false);

        testObj.render(listCellMock, columnConfigurationMock, transactionEntryMock, dataTypeMock, widgetInstanceManagerMock);

        verify(listCellMock, never()).setLabel(AMOUNT);
    }

    @Test
    public void shouldSetLabelToAuthorisedAmountIfAmountIsBlank() {
        when(labelServiceMock.getObjectLabel(any())).thenReturn("");

        testObj.render(listCellMock, columnConfigurationMock, transactionEntryMock, dataTypeMock, widgetInstanceManagerMock);

        verify(listCellMock).setLabel(BigDecimal.TEN.toString());
    }

    @Test
    public void shouldSetLabelToEmptyStringIfAuthorisedAmountIsNull() {
        when(propertyValueServiceMock.readValue(transactionEntryMock, QUALIFIER)).thenReturn(null);

        testObj.render(listCellMock, columnConfigurationMock, transactionEntryMock, dataTypeMock, widgetInstanceManagerMock);

        verify(listCellMock).setLabel("");
    }
}
