package com.worldpay.service;

import com.worldpay.exception.WorldpayException;
import com.worldpay.data.MerchantInfo;
import com.worldpay.service.request.AuthorisationCodeServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

@UnitTest
public class AuthorisationCodeServiceRequestTest {

    private static final String AUTHORISATION_CODE = "AC-1234567890";
    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";
    private static final String ORDER_CODE = "orderCode";

    private MerchantInfo merchantInfo;


    @SuppressWarnings("PMD.MemberScope")
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        final MerchantInfo merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantPassword(MERCHANT_PASSWORD);
        merchantInfo.setMerchantCode(MERCHANT_CODE);
        this.merchantInfo = merchantInfo;
    }

    @Test
    public void testAuthorisationCode() throws WorldpayException {

        final AuthorisationCodeServiceRequest request = AuthorisationCodeServiceRequest.createAuthorisationCodeRequest(merchantInfo, ORDER_CODE, AUTHORISATION_CODE);

        assertEquals(AUTHORISATION_CODE, request.getAuthorisationCode());
        assertEquals(merchantInfo, request.getMerchantInfo());
        assertEquals(ORDER_CODE, request.getOrderCode());
    }

    @Test
    public void authorisationCodeShouldRaiseIllegalArgumentExceptionWhenMerchantInfoIsNull() {
        expectedException.expect(IllegalArgumentException.class);

        AuthorisationCodeServiceRequest.createAuthorisationCodeRequest(null, ORDER_CODE, AUTHORISATION_CODE);
    }

    @Test
    public void authorisationCodeShouldRaiseIllegalArgumentExceptionWhenOrderCodeIsNull() {
        expectedException.expect(IllegalArgumentException.class);

        AuthorisationCodeServiceRequest.createAuthorisationCodeRequest(merchantInfo, null, AUTHORISATION_CODE);
    }

    @Test
    public void authorisationCodeShouldRaiseIllegalArgumentExceptionWhenAuthorizationCodeIsNull() {
        expectedException.expect(IllegalArgumentException.class);

        AuthorisationCodeServiceRequest.createAuthorisationCodeRequest(merchantInfo, ORDER_CODE, null);
    }
}
