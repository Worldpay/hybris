package com.worldpay.service;

import com.worldpay.data.MerchantInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.service.request.AuthorisationCodeServiceRequest;
import com.worldpay.service.response.AuthorisationCodeServiceResponse;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;

import static org.junit.Assert.*;

@IntegrationTest
public class AuthorisationCodeServiceRequestIntegrationTest extends ServicelayerBaseTest {

    private static final String AUTHORISATION_CODE = "AC-1234567890";
    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";
    private static final String orderCode = String.valueOf(new Date().getTime());

    private MerchantInfo merchantInfo;

    @Resource(name = "worldpayServiceGateway")
    private WorldpayServiceGateway gateway;

    @Before
    public void setUp() throws Exception {
        final MerchantInfo merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantPassword(MERCHANT_PASSWORD);
        merchantInfo.setMerchantCode(MERCHANT_CODE);
        this.merchantInfo = merchantInfo;
    }

    /**
     * Test method for {@link WorldpayServiceGateway#authorisationCode(AuthorisationCodeServiceRequest)}.
     */
    @Test
    public void testAuthorisationCode() throws WorldpayException {
        WPSGTestHelper.directAuthorise(gateway, merchantInfo, orderCode);

        final AuthorisationCodeServiceRequest request = AuthorisationCodeServiceRequest.createAuthorisationCodeRequest(merchantInfo, orderCode, AUTHORISATION_CODE);
        final AuthorisationCodeServiceResponse authorisationCode = gateway.authorisationCode(request);

        assertNotNull("Authorisation code response is null!", authorisationCode);
        assertFalse("Errors returned from authorisation code request", authorisationCode.isError());
        assertEquals("Order code returned is incorrect", orderCode, authorisationCode.getOrderCode());
        assertEquals("Authorisation code returned is incorrect", AUTHORISATION_CODE, authorisationCode.getAuthorisationCode());
    }
}
