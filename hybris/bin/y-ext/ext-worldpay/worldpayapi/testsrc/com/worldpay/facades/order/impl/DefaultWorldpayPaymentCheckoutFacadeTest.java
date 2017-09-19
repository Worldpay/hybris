package com.worldpay.facades.order.impl;

import com.worldpay.core.checkout.WorldpayCheckoutService;
import com.worldpay.exception.WorldpayException;
import com.worldpay.hostedorderpage.data.KlarnaRedirectAuthoriseResult;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.AuthorisedStatus;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.request.KlarnaOrderInquiryServiceRequest;
import com.worldpay.service.response.OrderInquiryServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayPaymentCheckoutFacadeTest {

    private static final String ADDRESS_DATA_ID = "0";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String KLARNA_CONTENT_ENCODED = "a2xhcm5hQ29udGVudA==";
    private static final String KLARNA_CONTENT_DECODED = "klarnaContent";

    @Rule
    @SuppressWarnings("PMD.MemberScope")
    public ExpectedException expectedException = ExpectedException.none();

    @Spy
    @InjectMocks
    private DefaultWorldpayPaymentCheckoutFacade testObj;

    @Mock
    private AddressData addressDataMock;
    @Mock
    private CheckoutFacade checkoutFacade;
    @Mock
    private CartService cartService;
    @Mock
    private CartModel cartModelMock;
    @Mock
    private WorldpayCheckoutService worldpayCheckoutServiceMock;
    @Mock
    private AddressModel addressModelMock;
    @Mock
    private DeliveryService deliveryServiceMock;
    @Mock
    private AddressModel paymentAddressMock;
    @Mock
    private WorldpayMerchantInfoService worldpayMerchantInfoServiceMock;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock
    private KlarnaOrderInquiryServiceRequest klarnaOrderInquiryServiceRequestMock;
    @Mock
    private WorldpayServiceGateway worldpayServiceGatewayMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OrderInquiryServiceResponse orderInquiryServiceResponseMock;
    @Mock
    private Amount amountMock;


    @Before
    public void setUp() {
        when(checkoutFacade.hasCheckoutCart()).thenReturn(true);
        when(cartService.getSessionCart()).thenReturn(cartModelMock);
        when(deliveryServiceMock.getSupportedDeliveryAddressesForOrder(cartModelMock, false)).thenReturn(Collections.singletonList(addressModelMock));
    }

    @Test
    public void setBillingDetailsSetsPaymentAddressWhenPkMatchesAddressDataId() {
        when(addressDataMock.getId()).thenReturn(ADDRESS_DATA_ID);
        when(addressModelMock.getPk()).thenReturn(PK.NULL_PK);

        testObj.setBillingDetails(addressDataMock);

        Mockito.verify(worldpayCheckoutServiceMock).setPaymentAddress(cartModelMock, addressModelMock);
    }

    @Test
    public void setBillingDetailsDoesNotSetPaymentAddressWhenPkDoesNotMatchesAddressDataId() {
        when(addressDataMock.getId()).thenReturn(ADDRESS_DATA_ID);
        when(addressModelMock.getPk()).thenReturn(PK.BIG_PK);

        testObj.setBillingDetails(addressDataMock);

        Mockito.verify(worldpayCheckoutServiceMock, never()).setPaymentAddress(cartModelMock, addressModelMock);
    }

    @Test
    public void setBillingDetailsDoesNotSetPaymentAddressWhenNoDeliveryAddressesReturned() {
        when(addressDataMock.getId()).thenReturn(ADDRESS_DATA_ID);
        when(deliveryServiceMock.getSupportedDeliveryAddressesForOrder(cartModelMock, false)).thenReturn(Collections.emptyList());

        testObj.setBillingDetails(addressDataMock);

        Mockito.verify(worldpayCheckoutServiceMock, never()).setPaymentAddress(cartModelMock, addressModelMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setBillingDetailsThrowsIllegalArgumentExceptionWhenAddressDataIdIsNull() {
        when(addressDataMock.getId()).thenReturn(null);
        when(addressModelMock.getPk()).thenReturn(PK.BIG_PK);

        testObj.setBillingDetails(addressDataMock);
    }

    @Test
    public void hasBillingDetailsReturnsFalseWhenCartIsNull() {
        when(cartService.getSessionCart()).thenReturn(null);
        final boolean result = testObj.hasBillingDetails();
        assertFalse(result);
    }

    @Test
    public void hasBillingDetailsReturnsFalseWhenPaymentAddressIsNull() {
        when(cartModelMock.getPaymentAddress()).thenReturn(null);
        final boolean result = testObj.hasBillingDetails();
        assertFalse(result);
    }

    @Test
    public void hasBillingDetailsReturnsTrueWhenCartModelAndPaymentAddressAreNotNull() {
        when(cartModelMock.getPaymentAddress()).thenReturn(paymentAddressMock);
        final boolean result = testObj.hasBillingDetails();
        assertTrue(result);
    }

    @Test
    public void shouldInquiryKlarnaOrderStatusAuthorised() throws WorldpayException {
        when(cartModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(worldpayMerchantInfoServiceMock.getCurrentSiteMerchant(UiExperienceLevel.DESKTOP)).thenReturn(merchantInfoMock);
        doReturn(klarnaOrderInquiryServiceRequestMock).when(testObj).createKlarnaOrderInquiryServiceRequest(merchantInfoMock, WORLDPAY_ORDER_CODE);
        doReturn(worldpayServiceGatewayMock).when(testObj).getWorldpayServiceGateway();
        when(worldpayServiceGatewayMock.orderInquiry(klarnaOrderInquiryServiceRequestMock)).thenReturn(orderInquiryServiceResponseMock);
        when(orderInquiryServiceResponseMock.getReference().getValue()).thenReturn(KLARNA_CONTENT_ENCODED);
        when(orderInquiryServiceResponseMock.getPaymentReply().getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);
        when(orderInquiryServiceResponseMock.getPaymentReply().getAmount()).thenReturn(amountMock);
        when(orderInquiryServiceResponseMock.getPaymentReply().getAmount().getExponent()).thenReturn("2");
        when(amountMock.getValue()).thenReturn("1051");

        final KlarnaRedirectAuthoriseResult result = testObj.checkKlarnaOrderStatus();

        assertThat(result.getPending()).isTrue();
        assertThat(result.getDecodedHTMLContent()).isEqualTo(KLARNA_CONTENT_DECODED);
        assertThat(result.getPaymentAmount()).isEqualTo(BigDecimal.valueOf(10.51D));
    }

    @Test
    public void shouldInquiryKlarnaOrderStatusShopperRedirect() throws WorldpayException {
        when(cartModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(worldpayMerchantInfoServiceMock.getCurrentSiteMerchant(UiExperienceLevel.DESKTOP)).thenReturn(merchantInfoMock);
        doReturn(klarnaOrderInquiryServiceRequestMock).when(testObj).createKlarnaOrderInquiryServiceRequest(merchantInfoMock, WORLDPAY_ORDER_CODE);
        doReturn(worldpayServiceGatewayMock).when(testObj).getWorldpayServiceGateway();
        when(worldpayServiceGatewayMock.orderInquiry(klarnaOrderInquiryServiceRequestMock)).thenReturn(orderInquiryServiceResponseMock);
        when(orderInquiryServiceResponseMock.getReference().getValue()).thenReturn(KLARNA_CONTENT_ENCODED);
        when(orderInquiryServiceResponseMock.getPaymentReply().getAuthStatus()).thenReturn(AuthorisedStatus.SHOPPER_REDIRECTED);
        when(orderInquiryServiceResponseMock.getPaymentReply().getAmount().getValue()).thenReturn("1850");
        when(orderInquiryServiceResponseMock.getPaymentReply().getAmount().getExponent()).thenReturn("2");

        final KlarnaRedirectAuthoriseResult result = testObj.checkKlarnaOrderStatus();

        assertThat(result.getDecodedHTMLContent()).isEqualTo(KLARNA_CONTENT_DECODED);
    }

    @Test
    public void shouldInquiryKlarnaOrderStatusShopperNotAuthorisedOrRedirectWillFail() throws WorldpayException {
        expectedException.expect(WorldpayException.class);
        expectedException.expectMessage("There was a problem placing the order");

        when(cartModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(worldpayMerchantInfoServiceMock.getCurrentSiteMerchant(UiExperienceLevel.DESKTOP)).thenReturn(merchantInfoMock);
        doReturn(klarnaOrderInquiryServiceRequestMock).when(testObj).createKlarnaOrderInquiryServiceRequest(merchantInfoMock, WORLDPAY_ORDER_CODE);
        doReturn(worldpayServiceGatewayMock).when(testObj).getWorldpayServiceGateway();
        when(worldpayServiceGatewayMock.orderInquiry(klarnaOrderInquiryServiceRequestMock)).thenReturn(orderInquiryServiceResponseMock);
        when(orderInquiryServiceResponseMock.getReference().getValue()).thenReturn(KLARNA_CONTENT_ENCODED);
        when(orderInquiryServiceResponseMock.getPaymentReply().getAuthStatus()).thenReturn(AuthorisedStatus.ERROR);

        testObj.checkKlarnaOrderStatus();
    }
}
