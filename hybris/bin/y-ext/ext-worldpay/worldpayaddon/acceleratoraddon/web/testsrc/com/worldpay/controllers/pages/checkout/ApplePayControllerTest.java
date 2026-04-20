package com.worldpay.controllers.pages.checkout;

import com.worldpay.data.ApplePayAdditionalAuthInfo;
import com.worldpay.data.ApplePayAuthorisationRequest;
import com.worldpay.data.ApplePayPaymentContact;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.order.WorldpayApplePayPaymentCheckoutFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.payment.applepay.ValidateMerchantRequestDTO;
import com.worldpay.payment.applepay.ValidateMerchantRequestData;
import com.worldpay.service.model.payment.PaymentType;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.session.SessionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ApplePayControllerTest {
    @Spy
    @InjectMocks
    private ApplePayController testObj;
    @Mock
    private RestTemplate restTemplateMock;
    @Mock
    private SessionService sessionServiceMock;
    @Mock
    private WorldpayApplePayPaymentCheckoutFacade worldpayApplePayPaymentCheckoutFacadeMock;
    @Mock
    private ValidateMerchantRequestData validateMerchantRequestDataMock;
    @Mock
    private ValidateMerchantRequestDTO validateMerchantRequestDTOMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ApplePayAuthorisationRequest authorisationRequestMock;
    @Mock
    private ApplePayPaymentContact billingContactMock;
    @Mock
    private WorldpayDirectOrderFacade worldpayDirectOrderFacadeMock;
    @Mock
    private ApplePayAdditionalAuthInfo paymentDataMock;

    @Test
    public void testAuthoriseOrder() throws WorldpayException, InvalidCartException {
        when(authorisationRequestMock.getBillingContact()).thenReturn(billingContactMock);
        when(authorisationRequestMock.getToken().getPaymentData()).thenReturn(paymentDataMock);
        doNothing().when(worldpayApplePayPaymentCheckoutFacadeMock).saveBillingAddresses(billingContactMock);

        testObj.authoriseOrder(authorisationRequestMock);

        verify(worldpayApplePayPaymentCheckoutFacadeMock).saveBillingAddresses(billingContactMock);
        verify(worldpayDirectOrderFacadeMock).authoriseApplePayDirect(paymentDataMock);
        verify(sessionServiceMock).setAttribute("paymentMethod", PaymentType.APPLEPAYSSL.getMethodCode());
    }

    @Test
    public void testRequestPaymentSession() {
        when(validateMerchantRequestDataMock.getValidationURL()).thenReturn("http://apple.com");

        when(worldpayApplePayPaymentCheckoutFacadeMock.getValidateMerchantRequestDTO()).thenReturn(validateMerchantRequestDTOMock);
        testObj.requestPaymentSession(validateMerchantRequestDataMock);

        verify(worldpayApplePayPaymentCheckoutFacadeMock).getValidateMerchantRequestDTO();
        verify(restTemplateMock).postForObject(validateMerchantRequestDataMock.getValidationURL(), validateMerchantRequestDTOMock, Object.class);
    }
}
