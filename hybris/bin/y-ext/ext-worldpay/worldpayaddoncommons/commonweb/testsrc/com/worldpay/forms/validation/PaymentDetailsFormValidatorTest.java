package com.worldpay.forms.validation;

import java.time.LocalDate;

import static com.worldpay.service.model.payment.PaymentType.ONLINE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.worldpay.core.services.APMConfigurationLookupService;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.forms.ACHForm;
import com.worldpay.forms.PaymentDetailsForm;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.service.apm.APMAvailabilityService;
import com.worldpay.service.model.payment.PaymentType;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;
import org.springframework.validation.Errors;

@UnitTest
@ExtendWith(MockitoExtension.class)
class PaymentDetailsFormValidatorTest {

    private static final String BANK_CODE = "bankCode";
    private static final String ACCOUNT_TYPE = "accountType";
    private static final String ROUTING_NUMBER = "123456789";
    private static final String CHECK_NUMBER = "123456789012345";
    private static final String ACCOUNT_NUMBER = "12345678901234567";
    private static final String CUSTOM_IDENTIFIER = "12345678901234";
    private static final String APM_PAYMENT_METHOD = "apmPaymentMethod";
    private static final String CUSTOM_IDENTIFIER_INVALID = "1234567890123456";

    private static final String GLOBAL_MISSING_DELIVERY_ADDRESS = "checkout.multi.paymentMethod.createSubscription.billingAddress.noneSelectedMsg";
    private static final String CHECKOUT_ERROR_TERMS_NOT_ACCEPTED = "checkout.error.terms.not.accepted";
    private static final String CHECKOUT_ERROR_FRAUDSIGHT_DOB_MANDATORY = "checkout.error.fraudSight.dob.mandatory";

    private static final String FIELD_PAYMENT_METHOD = "paymentMethod";
    private static final String FIELD_SHOPPER_BANK_CODE = "shopperBankCode";
    private static final String FIELD_BILLING_ADDRESS_FIRST_NAME = "billingAddress.firstName";
    private static final String FIELD_BILLING_ADDRESS_LAST_NAME = "billingAddress.lastName";
    private static final String FIELD_BILLING_ADDRESS_LINE1 = "billingAddress.line1";
    private static final String FIELD_BILLING_ADDRESS_TOWN_CITY = "billingAddress.townCity";
    private static final String FIELD_BILLING_ADDRESS_POSTCODE = "billingAddress.postcode";
    private static final String FIELD_BILLING_ADDRESS_COUNTRY_ISO = "billingAddress.countryIso";
    private static final String WORLDPAY_PAYMENT_METHOD_INVALID = "worldpay.paymentMethod.invalid";
    private static final String WORLDPAY_PAYMENT_METHOD_NO_SHOPPER_BANK_CODE = "worldpay.paymentMethod.noShopperBankCode";

    @Spy
    @InjectMocks
    private PaymentDetailsFormValidator testObj;

    @Mock
    private PaymentDetailsForm paymentDetailsFormMock;
    @Mock
    private ACHForm achFormMock;
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

