package com.worldpay.forms.validation;

import java.text.ParseException;
import java.time.LocalDate;

import static de.hybris.platform.assertions.BaseCommerceAssertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.forms.B2BCSEPaymentForm;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.Errors;

@UnitTest
@ExtendWith(MockitoExtension.class)
class B2BCseFormValidatorTest {

    private static final String CSE_TOKEN = "cseToken";
    private static final String CHECKOUT_ERROR_FRAUDSIGHT_DOB_MANDATORY = "checkout.error.fraudSight.dob.mandatory";

    @InjectMocks
    private B2BCseFormValidator testObj = new B2BCseFormValidator();

    @Mock
    private Errors errorsMock;
    @Mock
    private B2BCSEPaymentForm csePaymentFormMock;
    @Mock
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacadeMock;

    @Test
    void validate_WhenCseIsSet_ShouldNotReject() throws Exception {
        when(csePaymentFormMock.getCseToken()).thenReturn(CSE_TOKEN);

        testObj.validate(csePaymentFormMock, errorsMock);

        verify(errorsMock, never()).reject(B2BCseFormValidator.GLOBAL_MISSING_CSE_TOKEN);
    }

    @Test
    void validate_WhenCseIsNotSet_ShouldReject() throws Exception {
        when(csePaymentFormMock.getCseToken()).thenReturn("");

        testObj.validate(csePaymentFormMock, errorsMock);

        verify(errorsMock).reject(B2BCseFormValidator.GLOBAL_MISSING_CSE_TOKEN);
    }

    @Test
    void validate_WhenFSisNotEnabled_ShouldNotAddDobError() {
        when(csePaymentFormMock.getCseToken()).thenReturn(CSE_TOKEN);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(false);

        testObj.validate(csePaymentFormMock, errorsMock);

        verify(errorsMock, never()).reject(CHECKOUT_ERROR_FRAUDSIGHT_DOB_MANDATORY);
    }

    @Test
    void validate_WhenFSisEnabledAndBirthdateIsNotRequired_ShouldNotAddDobError() {
        when(csePaymentFormMock.getCseToken()).thenReturn(CSE_TOKEN);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);
        when(csePaymentFormMock.isDobRequired()).thenReturn(false);

        testObj.validate(csePaymentFormMock, errorsMock);

        verify(errorsMock, never()).reject(CHECKOUT_ERROR_FRAUDSIGHT_DOB_MANDATORY);
    }

    @Test
    void validate_WhenFSisEnabledAndBirthdayDateNull_ShouldAddTheError() {
        when(csePaymentFormMock.getCseToken()).thenReturn(CSE_TOKEN);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);
        when(csePaymentFormMock.isDobRequired()).thenReturn(true);
        when(csePaymentFormMock.getDateOfBirth()).thenReturn(null);

        testObj.validate(csePaymentFormMock, errorsMock);

        verify(errorsMock).reject(CHECKOUT_ERROR_FRAUDSIGHT_DOB_MANDATORY);
    }

    @Test
    void validate_WhenFSisEnabledAndBirthdayDateIsInFuture_ShouldAddTheError() throws ParseException {
        when(csePaymentFormMock.getCseToken()).thenReturn(CSE_TOKEN);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);
        when(csePaymentFormMock.isDobRequired()).thenReturn(true);
        when(csePaymentFormMock.getDateOfBirth()).thenReturn(LocalDate.of(2100, 10, 21));

        testObj.validate(csePaymentFormMock, errorsMock);

        verify(errorsMock).reject(CHECKOUT_ERROR_FRAUDSIGHT_DOB_MANDATORY);
    }

    @Test
    void validate_WhenFSisEnabledAndBirthdateIsInThePast_ShouldNotAddTheError() {
        when(csePaymentFormMock.getCseToken()).thenReturn(CSE_TOKEN);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);
        when(csePaymentFormMock.isDobRequired()).thenReturn(true);
        when(csePaymentFormMock.getDateOfBirth()).thenReturn(LocalDate.of(1990, 5, 15));

        testObj.validate(csePaymentFormMock, errorsMock);

        verify(errorsMock, never()).reject(CHECKOUT_ERROR_FRAUDSIGHT_DOB_MANDATORY);
    }

    @Test
    void supports_WhenClassIsB2BCSEPaymentForm_ShouldReturnTrue() {
        final boolean result = testObj.supports(B2BCSEPaymentForm.class);

        assertThat(result).isTrue();
    }

    @Test
    void supports_WhenClassIsNotB2BCSEPaymentForm_ShouldReturnFalse() {
        final boolean result = testObj.supports(String.class);

        assertThat(result).isFalse();
    }
}
