package com.worldpay.service.payment.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.user.AddressService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TestAbstractWorldpayOrderServiceTest {

    private static final String PAYMENT_PROVIDER = "paymentProvider";
    private AbstractWorldpayOrderService testObj;

    @Mock
    private AddressService addressServiceMock;
    @Mock
    private CommerceCheckoutService commerceCheckoutServiceMock;

    @Mock
    private AddressModel paymentAddressModelMock, clonedAddressModelMock;
    @Mock
    private CartModel cartModelMock;
    @Mock
    private PaymentInfoModel paymentInfoModelMock;

    @Before
    public void setUp() throws Exception {
        testObj = Mockito.mock(AbstractWorldpayOrderService.class, Mockito.CALLS_REAL_METHODS);
        Whitebox.setInternalState(testObj, "commerceCheckoutService", commerceCheckoutServiceMock);
        Whitebox.setInternalState(testObj, "addressService", addressServiceMock);
    }

    @Test
    public void createCommerceCheckoutParameter_shouldCreateCommerceCheckoutParameter_WhenItIsCalled() {
        when(commerceCheckoutServiceMock.getPaymentProvider()).thenReturn(PAYMENT_PROVIDER);

        final CommerceCheckoutParameter result = testObj.createCommerceCheckoutParameter(cartModelMock, paymentInfoModelMock, BigDecimal.ONE);

        assertEquals(paymentInfoModelMock, result.getPaymentInfo());
        assertTrue(result.isEnableHooks());
        assertEquals(BigDecimal.ONE, result.getAuthorizationAmount());
        assertEquals(PAYMENT_PROVIDER, result.getPaymentProvider());
        assertEquals(cartModelMock, result.getCart());
    }

    @Test
    public void cloneAndSetBillingAddressFromCart_ShouldCloneAndSetBillingAddressFromCart_WhenItIsCalled() {
        when(cartModelMock.getPaymentAddress()).thenReturn(paymentAddressModelMock);
        when(addressServiceMock.cloneAddressForOwner(paymentAddressModelMock, paymentInfoModelMock)).thenReturn(clonedAddressModelMock);

        final AddressModel result = testObj.cloneAndSetBillingAddressFromCart(cartModelMock, paymentInfoModelMock);

        assertEquals(result, clonedAddressModelMock);
        verify(clonedAddressModelMock).setBillingAddress(true);
        verify(clonedAddressModelMock).setShippingAddress(false);
        verify(paymentInfoModelMock).setBillingAddress(clonedAddressModelMock);
    }

}
