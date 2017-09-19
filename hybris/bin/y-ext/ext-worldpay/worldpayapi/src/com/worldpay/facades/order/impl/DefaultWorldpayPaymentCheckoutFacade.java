package com.worldpay.facades.order.impl;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.config.WorldpayConfigLookupService;
import com.worldpay.core.checkout.WorldpayCheckoutService;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.hostedorderpage.data.KlarnaRedirectAuthoriseResult;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.AuthorisedStatus;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.request.KlarnaOrderInquiryServiceRequest;
import com.worldpay.service.response.OrderInquiryServiceResponse;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;

/**
 * Worldpay checkout facade to ensure Worldpay details are included in correct place
 */
public class DefaultWorldpayPaymentCheckoutFacade implements WorldpayPaymentCheckoutFacade {

    private CheckoutFacade checkoutFacade;
    private WorldpayCheckoutService worldpayCheckoutService;
    private CartService cartService;
    private DeliveryService deliveryService;
    private WorldpayConfigLookupService worldpayConfigLookupService;
    private WorldpayMerchantInfoService worldpayMerchantInfoService;


    /**
     * {@inheritDoc}
     */
    @Override
    public void setBillingDetails(final AddressData addressData) {
        final CartModel cartModel = getCart();
        if (cartModel != null && addressData != null) {
            final AddressModel addressModel = getDeliveryAddressModelForCode(addressData.getId());
            worldpayCheckoutService.setPaymentAddress(cartModel, addressModel);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasBillingDetails() {
        final CartModel cartModel = getCart();
        return cartModel != null && cartModel.getPaymentAddress() != null;
    }

    @Override
    public KlarnaRedirectAuthoriseResult checkKlarnaOrderStatus() throws WorldpayException {
        final CartModel cartModel = getCart();
        final MerchantInfo merchantConfig = worldpayMerchantInfoService.getCurrentSiteMerchant(UiExperienceLevel.DESKTOP);
        final KlarnaOrderInquiryServiceRequest klarnaOrderInquiryServiceRequest = createKlarnaOrderInquiryServiceRequest(merchantConfig, cartModel.getWorldpayOrderCode());
        final OrderInquiryServiceResponse serviceResponse = getWorldpayServiceGateway().orderInquiry(klarnaOrderInquiryServiceRequest);
        final AuthorisedStatus authStatus = serviceResponse.getPaymentReply().getAuthStatus();

        if (AuthorisedStatus.AUTHORISED.equals(authStatus) || AuthorisedStatus.SHOPPER_REDIRECTED.equals(authStatus)) {
            final KlarnaRedirectAuthoriseResult klarnaRedirectAuthoriseResult = new KlarnaRedirectAuthoriseResult();
            klarnaRedirectAuthoriseResult.setPending(true);
            final int paymentExponent = Integer.valueOf(serviceResponse.getPaymentReply().getAmount().getExponent());
            final String paymentAmount = serviceResponse.getPaymentReply().getAmount().getValue();
            klarnaRedirectAuthoriseResult.setPaymentAmount(new BigDecimal(paymentAmount).movePointLeft(paymentExponent));
            final String decodedHtmlContent = new String(Base64.getDecoder().decode(serviceResponse.getReference().getValue()));
            klarnaRedirectAuthoriseResult.setDecodedHTMLContent(decodedHtmlContent);

            return klarnaRedirectAuthoriseResult;
        } else {
            throw new WorldpayException("There was a problem placing the order");
        }
    }

    protected WorldpayServiceGateway getWorldpayServiceGateway() {
        return WorldpayServiceGateway.getInstance();
    }

    protected KlarnaOrderInquiryServiceRequest createKlarnaOrderInquiryServiceRequest(MerchantInfo merchantInfo, String orderCode) throws WorldpayConfigurationException {
        final WorldpayConfig worldpayConfig = worldpayConfigLookupService.lookupConfig();
        return KlarnaOrderInquiryServiceRequest.createKlarnaOrderInquiryRequest(worldpayConfig, merchantInfo, orderCode);
    }

    protected CartModel getCart() {
        return checkoutFacade.hasCheckoutCart() ? cartService.getSessionCart() : null;
    }

    protected AddressModel getDeliveryAddressModelForCode(final String code) {
        Assert.notNull(code, "Parameter code cannot be null.");
        final CartModel cartModel = getCart();
        if (cartModel != null) {
            final List<AddressModel> addresses = getDeliveryService().getSupportedDeliveryAddressesForOrder(cartModel, false);
            if (CollectionUtils.isNotEmpty(addresses)) {
                return getMatchingAddressModel(code, addresses);
            }
        }
        return null;
    }

    protected AddressModel getMatchingAddressModel(final String code, final List<AddressModel> addresses) {
        for (final AddressModel address : addresses) {
            if (code.equals(address.getPk().toString())) {
                return address;
            }
        }
        return null;
    }

    @Required
    public void setCheckoutFacade(CheckoutFacade checkoutFacade) {
        this.checkoutFacade = checkoutFacade;
    }

    @Required
    public CartService getCartService() {
        return cartService;
    }

    @Required
    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    public DeliveryService getDeliveryService() {
        return deliveryService;
    }

    @Required
    public void setDeliveryService(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @Required
    public void setWorldpayCheckoutService(WorldpayCheckoutService worldpayCheckoutService) {
        this.worldpayCheckoutService = worldpayCheckoutService;
    }

    @Required
    public void setWorldpayConfigLookupService(final WorldpayConfigLookupService worldpayConfigLookupService) {
        this.worldpayConfigLookupService = worldpayConfigLookupService;
    }

    @Required
    public void setWorldpayMerchantInfoService(final WorldpayMerchantInfoService worldpayMerchantInfoService) {
        this.worldpayMerchantInfoService = worldpayMerchantInfoService;
    }
}
