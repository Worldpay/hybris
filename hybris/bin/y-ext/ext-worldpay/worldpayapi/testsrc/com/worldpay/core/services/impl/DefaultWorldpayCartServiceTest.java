package com.worldpay.core.services.impl;

import com.worldpay.core.dao.WorldpayCartDao;
import com.worldpay.service.payment.impl.DefaultWorldpaySessionService;
import com.worldpay.service.payment.impl.OccWorldpaySessionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayCartServiceTest {

    private static final String WORLDPAY_ORDER_CODE = "orderCode";
    private static final String DECLINE_CODE = "A19";
    private static final String BANK_CODE = "bankCode";

    @InjectMocks
    private DefaultWorldpayCartService testObj;

    @Mock
    private WorldpayCartDao worldpayCartDaoMock;
    @Mock
    private CartModel cartModelMock;
    @Mock
    private CartModel cartModelMock2;
    @Mock
    private CartService cartServiceMock;
    @Mock
    private OccWorldpaySessionService occWorldpaySessionServiceMock;
    @Mock
    private DefaultWorldpaySessionService worldpaySessionServiceMock;

    @Before
    public void setUp() throws Exception {
        Whitebox.setInternalState(testObj, "worldpaySessionService", worldpaySessionServiceMock);
        when(worldpayCartDaoMock.findCartByWorldpayOrderCode(WORLDPAY_ORDER_CODE)).thenReturn(cartModelMock);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
    }

    @Test
    public void setWorldpayDeclineCodeOnCartShouldSetDeclineCode() {

        testObj.setWorldpayDeclineCodeOnCart(WORLDPAY_ORDER_CODE, DECLINE_CODE);

        verify(worldpayCartDaoMock).findCartByWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        verify(cartModelMock).setWorldpayDeclineCode(DECLINE_CODE);
        verify(cartServiceMock).saveOrder(cartModelMock);
    }

    @Test
    public void setWorldpayDeclineCodeOnCartShouldNotSetDeclineCodeWhenNoCartsFound() {
        when(worldpayCartDaoMock.findCartByWorldpayOrderCode(WORLDPAY_ORDER_CODE)).thenThrow(new ModelNotFoundException("no cart found for the code"));
        testObj.setWorldpayDeclineCodeOnCart(WORLDPAY_ORDER_CODE, DECLINE_CODE);

        verify(worldpayCartDaoMock).findCartByWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        verify(cartModelMock, never()).setWorldpayDeclineCode(DECLINE_CODE);
        verify(cartServiceMock, never()).saveOrder(cartModelMock);
    }

    @Test
    public void setWorldpayDeclineCodeOnCartShouldNotSetDeclineCodeWhenMultipleCartsFound() {
        when(worldpayCartDaoMock.findCartByWorldpayOrderCode(WORLDPAY_ORDER_CODE)).thenThrow(new AmbiguousIdentifierException("more than one cart found for the code"));

        testObj.setWorldpayDeclineCodeOnCart(WORLDPAY_ORDER_CODE, DECLINE_CODE);

        verify(worldpayCartDaoMock).findCartByWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        verify(cartModelMock2, never()).setWorldpayDeclineCode(DECLINE_CODE);
        verify(cartModelMock2, never()).setWorldpayDeclineCode(DECLINE_CODE);
        verify(cartServiceMock, never()).saveOrder(cartModelMock);
        verify(cartServiceMock, never()).saveOrder(cartModelMock2);
    }

    @Test
    public void findCartsByWorldpayOrderCodeShouldUseWorldpay() {
        final CartModel result = testObj.findCartByWorldpayOrderCode(WORLDPAY_ORDER_CODE);

        verify(worldpayCartDaoMock).findCartByWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        assertEquals(cartModelMock, result);
    }

    @Test
    public void setWorldpayDeclineCodeAndBankCodeOnCart() {
        when(cartServiceMock.hasSessionCart()).thenReturn(true);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);

        testObj.resetDeclineCodeAndShopperBankOnCart(BANK_CODE);

        verify(cartModelMock).setWorldpayDeclineCode("0");
        verify(cartModelMock).setShopperBankCode(BANK_CODE);
        verify(cartServiceMock).saveOrder(cartModelMock);
    }

    @Test
    public void shouldDoNothigIfNoCartInSession() {
        when(cartServiceMock.hasSessionCart()).thenReturn(false);

        testObj.resetDeclineCodeAndShopperBankOnCart(BANK_CODE);

        verifyZeroInteractions(cartModelMock);
        verify(cartServiceMock, never()).saveOrder(cartModelMock);
    }

    @Test
    public void setSessionId_whenNotInstanceOfOccWorldpay_shouldDoNothig() {
        testObj.setSessionId("sessionId");

        verifyNoMoreInteractions(worldpaySessionServiceMock);
    }

    @Test
    public void setSessionId_whenInstanceOfOccWorldpay_shouldDoNothig() {
        Whitebox.setInternalState(testObj, "worldpaySessionService", occWorldpaySessionServiceMock);

        testObj.setSessionId("sessionId");

        verify(occWorldpaySessionServiceMock).setSessionIdFor3dSecure("sessionId");
    }
}
