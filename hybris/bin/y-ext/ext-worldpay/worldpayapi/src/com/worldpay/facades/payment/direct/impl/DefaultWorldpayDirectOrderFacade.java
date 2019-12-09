package com.worldpay.facades.payment.direct.impl;

import com.google.common.base.Preconditions;
import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.data.*;
import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.TransactionStatus;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.model.Request3DInfo;
import com.worldpay.service.payment.WorldpayDirectOrderService;
import com.worldpay.service.payment.WorldpayJsonWebTokenService;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import com.worldpay.strategy.WorldpayAuthenticatedShopperIdStrategy;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static com.worldpay.enums.order.AuthorisedStatus.*;
import static java.text.MessageFormat.format;

/**
 * Implementation of the authorise operations that enables the Client Side Encryption with Worldpay
 */
public class DefaultWorldpayDirectOrderFacade implements WorldpayDirectOrderFacade {

    protected static final String ERROR_AUTHORISING_ORDER = "There was a problem authorising the order with worldpayOrderCode [{0}]";
    protected static final String THERE_WAS_AN_ERROR_COMMUNICATING_WITH_WORLDPAY = "There was an error communicating with Worldpay";
    protected static final String THERE_WAS_AN_ERROR_IN_THE_SERVICE_GATEWAY_MESSAGE = "There was an error in the service gateway:  [{0}]";
    private static final Logger LOG = Logger.getLogger(DefaultWorldpayDirectOrderFacade.class);
    private static final String THERE_IS_NO_CONFIGURATION = "There is no configuration for the requested merchant. Please review your settings.";

    private WorldpayAuthenticatedShopperIdStrategy worldpayAuthenticatedShopperIdStrategy;
    private WorldpayDirectOrderService worldpayDirectOrderService;

    private WorldpayJsonWebTokenService worldpayJsonWebTokenService;
    private CartService cartService;
    private WorldpayMerchantInfoService worldpayMerchantInfoService;
    private AcceleratorCheckoutFacade acceleratorCheckoutFacade;
    private WorldpayPaymentInfoService worldpayPaymentInfoService;
    private WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade;
    private CartFacade cartFacade;

