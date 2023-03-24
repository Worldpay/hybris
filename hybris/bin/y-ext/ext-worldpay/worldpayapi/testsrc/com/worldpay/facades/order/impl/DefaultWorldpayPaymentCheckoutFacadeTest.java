package com.worldpay.facades.order.impl;

import com.worldpay.core.checkout.WorldpayCheckoutService;
import com.worldpay.service.payment.WorldpayFraudSightStrategy;
import com.worldpay.service.payment.WorldpayGuaranteedPaymentsStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayPaymentCheckoutFacadeTest {

    private static final String ADDRESS_DATA_ID = "1234";
    public static final String CART_CODE = "cartCode";
    public static final String CUSTOMER_ID = "customerID";

    @InjectMocks
    private DefaultWorldpayPaymentCheckoutFacade testObj;

    @Mock
    private WorldpayCheckoutService worldpayCheckoutServiceMock;
    @Mock
    private CheckoutFacade checkoutFacade;
    @Mock
    private CartService cartServiceMock;
    @Mock
    private CustomerAccountService customerAccountServiceMock;
    @Mock
    private WorldpayFraudSightStrategy worldpayFraudSightStrategyMock;
    @Mock
    private WorldpayGuaranteedPaymentsStrategy worldpayGuaranteedPaymentsStrategy;
    @Mock
    private CustomerFacade customerFacade;

    @Mock
    private AddressData addressDataMock;
    @Mock
    private CartModel cartModelMock;
    @Mock
    private AddressModel addressModelMock;
    @Mock
    private AddressModel paymentAddressMock;
    @Mock
    private CustomerModel customerModelMock;
    @Mock
    private CustomerData customerDataMock;

    @Before
    public void setUp() {
        when(checkoutFacade.hasCheckoutCart()).thenReturn(true);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(cartModelMock.getUser()).thenReturn(customerModelMock);
        when(customerAccountServiceMock.getAddressForCode(customerModelMock, ADDRESS_DATA_ID)).thenReturn(addressModelMock);
        when(addressDataMock.getId()).thenReturn(ADDRESS_DATA_ID);
        when(worldpayFraudSightStrategyMock.isFraudSightEnabled()).thenReturn(true);

    }

    @Test
    public void setBillingDetails_WhenPkMatchesAddressDataId_ShouldSetPaymentAddress() {
        testObj.setBillingDetails(addressDataMock);

        verify(worldpayCheckoutServiceMock).setPaymentAddress(cartModelMock, addressModelMock);
    }

    @Test
    public void setBillingDetails_WhenPkDoesNotMatchesAddressDataId_ShouldNotSetPaymentAddress() {
        when(customerAccountServiceMock.getAddressForCode(customerModelMock, ADDRESS_DATA_ID)).thenReturn(null);

        testObj.setBillingDetails(addressDataMock);

        verify(worldpayCheckoutServiceMock, never()).setPaymentAddress(cartModelMock, addressModelMock);
    }

    @Test
    public void setBillingDetails_WhenNoDeliveryAddressesReturned_ShouldNotSetPaymentAddress() {
        when(customerAccountServiceMock.getAddressForCode(customerModelMock, ADDRESS_DATA_ID)).thenReturn(null);

        testObj.setBillingDetails(addressDataMock);

        verify(worldpayCheckoutServiceMock, never()).setPaymentAddress(cartModelMock, addressModelMock);
    }

    @Test
    public void hasBillingDetails_WhenCartIsNull_ShouldReturnFalse() {
        when(cartServiceMock.getSessionCart()).thenReturn(null);

        final boolean result = testObj.hasBillingDetails();

        assertFalse(result);
    }

    @Test
    public void hasBillingDetails_WhenPaymentAddressIsNull_ShouldReturnFalse() {
        when(cartModelMock.getPaymentAddress()).thenReturn(null);

        final boolean result = testObj.hasBillingDetails();

        assertFalse(result);
    }

    @Test
    public void hasBillingDetails_WhenCartModelAndPaymentAddressAreNotNull_ShouldReturnTrue() {
        when(cartModelMock.getPaymentAddress()).thenReturn(paymentAddressMock);

        final boolean result = testObj.hasBillingDetails();

        assertTrue(result);
    }

    @Test
    public void isFSEnabled_WhenFSIsEnabled_ShouldReturnTrue() {
        final boolean result = testObj.isFSEnabled();

        assertThat(result).isTrue();
    }

    @Test
    public void isFSEnabled_WhenFSIsDisabled_ShouldReturnFalse() {
        when(worldpayFraudSightStrategyMock.isFraudSightEnabled()).thenReturn(false);

        final boolean result = testObj.isFSEnabled();

        assertThat(result).isFalse();
    }

    @Test
    public void setShippingAndBillingDetails_WhenPkMatchesAddressDataId_ShouldSetPaymentAddress() {
        testObj.setShippingAndBillingDetails(addressDataMock);

        verify(worldpayCheckoutServiceMock).setShippingAndPaymentAddress(cartModelMock, addressModelMock);
    }

    @Test
    public void setShippingAndBillingDetails_WhenPkDoesNotMatchesAddressDataId_ShouldNotSetPaymentAddress() {
        when(customerAccountServiceMock.getAddressForCode(customerModelMock, ADDRESS_DATA_ID)).thenReturn(null);

        testObj.setShippingAndBillingDetails(addressDataMock);

        verify(worldpayCheckoutServiceMock, never()).setShippingAndPaymentAddress(cartModelMock, addressModelMock);
    }

    @Test
    public void setShippingAndBillingDetails_WhenNoDeliveryAddressesReturned_ShouldNotSetPaymentAddress() {
        when(customerAccountServiceMock.getAddressForCode(customerModelMock, ADDRESS_DATA_ID)).thenReturn(null);

        testObj.setBillingDetails(addressDataMock);

        verify(worldpayCheckoutServiceMock, never()).setShippingAndPaymentAddress(cartModelMock, addressModelMock);
    }

    @Test
    public void isGPEnabled_WhenGPIsEnabled_ShouldReturnTrue() {
        when(worldpayGuaranteedPaymentsStrategy.isGuaranteedPaymentsEnabled()).thenReturn(true);
        final boolean result = testObj.isGPEnabled();

        assertThat(result).isTrue();
    }

    @Test
    public void isGPEnabled_WhenGPIsDisabled_ShouldReturnFalse() {
        when(worldpayGuaranteedPaymentsStrategy.isGuaranteedPaymentsEnabled()).thenReturn(false);

        final boolean result = testObj.isGPEnabled();

        assertThat(result).isFalse();
    }

    @Test
    public void createCheckoutId_whenCartHasSession_shouldReturnCustomerIdAndCartCode() {
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(cartModelMock.getCode()).thenReturn(CART_CODE);
        when(customerFacade.getCurrentCustomer()).thenReturn(customerDataMock);
        when(customerDataMock.getCustomerId()).thenReturn(CUSTOMER_ID);

        final String result = testObj.createCheckoutId();

        assertThat(result).isEqualTo(CUSTOMER_ID + "_" + CART_CODE);
    }

    @Test
    public void createCheckoutId_whenCartNoHasSession_shouldReturnCustomerId() {
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.FALSE);
        when(customerFacade.getCurrentCustomer()).thenReturn(customerDataMock);
        when(customerDataMock.getCustomerId()).thenReturn(CUSTOMER_ID);

        final String result = testObj.createCheckoutId();

        assertThat(result).isEqualTo(CUSTOMER_ID);
    }
}