    @Test
    void whenCheckTermsAreNotCheckedAddError() {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(checkoutCartMock);
        when(checkoutCartMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(true);
        when(paymentDetailsFormMock.isDobRequired()).thenReturn(true);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);

        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(false);

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock).reject(CHECKOUT_ERROR_TERMS_NOT_ACCEPTED);
    }

    @Test
    void validateChecksBillingAddressWhenShippingNotSameAsBilling() {
        doNothing().when(testObj).validateField(any(), anyString(), anyString());
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(true);
        when(paymentDetailsFormMock.isDobRequired()).thenReturn(true);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);

        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(false);

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verifyBillingAddressValidation(times(1));
    }

    @Test
    void validateBillingAddressNotValidatedWhenShippingSameAsBilling() {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(checkoutCartMock);
        when(checkoutCartMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(true);
        when(paymentDetailsFormMock.isDobRequired()).thenReturn(true);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verifyBillingAddressValidation(never());
        verify(checkoutCartMock).getDeliveryAddress();
    }

    @Test
    void validateDoesNotCheckApmAvailabilityWhenPaymentMethodValidationFails() {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(checkoutCartMock);
        when(checkoutCartMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(true);
        when(paymentDetailsFormMock.isDobRequired()).thenReturn(true);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);

        when(errorsMock.hasErrors()).thenReturn(true);

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(apmConfigurationLookupServiceMock, never()).getAPMConfigurationForCode(anyString());
        verify(apmAvailabilityServiceMock, never()).isAvailable(any(WorldpayAPMConfigurationModel.class), any(CartModel.class));
        verify(errorsMock, never()).rejectValue(eq(FIELD_PAYMENT_METHOD), anyString(), anyString());
    }

    @Test
    void validateDoesNotCheckApmAvailabilityWhenPaymentMethodIsONLINE() {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(checkoutCartMock);
        when(checkoutCartMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(true);
        when(paymentDetailsFormMock.isDobRequired()).thenReturn(true);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);

        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(ONLINE.getMethodCode());

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(apmConfigurationLookupServiceMock, never()).getAPMConfigurationForCode(anyString());
        verify(apmAvailabilityServiceMock, never()).isAvailable(any(WorldpayAPMConfigurationModel.class), any(CartModel.class));
        verify(errorsMock, never()).rejectValue(eq(FIELD_PAYMENT_METHOD), anyString(), anyString());
    }

    @Test
    void validateChecksApmAvailabilityWhenPaymentMethodIsNotCreditCardAndNoErrorsOnPaymentMethodValidation() {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(checkoutCartMock);
        when(checkoutCartMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(true);
        when(paymentDetailsFormMock.isDobRequired()).thenReturn(true);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);

        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(APM_PAYMENT_METHOD);
        when(apmConfigurationLookupServiceMock.getAPMConfigurationForCode(APM_PAYMENT_METHOD)).thenReturn(apmConfigurationMock);
        when(cartServiceMock.getSessionCart()).thenReturn(cartMock);

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(apmConfigurationLookupServiceMock).getAPMConfigurationForCode(APM_PAYMENT_METHOD);
        verify(apmAvailabilityServiceMock).isAvailable(apmConfigurationMock, cartMock);
        verify(errorsMock).rejectValue(eq(FIELD_PAYMENT_METHOD), anyString(), anyString());
    }

    @Test
    void validateRejectsFieldIfShopperBankCodeIsNotSelectedWhenIsBankApm() {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(checkoutCartMock);
        when(checkoutCartMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(true);
        when(paymentDetailsFormMock.isDobRequired()).thenReturn(true);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);

        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(APM_PAYMENT_METHOD);
        when(cartServiceMock.getSessionCart()).thenReturn(cartMock);
        when(apmConfigurationLookupServiceMock.getAPMConfigurationForCode(APM_PAYMENT_METHOD)).thenReturn(apmConfigurationMock);
        when(apmAvailabilityServiceMock.isAvailable(apmConfigurationMock, cartMock)).thenReturn(true);
        when(apmConfigurationMock.getBank()).thenReturn(true);

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock).reject(eq(WORLDPAY_PAYMENT_METHOD_NO_SHOPPER_BANK_CODE), anyString());
    }

    @Test
    void validateDoesNotRaiseErrorWhenShopperBankSelectedForBankApm() {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(checkoutCartMock);
        when(checkoutCartMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(true);
        when(paymentDetailsFormMock.isDobRequired()).thenReturn(true);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);

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
    void validateAddErrorWhenUseBillingAsAddressAndCartDoesNotHaveDeliveryAddress() {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(checkoutCartMock);
        when(checkoutCartMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(true);
        when(paymentDetailsFormMock.isDobRequired()).thenReturn(true);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);

        when(checkoutCartMock.getDeliveryAddress()).thenReturn(null);

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock).reject(GLOBAL_MISSING_DELIVERY_ADDRESS);
    }

    @Test
    void validateAddGlobalErrorWhenNoPaymentMethodIsSelectedIsEmpty() {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(checkoutCartMock);
        when(checkoutCartMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(true);
        when(paymentDetailsFormMock.isDobRequired()).thenReturn(true);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);

        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn("");

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock).reject(WORLDPAY_PAYMENT_METHOD_INVALID);
    }

    @Test
    void validateAddGlobalErrorWhenNoPaymentMethodIsSelectedAndValueIsNull() {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(checkoutCartMock);
        when(checkoutCartMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(true);
        when(paymentDetailsFormMock.isDobRequired()).thenReturn(true);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);

        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(null);

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock).reject(WORLDPAY_PAYMENT_METHOD_INVALID);
    }

    @Test
    void validate_WhenFSIsEnabledAndBirthdayDateNull_ShouldAddTheError() {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(checkoutCartMock);
        when(checkoutCartMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(true);
        when(paymentDetailsFormMock.isDobRequired()).thenReturn(true);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);

        when(paymentDetailsFormMock.getDateOfBirth()).thenReturn(null);

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock).reject(CHECKOUT_ERROR_FRAUDSIGHT_DOB_MANDATORY);
    }

    @Test
    void validate_WhenFSIsEnabledAndBirthdayDateNotInThePast_ShouldAddTheError() {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(checkoutCartMock);
        when(checkoutCartMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(true);
        when(paymentDetailsFormMock.isDobRequired()).thenReturn(true);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);

        when(paymentDetailsFormMock.getDateOfBirth()).thenReturn(LocalDate.of(2100, 10, 21));

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock).reject(CHECKOUT_ERROR_FRAUDSIGHT_DOB_MANDATORY);
    }

    @Test
    void validate_WhenFSIsDisabledAndBirthdayDateNull_ShouldNotAddTheError() {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(checkoutCartMock);
        when(checkoutCartMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(true);

        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(false);

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock, never()).reject(CHECKOUT_ERROR_FRAUDSIGHT_DOB_MANDATORY);
    }

    @Test
    void validate_WhenFSIsEnabledAndBirthdayDateNullAndDobNotRequired_ShouldNotAddTheError() {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(checkoutCartMock);
        when(checkoutCartMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(true);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);

        when(paymentDetailsFormMock.isDobRequired()).thenReturn(false);

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock, never()).reject(CHECKOUT_ERROR_FRAUDSIGHT_DOB_MANDATORY);
    }

    @Test
    void validate_whenAchPaymentMethodSelectedAndAchFormIsNull_shouldAddError() {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(checkoutCartMock);
        when(checkoutCartMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(true);
        when(paymentDetailsFormMock.isDobRequired()).thenReturn(true);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);

        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(PaymentType.ACHDIRECTDEBITSSL.getMethodCode());
        when(paymentDetailsFormMock.getAchForm()).thenReturn(null);

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock).rejectValue("achForm", "worldpay.achForm.invalid");
    }

    @Test
    void validate_whenAchPaymentMethodSelectedAndAccountTypeIsBlank_shouldAddError() {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(checkoutCartMock);
        when(checkoutCartMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(true);
        when(paymentDetailsFormMock.isDobRequired()).thenReturn(true);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);

        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(PaymentType.ACHDIRECTDEBITSSL.getMethodCode());
        when(paymentDetailsFormMock.getAchForm()).thenReturn(achFormMock);
        when(paymentDetailsFormMock.getAchForm().getAccountType()).thenReturn("");

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock).rejectValue("achForm.accountType", "worldpay.achForm.accountType.invalid");
    }

    @Test
    void validate_whenAchPaymentMethodSelectedAndAccountNumberIsBlank_shouldAddError() {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(checkoutCartMock);
        when(checkoutCartMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(true);
        when(paymentDetailsFormMock.isDobRequired()).thenReturn(true);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);

        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(PaymentType.ACHDIRECTDEBITSSL.getMethodCode());
        when(paymentDetailsFormMock.getAchForm()).thenReturn(achFormMock);
        when(paymentDetailsFormMock.getAchForm().getAccountType()).thenReturn(ACCOUNT_TYPE);
        when(paymentDetailsFormMock.getAchForm().getAccountNumber()).thenReturn("");

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock).rejectValue("achForm.accountNumber", "worldpay.achForm.accountNumber.invalid");
    }

    @Test
    void validate_whenAchPaymentMethodSelectedAndAccountNumberIsNotNumeric_shouldAddError() {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(checkoutCartMock);
        when(checkoutCartMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(true);
        when(paymentDetailsFormMock.isDobRequired()).thenReturn(true);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);

        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(PaymentType.ACHDIRECTDEBITSSL.getMethodCode());
        when(paymentDetailsFormMock.getAchForm()).thenReturn(achFormMock);
        when(paymentDetailsFormMock.getAchForm().getAccountType()).thenReturn(ACCOUNT_TYPE);
        when(paymentDetailsFormMock.getAchForm().getAccountNumber()).thenReturn("accountNumber");

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock).rejectValue("achForm.accountNumber", "worldpay.achForm.accountNumber.invalid");
    }

    @Test
    void validate_whenAchPaymentMethodSelectedAndAccountNumberIsTooLong_shouldAddError() {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(checkoutCartMock);
        when(checkoutCartMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(true);
        when(paymentDetailsFormMock.isDobRequired()).thenReturn(true);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);

        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(PaymentType.ACHDIRECTDEBITSSL.getMethodCode());
        when(paymentDetailsFormMock.getAchForm()).thenReturn(achFormMock);
        when(paymentDetailsFormMock.getAchForm().getAccountType()).thenReturn(ACCOUNT_TYPE);
        when(paymentDetailsFormMock.getAchForm().getAccountNumber()).thenReturn("123456789012345678");

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock).rejectValue("achForm.accountNumber", "worldpay.achForm.accountNumber.invalid");
    }

    @Test
    void validate_whenAchPaymentMethodSelectedAndRoutingNumberIsBlank_shouldAddError() {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(checkoutCartMock);
        when(checkoutCartMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(true);
        when(paymentDetailsFormMock.isDobRequired()).thenReturn(true);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);

        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(PaymentType.ACHDIRECTDEBITSSL.getMethodCode());
        when(paymentDetailsFormMock.getAchForm()).thenReturn(achFormMock);
        when(paymentDetailsFormMock.getAchForm().getAccountType()).thenReturn(ACCOUNT_TYPE);
        when(paymentDetailsFormMock.getAchForm().getAccountNumber()).thenReturn(ACCOUNT_NUMBER);
        when(paymentDetailsFormMock.getAchForm().getRoutingNumber()).thenReturn("");

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock).rejectValue("achForm.routingNumber", "worldpay.achForm.routingNumber.invalid");
    }

    @Test
    void validate_whenAchPaymentMethodSelectedAndRoutingNumberIsNull_shouldAddError() {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(checkoutCartMock);
        when(checkoutCartMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(true);
        when(paymentDetailsFormMock.isDobRequired()).thenReturn(true);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);

        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(PaymentType.ACHDIRECTDEBITSSL.getMethodCode());
        when(paymentDetailsFormMock.getAchForm()).thenReturn(achFormMock);
        when(paymentDetailsFormMock.getAchForm().getAccountType()).thenReturn(ACCOUNT_TYPE);
        when(paymentDetailsFormMock.getAchForm().getAccountNumber()).thenReturn(ACCOUNT_NUMBER);
        when(paymentDetailsFormMock.getAchForm().getRoutingNumber()).thenReturn(null);

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock).rejectValue("achForm.routingNumber", "worldpay.achForm.routingNumber.invalid");
    }

    @Test
    void validate_whenAchPaymentMethodSelectedAndRoutingNumberIsNotNumeric_shouldAddError() {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(checkoutCartMock);
        when(checkoutCartMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(true);
        when(paymentDetailsFormMock.isDobRequired()).thenReturn(true);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);

        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(PaymentType.ACHDIRECTDEBITSSL.getMethodCode());
        when(paymentDetailsFormMock.getAchForm()).thenReturn(achFormMock);
        when(paymentDetailsFormMock.getAchForm().getAccountType()).thenReturn(ACCOUNT_TYPE);
        when(paymentDetailsFormMock.getAchForm().getAccountNumber()).thenReturn(ACCOUNT_NUMBER);
        when(paymentDetailsFormMock.getAchForm().getRoutingNumber()).thenReturn("routingNumber");

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock).rejectValue("achForm.routingNumber", "worldpay.achForm.routingNumber.invalid");
    }

    @Test
    void validate_whenAchPaymentMethodSelectedAndRoutingNumberIsTooLong_shouldAddError() {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(checkoutCartMock);
        when(checkoutCartMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(true);
        when(paymentDetailsFormMock.isDobRequired()).thenReturn(true);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);

        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(PaymentType.ACHDIRECTDEBITSSL.getMethodCode());
        when(paymentDetailsFormMock.getAchForm()).thenReturn(achFormMock);
        when(paymentDetailsFormMock.getAchForm().getAccountType()).thenReturn(ACCOUNT_TYPE);
        when(paymentDetailsFormMock.getAchForm().getAccountNumber()).thenReturn(ACCOUNT_NUMBER);
        when(paymentDetailsFormMock.getAchForm().getRoutingNumber()).thenReturn("1234567890");

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock).rejectValue("achForm.routingNumber", "worldpay.achForm.routingNumber.invalid");
    }

    @Test
    void validate_whenAchPaymentMethodSelectedAndCheckNumberIsNotNumeric_shouldAddError() {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(checkoutCartMock);
        when(checkoutCartMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(true);
        when(paymentDetailsFormMock.isDobRequired()).thenReturn(true);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);

        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(PaymentType.ACHDIRECTDEBITSSL.getMethodCode());
        when(paymentDetailsFormMock.getAchForm()).thenReturn(achFormMock);
        when(paymentDetailsFormMock.getAchForm().getAccountType()).thenReturn(ACCOUNT_TYPE);
        when(paymentDetailsFormMock.getAchForm().getAccountNumber()).thenReturn(ACCOUNT_NUMBER);
        when(paymentDetailsFormMock.getAchForm().getRoutingNumber()).thenReturn(ROUTING_NUMBER);
        when(paymentDetailsFormMock.getAchForm().getCheckNumber()).thenReturn("checkNumber");

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock).rejectValue("achForm.checkNumber", "worldpay.achForm.checkNumber.invalid");
    }

    @Test
    void validate_whenAchPaymentMethodSelectedAndCheckNumberIsTooLong_shouldAddError() {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(checkoutCartMock);
        when(checkoutCartMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(true);
        when(paymentDetailsFormMock.isDobRequired()).thenReturn(true);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);

        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(PaymentType.ACHDIRECTDEBITSSL.getMethodCode());
        when(paymentDetailsFormMock.getAchForm()).thenReturn(achFormMock);
        when(paymentDetailsFormMock.getAchForm().getAccountType()).thenReturn(ACCOUNT_TYPE);
        when(paymentDetailsFormMock.getAchForm().getAccountNumber()).thenReturn(ACCOUNT_NUMBER);
        when(paymentDetailsFormMock.getAchForm().getRoutingNumber()).thenReturn(ROUTING_NUMBER);
        when(paymentDetailsFormMock.getAchForm().getCheckNumber()).thenReturn(CUSTOM_IDENTIFIER_INVALID);

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock).rejectValue("achForm.checkNumber", "worldpay.achForm.checkNumber.invalid");
    }

    @Test
    void validate_whenAchPaymentMethodSelectedAndCustomIdentifierIsTooLong_shouldAddError() {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(checkoutCartMock);
        when(checkoutCartMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(true);
        when(paymentDetailsFormMock.isDobRequired()).thenReturn(true);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);

        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(PaymentType.ACHDIRECTDEBITSSL.getMethodCode());
        when(paymentDetailsFormMock.getAchForm()).thenReturn(achFormMock);
        when(paymentDetailsFormMock.getAchForm().getAccountType()).thenReturn(ACCOUNT_TYPE);
        when(paymentDetailsFormMock.getAchForm().getAccountNumber()).thenReturn(ACCOUNT_NUMBER);
        when(paymentDetailsFormMock.getAchForm().getRoutingNumber()).thenReturn(ROUTING_NUMBER);
        when(paymentDetailsFormMock.getAchForm().getCheckNumber()).thenReturn(CHECK_NUMBER);
        when(paymentDetailsFormMock.getAchForm().getCustomIdentifier()).thenReturn(CUSTOM_IDENTIFIER_INVALID);

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock).rejectValue("achForm.customIdentifier", "worldpay.achForm.customIdentifier.invalid");
    }

    @Test
    void validate_whenAchPaymentMethodSelectedAndCustomIdentifierIsNotTooLong_shouldNotAddError() {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(checkoutCartMock);
        when(checkoutCartMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(true);
        when(paymentDetailsFormMock.isDobRequired()).thenReturn(true);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);

        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(PaymentType.ACHDIRECTDEBITSSL.getMethodCode());
        when(paymentDetailsFormMock.getAchForm()).thenReturn(achFormMock);
        when(paymentDetailsFormMock.getAchForm().getAccountType()).thenReturn(ACCOUNT_TYPE);
        when(paymentDetailsFormMock.getAchForm().getAccountNumber()).thenReturn(ACCOUNT_NUMBER);
        when(paymentDetailsFormMock.getAchForm().getRoutingNumber()).thenReturn(ROUTING_NUMBER);
        when(paymentDetailsFormMock.getAchForm().getCheckNumber()).thenReturn(CHECK_NUMBER);
        when(paymentDetailsFormMock.getAchForm().getCustomIdentifier()).thenReturn(CUSTOM_IDENTIFIER);

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock, never()).rejectValue("achForm.customIdentifier", "worldpay.achForm.customIdentifier.invalid");
    }

    @Test
    void validate_whenAchPaymentMethodSelectedAndCustomIdentifierIsNull_shouldNotAddError() {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(checkoutCartMock);
        when(checkoutCartMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(paymentDetailsFormMock.isTermsCheck()).thenReturn(true);
        when(paymentDetailsFormMock.isDobRequired()).thenReturn(true);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);

        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(PaymentType.ACHDIRECTDEBITSSL.getMethodCode());
        when(paymentDetailsFormMock.getAchForm()).thenReturn(achFormMock);
        when(paymentDetailsFormMock.getAchForm().getAccountType()).thenReturn(ACCOUNT_TYPE);
        when(paymentDetailsFormMock.getAchForm().getAccountNumber()).thenReturn(ACCOUNT_NUMBER);
        when(paymentDetailsFormMock.getAchForm().getRoutingNumber()).thenReturn(ROUTING_NUMBER);
        when(paymentDetailsFormMock.getAchForm().getCheckNumber()).thenReturn(CHECK_NUMBER);
        when(paymentDetailsFormMock.getAchForm().getCustomIdentifier()).thenReturn(CUSTOM_IDENTIFIER);

        testObj.validate(paymentDetailsFormMock, errorsMock);

        verify(errorsMock, never()).rejectValue("achForm.customIdentifier", "worldpay.achForm.customIdentifier.invalid");
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
