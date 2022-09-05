package com.worldpay.facades.payment.direct.impl;

import com.worldpay.config.merchant.ThreeDSFlexJsonWebTokenCredentials;
import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.service.payment.WorldpayJsonWebTokenService;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayDDCFacadeTest {

    private static final String JWT_VALUE = "JWT_VALUE";
    private static final String EVENT_ORIGIN_DOMAIN = "EVENT_ORIGIN_DOMAIN";

    @InjectMocks
    private DefaultWorldpayDDCFacade testObj;

    @Mock
    private WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacadeMock;
    @Mock
    private WorldpayJsonWebTokenService worldpayJsonWebTokenServiceMock;

    @Mock
    private WorldpayMerchantConfigData merchantConfigDataMock;
    @Mock
    private ThreeDSFlexJsonWebTokenCredentials threeDSJsonWebTokenCredentialsMock;

    @Test
    public void createJsonWebTokenForDDC_ShouldReturnAValidJsonWebToken() {
        when(worldpayMerchantConfigDataFacadeMock.getCurrentSiteMerchantConfigData()).thenReturn(merchantConfigDataMock);
        when(worldpayJsonWebTokenServiceMock.createJsonWebTokenForDDC(merchantConfigDataMock)).thenReturn(JWT_VALUE);

        final String result = testObj.createJsonWebTokenForDDC();

        assertThat(result).isEqualTo(JWT_VALUE);
    }

    @Test
    public void getOriginEventDomain() {
        when(worldpayMerchantConfigDataFacadeMock.getCurrentSiteMerchantConfigData()).thenReturn(merchantConfigDataMock);
        when(merchantConfigDataMock.getThreeDSFlexJsonWebTokenSettings()).thenReturn(threeDSJsonWebTokenCredentialsMock);
        when(threeDSJsonWebTokenCredentialsMock.getEventOriginDomain()).thenReturn(EVENT_ORIGIN_DOMAIN);

        final String result = testObj.getEventOriginDomainForDDC();

        assertThat(result).isEqualTo(EVENT_ORIGIN_DOMAIN);
    }

}
