package com.worldpay.worldpayaddonbackoffice.renderers;

import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListColumn;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;
import com.hybris.cockpitng.dataaccess.services.PropertyValueService;
import com.hybris.cockpitng.labels.LabelService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.zkoss.zul.Listcell;

import java.math.BigDecimal;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AbstractAmountRendererTest {

    private static final String AMOUNT = "amount";
    private static final String QUALIFIER = "qualifier";
    private static final String DATA_TYPE_CODE = "dataTypeCode";
    private static final String PAYMENT_TRANSACTION = "PaymentTransaction";

    private AbstractAmountRenderer testObj;

    @Mock
    private TypeFacade typeFacadeMock;
    @Mock
    private PermissionFacade permissionFacadeMock;
    @Mock
    private PropertyValueService propertyValueServiceMock;
    @Mock
    private LabelService labelServiceMock;

    @Mock
    private Listcell listCellMock;
    @Mock
    private ListColumn columnConfigurationMock;
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryModelMock;
    @Mock
    private DataType dataTypeMock;
    @Mock
    private CurrencyModel currencyModelMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModel;

    @Before
    public void setUp() throws TypeNotFoundException {
        testObj = Mockito.mock(
            AbstractAmountRenderer.class,
            Mockito.CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(testObj, "typeFacade", typeFacadeMock);
        ReflectionTestUtils.setField(testObj, "permissionFacade", permissionFacadeMock);
        ReflectionTestUtils.setField(testObj, "propertyValueService", propertyValueServiceMock);
        ReflectionTestUtils.setField(testObj, "labelService", labelServiceMock);

        when(typeFacadeMock.load(PaymentTransactionModel._TYPECODE)).thenReturn(dataTypeMock);
        when(propertyValueServiceMock.readValue(paymentTransactionEntryModelMock, QUALIFIER)).thenReturn(BigDecimal.TEN);
        when(propertyValueServiceMock.readValue(paymentTransactionModel, QUALIFIER)).thenReturn(BigDecimal.TEN);
        when(permissionFacadeMock.canReadProperty(DATA_TYPE_CODE, QUALIFIER)).thenReturn(true);
        when(labelServiceMock.getObjectLabel(any())).thenReturn(AMOUNT);
        when(columnConfigurationMock.getQualifier()).thenReturn(QUALIFIER);
        when(dataTypeMock.getCode()).thenReturn(DATA_TYPE_CODE);
        when(permissionFacadeMock.canReadProperty(DATA_TYPE_CODE, QUALIFIER)).thenReturn(true);
        when(paymentTransactionEntryModelMock.getCurrency()).thenReturn(currencyModelMock);
        when(currencyModelMock.getDigits()).thenReturn(2);
        when(paymentTransactionModel.getEntries()).thenReturn(singletonList(paymentTransactionEntryModelMock));
    }

    @Test
    public void renderAmount_WhenObjectIsTransactionEntry_ShouldSetLabel() {
        testObj.renderAmount(listCellMock, columnConfigurationMock, paymentTransactionEntryModelMock, PaymentTransactionModel._TYPECODE);

        verify(listCellMock).setLabel(AMOUNT);
    }

    @Test
    public void renderAmount_WhenObjectIsTransaction_ShouldSetLabel() {
        testObj.renderAmount(listCellMock, columnConfigurationMock, paymentTransactionModel, PaymentTransactionModel._TYPECODE);

        verify(listCellMock).setLabel(AMOUNT);
    }

    @Test
    public void renderAmount_WhenExceptionThrown_ShouldNotSetLabel() throws Exception {
        doThrow(new TypeNotFoundException("Something went wrong")).when(typeFacadeMock).load(PAYMENT_TRANSACTION);

        testObj.renderAmount(listCellMock, columnConfigurationMock, paymentTransactionEntryModelMock, PaymentTransactionModel._TYPECODE);

        verify(listCellMock, never()).setLabel(AMOUNT);
    }

    @Test
    public void renderAmount_WhenPropertyIsNotReadable_ShouldNotSetLabel() {
        when(permissionFacadeMock.canReadProperty(DATA_TYPE_CODE, QUALIFIER)).thenReturn(false);

        testObj.renderAmount(listCellMock, columnConfigurationMock, paymentTransactionEntryModelMock, PaymentTransactionModel._TYPECODE);

        verify(listCellMock, never()).setLabel(AMOUNT);
    }

    @Test
    public void renderAmount_WhenAmountIsBlank_ShouldSetLabelToAuthorisedAmount() {
        when(labelServiceMock.getObjectLabel(any())).thenReturn("");

        testObj.renderAmount(listCellMock, columnConfigurationMock, paymentTransactionEntryModelMock, PaymentTransactionModel._TYPECODE);

        verify(listCellMock).setLabel(BigDecimal.TEN.toString());
    }

    @Test
    public void renderAmount_WhenAuthorisedAmountIsNull_shouldSetLabelToEmptyString() {
        when(propertyValueServiceMock.readValue(paymentTransactionEntryModelMock, QUALIFIER)).thenReturn(null);

        testObj.renderAmount(listCellMock, columnConfigurationMock, paymentTransactionEntryModelMock, PaymentTransactionModel._TYPECODE);

        verify(listCellMock).setLabel("");
    }

}
