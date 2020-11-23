package com.worldpay.worldpayextocc.controllers;

import com.worldpay.config.merchant.ThreeDSFlexJsonWebTokenCredentials;
import com.worldpay.facades.payment.direct.WorldpayDDCFacade;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayOccApiControllerTest {

    private static final String CSE_PUBLIC_KEY = "CSE-public-key";
    private static final String DDC_URL_ATTR = "ddcUrl";
    private static final String DDC_JWT_ATTR = "jwt";
    private static final String HTTPS_WORLDPAY_COM_DDC = "https://worldpay.com/ddc";
    private static final String SOME_JWT = "some jwt";

    @InjectMocks
    private WorldpayOccApiController testObj;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacadeMock;

    @Mock
    private ThreeDSFlexJsonWebTokenCredentials threeDsConfigMock;

    @Mock
    private WorldpayDDCFacade worldpayDDCFacadeMock;
    @Test
    public void getCsePublicKey_WhenItIsCalled_ReturnsTheCSEPublicKey() {
        when(worldpayMerchantConfigDataFacadeMock.getCurrentSiteMerchantConfigData().getCsePublicKey()).thenReturn(CSE_PUBLIC_KEY);

        final ResponseEntity<String> result = testObj.getCsePublicKey();

        assertThat(result).hasFieldOrPropertyWithValue("body", CSE_PUBLIC_KEY);
    }

    @Test
    public void getThreeDsDDCInfo_WhenItIsCalled_ReturnsConfigAndJwt() {
        when(threeDsConfigMock.getDdcUrl()).thenReturn(HTTPS_WORLDPAY_COM_DDC);
        when(worldpayMerchantConfigDataFacadeMock.getCurrentSiteMerchantConfigData().getThreeDSFlexJsonWebTokenSettings()).thenReturn(threeDsConfigMock);
        when(worldpayDDCFacadeMock.createJsonWebTokenForDDC()).thenReturn(SOME_JWT);

        final ResponseEntity<Map<String, String>> result = testObj.getThreeDsDDCInfo();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).hasSize(2);
        assertThat(result.getBody()).containsEntry(DDC_URL_ATTR, HTTPS_WORLDPAY_COM_DDC);
        assertThat(result.getBody()).containsEntry(DDC_JWT_ATTR, SOME_JWT);
    }
}
