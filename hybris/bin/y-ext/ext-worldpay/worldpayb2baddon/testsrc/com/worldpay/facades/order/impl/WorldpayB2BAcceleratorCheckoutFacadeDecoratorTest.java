package com.worldpay.facades.order.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCommentModel;
import de.hybris.platform.b2b.services.B2BCommentService;
import de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData;
import de.hybris.platform.b2bacceleratorfacades.exception.EntityValidationException;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BReplenishmentRecurrenceEnum;
import de.hybris.platform.b2bacceleratorfacades.order.data.TriggerData;
import de.hybris.platform.b2bacceleratorfacades.order.impl.DefaultB2BAcceleratorCheckoutFacade;
import de.hybris.platform.b2bacceleratorservices.enums.CheckoutPaymentType;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
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
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayB2BAcceleratorCheckoutFacadeDecoratorTest {

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

    @Before
    public void setUp() {
        CustomerModel customerModelMock = Mockito.mock(CustomerModel.class);

        Mockito.when(cartFacadeMock.hasSessionCart()).thenReturn(true);
        Mockito.when(cartFacadeMock.getSessionCart()).thenReturn(cartDataMock);
        Mockito.when(cartDataMock.isCalculated()).thenReturn(true);
        Mockito.when(cartDataMock.getDeliveryAddress()).thenReturn(Mockito.mock(AddressData.class));
        Mockito.when(cartDataMock.getDeliveryMode()).thenReturn(Mockito.mock(DeliveryModeData.class));
        Mockito.when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        Mockito.lenient().when(cartModelMock.getUser()).thenReturn(customerModelMock);
        Mockito.lenient().when(checkoutCustomerStrategy.getCurrentUserForCheckout()).thenReturn(customerModelMock);

        Mockito.when(i18NService.getCurrentTimeZone()).thenReturn(TimeZone.getDefault());
        Mockito.when(i18NService.getCurrentLocale()).thenReturn(Locale.getDefault());
        Mockito.lenient().when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSiteModelMock);
        Mockito.lenient().when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModelMock);

        Mockito.lenient().when(modelService.create(TriggerModel.class)).thenReturn(triggerModelMock);
        Mockito.lenient().when(modelService.create(B2BCommentModel.class)).thenReturn(b2BCommentModelMock);
    }

    @Test
    public void placeOrderShouldPlaceOrderOnBuyNowWhenAuthorized() throws InvalidCartException {
        PlaceOrderData placeOrderData = createPlaceOrderData(false);
        PaymentTransactionEntryModel paymentTransactionEntryModelMock = Mockito.mock(PaymentTransactionEntryModel.class);
        PaymentTransactionModel paymentTransactionModelMock = Mockito.mock(PaymentTransactionModel.class);

        Mockito.when(paymentTransactionEntryModelMock.getType()).thenReturn(PaymentTransactionType.AUTHORIZATION);
        Mockito.when(paymentTransactionEntryModelMock.getTransactionStatus()).thenReturn(TransactionStatus.ACCEPTED.name());
        Mockito.when(paymentTransactionModelMock.getEntries()).thenReturn(Collections.singletonList(paymentTransactionEntryModelMock));

        mockPayByCard();
        Mockito.when(cartModelMock.getPaymentTransactions()).thenReturn(Collections.singletonList(paymentTransactionModelMock));

        testObj.placeOrder(placeOrderData);

        Mockito.verify(b2BAcceleratorCheckoutFacade, Mockito.times(1)).placeOrder();
    }

    @Test(expected = EntityValidationException.class)
    public void placeOrderShouldNotPlaceOrderOnBuyNowWhenNotAuthorized() throws InvalidCartException {
        PlaceOrderData placeOrderData = createPlaceOrderData(false);

        Mockito.when(cartModelMock.getPaymentType()).thenReturn(CheckoutPaymentType.CARD);

        testObj.placeOrder(placeOrderData);
    }

    @Test
    public void placeOrderShouldNotPlaceOrderOnReplenishment() throws InvalidCartException {
        PlaceOrderData placeOrderData = createPlaceOrderData(true);

        mockPayByCard();
        Mockito.lenient().when(triggerModelMock.getRelative()).thenReturn(Boolean.TRUE);

        testObj.placeOrder(placeOrderData);

        Mockito.verify(b2BAcceleratorCheckoutFacade, Mockito.times(0)).placeOrder();
    }

    @Test
    public void placeOrderShouldScheduleReplenishment() throws InvalidCartException {
        PlaceOrderData placeOrderData = createPlaceOrderData(true);

        mockPayByCard();
        Mockito.lenient().when(triggerModelMock.getRelative()).thenReturn(Boolean.TRUE);

        testObj.placeOrder(placeOrderData);

        Mockito.verify(b2BAcceleratorCheckoutFacade, Mockito.times(1)).scheduleOrder(Matchers.any());
    }

    protected void mockPayByCard() {
        Mockito.when(cartModelMock.getPaymentType()).thenReturn(CheckoutPaymentType.CARD);
        B2BPaymentTypeData b2BPaymentTypeData = new B2BPaymentTypeData();
        b2BPaymentTypeData.setCode("CARD");
        Mockito.when(cartDataMock.getPaymentType()).thenReturn(b2BPaymentTypeData);
        Mockito.when(cartDataMock.getPaymentInfo()).thenReturn(new CCPaymentInfoData());
    }


    protected PlaceOrderData createPlaceOrderData(final boolean replenishment) {
        PlaceOrderData placeOrderData = new PlaceOrderData();
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
