package com.worldpay.forms.validation;

import com.worldpay.core.services.APMConfigurationLookupService;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.forms.PaymentDetailsForm;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.service.apm.APMAvailabilityService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.verification.VerificationMode;
import org.springframework.validation.Errors;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static com.worldpay.forms.validation.PaymentDetailsFormValidator.*;
import static com.worldpay.service.model.payment.PaymentType.ONLINE;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PaymentDetailsFormValidatorTest {

    private static final String APM_PAYMENT_METHOD = "apmPaymentMethod";
    private static final String BANK_CODE = "bankCode";
    private static final String BIRTH_DAY_DATE_FORMAT = "dd/MM/yyyy";

    @Spy
    @InjectMocks
    private PaymentDetailsFormValidator testObj;

    @Mock
    private PaymentDetailsForm paymentDetailsFormMock;
    @Mock
    private Errors errorsMock;
    @Mock
    private APMAvailabilityService apmAvailabilityServiceMock;
    @Mock
    private APMConfigurationLookupService apmConfigurationLookupServiceMock;
    @Mock
    private CartService cartServiceMock;
    @Mock
    private WorldpayAPMConfigurationModel apmConfigurationMock;
    @Mock
    private CartModel cartMock;
    @Mock
    private AcceleratorCheckoutFacade checkoutFacadeMock;
    @Mock
    private AddressData deliveryAddressMock;
    @Mock
    private CartData checkoutCartMock;
    @Mock
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacadeMock;

    @Before
    public void setUp() {
        doNothing().when(testObj).validateField(any(Errors.class), anyString(), anyString());
        doNothing().when(testObj).validateField(any(Errors.class), anyString(), anyString(), anyString());
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(checkoutCartMock);
        when(checkoutCartMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(true);
        when(paymentDetailsFormMock.isDobRequired()).thenReturn(true);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);
    }

    @Test
    public void whenCheckTermsAreNotCheckedAddError() {
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(false);

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock).reject(CHECKOUT_ERROR_TERMS_NOT_ACCEPTED);
    }

    @Test
    public void validateChecksBillingAddressWhenShippingNotSameAsBilling() {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(false);

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verifyBillingAddressValidation(times(1));
    }

    @Test
    public void validateBillingAddressNotValidatedWhenShippingSameAsBilling() {

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verifyBillingAddressValidation(never());
        verify(checkoutCartMock).getDeliveryAddress();
    }

    @Test
    public void validateDoesNotCheckApmAvailabilityWhenPaymentMethodValidationFails() {
        when(errorsMock.hasErrors()).thenReturn(true);

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(apmConfigurationLookupServiceMock, never()).getAPMConfigurationForCode(anyString());
        verify(apmAvailabilityServiceMock, never()).isAvailable(any(WorldpayAPMConfigurationModel.class), any(CartModel.class));
        verify(errorsMock, never()).rejectValue(eq(FIELD_PAYMENT_METHOD), anyString(), anyString());
    }

    @Test
    public void validateDoesNotCheckApmAvailabilityWhenPaymentMethodIsONLINE() {
        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(ONLINE.getMethodCode());

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(apmConfigurationLookupServiceMock, never()).getAPMConfigurationForCode(anyString());
        verify(apmAvailabilityServiceMock, never()).isAvailable(any(WorldpayAPMConfigurationModel.class), any(CartModel.class));
        verify(errorsMock, never()).rejectValue(eq(FIELD_PAYMENT_METHOD), anyString(), anyString());
    }

    @Test
    public void validateChecksApmAvailabilityWhenPaymentMethodIsNotCreditCardAndNoErrorsOnPaymentMethodValidation() {
        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(APM_PAYMENT_METHOD);
        when(apmConfigurationLookupServiceMock.getAPMConfigurationForCode(APM_PAYMENT_METHOD)).thenReturn(apmConfigurationMock);
        when(cartServiceMock.getSessionCart()).thenReturn(cartMock);

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(apmConfigurationLookupServiceMock).getAPMConfigurationForCode(APM_PAYMENT_METHOD);
        verify(apmAvailabilityServiceMock).isAvailable(apmConfigurationMock, cartMock);
        verify(errorsMock).rejectValue(eq(FIELD_PAYMENT_METHOD), anyString(), anyString());
    }

    @Test
    public void validateRejectsFieldIfShopperBankCodeIsNotSelectedWhenIsBankApm() {
        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(APM_PAYMENT_METHOD);
        when(cartServiceMock.getSessionCart()).thenReturn(cartMock);
        when(apmConfigurationLookupServiceMock.getAPMConfigurationForCode(APM_PAYMENT_METHOD)).thenReturn(apmConfigurationMock);
        when(apmAvailabilityServiceMock.isAvailable(apmConfigurationMock, cartMock)).thenReturn(true);
        when(apmConfigurationMock.getBank()).thenReturn(true);

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock).reject(eq(WORLDPAY_PAYMENT_METHOD_NO_SHOPPER_BANK_CODE), anyString());
    }

    @Test
    public void validateDoesNotRaiseErrorWhenShopperBankSelectedForBankApm() {
        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(APM_PAYMENT_METHOD);
        when(paymentDetailsFormMock.getShopperBankCode()).thenReturn(BANK_CODE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartMock);
        when(apmConfigurationLookupServiceMock.getAPMConfigurationForCode(APM_PAYMENT_METHOD)).thenReturn(apmConfigurationMock);
        when(apmAvailabilityServiceMock.isAvailable(apmConfigurationMock, cartMock)).thenReturn(true);
        when(apmConfigurationMock.getBank()).thenReturn(true);

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock, never()).rejectValue(eq(FIELD_SHOPPER_BANK_CODE), anyString(), anyString());
    }


    @Test
    public void validateAddErrorWhenUseBillingAsAddressAndCartDoesNotHaveDeliveryAddress() {
        when(checkoutFacadeMock.getCheckoutCart().getDeliveryAddress()).thenReturn(null);

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock).reject(GLOBAL_MISSING_DELIVERY_ADDRESS);
    }

    @Test
    public void validateAddGlobalErrorWhenNoPaymentMethodIsSelectedIsEmpty() {
        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn("");

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock).reject(WORLDPAY_PAYMENT_METHOD_INVALID);
    }

    @Test
    public void validateAddGlobalErrorWhenNoPaymentMethodIsSelectedAndValueIsNull() {
        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(null);

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock).reject(WORLDPAY_PAYMENT_METHOD_INVALID);
    }

    @Test
    public void validate_WhenFSIsEnabledAndBirthdayDateNull_ShouldAddTheError() {
        when(paymentDetailsFormMock.getDateOfBirth()).thenReturn(null);

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock).reject(CHECKOUT_ERROR_FRAUDSIGHT_DOB_MANDATORY);
    }

    @Test
    public void validate_WhenFSIsEnabledAndBirthdayDateNotInThePast_ShouldAddTheError() throws ParseException {
        final SimpleDateFormat df = new SimpleDateFormat(BIRTH_DAY_DATE_FORMAT);
        when(paymentDetailsFormMock.getDateOfBirth()).thenReturn(df.parse("21/10/2100"));

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock).reject(CHECKOUT_ERROR_FRAUDSIGHT_DOB_MANDATORY);
    }

    @Test
    public void validate_WhenFSIsDisabledAndBirthdayDateNull_ShouldNotAddTheError() {
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(false);
        when(paymentDetailsFormMock.getDateOfBirth()).thenReturn(null);

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock, never()).reject(CHECKOUT_ERROR_FRAUDSIGHT_DOB_MANDATORY);
    }

    @Test
    public void validate_WhenFSIsEnabledAndBirthdayDateNullAndDobNotRequired_ShouldNotAddTheError() {
        when(paymentDetailsFormMock.isDobRequired()).thenReturn(false);
        when(paymentDetailsFormMock.getDateOfBirth()).thenReturn(null);

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock, never()).reject(CHECKOUT_ERROR_FRAUDSIGHT_DOB_MANDATORY);
    }

    protected void verifyBillingAddressValidation(VerificationMode verificationMode) {
        verify(testObj, verificationMode).validateField(eq(errorsMock), eq(FIELD_BILLING_ADDRESS_FIRST_NAME), anyString());
        verify(testObj, verificationMode).validateField(eq(errorsMock), eq(FIELD_BILLING_ADDRESS_LAST_NAME), anyString());
        verify(testObj, verificationMode).validateField(eq(errorsMock), eq(FIELD_BILLING_ADDRESS_LINE1), anyString());
        verify(testObj, verificationMode).validateField(eq(errorsMock), eq(FIELD_BILLING_ADDRESS_TOWN_CITY), anyString());
        verify(testObj, verificationMode).validateField(eq(errorsMock), eq(FIELD_BILLING_ADDRESS_POSTCODE), anyString());
        verify(testObj, verificationMode).validateField(eq(errorsMock), eq(FIELD_BILLING_ADDRESS_COUNTRY_ISO), anyString());
    }
}
