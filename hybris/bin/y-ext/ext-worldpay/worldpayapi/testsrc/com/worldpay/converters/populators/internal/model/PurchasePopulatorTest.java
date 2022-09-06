package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.Amount;
import com.worldpay.data.Date;
import com.worldpay.data.Item;
import com.worldpay.data.Purchase;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PurchasePopulatorTest {

    private static final String INVOICE_REFERENCE_NUMBER = "invoiceReferenceNumber";
    private static final String CUSTOMER_REFERENCE = "customerReference";
    private static final String CARD_ACCEPTOR_TAX_ID = "cardAcceptorTaxId";
    private static final String COUNTRY_CODE = "countryCode";
    private static final String POSTAL_CODE = "postalCode";

    @InjectMocks
    private PurchasePopulator testObj;

    @Mock
    private Converter<Item, com.worldpay.internal.model.Item> internalItemConverterMock;
    @Mock
    private Converter<Date, com.worldpay.internal.model.Date> internalDateConverterMock;
    @Mock
    private Converter<Amount, com.worldpay.internal.model.Amount> internalAmountConverterMock;

    @Mock
    private Purchase sourceMock;
    @Mock
    private Item itemMock;
    @Mock
    private com.worldpay.internal.model.Item intItemMock;
    @Mock
    private Date dateMock;
    @Mock
    private com.worldpay.internal.model.Date intDateMock;
    @Mock
    private Amount salesTaxMock, discountAmountMock, shippingAmountMock, dutyAmountMock;
    @Mock
    private com.worldpay.internal.model.Amount intSalesTaxMock, intDiscountAmountMock, intShippingAmountMock, intDutyAmountMock;

    @Before
    public void setUp() {
        testObj = new PurchasePopulator(internalItemConverterMock, internalDateConverterMock, internalAmountConverterMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.Purchase());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_WhenOrderDateIsNull_ShouldNotPopulateOrderDate() {
        when(sourceMock.getOrderDate()).thenReturn(null);

        final com.worldpay.internal.model.Purchase target = new com.worldpay.internal.model.Purchase();
        testObj.populate(sourceMock, target);

        assertThat(target.getOrderDate()).isNull();
    }

    @Test
    public void populate_WhenSalesTaxIsNull_ShouldNotPopulateSalesTax() {
        when(sourceMock.getSalesTax()).thenReturn(null);

        final com.worldpay.internal.model.Purchase target = new com.worldpay.internal.model.Purchase();
        testObj.populate(sourceMock, target);

        assertThat(target.getSalesTax()).isNull();
    }

    @Test
    public void populate_WhenDiscountAmountIsNull_ShouldNotPopulateDiscountAmount() {
        when(sourceMock.getDiscountAmount()).thenReturn(null);

        final com.worldpay.internal.model.Purchase target = new com.worldpay.internal.model.Purchase();
        testObj.populate(sourceMock, target);

        assertThat(target.getDiscountAmount()).isNull();
    }

    @Test
    public void populate_WhenShippingAmountIsNull_ShouldNotPopulateShippingAmount() {
        when(sourceMock.getShippingAmount()).thenReturn(null);

        final com.worldpay.internal.model.Purchase target = new com.worldpay.internal.model.Purchase();
        testObj.populate(sourceMock, target);

        assertThat(target.getShippingAmount()).isNull();
    }

    @Test
    public void populate_WhenDutyAmountIsNull_ShouldNotPopulateDutyAmount() {
        when(sourceMock.getDutyAmount()).thenReturn(null);

        final com.worldpay.internal.model.Purchase target = new com.worldpay.internal.model.Purchase();
        testObj.populate(sourceMock, target);

        assertThat(target.getDutyAmount()).isNull();
    }

    @Test
    public void populate_WhenGetItemIsNull_ShouldNotPopulateItem() {
        when(sourceMock.getItem()).thenReturn(null);

        final com.worldpay.internal.model.Purchase target = new com.worldpay.internal.model.Purchase();
        testObj.populate(sourceMock, target);

        assertThat(target.getItem()).isEmpty();
    }

    @Test
    public void populate_WhenSourceAndTargetAreNotNullAndAllTheFieldsAreNotNull_ShouldPopulate() {
        when(internalItemConverterMock.convertAll(List.of(itemMock))).thenReturn(List.of(intItemMock));
        when(internalDateConverterMock.convert(dateMock)).thenReturn(intDateMock);
        when(internalAmountConverterMock.convert(salesTaxMock)).thenReturn(intSalesTaxMock);
        when(internalAmountConverterMock.convert(discountAmountMock)).thenReturn(intDiscountAmountMock);
        when(internalAmountConverterMock.convert(shippingAmountMock)).thenReturn(intShippingAmountMock);
        when(internalAmountConverterMock.convert(dutyAmountMock)).thenReturn(intDutyAmountMock);
        when(sourceMock.getInvoiceReferenceNumber()).thenReturn(INVOICE_REFERENCE_NUMBER);
        when(sourceMock.getCustomerReference()).thenReturn(CUSTOMER_REFERENCE);
        when(sourceMock.getCardAcceptorTaxId()).thenReturn(CARD_ACCEPTOR_TAX_ID);
        when(sourceMock.getDestinationCountryCode()).thenReturn(COUNTRY_CODE);
        when(sourceMock.getDestinationPostalCode()).thenReturn(POSTAL_CODE);
        when(sourceMock.isTaxExempt()).thenReturn(Boolean.TRUE);
        when(sourceMock.getItem()).thenReturn(List.of(itemMock));
        when(sourceMock.getOrderDate()).thenReturn(dateMock);
        when(sourceMock.getSalesTax()).thenReturn(salesTaxMock);
        when(sourceMock.getDiscountAmount()).thenReturn(discountAmountMock);
        when(sourceMock.getShippingAmount()).thenReturn(shippingAmountMock);
        when(sourceMock.getDutyAmount()).thenReturn(dutyAmountMock);

        final com.worldpay.internal.model.Purchase target = new com.worldpay.internal.model.Purchase();
        testObj.populate(sourceMock, target);

        assertThat(target.getInvoiceReferenceNumber()).isEqualTo(INVOICE_REFERENCE_NUMBER);
        assertThat(target.getCustomerReference()).isEqualTo(CUSTOMER_REFERENCE);
        assertThat(target.getCardAcceptorTaxId()).isEqualTo(CARD_ACCEPTOR_TAX_ID);
        assertThat(target.getDestinationCountryCode()).isEqualTo(COUNTRY_CODE);
        assertThat(target.getDestinationPostalCode()).isEqualTo(POSTAL_CODE);
        assertThat(target.getTaxExempt()).isEqualTo(Boolean.TRUE.toString());
        assertThat(target.getItem()).isEqualTo(List.of(intItemMock));
        assertThat(target.getOrderDate().getDate()).isEqualTo(intDateMock);
        assertThat(target.getSalesTax().getAmount()).isEqualTo(intSalesTaxMock);
        assertThat(target.getDiscountAmount().getAmount()).isEqualTo(intDiscountAmountMock);
        assertThat(target.getShippingAmount().getAmount()).isEqualTo(intShippingAmountMock);
        assertThat(target.getDutyAmount().getAmount()).isEqualTo(intDutyAmountMock);
    }
}
