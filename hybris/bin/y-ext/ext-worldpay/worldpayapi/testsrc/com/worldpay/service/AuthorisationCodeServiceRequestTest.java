package com.worldpay.service;

import static com.worldpay.service.request.AuthorisationCodeServiceRequest.createAuthorisationCodeRequest;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.worldpay.data.MerchantInfo;
import com.worldpay.service.request.AuthorisationCodeServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@UnitTest
public class AuthorisationCodeServiceRequestTest {

    private static final String AUTHORISATION_CODE = "AC-1234567890";
    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";
    private static final String ORDER_CODE = "orderCode";

    private MerchantInfo merchantInfo;

    @BeforeEach
    public void setUp() throws Exception {
        merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantPassword(MERCHANT_PASSWORD);
        merchantInfo.setMerchantCode(MERCHANT_CODE);
    }

    @Test
    public void testAuthorisationCode() {

        final AuthorisationCodeServiceRequest request = createAuthorisationCodeRequest(merchantInfo, ORDER_CODE, AUTHORISATION_CODE);

        assertEquals(AUTHORISATION_CODE, request.getAuthorisationCode());
        assertEquals(merchantInfo, request.getMerchantInfo());
        assertEquals(ORDER_CODE, request.getOrderCode());
    }

    @Test
    public void authorisationCodeShouldRaiseIllegalArgumentExceptionWhenMerchantInfoIsNull() {
        assertThatThrownBy(() -> createAuthorisationCodeRequest(null, ORDER_CODE, AUTHORISATION_CODE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("WorldpayConfig, MerchantInfo, Order Code and Authorisation Code cannot be null");
    }

    @Test
    public void authorisationCodeShouldRaiseIllegalArgumentExceptionWhenOrderCodeIsNull() {
        assertThatThrownBy(() -> createAuthorisationCodeRequest(merchantInfo, null, AUTHORISATION_CODE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("WorldpayConfig, MerchantInfo, Order Code and Authorisation Code cannot be null");
    }

    @Test
    public void authorisationCodeShouldRaiseIllegalArgumentExceptionWhenAuthorizationCodeIsNull() {
        assertThatThrownBy(() -> createAuthorisationCodeRequest(merchantInfo, ORDER_CODE, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("WorldpayConfig, MerchantInfo, Order Code and Authorisation Code cannot be null");
    }
}
