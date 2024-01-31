package com.worldpay.worldpayextocc.controllers;

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

import static com.worldpay.enums.order.AuthorisedStatus.AUTHORISED;
import static com.worldpay.enums.order.AuthorisedStatus.ERROR;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayOrdersControllerTest {

    private static final String ENCRYPTED_ORDER_ID = "encryptedOrderId";
    private static final String PAYMENT_STATUS = "paymentStatus";
    private static final String ANONYMOUS_UID = "anonymous";
    private static final String ORDER_CODE = "orderCode";
    private static final String USER_ID = "userID";

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

    private final MockHttpServletRequest httpServletRequestMock = new MockHttpServletRequest();

    @Test(expected = WorldpayException.class)
    public void placeRedirectOrder_shouldThrowException_whenContainsPaymentStatusAndRedirectResponseIsNotValid() throws InvalidCartException, WorldpayException {
        httpServletRequestMock.setParameter(PAYMENT_STATUS, ERROR.name());
        doReturn(redirectAuthoriseResultMock).when(occWorldpayOrderFacadeMock).getRedirectAuthoriseResult(anyMap());
        when(worldpayAfterRedirectValidationFacadeMock.validateRedirectResponse(anyMap())).thenReturn(false);

        testObj.placeRedirectOrder(httpServletRequestMock, FieldSetLevelHelper.DEFAULT_LEVEL);
    }

    @Test
    public void placeRedirectOrder_ShouldReturnOrderWsDTO_whenRequestNotContainsPaymentStatus() throws InvalidCartException, WorldpayException {
        doReturn(redirectAuthoriseResultMock).when(occWorldpayOrderFacadeMock).getRedirectAuthoriseResult(anyMap());
        when(occWorldpayOrderFacadeMock.getRedirectAuthoriseResult(anyMap())).thenReturn(redirectAuthoriseResultMock);
        when(occWorldpayOrderFacadeMock.handleHopResponseWithoutPaymentStatus(redirectAuthoriseResultMock)).thenReturn(orderDataMock);
        lenient().when(dataMapperMock.map(orderDataMock, OrderWsDTO.class)).thenReturn(orderWsDTOMock);

        testObj.placeRedirectOrder(httpServletRequestMock, FieldSetLevelHelper.DEFAULT_LEVEL);

        verify(dataMapperMock).map(orderDataMock, OrderWsDTO.class, FieldSetLevelHelper.DEFAULT_LEVEL);

    }

    @Test
    public void placeRedirectOrder_ShouldReturnOrderWsDTO_whenRequestContainsPaymentStatusAndRedirectResponseIsValid() throws InvalidCartException, WorldpayException {
        httpServletRequestMock.setParameter(PAYMENT_STATUS, AUTHORISED.name());
        when(occWorldpayOrderFacadeMock.getRedirectAuthoriseResult(anyMap())).thenReturn(redirectAuthoriseResultMock);
        when(worldpayAfterRedirectValidationFacadeMock.validateRedirectResponse(anyMap())).thenReturn(true);
        when(occWorldpayOrderFacadeMock.handleHopResponseWithPaymentStatus(redirectAuthoriseResultMock)).thenReturn(orderDataMock);
        lenient().when(dataMapperMock.map(orderDataMock, OrderWsDTO.class)).thenReturn(orderWsDTOMock);

        testObj.placeRedirectOrder(httpServletRequestMock, FieldSetLevelHelper.DEFAULT_LEVEL);

        verify(dataMapperMock).map(orderDataMock, OrderWsDTO.class, FieldSetLevelHelper.DEFAULT_LEVEL);
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
    public void placeBankTransferRedirectOrder_shouldThrowException_whenIsNotValidEncryptedOrderCode() throws InvalidCartException, WorldpayException {
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
