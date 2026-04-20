package com.worldpay.service;

import static com.worldpay.service.request.AddBackOfficeCodeServiceRequest.createAddBackOfficeCodeRequest;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

import com.worldpay.data.MerchantInfo;
import com.worldpay.service.request.AddBackOfficeCodeServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;

@UnitTest
public class AddBackOfficeCodeServiceRequestTest {

    private static final String BACK_OFFICE_CODE = "BOC-1234567890";
    private static final String MERCHANT1ECOM = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";
    private static final String ORDER_CODE = "orderCode";

    private MerchantInfo merchantInfo;

    @Before
    public void setUp() throws Exception {
        merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantPassword(MERCHANT_PASSWORD);
        merchantInfo.setMerchantCode(MERCHANT1ECOM);
    }

    @Test
    public void createAddBackOfficeCodeRequestShouldContainBackOfficeCodeAndWorldpayConfigAndMerchantInfoAndOrderCode() {
        final AddBackOfficeCodeServiceRequest request = createAddBackOfficeCodeRequest(merchantInfo, ORDER_CODE, BACK_OFFICE_CODE);

        assertEquals(BACK_OFFICE_CODE, request.getBackOfficeCode());
        assertEquals(merchantInfo, request.getMerchantInfo());
        assertEquals(ORDER_CODE, request.getOrderCode());
    }

    @Test
    public void createAddBackOfficeCodeRequestShouldRaiseIllegalArgumentExceptionWhenMerchantInfoIsNull() {
        assertThatThrownBy(() -> createAddBackOfficeCodeRequest(null, ORDER_CODE, BACK_OFFICE_CODE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("WorldpayConfig, MerchantInfo, Order Code and Back Office Code cannot be null");
    }

    @Test
    public void createAddBackOfficeCodeRequestShouldRaiseIllegalArgumentExceptionWhenOrderCodeIsNull() {

        assertThatThrownBy(() -> createAddBackOfficeCodeRequest(merchantInfo, null, BACK_OFFICE_CODE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("WorldpayConfig, MerchantInfo, Order Code and Back Office Code cannot be null");
    }

    @Test
    public void createAddBackOfficeCodeRequestShouldRaiseIllegalArgumentExceptionWhenBackOfficeCodeIsNull() {
        assertThatThrownBy(() -> createAddBackOfficeCodeRequest(merchantInfo, ORDER_CODE, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("WorldpayConfig, MerchantInfo, Order Code and Back Office Code cannot be null");
    }
}
