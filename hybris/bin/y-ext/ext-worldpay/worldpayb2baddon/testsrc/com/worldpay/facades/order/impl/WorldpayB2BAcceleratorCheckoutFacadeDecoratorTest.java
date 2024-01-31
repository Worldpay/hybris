package com.worldpay.facades.order.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bacceleratorservices.enums.CheckoutPaymentType;
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
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

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
    @Mock
    private B2BPaymentTypeData b2BPaymentTypeDataMock;

    @Before
    public void setUp() {
        CustomerModel customerModelMock = mock(CustomerModel.class);

        when(cartFacadeMock.hasSessionCart()).thenReturn(true);
        when(cartFacadeMock.getSessionCart()).thenReturn(cartDataMock);
        when(cartDataMock.isCalculated()).thenReturn(true);
        when(cartDataMock.getDeliveryAddress()).thenReturn(mock(AddressData.class));
        when(cartDataMock.getDeliveryMode()).thenReturn(mock(DeliveryModeData.class));
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(b2BPaymentTypeDataMock.getCode()).thenReturn("CARD");
        doReturn(Collections.singletonList(b2BPaymentTypeDataMock)).when(testObj).getPaymentTypes();
        when(cartModelMock.getUser()).thenReturn(customerModelMock);
        when(checkoutCustomerStrategy.getCurrentUserForCheckout()).thenReturn(customerModelMock);

        when(i18NService.getCurrentTimeZone()).thenReturn(TimeZone.getDefault());
        when(i18NService.getCurrentLocale()).thenReturn(Locale.getDefault());
        when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSiteModelMock);
        when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModelMock);

        when(modelService.create(TriggerModel.class)).thenReturn(triggerModelMock);
        when(modelService.create(B2BCommentModel.class)).thenReturn(b2BCommentModelMock);
    }

    @Test
    public void placeOrderShouldPlaceOrderOnBuyNowWhenAuthorized() throws InvalidCartException {
        PlaceOrderData placeOrderData = createPlaceOrderData(false);
        PaymentTransactionEntryModel paymentTransactionEntryModelMock = mock(PaymentTransactionEntryModel.class);
        PaymentTransactionModel paymentTransactionModelMock = mock(PaymentTransactionModel.class);

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
        PlaceOrderData placeOrderData = createPlaceOrderData(false);

        when(cartModelMock.getPaymentType()).thenReturn(CheckoutPaymentType.CARD);

        testObj.placeOrder(placeOrderData);
    }

    @Test
    public void placeOrderShouldNotPlaceOrderOnReplenishment() throws InvalidCartException {
        PlaceOrderData placeOrderData = createPlaceOrderData(true);

        mockPayByCard();
        when(triggerModelMock.getRelative()).thenReturn(Boolean.TRUE);

        testObj.placeOrder(placeOrderData);

        verify(b2BAcceleratorCheckoutFacade, times(0)).placeOrder();
    }

    @Test
    public void placeOrderShouldScheduleReplenishment() throws InvalidCartException {
        PlaceOrderData placeOrderData = createPlaceOrderData(true);

        mockPayByCard();
        when(triggerModelMock.getRelative()).thenReturn(Boolean.TRUE);

        testObj.placeOrder(placeOrderData);

        verify(b2BAcceleratorCheckoutFacade, times(1)).scheduleOrder(Matchers.any());
    }

    protected void mockPayByCard() {
        when(cartModelMock.getPaymentType()).thenReturn(CheckoutPaymentType.CARD);
        B2BPaymentTypeData b2BPaymentTypeData = new B2BPaymentTypeData();
        b2BPaymentTypeData.setCode("CARD");
        when(cartDataMock.getPaymentType()).thenReturn(b2BPaymentTypeData);
        when(cartDataMock.getPaymentInfo()).thenReturn(new CCPaymentInfoData());
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
