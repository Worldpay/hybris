package com.worldpay.facades.impl;

import com.worldpay.core.services.WorldpayCartService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private CartModel cartModelMock;
    @Mock
    private PaymentInfoModel paymentInfoModelMock;
    @Mock
    private AddressModel addressModelMock;

    @Test
    public void resetDeclineCodeAndShopperBankOnCart() {
        testObj.resetDeclineCodeAndShopperBankOnCart(BANK);
        verify(worldpayCartServiceMock).resetDeclineCodeAndShopperBankOnCart(BANK);
    }

    @Test
    public void shouldSetBillingAddressFromPaymentInfoWhenPaymentAddressIsNull() {
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(cartModelMock.getPaymentAddress()).thenReturn(null);
        when(cartModelMock.getPaymentInfo()).thenReturn(paymentInfoModelMock);
        when(paymentInfoModelMock.getBillingAddress()).thenReturn(addressModelMock);

        testObj.setBillingAddressFromPaymentInfo();

        verify(cartModelMock).setPaymentAddress(addressModelMock);
        verify(cartServiceMock).saveOrder(cartModelMock);
    }

    @Test
    public void shouldNotSetPaymentAddressWhenIsNotNull() {
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(cartModelMock.getPaymentAddress()).thenReturn(addressModelMock);

        testObj.setBillingAddressFromPaymentInfo();

        verify(cartModelMock, never()).setPaymentAddress(addressModelMock);
        verify(cartServiceMock, never()).saveOrder(cartModelMock);
    }
}