    /**
     * {@inheritDoc}
     */
    @Override
    public void tokenize(final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException {
        Preconditions.checkState(cartService.hasSessionCart(), "Cannot tokenize where there is no cart");

        final CartModel cart = cartService.getSessionCart();
        try {
            final MerchantInfo merchantInfo = worldpayMerchantInfoService.getCurrentSiteMerchant();
            final String authenticatedShopperId = worldpayAuthenticatedShopperIdStrategy.getAuthenticatedShopperId(cart.getUser());
            worldpayAdditionalInfoData.setAuthenticatedShopperId(authenticatedShopperId);

            worldpayDirectOrderService.createToken(merchantInfo, cart, cseAdditionalAuthInfo, worldpayAdditionalInfoData);
        } catch (final WorldpayConfigurationException e) {
            LOG.error(THERE_IS_NO_CONFIGURATION);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectResponseData authorise(final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException {
        Preconditions.checkState(cartService.hasSessionCart(), "Cannot authorize payment where there is no cart");

        final CartModel cart = cartService.getSessionCart();
        final MerchantInfo merchantInfo = worldpayMerchantInfoService.getCurrentSiteMerchant();
        return internalAuthorise(cart, worldpayAdditionalInfoData, merchantInfo);
    }

    @Override
    public DirectResponseData authoriseRecurringPayment(final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException {
        Preconditions.checkState(cartService.hasSessionCart(), "Cannot authorize payment where there is no cart");

        final CartModel cart = cartService.getSessionCart();
        final MerchantInfo merchantInfo = worldpayMerchantInfoService.getCurrentSiteMerchant();
        return internalAuthoriseRecurringPayment(cart, worldpayAdditionalInfoData, merchantInfo);
    }

    @Override
    public DirectResponseData authoriseRecurringPayment(final AbstractOrderModel abstractOrderModel,
                                                        final WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                        final MerchantInfo merchantInfo) throws WorldpayException, InvalidCartException {
        return internalAuthoriseRecurringPayment(abstractOrderModel, worldpayAdditionalInfoData, merchantInfo);
    }

    protected DirectResponseData internalAuthoriseRecurringPayment(final AbstractOrderModel abstractOrderModel,
                                                                   final WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                                   final MerchantInfo merchantInfo) throws WorldpayException, InvalidCartException {
        try {
            final String authenticatedShopperId = worldpayAuthenticatedShopperIdStrategy.getAuthenticatedShopperId(abstractOrderModel.getUser());
            worldpayAdditionalInfoData.setAuthenticatedShopperId(authenticatedShopperId);

            final DirectAuthoriseServiceResponse directAuthoriseServiceResponse = worldpayDirectOrderService.authoriseRecurringPayment(merchantInfo,
                    abstractOrderModel,
                    worldpayAdditionalInfoData);
            return handleDirectServiceResponse(directAuthoriseServiceResponse, merchantInfo, abstractOrderModel);
        } catch (final WorldpayConfigurationException e) {
            LOG.error(THERE_IS_NO_CONFIGURATION);
            throw e;
        } catch (final InvalidCartException e) {
            LOG.error(format("There was an error placing the order for cart [{0}]", abstractOrderModel.getCode()), e);
            throw e;
        }
    }

    protected DirectResponseData internalAuthorise(final CartModel cart, final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final MerchantInfo merchantInfo) throws WorldpayException, InvalidCartException {
        try {
            final String authenticatedShopperId = worldpayAuthenticatedShopperIdStrategy.getAuthenticatedShopperId(cart.getUser());
            worldpayAdditionalInfoData.setAuthenticatedShopperId(authenticatedShopperId);

            final DirectAuthoriseServiceResponse serviceResponse = worldpayDirectOrderService.authorise(merchantInfo, cart, worldpayAdditionalInfoData);

            return handleDirectServiceResponse(serviceResponse, merchantInfo, cart);
        } catch (final WorldpayConfigurationException e) {
            LOG.error(THERE_IS_NO_CONFIGURATION);
            throw e;
        }
    }

    @Override
    public String authoriseKlarnaRedirect(final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final AdditionalAuthInfo additionalAuthInfo) throws WorldpayException {
        final CartModel cart = cartService.getSessionCart();
        try {
            final MerchantInfo merchantInfo = worldpayMerchantInfoService.getCurrentSiteMerchant();
            final DirectAuthoriseServiceResponse serviceResponse = worldpayDirectOrderService.authoriseKlarna(merchantInfo, cart, worldpayAdditionalInfoData, additionalAuthInfo);
            final String klarnaRedirectContentEncoded = handleAuthoriseRedirectServiceResponse(serviceResponse);
            return new String(Base64.getDecoder().decode(klarnaRedirectContentEncoded), StandardCharsets.UTF_8);
        } catch (final WorldpayConfigurationException e) {
            LOG.error(THERE_IS_NO_CONFIGURATION, e);
            throw e;
        }
    }

    @Override
    public DirectResponseData authoriseApplePayDirect(final ApplePayAdditionalAuthInfo applePayAdditionalAuthInfo) throws WorldpayException, InvalidCartException {
        final CartModel cart = cartService.getSessionCart();

        try {
            final MerchantInfo merchantInfo = worldpayMerchantInfoService.getCurrentSiteMerchant();
            final DirectAuthoriseServiceResponse directAuthoriseServiceResponse = worldpayDirectOrderService.authoriseApplePay(merchantInfo, cart, applePayAdditionalAuthInfo);
            if (directAuthoriseServiceResponse.getPaymentReply().getAuthStatus().equals(AUTHORISED)) {
                worldpayPaymentInfoService.createPaymentInfoApplePay(cart, applePayAdditionalAuthInfo);
            }
            return handleDirectServiceResponse(directAuthoriseServiceResponse, merchantInfo, cart);
        } catch (final WorldpayConfigurationException e) {
            LOG.error(THERE_IS_NO_CONFIGURATION, e);
            throw e;
        }
    }

    @Override
    public DirectResponseData authoriseGooglePayDirect(final GooglePayAdditionalAuthInfo googlePayAdditionalAuthInfo) throws WorldpayException, InvalidCartException {
        try {
            final CartModel cart = getCartService().getSessionCart();
            final MerchantInfo merchantInfo = worldpayMerchantInfoService.getCurrentSiteMerchant();
            final DirectAuthoriseServiceResponse directAuthoriseServiceResponse = worldpayDirectOrderService.authoriseGooglePay(merchantInfo, cart, googlePayAdditionalAuthInfo);
            if (directAuthoriseServiceResponse.getPaymentReply().getAuthStatus().equals(AUTHORISED)) {
                worldpayPaymentInfoService.createPaymentInfoGooglePay(cart, googlePayAdditionalAuthInfo);
            }
            return handleDirectServiceResponse(directAuthoriseServiceResponse, merchantInfo, cart);
        } catch (final WorldpayConfigurationException e) {
            LOG.error(THERE_IS_NO_CONFIGURATION, e);
            throw e;
        }
    }

    @Override
    public ApplePayOrderUpdate updatePaymentMethod(final ApplePayPaymentMethodUpdateRequest paymentMethodUpdateRequest) {
        return createNoop();
    }

    @Override
    public String createJsonWebTokenForDDC() {
        final WorldpayMerchantConfigData merchantConfigData = getWorldpayMerchantConfigDataFacade().getCurrentSiteMerchantConfigData();
        return worldpayJsonWebTokenService.createJsonWebTokenForDDC(merchantConfigData);
    }

    @Override
    public String getEventOriginDomainForDDC() {
        return getWorldpayMerchantConfigDataFacade().getCurrentSiteMerchantConfigData() != null &&
                getWorldpayMerchantConfigDataFacade().getCurrentSiteMerchantConfigData().getThreeDSFlexJsonWebTokenSettings() != null ?
                getWorldpayMerchantConfigDataFacade().getCurrentSiteMerchantConfigData().getThreeDSFlexJsonWebTokenSettings().getEventOriginDomain() : null;
    }

    protected ApplePayOrderUpdate createNoop() {
        final ApplePayLineItem newTotalLineItem = new ApplePayLineItem();
        newTotalLineItem.setType("final");
        newTotalLineItem.setLabel(worldpayMerchantConfigDataFacade.getCurrentSiteMerchantConfigData().getApplePaySettings().getMerchantName());
        newTotalLineItem.setAmount(cartFacade.getSessionCart().getTotalPrice().getValue().toString());

        final ApplePayOrderUpdate update = new ApplePayOrderUpdate();
        update.setNewTotal(newTotalLineItem);

        return update;
    }

    @Override
    public String authoriseBankTransferRedirect(final BankTransferAdditionalAuthInfo bankTransferAdditionalAuthInfo,
                                                final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException {
        final CartModel cart = cartService.getSessionCart();
        try {
            final MerchantInfo merchantInfo = worldpayMerchantInfoService.getCurrentSiteMerchant();
            final DirectAuthoriseServiceResponse serviceResponse = worldpayDirectOrderService.authoriseBankTransfer(merchantInfo, cart, bankTransferAdditionalAuthInfo, worldpayAdditionalInfoData);
            return handleAuthoriseRedirectServiceResponse(serviceResponse);
        } catch (final WorldpayConfigurationException e) {
            LOG.error(THERE_IS_NO_CONFIGURATION);
            throw e;
        }
    }

    protected String handleAuthoriseRedirectServiceResponse(final DirectAuthoriseServiceResponse serviceResponse) throws WorldpayException {
        if (shouldProcessRedirect(serviceResponse)) {
            return serviceResponse.getRedirectReference().getValue();
        } else if (serviceResponse.getErrorDetail() != null) {
            final String errorMessage = format(THERE_WAS_AN_ERROR_IN_THE_SERVICE_GATEWAY_MESSAGE, serviceResponse.getErrorDetail().getMessage());
            LOG.error(errorMessage);
            throw new WorldpayException(errorMessage);
        } else {
            throw new WorldpayException(THERE_WAS_AN_ERROR_COMMUNICATING_WITH_WORLDPAY);
        }
    }

    @Override
    public DirectResponseData authorise3DSecure(final String paResponse, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException {
        Preconditions.checkState(cartService.hasSessionCart(), "Cannot authorize payment where there is no abstractOrderModel");
        final CartModel cartModel = cartService.getSessionCart();
        return internalAuthorise3DSecure(cartModel, paResponse, worldpayAdditionalInfoData);
    }

    protected DirectResponseData internalAuthorise3DSecure(final AbstractOrderModel abstractOrderModel,
                                                           final String paResponse,
                                                           final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException {
        final MerchantInfo merchantInfo = worldpayMerchantInfoService.getCurrentSiteMerchant();
        final String authenticatedShopperId = worldpayAuthenticatedShopperIdStrategy.getAuthenticatedShopperId(abstractOrderModel.getUser());
        worldpayAdditionalInfoData.setAuthenticatedShopperId(authenticatedShopperId);

        final DirectAuthoriseServiceResponse serviceResponse = worldpayDirectOrderService.authorise3DSecure(merchantInfo,
                abstractOrderModel.getWorldpayOrderCode(),
                worldpayAdditionalInfoData,
                paResponse);
        if (shouldProcessResponse(serviceResponse)) {
            return handle3DSecureResponse(abstractOrderModel, serviceResponse, merchantInfo);
        } else if (serviceResponse.getErrorDetail() != null) {
            final String errorMessage = format(THERE_WAS_AN_ERROR_IN_THE_SERVICE_GATEWAY_MESSAGE, serviceResponse.getErrorDetail().getMessage());
            LOG.error(errorMessage);
            throw new WorldpayException(errorMessage);
        } else {
            throw new WorldpayException(THERE_WAS_AN_ERROR_COMMUNICATING_WITH_WORLDPAY);
        }
    }

    @Override
    public DirectResponseData executeSecondPaymentAuthorisation3DSecure(final String sessionId) throws WorldpayException, InvalidCartException {
        final MerchantInfo merchantInfo = worldpayMerchantInfoService.getCurrentSiteMerchant();
        return internalSecondPaymentAuthorise3DSecure(sessionId, merchantInfo);
    }

    private DirectResponseData internalSecondPaymentAuthorise3DSecure(final String sessionId, final MerchantInfo merchantInfo) throws WorldpayException, InvalidCartException {
        final DirectResponseData response = new DirectResponseData();
        DirectAuthoriseServiceResponse serviceResponse = null;
        if (cartService.hasSessionCart()) {
            final CartModel cartModel = cartService.getSessionCart();
            serviceResponse = worldpayDirectOrderService.authorise3DSecureAgain(merchantInfo, cartModel.getWorldpayOrderCode(), sessionId);
            if (shouldProcessResponse(serviceResponse)) {
                final PaymentReply paymentReply = serviceResponse.getPaymentReply();
                final AuthorisedStatus authStatus = paymentReply.getAuthStatus();
                if (AUTHORISED.equals(authStatus)) {
                    worldpayDirectOrderService.completeAuthorise3DSecure(cartModel, serviceResponse, merchantInfo);
                    handleAuthorisedResponse(response);
                } else if (REFUSED.equals(authStatus)) {
                    handleRefusedResponse(response, paymentReply.getReturnCode());
                } else {
                    final String errorMessage = format(ERROR_AUTHORISING_ORDER, serviceResponse.getOrderCode());
                    LOG.error(errorMessage);
                    throw new WorldpayException(errorMessage);
                }
                return response;
            } else if (serviceResponse.getErrorDetail() != null) {
                final String errorMessage = format(THERE_WAS_AN_ERROR_IN_THE_SERVICE_GATEWAY_MESSAGE, serviceResponse.getErrorDetail().getMessage());
                LOG.error(errorMessage);
                throw new WorldpayException(errorMessage);
            } else {
                throw new WorldpayException(THERE_WAS_AN_ERROR_COMMUNICATING_WITH_WORLDPAY);
            }
        } else {
            throwWorldpayException("The session has not a valid cart");
        }
        return response;
    }

    protected DirectResponseData handleDirectServiceResponse(final DirectAuthoriseServiceResponse serviceResponse, final MerchantInfo merchantInfo, final AbstractOrderModel abstractOrderModel)
            throws WorldpayException, InvalidCartException {
        if (shouldProcessResponse(serviceResponse)) {
            return processDirectResponse(serviceResponse, abstractOrderModel, merchantInfo);
        } else {
            return handleErrorOnServiceResponse(serviceResponse);
        }
    }

    private DirectResponseData handleErrorOnServiceResponse(final DirectAuthoriseServiceResponse serviceResponse) throws WorldpayException {
        if (serviceResponse.getErrorDetail() != null) {
            final String errorMessage = format(THERE_WAS_AN_ERROR_IN_THE_SERVICE_GATEWAY_MESSAGE, serviceResponse.getErrorDetail().getMessage());
            LOG.error(errorMessage);
            throw new WorldpayException(errorMessage);
        } else {
            throw new WorldpayException(THERE_WAS_AN_ERROR_COMMUNICATING_WITH_WORLDPAY);
        }
    }

    protected DirectResponseData handle3DSecureResponse(final AbstractOrderModel abstractOrderModel,
                                                        final DirectAuthoriseServiceResponse serviceResponse,
                                                        final MerchantInfo merchantInfo) throws InvalidCartException, WorldpayException {
        final DirectResponseData response = new DirectResponseData();
        final PaymentReply paymentReply = serviceResponse.getPaymentReply();
        final AuthorisedStatus authStatus = paymentReply.getAuthStatus();
        if (AUTHORISED.equals(authStatus)) {
            worldpayDirectOrderService.completeAuthorise3DSecure(abstractOrderModel, serviceResponse, merchantInfo);
            handleAuthorisedResponse(response);
        } else if (REFUSED.equals(authStatus)) {
            handleRefusedResponse(response, paymentReply.getReturnCode());
        } else {
            final String errorMessage = format(ERROR_AUTHORISING_ORDER, serviceResponse.getOrderCode());
            LOG.error(errorMessage);
            throw new WorldpayException(errorMessage);
        }
        return response;
    }

    protected DirectResponseData processDirectResponse(final DirectAuthoriseServiceResponse serviceResponse,
                                                       final AbstractOrderModel abstractOrderModel,
                                                       final MerchantInfo merchantInfo) throws WorldpayException, InvalidCartException {
        final DirectResponseData response = new DirectResponseData();
        final PaymentReply paymentReply = serviceResponse.getPaymentReply();
        if (paymentReply != null) {
            final AuthorisedStatus status = paymentReply.getAuthStatus();
            if (AUTHORISED.equals(status)) {
                worldpayDirectOrderService.completeAuthorise(serviceResponse, abstractOrderModel, merchantInfo.getMerchantCode());
                handleAuthorisedResponse(response);
            } else if (REFUSED.equals(status)) {
                handleRefusedResponse(response, paymentReply.getReturnCode());
            } else if (CANCELLED.equals(status)) {
                handleCancelledResponse(response);
            }
        } else if (serviceResponse.get3DSecureFlow().isPresent()) {
            handle3DInfoRequest(response, serviceResponse.getRequest3DInfo());
        } else {
            final String errorMessage = format(ERROR_AUTHORISING_ORDER, abstractOrderModel.getWorldpayOrderCode());
            throwWorldpayException(errorMessage);
        }
        return response;
    }

    private void throwWorldpayException(final String errorMessage) throws WorldpayException {
        LOG.error(errorMessage);
        throw new WorldpayException(errorMessage);
    }

    protected boolean shouldProcessResponse(final DirectAuthoriseServiceResponse serviceResponse) {
        return serviceResponse.getPaymentReply() != null || serviceResponse.get3DSecureFlow().isPresent();
    }

    protected boolean shouldProcessRedirect(final DirectAuthoriseServiceResponse serviceResponse) {
        return serviceResponse.getRedirectReference() != null;
    }

    protected void handleAuthorisedResponse(final DirectResponseData response) throws InvalidCartException {
        final OrderData orderData = acceleratorCheckoutFacade.placeOrder();
        response.setOrderData(orderData);
        response.setTransactionStatus(TransactionStatus.AUTHORISED);
    }

    protected void handleRefusedResponse(final DirectResponseData response, final String returnCode) {
        response.setTransactionStatus(TransactionStatus.REFUSED);
        response.setReturnCode(returnCode);
    }

    protected void handleCancelledResponse(final DirectResponseData response) {
        response.setTransactionStatus(TransactionStatus.CANCELLED);
    }

    protected void handle3DInfoRequest(final DirectResponseData response, final Request3DInfo request3DInfo) {
        response.setIssuerURL(request3DInfo.getIssuerUrl());
        response.setPaRequest(request3DInfo.getPaRequest());
        response.setIssuerPayload(request3DInfo.getIssuerPayload());
        response.setMajor3DSVersion(request3DInfo.getMajor3DSVersion());
        response.setTransactionId3DS(request3DInfo.getTransactionId3DS());
        response.setTransactionStatus(TransactionStatus.AUTHENTICATION_REQUIRED);
    }

    @Required
    public void setWorldpayDirectOrderService(final WorldpayDirectOrderService worldpayDirectOrderService) {
        this.worldpayDirectOrderService = worldpayDirectOrderService;
    }

    @Required
    public void setWorldpayAuthenticatedShopperIdStrategy(final WorldpayAuthenticatedShopperIdStrategy worldpayAuthenticatedShopperIdStrategy) {
        this.worldpayAuthenticatedShopperIdStrategy = worldpayAuthenticatedShopperIdStrategy;
    }

    @Required
    public void setAcceleratorCheckoutFacade(final AcceleratorCheckoutFacade acceleratorCheckoutFacade) {
        this.acceleratorCheckoutFacade = acceleratorCheckoutFacade;
    }

    @Required
    public void setWorldpayPaymentInfoService(final WorldpayPaymentInfoService worldpayPaymentInfoService) {
        this.worldpayPaymentInfoService = worldpayPaymentInfoService;
    }

    public WorldpayMerchantInfoService getWorldpayMerchantInfoService() {
        return worldpayMerchantInfoService;
    }

    @Required
    public void setWorldpayMerchantInfoService(final WorldpayMerchantInfoService worldpayMerchantInfoService) {
        this.worldpayMerchantInfoService = worldpayMerchantInfoService;
    }

    public CartService getCartService() {
        return cartService;
    }

    @Required
    public void setCartService(final CartService cartService) {
        this.cartService = cartService;
    }

    public WorldpayMerchantConfigDataFacade getWorldpayMerchantConfigDataFacade() {
        return worldpayMerchantConfigDataFacade;
    }

    @Required
    public void setWorldpayMerchantConfigDataFacade(final WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade) {
        this.worldpayMerchantConfigDataFacade = worldpayMerchantConfigDataFacade;
    }

    public CartFacade getCartFacade() {
        return cartFacade;
    }

    @Required
    public void setCartFacade(final CartFacade cartFacade) {
        this.cartFacade = cartFacade;
    }

    @Required
    public void setWorldpayJsonWebTokenService(final WorldpayJsonWebTokenService worldpayJsonWebTokenService) {
        this.worldpayJsonWebTokenService = worldpayJsonWebTokenService;
    }
}
