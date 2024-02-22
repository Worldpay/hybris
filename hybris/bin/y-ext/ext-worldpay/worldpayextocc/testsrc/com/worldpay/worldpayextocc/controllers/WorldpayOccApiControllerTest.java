package com.worldpay.worldpayextocc.controllers;

import com.worldpay.config.merchant.ThreeDSFlexJsonWebTokenCredentials;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.direct.WorldpayDDCFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.payment.DirectResponseData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.PrincipalData;
import de.hybris.platform.order.InvalidCartException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayOccApiControllerTest {

    private static final String CSE_PUBLIC_KEY = "CSE-public-key";
    private static final String DDC_URL_ATTR = "ddcUrl";
    private static final String DDC_JWT_ATTR = "jwt";
    private static final String HTTPS_WORLDPAY_COM_DDC = "https://worldpay.com/ddc";
    private static final String SOME_JWT = "some jwt";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String ORDER_CODE = "orderCode";
    private static final String USER_UID = "userUID";
    private static final String GUEST_CUSTOMER_BODY = "\"guestCustomer\":true";
    private static final String ORDER_CODE_BODY = "\"orderCode\":\"orderCode\"";
    private static final String ORDER_CODE_NULL_BODY = "\"orderCode\":\"\"";
    private static final String ACCEPTED_TRUE_BODY = "\"accepted\":true";
    private static final String ACCEPTED_FALSE_BODY = "\"accepted\":false";
    private static final String TRANSACTION_ID = "transactionId";
    private static final String RESPONSE = "response";


    @InjectMocks
    private WorldpayOccApiController testObj;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacadeMock;

    @Mock
    private ThreeDSFlexJsonWebTokenCredentials threeDsConfigMock;
    @Mock
    private DirectResponseData directResponseDataMock;
    @Mock
    private OrderData orderDataMock;
    @Mock
    private PrincipalData principalDataMock;

    @Mock
    private WorldpayDDCFacade worldpayDDCFacadeMock;
    @Mock
    private WorldpayDirectOrderFacade worldpayDirectOrderFacadeMock;

    private HttpServletResponse httpServletResponse = new MockHttpServletResponse();

    @Before
    public void setUp() throws InvalidCartException, WorldpayException {
        when(worldpayDirectOrderFacadeMock.executeSecondPaymentAuthorisation3DSecure(WORLDPAY_ORDER_CODE)).thenReturn(directResponseDataMock);
        when(directResponseDataMock.getOrderData()).thenReturn(orderDataMock);
        when(orderDataMock.getCode()).thenReturn(ORDER_CODE);
    }
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

    @Test
    public void handleChallengeSubmit_shouldReturnMessage_whenIsGuestUser() {
        when(orderDataMock.isGuestCustomer()).thenReturn(Boolean.TRUE);
        when(orderDataMock.getUser()).thenReturn(principalDataMock);
        when(principalDataMock.getUid()).thenReturn(USER_UID);

        final ResponseEntity<String> result = testObj.handleChallengeSubmit(TRANSACTION_ID, RESPONSE, WORLDPAY_ORDER_CODE, httpServletResponse);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).contains(GUEST_CUSTOMER_BODY);
        assertThat(result.getBody()).contains(ACCEPTED_TRUE_BODY);
        assertThat(result.getBody()).contains(ORDER_CODE_BODY);
    }

    @Test
    public void handleChallengeSubmit_shouldReturnMessage_whenIsRegisteredUser() {
        when(orderDataMock.isGuestCustomer()).thenReturn(Boolean.FALSE);

        final ResponseEntity<String> result = testObj.handleChallengeSubmit(TRANSACTION_ID, RESPONSE, WORLDPAY_ORDER_CODE, httpServletResponse);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).contains(ACCEPTED_TRUE_BODY);
        assertThat(result.getBody()).contains(ORDER_CODE_BODY);
    }

    @Test
    public void handleChallengeSubmit_shouldReturnMessage_whenWorldpayExceptionIsThrownAndRegisteredCustomer() throws InvalidCartException, WorldpayException {
        when(worldpayDirectOrderFacadeMock.executeSecondPaymentAuthorisation3DSecure(WORLDPAY_ORDER_CODE)).thenThrow(WorldpayException.class);

        final ResponseEntity<String> result = testObj.handleChallengeSubmit(TRANSACTION_ID, RESPONSE, WORLDPAY_ORDER_CODE, httpServletResponse);
        assertThat(result.getBody()).contains(ORDER_CODE_NULL_BODY);
        assertThat(result.getBody()).contains(ACCEPTED_FALSE_BODY);
    }

    @Test
    public void handleChallengeSubmit_shouldReturnMessage_whenCartIsNotFoundAndRegisteredCustomer() throws InvalidCartException, WorldpayException {
        when(worldpayDirectOrderFacadeMock.executeSecondPaymentAuthorisation3DSecure(WORLDPAY_ORDER_CODE)).thenThrow(InvalidCartException.class);

        final ResponseEntity<String> result = testObj.handleChallengeSubmit(TRANSACTION_ID, RESPONSE, WORLDPAY_ORDER_CODE, httpServletResponse);

        assertThat(result.getBody()).contains(ORDER_CODE_NULL_BODY);
        assertThat(result.getBody()).contains(ACCEPTED_FALSE_BODY);
    }
}
