package com.worldpay.worldpayocccommons.validator;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class BooleanValidatorTest {

    @InjectMocks
    private BooleanValidator testObj;

    @Mock
    private Errors errors;

    private static final String FIELD_PATH = "active";
    private static final String ERROR_MESSAGE_ID = "error.boolean.mismatch";

    @Before
    public void setUp() {
        testObj.setBooleanValue("true");
        testObj.setFieldPath(FIELD_PATH);
        testObj.setErrorMessageID(ERROR_MESSAGE_ID);
    }

    @Test
    public void shouldNotRejectWhenBooleanMatches() {
        when(errors.getFieldValue(FIELD_PATH)).thenReturn(Boolean.TRUE);

        testObj.validate(new Object(), errors);

        verify(errors, never()).rejectValue(anyString(), anyString(), any(String[].class), any());
    }

    @Test
    public void shouldRejectWhenBooleanDoesNotMatch() {
        when(errors.getFieldValue(FIELD_PATH)).thenReturn(Boolean.FALSE);

        testObj.validate(new Object(), errors);

        verify(errors).rejectValue(eq(FIELD_PATH), eq(ERROR_MESSAGE_ID), any(String[].class), isNull());
    }

    @Test
    public void shouldRejectWhenFieldValueIsNotBoolean() {
        when(errors.getFieldValue(FIELD_PATH)).thenReturn("notABoolean");

        testObj.validate(new Object(), errors);

        verify(errors).rejectValue(eq(FIELD_PATH), eq(ERROR_MESSAGE_ID), any(String[].class), isNull());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenErrorsIsNull() {
        testObj.validate(new Object(), null);
    }

}
