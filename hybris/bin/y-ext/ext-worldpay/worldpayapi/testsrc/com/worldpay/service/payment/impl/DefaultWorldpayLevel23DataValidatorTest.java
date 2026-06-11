package com.worldpay.service.payment.impl;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.worldpay.data.Amount;
import com.worldpay.data.Date;
import com.worldpay.data.Item;
import com.worldpay.data.Purchase;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
@ExtendWith(MockitoExtension.class)
class DefaultWorldpayLevel23DataValidatorTest {

    private static final String INVOICE_REFERENCE_NUMBER = "invoiceReference";
    private static final String CUSTOMER_REFERENCE = "customerReference";
    private static final String CARD_ACCEPTOR_TAX_ID = "123456789";
    private static final String US_COUNTRY_CODE = "US";
    private static final String DESTINATION_POSTAL_CODE = "120125";
    private static final String PRODUCT_DESCRIPTION = "product description";
    private static final String PRODUCT_CODE = "productCode";
    private static final String COMMODITY_CODE = "1234567";
    private static final String UNIT_OF_MEASURE = "pieces";
    private static final String OVER_MAX_LENGTH_CUSTOMER_REFERENCE = "1".repeat(21);
    private static final String OVER_MAX_LENGTH_CARD_ACCEPTOR_TAX_ID = "1".repeat(21);
    private static final String OVER_MAX_LENGTH_DESTINATION_POSTAL_CODE = "1".repeat(14);
    private static final String OVER_MAX_LENGTH_DESCRIPTION = "1".repeat(28);
    private static final String OVER_MAX_LENGTH_PRODUCT_CODE = "1".repeat(15);
    private static final String OVER_MAX_LENGTH_COMMODITY_CODE = "1".repeat(17);
    private static final String OVER_MAX_LENGTH_UNIT_OF_MEASURE = "1".repeat(17);

    @InjectMocks
    private DefaultWorldpayLevel23DataValidator testObj;

    @Mock
    private Date orderDateMock;
    @Mock
    private Amount hundredAmountMock, ninetyAmountMock, tenAmountMock, dutyAmountMock, totalAmountMock, taxAmountMock;

    private Purchase purchase;
    private Item item;

    @BeforeEach
    void setUp() {
        createItem();
        createPurchase();
    }

    @Test
    void isValidLevel2Data_WhenPurchaseListIsEmpty_ShouldThrowException() {
        List<Purchase> emptyPurchaseList = Collections.emptyList();
        assertThrows(NoSuchElementException.class, () -> testObj.isValidLevel2Data(emptyPurchaseList));
    }

    @Test
    void isValidLevel2Data_WhenCustomerReferenceIsInvalid_ShouldReturnFalse() {
        purchase.setCustomerReference(OVER_MAX_LENGTH_CUSTOMER_REFERENCE);

        boolean result = testObj.isValidLevel2Data(Collections.singletonList(purchase));

        assertFalse(result);
    }

    @Test
    void isValidLevel2Data_WhenCardAcceptorTaxIdIsInvalid_ShouldReturnFalse() {
        purchase.setCardAcceptorTaxId(OVER_MAX_LENGTH_CARD_ACCEPTOR_TAX_ID);

        boolean result = testObj.isValidLevel2Data(Collections.singletonList(purchase));

        assertFalse(result);
    }

    @Test
    void isValidLevel2Data_WhenDestinationPostalCodeIsInvalid_ShouldSetValueToNullAndReturnTrue() {
        purchase.setDestinationPostalCode(OVER_MAX_LENGTH_DESTINATION_POSTAL_CODE);

        boolean result = testObj.isValidLevel2Data(Collections.singletonList(purchase));

        assertTrue(result);
        assertNull(purchase.getDestinationPostalCode());
    }

    @Test
    void isValidLevel2Data_WhenItemIsNull_ShouldReturnTrue() {
        purchase.setItem(null);

        boolean result = testObj.isValidLevel2Data(Collections.singletonList(purchase));

        assertTrue(result);
    }

    @Test
    void isValidLevel2Data_WhenItemIsEmptyList_ShouldReturnTrue() {
        purchase.setItem(Collections.emptyList());

        boolean result = testObj.isValidLevel2Data(Collections.singletonList(purchase));

        assertTrue(result);
    }

    @Test
    void isValidLevel2Data_WhenDescriptionInvalid_ShouldReturnFalse() {
        item.setDescription(OVER_MAX_LENGTH_DESCRIPTION);

        boolean result = testObj.isValidLevel2Data(Collections.singletonList(purchase));

        assertFalse(result);
    }

    @Test
    void isValidLevel2Data_WhenProductCodeInvalid_ShouldSetValueToNullAndReturnTrue() {
        item.setProductCode(OVER_MAX_LENGTH_PRODUCT_CODE);

        boolean result = testObj.isValidLevel2Data(Collections.singletonList(purchase));

        assertTrue(result);
        assertNull(item.getProductCode());
    }

    @Test
    void isValidLevel2Data_WhenCommodityCodeInvalid_ShouldSetValueToNullAndReturnTrue() {
        item.setCommodityCode(OVER_MAX_LENGTH_COMMODITY_CODE);

        boolean result = testObj.isValidLevel2Data(Collections.singletonList(purchase));

        assertTrue(result);
        assertNull(item.getCommodityCode());
    }

