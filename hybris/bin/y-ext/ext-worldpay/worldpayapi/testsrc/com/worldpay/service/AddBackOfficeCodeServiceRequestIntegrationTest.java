package com.worldpay.service;

import com.worldpay.data.MerchantInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.service.request.AddBackOfficeCodeServiceRequest;
import com.worldpay.service.response.AddBackOfficeCodeServiceResponse;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;

import static org.junit.Assert.*;

@IntegrationTest
public class AddBackOfficeCodeServiceRequestIntegrationTest extends ServicelayerBaseTest {

    private static final String BACK_OFFICE_CODE = "BOC-1234567890";
    private static final String ORDER_CODE = String.valueOf(new Date().getTime());
    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";

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

    @Test
    public void testAddBackOfficeCode() throws WorldpayException {

        WPSGTestHelper.directAuthorise(gateway, merchantInfo, ORDER_CODE);

        final AddBackOfficeCodeServiceRequest request = AddBackOfficeCodeServiceRequest.createAddBackOfficeCodeRequest(merchantInfo, ORDER_CODE, BACK_OFFICE_CODE);
        final AddBackOfficeCodeServiceResponse addBackOfficeCode = gateway.addBackOfficeCode(request);

        assertNotNull("Add back office code response is null!", addBackOfficeCode);
        assertFalse("Errors returned from add back office code request", addBackOfficeCode.isError());
        assertEquals("Order code returned is incorrect", ORDER_CODE, addBackOfficeCode.getOrderCode());
        assertEquals("Back office code returned is incorrect", BACK_OFFICE_CODE, addBackOfficeCode.getBackOfficeCode());
    }
}
