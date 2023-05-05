package com.worldpay.facades.order.impl;

import com.worldpay.facades.order.data.WorldpayAPMPaymentInfoData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.acceleratorservices.order.AcceleratorCheckoutService;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.enums.CountryType;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.storelocator.pos.PointOfServiceService;
import io.netty.util.internal.StringUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayCheckoutFacadeDecoratorTest {

    private static final String PICKUP_POINT_OF_SERVICE_NAME = "pickupPointOfServiceName";
    private static final String PAYMENT_INFO = "123456";
    private static final String DELIVERY_MODE_CODE = "deliveryModeCode";
    private static final String ADDRESS_CODE = "addressCode";

    @Rule
    @SuppressWarnings("PMD.MemberScope")
    public ExpectedException expectedException = ExpectedException.none();

    @Spy
    @InjectMocks
    private WorldpayCheckoutFacadeDecorator testObj;

    @Mock
    private AddressModel sourceMock;
    @Mock
    private CommerceCheckoutService commerceCheckoutServiceMock;
    @Mock
    private CheckoutFlowFacade checkoutFlowFacadeMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CartService cartServiceMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CartModel cartModelMock;
    @Mock
    private Converter<AddressModel, AddressData> addressConverterMock;
    @Mock
    private AddressData addressDataMock;
    @Mock
    private CartData cartDataMock;
    @Mock
    private CCPaymentInfoData ccPaymentInfoDataMock;
    @Mock
    private WorldpayAPMPaymentInfoData apmPaymentInfoDataMock;
    @Mock
    private CustomerModel customerModelMock;
    @Mock
    private AddressModel addressModelMock;
    @Mock
    private CartFacade cartFacadeMock;
    @Mock
    private CheckoutCustomerStrategy checkoutCustomerStrategyMock;
    @Mock
    private PointOfServiceService pointOfServiceServiceMock;
    @Mock
    private AcceleratorCheckoutService acceleratorCheckoutService;
    @Mock
    private PointOfServiceModel pointOfServiceModelMock;
    @Mock
    private CommerceCartModification commerceCartModificationMock;
    @Mock
    private PaymentInfoModel paymentInfoModelMock;
    @Mock
    private CustomerModel cartUserMock;
    @Captor
    private ArgumentCaptor<CommerceCheckoutParameter> commerceCheckoutParameterArgumentCaptor;

    private PK pk = PK.fromLong(123456L);

    @Before
    public void setUp() throws CommerceCartModificationException {
        when(addressConverterMock.convert(sourceMock)).thenReturn(addressDataMock);
        when(checkoutFlowFacadeMock.hasCheckoutCart()).thenReturn(true);
        when(cartFacadeMock.hasSessionCart()).thenReturn(true);
        when(checkoutCustomerStrategyMock.getCurrentUserForCheckout()).thenReturn(customerModelMock);
        when(pointOfServiceServiceMock.getPointOfServiceForName(PICKUP_POINT_OF_SERVICE_NAME)).thenReturn(pointOfServiceModelMock);
        when(acceleratorCheckoutService.consolidateCheckoutCart(cartModelMock, pointOfServiceModelMock)).thenReturn(Collections.singletonList(commerceCartModificationMock));
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(cartFacadeMock.getSessionCart()).thenReturn(cartDataMock);
        when(cartModelMock.getPaymentInfo()).thenReturn(paymentInfoModelMock);
    }

    @Test
    public void getBillingAddress_ShouldReturnCartPaymentAddress() {
        when(cartModelMock.getPaymentAddress()).thenReturn(sourceMock);

        final AddressData result = testObj.getBillingAddress();

        verify(addressConverterMock).convert(sourceMock);
        assertEquals(addressDataMock, result);
    }

    @Test
    public void getBillingAddress_IfNoCartFound_ShouldReturnNull() {
        when(cartModelMock.getPaymentAddress()).thenReturn(sourceMock);
        when(cartServiceMock.getSessionCart()).thenReturn(null);

        final AddressData result = testObj.getBillingAddress();

        verify(addressConverterMock, never()).convert(sourceMock);
        assertNull(result);
    }

    @Test
    public void getBillingAddress_IfCartHasNoPaymentAddress_ShouldReturnNull() {
        when(cartModelMock.getPaymentAddress()).thenReturn(null);

        final AddressData result = testObj.getBillingAddress();

        verify(addressConverterMock, never()).convert(sourceMock);
        assertNull(result);
    }

    @Test
    public void hasNoPaymentInfo_WhenCartIsNull_ShouldReturnTrue() {
        when(checkoutFlowFacadeMock.getCheckoutCart()).thenReturn(null);

        final boolean result = testObj.hasNoPaymentInfo();

        assertTrue(result);
    }

    @Test
    public void hasNoPaymentInfo_WhenCartIsNotNullAndHasNoPaymentInfos_ShouldReturnTrue() {
        when(checkoutFlowFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(cartDataMock.getPaymentInfo()).thenReturn(null);
        when(cartDataMock.getWorldpayAPMPaymentInfo()).thenReturn(null);

        final boolean result = testObj.hasNoPaymentInfo();

        assertTrue(result);
    }

    @Test
    public void hasNoPaymentInfo_WhenCartDataIsNotNullAndHasPaymentInfoAndAPMPaymentInfo_ShouldReturnFalse() {
        when(checkoutFlowFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(cartDataMock.getPaymentInfo()).thenReturn(ccPaymentInfoDataMock);
        when(cartDataMock.getWorldpayAPMPaymentInfo()).thenReturn(apmPaymentInfoDataMock);

        final boolean result = testObj.hasNoPaymentInfo();

        assertFalse(result);
    }

    @Test
    public void hasNoPaymentInfo_WhenCartHasNoCCPaymentInfoAndHasAPMPaymentInfo_ShouldReturnFalse() {
        when(checkoutFlowFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(cartDataMock.getPaymentInfo()).thenReturn(null);
        when(cartDataMock.getWorldpayAPMPaymentInfo()).thenReturn(apmPaymentInfoDataMock);

        final boolean result = testObj.hasNoPaymentInfo();

        assertFalse(result);
    }

    @Test
    public void hasNoPaymentInfo_WhenCartHasNoAPMPaymentInfo_ShouldReturnFalse() {
        when(checkoutFlowFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(cartDataMock.getWorldpayAPMPaymentInfo()).thenReturn(null);
        when(cartDataMock.getPaymentInfo()).thenReturn(ccPaymentInfoDataMock);

        final boolean result = testObj.hasNoPaymentInfo();

        assertFalse(result);
    }

    @Test
    public void getBillingAddressIfCartModelIsNull_ShouldReturnNull() {
        final AddressData billingAddress = testObj.getBillingAddress();
        assertNull(billingAddress);
    }

    @Test
    public void getBillingAddress_IfAddressModelIsNull_ShouldReturnNull() {
        final AddressData billingAddress = testObj.getBillingAddress();
        assertNull(billingAddress);
    }

    @Test
    public void getBillingAddress_IfCartAndAddressIsNotNull_ShouldReturnAddressData() {
        when(cartModelMock.getPaymentAddress()).thenReturn(addressModelMock);
        when(addressConverterMock.convert(addressModelMock)).thenReturn(addressDataMock);

        final AddressData billingAddress = testObj.getBillingAddress();

        assertEquals(addressDataMock, billingAddress);
        assertNotNull(addressModelMock);
        verify(addressConverterMock).convert(addressModelMock);
    }

    @Test
    public void getCartModel_WhenThereIsNoSessionCart_ShouldReturnNull() {
        when(checkoutFlowFacadeMock.hasCheckoutCart()).thenReturn(false);

        final CartModel cartModel = testObj.getSessionCart();

        assertNull(cartModel);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setPaymentDetails_ShouldThrowIllegalArgumentException_WhenPaymentInfoIsNull() {
        testObj.setPaymentDetails(null);
    }

    @Test
    public void setPaymentDetails_ShouldReturnFalse_WhenCurrentUserIsNotTheCartUser() {
        when(cartModelMock.getUser()).thenReturn(cartUserMock);

        final boolean result = testObj.setPaymentDetails(PAYMENT_INFO);

        assertFalse(result);
    }

    @Test
    public void setPaymentDetails_ShouldReturnFalse_WhenPaymentInfoIsEmpty() {
        when(cartModelMock.getUser()).thenReturn(cartUserMock);

        final boolean result = testObj.setPaymentDetails(StringUtil.EMPTY_STRING);

        assertFalse(result);
    }

    @Test
    public void setPaymentDetails_WhenCreditCartPaymentInfoNotFoundForCustomer_ShouldReturnFalse() {
        when(checkoutCustomerStrategyMock.getCurrentUserForCheckout()).thenReturn(cartUserMock);
        when(cartModelMock.getUser()).thenReturn(customerModelMock);

        final boolean result = testObj.setPaymentDetails(PAYMENT_INFO);

        assertFalse(result);
    }

    @Test
    public void setPaymentDetails_WhenCreditCartPaymentInfoFoundForCustomer_ShouldAddPaymentAddressToCart() {
        when(cartModelMock.getPaymentInfo().getBillingAddress()).thenReturn(addressModelMock);
        when(checkoutFlowFacadeMock.setPaymentDetails(PAYMENT_INFO)).thenReturn(true);
        when(cartModelMock.getUser()).thenReturn(customerModelMock);
        when(customerModelMock.getPaymentInfos()).thenReturn(List.of(paymentInfoModelMock));
        when(paymentInfoModelMock.getPk()).thenReturn(pk);
        when(commerceCheckoutServiceMock.setPaymentInfo(any())).thenReturn(Boolean.TRUE);

        final boolean result = testObj.setPaymentDetails(PAYMENT_INFO);

        final InOrder inOrder = inOrder(commerceCheckoutServiceMock, cartModelMock);
        inOrder.verify(cartModelMock).setPaymentAddress(addressModelMock);
        inOrder.verify(commerceCheckoutServiceMock).setPaymentInfo(commerceCheckoutParameterArgumentCaptor.capture());

        final CommerceCheckoutParameter parameter = commerceCheckoutParameterArgumentCaptor.getValue();

        assertEquals(parameter.getPaymentInfo(), paymentInfoModelMock);
        assertTrue(result);
    }

    @Test
    public void createPaymentSubscription() {
        testObj.createPaymentSubscription(ccPaymentInfoDataMock);
        verify(checkoutFlowFacadeMock).createPaymentSubscription(ccPaymentInfoDataMock);
    }

    @Test
    public void placeOrder() throws InvalidCartException {
        testObj.placeOrder();
        verify(checkoutFlowFacadeMock).placeOrder();
    }

    @Test
    public void getSupportedDeliveryAddresses() {
        testObj.getSupportedDeliveryAddresses(true);
        verify(checkoutFlowFacadeMock).getSupportedDeliveryAddresses(true);
    }

    @Test
    public void setCheapestDeliveryModeForCheckout() {
        testObj.setCheapestDeliveryModeForCheckout();
        verify(checkoutFlowFacadeMock).setCheapestDeliveryModeForCheckout();
    }

    @Test
    public void isTaxEstimationEnabledForCart() {
        testObj.isTaxEstimationEnabledForCart();
        verify(checkoutFlowFacadeMock).isTaxEstimationEnabledForCart();
    }

    @Test
    public void hasCheckoutCart() {
        testObj.hasCheckoutCart();
        verify(checkoutFlowFacadeMock).hasCheckoutCart();
    }

    @Test
    public void hasShippingItems() {
        testObj.hasShippingItems();
        verify(checkoutFlowFacadeMock).hasShippingItems();
    }

    @Test
    public void getConsolidatedPickupOptions() {
        testObj.getConsolidatedPickupOptions();
        verify(checkoutFlowFacadeMock).getConsolidatedPickupOptions();
    }

    @Test
    public void getSupportedDeliveryModes() {
        testObj.getSupportedDeliveryModes();
        verify(checkoutFlowFacadeMock).getSupportedDeliveryModes();
    }

    @Test
    public void removeDeliveryMode() {
        testObj.removeDeliveryMode();
        verify(checkoutFlowFacadeMock).removeDeliveryMode();
    }

    @Test
    public void setDeliveryMode() {
        testObj.setDeliveryMode(DELIVERY_MODE_CODE);
        verify(checkoutFlowFacadeMock).setDeliveryMode(DELIVERY_MODE_CODE);
    }

    @Test
    public void setDeliveryAddressIfAvailable() {
        testObj.setDeliveryAddressIfAvailable();
        verify(checkoutFlowFacadeMock).setDeliveryAddressIfAvailable();
    }

    @Test
    public void containsTaxValues() {
        testObj.containsTaxValues();
        verify(checkoutFlowFacadeMock).containsTaxValues();
    }

    @Test
    public void getSupportedCardTypes() {
        testObj.getSupportedCardTypes();
        verify(checkoutFlowFacadeMock).getSupportedCardTypes();
    }

    @Test
    public void setDeliveryModeIfAvailable() {
        testObj.setDeliveryModeIfAvailable();
        verify(checkoutFlowFacadeMock).setDeliveryModeIfAvailable();
    }

    @Test
    public void setDefaultPaymentInfoForCheckout() {
        testObj.setDefaultPaymentInfoForCheckout();
        verify(checkoutFlowFacadeMock).setDefaultPaymentInfoForCheckout();
    }

    @Test
    public void getSubscriptionPciOption() {
        testObj.getSubscriptionPciOption();
        verify(checkoutFlowFacadeMock).getSubscriptionPciOption();
    }

    @Test
    public void hasNoDeliveryAddress() {
        testObj.hasNoDeliveryAddress();
        verify(checkoutFlowFacadeMock).hasNoDeliveryAddress();
    }

    @Test
    public void isExpressCheckoutAllowedForCart() {
        testObj.isExpressCheckoutAllowedForCart();
        verify(checkoutFlowFacadeMock).isExpressCheckoutAllowedForCart();
    }

    @Test
    public void consolidateCheckoutCart() throws CommerceCartModificationException {
        testObj.consolidateCheckoutCart(PICKUP_POINT_OF_SERVICE_NAME);
        verify(checkoutFlowFacadeMock).consolidateCheckoutCart(PICKUP_POINT_OF_SERVICE_NAME);
    }


    @Test
    public void hasNoDeliveryMode() {
        testObj.hasNoDeliveryMode();
        verify(checkoutFlowFacadeMock).hasNoDeliveryMode();
    }

    @Test
    public void getDeliveryAddressForCode() {
        testObj.getDeliveryAddressForCode(ADDRESS_CODE);
        verify(checkoutFlowFacadeMock).getDeliveryAddressForCode(ADDRESS_CODE);
    }

    @Test
    public void setPaymentInfoIfAvailable() {
        testObj.setPaymentInfoIfAvailable();
        verify(checkoutFlowFacadeMock).setPaymentInfoIfAvailable();
    }

    @Test
    public void setDeliveryAddress() {
        testObj.setDeliveryAddress(addressDataMock);
        verify(checkoutFlowFacadeMock).setDeliveryAddress(addressDataMock);
    }

    @Test
    public void getAddressDataForId() {
        testObj.getAddressDataForId(ADDRESS_CODE, true);
        verify(checkoutFlowFacadeMock).getAddressDataForId(ADDRESS_CODE, true);
    }

    @Test
    public void isExpressCheckoutEnabledForStore() {
        testObj.isExpressCheckoutEnabledForStore();
        verify(checkoutFlowFacadeMock).isExpressCheckoutEnabledForStore();
    }

    @Test
    public void setDefaultDeliveryAddressForCheckout() {
        testObj.setDefaultDeliveryAddressForCheckout();
        verify(checkoutFlowFacadeMock).setDefaultDeliveryAddressForCheckout();
    }

    @Test
    public void performExpressCheckout() {
        testObj.performExpressCheckout();
        verify(checkoutFlowFacadeMock).performExpressCheckout();
    }

    @Test
    public void getCheckoutFlowGroupForCheckout() {
        testObj.getCheckoutFlowGroupForCheckout();
        verify(checkoutFlowFacadeMock).getCheckoutFlowGroupForCheckout();
    }

    @Test
    public void removeDeliveryAddress() {
        testObj.removeDeliveryAddress();
        verify(checkoutFlowFacadeMock).removeDeliveryAddress();
    }

    @Test
    public void hasPickUpItems() {
        testObj.hasPickUpItems();
        verify(checkoutFlowFacadeMock).hasPickUpItems();
    }

    @Test
    public void prepareCartForCheckout() {
        testObj.prepareCartForCheckout();
        verify(checkoutFlowFacadeMock).prepareCartForCheckout();
    }

    @Test
    public void hasValidCart() {
        testObj.hasValidCart();
        verify(checkoutFlowFacadeMock).hasValidCart();
    }

    @Test
    public void getCountries_ForTypeBilling_ShouldUseMethodFromCheckoutFlowFacade() {
        testObj.getCountries(CountryType.BILLING);
        verify(checkoutFlowFacadeMock).getCountries(CountryType.BILLING);
    }

    @Test
    public void getCountries_ForTypeDelivery_ShouldUseMethodFromCheckoutFlowFacade() {
        testObj.getCountries(CountryType.SHIPPING);
        verify(checkoutFlowFacadeMock).getCountries(CountryType.SHIPPING);
    }
}
