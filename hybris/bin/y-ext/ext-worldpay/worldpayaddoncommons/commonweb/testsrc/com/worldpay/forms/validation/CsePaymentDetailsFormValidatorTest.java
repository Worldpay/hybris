package com.worldpay.forms.validation;

import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.forms.PaymentDetailsForm;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import static com.worldpay.forms.validation.PaymentDetailsFormValidator.CHECKOUT_ERROR_TERMS_NOT_ACCEPTED;
import static com.worldpay.service.model.payment.PaymentType.ONLINE;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CsePaymentDetailsFormValidatorTest {

    private static final String SOME_OTHER_METHOD_OF_PAYMENT = "someOtherMethodOfPayment";

    @InjectMocks
    private CsePaymentDetailsFormValidator testObj;

    @Mock
    private PaymentDetailsForm paymentDetailsFormMock;
    @Mock
    private Errors errorsMock;
    @Mock
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacadeMock;

    @Before
    public void setUp() {
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(false);
        lenient().when(paymentDetailsFormMock.isDobRequired()).thenReturn(false);
    }

    @Test
    public void shouldNotAddErrorsOnTermsAndConditionsIfPaymentMethodIsOnline() throws Exception {
        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(ONLINE.getMethodCode());
        lenient().when(paymentDetailsFormMock.isTermsCheck()).thenReturn(false);

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock, never()).reject(CHECKOUT_ERROR_TERMS_NOT_ACCEPTED);
    }

    @Test
    public void shouldAddErrorsOnTermsAndConditionsIfPaymentMethodIsAPM() throws Exception {
        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(SOME_OTHER_METHOD_OF_PAYMENT);
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(false);
        when(errorsMock.hasErrors()).thenReturn(true);

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock).reject(CHECKOUT_ERROR_TERMS_NOT_ACCEPTED);
    }

    @Test
    public void shouldNotAddErrorsOnTermsAndConditionsIfTermsAndConditionIsCheckedAndPaymentMethodIsAPM() throws Exception {
        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(SOME_OTHER_METHOD_OF_PAYMENT);
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(true);
        when(errorsMock.hasErrors()).thenReturn(true);

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock, never()).reject(CHECKOUT_ERROR_TERMS_NOT_ACCEPTED);
    }
}
