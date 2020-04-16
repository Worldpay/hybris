package com.worldpay.facades.order.impl;

import com.worldpay.core.checkout.WorldpayCheckoutService;
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
import org.mockito.runners.MockitoJUnitRunner;

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
    }

    @Test
    public void setBillingDetailsSetsPaymentAddressWhenPkMatchesAddressDataId() {
        when(addressDataMock.getId()).thenReturn(ADDRESS_DATA_ID);
        when(customerAccountServiceMock.getAddressForCode(customerModelMock, ADDRESS_DATA_ID)).thenReturn(addressModelMock);

        testObj.setBillingDetails(addressDataMock);

        verify(worldpayCheckoutServiceMock).setPaymentAddress(cartModelMock, addressModelMock);
    }

    @Test
    public void setBillingDetailsDoesNotSetPaymentAddressWhenPkDoesNotMatchesAddressDataId() {
        when(addressDataMock.getId()).thenReturn(ADDRESS_DATA_ID);
        when(customerAccountServiceMock.getAddressForCode(customerModelMock, ADDRESS_DATA_ID)).thenReturn(null);

        testObj.setBillingDetails(addressDataMock);

        verify(worldpayCheckoutServiceMock, never()).setPaymentAddress(cartModelMock, addressModelMock);
    }

    @Test
    public void setBillingDetailsDoesNotSetPaymentAddressWhenNoDeliveryAddressesReturned() {
        when(addressDataMock.getId()).thenReturn(ADDRESS_DATA_ID);
        when(customerAccountServiceMock.getAddressForCode(customerModelMock, ADDRESS_DATA_ID)).thenReturn(null);

        testObj.setBillingDetails(addressDataMock);

        verify(worldpayCheckoutServiceMock, never()).setPaymentAddress(cartModelMock, addressModelMock);
    }

    @Test
    public void hasBillingDetailsReturnsFalseWhenCartIsNull() {
        when(cartServiceMock.getSessionCart()).thenReturn(null);
        final boolean result = testObj.hasBillingDetails();
        assertFalse(result);
    }

    @Test
    public void hasBillingDetailsReturnsFalseWhenPaymentAddressIsNull() {
        when(cartModelMock.getPaymentAddress()).thenReturn(null);
        final boolean result = testObj.hasBillingDetails();
        assertFalse(result);
    }

    @Test
    public void hasBillingDetailsReturnsTrueWhenCartModelAndPaymentAddressAreNotNull() {
        when(cartModelMock.getPaymentAddress()).thenReturn(paymentAddressMock);
        final boolean result = testObj.hasBillingDetails();
        assertTrue(result);
    }
}
