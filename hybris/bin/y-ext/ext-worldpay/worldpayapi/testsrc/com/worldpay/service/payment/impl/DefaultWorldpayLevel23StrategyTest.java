package com.worldpay.service.payment.impl;

import com.google.common.collect.ImmutableList;
import com.worldpay.model.WorldpayMerchantConfigurationModel;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.data.*;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayLevel23StrategyTest {

    private static final double TEN_DOUBLE_VALUE = 10d;
    private static final double HUNDRED_DOUBLE_VALUE = 100d;
    private static final String CARD_ACCEPTOR_TAX_ID = "123456789";
    private static final String US_COUNTRY_CODE = "US";
    private static final String DESTINATION_POSTAL_CODE = "120125";
    private static final String PRODUCT_CODE = "productCode";
    private static final String COMMODITY_CODE = "1234567";
    private static final String UNIT_OF_MEASURE = "pieces";
    private static final java.util.Date ORDER_DATE_VALUE = new java.util.Date(1990, Calendar.MAY, 17);

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
    private Amount hundredAmountMock, ninetyAmountMock, tenAmountMock;
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

    private List<Purchase> purchaseList;

    @Before
    public void setUp() {
        purchaseList = ImmutableList.of(purchaseMock);

        setUpCart();
        setUpCartEntry();

        when(level23DataMock.getPurchase()).thenReturn(purchaseList);
        when(cartMock.getSite()).thenReturn(baseSiteMock);
        when(baseSiteMock.getEnableLevel2()).thenReturn(true);
        when(baseSiteMock.getEnableLevel3()).thenReturn(true);
        when(worldpayMerchantStrategyMock.getMerchant()).thenReturn(merchantMock);
        when(merchantMock.getCardAcceptorTaxID()).thenReturn(CARD_ACCEPTOR_TAX_ID);

        when(worldpayOrderService.createAmount(currencyMock, HUNDRED_DOUBLE_VALUE)).thenReturn(hundredAmountMock);
        when(worldpayOrderService.createAmount(currencyMock, TEN_DOUBLE_VALUE)).thenReturn(tenAmountMock);
        when(worldpayOrderService.createAmount(currencyMock, 0d)).thenReturn(ninetyAmountMock);
        when(worldpayOrderService.createAmount(currencyMock, 90d)).thenReturn(ninetyAmountMock);
    }

    @Test
    public void populateRequestWithAdditionalData_WhenLevel23Disabled_ShouldDoNothing() {
        doNothing().when(testObj).setCustomerReference(any(), any());
        doNothing().when(testObj).setProductDescription(any(), any());
        doNothing().when(testObj).setDutyAmount(any(), any());

        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreatorMock);

        verify(worldpayLevel23DataValidatorMock, never()).isValidLevel3Data(purchaseList);
        verify(worldpayLevel23DataValidatorMock, never()).isValidLevel2Data(purchaseList);
        verify(authoriseRequestParametersCreatorMock, never()).withLevel23Data(level23DataMock);
    }

    @Test
    public void populateRequestWithAdditionalData_WhenLevel3EnabledAndValidData_ShouldSetLevel3Data() {
        doReturn(true).when(testObj).isLevel2Enabled(cartMock);
        doReturn(level23DataMock).when(testObj).createLevel23Data(cartMock);

        doNothing().when(testObj).setCustomerReference(any(), any());
        doNothing().when(testObj).setProductDescription(any(), any());
        doNothing().when(testObj).setDutyAmount(any(), any());
        when(worldpayLevel23DataValidatorMock.isValidLevel3Data(purchaseList)).thenReturn(true);

        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreatorMock);

        verify(worldpayLevel23DataValidatorMock, never()).isValidLevel2Data(purchaseList);
        verify(authoriseRequestParametersCreatorMock).withLevel23Data(level23DataMock);
    }

    @Test
    public void populateRequestWithAdditionalData_WhenLevel3EnabledAndInValidData_ShouldSetLevel2Data() {
        doReturn(true).when(testObj).isLevel3Enabled(cartMock);
        doReturn(level23DataMock).when(testObj).createLevel23Data(cartMock);

        doNothing().when(testObj).setCustomerReference(any(), any());
        doNothing().when(testObj).setProductDescription(any(), any());
        doNothing().when(testObj).setDutyAmount(any(), any());
        when(worldpayLevel23DataValidatorMock.isValidLevel3Data(purchaseList)).thenReturn(false);
        when(worldpayLevel23DataValidatorMock.isValidLevel2Data(purchaseList)).thenReturn(true);

        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreatorMock);

        verify(authoriseRequestParametersCreatorMock).withLevel23Data(level23DataMock);
    }

    @Test
    public void isLevel2Enabled_WhenFlagIsSetToFalse_ShouldReturnFalse() {
        when(baseSiteMock.getEnableLevel2()).thenReturn(false);

        boolean result = testObj.isLevel2Enabled(cartMock);

        assertThat(result).isFalse();
    }

    @Test
    public void isLevel2Enabled_WhenFlagIsSetToTrueAndAddressIsNotUSorCA_ShouldReturnFalse() {
        when(addressMock.getCountry().getIsocode()).thenReturn("UK");

        boolean result = testObj.isLevel2Enabled(cartMock);

        assertThat(result).isFalse();
    }

    @Test
    public void isLevel2Enabled_WhenAddressIsNull_ShouldReturnFalse() {
        when(cartMock.getPaymentAddress()).thenReturn(null);

        boolean result = testObj.isLevel2Enabled(cartMock);

        assertThat(result).isFalse();
    }


    @Test
    public void isLevel2Enabled_WhenFlagIsSetToTrueAndAddressUS_ShouldReturnTrue() {
        boolean result = testObj.isLevel2Enabled(cartMock);

        assertThat(result).isTrue();
    }

    @Test
    public void isLevel3Enabled_WhenFlagIsSetToFalse_ShouldReturnFalse() {
        when(baseSiteMock.getEnableLevel3()).thenReturn(false);

        boolean result = testObj.isLevel3Enabled(cartMock);

        assertThat(result).isFalse();
    }

    @Test
    public void isLevel3Enabled_WhenFlagIsSetToTrueAndAddressIsNotUSorCA_ShouldReturnFalse() {
        when(addressMock.getCountry().getIsocode()).thenReturn("UK");

        boolean result = testObj.isLevel3Enabled(cartMock);

        assertThat(result).isFalse();
    }

    @Test
    public void isLevel3Enabled_WhenFlagIsSetToTrueAndAddressUS_ShouldReturnTrue() {
        boolean result = testObj.isLevel3Enabled(cartMock);

        assertThat(result).isTrue();
    }

    @Test(expected = NotImplementedException.class)
    public void setCustomerReference_ShouldThrowException() {
        testObj.setCustomerReference(eq(cartMock), any(Purchase.class));
    }

    @Test(expected = NotImplementedException.class)
    public void setDutyAmount_ShouldThrowException() {
        testObj.setDutyAmount(eq(cartMock), any(Purchase.class));
    }

    @Test(expected = NotImplementedException.class)
    public void setProductDescription_ShouldThrowException() {
        testObj.setProductDescription(eq(productMock), any(Item.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createLevel23Data_WhenCartIsNull_ShouldThrowException() {
        testObj.createLevel23Data(null);
    }

    @Test
    public void createLevel23Data_WhenCartIsNotNull_ShouldCreateBranchSpecific() {
        doNothing().when(testObj).setCustomerReference(any(), any());
        doNothing().when(testObj).setProductDescription(any(), any());
        doNothing().when(testObj).setDutyAmount(any(), any());

        final BranchSpecificExtension result = testObj.createLevel23Data(cartMock);

        assertThat(result.getPurchase().size()).isEqualTo(1);
        final Purchase purchase = result.getPurchase().get(0);
        assertThat(purchase.getSalesTax()).isEqualTo(hundredAmountMock);
        assertThat(purchase.getDiscountAmount()).isEqualTo(tenAmountMock);
        assertThat(purchase.getShippingAmount()).isEqualTo(ninetyAmountMock);
        assertThat(purchase.getCardAcceptorTaxId()).isEqualTo(CARD_ACCEPTOR_TAX_ID);
        assertThat(purchase.getDestinationPostalCode()).isEqualTo(DESTINATION_POSTAL_CODE);
        assertThat(purchase.getDestinationCountryCode()).isEqualTo(US_COUNTRY_CODE);

        assertThat(purchase.getItem().size()).isEqualTo(1);
        final Item item = purchase.getItem().get(0);
        assertThat(item.getProductCode()).isEqualTo(PRODUCT_CODE);
        assertThat(item.getCommodityCode()).isEqualTo(COMMODITY_CODE);
        assertThat(item.getQuantity()).isEqualTo("1");
        assertThat(item.getUnitOfMeasure()).isEqualTo(UNIT_OF_MEASURE);

        assertThat(item.getUnitCost()).isEqualTo(hundredAmountMock);
        assertThat(item.getItemTotalWithTax()).isEqualTo(hundredAmountMock);
        assertThat(item.getTaxAmount()).isEqualTo(tenAmountMock);
        assertThat(item.getItemTotal()).isEqualTo(ninetyAmountMock);
        assertThat(item.getItemDiscountAmount()).isEqualTo(tenAmountMock);
    }

    private void setUpCart() {
        when(cartMock.getCurrency()).thenReturn(currencyMock);
        when(cartMock.getTotalTax()).thenReturn(HUNDRED_DOUBLE_VALUE);
        when(cartMock.getTotalDiscounts()).thenReturn(TEN_DOUBLE_VALUE);
        when(cartMock.getDeliveryCost()).thenReturn(0d);
        when(cartMock.getDate()).thenReturn(ORDER_DATE_VALUE);
        when(cartMock.getEntries()).thenReturn(ImmutableList.of(entryMock));
        when(cartMock.getDeliveryAddress()).thenReturn(addressMock);
        when(cartMock.getPaymentAddress()).thenReturn(addressMock);
        when(addressMock.getCountry().getIsocode()).thenReturn(US_COUNTRY_CODE);
        when(addressMock.getPostalcode()).thenReturn(DESTINATION_POSTAL_CODE);
    }

    private void setUpCartEntry() {
        when(entryMock.getProduct()).thenReturn(productMock);
        when(productMock.getCode()).thenReturn(PRODUCT_CODE);
        when(productMock.getCommodityCode()).thenReturn(COMMODITY_CODE);
        when(entryMock.getQuantity()).thenReturn(1L);
        when(entryMock.getBasePrice()).thenReturn(HUNDRED_DOUBLE_VALUE);
        when(entryMock.getUnit().getCode()).thenReturn(UNIT_OF_MEASURE);
        when(entryMock.getTotalPrice()).thenReturn(HUNDRED_DOUBLE_VALUE);
        when(entryMock.getTaxValues()).thenReturn(ImmutableList.of(taxValueMock));
        when(taxValueMock.getAppliedValue()).thenReturn(TEN_DOUBLE_VALUE);
        when(entryMock.getDiscountValues()).thenReturn(ImmutableList.of(discountValueMock));
        when(discountValueMock.getAppliedValue()).thenReturn(TEN_DOUBLE_VALUE);
    }
}
