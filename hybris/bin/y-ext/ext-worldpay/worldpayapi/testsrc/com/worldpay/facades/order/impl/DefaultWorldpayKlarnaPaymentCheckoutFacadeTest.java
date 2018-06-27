package com.worldpay.facades.order.impl;

import com.worldpay.core.services.OrderInquiryService;
import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.exception.WorldpayException;
import com.worldpay.hostedorderpage.data.KlarnaRedirectAuthoriseResult;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.request.KlarnaOrderInquiryServiceRequest;
import com.worldpay.service.response.OrderInquiryServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayKlarnaPaymentCheckoutFacadeTest {
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String KLARNA_CONTENT_ENCODED = "a2xhcm5hQ29udGVudA==";
    private static final String KLARNA_CONTENT_DECODED = "klarnaContent";

    @InjectMocks
    private DefaultWorldpayKlarnaPaymentCheckoutFacade testObj;

    @Mock
    private CartModel cartModelMock;
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
    @Mock
    private CheckoutFacade checkoutFacade;
    @Mock
    private CartService cartService;
    @Mock
    private OrderInquiryService orderInquiryServiceMock;

    @Rule
    @SuppressWarnings("PMD.MemberScope")
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws WorldpayException {
        when(checkoutFacade.hasCheckoutCart()).thenReturn(true);
        when(cartService.getSessionCart()).thenReturn(cartModelMock);
        when(cartModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(orderInquiryServiceMock.inquiryKlarnaOrder(merchantInfoMock, WORLDPAY_ORDER_CODE)).thenReturn(orderInquiryServiceResponseMock);
    }

    @Test
    public void shouldInquiryKlarnaOrderStatusAuthorised() throws WorldpayException {
        when(worldpayMerchantInfoServiceMock.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
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
        when(worldpayMerchantInfoServiceMock.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
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

        when(worldpayMerchantInfoServiceMock.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        when(worldpayServiceGatewayMock.orderInquiry(klarnaOrderInquiryServiceRequestMock)).thenReturn(orderInquiryServiceResponseMock);
        when(orderInquiryServiceResponseMock.getReference().getValue()).thenReturn(KLARNA_CONTENT_ENCODED);
        when(orderInquiryServiceResponseMock.getPaymentReply().getAuthStatus()).thenReturn(AuthorisedStatus.ERROR);

        testObj.checkKlarnaOrderStatus();
    }

}
