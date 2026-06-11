package com.worldpay.service.payment.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.worldpay.data.Amount;
import com.worldpay.data.BranchSpecificExtension;
import com.worldpay.data.Item;
import com.worldpay.data.Purchase;
import com.worldpay.model.WorldpayMerchantConfigurationModel;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.payment.WorldpayLevel23DataValidator;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.request.AuthoriseRequestParameters;
import com.worldpay.strategy.WorldpayMerchantStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.TaxValue;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
@ExtendWith(MockitoExtension.class)
class DefaultWorldpayLevel23StrategyTest {

    private static final double TEN_DOUBLE_VALUE = 10d;
    private static final double HUNDRED_DOUBLE_VALUE = 100d;
    private static final String CARD_ACCEPTOR_TAX_ID = "123456789";
    private static final String US_COUNTRY_CODE = "US";
    private static final String CA_COUNTRY_CODE = "CA";
    private static final String UK_COUNTRY_CODE = "UK";
    private static final String DESTINATION_POSTAL_CODE = "120125";
    private static final String PRODUCT_CODE = "productCode";
    private static final String COMMODITY_CODE = "1234567";
    private static final String UNIT_OF_MEASURE = "pieces";
    private static final Date ORDER_DATE_VALUE = Date.from(LocalDate.of(1990, 5, 17).atStartOfDay(ZoneId.systemDefault()).toInstant());

    @Spy
    @InjectMocks
    private DefaultWorldpayLevel23Strategy testObj;

    @Mock
    private WorldpayMerchantStrategy worldpayMerchantStrategyMock;
    @Mock
    private WorldpayLevel23DataValidator worldpayLevel23DataValidatorMock;
    @Mock
    private WorldpayOrderService worldpayOrderService;

    @Mock
    private AbstractOrderModel cartMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private AddressModel addressMock;
    @Mock
    private BaseSiteModel baseSiteMock;
    @Mock
    private ProductModel productMock;
    @Mock
    private Amount hundredAmountMock, ninetyAmountMock, tenAmountMock, zeroAmountMock;
    @Mock
    private WorldpayMerchantConfigurationModel merchantMock;
    @Mock
    private CurrencyModel currencyMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private AbstractOrderEntryModel entryMock;
    @Mock
    private TaxValue taxValueMock;
    @Mock
    private DiscountValue discountValueMock;
    @Mock
    private AuthoriseRequestParameters.AuthoriseRequestParametersCreator authoriseRequestParametersCreatorMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    private BranchSpecificExtension level23DataMock;
    @Mock
    private Purchase purchaseMock;

    @Test
    void populateRequestWithAdditionalData_WhenLevel23Disabled_ShouldDoNothing() {
        doReturn(false).when(testObj).isLevel2Enabled(cartMock);
        doReturn(false).when(testObj).isLevel3Enabled(cartMock);

        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreatorMock);

