package com.worldpay.forms.validation;

import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.forms.B2BCSEPaymentForm;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static de.hybris.platform.assertions.BaseCommerceAssertions.assertThat;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class B2BCseFormValidatorTest {

    private static final String CSE_TOKEN = "cseToken";
    private static final String BIRTH_DAY_DATE_FORMAT = "dd/MM/yyyy";
    private static final String CHECKOUT_ERROR_FRAUDSIGHT_DOB_MANDATORY = "checkout.error.fraudSight.dob.mandatory";

    @InjectMocks
    private B2BCseFormValidator testObj = new B2BCseFormValidator();

    @Mock
    private Errors errorsMock;
    @Mock
    private B2BCSEPaymentForm csePaymentFormMock;
    @Mock
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacadeMock;

    @Before
    public void setUp() {
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);
        when(csePaymentFormMock.isDobRequired()).thenReturn(true);
    }

    @Test
    public void shouldNotRejectIfCseIsSet() throws Exception {
        when(csePaymentFormMock.getCseToken()).thenReturn(CSE_TOKEN);

        testObj.validate(csePaymentFormMock, errorsMock);

        verify(errorsMock, never()).reject(B2BCseFormValidator.GLOBAL_MISSING_CSE_TOKEN);
    }

    @Test
    public void shouldRejectIfCseIsNotSet() throws Exception {
        when(csePaymentFormMock.getCseToken()).thenReturn("");

        testObj.validate(csePaymentFormMock, errorsMock);

        verify(errorsMock).reject(B2BCseFormValidator.GLOBAL_MISSING_CSE_TOKEN);
    }

    @Test
    public void validate_WhenFSIsEnabledAndBirthdayDateNull_ShouldAddTheError() {
        when(csePaymentFormMock.getDateOfBirth()).thenReturn(null);

        testObj.validate(csePaymentFormMock, errorsMock);

        verify(errorsMock).reject(CHECKOUT_ERROR_FRAUDSIGHT_DOB_MANDATORY);
    }

    @Test
    public void validate_WhenFSIsEnabledAndBirthdayDateNotInThePast_ShouldAddTheError() throws ParseException {
        final SimpleDateFormat df = new SimpleDateFormat(BIRTH_DAY_DATE_FORMAT);
        when(csePaymentFormMock.getDateOfBirth()).thenReturn(df.parse("21/10/2100"));

        testObj.validate(csePaymentFormMock, errorsMock);

        verify(errorsMock).reject(CHECKOUT_ERROR_FRAUDSIGHT_DOB_MANDATORY);
    }

    @Test
    public void support_WhenClassIsB2BCSEPaymentForm_ShouldReturnTrue() {
        final boolean result = testObj.supports(B2BCSEPaymentForm.class);

        assertThat(result).isTrue();
    }

    @Test
    public void support_WhenClassIsNotB2BCSEPaymentForm_ShouldReturnFalse() {
        final boolean result = testObj.supports(String.class);

        assertThat(result).isFalse();
    }
}
