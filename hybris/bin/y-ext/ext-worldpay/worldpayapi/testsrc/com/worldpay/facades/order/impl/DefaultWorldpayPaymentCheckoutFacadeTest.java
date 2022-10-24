package com.worldpay.facades.order.impl;

import com.worldpay.core.checkout.WorldpayCheckoutService;
import com.worldpay.service.payment.WorldpayFraudSightStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
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
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayPaymentCheckoutFacadeTest {

    private static final String ADDRESS_DATA_ID = "1234";

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
    private AddressData addressDataMock;
    @Mock
    private CartModel cartModelMock;
    @Mock
    private AddressModel addressModelMock;
    @Mock
    private AddressModel paymentAddressMock;
    @Mock
    private CustomerModel customerModelMock;

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
}