        verify(worldpayLevel23DataValidatorMock, never()).isValidLevel2Data(any());
        verify(worldpayLevel23DataValidatorMock, never()).isValidLevel3Data(any());
        verify(authoriseRequestParametersCreatorMock, never()).withLevel23Data(any());
    }

    @Test
    void populateRequestWithAdditionalData_WhenLevel3EnabledAndValidData_ShouldSetLevel3Data() {
        doReturn(true).when(testObj).isLevel3Enabled(cartMock);
        doReturn(level23DataMock).when(testObj).createLevel23Data(cartMock);

        final List<Purchase> purchaseList = Collections.singletonList(purchaseMock);

        when(level23DataMock.getPurchase()).thenReturn(purchaseList);
        when(worldpayLevel23DataValidatorMock.isValidLevel3Data(purchaseList)).thenReturn(true);


        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreatorMock);


        verify(testObj).isLevel3Enabled(cartMock);
        verify(testObj, never()).isLevel2Enabled(cartMock);

        verify(testObj).createLevel23Data(cartMock);

        verify(worldpayLevel23DataValidatorMock).isValidLevel3Data(purchaseList);
        verify(worldpayLevel23DataValidatorMock, never()).isValidLevel2Data(purchaseList);

        verify(authoriseRequestParametersCreatorMock).withLevel23Data(level23DataMock);
    }

    @Test
    void populateRequestWithAdditionalData_WhenLevel3EnabledAndInvalidData_ShouldSetLevel2Data() {
        doReturn(true).when(testObj).isLevel3Enabled(cartMock);
        doReturn(level23DataMock).when(testObj).createLevel23Data(cartMock);

        final List<Purchase> purchaseList = Collections.singletonList(purchaseMock);

        when(level23DataMock.getPurchase()).thenReturn(purchaseList);

        when(worldpayLevel23DataValidatorMock.isValidLevel3Data(purchaseList)).thenReturn(false);
        when(worldpayLevel23DataValidatorMock.isValidLevel2Data(purchaseList)).thenReturn(true);

        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreatorMock);

        verify(authoriseRequestParametersCreatorMock).withLevel23Data(level23DataMock);
    }

    @Test
    void populateRequestWithAdditionalData_WhenLevel2EnabledAndValidData_ShouldSetLevel2Data() {
        doReturn(false).when(testObj).isLevel3Enabled(cartMock);
        doReturn(true).when(testObj).isLevel2Enabled(cartMock);
        doReturn(level23DataMock).when(testObj).createLevel23Data(cartMock);

        final List<Purchase> purchaseList = Collections.singletonList(purchaseMock);
        when(level23DataMock.getPurchase()).thenReturn(purchaseList);
        when(worldpayLevel23DataValidatorMock.isValidLevel3Data(purchaseList)).thenReturn(false);
        when(worldpayLevel23DataValidatorMock.isValidLevel2Data(purchaseList)).thenReturn(true);

        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreatorMock);

        verify(authoriseRequestParametersCreatorMock).withLevel23Data(level23DataMock);
    }

    @Test
    void populateRequestWithAdditionalData_WhenEnabledButBothValidationsFail_ShouldNotSetData() {
        doReturn(true).when(testObj).isLevel3Enabled(cartMock);
        doReturn(level23DataMock).when(testObj).createLevel23Data(cartMock);

        final List<Purchase> purchaseList = Collections.singletonList(purchaseMock);
        when(level23DataMock.getPurchase()).thenReturn(purchaseList);
        when(worldpayLevel23DataValidatorMock.isValidLevel3Data(purchaseList)).thenReturn(false);
        when(worldpayLevel23DataValidatorMock.isValidLevel2Data(purchaseList)).thenReturn(false);

        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreatorMock);

        verify(authoriseRequestParametersCreatorMock, never()).withLevel23Data(any());
    }

    @Test
    void isLevel2Enabled_WhenFlagIsSetToTrueAndAddressUS_ShouldReturnTrue() {
        when(cartMock.getSite()).thenReturn(baseSiteMock);
        when(baseSiteMock.getEnableLevel2()).thenReturn(true);
        when(cartMock.getPaymentAddress()).thenReturn(addressMock);
        when(addressMock.getCountry().getIsocode()).thenReturn(US_COUNTRY_CODE);

        boolean result = testObj.isLevel2Enabled(cartMock);

        assertTrue(result);
    }

    @Test
    void isLevel2Enabled_WhenFlagIsSetToTrueAndAddressCA_ShouldReturnTrue() {
        when(cartMock.getSite()).thenReturn(baseSiteMock);
        when(baseSiteMock.getEnableLevel2()).thenReturn(true);
        when(cartMock.getPaymentAddress()).thenReturn(addressMock);
        when(addressMock.getCountry().getIsocode()).thenReturn(CA_COUNTRY_CODE);

        boolean result = testObj.isLevel2Enabled(cartMock);

        assertTrue(result);
    }

    @Test
    void isLevel2Enabled_WhenFlagIsSetToFalse_ShouldReturnFalse() {
        when(cartMock.getSite()).thenReturn(baseSiteMock);
        when(baseSiteMock.getEnableLevel2()).thenReturn(false);

        boolean result = testObj.isLevel2Enabled(cartMock);

        assertFalse(result);
    }

    @Test
    void isLevel2Enabled_WhenFlagIsSetToTrueAndAddressIsNotUSorCA_ShouldReturnFalse() {
        when(cartMock.getSite()).thenReturn(baseSiteMock);
        when(baseSiteMock.getEnableLevel2()).thenReturn(true);

        when(cartMock.getPaymentAddress()).thenReturn(addressMock);
        when(addressMock.getCountry().getIsocode()).thenReturn(UK_COUNTRY_CODE);

        boolean result = testObj.isLevel2Enabled(cartMock);

        assertFalse(result);
    }

    @Test
    void isLevel2Enabled_WhenAddressIsNull_ShouldReturnFalse() {
        when(cartMock.getSite()).thenReturn(baseSiteMock);
        when(baseSiteMock.getEnableLevel2()).thenReturn(true);

        when(cartMock.getPaymentAddress()).thenReturn(null);

        boolean result = testObj.isLevel2Enabled(cartMock);

        assertFalse(result);
    }

    @Test
    void isLevel3Enabled_WhenFlagIsSetToTrueAndAddressUS_ShouldReturnTrue() {
        when(cartMock.getSite()).thenReturn(baseSiteMock);
        when(baseSiteMock.getEnableLevel3()).thenReturn(true);

        when(cartMock.getPaymentAddress()).thenReturn(addressMock);
        when(addressMock.getCountry().getIsocode()).thenReturn(US_COUNTRY_CODE);


        boolean result = testObj.isLevel3Enabled(cartMock);

        assertTrue(result);
    }

    @Test
    void isLevel3Enabled_WhenFlagIsSetToTrueAndAddressCA_ShouldReturnTrue() {
        when(cartMock.getSite()).thenReturn(baseSiteMock);
        when(baseSiteMock.getEnableLevel3()).thenReturn(true);

        when(cartMock.getPaymentAddress()).thenReturn(addressMock);
        when(addressMock.getCountry().getIsocode()).thenReturn(CA_COUNTRY_CODE);


        boolean result = testObj.isLevel3Enabled(cartMock);

        assertTrue(result);
    }

    @Test
    void isLevel3Enabled_WhenFlagIsSetToFalse_ShouldReturnFalse() {
        when(cartMock.getSite()).thenReturn(baseSiteMock);
        when(baseSiteMock.getEnableLevel3()).thenReturn(false);

        boolean result = testObj.isLevel3Enabled(cartMock);

        assertFalse(result);
    }

    @Test
    void isLevel3Enabled_WhenFlagIsSetToTrueAndAddressIsNotUSorCA_ShouldReturnFalse() {
        when(cartMock.getSite()).thenReturn(baseSiteMock);
        when(baseSiteMock.getEnableLevel3()).thenReturn(true);

        when(cartMock.getPaymentAddress()).thenReturn(addressMock);
        when(addressMock.getCountry().getIsocode()).thenReturn("UK");

        boolean result = testObj.isLevel3Enabled(cartMock);

        assertFalse(result);
    }

    @Test
    void setCustomerReference_ShouldThrowException() {
        assertThrows(NotImplementedException.class, () -> testObj.setCustomerReference(cartMock, purchaseMock));
    }

    @Test
    void setDutyAmount_ShouldThrowException() {
        assertThrows(NotImplementedException.class, () -> testObj.setDutyAmount(cartMock, purchaseMock));
    }

    @Test
    void setProductDescription_ShouldThrowException() {
        final Item dummyItem = new Item();
        assertThrows(NotImplementedException.class, () -> testObj.setProductDescription(productMock, dummyItem));
    }

    @Test
    void createLevel23Data_WhenCartIsNull_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> testObj.createLevel23Data(null));
    }

    @Test
    void createLevel23Data_WhenCartIsNotNull_ShouldCreateBranchSpecific() {
        doNothing().when(testObj).setCustomerReference(any(), any());
        doNothing().when(testObj).setDutyAmount(any(), any());
        doNothing().when(testObj).setProductDescription(any(), any());

        when(worldpayMerchantStrategyMock.getMerchant()).thenReturn(merchantMock);
        when(merchantMock.getCardAcceptorTaxID()).thenReturn(CARD_ACCEPTOR_TAX_ID);

        when(cartMock.getCurrency()).thenReturn(currencyMock);
        when(cartMock.getTotalTax()).thenReturn(HUNDRED_DOUBLE_VALUE);
        when(cartMock.getTotalDiscounts()).thenReturn(TEN_DOUBLE_VALUE);
        when(cartMock.getDeliveryCost()).thenReturn(0d);
        when(cartMock.getDeliveryAddress()).thenReturn(addressMock);
        when(cartMock.getDate()).thenReturn(ORDER_DATE_VALUE);
        when(cartMock.getEntries()).thenReturn(Collections.singletonList(entryMock));

        when(addressMock.getCountry().getIsocode()).thenReturn(US_COUNTRY_CODE);
        when(addressMock.getPostalcode()).thenReturn(DESTINATION_POSTAL_CODE);

        when(entryMock.getProduct()).thenReturn(productMock);
        when(productMock.getCode()).thenReturn(PRODUCT_CODE);
        when(productMock.getCommodityCode()).thenReturn(COMMODITY_CODE);
        when(entryMock.getQuantity()).thenReturn(1L);
        when(entryMock.getBasePrice()).thenReturn(HUNDRED_DOUBLE_VALUE);
        when(entryMock.getUnit().getCode()).thenReturn(UNIT_OF_MEASURE);
        when(entryMock.getTotalPrice()).thenReturn(HUNDRED_DOUBLE_VALUE);
        when(entryMock.getTaxValues()).thenReturn(List.of(taxValueMock));
        when(taxValueMock.getAppliedValue()).thenReturn(TEN_DOUBLE_VALUE);
        when(entryMock.getDiscountValues()).thenReturn(Collections.singletonList(discountValueMock));
        when(discountValueMock.getAppliedValue()).thenReturn(TEN_DOUBLE_VALUE);

        when(worldpayOrderService.createAmount(currencyMock, HUNDRED_DOUBLE_VALUE)).thenReturn(hundredAmountMock);
        when(worldpayOrderService.createAmount(currencyMock, TEN_DOUBLE_VALUE)).thenReturn(tenAmountMock);
        when(worldpayOrderService.createAmount(currencyMock, 0d)).thenReturn(zeroAmountMock);
        when(worldpayOrderService.createAmount(currencyMock, 90d)).thenReturn(ninetyAmountMock);

        final BranchSpecificExtension result = testObj.createLevel23Data(cartMock);

        assertThat(result.getPurchase()).hasSize(1);
        final Purchase purchase = result.getPurchase().getFirst();
        verify(testObj).setCustomerReference(eq(cartMock), any(Purchase.class));
        assertEquals(hundredAmountMock, purchase.getSalesTax());
        assertFalse(purchase.isTaxExempt());
        assertEquals(tenAmountMock, purchase.getDiscountAmount());
        assertEquals(zeroAmountMock, purchase.getShippingAmount());
        assertEquals(CARD_ACCEPTOR_TAX_ID, purchase.getCardAcceptorTaxId());
        verify(testObj).setDutyAmount(eq(cartMock), any(Purchase.class));
        assertEquals(DESTINATION_POSTAL_CODE, purchase.getDestinationPostalCode());
        assertEquals(US_COUNTRY_CODE, purchase.getDestinationCountryCode());
        assertNotNull(purchase.getOrderDate());

        verify(testObj).setProductDescription(eq(productMock), any(Item.class));
        assertThat(purchase.getItem()).hasSize(1);
        final Item item = purchase.getItem().getFirst();
        assertEquals(PRODUCT_CODE, item.getProductCode());
        assertEquals(COMMODITY_CODE, item.getCommodityCode());
        assertEquals("1", item.getQuantity());
        assertEquals(UNIT_OF_MEASURE, item.getUnitOfMeasure());

        assertEquals(hundredAmountMock, item.getUnitCost());
        assertEquals(hundredAmountMock, item.getItemTotalWithTax());
        assertEquals(tenAmountMock, item.getTaxAmount());
        assertEquals(ninetyAmountMock, item.getItemTotal());
        assertEquals(tenAmountMock, item.getItemDiscountAmount());
    }

    @Test
    void createLevel23Data_WhenDeliveryAddressIsNull_ShouldNotSetDestination() {
        doNothing().when(testObj).setCustomerReference(any(), any());
        doNothing().when(testObj).setDutyAmount(any(), any());
        doNothing().when(testObj).setProductDescription(any(), any());

        when(worldpayMerchantStrategyMock.getMerchant()).thenReturn(merchantMock);
        when(merchantMock.getCardAcceptorTaxID()).thenReturn(CARD_ACCEPTOR_TAX_ID);

        when(cartMock.getCurrency()).thenReturn(currencyMock);
        when(cartMock.getTotalTax()).thenReturn(HUNDRED_DOUBLE_VALUE);
        when(cartMock.getTotalDiscounts()).thenReturn(TEN_DOUBLE_VALUE);
        when(cartMock.getDeliveryCost()).thenReturn(0d);
        when(cartMock.getDeliveryAddress()).thenReturn(null);
        when(cartMock.getDate()).thenReturn(ORDER_DATE_VALUE);
        when(cartMock.getEntries()).thenReturn(Collections.singletonList(entryMock));

        when(entryMock.getProduct()).thenReturn(productMock);
        when(productMock.getCode()).thenReturn(PRODUCT_CODE);
        when(productMock.getCommodityCode()).thenReturn(COMMODITY_CODE);
        when(entryMock.getQuantity()).thenReturn(1L);
        when(entryMock.getBasePrice()).thenReturn(HUNDRED_DOUBLE_VALUE);
        when(entryMock.getUnit().getCode()).thenReturn(UNIT_OF_MEASURE);
        when(entryMock.getTotalPrice()).thenReturn(HUNDRED_DOUBLE_VALUE);
        when(entryMock.getTaxValues()).thenReturn(Collections.singletonList(taxValueMock));
        when(taxValueMock.getAppliedValue()).thenReturn(TEN_DOUBLE_VALUE);
        when(entryMock.getDiscountValues()).thenReturn(Collections.singletonList(discountValueMock));
        when(discountValueMock.getAppliedValue()).thenReturn(TEN_DOUBLE_VALUE);

        when(worldpayOrderService.createAmount(currencyMock, HUNDRED_DOUBLE_VALUE)).thenReturn(hundredAmountMock);
        when(worldpayOrderService.createAmount(currencyMock, TEN_DOUBLE_VALUE)).thenReturn(tenAmountMock);
        when(worldpayOrderService.createAmount(currencyMock, 0d)).thenReturn(zeroAmountMock);
        when(worldpayOrderService.createAmount(currencyMock, 90d)).thenReturn(ninetyAmountMock);

        final BranchSpecificExtension result = testObj.createLevel23Data(cartMock);

        final Purchase purchase = result.getPurchase().getFirst();
        assertNull(purchase.getDestinationCountryCode());
        assertNull(purchase.getDestinationPostalCode());
    }

    @Test
    void setSalesTaxAndTaxExempt_WhenTotalTaxIsZero_ShouldMarkPurchaseAsTaxExempt() {
        final Purchase purchase = new Purchase();

        when(cartMock.getTotalTax()).thenReturn(0d);
        when(worldpayOrderService.createAmount(currencyMock, 0d)).thenReturn(zeroAmountMock);

        testObj.setSalesTaxAndTaxExempt(purchase, currencyMock, cartMock);

        assertEquals(zeroAmountMock, purchase.getSalesTax());
        assertTrue(purchase.isTaxExempt());
    }
}
