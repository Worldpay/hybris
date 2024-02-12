package com.worldpay.facade.impl;

import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.order.impl.WorldpayCheckoutFacadeDecorator;
import com.worldpay.facades.payment.hosted.WorldpayHOPNoReturnParamsStrategy;
import com.worldpay.facades.payment.hosted.WorldpayHostedOrderFacade;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.service.hop.WorldpayOrderCodeVerificationService;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOCCWorldpayOrderFacadeTest {
    private static final String ORDER_CODE = "orderCode";
    private static final String USER_ID = "userID";
    private static final String OTHER_USER_ID = "otherUserID";

    @InjectMocks
    private DefaultOCCWorldpayOrderFacade testObj;

    @Mock
    private WorldpayOrderCodeVerificationService worldpayOrderCodeVerificationServiceMock;
    @Mock
    private Converter<Map<String, String>, RedirectAuthoriseResult> redirectAuthoriseResultConverterMock;
    @Mock
    private WorldpayCheckoutFacadeDecorator worldpayCheckoutFacadeDecoratorMock;
    @Mock
    private WorldpayHOPNoReturnParamsStrategy worldpayHOPNoReturnParamsStrategyMock;
    @Mock
    private WorldpayHostedOrderFacade worldpayHostedOrderFacadeMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionService;
    @Mock
    private Converter<AbstractOrderModel, OrderData> orderConverterMock;
    @Mock
    private BaseStoreService baseStoreServiceMock;
    @Mock
    private CustomerAccountService customerAccountServiceMock;

    @Mock
    private RedirectAuthoriseResult redirectAuthoriseResultMock, redirectAuthoriseResultTwoMock;
    @Mock
    private OrderData orderDataMock;
    @Mock
    private PaymentTransactionModel paymentTransactionMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private BaseStoreModel baseStoreModelMock;
    @Mock
    private UserModel userModelMock;


    @Before
    public void setUp() {
        testObj = new DefaultOCCWorldpayOrderFacade(worldpayHOPNoReturnParamsStrategyMock, worldpayPaymentTransactionService,
                orderConverterMock, worldpayCheckoutFacadeDecoratorMock, redirectAuthoriseResultConverterMock,
                worldpayHostedOrderFacadeMock, worldpayOrderCodeVerificationServiceMock);
        when(redirectAuthoriseResultMock.getOrderCode()).thenReturn(ORDER_CODE);

        testObj.setBaseStoreService(baseStoreServiceMock);
        testObj.setCustomerAccountService(customerAccountServiceMock);
    }


    @Test
    public void isValidEncryptedOrderCode_shouldReturnTrue_WhenOrderCodeIsValid(){
        when(worldpayOrderCodeVerificationServiceMock.isValidEncryptedOrderCode(ORDER_CODE)).thenReturn(true);

        final boolean result = testObj.isValidEncryptedOrderCode(ORDER_CODE);

        assertThat(result).isTrue();
    }

    @Test
    public void isValidEncryptedOrderCode_shouldReturnFalse_WhenOrderCodeIsValid(){
        when(worldpayOrderCodeVerificationServiceMock.isValidEncryptedOrderCode(ORDER_CODE)).thenReturn(false);

        final boolean result = testObj.isValidEncryptedOrderCode(ORDER_CODE);

        assertThat(result).isFalse();
    }

    @Test
    public void getRedirectAuthoriseResult_shouldReturnRedirectAuthoriseResult(){
        doReturn(redirectAuthoriseResultMock).when(redirectAuthoriseResultConverterMock).convert(anyMap());

        final RedirectAuthoriseResult result = testObj.getRedirectAuthoriseResult(anyMap());

        assertThat(result).isEqualTo(redirectAuthoriseResultMock);
    }

    @Test
    public void handleHopResponseWithoutPaymentStatus_shouldReturnOrderData_whenCartIsValid() throws WorldpayException, InvalidCartException {
        doReturn(true).when(worldpayCheckoutFacadeDecoratorMock).hasValidCart();
        doReturn(redirectAuthoriseResultTwoMock).when(worldpayHOPNoReturnParamsStrategyMock).authoriseCart();
        doNothing().when(worldpayHostedOrderFacadeMock).completeRedirectAuthorise(redirectAuthoriseResultTwoMock);
        doReturn(orderDataMock).when(worldpayCheckoutFacadeDecoratorMock).placeOrder();

        final OrderData result = testObj.handleHopResponseWithoutPaymentStatus(redirectAuthoriseResultMock);

        assertThat(result).isEqualTo(orderDataMock);
    }

    @Test(expected = WorldpayException.class)
    public void handleHopResponseWithoutPaymentStatus_shouldThrowException_whenCartIsValid() throws WorldpayException, InvalidCartException {
        doReturn(true).when(worldpayCheckoutFacadeDecoratorMock).hasValidCart();
        doReturn(redirectAuthoriseResultTwoMock).when(worldpayHOPNoReturnParamsStrategyMock).authoriseCart();
        doNothing().when(worldpayHostedOrderFacadeMock).completeRedirectAuthorise(redirectAuthoriseResultTwoMock);
        doThrow(InvalidCartException.class).when(worldpayCheckoutFacadeDecoratorMock).placeOrder();

        testObj.handleHopResponseWithoutPaymentStatus(redirectAuthoriseResultMock);
    }

    @Test
    public void handleHopResponseWithoutPaymentStatus_ShouldReturnOrderData_whenCartIsNotValidAndPaymentTransactionExistsAndContainsOrder() throws WorldpayException {
        doReturn(false).when(worldpayCheckoutFacadeDecoratorMock).hasValidCart();
        doReturn(orderDataMock).when(orderConverterMock).convert(orderModelMock);
        when(redirectAuthoriseResultMock.getOrderCode()).thenReturn(ORDER_CODE);
        when(worldpayPaymentTransactionService.getPaymentTransactionFromCode(ORDER_CODE)).thenReturn(paymentTransactionMock);
        when(paymentTransactionMock.getOrder()).thenReturn(orderModelMock);


        final OrderData result = testObj.handleHopResponseWithoutPaymentStatus(redirectAuthoriseResultMock);

        assertThat(result).isEqualTo(orderDataMock);
    }

    @Test(expected = WorldpayException.class)
    public void handleHopResponseWithoutPaymentStatus_ShouldThrowException_whenCartIsNotValidAndPaymentTransactionNotExists() throws WorldpayException {
        doReturn(false).when(worldpayCheckoutFacadeDecoratorMock).hasValidCart();
        when(worldpayPaymentTransactionService.getPaymentTransactionFromCode(ORDER_CODE)).thenReturn(null);

        testObj.handleHopResponseWithoutPaymentStatus(redirectAuthoriseResultMock);
    }


    @Test
    public void handleHopResponseWithPaymentStatus_shouldReturnOrderData_whenCartIsValid() throws WorldpayException, InvalidCartException {
        doReturn(true).when(worldpayCheckoutFacadeDecoratorMock).hasValidCart();
        doNothing().when(worldpayHostedOrderFacadeMock).completeRedirectAuthorise(redirectAuthoriseResultMock);
        doReturn(orderDataMock).when(worldpayCheckoutFacadeDecoratorMock).placeOrder();

        final OrderData result = testObj.handleHopResponseWithPaymentStatus(redirectAuthoriseResultMock);

        assertThat(result).isEqualTo(orderDataMock);
    }

    @Test(expected = WorldpayException.class)
    public void handleHopResponseWithPaymentStatus_shouldThrowException_whenCartIsValid() throws WorldpayException, InvalidCartException {
        doReturn(true).when(worldpayCheckoutFacadeDecoratorMock).hasValidCart();
        doNothing().when(worldpayHostedOrderFacadeMock).completeRedirectAuthorise(redirectAuthoriseResultMock);
        doThrow(InvalidCartException.class).when(worldpayCheckoutFacadeDecoratorMock).placeOrder();

        testObj.handleHopResponseWithPaymentStatus(redirectAuthoriseResultMock);
    }

    @Test
    public void handleHopResponseWithPaymentStatus_ShouldReturnOrderData_whenCartIsNotValidAndPaymentTransactionExistsAndContainsOrder() throws WorldpayException {
        doReturn(orderDataMock).when(orderConverterMock).convert(orderModelMock);
        doReturn(false).when(worldpayCheckoutFacadeDecoratorMock).hasValidCart();
        when(redirectAuthoriseResultMock.getOrderCode()).thenReturn(ORDER_CODE);
        when(worldpayPaymentTransactionService.getPaymentTransactionFromCode(ORDER_CODE)).thenReturn(paymentTransactionMock);
        when(paymentTransactionMock.getOrder()).thenReturn(orderModelMock);

        final OrderData result = testObj.handleHopResponseWithPaymentStatus(redirectAuthoriseResultMock);

        assertThat(result).isEqualTo(orderDataMock);
    }

    @Test(expected = WorldpayException.class)
    public void handleHopResponseWithPaymentStatus_ShouldThrowException_whenCartIsNotValidAndPaymentTransactionNotExists() throws WorldpayException {
        doReturn(false).when(worldpayCheckoutFacadeDecoratorMock).hasValidCart();
        when(worldpayPaymentTransactionService.getPaymentTransactionFromCode(ORDER_CODE)).thenReturn(null);

        testObj.handleHopResponseWithoutPaymentStatus(redirectAuthoriseResultMock);
    }

    @Test(expected = UnknownIdentifierException.class)
    public void findOrderByCodeAndUserId_shouldThrowException_whenOrderIsNotFound() {
        doReturn(baseStoreModelMock).when(baseStoreServiceMock).getCurrentBaseStore();
        when(customerAccountServiceMock.getOrderForCode(ORDER_CODE, baseStoreModelMock)).thenThrow(new UnknownIdentifierException("Order with orderGUID " + ORDER_CODE + " not found in current BaseStore"));

        testObj.findOrderByCodeAndUserId(ORDER_CODE, USER_ID);
    }

    @Test
    public void findOrderByCodeAndUserId_shouldCallOrderConverter_whenOrderIsFoundAndUserMatch() {
        doReturn(baseStoreModelMock).when(baseStoreServiceMock).getCurrentBaseStore();
        doReturn(orderModelMock).when(customerAccountServiceMock).getOrderForCode(ORDER_CODE, baseStoreModelMock);
        when(orderModelMock.getUser()).thenReturn(userModelMock);
        when(userModelMock.getUid()).thenReturn(USER_ID);

        testObj.findOrderByCodeAndUserId(ORDER_CODE, USER_ID);

        verify(orderConverterMock).convert(orderModelMock);
    }

    @Test(expected = UnknownIdentifierException.class)
    public void findOrderByCodeAndUserId_shouldThrowException_whenOrderNotMatchWithUserId() {
        doReturn(baseStoreModelMock).when(baseStoreServiceMock).getCurrentBaseStore();
        doReturn(orderModelMock).when(customerAccountServiceMock).getOrderForCode(ORDER_CODE, baseStoreModelMock);
        doReturn(userModelMock).when(orderModelMock).getUser();
        doReturn(OTHER_USER_ID).when(userModelMock).getUid();

        testObj.findOrderByCodeAndUserId(ORDER_CODE, USER_ID);
    }

}