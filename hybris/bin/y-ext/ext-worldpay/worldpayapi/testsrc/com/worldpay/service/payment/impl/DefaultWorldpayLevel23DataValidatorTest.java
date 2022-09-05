package com.worldpay.service.payment.impl;

import com.google.common.collect.ImmutableList;
import com.worldpay.data.Amount;
import com.worldpay.data.Date;
import com.worldpay.data.Item;
import com.worldpay.data.Purchase;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayLevel23DataValidatorTest {

    private static final String INVOICE_REFERENCE_NUMBER = "invoiceReference";
    private static final String CUSTOMER_REFERENCE = "customerReference";
    private static final String CARD_ACCEPTOR_TAX_ID = "123456789";
    private static final String US_COUNTRY_CODE = "US";
    private static final String DESTINATION_POSTAL_CODE = "120125";
    private static final String PRODUCT_DESCRIPRION = "product descriprion";
    private static final String PRODUCT_CODE = "productCode";
    private static final String COMMODITY_CODE = "1234567";
    private static final String UNIT_OF_MEASURE = "pieces";

    @InjectMocks
    private DefaultWorldpayLevel23DataValidator testObj;

    @Mock
    private Date orderDateMock;
    @Mock
    private Amount hundredAmountMock, ninetyAmountMock, tenAmountMock, dutyAmountMock, totalAmountMock, taxAmountMock;

    private Purchase purchase;
    private Item item;

    @Before
    public void setUp() {
        item = createItem();
        purchase = createPurchase();
    }

    @Test
    public void isValidLevel3Data_WhenAllFieldsAreValid_ShouldReturnTrue() {
        boolean result = testObj.isValidLevel3Data(ImmutableList.of(purchase));

        assertThat(result).isTrue();
    }

    @Test
    public void isValidLevel3Data_WhenOneOfTheFieldsIsInvalid_ShouldReturnFalse() {
        purchase.setCustomerReference("longCustomerReference");

        boolean result = testObj.isValidLevel3Data(ImmutableList.of(purchase));

        assertThat(result).isFalse();
    }

    @Test
    public void isValidLevel3Data_WhenItemIsNull_ShouldReturnFalse() {
        purchase.setItem(null);

        boolean result = testObj.isValidLevel3Data(ImmutableList.of(purchase));

        assertThat(result).isFalse();
    }

    @Test
    public void isValidLevel3Data_WhenItemIsInvalid_ShouldReturnFalse() {
        item.setProductCode("longProductCode");

        boolean result = testObj.isValidLevel3Data(ImmutableList.of(purchase));

        assertThat(result).isFalse();
    }

    @Test
    public void isValidLevel2Data_WhenItemIsNull_ShouldReturnTrue() {
        purchase.setItem(null);

        boolean result = testObj.isValidLevel2Data(ImmutableList.of(purchase));

        assertThat(result).isTrue();
    }

    @Test
    public void isValidLevel2Data_WhenItemIsEmptyList_ShouldReturnTrue() {
        purchase.setItem(Collections.emptyList());

        boolean result = testObj.isValidLevel2Data(ImmutableList.of(purchase));

        assertThat(result).isTrue();
    }

    @Test
    public void isValidLevel2Data_WhenProductCodeInvalid_ShouldSetValueToNullAndReturnTrue() {
        item.setProductCode("longProductCode");

        boolean result = testObj.isValidLevel2Data(ImmutableList.of(purchase));

        assertThat(result).isTrue();
        assertThat(item.getProductCode()).isNull();
    }

    @Test
    public void isValidLevel2Data_WhenCommodityCodeInvalid_ShouldSetValueToNullAndReturnTrue() {
        item.setCommodityCode("longCommodityCode");

        boolean result = testObj.isValidLevel2Data(ImmutableList.of(purchase));

        assertThat(result).isTrue();
        assertThat(item.getCommodityCode()).isNull();
    }

    @Test
    public void isValidLevel2Data_WhenUnitOfMeasureInvalid_ShouldSetValueToNullAndReturnTrue() {
        item.setUnitOfMeasure("longUnitOfMeasure");

        boolean result = testObj.isValidLevel2Data(ImmutableList.of(purchase));

        assertThat(result).isTrue();
        assertThat(item.getUnitOfMeasure()).isNull();
    }

    @Test
    public void isValidLevel2Data_WhenDescriptionInvalid_ShouldReturnFalse() {
        item.setDescription("reallyLongProductDescription");

        boolean result = testObj.isValidLevel2Data(ImmutableList.of(purchase));

        assertThat(result).isFalse();
    }

    @Test
    public void isValidLevel2Data_WhenMandatoryFieldInvalid_ShouldReturnFalse() {
        purchase.setCustomerReference("longCustomerReference");

        boolean result = testObj.isValidLevel2Data(ImmutableList.of(purchase));

        assertThat(result).isFalse();
    }

    @Test
    public void isValidLevel2Data_WhenDestinationPostcodeInvalid_ShouldSetValueToNullAndReturnTrue() {
        purchase.setDestinationPostalCode("longPostalCode");

        boolean result = testObj.isValidLevel2Data(ImmutableList.of(purchase));

        assertThat(result).isTrue();
        assertThat(purchase.getDestinationPostalCode()).isNull();
    }

    private Item createItem() {
        final Item item = new Item();
        item.setDescription(PRODUCT_DESCRIPRION);
        item.setProductCode(PRODUCT_CODE);
        item.setQuantity("1");
        item.setCommodityCode(COMMODITY_CODE);
        item.setUnitOfMeasure(UNIT_OF_MEASURE);
        item.setItemTotal(hundredAmountMock);
        item.setItemTotalWithTax(totalAmountMock);
        item.setItemDiscountAmount(tenAmountMock);
        item.setTaxAmount(taxAmountMock);

        return item;
    }

    private Purchase createPurchase() {
        final Purchase purchase = new Purchase();
        purchase.setInvoiceReferenceNumber(INVOICE_REFERENCE_NUMBER);
        purchase.setCustomerReference(CUSTOMER_REFERENCE);
        purchase.setCardAcceptorTaxId(CARD_ACCEPTOR_TAX_ID);
        purchase.setDestinationCountryCode(US_COUNTRY_CODE);
        purchase.setDestinationPostalCode(DESTINATION_POSTAL_CODE);
        purchase.setTaxExempt(false);
        purchase.setSalesTax(hundredAmountMock);
        purchase.setDiscountAmount(tenAmountMock);
        purchase.setShippingAmount(ninetyAmountMock);
        purchase.setDutyAmount(dutyAmountMock);
        purchase.setOrderDate(orderDateMock);
        purchase.setItem(ImmutableList.of(item));

        return purchase;
    }
}
