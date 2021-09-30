package com.worldpay.strategies.impl;

import com.worldpay.core.services.OrderNotificationService;
import com.worldpay.notification.processors.WorldpayOrderNotificationHandler;
import com.worldpay.data.Amount;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.payment.WorldpayRedirectOrderService;
import com.worldpay.worldpaynotifications.enums.DefectiveReason;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

import static java.math.BigDecimal.TEN;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayPlaceOrderFromNotificationStrategyTest {

    private static final String SERIALIZED_JSON_STRING = "Serialized json string";
    private static final String MERCHANT_CODE = "MerchantCode";

    @InjectMocks
    private DefaultWorldpayPlaceOrderFromNotificationStrategy testObj;

    @Mock
    private OrderNotificationService orderNotificationServiceMock;
    @Mock
    private WorldpayOrderService worldpayOrderServiceMock;
    @Mock
    private CartService cartServiceMock;
    @Mock
    private CommerceCheckoutService commerceCheckoutServiceMock;
    @Mock
    private WorldpayOrderNotificationHandler worldpayOrderNotificationHandlerMock;
    @Mock
    private WorldpayRedirectOrderService worldpayRedirectOrderServiceMock;
    @Mock
    private ModelService modelServiceMock;

    @Mock
    private WorldpayOrderModificationModel worldpayOrderModificationModelMock;
    @Mock
    private CartModel cartModelMock;
    @Captor
    private ArgumentCaptor<CommerceCheckoutParameter> commerceCheckoutParameterCaptor;
    @Mock
    private CommerceOrderResult commerceOrderResultMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OrderNotificationMessage orderNotificationMessageMock;
    @Mock
    private Amount amountMock;
    @Mock
    private OrderModel orderModelMock;

    @Before
    public void setUp() {
        Whitebox.setInternalState(testObj, "impersonationService", new TestImpersonationService());
        when(worldpayOrderModificationModelMock.getOrderNotificationMessage()).thenReturn(SERIALIZED_JSON_STRING);
        when(orderNotificationServiceMock.deserialiseNotification(SERIALIZED_JSON_STRING)).thenReturn(orderNotificationMessageMock);
    }

    @Test
    public void placeOrderFromNotification() throws InvalidCartException {
        when(orderNotificationMessageMock.getPaymentReply().getAmount()).thenReturn(amountMock);
        when(commerceCheckoutServiceMock.placeOrder(commerceCheckoutParameterCaptor.capture())).thenReturn(commerceOrderResultMock);
        when(commerceOrderResultMock.getOrder()).thenReturn(orderModelMock);
        when(orderNotificationMessageMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(worldpayOrderServiceMock.convertAmount(amountMock)).thenReturn(TEN);

        testObj.placeOrderFromNotification(worldpayOrderModificationModelMock, cartModelMock);

        final InOrder inOrder = Mockito.inOrder(cartServiceMock, worldpayRedirectOrderServiceMock, worldpayOrderNotificationHandlerMock, orderNotificationServiceMock);
        inOrder.verify(cartServiceMock).setSessionCart(cartModelMock);
        inOrder.verify(worldpayRedirectOrderServiceMock).completeConfirmedRedirectAuthorise(TEN, MERCHANT_CODE, cartModelMock);
        inOrder.verify(orderNotificationServiceMock).setNonDefectiveAndProcessed(worldpayOrderModificationModelMock);

        verify(modelServiceMock).refresh(orderModelMock);
        verify(cartServiceMock).removeSessionCart();

        final CommerceCheckoutParameter value = commerceCheckoutParameterCaptor.getValue();
        assertThat(value.isEnableHooks()).isTrue();
        assertThat(value.getCart()).isEqualTo(cartModelMock);
    }

    @Test
    public void placeOrderFromNotificationShouldMarkNotificationAsDefectiveWhenThereIsAnErrorPlacingTheOrder() throws InvalidCartException {
        when(orderNotificationMessageMock.getPaymentReply().getAmount()).thenReturn(amountMock);
        when(commerceCheckoutServiceMock.placeOrder(commerceCheckoutParameterCaptor.capture())).thenThrow(new InvalidCartException("Invalid cart exception"));
        when(commerceOrderResultMock.getOrder()).thenReturn(orderModelMock);
        when(orderNotificationMessageMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(worldpayOrderServiceMock.convertAmount(amountMock)).thenReturn(TEN);

        testObj.placeOrderFromNotification(worldpayOrderModificationModelMock, cartModelMock);

        verify(orderNotificationServiceMock).setDefectiveReason(worldpayOrderModificationModelMock, DefectiveReason.ERROR_PLACING_ORDER);
    }

    private class TestImpersonationService implements ImpersonationService {
        @Override
        public <R, T extends Throwable> R executeInContext(final ImpersonationContext context, final Executor<R, T> wrapper) throws T {
            return wrapper.execute();
        }
    }
}
