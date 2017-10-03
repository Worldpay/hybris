package com.worldpay.facades.order.impl;

import com.worldpay.core.services.OrderInquiryService;
import com.worldpay.core.services.impl.DefaultOrderInquiryService;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.order.WorldpayKlarnaPaymentCheckoutFacade;
import com.worldpay.hostedorderpage.data.KlarnaRedirectAuthoriseResult;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.AuthorisedStatus;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.request.KlarnaOrderInquiryServiceRequest;
import com.worldpay.service.response.OrderInquiryServiceResponse;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import org.springframework.beans.factory.annotation.Required;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Klarna checkout facade to perform specific operations required by the APM
 */
public class DefaultWorldpayKlarnaPaymentCheckoutFacade implements WorldpayKlarnaPaymentCheckoutFacade {

    private CheckoutFacade checkoutFacade;
    private CartService cartService;
    private WorldpayMerchantInfoService worldpayMerchantInfoService;
    private OrderInquiryService orderInquiryService;

    /**
     * {@inheritDoc}
     */
    @Override
    public KlarnaRedirectAuthoriseResult checkKlarnaOrderStatus() throws WorldpayException {
        final CartModel cartModel = getCart();
        final MerchantInfo merchantConfig = worldpayMerchantInfoService.getCurrentSiteMerchant();
        final OrderInquiryServiceResponse serviceResponse = orderInquiryService.inquiryKlarnaOrder(merchantConfig, cartModel.getWorldpayOrderCode());
        final AuthorisedStatus authStatus = serviceResponse.getPaymentReply().getAuthStatus();

        if (AuthorisedStatus.AUTHORISED.equals(authStatus) || AuthorisedStatus.SHOPPER_REDIRECTED.equals(authStatus)) {
            final KlarnaRedirectAuthoriseResult klarnaRedirectAuthoriseResult = new KlarnaRedirectAuthoriseResult();
            klarnaRedirectAuthoriseResult.setPending(true);
            final int paymentExponent = Integer.parseInt(serviceResponse.getPaymentReply().getAmount().getExponent());
            final String paymentAmount = serviceResponse.getPaymentReply().getAmount().getValue();
            klarnaRedirectAuthoriseResult.setPaymentAmount(new BigDecimal(paymentAmount).movePointLeft(paymentExponent));
            final String decodedHtmlContent = new String(Base64.getDecoder().decode(serviceResponse.getReference().getValue()), StandardCharsets.UTF_8);
            klarnaRedirectAuthoriseResult.setDecodedHTMLContent(decodedHtmlContent);

            return klarnaRedirectAuthoriseResult;
        } else {
            throw new WorldpayException("There was a problem placing the order");
        }
    }

    protected CartModel getCart() {
        return checkoutFacade.hasCheckoutCart() ? cartService.getSessionCart() : null;
    }

    @Required
    public void setCheckoutFacade(final CheckoutFacade checkoutFacade) {
        this.checkoutFacade = checkoutFacade;
    }

    @Required
    public void setCartService(final CartService cartService) {
        this.cartService = cartService;
    }

    @Required
    public void setWorldpayMerchantInfoService(final WorldpayMerchantInfoService worldpayMerchantInfoService) {
        this.worldpayMerchantInfoService = worldpayMerchantInfoService;
    }

    @Required
    public void setOrderInquiryService(DefaultOrderInquiryService orderInquiryService) {
        this.orderInquiryService = orderInquiryService;
    }
}
