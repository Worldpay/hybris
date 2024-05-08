package com.worldpay.facades.payment.direct.impl;

import com.google.common.base.Preconditions;
import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.data.*;
import com.worldpay.data.payment.Card;
import com.worldpay.data.token.TokenDetails;
import com.worldpay.data.token.TokenReply;
import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.model.GooglePayPaymentInfoModel;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.TransactionStatus;
import com.worldpay.service.payment.WorldpayDirectOrderService;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

import static com.worldpay.enums.order.AuthorisedStatus.*;
import static java.text.MessageFormat.format;

/**
 * Implementation of the authorise operations that enables the Client Side Encryption with Worldpay
 */
@SuppressWarnings("java:S107")
public class DefaultWorldpayDirectOrderFacade implements WorldpayDirectOrderFacade {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultWorldpayDirectOrderFacade.class);

    private static final String ERROR_AUTHORISING_ORDER = "There was a problem authorising the order with worldpayOrderCode [{0}]";
    private static final String THERE_WAS_AN_ERROR_COMMUNICATING_WITH_WORLDPAY = "There was an error communicating with Worldpay";
    private static final String THERE_WAS_AN_ERROR_IN_THE_SERVICE_GATEWAY_MESSAGE = "There was an error in the service gateway: [{0}]";
    private static final String THERE_IS_NO_CONFIGURATION = "There is no configuration for the requested merchant. Please review your settings.";
    private static final String CANNOT_AUTHORIZE_PAYMENT_WHERE_THERE_IS_NO_CART_MESSAGE = "Cannot authorize payment where there is no cart";

    protected final WorldpayDirectOrderService worldpayDirectOrderService;
    protected final CartService cartService;
    protected final AcceleratorCheckoutFacade acceleratorCheckoutFacade;
    protected final WorldpayPaymentInfoService worldpayPaymentInfoService;
    protected final WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade;
    protected final WorldpayCartService worldpayCartService;
    protected final Populator<WorldpayAPMPaymentInfoModel, CCPaymentInfoData> apmPaymentInfoPopulator;

    public DefaultWorldpayDirectOrderFacade(final WorldpayDirectOrderService worldpayDirectOrderService,
                                            final CartService cartService,
                                            final AcceleratorCheckoutFacade acceleratorCheckoutFacade,
                                            final WorldpayPaymentInfoService worldpayPaymentInfoService,
                                            final WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade,
                                            final WorldpayCartService worldpayCartService,
                                            final Populator<WorldpayAPMPaymentInfoModel, CCPaymentInfoData> apmPaymentInfoPopulator) {
        this.worldpayDirectOrderService = worldpayDirectOrderService;
        this.cartService = cartService;
        this.acceleratorCheckoutFacade = acceleratorCheckoutFacade;
        this.worldpayPaymentInfoService = worldpayPaymentInfoService;
        this.worldpayMerchantConfigDataFacade = worldpayMerchantConfigDataFacade;
        this.worldpayCartService = worldpayCartService;
        this.apmPaymentInfoPopulator = apmPaymentInfoPopulator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tokenize(final CartModel cartModel, final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException {
        Preconditions.checkState(Objects.nonNull(cartModel), "Cannot tokenize where there is no cart");
        try {
            worldpayDirectOrderService.createToken(cartModel, cseAdditionalAuthInfo, worldpayAdditionalInfoData);
        } catch (final WorldpayConfigurationException e) {
            LOG.error(THERE_IS_NO_CONFIGURATION);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tokenize(final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException {
        Preconditions.checkState(cartService.hasSessionCart(), CANNOT_AUTHORIZE_PAYMENT_WHERE_THERE_IS_NO_CART_MESSAGE);
        final CartModel cartModel = cartService.getSessionCart();
        setAuthenticatedShopperIdOnAdditionalInfoData(worldpayAdditionalInfoData, cartModel);

        tokenize(cartModel, cseAdditionalAuthInfo, worldpayAdditionalInfoData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectResponseData executeFirstPaymentAuthorisation3DSecure(final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException {
        final DirectResponseData directResponseData;
        if (StringUtils.isEmpty(cseAdditionalAuthInfo.getEncryptedData())) {
            final CartModel cart = cartService.getSessionCart();
            directResponseData = authoriseRecurringPayment(cart, worldpayAdditionalInfoData);
        } else {
            directResponseData = authoriseAndTokenize(worldpayAdditionalInfoData, cseAdditionalAuthInfo);
        }
        return directResponseData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectResponseData authorise(final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException {
        Preconditions.checkState(cartService.hasSessionCart(), CANNOT_AUTHORIZE_PAYMENT_WHERE_THERE_IS_NO_CART_MESSAGE);

        final CartModel cart = cartService.getSessionCart();
        return internalAuthorise(cart, worldpayAdditionalInfoData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectResponseData authoriseRecurringPayment(final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException {
        Preconditions.checkState(cartService.hasSessionCart(), CANNOT_AUTHORIZE_PAYMENT_WHERE_THERE_IS_NO_CART_MESSAGE);

        final CartModel cart = cartService.getSessionCart();
        return internalAuthoriseRecurringPayment(cart, worldpayAdditionalInfoData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectResponseData authoriseRecurringPayment(final AbstractOrderModel abstractOrderModel,
                                                        final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException {
        return internalAuthoriseRecurringPayment(abstractOrderModel, worldpayAdditionalInfoData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String authoriseKlarnaRedirect(final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final AdditionalAuthInfo additionalAuthInfo) throws WorldpayException {
        final CartModel cart = cartService.getSessionCart();
        try {
            final DirectAuthoriseServiceResponse serviceResponse = worldpayDirectOrderService.authoriseKlarna(cart, worldpayAdditionalInfoData, additionalAuthInfo);
            final String klarnaRedirectContentEncoded = handleAuthoriseRedirectServiceResponse(serviceResponse);
            return new String(Base64.getDecoder().decode(klarnaRedirectContentEncoded), StandardCharsets.UTF_8);
        } catch (final WorldpayConfigurationException e) {
            throw new WorldpayConfigurationException(THERE_IS_NO_CONFIGURATION);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectResponseData authoriseApplePayDirect(final ApplePayAdditionalAuthInfo applePayAdditionalAuthInfo) throws WorldpayException, InvalidCartException {
        final CartModel cart = cartService.getSessionCart();

        try {
            final DirectAuthoriseServiceResponse directAuthoriseServiceResponse = worldpayDirectOrderService.authoriseApplePay(cart, applePayAdditionalAuthInfo);
            if (directAuthoriseServiceResponse.getPaymentReply().getAuthStatus().equals(AUTHORISED)) {
                worldpayPaymentInfoService.createPaymentInfoApplePay(cart, applePayAdditionalAuthInfo);
            }
            return handleDirectServiceResponse(directAuthoriseServiceResponse, cart);
        } catch (final WorldpayConfigurationException e) {
            throw new WorldpayConfigurationException(THERE_IS_NO_CONFIGURATION);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectResponseData authoriseGooglePayDirect(final GooglePayAdditionalAuthInfo googlePayAdditionalAuthInfo) throws WorldpayException, InvalidCartException {
        try {
            final CartModel cart = cartService.getSessionCart();
            final DirectAuthoriseServiceResponse directAuthoriseServiceResponse = worldpayDirectOrderService.authoriseGooglePay(cart, googlePayAdditionalAuthInfo);
            if (directAuthoriseServiceResponse.getPaymentReply().getAuthStatus().equals(AUTHORISED)) {
                final String paymentTokenID = Optional.ofNullable(directAuthoriseServiceResponse.getToken())
                    .map((TokenReply::getTokenDetails))
                    .map(TokenDetails::getPaymentTokenID)
                    .orElse(null);
                final Card cardDetails = Optional.ofNullable(directAuthoriseServiceResponse.getPaymentReply())
                    .map(PaymentReply::getCardDetails)
                    .orElse(null);

                worldpayPaymentInfoService.createPaymentInfoGooglePay(cart, googlePayAdditionalAuthInfo, paymentTokenID, cardDetails);
            }
            return handleDirectServiceResponse(directAuthoriseServiceResponse, cart);
        } catch (final WorldpayConfigurationException e) {
            throw new WorldpayConfigurationException(THERE_IS_NO_CONFIGURATION);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplePayOrderUpdate updatePaymentMethod(final ApplePayPaymentMethodUpdateRequest paymentMethodUpdateRequest) {
        return createNoop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String authoriseBankTransferRedirect(final BankTransferAdditionalAuthInfo bankTransferAdditionalAuthInfo,
                                                final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException {
        final CartModel cart = cartService.getSessionCart();
        try {
            final DirectAuthoriseServiceResponse serviceResponse = worldpayDirectOrderService.authoriseBankTransfer(cart, bankTransferAdditionalAuthInfo, worldpayAdditionalInfoData);
            return handleAuthoriseRedirectServiceResponse(serviceResponse);
        } catch (final WorldpayConfigurationException e) {
            LOG.error(THERE_IS_NO_CONFIGURATION);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectResponseData authorise3DSecure(final String paResponse, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException {
        Preconditions.checkState(cartService.hasSessionCart(), "Cannot authorize payment where there is no abstractOrderModel");
        final CartModel cartModel = cartService.getSessionCart();
        return internalAuthorise3DSecure(cartModel, paResponse, worldpayAdditionalInfoData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectResponseData executeSecondPaymentAuthorisation3DSecure() throws WorldpayException, InvalidCartException {
        final DirectResponseData response = new DirectResponseData();
        if (cartService.hasSessionCart()) {
            final CartModel cartModel = cartService.getSessionCart();
            final DirectAuthoriseServiceResponse serviceResponse = worldpayDirectOrderService.authorise3DSecureAgain(cartModel.getWorldpayOrderCode());
            if (shouldProcessResponse(serviceResponse)) {
                return processDirectResponseData(cartModel, serviceResponse, response);
            } else if (Objects.nonNull(serviceResponse.getErrorDetail())) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectResponseData executeSecondPaymentAuthorisation3DSecure(final String worldpayOrderCode) throws WorldpayException, InvalidCartException {
        final CartModel cartModel = worldpayCartService.findCartByWorldpayOrderCode(worldpayOrderCode);
        cartService.setSessionCart(cartModel);

        final DirectAuthoriseServiceResponse serviceResponse = worldpayDirectOrderService.authorise3DSecureAgain(cartModel.getWorldpayOrderCode());
        if (shouldProcessResponse(serviceResponse)) {
            return processDirectResponseData(cartModel, serviceResponse, new DirectResponseData());
        } else if (Objects.nonNull(serviceResponse.getErrorDetail())) {
            final String errorMessage = format(THERE_WAS_AN_ERROR_IN_THE_SERVICE_GATEWAY_MESSAGE, serviceResponse.getErrorDetail().getMessage());
            LOG.error(errorMessage);
            throw new WorldpayException(errorMessage);
        }

        throw new WorldpayException(THERE_WAS_AN_ERROR_COMMUNICATING_WITH_WORLDPAY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectResponseData authoriseAndTokenize(final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final CSEAdditionalAuthInfo cseAdditionalAuthInfo) throws WorldpayException, InvalidCartException {
        final CartModel cart = cartService.getSessionCart();
        removePaymentInfo(cart);
        setAuthenticatedShopperIdOnAdditionalInfoData(worldpayAdditionalInfoData, cart);
        if (Boolean.TRUE.equals(cseAdditionalAuthInfo.getSaveCard())) {
            return internalTokenizeAndAuthorise(cart, worldpayAdditionalInfoData, cseAdditionalAuthInfo);
        } else {
            tokenize(cart, cseAdditionalAuthInfo, worldpayAdditionalInfoData);
            return authorise(worldpayAdditionalInfoData);
        }
    }

    private void removePaymentInfo(final CartModel cart) {
        cart.setPaymentInfo(null);
        cart.setApmCode(StringUtils.EMPTY);
        cart.setApmName(StringUtils.EMPTY);
    }

    protected String handleAuthoriseRedirectServiceResponse(final DirectAuthoriseServiceResponse serviceResponse) throws WorldpayException {
        if (shouldProcessRedirect(serviceResponse)) {
            return serviceResponse.getRedirectReference().getValue();
        } else if (Objects.nonNull(serviceResponse.getErrorDetail())) {
            final String errorMessage = format(THERE_WAS_AN_ERROR_IN_THE_SERVICE_GATEWAY_MESSAGE, serviceResponse.getErrorDetail().getMessage());
            LOG.error(errorMessage);
            throw new WorldpayException(errorMessage);
        } else {
            throw new WorldpayException(THERE_WAS_AN_ERROR_COMMUNICATING_WITH_WORLDPAY);
        }
    }

    protected ApplePayOrderUpdate createNoop() {
        final ApplePayLineItem newTotalLineItem = new ApplePayLineItem();
        newTotalLineItem.setType("final");
        newTotalLineItem.setLabel(worldpayMerchantConfigDataFacade.getCurrentSiteMerchantConfigData().getApplePaySettings().getMerchantName());
        newTotalLineItem.setAmount(cartService.getSessionCart().getTotalPrice().toString());

        final ApplePayOrderUpdate update = new ApplePayOrderUpdate();
        update.setNewTotal(newTotalLineItem);

        return update;
    }

    protected DirectResponseData internalAuthoriseRecurringPayment(final AbstractOrderModel abstractOrderModel,
                                                                   final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException {
        try {
            setAuthenticatedShopperIdOnAdditionalInfoData(worldpayAdditionalInfoData, abstractOrderModel);
            final var directAuthoriseServiceResponse = worldpayDirectOrderService.authoriseRecurringPayment(
                abstractOrderModel,
                worldpayAdditionalInfoData);
            return handleDirectServiceResponse(directAuthoriseServiceResponse, abstractOrderModel);
        } catch (final WorldpayConfigurationException e) {
            LOG.error(THERE_IS_NO_CONFIGURATION);
            throw e;
        } catch (final InvalidCartException e) {
            throw new InvalidCartException(MessageFormat.format("There was an error placing the order for cart [{}]", abstractOrderModel.getCode()));
        }
    }

    protected DirectResponseData internalAuthorise(final CartModel cart, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException {
        try {
            setAuthenticatedShopperIdOnAdditionalInfoData(worldpayAdditionalInfoData, cart);
            final DirectAuthoriseServiceResponse serviceResponse = worldpayDirectOrderService.authorise(cart, worldpayAdditionalInfoData);

            return handleDirectServiceResponse(serviceResponse, cart);
        } catch (final WorldpayConfigurationException e) {
            LOG.error(THERE_IS_NO_CONFIGURATION);
            throw e;
        }
    }

    private void setAuthenticatedShopperIdOnAdditionalInfoData(final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final AbstractOrderModel cart) {
        final String authenticatedShopperId = worldpayCartService.getAuthenticatedShopperId(cart);
        worldpayAdditionalInfoData.setAuthenticatedShopperId(authenticatedShopperId);
    }

    @Override
    public DirectResponseData internalTokenizeAndAuthorise(final CartModel cart, final WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                           final CSEAdditionalAuthInfo cseAdditionalAuthInfo) throws WorldpayException, InvalidCartException {
        Preconditions.checkState(Objects.nonNull(cart), CANNOT_AUTHORIZE_PAYMENT_WHERE_THERE_IS_NO_CART_MESSAGE);

        final DirectAuthoriseServiceResponse directAuthoriseServiceResponse = worldpayDirectOrderService.createTokenAndAuthorise(cart, worldpayAdditionalInfoData, cseAdditionalAuthInfo);

        return handleDirectServiceResponse(directAuthoriseServiceResponse, cart);
    }

    @Override
    public DirectResponseData authoriseACHDirectDebit(final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final ACHDirectDebitAdditionalAuthInfo additionalAuthInfo) throws WorldpayException, InvalidCartException {
        final CartModel cart = cartService.getSessionCart();
        try {
            final DirectAuthoriseServiceResponse serviceResponse = worldpayDirectOrderService.authoriseACHDirectDebit(cart, additionalAuthInfo, worldpayAdditionalInfoData);
            return handleACHDirectDebitResponse(serviceResponse, cart);
        } catch (final WorldpayConfigurationException e) {
            LOG.error(THERE_IS_NO_CONFIGURATION);
            throw e;
        } catch (final InvalidCartException e) {
            throw new InvalidCartException(MessageFormat.format("There was an error placing the order for cart [{}]", cart.getCode()));
        }
    }

    protected DirectResponseData handleDirectServiceResponse(final DirectAuthoriseServiceResponse serviceResponse, final AbstractOrderModel abstractOrderModel)
        throws WorldpayException, InvalidCartException {
        if (shouldProcessResponse(serviceResponse)) {
            return processDirectResponse(serviceResponse, abstractOrderModel);
        } else {
            return handleErrorOnServiceResponse(serviceResponse);
        }
    }

    protected DirectResponseData handleACHDirectDebitResponse(final DirectAuthoriseServiceResponse serviceResponse, final CartModel abstractOrderModel)
            throws WorldpayException, InvalidCartException {
        if (shouldProcessACHResponse(serviceResponse)) {
            return processACHDirectDebitDirectResponse(serviceResponse, abstractOrderModel);
        } else {
            return handleErrorOnServiceResponse(serviceResponse);
        }
    }

    protected DirectResponseData internalAuthorise3DSecure(final AbstractOrderModel abstractOrderModel,
                                                           final String paResponse,
                                                           final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException {
        setAuthenticatedShopperIdOnAdditionalInfoData(worldpayAdditionalInfoData, abstractOrderModel);

        final DirectAuthoriseServiceResponse serviceResponse = worldpayDirectOrderService.authorise3DSecure(
            abstractOrderModel.getWorldpayOrderCode(),
            worldpayAdditionalInfoData,
            paResponse);
        if (shouldProcessResponse(serviceResponse)) {
            return handle3DSecureResponse(abstractOrderModel, serviceResponse);
        } else if (Objects.nonNull(serviceResponse.getErrorDetail())) {
            final String errorMessage = format(THERE_WAS_AN_ERROR_IN_THE_SERVICE_GATEWAY_MESSAGE, serviceResponse.getErrorDetail().getMessage());
            LOG.error(errorMessage);
            throw new WorldpayException(errorMessage);
        } else {
            throw new WorldpayException(THERE_WAS_AN_ERROR_COMMUNICATING_WITH_WORLDPAY);
        }
    }

    protected DirectResponseData handle3DSecureResponse(final AbstractOrderModel abstractOrderModel,
                                                        final DirectAuthoriseServiceResponse serviceResponse) throws InvalidCartException, WorldpayException {
        final DirectResponseData response = new DirectResponseData();
        return processDirectResponseData(abstractOrderModel, serviceResponse, response);
    }

    protected DirectResponseData processDirectResponseData(final AbstractOrderModel abstractOrderModel, final DirectAuthoriseServiceResponse serviceResponse, final DirectResponseData response) throws InvalidCartException, WorldpayException {
        final PaymentReply paymentReply = serviceResponse.getPaymentReply();
        final AuthorisedStatus authStatus = paymentReply.getAuthStatus();
        if (AUTHORISED.equals(authStatus)) {
            worldpayDirectOrderService.completeAuthorise3DSecure(abstractOrderModel, serviceResponse);
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

    protected DirectResponseData processACHDirectDebitDirectResponse(final DirectAuthoriseServiceResponse serviceResponse,
                                                                     final CartModel abstractOrderModel) throws InvalidCartException, WorldpayException {
        final DirectResponseData response = new DirectResponseData();
        final PaymentReply paymentReply = serviceResponse.getPaymentReply();
        String merchantCode = worldpayMerchantConfigDataFacade.getCurrentSiteMerchantConfigData().getCode();
        if (paymentReply != null) {
            final AuthorisedStatus status = paymentReply.getAuthStatus();
            if (AUTHORISED.equals(status) || CAPTURED.equals(status)) {
                worldpayDirectOrderService.completeAuthoriseACHDirectDebit(serviceResponse, abstractOrderModel, merchantCode);
                handleAuthorisedResponse(response);
            } else if (REFUSED.equals(status)) {
                handleRefusedResponse(response, paymentReply.getReturnCode());
            } else if (CANCELLED.equals(status)) {
                handleCancelledResponse(response);
            }
        } else {
            final String errorMessage = format(ERROR_AUTHORISING_ORDER, abstractOrderModel.getWorldpayOrderCode());
            throwWorldpayException(errorMessage);
        }
        return response;
    }

    protected DirectResponseData processDirectResponse(final DirectAuthoriseServiceResponse serviceResponse,
                                                       final AbstractOrderModel abstractOrderModel) throws InvalidCartException, WorldpayException {
        final DirectResponseData response = new DirectResponseData();
        final PaymentReply paymentReply = serviceResponse.getPaymentReply();
        if (paymentReply != null) {
            final AuthorisedStatus status = paymentReply.getAuthStatus();
            if (AUTHORISED.equals(status)) {
                worldpayDirectOrderService.completeAuthorise(serviceResponse, abstractOrderModel);
                handleAuthorisedResponse(response);
                populateApmAttributes(abstractOrderModel, response);
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

    protected void populateApmAttributes(final AbstractOrderModel abstractOrderModel, final DirectResponseData response) {
        final PaymentInfoModel paymentInfo = abstractOrderModel.getPaymentInfo();
        if (paymentInfo instanceof GooglePayPaymentInfoModel) {
            apmPaymentInfoPopulator.populate((WorldpayAPMPaymentInfoModel) paymentInfo, response.getOrderData().getPaymentInfo());
        }
    }

    protected boolean shouldProcessResponse(final DirectAuthoriseServiceResponse serviceResponse) {
        return serviceResponse.getPaymentReply() != null || serviceResponse.get3DSecureFlow().isPresent();
    }

    protected boolean shouldProcessACHResponse(final DirectAuthoriseServiceResponse serviceResponse) {
        return serviceResponse.getPaymentReply() != null &&
                (serviceResponse.getPaymentReply().getAuthStatus().equals(AUTHORISED) ||
                        serviceResponse.getPaymentReply().getAuthStatus().equals(CAPTURED));
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

    protected DirectResponseData handleErrorOnServiceResponse(final DirectAuthoriseServiceResponse serviceResponse) throws WorldpayException {
        if (Objects.nonNull(serviceResponse.getErrorDetail())) {
            final String errorMessage = format(THERE_WAS_AN_ERROR_IN_THE_SERVICE_GATEWAY_MESSAGE, serviceResponse.getErrorDetail().getMessage());
            LOG.error(errorMessage);
            throw new WorldpayException(errorMessage);
        } else {
            throw new WorldpayException(THERE_WAS_AN_ERROR_COMMUNICATING_WITH_WORLDPAY);
        }
    }

    private void throwWorldpayException(final String errorMessage) throws WorldpayException {
        LOG.error(errorMessage);
        throw new WorldpayException(errorMessage);
    }

    public CartService getCartService() {
        return cartService;
    }
}
