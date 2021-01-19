package com.worldpay.facades.impl;

import com.worldpay.core.address.services.WorldpayAddressService;
import com.worldpay.core.services.WorldpayCartService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayCartFacadeTest {

    private static final String BANK = "bank";

    @InjectMocks
    private DefaultWorldpayCartFacade testObj;
    @Mock
    private WorldpayCartService worldpayCartServiceMock;
    @Mock
    private CartService cartServiceMock;
    @Mock
    private WorldpayAddressService addressServiceMock;
    @Mock
    private CartModel cartModelMock;
    @Mock
    private PaymentInfoModel paymentInfoModelMock;
    @Mock
    private AddressModel addressModelMock, clonedAddressMock;

    @Before
    public void setUp() throws Exception {
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(cartServiceMock.hasSessionCart()).thenReturn(true);
        when(cartModelMock.getPaymentInfo()).thenReturn(paymentInfoModelMock);
        when(paymentInfoModelMock.getBillingAddress()).thenReturn(addressModelMock);
    }

    @Test
    public void resetDeclineCodeAndShopperBankOnCart() {
        testObj.resetDeclineCodeAndShopperBankOnCart(BANK);
        verify(worldpayCartServiceMock).resetDeclineCodeAndShopperBankOnCart(BANK);
    }

    @Test
    public void setBillingAddressFromPaymentInfo_WhenCartAndPaymentAddressArePresent_ShouldUpdateCart() {
        when(addressServiceMock.cloneAddress(addressModelMock)).thenReturn(clonedAddressMock);

        testObj.setBillingAddressFromPaymentInfo();

        verify(cartServiceMock).getSessionCart();
        verify(addressServiceMock).setCartPaymentAddress(cartModelMock, clonedAddressMock);
    }

    @Test
    public void setBillingAddressFromPaymentInfo_WhenNoSessionCart_ShouldDoNothing() {
        when(cartServiceMock.hasSessionCart()).thenReturn(false);

        testObj.setBillingAddressFromPaymentInfo();

        verify(cartServiceMock, never()).getSessionCart();
        verifyZeroInteractions(addressServiceMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setBillingAddressFromPaymentInfo_WhenPaymentInfoNull_ShouldThrowException() {
        when(cartModelMock.getPaymentInfo()).thenReturn(null);

        testObj.setBillingAddressFromPaymentInfo();
    }
}
