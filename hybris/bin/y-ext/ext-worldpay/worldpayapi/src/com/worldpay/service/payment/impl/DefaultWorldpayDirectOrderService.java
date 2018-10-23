package com.worldpay.service.payment.impl;

import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.data.BankTransferAdditionalAuthInfo;
import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.data.GooglePayAdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.payment.WorldpayDirectOrderService;
import com.worldpay.service.payment.request.WorldpayRequestFactory;
import com.worldpay.service.request.CreateTokenServiceRequest;
import com.worldpay.service.request.DeleteTokenServiceRequest;
import com.worldpay.service.request.DirectAuthoriseServiceRequest;
import com.worldpay.service.request.UpdateTokenServiceRequest;
import com.worldpay.service.response.CreateTokenResponse;
import com.worldpay.service.response.DeleteTokenResponse;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import com.worldpay.service.response.UpdateTokenResponse;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.session.SessionService;
import org.springframework.beans.factory.annotation.Required;

import java.math.BigDecimal;
import java.util.Optional;

import static com.worldpay.enums.token.TokenEvent.CONFLICT;

/**
 * Default implementation of {@link WorldpayDirectOrderService}
 */
public class DefaultWorldpayDirectOrderService extends AbstractWorldpayOrderService implements WorldpayDirectOrderService {

    private static final String THREE_D_SECURE_ECHO_DATA_PARAM = "3DSecureEchoData";
    private static final String THREE_D_SECURE_COOKIE_PARAM = "3DSecureCookie";

