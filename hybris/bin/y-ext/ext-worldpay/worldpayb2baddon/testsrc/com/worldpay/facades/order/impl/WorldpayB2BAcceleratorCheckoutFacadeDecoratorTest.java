package com.worldpay.facades.order.impl;

import com.worldpay.facades.order.data.WorldpayAPMPaymentInfoData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.b2b.model.B2BCommentModel;
import de.hybris.platform.b2b.services.B2BCommentService;
import de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData;
import de.hybris.platform.b2bacceleratorfacades.exception.EntityValidationException;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BReplenishmentRecurrenceEnum;
import de.hybris.platform.b2bacceleratorfacades.order.data.TriggerData;
import de.hybris.platform.b2bacceleratorfacades.order.impl.DefaultB2BAcceleratorCheckoutFacade;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.cronjob.enums.DayOfWeek;
import de.hybris.platform.cronjob.model.TriggerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.orderscheduling.ScheduleOrderService;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayB2BAcceleratorCheckoutFacadeDecoratorTest {

    @Spy
    @InjectMocks
    private WorldpayB2BAcceleratorCheckoutFacadeDecorator testObj;
    @Mock
    private DefaultB2BAcceleratorCheckoutFacade b2BAcceleratorCheckoutFacade;
    @Mock
    private CartFacade cartFacadeMock;
    @Mock
    private CartService cartServiceMock;
    @Mock
    private CheckoutCustomerStrategy checkoutCustomerStrategy;
    @Mock
    @SuppressWarnings("PMD.UnusedPrivateField")
    private L10NService l10NService;
    @Mock
    @SuppressWarnings("PMD.UnusedPrivateField")
    private I18NService i18NService;
    @Mock
    private BaseSiteService baseSiteService;
    @Mock
    private BaseStoreService baseStoreService;
    @Mock
    private ModelService modelService;
    @Mock
    @SuppressWarnings("PMD.UnusedPrivateField")
    private Populator<TriggerData, TriggerModel> triggerPopulator;
    @Mock
    @SuppressWarnings("PMD.UnusedPrivateField")
    private ScheduleOrderService scheduleOrderService;
    @Mock
    @SuppressWarnings("PMD.UnusedPrivateField")
    private B2BCommentService<AbstractOrderModel> b2bCommentService;
    @Mock
    private CommerceCheckoutService commerceCheckoutServiceMock;

    @Mock
    private CartModel cartModelMock;
    @Mock
    private CartData cartDataMock;
    @Mock
    private BaseSiteModel baseSiteModelMock;
    @Mock
    private BaseStoreModel baseStoreModelMock;
    @Mock
    private TriggerModel triggerModelMock;
    @Mock
    private B2BCommentModel b2BCommentModelMock;
    @Mock
    private B2BPaymentTypeData b2BPaymentTypeDataMock;
    @Mock
    private CCPaymentInfoData ccPaymentInfoDataMock;
    @Mock
    private WorldpayAPMPaymentInfoData apmPaymentInfoDataMock;
    @Mock
    private CustomerModel customerModelMock;
    @Mock
    private PaymentInfoModel paymentInfoModelMock;

    private PK pk = PK.fromLong(123456L);

    @Before
    public void setUp() {
        when(cartDataMock.isCalculated()).thenReturn(true);
        when(cartDataMock.getDeliveryAddress()).thenReturn(mock(AddressData.class));
        when(cartDataMock.getDeliveryMode()).thenReturn(mock(DeliveryModeData.class));
        lenient().when(cartModelMock.getUser()).thenReturn(customerModelMock);
        lenient().when(checkoutCustomerStrategy.getCurrentUserForCheckout()).thenReturn(customerModelMock);
        doReturn(cartDataMock).when(testObj).callSuperGetCheckoutCart();
        doReturn(cartModelMock).when(testObj).callSuperGetCart();

        when(i18NService.getCurrentTimeZone()).thenReturn(TimeZone.getDefault());
        when(i18NService.getCurrentLocale()).thenReturn(Locale.getDefault());
        lenient().when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSiteModelMock);
        lenient().when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModelMock);

        lenient().when(modelService.create(TriggerModel.class)).thenReturn(triggerModelMock);
        lenient().when(modelService.create(B2BCommentModel.class)).thenReturn(b2BCommentModelMock);
    }

    @Test
    public void placeOrderShouldPlaceOrderOnBuyNowWhenAuthorized() throws InvalidCartException {
        final PlaceOrderData placeOrderData = createPlaceOrderData(false);
        final PaymentTransactionEntryModel paymentTransactionEntryModelMock = mock(PaymentTransactionEntryModel.class);
        final PaymentTransactionModel paymentTransactionModelMock = mock(PaymentTransactionModel.class);

        when(paymentTransactionEntryModelMock.getType()).thenReturn(PaymentTransactionType.AUTHORIZATION);
        when(paymentTransactionEntryModelMock.getTransactionStatus()).thenReturn(TransactionStatus.ACCEPTED.name());
        when(paymentTransactionModelMock.getEntries()).thenReturn(Collections.singletonList(paymentTransactionEntryModelMock));

        mockPayByCard();
        when(cartModelMock.getPaymentTransactions()).thenReturn(Collections.singletonList(paymentTransactionModelMock));

        testObj.placeOrder(placeOrderData);

        verify(b2BAcceleratorCheckoutFacade, times(1)).placeOrder();
    }

    @Test(expected = EntityValidationException.class)
    public void placeOrderShouldNotPlaceOrderOnBuyNowWhenNotAuthorized() throws InvalidCartException {
        final PlaceOrderData placeOrderData = createPlaceOrderData(false);

        when(cartModelMock.getPaymentType()).thenReturn(CheckoutPaymentType.CARD);

        testObj.placeOrder(placeOrderData);
    }

    @Test
    public void placeOrderShouldNotPlaceOrderOnReplenishment() throws InvalidCartException {
        final PlaceOrderData placeOrderData = createPlaceOrderData(true);

        mockPayByCard();
        lenient().when(triggerModelMock.getRelative()).thenReturn(Boolean.TRUE);

        testObj.placeOrder(placeOrderData);

        verify(b2BAcceleratorCheckoutFacade, times(0)).placeOrder();
    }

    @Test
    public void placeOrderShouldScheduleReplenishment() throws InvalidCartException {
        final PlaceOrderData placeOrderData = createPlaceOrderData(true);

        mockPayByCard();
        lenient().when(triggerModelMock.getRelative()).thenReturn(Boolean.TRUE);

        testObj.placeOrder(placeOrderData);

        verify(b2BAcceleratorCheckoutFacade, times(1)).scheduleOrder(any());
    }


    @Test
    public void hasNoPaymentInfo_ShouldReturnTrue_WhenCartDataIsNull() {
        doReturn(null).when(testObj).callSuperGetCheckoutCart();
        assertTrue(testObj.hasNoPaymentInfo());
    }

    @Test
    public void hasNoPaymentInfo_ShouldReturnTrue_WhenBothPaymentInfosAreNull() {
        when(cartDataMock.getPaymentInfo()).thenReturn(null);
        when(cartDataMock.getWorldpayAPMPaymentInfo()).thenReturn(null);
        assertTrue(testObj.hasNoPaymentInfo());
    }

    @Test
    public void hasNoPaymentInfo_ShouldReturnFalse_WhenCCPaymentInfoExists() {
        when(cartDataMock.getPaymentInfo()).thenReturn(ccPaymentInfoDataMock);
        assertFalse(testObj.hasNoPaymentInfo());
    }

    @Test
    public void hasNoPaymentInfo_ShouldReturnFalse_WhenAPMPaymentInfoExists() {
        when(cartDataMock.getPaymentInfo()).thenReturn(null);
        when(cartDataMock.getWorldpayAPMPaymentInfo()).thenReturn(apmPaymentInfoDataMock);
        assertFalse(testObj.hasNoPaymentInfo());
    }


    @Test
    public void setPaymentDetails_ShouldReturnFalse_WhenPaymentInfoIdIsBlank() {
        assertFalse(testObj.setPaymentDetails("   "));
    }

    @Test
    public void setPaymentDetails_ShouldReturnTrue_WhenPaymentInfoFoundAndSet() {
        doReturn(true).when(testObj).callSuperCheckIfCurrentUserIsTheCartUser();
        doReturn(customerModelMock).when(testObj).callSuperGetCurrentUserForCheckout();
        when(paymentInfoModelMock.getPk()).thenReturn(pk);
        when(customerModelMock.getPaymentInfos()).thenReturn(new HashSet<>(Collections.singletonList(paymentInfoModelMock)));
        when(commerceCheckoutServiceMock.setPaymentInfo(any())).thenReturn(true);

        assertTrue(testObj.setPaymentDetails(pk.toString()));
    }

    @Test
    public void setPaymentDetails_ShouldReturnFalse_WhenNoMatchingPaymentInfo() {
        final String paymentInfoId = "notfound";
        doReturn(true).when(testObj).callSuperCheckIfCurrentUserIsTheCartUser();
        doReturn(customerModelMock).when(testObj).callSuperGetCurrentUserForCheckout();
        when(customerModelMock.getPaymentInfos()).thenReturn(new HashSet<>(Collections.singletonList(paymentInfoModelMock)));
        when(paymentInfoModelMock.getPk()).thenReturn(pk);

        assertFalse(testObj.setPaymentDetails(paymentInfoId));
    }

    @Test
    public void isPayNowOrder_ShouldReturnTrue_WhenReplenishmentOrderIsNull() {
        assertTrue(testObj.isPayNowOrder(new de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData()));
    }

    @Test
    public void isPayNowOrder_ShouldReturnFalse_WhenReplenishmentOrderIsTrue() {
        final PlaceOrderData placeOrderData = new PlaceOrderData();
        placeOrderData.setReplenishmentOrder(Boolean.TRUE);
        assertFalse(testObj.isPayNowOrder(placeOrderData));
    }

    @Test
    public void isPayNowOrder_ShouldReturnTrue_WhenReplenishmentOrderIsFalse() {
        de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData data =
                new de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData();
        data.setReplenishmentOrder(Boolean.FALSE);
        assertTrue(testObj.isPayNowOrder(data));
    }

    @Test
    public void callSuperGetCheckoutCart_ShouldReturnCartData() {
        assertSame(cartDataMock, testObj.callSuperGetCheckoutCart());
    }

    @Test
    public void callSuperGetCart_ShouldReturnCartModel() {
        assertSame(cartModelMock, testObj.callSuperGetCart());
    }

    protected void mockPayByCard() {
        when(cartModelMock.getPaymentType()).thenReturn(CheckoutPaymentType.CARD);
        final B2BPaymentTypeData b2BPaymentTypeData = new B2BPaymentTypeData();
        b2BPaymentTypeData.setCode("CARD");
    }


    protected PlaceOrderData createPlaceOrderData(final boolean replenishment) {
        final PlaceOrderData placeOrderData = new PlaceOrderData();
        placeOrderData.setReplenishmentOrder(replenishment);
        placeOrderData.setTermsCheck(true);

        if (replenishment) {
            placeOrderData.setReplenishmentStartDate(new Date());
            placeOrderData.setReplenishmentRecurrence(B2BReplenishmentRecurrenceEnum.WEEKLY);
            placeOrderData.setNDaysOfWeek(Collections.singletonList(DayOfWeek.FRIDAY));
            placeOrderData.setNWeeks("2");
        }

        return placeOrderData;
    }
}
