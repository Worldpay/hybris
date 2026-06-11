package com.worldpay.forms.validation;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.forms.CSEPaymentForm;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.Errors;

@UnitTest
@ExtendWith(MockitoExtension.class)
class CseFormValidatorTest {

    private static final String CSE_TOKEN = "cseToken";
    private static final String CHECKOUT_ERROR_FRAUDSIGHT_DOB_MANDATORY = "checkout.error.fraudSight.dob.mandatory";

    @InjectMocks
    private CseFormValidator testObj = new CseFormValidator();

    @Mock
    private Errors errorsMock;
    @Mock
    private CSEPaymentForm csePaymentFormMock;
    @Mock
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacadeMock;

    @Test
    void supports_WhenClassIsCsePaymentForm_ShouldReturnTrue() {
        final boolean result = testObj.supports(CSEPaymentForm.class);

        assertTrue(result);
    }

    @Test
    void supports_WhenClassIsNotB2BCsePaymentForm_ShouldReturnFalse() {
        final boolean result = testObj.supports(String.class);

        assertFalse(result);
    }

    @Test
    void validate_ShouldNotRejectIfCseIsSet() {
        when(csePaymentFormMock.getCseToken()).thenReturn(CSE_TOKEN);

        testObj.validate(csePaymentFormMock, errorsMock);

        verify(errorsMock, never()).reject(CseFormValidator.GLOBAL_MISSING_CSE_TOKEN);
    }

    @Test
    void validate_ShouldRejectIfCseIsNotSet() {
        when(csePaymentFormMock.getCseToken()).thenReturn("");

        testObj.validate(csePaymentFormMock, errorsMock);

        verify(errorsMock).reject(CseFormValidator.GLOBAL_MISSING_CSE_TOKEN);
    }

    @Test
    void validate_ShouldNotRejectIfTermsIsChecked() {
        when(csePaymentFormMock.isTermsCheck()).thenReturn(true);

        testObj.validate(csePaymentFormMock, errorsMock);

        verify(errorsMock, never()).reject(CseFormValidator.CHECKOUT_ERROR_TERMS_NOT_ACCEPTED);
    }

    @Test
    void validate_ShouldRejectIfTermsIsNotChecked() {
        when(csePaymentFormMock.isTermsCheck()).thenReturn(false);

        testObj.validate(csePaymentFormMock, errorsMock);

        verify(errorsMock).reject(CseFormValidator.CHECKOUT_ERROR_TERMS_NOT_ACCEPTED);
    }

    @Test
    void validate_WhenFsIsDisabledAndBirthdayDateNull_ShouldNotAddTheError() {
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(false);

        testObj.validate(csePaymentFormMock, errorsMock);

        verify(errorsMock, never()).reject(CHECKOUT_ERROR_FRAUDSIGHT_DOB_MANDATORY);
    }

    @Test
    void validate_WhenFsIsEnabledAndBirthdayDateNullAndDobNotRequired_ShouldNotAddTheError() {
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);
        when(csePaymentFormMock.isDobRequired()).thenReturn(false);

        testObj.validate(csePaymentFormMock, errorsMock);

        verify(errorsMock, never()).reject(CHECKOUT_ERROR_FRAUDSIGHT_DOB_MANDATORY);
    }

    @Test
    void validate_WhenFsIsEnabledAndBirthdayDateNull_ShouldAddTheError() {
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);
        when(csePaymentFormMock.isDobRequired()).thenReturn(true);

        when(csePaymentFormMock.getDateOfBirth()).thenReturn(null);

        testObj.validate(csePaymentFormMock, errorsMock);

        verify(errorsMock).reject(CHECKOUT_ERROR_FRAUDSIGHT_DOB_MANDATORY);
    }

    @Test
    void validate_WhenFsIsEnabledAndBirthdayDateNotInThePast_ShouldAddTheError() {
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);
        when(csePaymentFormMock.isDobRequired()).thenReturn(true);

        when(csePaymentFormMock.getDateOfBirth()).thenReturn(LocalDate.of(2100, 10, 21));

        testObj.validate(csePaymentFormMock, errorsMock);

        verify(errorsMock).reject(CHECKOUT_ERROR_FRAUDSIGHT_DOB_MANDATORY);
    }

    @Test
    void validate_WhenFsIsEnabledAndBirthdayDateIsInThePast_ShouldNotAddTheError() {
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);
        when(csePaymentFormMock.isDobRequired()).thenReturn(true);
        when(csePaymentFormMock.getDateOfBirth()).thenReturn(LocalDate.now().minusYears(18));

        testObj.validate(csePaymentFormMock, errorsMock);

        verify(errorsMock, never()).reject(CHECKOUT_ERROR_FRAUDSIGHT_DOB_MANDATORY);
    }
}
