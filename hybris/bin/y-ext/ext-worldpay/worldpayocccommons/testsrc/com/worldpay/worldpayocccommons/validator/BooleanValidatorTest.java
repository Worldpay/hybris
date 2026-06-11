package com.worldpay.worldpayocccommons.validator;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.Errors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@UnitTest
@ExtendWith(MockitoExtension.class) 
class BooleanValidatorTest {

    @InjectMocks
    private BooleanValidator testObj;

    @Mock
    private Errors errors;

    private static final String FIELD_PATH = "active";
    private static final String ERROR_MESSAGE_ID = "error.boolean.mismatch";

    @BeforeEach
    void setUp() {
        testObj = new BooleanValidator(Boolean.TRUE.toString(),FIELD_PATH,ERROR_MESSAGE_ID);
    }

    @Test
    void shouldNotRejectWhenBooleanMatches() {
        when(errors.getFieldValue(FIELD_PATH)).thenReturn(Boolean.TRUE);

        testObj.validate(new Object(), errors);

        verify(errors, never()).rejectValue(anyString(), anyString(), any(String[].class), any());
    }

    @Test
    void shouldRejectWhenBooleanDoesNotMatch() {
        when(errors.getFieldValue(FIELD_PATH)).thenReturn(Boolean.FALSE);

        testObj.validate(new Object(), errors);

        verify(errors).rejectValue(eq(FIELD_PATH), eq(ERROR_MESSAGE_ID), any(String[].class), isNull());
    }

    @Test
    void shouldRejectWhenFieldValueIsNotBoolean() {
        when(errors.getFieldValue(FIELD_PATH)).thenReturn("notABoolean");

        testObj.validate(new Object(), errors);

        verify(errors).rejectValue(eq(FIELD_PATH), eq(ERROR_MESSAGE_ID), any(String[].class), isNull());
    }

    @Test
    void shouldThrowExceptionWhenErrorsIsNull() {
        final Object obj = new Object(); // optional, just to be explicit
        assertThrows(IllegalArgumentException.class, () -> testObj.validate(obj, null));
    }

}
