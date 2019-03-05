package com.worldpay.merchant.impl;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.strategy.WorldpayMerchantStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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
    private WorldpayMerchantConfigData merchantConfigDataMock;
    @Mock
    private MerchantInfo merchantInfo;

    @Before
    public void setUp() {
        when(worldpayMerchantConfigDataMock.getCode()).thenReturn(KNOWN_MERCHANT_CODE);
        when(anotherWorldpayMerchantConfigDataMock.getCode()).thenReturn(MERCHANT_CODE);
        testObj.setConfiguredMerchants(Arrays.asList(worldpayMerchantConfigDataMock, anotherWorldpayMerchantConfigDataMock));
    }

    @Test
    public void shouldCreateMerchantInfoFromReplenishmentMerchantConfigData() {
        when(worldpayMerchantStrategyMock.getReplenishmentMerchant()).thenReturn(merchantConfigDataMock);
        doReturn(merchantInfo).when(testObj).createMerchantInfo(merchantConfigDataMock);

        final MerchantInfo result = testObj.getReplenishmentMerchant();

        assertEquals(merchantInfo, result);
    }

    @Test
    public void shouldCreateMerchantInfoFromWebSiteMerchantConfigData() {
        when(worldpayMerchantStrategyMock.getMerchant()).thenReturn(merchantConfigDataMock);
        doReturn(merchantInfo).when(testObj).createMerchantInfo(merchantConfigDataMock);

        final MerchantInfo result = testObj.getCurrentSiteMerchant();

        assertEquals(merchantInfo, result);
    }

    @Test
    public void getMerchantConfigShouldReturnAlreadyKnownMerchantCode() throws WorldpayConfigurationException {
        final MerchantInfo result = testObj.getMerchantInfoByCode(KNOWN_MERCHANT_CODE);

        assertEquals(KNOWN_MERCHANT_CODE, result.getMerchantCode());
    }

    @Test(expected = WorldpayConfigurationException.class)
    public void getKnownMerchantShouldReturnExceptionWhenMerchantIsNotFound() throws WorldpayConfigurationException {
        testObj.getMerchantInfoByCode("mumbo jumbo");
    }

    @Test
    public void testGetMerchantInfoFromTransaction() throws WorldpayConfigurationException {
        when(paymentTransactionMock.getRequestToken()).thenReturn(MERCHANT_CODE);

        final MerchantInfo result = testObj.getMerchantInfoFromTransaction(paymentTransactionMock);

        assertEquals(MERCHANT_CODE, result.getMerchantCode());
    }

    @Test
    public void shouldCreateMerchantInfoUsingMacValidation() {
        when(merchantConfigDataMock.getCode()).thenReturn(MERCHANT_CODE);
        when(merchantConfigDataMock.getPassword()).thenReturn(MERCHANT_PASSWORD);
        when(merchantConfigDataMock.getMacSecret()).thenReturn(MAC_SECRET);
        when(merchantConfigDataMock.getMacValidation()).thenReturn(true);

        final MerchantInfo result = testObj.createMerchantInfo(merchantConfigDataMock);

        assertEquals(MAC_SECRET, result.getMacSecret());
        assertEquals(MERCHANT_CODE, result.getMerchantCode());
        assertEquals(MERCHANT_PASSWORD, result.getMerchantPassword());
        assertEquals(true, result.isUsingMacValidation());
    }

    @Test
    public void shouldCreateMerchantInfoNotUsingMacValidation() {
        when(merchantConfigDataMock.getCode()).thenReturn(MERCHANT_CODE);
        when(merchantConfigDataMock.getPassword()).thenReturn(MERCHANT_PASSWORD);
        when(merchantConfigDataMock.getMacSecret()).thenReturn(MAC_SECRET);
        when(merchantConfigDataMock.getMacValidation()).thenReturn(false);

        final MerchantInfo result = testObj.createMerchantInfo(merchantConfigDataMock);

        assertEquals(MERCHANT_CODE, result.getMerchantCode());
        assertEquals(MERCHANT_PASSWORD, result.getMerchantPassword());
        assertNull(result.getMacSecret());
        assertEquals(false, result.isUsingMacValidation());
    }
}
