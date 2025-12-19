package com.worldpay.worldpayextocc.controllers;

import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facade.OCCWorldpayOrderFacade;
import com.worldpay.facades.order.impl.WorldpayCheckoutFacadeDecorator;
import com.worldpay.facades.payment.hosted.WorldpayAfterRedirectValidationFacade;
import com.worldpay.facades.payment.hosted.WorldpayHostedOrderFacade;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderWsDTO;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayOrdersControllerTest {

    private static final String ENCRYPTED_ORDER_ID = "encryptedOrderId";
    private static final String ANONYMOUS_UID = "anonymous";
    private static final String ORDER_CODE = "orderCode";
    private static final String USER_ID = "userID";
    private static final String PAYMENT_STATUS_PARAMETER_NAME = "paymentStatus";
    private static final String PENDING_PARAM_NAME = "pending";

    @InjectMocks
    private WorldpayOrdersController testObj;

    @Mock
    private WorldpayCheckoutFacadeDecorator worldpayCheckoutFacadeDecoratorMock;
    @Mock
    private WorldpayHostedOrderFacade worldpayHostedOrderFacadeMock;
    @Mock
    private OCCWorldpayOrderFacade occWorldpayOrderFacadeMock;
    @Mock
    private WorldpayAfterRedirectValidationFacade worldpayAfterRedirectValidationFacadeMock;
    @Mock
    protected UserFacade worldpayUserFacade;

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

    private final MockHttpServletRequest httpServletRequestMock = new MockHttpServletRequest();

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
        when(worldpayCheckoutFacadeDecoratorMock.placeOrder()).thenReturn(orderDataMock);
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
        when(worldpayCheckoutFacadeDecoratorMock.placeOrder()).thenReturn(orderDataMock);
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
    public void placeBankTransferRedirectOrder_shouldReturnOrderWsDTO() throws InvalidCartException, WorldpayException {
        when(occWorldpayOrderFacadeMock.isValidEncryptedOrderCode(ENCRYPTED_ORDER_ID)).thenReturn(true);
        doReturn(redirectAuthoriseResultMock).when(occWorldpayOrderFacadeMock).getRedirectAuthoriseResult(anyMap());
        doNothing().when(worldpayHostedOrderFacadeMock).completeRedirectAuthorise(any());
        when(worldpayCheckoutFacadeDecoratorMock.placeOrder()).thenReturn(orderDataMock);
        lenient().when(dataMapperMock.map(orderDataMock, OrderWsDTO.class)).thenReturn(orderWsDTOMock);

        testObj.placeBankTransferRedirectOrder(httpServletRequestMock, ENCRYPTED_ORDER_ID, FieldSetLevelHelper.DEFAULT_LEVEL);

        verify(dataMapperMock).map(orderDataMock, OrderWsDTO.class, FieldSetLevelHelper.DEFAULT_LEVEL);
    }

    @Test(expected = WorldpayException.class)
    public void placeBankTransferRedirectOrder_shouldThrowException_whenIsNotValidEncryptedOrderCode() throws WorldpayException {
        when(occWorldpayOrderFacadeMock.isValidEncryptedOrderCode(ENCRYPTED_ORDER_ID)).thenReturn(false);

        testObj.placeBankTransferRedirectOrder(httpServletRequestMock, ENCRYPTED_ORDER_ID, FieldSetLevelHelper.DEFAULT_LEVEL);
    }

    @Test(expected = WorldpayException.class)
    public void placeBankTransferRedirectOrder_shouldThrowException_whenCartIsInvalid() throws InvalidCartException, WorldpayException {
        when(occWorldpayOrderFacadeMock.isValidEncryptedOrderCode(ENCRYPTED_ORDER_ID)).thenReturn(true);
        doReturn(redirectAuthoriseResultMock).when(occWorldpayOrderFacadeMock).getRedirectAuthoriseResult(anyMap());
        doNothing().when(worldpayHostedOrderFacadeMock).completeRedirectAuthorise(any());
        when(worldpayCheckoutFacadeDecoratorMock.placeOrder()).thenThrow(new InvalidCartException("Chan chan"));
        lenient().when(dataMapperMock.map(orderDataMock, OrderWsDTO.class)).thenReturn(orderWsDTOMock);

        testObj.placeBankTransferRedirectOrder(httpServletRequestMock, ENCRYPTED_ORDER_ID, FieldSetLevelHelper.DEFAULT_LEVEL);
    }

    @Test
    public void getUserOrders_shouldReturnOrder_WhenUserAndOrderExists() {
        when(worldpayUserFacade.isUserExisting(USER_ID)).thenReturn(true);
        when(occWorldpayOrderFacadeMock.findOrderByCodeAndUserId(ORDER_CODE, USER_ID)).thenReturn(orderDataMock);
        lenient().when(dataMapperMock.map(orderDataMock, OrderWsDTO.class)).thenReturn(orderWsDTOMock);

        testObj.getUserOrders(USER_ID,ORDER_CODE, FieldSetLevelHelper.DEFAULT_LEVEL);

        verify(dataMapperMock).map(orderDataMock, OrderWsDTO.class, FieldSetLevelHelper.DEFAULT_LEVEL);
    }

    @Test(expected = AccessDeniedException.class)
    public void getUserOrders_shouldThrowException_WhenUserIsAnonymous() {
        testObj.getUserOrders(ANONYMOUS_UID,ORDER_CODE, FieldSetLevelHelper.DEFAULT_LEVEL);
    }

    @Test(expected = AccessDeniedException.class)
    public void getUserOrders_shouldThrowException_WhenUserIsNotExists() {
        when(worldpayUserFacade.isUserExisting(USER_ID)).thenReturn(false);

        testObj.getUserOrders(USER_ID,ORDER_CODE, FieldSetLevelHelper.DEFAULT_LEVEL);
    }

    @Test(expected = UnknownIdentifierException.class)
    public void getUserOrders_shouldThrowException_WhenOrderIsNotFound() {
        when(worldpayUserFacade.isUserExisting(USER_ID)).thenReturn(true);
        when(occWorldpayOrderFacadeMock.findOrderByCodeAndUserId(ORDER_CODE, USER_ID))
                .thenThrow(new UnknownIdentifierException("Order with orderGUID " + ORDER_CODE + " not found in current BaseStore"));

        testObj.getUserOrders(USER_ID,ORDER_CODE, FieldSetLevelHelper.DEFAULT_LEVEL);
    }
}