    @Test
    void isValidLevel2Data_WhenUnitOfMeasureInvalid_ShouldSetValueToNullAndReturnTrue() {
        item.setUnitOfMeasure(OVER_MAX_LENGTH_UNIT_OF_MEASURE);

        boolean result = testObj.isValidLevel2Data(Collections.singletonList(purchase));

        assertTrue(result);
        assertNull(item.getUnitOfMeasure());
    }

    @Test
    void isValidLevel3Data_WhenAllFieldsAreValid_ShouldReturnTrue() {
        boolean result = testObj.isValidLevel3Data(Collections.singletonList(purchase));

        assertTrue(result);
    }

    @Test
    void isValidLevel3Data_WhenPurchaseListIsEmpty_ShouldThrowException() {
        List<Purchase> emptyPurchaseList = Collections.emptyList();
        assertThrows(NoSuchElementException.class, () -> testObj.isValidLevel3Data(emptyPurchaseList));
    }

    @Test
    void isValidLevel3Data_WhenCustomerReferenceIsInvalid_ShouldReturnFalse() {
        purchase.setCustomerReference(OVER_MAX_LENGTH_CUSTOMER_REFERENCE);

        boolean result = testObj.isValidLevel3Data(Collections.singletonList(purchase));

        assertFalse(result);
    }

    @Test
    void isValidLevel3Data_WhenCardAcceptorTaxIdIsInvalid_ShouldReturnFalse() {
        purchase.setCardAcceptorTaxId(OVER_MAX_LENGTH_CARD_ACCEPTOR_TAX_ID);

        boolean result = testObj.isValidLevel3Data(Collections.singletonList(purchase));

        assertFalse(result);
    }

    @Test
    void isValidLevel3Data_WhenDestinationPostalCodeIsInvalid_ShouldReturnFalse() {
        purchase.setDestinationPostalCode(OVER_MAX_LENGTH_DESTINATION_POSTAL_CODE);

        boolean result = testObj.isValidLevel3Data(Collections.singletonList(purchase));

        assertFalse(result);
    }

    @Test
    void isValidLevel3Data_WhenItemListIsNull_ShouldReturnFalse() {
        purchase.setItem(null);

        boolean result = testObj.isValidLevel3Data(Collections.singletonList(purchase));

        assertFalse(result);
    }

    @Test
    void isValidLevel3Data_WhenItemContainsNullEntry_ShouldReturnFalse() {
        purchase.setItem(Collections.singletonList(null));

        boolean result = testObj.isValidLevel3Data(Collections.singletonList(purchase));

        assertFalse(result);
    }

    @Test
    void isValidLevel3Data_WhenItemDescriptionIsInvalid_ShouldReturnFalse() {
        item.setDescription(OVER_MAX_LENGTH_DESCRIPTION);

        boolean result = testObj.isValidLevel3Data(Collections.singletonList(purchase));

        assertFalse(result);
    }

    @Test
    void isValidLevel3Data_WhenItemProductCodeIsInvalid_ShouldReturnFalse() {
        item.setProductCode(OVER_MAX_LENGTH_PRODUCT_CODE);

        boolean result = testObj.isValidLevel3Data(Collections.singletonList(purchase));

        assertFalse(result);
    }

    @Test
    void isValidLevel3Data_WhenItemCommodityCodeIsInvalid_ShouldReturnFalse() {
        item.setCommodityCode(OVER_MAX_LENGTH_COMMODITY_CODE);

        boolean result = testObj.isValidLevel3Data(Collections.singletonList(purchase));

        assertFalse(result);
    }

    @Test
    void isValidLevel3Data_WhenItemUnitOfMeasureIsInvalid_ShouldReturnFalse() {
        item.setUnitOfMeasure(OVER_MAX_LENGTH_UNIT_OF_MEASURE);

        boolean result = testObj.isValidLevel3Data(Collections.singletonList(purchase));

        assertFalse(result);
    }

    @Test
    void isValidField_WhenLengthEqualsMaxSize_ShouldReturnTrue() {
        final String MAX_LENGTH_FIELD = "1".repeat(12);

        boolean result = testObj.isValidField(MAX_LENGTH_FIELD, 12);

        assertTrue(result);
    }

    @Test
    void isValidField_WhenLengthExceedsMaxSize_ShouldReturnFalse() {
        final String OVER_MAX_LENGTH_FIELD = "1".repeat(13);

        boolean result = testObj.isValidField(OVER_MAX_LENGTH_FIELD, 12);

        assertFalse(result);
    }

    @Test
    void isValidField_WhenValueIsBlank_ShouldReturnFalse() {
        final String BLANK_FIELD = "   ";

        boolean result = testObj.isValidField(BLANK_FIELD, 12);

        assertFalse(result);
    }

    private void createItem() {
        item = new Item();
        item.setDescription(PRODUCT_DESCRIPTION);
        item.setProductCode(PRODUCT_CODE);
        item.setQuantity("1");
        item.setCommodityCode(COMMODITY_CODE);
        item.setUnitOfMeasure(UNIT_OF_MEASURE);
        item.setItemTotal(hundredAmountMock);
        item.setItemTotalWithTax(totalAmountMock);
        item.setItemDiscountAmount(tenAmountMock);
        item.setTaxAmount(taxAmountMock);
    }

    private void createPurchase() {
        purchase = new Purchase();
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
        purchase.setItem(Collections.singletonList(item));
    }
}
