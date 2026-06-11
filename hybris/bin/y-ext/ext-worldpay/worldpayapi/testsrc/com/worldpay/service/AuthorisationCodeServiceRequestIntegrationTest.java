package com.worldpay.service;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.worldpay.data.MerchantInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.service.request.AuthorisationCodeServiceRequest;
import com.worldpay.service.response.AuthorisationCodeServiceResponse;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerJUnit5BaseTest;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@IntegrationTest
class AuthorisationCodeServiceRequestIntegrationTest extends ServicelayerJUnit5BaseTest {

    private static final String AUTHORISATION_CODE = "AC-1234567890";
    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";
    private static final String ORDER_CODE = String.valueOf(Instant.now().toEpochMilli());

    private MerchantInfo merchantInfo;

    @Resource(name = "worldpayServiceGateway")
    private WorldpayServiceGateway gateway;

    @BeforeEach
    void setUp() {
        this.merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantPassword(MERCHANT_PASSWORD);
        merchantInfo.setMerchantCode(MERCHANT_CODE);
    }

    /**
     * Test method for {@link WorldpayServiceGateway#authorisationCode(AuthorisationCodeServiceRequest)}.
     */
    @Test
    void testAuthorisationCode() throws WorldpayException {
        WPSGTestHelper.directAuthorise(gateway, merchantInfo, ORDER_CODE);

        final AuthorisationCodeServiceRequest request = AuthorisationCodeServiceRequest.createAuthorisationCodeRequest(merchantInfo, ORDER_CODE, AUTHORISATION_CODE);
        final AuthorisationCodeServiceResponse authorisationCode = gateway.authorisationCode(request);

        assertNotNull(authorisationCode, "Authorisation code response is null!");
        assertFalse(authorisationCode.isError(), "Errors returned from authorisation code request");
        assertEquals(ORDER_CODE, authorisationCode.getOrderCode(), "Order code returned is incorrect");
        assertEquals(AUTHORISATION_CODE, authorisationCode.getAuthorisationCode(), "Authorisation code returned is incorrect");
    }
}
