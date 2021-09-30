package com.worldpay.service;

import com.worldpay.data.MerchantInfo;
import com.worldpay.service.request.AddBackOfficeCodeServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

@UnitTest
public class AddBackOfficeCodeServiceRequestTest {

    private static final String BACK_OFFICE_CODE = "BOC-1234567890";
    private static final String MERCHANT1ECOM = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";
    private static final String ORDER_CODE = "orderCode";

    private MerchantInfo merchantInfo;

    @Rule
    @SuppressWarnings("PMD.MemberScope")
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        final MerchantInfo merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantPassword(MERCHANT_PASSWORD);
        merchantInfo.setMerchantCode(MERCHANT1ECOM);
        this.merchantInfo = merchantInfo;
    }

    @Test
    public void createAddBackOfficeCodeRequestShouldContainBackOfficeCodeAndWorldpayConfigAndMerchantInfoAndOrderCode() {
        final AddBackOfficeCodeServiceRequest request = AddBackOfficeCodeServiceRequest.createAddBackOfficeCodeRequest(merchantInfo, ORDER_CODE, BACK_OFFICE_CODE);

        assertEquals(BACK_OFFICE_CODE, request.getBackOfficeCode());
        assertEquals(merchantInfo, request.getMerchantInfo());
        assertEquals(ORDER_CODE, request.getOrderCode());
    }

    @Test
    public void createAddBackOfficeCodeRequestShouldRaiseIllegalArgumentExceptionWhenMerchantInfoIsNull() {
        expectedException.expect(IllegalArgumentException.class);

        AddBackOfficeCodeServiceRequest.createAddBackOfficeCodeRequest(null, ORDER_CODE, BACK_OFFICE_CODE);
    }

    @Test
    public void createAddBackOfficeCodeRequestShouldRaiseIllegalArgumentExceptionWhenOrderCodeIsNull() {
        expectedException.expect(IllegalArgumentException.class);

        AddBackOfficeCodeServiceRequest.createAddBackOfficeCodeRequest(merchantInfo, null, BACK_OFFICE_CODE);
    }

    @Test
    public void createAddBackOfficeCodeRequestShouldRaiseIllegalArgumentExceptionWhenBackOfficeCodeIsNull() {
        expectedException.expect(IllegalArgumentException.class);

        AddBackOfficeCodeServiceRequest.createAddBackOfficeCodeRequest(merchantInfo, ORDER_CODE, null);
    }
}
