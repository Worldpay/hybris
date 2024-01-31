package com.worldpay.core.services.impl;

import com.worldpay.core.dao.WorldpayCartDao;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.data.Address;
import com.worldpay.service.payment.impl.DefaultWorldpaySessionService;
import com.worldpay.service.payment.impl.OccWorldpaySessionService;
import com.worldpay.strategy.WorldpayDeliveryAddressStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayCartServiceTest {

    private static final String WORLDPAY_ORDER_CODE = "orderCode";
    private static final String DECLINE_CODE = "A19";
    private static final String BANK_CODE = "bankCode";
    private static final String CUSTOMER_ID = "customerId";
    private static final String ORIGINAL_UID = "originalUid";

    @InjectMocks
    private DefaultWorldpayCartService testObj;

    @Mock
    private WorldpayCartDao worldpayCartDaoMock;
    @Mock
    private WorldpayDeliveryAddressStrategy worldpayDeliveryAddressStrategyMock;
    @Mock
    private CustomerEmailResolutionService customerEmailResolutionServiceMock;
    @Mock
    private CartService cartServiceMock;
    @Mock
    private OccWorldpaySessionService occWorldpaySessionServiceMock;
    @Mock
    private DefaultWorldpaySessionService worldpaySessionServiceMock;
    @Mock
    private Converter<AddressModel, Address> worldpayAddressConverterMock;

    @Mock
    private CartModel cartModelMock, cartModelMock2;
    @Mock
    private CustomerModel customerModelMock;
    @Mock
    private UserModel userModelMock;
    @Mock
    private Address addressMock;
    @Mock
    private AddressModel deliveryAddressMock, paymentAddressMock;
    @Mock
    private AdditionalAuthInfo additionalAuthInfoMock;

    @Before
    public void setUp() {
        when(worldpayCartDaoMock.findCartByWorldpayOrderCode(WORLDPAY_ORDER_CODE)).thenReturn(cartModelMock);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(cartModelMock.getUser()).thenReturn(customerModelMock);
        when(worldpayAddressConverterMock.convert(deliveryAddressMock)).thenReturn(addressMock);
        when(worldpayAddressConverterMock.convert(paymentAddressMock)).thenReturn(addressMock);
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
        ReflectionTestUtils.setField(testObj, "worldpaySessionService", occWorldpaySessionServiceMock);

        testObj.setSessionId("sessionId");

        verify(occWorldpaySessionServiceMock).setSessionIdFor3dSecure("sessionId");
    }

    @Test
    public void getAuthenticatedShopperId_ShouldReturnCustomerId() {
        when(customerModelMock.getCustomerID()).thenReturn(CUSTOMER_ID);

        final String result = testObj.getAuthenticatedShopperId(cartModelMock);

        assertEquals(CUSTOMER_ID, result);
    }

    @Test
    public void getAuthenticatedShopperId_WhenCustomerIdNotPresesnt_ShouldReturnOriginalUID() {
        when(customerModelMock.getCustomerID()).thenReturn("");
        when(customerModelMock.getOriginalUid()).thenReturn(ORIGINAL_UID);

        final String result = testObj.getAuthenticatedShopperId(cartModelMock);

        assertEquals(ORIGINAL_UID, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAuthenticatedShopperId_WhenNotInstanceOfCustomer_ShouldThrowIllegalArgumentException() {
        when(cartModelMock.getUser()).thenReturn(userModelMock);

        testObj.getAuthenticatedShopperId(cartModelMock);
    }

    @Test(expected = NullPointerException.class)
    public void getAuthenticatedShopperId_WhenCartIsNull_ShouldThrowException() {
        testObj.getAuthenticatedShopperId(null);
    }

    @Test
    public void getEmailForCustomer_ShouldReturnCustomerEmail() {
        testObj.getEmailForCustomer(cartModelMock);

        verify(customerEmailResolutionServiceMock).getEmailForCustomer(customerModelMock);
    }

    @Test
    public void getAddressFromCart_WhenIsDeliveryAddressTrue_ShouldReturnDeliveryAddress() {
        when(worldpayDeliveryAddressStrategyMock.getDeliveryAddress(cartModelMock)).thenReturn(deliveryAddressMock);

        final Address result = testObj.getAddressFromCart(cartModelMock, true);

        assertThat(result).isEqualTo(addressMock);
    }

    @Test
    public void getAddressFromCart_WhenIsDeliveryAddressFalse_ShouldReturnPaymentAddress() {
        when(cartModelMock.getPaymentAddress()).thenReturn(paymentAddressMock);

        final Address result = testObj.getAddressFromCart(cartModelMock, false);

        assertThat(result).isEqualTo(addressMock);
    }

    @Test
    public void getBillingAddress_WhenUsingShippingAsBilling_ShouldReturnBillingAddress() {
        when(cartModelMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(additionalAuthInfoMock.getUsingShippingAsBilling()).thenReturn(Boolean.TRUE);

        final Address result = testObj.getBillingAddress(cartModelMock, additionalAuthInfoMock);

        assertThat(result).isEqualTo(addressMock);
    }

    @Test
    public void getBillingAddress_WhenUsingShippingAsBillingFalse_ShouldReturnPaymentAddress() {
        when(cartModelMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(cartModelMock.getPaymentAddress()).thenReturn(paymentAddressMock);
        when(additionalAuthInfoMock.getUsingShippingAsBilling()).thenReturn(Boolean.FALSE);

        final Address result = testObj.getBillingAddress(cartModelMock, additionalAuthInfoMock);

        assertThat(result).isEqualTo(addressMock);
    }

    @Test
    public void getBillingAddress_WhenNoPaymentOrDeliveryAddress_ShouldReturnNull() {
        when(cartModelMock.getDeliveryAddress()).thenReturn(null);
        when(cartModelMock.getPaymentAddress()).thenReturn(null);

        final Address result = testObj.getBillingAddress(cartModelMock, additionalAuthInfoMock);

        assertThat(result).isNull();
    }
}
