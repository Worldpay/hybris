package com.worldpay.facades.order.impl;

import com.worldpay.core.services.OrderInquiryService;
import com.worldpay.core.services.impl.DefaultOrderInquiryService;
import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.order.WorldpayKlarnaPaymentCheckoutFacade;
import com.worldpay.hostedorderpage.data.KlarnaRedirectAuthoriseResult;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.data.MerchantInfo;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.response.OrderInquiryServiceResponse;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;


import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Klarna checkout facade to perform specific operations required by the APM
 */
public class DefaultWorldpayKlarnaPaymentCheckoutFacade implements WorldpayKlarnaPaymentCheckoutFacade {

    protected final CheckoutFacade checkoutFacade;
    protected final CartService cartService;
    protected final WorldpayMerchantInfoService worldpayMerchantInfoService;
    protected final OrderInquiryService orderInquiryService;
    protected final WorldpayOrderService worldpayOrderService;

    public DefaultWorldpayKlarnaPaymentCheckoutFacade(final CheckoutFacade checkoutFacade,
                                                      final CartService cartService,
                                                      final WorldpayMerchantInfoService worldpayMerchantInfoService,
                                                      final OrderInquiryService orderInquiryService,
                                                      final WorldpayOrderService worldpayOrderService) {
        this.checkoutFacade = checkoutFacade;
        this.cartService = cartService;
        this.worldpayMerchantInfoService = worldpayMerchantInfoService;
        this.orderInquiryService = orderInquiryService;
        this.worldpayOrderService = worldpayOrderService;
    }

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
            klarnaRedirectAuthoriseResult.setPaymentAmount(worldpayOrderService.convertAmount(serviceResponse.getPaymentReply().getAmount()));
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

}
