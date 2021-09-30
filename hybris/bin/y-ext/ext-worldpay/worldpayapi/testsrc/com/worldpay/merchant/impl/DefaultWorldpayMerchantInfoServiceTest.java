package com.worldpay.merchant.impl;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.merchant.configuration.services.WorldpayMerchantConfigurationService;
import com.worldpay.model.WorldpayMerchantConfigurationModel;
import com.worldpay.data.MerchantInfo;
import com.worldpay.strategy.WorldpayMerchantStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayMerchantInfoServiceTest {

    private static final String KNOWN_MERCHANT_CODE = "knownMerchantCode";
    private static final String MERCHANT_CODE = "merchantCode";
    private static final String MAC_SECRET = "macSecret";
    private static final String MERCHANT_PASSWORD = "password";

    @Spy
    @InjectMocks
    private DefaultWorldpayMerchantInfoService testObj;
    @Mock
    private WorldpayMerchantConfigData worldpayMerchantConfigDataMock, anotherWorldpayMerchantConfigDataMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private PaymentTransactionModel paymentTransactionMock;
    @Mock
    private WorldpayMerchantStrategy worldpayMerchantStrategyMock;
    @Mock
    private WorldpayMerchantConfigurationService worldpayMerchantConfigurationServiceMock;
    @Mock
    private MerchantInfo merchantInfo;
    @Mock
    private WorldpayMerchantConfigurationModel worldpayMerchantConfigurationMock;
    @Mock
    private CMSSiteModel cmsSiteMock;

    @Before
    public void setUp() {
        when(worldpayMerchantConfigDataMock.getCode()).thenReturn(KNOWN_MERCHANT_CODE);
        when(anotherWorldpayMerchantConfigDataMock.getCode()).thenReturn(MERCHANT_CODE);
        when(worldpayMerchantConfigurationServiceMock.getAllSystemActiveSiteMerchantConfigurations()).thenReturn(Set.of(worldpayMerchantConfigurationMock));
    }

    @Test
    public void getReplenishmentMerchant_whenSiteAndConfigNotNull_shouldCreateMerchantInfoFromReplenishmentMerchantConfigData() {
        when(cmsSiteMock.getReplenishmentMerchantConfiguration()).thenReturn(worldpayMerchantConfigurationMock);
        doReturn(merchantInfo).when(testObj).createMerchantInfo(worldpayMerchantConfigurationMock);

        final MerchantInfo result = testObj.getReplenishmentMerchant(cmsSiteMock);

        assertEquals(merchantInfo, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getReplenishmentMerchant_whenSiteNull_shouldCreateMerchantInfoFromReplenishmentMerchantConfigData() {
        testObj.getReplenishmentMerchant(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getReplenishmentMerchant_whenSiteDoesNotHaveReplenishmentConfig_shouldCreateMerchantInfoFromReplenishmentMerchantConfigData() {
        testObj.getReplenishmentMerchant(cmsSiteMock);
    }

    @Test
    public void getCurrentSiteMerchant_shouldCreateMerchantInfoFromWebSiteMerchantConfigData() {
        when(worldpayMerchantStrategyMock.getMerchant()).thenReturn(worldpayMerchantConfigurationMock);
        doReturn(merchantInfo).when(testObj).createMerchantInfo(worldpayMerchantConfigurationMock);

        final MerchantInfo result = testObj.getCurrentSiteMerchant();

        assertEquals(merchantInfo, result);
    }

    @Test
    public void getMerchantInfoByCode_shouldReturnAlreadyKnownMerchantCode() throws WorldpayConfigurationException {
        when(worldpayMerchantConfigurationMock.getCode()).thenReturn(KNOWN_MERCHANT_CODE);

        final MerchantInfo result = testObj.getMerchantInfoByCode(KNOWN_MERCHANT_CODE);

        assertEquals(KNOWN_MERCHANT_CODE, result.getMerchantCode());
    }

    @Test(expected = WorldpayConfigurationException.class)
    public void getMerchantInfoByCode_shouldReturnExceptionWhenMerchantIsNotFound() throws WorldpayConfigurationException {
        when(worldpayMerchantConfigurationMock.getCode()).thenReturn(KNOWN_MERCHANT_CODE);

        testObj.getMerchantInfoByCode("mumbo jumbo");
    }

    @Test
    public void getMerchantInfoFromTransaction_shouldGetMerchantInfoFromTransaction() throws WorldpayConfigurationException {
        when(worldpayMerchantConfigurationMock.getCode()).thenReturn(MERCHANT_CODE);
        when(paymentTransactionMock.getRequestToken()).thenReturn(MERCHANT_CODE);

        final MerchantInfo result = testObj.getMerchantInfoFromTransaction(paymentTransactionMock);

        assertEquals(MERCHANT_CODE, result.getMerchantCode());
    }

    @Test
    public void createMerchantInfo_shouldCreateMerchantInfoUsingMacValidation() {
        when(worldpayMerchantConfigurationMock.getCode()).thenReturn(MERCHANT_CODE);
        when(worldpayMerchantConfigurationMock.getPassword()).thenReturn(MERCHANT_PASSWORD);
        when(worldpayMerchantConfigurationMock.getMacSecret()).thenReturn(MAC_SECRET);
        when(worldpayMerchantConfigurationMock.getMacValidation()).thenReturn(true);

        final MerchantInfo result = testObj.createMerchantInfo(worldpayMerchantConfigurationMock);

        assertEquals(MAC_SECRET, result.getMacSecret());
        assertEquals(MERCHANT_CODE, result.getMerchantCode());
        assertEquals(MERCHANT_PASSWORD, result.getMerchantPassword());
        assertTrue(result.isUsingMacValidation());
    }

    @Test
    public void createMerchantInfo_shouldCreateMerchantInfoNotUsingMacValidation() {
        when(worldpayMerchantConfigurationMock.getCode()).thenReturn(MERCHANT_CODE);
        when(worldpayMerchantConfigurationMock.getPassword()).thenReturn(MERCHANT_PASSWORD);
        when(worldpayMerchantConfigurationMock.getMacSecret()).thenReturn(MAC_SECRET);
        when(worldpayMerchantConfigurationMock.getMacValidation()).thenReturn(false);

        final MerchantInfo result = testObj.createMerchantInfo(worldpayMerchantConfigurationMock);

        assertEquals(MERCHANT_CODE, result.getMerchantCode());
        assertEquals(MERCHANT_PASSWORD, result.getMerchantPassword());
        assertNull(result.getMacSecret());
        assertFalse(result.isUsingMacValidation());
    }
}
