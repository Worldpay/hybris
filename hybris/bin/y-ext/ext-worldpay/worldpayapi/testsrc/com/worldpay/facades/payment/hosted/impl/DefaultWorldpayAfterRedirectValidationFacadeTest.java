package com.worldpay.facades.payment.hosted.impl;

import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.data.MerchantInfo;
import com.worldpay.service.payment.WorldpayAfterRedirectValidationService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.session.SessionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;

import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayAfterRedirectValidationFacadeTest {

    private static final String MERCHANT_CODE = "merchantCode";
    private static final String WORLDPAY_MERCHANT_CODE = "worldpayMerchantCode";
    private static final Map<String, String> WORLDPAY_RESPONSE = Map.of("responseKey", "responseValue");

    @InjectMocks
    private DefaultWorldpayAfterRedirectValidationFacade testObj;

    @Mock
    private WorldpayAfterRedirectValidationService worldpayAfterRedirectValidationServiceMock;
    @Mock
    private WorldpayMerchantInfoService worldpayMerchantInfoServiceMock;
    @Mock
    private SessionService sessionServiceMock;

    @Mock
    private MerchantInfo merchantInfoMock;

    @Before
    public void setUp() {
        when(sessionServiceMock.getAttribute(WORLDPAY_MERCHANT_CODE)).thenReturn(MERCHANT_CODE);
    }

    @Test
    public void validateRedirectResponse_ShouldReturnTrue_WhenRedirectResponseIsValid() throws WorldpayConfigurationException {
        when(worldpayMerchantInfoServiceMock.getMerchantInfoByCode(MERCHANT_CODE)).thenReturn(merchantInfoMock);
        when(worldpayAfterRedirectValidationServiceMock.validateRedirectResponse(eq(merchantInfoMock), eq(WORLDPAY_RESPONSE))).thenReturn(true);

        final boolean result = testObj.validateRedirectResponse(WORLDPAY_RESPONSE);

        assertTrue(result);
    }

    @Test
    public void validateRedirectResponse_ShouldReturnFalse_WhenRedirectResponseIsNotValid() {
        when(worldpayAfterRedirectValidationServiceMock.validateRedirectResponse(eq(merchantInfoMock), eq(WORLDPAY_RESPONSE))).thenReturn(false);

        final boolean result = testObj.validateRedirectResponse(WORLDPAY_RESPONSE);

        assertFalse(result);
    }

    @Test
    public void validateRedirectResponse_ShouldThrowAnException_WhenMerchantIsNotConfigured() throws WorldpayConfigurationException {
        when(worldpayMerchantInfoServiceMock.getMerchantInfoByCode(MERCHANT_CODE)).thenThrow(new WorldpayConfigurationException("No merchant found for code"));

        final boolean result = testObj.validateRedirectResponse(WORLDPAY_RESPONSE);

        assertFalse(result);
    }
}
