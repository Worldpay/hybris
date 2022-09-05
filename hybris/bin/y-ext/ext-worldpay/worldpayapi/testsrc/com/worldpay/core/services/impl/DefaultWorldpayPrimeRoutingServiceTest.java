package com.worldpay.core.services.impl;

import com.worldpay.core.services.WorldpayHybrisOrderService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayPrimeRoutingServiceTest {

    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String US_COUNTRY_ISO_CODE = "US";

    @InjectMocks
    private DefaultWorldpayPrimeRoutingService testObj;

    @Mock
    private CartService cartService;
    @Mock
    private WorldpayHybrisOrderService worldpayHybrisOrderServiceMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CartModel cartMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private AddressModel addressMock;
    @Mock
    private OrderModel orderMock;

    @Before
    public void setUp() {
        when(cartMock.getSite().getEnablePR()).thenReturn(true);
        when(cartMock.getPaymentAddress()).thenReturn(addressMock);
        when(addressMock.getCountry().getIsocode()).thenReturn(US_COUNTRY_ISO_CODE);
    }

    @Test
    public void isPrimeRoutingEnabled_WhenPrimeRoutingEnabledAndUSBillingAddress_ShouldReturnTrue() {
        boolean result = testObj.isPrimeRoutingEnabled(cartMock);

        assertThat(result).isTrue();
    }

    @Test
    public void isPrimeRoutingEnabled_WhenPrimeRoutingEnabledAndNotUSBillingAddress_ShouldReturnFalse() {
        when(addressMock.getCountry().getIsocode()).thenReturn("UK");

        boolean result = testObj.isPrimeRoutingEnabled(cartMock);

        assertThat(result).isFalse();
    }

    @Test
    public void isPrimeRoutingEnabled_WhenPrimeRoutingDisabled_ShouldReturnFalse() {
        when(cartMock.getSite().getEnablePR()).thenReturn(false);

        boolean result = testObj.isPrimeRoutingEnabled(cartMock);

        assertThat(result).isFalse();
    }

    @Test
    public void isPrimeRoutingEnabled_WhenPrimeRoutingEnabledAndBillingAddressIsNull_ShouldReturnFalse() {
        when(cartMock.getPaymentAddress()).thenReturn(null);

        boolean result = testObj.isPrimeRoutingEnabled(cartMock);

        assertThat(result).isFalse();
    }

    @Test
    public void setAuthorisedWithPrimeRoutingOnCart_ShouldSetFlagOnCart() {
        testObj.setAuthorisedWithPrimeRoutingOnCart(cartMock);

        final InOrder inOrder = inOrder(cartMock, cartService);
        inOrder.verify(cartMock).setIsPrimeRouteAuth(Boolean.TRUE);
        inOrder.verify(cartService).saveOrder(cartMock);
    }

    @Test
    public void isOrderAuthorisedWithPrimeRouting_WhenOrderAuthorisedWithPrimeRouting_ShouldReturnTrue() {
        when(worldpayHybrisOrderServiceMock.findOrderByWorldpayOrderCode(WORLDPAY_ORDER_CODE)).thenReturn(orderMock);
        when(orderMock.getIsPrimeRouteAuth()).thenReturn(Boolean.TRUE);

        boolean result = testObj.isOrderAuthorisedWithPrimeRouting(WORLDPAY_ORDER_CODE);

        assertThat(result).isTrue();
    }
}
