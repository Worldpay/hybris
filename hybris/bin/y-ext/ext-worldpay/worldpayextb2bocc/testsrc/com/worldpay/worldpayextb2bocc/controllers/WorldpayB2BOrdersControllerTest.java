package com.worldpay.worldpayextb2bocc.controllers;

import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.order.impl.WorldpayB2BAcceleratorCheckoutFacadeDecorator;
import com.worldpay.facades.payment.hosted.WorldpayAfterRedirectValidationFacade;
import com.worldpay.facades.payment.hosted.WorldpayHostedOrderFacade;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.worldpayocccommons.facade.OCCWorldpayOrderFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade;
import de.hybris.platform.b2bocc.v2.requestfrom.RequestFromValueSetter;
import de.hybris.platform.b2bocc.v2.skipfield.SkipReplenishmentOrderFieldValueSetter;
import de.hybris.platform.b2bwebservicescommons.dto.order.ReplenishmentOrderWsDTO;
import de.hybris.platform.b2bwebservicescommons.dto.order.ScheduleReplenishmentFormWsDTO;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.commercewebservicescommons.strategies.CartLoaderStrategy;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.access.AccessDeniedException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayB2BOrdersControllerTest {

    private static final String ANONYMOUS_UID = "anonymous";
    private static final String ORDER_CODE = "orderCode";
    private static final String USER_ID = "userID";
    private static final String PAYMENT_STATUS_PARAMETER_NAME = "paymentStatus";
    private static final String PENDING_PARAM_NAME = "pending";
    private static final String CART_ID = "cartId";

    @Spy
    @InjectMocks
    private WorldpayB2BOrdersController testObj;

    @Mock
    protected UserFacade worldpayUserFacadeMock;
    @Mock
    protected WorldpayB2BAcceleratorCheckoutFacadeDecorator worldpayB2BCheckoutFacadeDecoratorMock;
    @Mock
    private WorldpayHostedOrderFacade worldpayHostedOrderFacadeMock;
    @Mock
    private OCCWorldpayOrderFacade occWorldpayOrderFacadeMock;
    @Mock
    private WorldpayAfterRedirectValidationFacade worldpayAfterRedirectValidationFacadeMock;
    @Mock
    private SkipReplenishmentOrderFieldValueSetter skipReplenishmentOrderFieldValueSetterMock;
    @Mock
    private RequestFromValueSetter requestFromValueSetterMock;
    @Mock
    private CartLoaderStrategy cartLoaderStrategyMock;

    @Mock
    private OrderData orderDataMock;
    @Mock
    private DataMapper dataMapperMock;
    @Mock
    private OrderWsDTO orderWsDTOMock;
    @Mock
    private RedirectAuthoriseResult redirectAuthoriseResultMock;
    @Mock
    private Set<AuthorisedStatus> apmErrorResponseStatusesMock;
    @Mock
    private Map<String, String> requestMapMock;
    @Mock
    private CartFacade cartFacadeMock;
    @Mock
    private CartData cartDataMock;
    @Mock
    private ScheduleReplenishmentFormWsDTO scheduleReplenishmentFormMock;
    @Mock
    private ReplenishmentOrderWsDTO replenishmentOrderWsDTOMock;

    @Before
    public void setUp() {
        when(cartFacadeMock.getCurrentCart()).thenReturn(cartDataMock);
        when(dataMapperMock.map(any(), eq(ReplenishmentOrderWsDTO.class), anyString())).thenReturn(replenishmentOrderWsDTOMock);
    }

    @Test(expected = WorldpayException.class)
    public void placeRedirectOrder_shouldThrowException_whenContainsPaymentStatusAndRedirectResponseIsNotValid() throws WorldpayException, InvalidCartException {
        doReturn(redirectAuthoriseResultMock).when(occWorldpayOrderFacadeMock).getRedirectAuthoriseResult(anyMap());
        when(worldpayAfterRedirectValidationFacadeMock.validateRedirectResponse(anyMap())).thenReturn(false);
        when(requestMapMock.get(PENDING_PARAM_NAME)).thenReturn(Boolean.TRUE.toString());

        testObj.placeRedirectOrder(requestMapMock, FieldSetLevelHelper.DEFAULT_LEVEL);
    }

    @Test
    public void placeRedirectOrder_ShouldReturnOrderWsDTO_whenRequestIsPendingNotContainsPaymentStatus() throws WorldpayException, InvalidCartException {
        when(worldpayAfterRedirectValidationFacadeMock.validateRedirectResponse(anyMap())).thenReturn(true);
        doReturn(redirectAuthoriseResultMock).when(occWorldpayOrderFacadeMock).getRedirectAuthoriseResult(anyMap());
        when(worldpayB2BCheckoutFacadeDecoratorMock.placeOrder(any())).thenReturn(orderDataMock);
        lenient().when(dataMapperMock.map(orderDataMock, OrderWsDTO.class)).thenReturn(orderWsDTOMock);
        when(requestMapMock.get(PENDING_PARAM_NAME)).thenReturn(Boolean.TRUE.toString());

        testObj.placeRedirectOrder(requestMapMock, FieldSetLevelHelper.DEFAULT_LEVEL);

        verify(dataMapperMock).map(orderDataMock, OrderWsDTO.class, FieldSetLevelHelper.DEFAULT_LEVEL);

    }

    @Test
    public void placeRedirectOrder_ShouldReturnOrderWsDTO_whenRequestIsNotPendingAndNotContainsPaymentStatus() throws WorldpayException, InvalidCartException {
        doReturn(redirectAuthoriseResultMock).when(occWorldpayOrderFacadeMock).getRedirectAuthoriseResult(anyMap());
        when(occWorldpayOrderFacadeMock.handleHopResponseWithoutPaymentStatus(redirectAuthoriseResultMock)).thenReturn(orderDataMock);

        lenient().when(dataMapperMock.map(orderDataMock, OrderWsDTO.class)).thenReturn(orderWsDTOMock);
        final Map<String, String> requestMap = Map.of(PENDING_PARAM_NAME, Boolean.FALSE.toString());

        testObj.placeRedirectOrder(requestMap, FieldSetLevelHelper.DEFAULT_LEVEL);

        verify(dataMapperMock).map(orderDataMock, OrderWsDTO.class, FieldSetLevelHelper.DEFAULT_LEVEL);

    }

    @Test
    public void placeRedirectOrder_ShouldReturnOrderWsDTO_whenRequestIsNotPendingAndContainsPaymentStatus() throws WorldpayException, InvalidCartException {
        doReturn(redirectAuthoriseResultMock).when(occWorldpayOrderFacadeMock).getRedirectAuthoriseResult(anyMap());
        when(worldpayAfterRedirectValidationFacadeMock.validateRedirectResponse(anyMap())).thenReturn(true);
        when(occWorldpayOrderFacadeMock.handleHopResponseWithPaymentStatus(redirectAuthoriseResultMock)).thenReturn(orderDataMock);
        lenient().when(dataMapperMock.map(orderDataMock, OrderWsDTO.class)).thenReturn(orderWsDTOMock);
        final Map<String, String> requestMap = new HashMap<>();
        requestMap.put(PENDING_PARAM_NAME, Boolean.FALSE.toString());
        requestMap.put(PAYMENT_STATUS_PARAMETER_NAME, AuthorisedStatus.OPEN.name());

        testObj.placeRedirectOrder(requestMap, FieldSetLevelHelper.DEFAULT_LEVEL);

        verify(dataMapperMock).map(orderDataMock, OrderWsDTO.class, FieldSetLevelHelper.DEFAULT_LEVEL);

    }

    @Test
    public void placeRedirectOrder_ShouldReturnOrderWsDTO_whenRequestIsPendingContainsPaymentStatusAndRedirectResponseIsValid() throws WorldpayException, InvalidCartException {
        when(occWorldpayOrderFacadeMock.getRedirectAuthoriseResult(anyMap())).thenReturn(redirectAuthoriseResultMock);
        when(worldpayAfterRedirectValidationFacadeMock.validateRedirectResponse(anyMap())).thenReturn(true);
        when(worldpayB2BCheckoutFacadeDecoratorMock.placeOrder(any())).thenReturn(orderDataMock);
        lenient().when(dataMapperMock.map(orderDataMock, OrderWsDTO.class)).thenReturn(orderWsDTOMock);
        when(requestMapMock.get(PENDING_PARAM_NAME)).thenReturn(Boolean.TRUE.toString());

        testObj.placeRedirectOrder(requestMapMock, FieldSetLevelHelper.DEFAULT_LEVEL);

        verify(dataMapperMock).map(orderDataMock, OrderWsDTO.class, FieldSetLevelHelper.DEFAULT_LEVEL);
    }

    @Test(expected = WorldpayException.class)
    public void placeRedirectOrder_ShouldThrowException_whenRequestIsPendingAndContainsPaymentStatusErrorStatus() throws WorldpayException, InvalidCartException {
        when(occWorldpayOrderFacadeMock.getRedirectAuthoriseResult(anyMap())).thenReturn(redirectAuthoriseResultMock);
        when(worldpayAfterRedirectValidationFacadeMock.validateRedirectResponse(anyMap())).thenReturn(true);
        when(apmErrorResponseStatusesMock.contains(AuthorisedStatus.ERROR)).thenReturn(true);
        when(redirectAuthoriseResultMock.getPaymentStatus()).thenReturn(AuthorisedStatus.ERROR);
        when(requestMapMock.get(PENDING_PARAM_NAME)).thenReturn(Boolean.TRUE.toString());

        testObj.placeRedirectOrder(requestMapMock, FieldSetLevelHelper.DEFAULT_LEVEL);
    }


    @Test
    public void getUserOrders_shouldReturnOrder_WhenUserAndOrderExists() {
        when(worldpayUserFacadeMock.isUserExisting(USER_ID)).thenReturn(true);
        when(occWorldpayOrderFacadeMock.findOrderByCodeAndUserId(ORDER_CODE, USER_ID)).thenReturn(orderDataMock);
        lenient().when(dataMapperMock.map(orderDataMock, OrderWsDTO.class)).thenReturn(orderWsDTOMock);

        testObj.getUserOrders(USER_ID, ORDER_CODE, FieldSetLevelHelper.DEFAULT_LEVEL);

        verify(dataMapperMock).map(orderDataMock, OrderWsDTO.class, FieldSetLevelHelper.DEFAULT_LEVEL);
    }

    @Test(expected = AccessDeniedException.class)
    public void getUserOrders_shouldThrowException_WhenUserIsAnonymous() {
        testObj.getUserOrders(ANONYMOUS_UID, ORDER_CODE, FieldSetLevelHelper.DEFAULT_LEVEL);
    }

    @Test(expected = AccessDeniedException.class)
    public void getUserOrders_shouldThrowException_WhenUserIsNotExists() {
        when(worldpayUserFacadeMock.isUserExisting(USER_ID)).thenReturn(false);

        testObj.getUserOrders(USER_ID, ORDER_CODE, FieldSetLevelHelper.DEFAULT_LEVEL);
    }

    @Test(expected = UnknownIdentifierException.class)
    public void getUserOrders_shouldThrowException_WhenOrderIsNotFound() {
        when(worldpayUserFacadeMock.isUserExisting(USER_ID)).thenReturn(true);
        when(occWorldpayOrderFacadeMock.findOrderByCodeAndUserId(ORDER_CODE, USER_ID))
                .thenThrow(new UnknownIdentifierException("Order with orderGUID " + ORDER_CODE + " not found in current BaseStore"));

        testObj.getUserOrders(USER_ID, ORDER_CODE, FieldSetLevelHelper.DEFAULT_LEVEL);
    }


    @Test
    public void createReplenishmentOrder_ShouldReturnDTO_WhenValidInput() throws InvalidCartException {
        doNothing().when(testObj).validateCart(cartDataMock);
        doNothing().when(testObj).validateScheduleReplenishmentForm(scheduleReplenishmentFormMock);

        final ReplenishmentOrderWsDTO result = testObj.createReplenishmentOrder(CART_ID, Boolean.TRUE, scheduleReplenishmentFormMock, FieldSetLevelHelper.DEFAULT_LEVEL);

        assertThat(result).isEqualTo(replenishmentOrderWsDTOMock);
        verify(cartFacadeMock).getCurrentCart();
        verify(dataMapperMock).map(any(), eq(ReplenishmentOrderWsDTO.class), eq(FieldSetLevelHelper.DEFAULT_LEVEL));
    }

    @Test
    public void createReplenishmentOrder_ShouldThrowRequestParameterException_WhenTermsNotChecked() {
        doReturn("cart.term.unchecked").when(testObj).callSuperGetLocalizedString("cart.term.unchecked");

        assertThatThrownBy(() -> testObj.createReplenishmentOrder(CART_ID, Boolean.FALSE, scheduleReplenishmentFormMock, FieldSetLevelHelper.DEFAULT_LEVEL))
                .isInstanceOf(RequestParameterException.class);
    }

    @Test
    public void createReplenishmentOrder_ShouldThrowWebserviceValidationException_WhenCartIsInvalid() {
        doThrow(new WebserviceValidationException("cart invalid")).when(cartFacadeMock).getCurrentCart();

        assertThatThrownBy(() -> testObj.createReplenishmentOrder(CART_ID, Boolean.TRUE, scheduleReplenishmentFormMock, FieldSetLevelHelper.DEFAULT_LEVEL))
                .isInstanceOf(WebserviceValidationException.class);
    }

}
