package com.worldpay.support.appender.impl;

import com.worldpay.merchant.configuration.services.WorldpayMerchantConfigurationService;
import com.worldpay.model.WorldpayMerchantConfigurationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayMerchantConfigurationAppenderTest {

    private static final Boolean MAC_VALIDATION = Boolean.TRUE;
    private static final String MERCHANT_CODE = "merchantCode";
    private static final String INSTALLATION_ID = "installationId";
    private static final String PAYMENT_METHOD_1 = "paymentMethod1";
    private static final String PAYMENT_METHOD_2 = "paymentMethod2";
    private static final String PAYMENT_METHOD_3 = "paymentMethod3";
    private static final String PAYMENT_METHOD_4 = "paymentMethod4";
    private static final String WEB_MERCHANT_ID = "webMerchantId";
    private static final String MOBILE_MERCHANT_ID = "mobileMerchantId";

    @Spy
    @InjectMocks
    private WorldpayMerchantConfigurationAppender testObj;

    @Mock
    private WorldpayMerchantConfigurationService worldpayMerchantConfigurationServiceMock;
    @Mock
    private WorldpayMerchantConfigurationModel webWorldpayMerchantConfigurationMock, mobileWorldpayMerchantConfigurationMock;

    @Test
    public void appendContent_ShouldAppendMerchantConfiguration() {
        when(worldpayMerchantConfigurationServiceMock.getAllSystemActiveSiteMerchantConfigurations()).thenReturn(Set.of(webWorldpayMerchantConfigurationMock, mobileWorldpayMerchantConfigurationMock));
        final List<String> includedPaymentMethods = asList(PAYMENT_METHOD_1, PAYMENT_METHOD_2);
        final List<String> excludedPaymentMethods = asList(PAYMENT_METHOD_3, PAYMENT_METHOD_4);

        when(webWorldpayMerchantConfigurationMock.getIncludedPaymentTypes()).thenReturn(includedPaymentMethods);
        when(webWorldpayMerchantConfigurationMock.getExcludedPaymentTypes()).thenReturn(excludedPaymentMethods);
        when(webWorldpayMerchantConfigurationMock.getMacValidation()).thenReturn(MAC_VALIDATION);
        when(webWorldpayMerchantConfigurationMock.getCode()).thenReturn(MERCHANT_CODE);
        when(webWorldpayMerchantConfigurationMock.getIdentifier()).thenReturn(WEB_MERCHANT_ID);
        when(webWorldpayMerchantConfigurationMock.getInstallationId()).thenReturn(INSTALLATION_ID);

        when(mobileWorldpayMerchantConfigurationMock.getIncludedPaymentTypes()).thenReturn(includedPaymentMethods);
        when(mobileWorldpayMerchantConfigurationMock.getExcludedPaymentTypes()).thenReturn(excludedPaymentMethods);
        when(mobileWorldpayMerchantConfigurationMock.getMacValidation()).thenReturn(MAC_VALIDATION);
        when(mobileWorldpayMerchantConfigurationMock.getCode()).thenReturn(MERCHANT_CODE);
        when(mobileWorldpayMerchantConfigurationMock.getIdentifier()).thenReturn(MOBILE_MERCHANT_ID);
        when(mobileWorldpayMerchantConfigurationMock.getInstallationId()).thenReturn(INSTALLATION_ID);

        final String result = testObj.appendContent();

        assertTrue(result.contains("Merchant Configuration"));
        assertTrue(result.contains(WEB_MERCHANT_ID));
        assertTrue(result.contains(MOBILE_MERCHANT_ID));

        assertEquals(2, StringUtils.countMatches(result, MERCHANT_CODE));
        assertEquals(2, StringUtils.countMatches(result, INSTALLATION_ID));
        assertEquals(2, StringUtils.countMatches(result, String.valueOf(MAC_VALIDATION)));

        assertTrue(result.contains("Included payment methods"));
        assertEquals(2, StringUtils.countMatches(result, PAYMENT_METHOD_1));
        assertEquals(2, StringUtils.countMatches(result, PAYMENT_METHOD_2));

        assertTrue(result.contains("Excluded payment methods"));
        assertEquals(2, StringUtils.countMatches(result, PAYMENT_METHOD_3));
        assertEquals(2, StringUtils.countMatches(result, PAYMENT_METHOD_4));
    }
}

