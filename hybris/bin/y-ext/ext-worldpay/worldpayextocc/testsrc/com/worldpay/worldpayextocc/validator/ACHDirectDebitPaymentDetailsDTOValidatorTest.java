package com.worldpay.worldpayextocc.validator;

import com.worldpay.dto.payment.AchDirectDebitPaymentWsDTO;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ACHDirectDebitPaymentDetailsDTOValidatorTest {

    private static final String ACCOUNT_TYPE = "accountType";
    private static final String ACCOUNT_NUMBER = "12345678901234567";
    private static final String ROUTING_NUMBER = "123456789";
    private static final String CHECK_NUMBER = "123456789012345";
    private static final String CUSTOM_IDENTIFIER = "12345678901234";
    private static final String CUSTOM_IDENTIFIER_INVALID = "1234567890123456";

    @InjectMocks
    private ACHDirectDebitPaymentDetailsDTOValidator testObj;

    @Mock
    private Errors errorsMock;

    @Mock
    private AchDirectDebitPaymentWsDTO achDirectDebitPaymentWsDTOMock;

    @Test
    public void supports_whenClassIsAchDirectDebitPaymentWsDTO_shouldReturnTrue() {
        final boolean result = testObj.supports(achDirectDebitPaymentWsDTOMock.getClass());

        assertTrue(result);
    }

    @Test
    public void supports_whenClassIsAchDirectDebitPaymentWsDTO_shouldReturnFalse() {
        final boolean result = testObj.supports(errorsMock.getClass());

        assertFalse(result);
    }

    @Test
    public void validate_whenAchPaymentMethodSelectedAndAccountTypeIsBlank_shouldAddError() {
        when(achDirectDebitPaymentWsDTOMock.getAccountType()).thenReturn("");

        testObj.validate(achDirectDebitPaymentWsDTOMock, errorsMock);

        verify(errorsMock).rejectValue("accountType", "worldpay.achDirectDebit.accountType.invalid");
    }

    @Test
    public void validate_whenAchPaymentMethodSelectedAndAccountNumberIsBlank_shouldAddError() {
        when(achDirectDebitPaymentWsDTOMock.getAccountType()).thenReturn(ACCOUNT_TYPE);
        when(achDirectDebitPaymentWsDTOMock.getAccountNumber()).thenReturn("");

        testObj.validate(achDirectDebitPaymentWsDTOMock, errorsMock);

        verify(errorsMock).rejectValue("accountNumber", "worldpay.achDirectDebit.accountNumber.invalid");
    }

    @Test
    public void validate_whenAchPaymentMethodSelectedAndAccountNumberIsNotNumeric_shouldAddError() {


        when(achDirectDebitPaymentWsDTOMock.getAccountType()).thenReturn(ACCOUNT_TYPE);
        when(achDirectDebitPaymentWsDTOMock.getAccountNumber()).thenReturn("accountNumber");

        testObj.validate(achDirectDebitPaymentWsDTOMock, errorsMock);

        verify(errorsMock).rejectValue("accountNumber", "worldpay.achDirectDebit.accountNumber.invalid");
    }

    @Test
    public void validate_whenAchPaymentMethodSelectedAndAccountNumberIsTooLong_shouldAddError() {


        when(achDirectDebitPaymentWsDTOMock.getAccountType()).thenReturn(ACCOUNT_TYPE);
        when(achDirectDebitPaymentWsDTOMock.getAccountNumber()).thenReturn("123456789012345678");

        testObj.validate(achDirectDebitPaymentWsDTOMock, errorsMock);

        verify(errorsMock).rejectValue("accountNumber", "worldpay.achDirectDebit.accountNumber.invalid");
    }

    @Test
    public void validate_whenAchPaymentMethodSelectedAndRoutingNumberIsBlank_shouldAddError() {


        when(achDirectDebitPaymentWsDTOMock.getAccountType()).thenReturn(ACCOUNT_TYPE);
        when(achDirectDebitPaymentWsDTOMock.getAccountNumber()).thenReturn(ACCOUNT_NUMBER);
        when(achDirectDebitPaymentWsDTOMock.getRoutingNumber()).thenReturn("");

        testObj.validate(achDirectDebitPaymentWsDTOMock, errorsMock);

        verify(errorsMock).rejectValue("routingNumber", "worldpay.achDirectDebit.routingNumber.invalid");
    }

    @Test
    public void validate_whenAchPaymentMethodSelectedAndRoutingNumberIsNull_shouldAddError() {


        when(achDirectDebitPaymentWsDTOMock.getAccountType()).thenReturn(ACCOUNT_TYPE);
        when(achDirectDebitPaymentWsDTOMock.getAccountNumber()).thenReturn(ACCOUNT_NUMBER);
        when(achDirectDebitPaymentWsDTOMock.getRoutingNumber()).thenReturn(null);

        testObj.validate(achDirectDebitPaymentWsDTOMock, errorsMock);

        verify(errorsMock).rejectValue("routingNumber", "worldpay.achDirectDebit.routingNumber.invalid");
    }

    @Test
    public void validate_whenAchPaymentMethodSelectedAndRoutingNumberIsNotNumeric_shouldAddError() {


        when(achDirectDebitPaymentWsDTOMock.getAccountType()).thenReturn(ACCOUNT_TYPE);
        when(achDirectDebitPaymentWsDTOMock.getAccountNumber()).thenReturn(ACCOUNT_NUMBER);
        when(achDirectDebitPaymentWsDTOMock.getRoutingNumber()).thenReturn("routingNumber");

        testObj.validate(achDirectDebitPaymentWsDTOMock, errorsMock);

        verify(errorsMock).rejectValue("routingNumber", "worldpay.achDirectDebit.routingNumber.invalid");
    }

    @Test
    public void validate_whenAchPaymentMethodSelectedAndRoutingNumberIsTooLong_shouldAddError() {


        when(achDirectDebitPaymentWsDTOMock.getAccountType()).thenReturn(ACCOUNT_TYPE);
        when(achDirectDebitPaymentWsDTOMock.getAccountNumber()).thenReturn(ACCOUNT_NUMBER);
        when(achDirectDebitPaymentWsDTOMock.getRoutingNumber()).thenReturn("1234567890");

        testObj.validate(achDirectDebitPaymentWsDTOMock, errorsMock);

        verify(errorsMock).rejectValue("routingNumber", "worldpay.achDirectDebit.routingNumber.invalid");
    }

    @Test
    public void validate_whenAchPaymentMethodSelectedAndCheckNumberIsNotNumeric_shouldAddError() {


        when(achDirectDebitPaymentWsDTOMock.getAccountType()).thenReturn(ACCOUNT_TYPE);
        when(achDirectDebitPaymentWsDTOMock.getAccountNumber()).thenReturn(ACCOUNT_NUMBER);
        when(achDirectDebitPaymentWsDTOMock.getRoutingNumber()).thenReturn(ROUTING_NUMBER);
        when(achDirectDebitPaymentWsDTOMock.getCheckNumber()).thenReturn("checkNumber");

        testObj.validate(achDirectDebitPaymentWsDTOMock, errorsMock);

        verify(errorsMock).rejectValue("checkNumber", "worldpay.achDirectDebit.checkNumber.invalid");
    }

    @Test
    public void validate_whenAchPaymentMethodSelectedAndCheckNumberIsTooLong_shouldAddError() {


        when(achDirectDebitPaymentWsDTOMock.getAccountType()).thenReturn(ACCOUNT_TYPE);
        when(achDirectDebitPaymentWsDTOMock.getAccountNumber()).thenReturn(ACCOUNT_NUMBER);
        when(achDirectDebitPaymentWsDTOMock.getRoutingNumber()).thenReturn(ROUTING_NUMBER);
        when(achDirectDebitPaymentWsDTOMock.getCheckNumber()).thenReturn(CUSTOM_IDENTIFIER_INVALID);

        testObj.validate(achDirectDebitPaymentWsDTOMock, errorsMock);

        verify(errorsMock).rejectValue("checkNumber", "worldpay.achDirectDebit.checkNumber.invalid");
    }

    @Test
    public void validate_whenAchPaymentMethodSelectedAndCustomIdentifierIsTooLong_shouldAddError() {


        when(achDirectDebitPaymentWsDTOMock.getAccountType()).thenReturn(ACCOUNT_TYPE);
        when(achDirectDebitPaymentWsDTOMock.getAccountNumber()).thenReturn(ACCOUNT_NUMBER);
        when(achDirectDebitPaymentWsDTOMock.getRoutingNumber()).thenReturn(ROUTING_NUMBER);
        when(achDirectDebitPaymentWsDTOMock.getCheckNumber()).thenReturn(CHECK_NUMBER);
        when(achDirectDebitPaymentWsDTOMock.getCustomIdentifier()).thenReturn(CUSTOM_IDENTIFIER_INVALID);

        testObj.validate(achDirectDebitPaymentWsDTOMock, errorsMock);

        verify(errorsMock).rejectValue("customIdentifier", "worldpay.achDirectDebit.customIdentifier.invalid");
    }

    @Test
    public void validate_whenAchPaymentMethodSelectedAndCustomIdentifierIsNotTooLong_shouldNotAddError() {


        when(achDirectDebitPaymentWsDTOMock.getAccountType()).thenReturn(ACCOUNT_TYPE);
        when(achDirectDebitPaymentWsDTOMock.getAccountNumber()).thenReturn(ACCOUNT_NUMBER);
        when(achDirectDebitPaymentWsDTOMock.getRoutingNumber()).thenReturn(ROUTING_NUMBER);
        when(achDirectDebitPaymentWsDTOMock.getCheckNumber()).thenReturn(CHECK_NUMBER);
        when(achDirectDebitPaymentWsDTOMock.getCustomIdentifier()).thenReturn(CUSTOM_IDENTIFIER);

        testObj.validate(achDirectDebitPaymentWsDTOMock, errorsMock);

        verify(errorsMock, never()).rejectValue("customIdentifier", "worldpay.achDirectDebit.customIdentifier.invalid");
    }

    @Test
    public void validate_whenAchPaymentMethodSelectedAndCustomIdentifierIsNull_shouldNotAddError() {


        when(achDirectDebitPaymentWsDTOMock.getAccountType()).thenReturn(ACCOUNT_TYPE);
        when(achDirectDebitPaymentWsDTOMock.getAccountNumber()).thenReturn(ACCOUNT_NUMBER);
        when(achDirectDebitPaymentWsDTOMock.getRoutingNumber()).thenReturn(ROUTING_NUMBER);
        when(achDirectDebitPaymentWsDTOMock.getCheckNumber()).thenReturn(CHECK_NUMBER);
        when(achDirectDebitPaymentWsDTOMock.getCustomIdentifier()).thenReturn(CUSTOM_IDENTIFIER);

        testObj.validate(achDirectDebitPaymentWsDTOMock, errorsMock);

        verify(errorsMock, never()).rejectValue("customIdentifier", "worldpay.achDirectDebit.customIdentifier.invalid");
    }


}