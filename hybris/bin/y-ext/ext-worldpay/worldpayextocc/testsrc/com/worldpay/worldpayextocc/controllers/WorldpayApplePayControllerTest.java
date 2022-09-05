package com.worldpay.worldpayextocc.controllers;

import com.worldpay.data.ApplePayAdditionalAuthInfo;
import com.worldpay.data.ApplePayAuthorisationRequest;
import com.worldpay.data.ApplePayPaymentContact;
import com.worldpay.data.ApplePayPaymentRequest;
import com.worldpay.dto.order.PlaceOrderResponseWsDTO;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.WorldpayDirectResponseFacade;
import com.worldpay.facades.order.WorldpayApplePayPaymentCheckoutFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.applepay.ValidateMerchantRequestDTO;
import com.worldpay.payment.applepay.ValidateMerchantRequestData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayApplePayControllerTest {

    @InjectMocks
    public WorldpayApplePayController testObj;

    @Mock
    private WorldpayDirectOrderFacade worldpayDirectOrderFacadeMock;
    @Mock
    private WorldpayApplePayPaymentCheckoutFacade worldpayApplePayPaymentCheckoutFacadeMock;
    @Mock
    private RestTemplate restTemplateMock;
    @Mock
    private CheckoutFacade checkoutFacadeMock;
    @Mock
    private WorldpayDirectResponseFacade worldpayDirectResponseFacadeMock;

    @Mock
    private CartData cartMock;
    @Mock
    private ApplePayPaymentRequest paymentRequestMock;
    @Mock
    private ValidateMerchantRequestDTO validateMerchantRequestMock;
    @Mock
    private Object appleResponseMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ApplePayAuthorisationRequest authorisationRequestMock;
    @Mock
    private ApplePayPaymentContact billingContactMock;
    @Mock
    private ApplePayAdditionalAuthInfo paymentDataMock;
    @Mock
    private DirectResponseData authorisationResponseMock;
    @Mock
    private HttpServletResponse httpResponseMock;

    @Test
    public void getApplePayPaymentRequest_shouldPopulateResponse() {
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartMock);
        when(worldpayApplePayPaymentCheckoutFacadeMock.getApplePayPaymentRequest(cartMock))
            .thenReturn(paymentRequestMock);

        final ResponseEntity<ApplePayPaymentRequest> result = testObj.getPaymentRequest();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(paymentRequestMock, result.getBody());
    }

    @Test
    public void requestPaymentSession_shouldPostRequest() {
        final ValidateMerchantRequestData request = new ValidateMerchantRequestData();
        request.setValidationURL("https://e2ycommerce.com");

        when(worldpayApplePayPaymentCheckoutFacadeMock.getValidateMerchantRequestDTO())
            .thenReturn(validateMerchantRequestMock);
        when(restTemplateMock.postForObject("https://e2ycommerce.com", validateMerchantRequestMock, Object.class))
            .thenReturn(appleResponseMock);

        final Object result = testObj.requestPaymentSession(request);

        assertEquals(appleResponseMock, result);
    }

    @Test
    public void authoriseOrder_shouldSaveBillingAddressAndAuthorisePayment() throws WorldpayException, InvalidCartException {
        when(authorisationRequestMock.getBillingContact()).thenReturn(billingContactMock);
        when(authorisationRequestMock.getToken().getPaymentData()).thenReturn(paymentDataMock);

        when(worldpayDirectOrderFacadeMock.authoriseApplePayDirect(paymentDataMock))
            .thenReturn(authorisationResponseMock);

        testObj.authoriseOrder(authorisationRequestMock, FieldSetLevelHelper.DEFAULT_LEVEL, httpResponseMock);

        verify(worldpayApplePayPaymentCheckoutFacadeMock)
            .saveBillingAddresses(authorisationRequestMock.getBillingContact());

        verify(worldpayDirectResponseFacadeMock)
            .isAuthorised(authorisationResponseMock);
    }
}