    private SessionService sessionService;
    private WorldpayRequestFactory worldpayRequestFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectAuthoriseServiceResponse authorise(final MerchantInfo merchantInfo, final CartModel cartModel, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException {

        final DirectAuthoriseServiceRequest directAuthoriseRequest = worldpayRequestFactory.buildDirectAuthoriseRequestWithTokenForCSE(merchantInfo, cartModel, worldpayAdditionalInfoData);
        final DirectAuthoriseServiceResponse response = getWorldpayServiceGateway().directAuthorise(directAuthoriseRequest);
        if (response.is3DSecured()) {
          /*
          In case the transaction requires 3d secure, two strings need to be placed in the users session: echoData and a cookie.
          These are needed to successfully reference the initial transaction in Worldpay when the user comes back from the 3d secure page.
          Example values:
              echoData=148556494881709
              cookie=machine=0ab20014;path=/
           */
            sessionService.setAttribute(THREE_D_SECURE_COOKIE_PARAM, response.getCookie());
            sessionService.setAttribute(THREE_D_SECURE_ECHO_DATA_PARAM, response.getEchoData());
        }
        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectAuthoriseServiceResponse authoriseRecurringPayment(final MerchantInfo merchantInfo, final AbstractOrderModel abstractOrderModel,
                                                                    final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException {
        final DirectAuthoriseServiceRequest directAuthoriseRequest = worldpayRequestFactory.buildDirectAuthoriseRecurringPayment(merchantInfo, abstractOrderModel, worldpayAdditionalInfoData);
        final DirectAuthoriseServiceResponse response = getWorldpayServiceGateway().directAuthorise(directAuthoriseRequest);
        if (response.is3DSecured()) {
            sessionService.setAttribute(THREE_D_SECURE_COOKIE_PARAM, response.getCookie());
            sessionService.setAttribute(THREE_D_SECURE_ECHO_DATA_PARAM, response.getEchoData());
        }
        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectAuthoriseServiceResponse authoriseKlarna(final MerchantInfo merchantInfo, final CartModel cartModel, final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final AdditionalAuthInfo additionalAuthInfo) throws WorldpayException {
        final DirectAuthoriseServiceRequest directAuthoriseKlarnaRequest = worldpayRequestFactory.buildDirectAuthoriseKlarnaRequest(
                merchantInfo, cartModel, worldpayAdditionalInfoData, additionalAuthInfo);
        return getWorldpayServiceGateway().directAuthorise(directAuthoriseKlarnaRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectAuthoriseServiceResponse authoriseBankTransfer(final MerchantInfo merchantInfo, final CartModel cartModel,
                                                                final BankTransferAdditionalAuthInfo bankTransferAdditionalAuthInfo,
                                                                final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException {
        final DirectAuthoriseServiceRequest directAuthoriseBankTransferRequest = worldpayRequestFactory.buildDirectAuthoriseBankTransferRequest(
                merchantInfo, cartModel, bankTransferAdditionalAuthInfo, worldpayAdditionalInfoData);
        return getWorldpayServiceGateway().directAuthorise(directAuthoriseBankTransferRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectAuthoriseServiceResponse authoriseGooglePay(final MerchantInfo merchantInfo, final CartModel cartModel, final GooglePayAdditionalAuthInfo googlePayAdditionalAuthInfo) throws WorldpayException {
        final DirectAuthoriseServiceRequest directAuthoriseGooglePayRequest = worldpayRequestFactory.buildDirectAuthoriseGooglePayRequest(merchantInfo, cartModel, googlePayAdditionalAuthInfo);
        return getWorldpayServiceGateway().directAuthorise(directAuthoriseGooglePayRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createToken(final MerchantInfo merchantInfo, final CartModel cartModel, final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData)
            throws WorldpayException {
        final CreateTokenServiceRequest createTokenRequest = worldpayRequestFactory.buildTokenRequest(merchantInfo, cartModel, cseAdditionalAuthInfo, worldpayAdditionalInfoData);
        final CreateTokenResponse createTokenResponse = getWorldpayServiceGateway().createToken(createTokenRequest);
        if (createTokenResponse.isError()) {
            throw new WorldpayException(createTokenResponse.getErrorDetail().getMessage());
        }
        final Boolean saveCard = cseAdditionalAuthInfo.getSaveCard();
        final String merchantCode = merchantInfo.getMerchantCode();

        Optional.ofNullable(handleCreateTokenResponse(merchantInfo, cartModel, cseAdditionalAuthInfo, worldpayAdditionalInfoData, createTokenResponse, saveCard, merchantCode))
                .ifPresent(creditCardPaymentInfoModel -> {
                    final CommerceCheckoutParameter commerceCheckoutParameter = new CommerceCheckoutParameter();
                    commerceCheckoutParameter.setCart(cartModel);
                    commerceCheckoutParameter.setPaymentInfo(creditCardPaymentInfoModel);
                    getCommerceCheckoutService().setPaymentInfo(commerceCheckoutParameter);
                });
    }

    protected CreditCardPaymentInfoModel handleCreateTokenResponse(final MerchantInfo merchantInfo, final CartModel cartModel, final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final CreateTokenResponse createTokenResponse, final Boolean saveCard, final String merchantCode) throws WorldpayException {
        final CreditCardPaymentInfoModel creditCardPaymentInfoModel;
        if (createTokenRepliesWithConflict(createTokenResponse)) {
            creditCardPaymentInfoModel = handleTokenConflict(merchantInfo, cartModel, cseAdditionalAuthInfo, worldpayAdditionalInfoData, createTokenResponse, saveCard, merchantCode);
        } else {
            creditCardPaymentInfoModel = getWorldpayPaymentInfoService().createCreditCardPaymentInfo(cartModel, createTokenResponse, saveCard, merchantCode);
        }
        return creditCardPaymentInfoModel;
    }

    protected CreditCardPaymentInfoModel handleTokenConflict(final MerchantInfo merchantInfo, final CartModel cartModel, final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final CreateTokenResponse createTokenResponse, final Boolean saveCard, final String merchantCode) throws WorldpayException {
        final CreditCardPaymentInfoModel creditCardPaymentInfoModel;
        final UpdateTokenServiceRequest updateTokenServiceRequest = worldpayRequestFactory.buildTokenUpdateRequest(merchantInfo,
                cseAdditionalAuthInfo,
                worldpayAdditionalInfoData,
                createTokenResponse);
        final UpdateTokenResponse updateTokenResponse = getWorldpayServiceGateway().updateToken(updateTokenServiceRequest);
        if (updateTokenResponse.isError()) {
            throw new WorldpayException(updateTokenResponse.getErrorDetail().getMessage());
        }
        creditCardPaymentInfoModel = getWorldpayPaymentInfoService().updateCreditCardPaymentInfo(cartModel, updateTokenServiceRequest)
                .orElseGet(() ->
                        getWorldpayPaymentInfoService().createCreditCardPaymentInfo(cartModel, createTokenResponse, saveCard, merchantCode));
        return creditCardPaymentInfoModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteToken(final MerchantInfo merchantInfo, final CreditCardPaymentInfoModel creditCardPaymentInfoModel)
            throws WorldpayException {
        final DeleteTokenServiceRequest deleteTokenServiceRequest = worldpayRequestFactory.buildTokenDeleteRequest(merchantInfo, creditCardPaymentInfoModel);
        final DeleteTokenResponse deleteTokenResponse = getWorldpayServiceGateway().deleteToken(deleteTokenServiceRequest);
        if (deleteTokenResponse.isError()) {
            throw new WorldpayException(deleteTokenResponse.getErrorDetail().getMessage());
        }
    }

    protected boolean createTokenRepliesWithConflict(final CreateTokenResponse createTokenResponse) {
        return CONFLICT.name().equals(createTokenResponse.getToken().getTokenDetails().getTokenEvent());
    }

    /**
     * {@inheritDoc}DefaultWorldpayOrderService.java
     */
    @Override
    public DirectAuthoriseServiceResponse authorise3DSecure(final MerchantInfo merchantInfo,
                                                            final String worldpayOrderCode,
                                                            final WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                            final String paResponse) throws WorldpayException {
        final String cookie = getAndRemoveSessionAttribute(THREE_D_SECURE_COOKIE_PARAM);

        final DirectAuthoriseServiceRequest directAuthoriseServiceRequest = worldpayRequestFactory.build3dDirectAuthoriseRequest(
                merchantInfo, worldpayOrderCode, worldpayAdditionalInfoData, paResponse, cookie);
        return getWorldpayServiceGateway().directAuthorise(directAuthoriseServiceRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completeAuthorise(final DirectAuthoriseServiceResponse serviceResponse, final AbstractOrderModel abstractOrderModel, final String merchantCode) {
        final PaymentInfoModel paymentInfoModel = abstractOrderModel.getPaymentInfo();
        final BigDecimal authorisationAmount = getWorldpayOrderService().convertAmount(serviceResponse.getPaymentReply().getAmount());
        final CommerceCheckoutParameter commerceCheckoutParameter;

        if (abstractOrderModel instanceof CartModel) {
            final CartModel cartModel = (CartModel) abstractOrderModel;
            cloneAndSetBillingAddressFromCart(cartModel, paymentInfoModel);
            commerceCheckoutParameter = createCommerceCheckoutParameter(cartModel, paymentInfoModel, authorisationAmount);
            getCommerceCheckoutService().setPaymentInfo(commerceCheckoutParameter);
        } else {
            commerceCheckoutParameter = createCommerceCheckoutParameter(abstractOrderModel, paymentInfoModel, authorisationAmount);
        }

        final PaymentTransactionModel paymentTransaction = getWorldpayPaymentTransactionService().createPaymentTransaction(false, merchantCode, commerceCheckoutParameter);
        getWorldpayPaymentTransactionService().addRiskScore(paymentTransaction, serviceResponse.getPaymentReply());

        final PaymentTransactionEntryModel transactionEntry = getWorldpayPaymentTransactionService().createNonPendingAuthorisePaymentTransactionEntry(paymentTransaction,
                merchantCode,
                abstractOrderModel,
                authorisationAmount);
        getWorldpayPaymentTransactionService().addAavFields(transactionEntry, serviceResponse.getPaymentReply());
        getWorldpayPaymentInfoService().updateAndAttachPaymentInfoModel(paymentTransaction, abstractOrderModel, paymentInfoModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completeAuthoriseGooglePay(final DirectAuthoriseServiceResponse serviceResponse, final AbstractOrderModel abstractOrderModel, final String merchantCode) {
        final PaymentInfoModel paymentInfoModel = abstractOrderModel.getPaymentInfo();
        final BigDecimal authorisationAmount = getWorldpayOrderService().convertAmount(serviceResponse.getPaymentReply().getAmount());
        final CommerceCheckoutParameter commerceCheckoutParameter;

        if (abstractOrderModel instanceof CartModel) {
            final CartModel cartModel = (CartModel) abstractOrderModel;
            cloneAndSetBillingAddressFromCart(cartModel, paymentInfoModel);
            commerceCheckoutParameter = createCommerceCheckoutParameter(cartModel, paymentInfoModel, authorisationAmount);
            getCommerceCheckoutService().setPaymentInfo(commerceCheckoutParameter);
            final PaymentTransactionModel paymentTransaction = getWorldpayPaymentTransactionService().createPaymentTransaction(false, merchantCode, commerceCheckoutParameter);
            getWorldpayPaymentTransactionService().createPendingAuthorisePaymentTransactionEntry(paymentTransaction,
                    merchantCode,
                    cartModel,
                    authorisationAmount);
            getWorldpayPaymentInfoService().updateAndAttachPaymentInfoModel(paymentTransaction, abstractOrderModel, paymentInfoModel);

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completeAuthorise3DSecure(final AbstractOrderModel abstractOrderModel, final DirectAuthoriseServiceResponse serviceResponse, final MerchantInfo merchantInfo) {
        completeAuthorise(serviceResponse, abstractOrderModel, merchantInfo.getMerchantCode());
    }

    private String getAndRemoveSessionAttribute(final String param) {
        final String attribute = sessionService.getAttribute(param);
        sessionService.removeAttribute(param);
        return attribute;
    }

    @Required
    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Required
    public void setWorldpayRequestFactory(final WorldpayRequestFactory worldpayRequestFactory) {
        this.worldpayRequestFactory = worldpayRequestFactory;
    }
}
