package com.worldpay.service;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.exception.WorldpayException;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.request.AuthorisationCodeServiceRequest;
import com.worldpay.service.response.AuthorisationCodeServiceResponse;
import de.hybris.bootstrap.annotations.IntegrationTest;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

@IntegrationTest
public class AuthorisationCodeServiceRequestIntegrationTest {

    private static final String AUTHORISATION_CODE = "AC-1234567890";
    private static final WorldpayConfig WORLD_PAY_CONFIG = WorldpayTestConfigHelper.getWorldpayTestConfig();
    private static final WorldpayServiceGateway gateway = WorldpayServiceGateway.getInstance();
    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";
    private static final MerchantInfo merchantInfo = new MerchantInfo(MERCHANT_CODE, MERCHANT_PASSWORD);
    private static final String orderCode = String.valueOf(new Date().getTime());

    /**
     * Test method for {@link WorldpayServiceGateway#authorisationCode(AuthorisationCodeServiceRequest)}.
     */
    @Test
    public void testAuthorisationCode() throws WorldpayException {
        WPSGTestHelper.directAuthorise(gateway, WORLD_PAY_CONFIG, merchantInfo, orderCode);

        final AuthorisationCodeServiceRequest request = AuthorisationCodeServiceRequest.createAuthorisationCodeRequest(WORLD_PAY_CONFIG, merchantInfo, orderCode, AUTHORISATION_CODE);
        final AuthorisationCodeServiceResponse authorisationCode = gateway.authorisationCode(request);

        assertNotNull("Authorisation code response is null!", authorisationCode);
        assertFalse("Errors returned from authorisation code request", authorisationCode.isError());
        assertEquals("Order code returned is incorrect", orderCode, authorisationCode.getOrderCode());
        assertEquals("Authorisation code returned is incorrect", AUTHORISATION_CODE, authorisationCode.getAuthorisationCode());
    }
}
