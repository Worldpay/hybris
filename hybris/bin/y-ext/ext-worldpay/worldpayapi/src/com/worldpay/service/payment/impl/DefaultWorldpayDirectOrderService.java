package com.worldpay.service.payment.impl;

import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.data.*;
import com.worldpay.data.token.TokenReply;
import com.worldpay.enums.token.TokenEvent;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.payment.WorldpayDirectOrderService;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.payment.WorldpaySessionService;
import com.worldpay.service.payment.request.WorldpayRequestFactory;
import com.worldpay.service.request.*;
import com.worldpay.service.response.CreateTokenResponse;
import com.worldpay.service.response.DeleteTokenResponse;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import com.worldpay.service.response.UpdateTokenResponse;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static com.worldpay.enums.token.TokenEvent.CONFLICT;

/**
 * Default implementation of {@link WorldpayDirectOrderService}
 */
public class DefaultWorldpayDirectOrderService extends AbstractWorldpayOrderService implements WorldpayDirectOrderService {

    protected final WorldpayRequestFactory worldpayRequestFactory;
    protected final WorldpaySessionService worldpaySessionService;
    protected final WorldpayMerchantInfoService worldpayMerchantInfoService;

    public DefaultWorldpayDirectOrderService(final WorldpayRequestFactory worldpayRequestFactory,
                                             final WorldpaySessionService worldpaySessionService,
                                             final WorldpayPaymentInfoService worldpayPaymentInfoService,
                                             final WorldpayPaymentTransactionService worldpayPaymentTransactionService,
                                             final WorldpayOrderService worldpayOrderService,
                                             final WorldpayServiceGateway worldpayServiceGateway,
                                             final WorldpayMerchantInfoService worldpayMerchantInfoService) {
        super(worldpayPaymentInfoService, worldpayPaymentTransactionService, worldpayOrderService, worldpayServiceGateway);
        this.worldpayRequestFactory = worldpayRequestFactory;
        this.worldpaySessionService = worldpaySessionService;
        this.worldpayMerchantInfoService = worldpayMerchantInfoService;
    }

    protected boolean createTokenRepliesWithConflict(final CreateTokenResponse createTokenResponse) {
        return CONFLICT.name().equals(createTokenResponse.getToken().getTokenDetails().getTokenEvent());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectAuthoriseServiceResponse authorise(final CartModel cartModel, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException {

        final MerchantInfo merchantInfo = worldpayMerchantInfoService.getCurrentSiteMerchant();
        final DirectAuthoriseServiceRequest directAuthoriseRequest = worldpayRequestFactory.buildDirectAuthoriseRequestWithTokenForCSE(merchantInfo, cartModel, worldpayAdditionalInfoData);
        final DirectAuthoriseServiceResponse response = worldpayServiceGateway.directAuthorise(directAuthoriseRequest);
        worldpaySessionService.setSessionAttributesFor3DSecure(response, worldpayAdditionalInfoData);

        return response;
    }

    @Override
    public DirectAuthoriseServiceResponse createTokenAndAuthorise(final CartModel cartModel, final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final CSEAdditionalAuthInfo cseAdditionalAuthInfo) throws WorldpayException {
        final MerchantInfo merchantInfo = worldpayMerchantInfoService.getCurrentSiteMerchant();
        final DirectAuthoriseServiceRequest directAuthoriseServiceRequest = worldpayRequestFactory.buildDirectTokenAndAuthorise(merchantInfo, cartModel, worldpayAdditionalInfoData, cseAdditionalAuthInfo);
        final DirectAuthoriseServiceResponse directAuthoriseServiceResponse = worldpayServiceGateway.directAuthorise(directAuthoriseServiceRequest);
        worldpaySessionService.setSessionAttributesFor3DSecure(directAuthoriseServiceResponse, worldpayAdditionalInfoData);
        if (directAuthoriseServiceResponse.isError()) {
            throw new WorldpayException(directAuthoriseServiceResponse.getErrorDetail().getMessage());
        }
        return directAuthoriseServiceResponse;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectAuthoriseServiceResponse authoriseRecurringPayment(final AbstractOrderModel abstractOrderModel,
                                                                    final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException {
        final MerchantInfo merchantInfo = worldpayMerchantInfoService.getCurrentSiteMerchant();
        final DirectAuthoriseServiceRequest directAuthoriseRequest = worldpayRequestFactory.buildDirectAuthoriseRecurringPayment(merchantInfo, abstractOrderModel, worldpayAdditionalInfoData);
        final DirectAuthoriseServiceResponse response = worldpayServiceGateway.directAuthorise(directAuthoriseRequest);

        worldpaySessionService.setSessionAttributesFor3DSecure(response, worldpayAdditionalInfoData);

        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectAuthoriseServiceResponse authoriseKlarna(final CartModel cartModel, final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final AdditionalAuthInfo additionalAuthInfo) throws WorldpayException {
        final MerchantInfo merchantInfo = worldpayMerchantInfoService.getCurrentSiteMerchant();
        final DirectAuthoriseServiceRequest directAuthoriseKlarnaRequest = worldpayRequestFactory.buildDirectAuthoriseKlarnaRequest(
            merchantInfo, cartModel, worldpayAdditionalInfoData, additionalAuthInfo);
        return worldpayServiceGateway.directAuthorise(directAuthoriseKlarnaRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectAuthoriseServiceResponse authoriseBankTransfer(final CartModel cartModel,
                                                                final BankTransferAdditionalAuthInfo bankTransferAdditionalAuthInfo,
                                                                final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException {
        final MerchantInfo merchantInfo = worldpayMerchantInfoService.getCurrentSiteMerchant();
        final DirectAuthoriseServiceRequest directAuthoriseBankTransferRequest = worldpayRequestFactory.buildDirectAuthoriseBankTransferRequest(
            merchantInfo, cartModel, bankTransferAdditionalAuthInfo, worldpayAdditionalInfoData);
        return worldpayServiceGateway.directAuthorise(directAuthoriseBankTransferRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectAuthoriseServiceResponse authoriseApplePay(final CartModel cartModel, final ApplePayAdditionalAuthInfo applePayAdditionalAuthInfo) throws WorldpayException {
        final MerchantInfo merchantInfo = worldpayMerchantInfoService.getCurrentSiteMerchant();
        final DirectAuthoriseServiceRequest directAuthoriseApplePayRequest = worldpayRequestFactory.buildApplePayDirectAuthorisationRequest(
            merchantInfo, cartModel, applePayAdditionalAuthInfo);
        return worldpayServiceGateway.directAuthorise(directAuthoriseApplePayRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectAuthoriseServiceResponse authoriseGooglePay(final CartModel cartModel, final GooglePayAdditionalAuthInfo googlePayAdditionalAuthInfo) throws WorldpayException {
        final MerchantInfo merchantInfo = worldpayMerchantInfoService.getCurrentSiteMerchant();
        final DirectAuthoriseServiceRequest directAuthoriseGooglePayRequest = worldpayRequestFactory.buildDirectAuthoriseGooglePayRequest(merchantInfo, cartModel, googlePayAdditionalAuthInfo);
        return worldpayServiceGateway.directAuthorise(directAuthoriseGooglePayRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectAuthoriseServiceResponse authorise3DSecureAgain(final String worldpayOrderCode) throws WorldpayException {
        final MerchantInfo merchantInfo = worldpayMerchantInfoService.getCurrentSiteMerchant();
        final String cookie = worldpaySessionService.getAndRemoveThreeDSecureCookie();
        final String sessionId = worldpaySessionService.getAndRemoveAdditionalDataSessionId();
        final SecondThreeDSecurePaymentRequest request = worldpayRequestFactory.buildSecondThreeDSecurePaymentRequest(merchantInfo, worldpayOrderCode, sessionId, cookie);
        return worldpayServiceGateway.sendSecondThreeDSecurePayment(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createToken(final CartModel cartModel, final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData)
        throws WorldpayException {
        final MerchantInfo merchantInfo = worldpayMerchantInfoService.getCurrentSiteMerchant();
        final CreateTokenServiceRequest createTokenRequest = worldpayRequestFactory.buildTokenRequest(merchantInfo, cartModel, cseAdditionalAuthInfo, worldpayAdditionalInfoData);
        final CreateTokenResponse createTokenResponse = worldpayServiceGateway.createToken(createTokenRequest);
        if (createTokenResponse.isError()) {
            throw new WorldpayException(createTokenResponse.getErrorDetail().getMessage());
        }
        final Boolean saveCard = cseAdditionalAuthInfo.getSaveCard();
        final String merchantCode = merchantInfo.getMerchantCode();

        final CreditCardPaymentInfoModel creditCardPaymentInfo = handleCreateTokenResponse(merchantInfo, cartModel, cseAdditionalAuthInfo, worldpayAdditionalInfoData, createTokenResponse, saveCard, merchantCode);
        worldpayPaymentInfoService.setPaymentInfoOnCart(cartModel, creditCardPaymentInfo);
    }

    protected CreditCardPaymentInfoModel handleCreateTokenResponse(final MerchantInfo merchantInfo, final CartModel cartModel, final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final CreateTokenResponse createTokenResponse, final Boolean saveCard, final String merchantCode) throws WorldpayException {
        final CreditCardPaymentInfoModel creditCardPaymentInfoModel;
        if (createTokenRepliesWithConflict(createTokenResponse)) {
            creditCardPaymentInfoModel = handleTokenConflict(merchantInfo, cartModel, cseAdditionalAuthInfo, worldpayAdditionalInfoData, createTokenResponse, saveCard, merchantCode);
        } else {
            creditCardPaymentInfoModel = worldpayPaymentInfoService.createCreditCardPaymentInfo(cartModel, createTokenResponse.getToken(), saveCard, merchantCode);
        }
        return creditCardPaymentInfoModel;
    }

    protected CreditCardPaymentInfoModel handleTokenConflict(final MerchantInfo merchantInfo, final CartModel cartModel, final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final CreateTokenResponse createTokenResponse, final Boolean saveCard, final String merchantCode) throws WorldpayException {
        final CreditCardPaymentInfoModel creditCardPaymentInfoModel;
        final UpdateTokenServiceRequest updateTokenServiceRequest = worldpayRequestFactory.buildTokenUpdateRequest(merchantInfo,
            cseAdditionalAuthInfo,
            worldpayAdditionalInfoData,
            cartModel.getPaymentAddress(),
            createTokenResponse);
        final UpdateTokenResponse updateTokenResponse = worldpayServiceGateway.updateToken(updateTokenServiceRequest);
        if (updateTokenResponse.isError()) {
            throw new WorldpayException(updateTokenResponse.getErrorDetail().getMessage());
        }
        creditCardPaymentInfoModel = worldpayPaymentInfoService.updateCreditCardPaymentInfo(cartModel, updateTokenServiceRequest, saveCard)
            .orElseGet(() ->
                worldpayPaymentInfoService.createCreditCardPaymentInfo(cartModel, createTokenResponse.getToken(), saveCard, merchantCode));
        return creditCardPaymentInfoModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteToken(final MerchantInfo merchantInfo, final PaymentInfoModel paymentInfoModel, final String subscriptionId)
        throws WorldpayException {
        final DeleteTokenServiceRequest deleteTokenServiceRequest = worldpayRequestFactory.buildTokenDeleteRequest(merchantInfo, paymentInfoModel, subscriptionId);
        final DeleteTokenResponse deleteTokenResponse = worldpayServiceGateway.deleteToken(deleteTokenServiceRequest);
        if (deleteTokenResponse.isError()) {
            throw new WorldpayException(deleteTokenResponse.getErrorDetail().getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectAuthoriseServiceResponse authorise3DSecure(final String worldpayOrderCode,
                                                            final WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                            final String paResponse) throws WorldpayException {
        final String cookie = worldpaySessionService.getAndRemoveThreeDSecureCookie();
        final MerchantInfo merchantInfo = worldpayMerchantInfoService.getCurrentSiteMerchant();

        final DirectAuthoriseServiceRequest directAuthoriseServiceRequest = worldpayRequestFactory.build3dDirectAuthoriseRequest(
            merchantInfo, worldpayOrderCode, worldpayAdditionalInfoData, paResponse, cookie);
        return worldpayServiceGateway.directAuthorise(directAuthoriseServiceRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completeAuthorise(final DirectAuthoriseServiceResponse serviceResponse, final AbstractOrderModel abstractOrderModel) throws WorldpayConfigurationException {
        final String merchantCode = worldpayMerchantInfoService.getCurrentSiteMerchant().getMerchantCode();
        final PaymentInfoModel paymentInfoModel = Optional.ofNullable(abstractOrderModel.getPaymentInfo())
            .orElseGet(() -> createCreditCardPaymentInfo(serviceResponse, abstractOrderModel, merchantCode));
        final PaymentReply paymentReply = serviceResponse.getPaymentReply();
        final BigDecimal authorisationAmount = worldpayOrderService.convertAmount(paymentReply.getAmount());
        final CommerceCheckoutParameter commerceCheckoutParameter;

        if (abstractOrderModel instanceof CartModel) {
            final CartModel cartModel = (CartModel) abstractOrderModel;
            cloneAndSetBillingAddressFromCart(cartModel, paymentInfoModel);
            commerceCheckoutParameter = worldpayOrderService.createCheckoutParameterAndSetPaymentInfo(paymentInfoModel, authorisationAmount, cartModel);
        } else {
            commerceCheckoutParameter = worldpayOrderService.createCommerceCheckoutParameter(abstractOrderModel, paymentInfoModel, authorisationAmount);
        }

        final PaymentTransactionModel paymentTransaction = worldpayPaymentTransactionService.createPaymentTransaction(false, merchantCode, commerceCheckoutParameter);
        worldpayPaymentTransactionService.addRiskScore(paymentTransaction, paymentReply);

        worldpayPaymentTransactionService.addFraudSightToPaymentTransaction(paymentTransaction, paymentReply);

        final PaymentTransactionEntryModel transactionEntry = worldpayPaymentTransactionService.createNonPendingAuthorisePaymentTransactionEntry(paymentTransaction,
            merchantCode,
            abstractOrderModel,
            authorisationAmount);
        worldpayPaymentTransactionService.addAavFields(transactionEntry, paymentReply);
        getTransactionIdentifier(serviceResponse)
            .ifPresent(transactionIdentifier ->
                worldpayPaymentInfoService.setTransactionIdentifierOnPaymentInfo(paymentInfoModel, transactionIdentifier));
        worldpayPaymentInfoService.updateAndAttachPaymentInfoModel(paymentTransaction, abstractOrderModel, paymentInfoModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completeAuthoriseGooglePay(final DirectAuthoriseServiceResponse serviceResponse, final AbstractOrderModel abstractOrderModel, final String merchantCode) {
        final PaymentInfoModel paymentInfoModel = abstractOrderModel.getPaymentInfo();
        final BigDecimal authorisationAmount = worldpayOrderService.convertAmount(serviceResponse.getPaymentReply().getAmount());
        final CommerceCheckoutParameter commerceCheckoutParameter;

        if (abstractOrderModel instanceof CartModel) {
            final CartModel cartModel = (CartModel) abstractOrderModel;
            cloneAndSetBillingAddressFromCart(cartModel, paymentInfoModel);
            commerceCheckoutParameter = worldpayOrderService.createCheckoutParameterAndSetPaymentInfo(paymentInfoModel, authorisationAmount, cartModel);
            final PaymentTransactionModel paymentTransaction = worldpayPaymentTransactionService.createPaymentTransaction(false, merchantCode, commerceCheckoutParameter);
            worldpayPaymentTransactionService.createPendingAuthorisePaymentTransactionEntry(paymentTransaction,
                merchantCode,
                cartModel,
                authorisationAmount);
            worldpayPaymentInfoService.updateAndAttachPaymentInfoModel(paymentTransaction, abstractOrderModel, paymentInfoModel);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completeAuthorise3DSecure(final AbstractOrderModel abstractOrderModel, final DirectAuthoriseServiceResponse serviceResponse) throws WorldpayConfigurationException {
        completeAuthorise(serviceResponse, abstractOrderModel);
    }

    protected Optional<String> getTransactionIdentifier(final DirectAuthoriseServiceResponse serviceResponse) {
        return Optional.ofNullable(serviceResponse.getPaymentReply().getSchemeResponse())
                .map(SchemeResponse::getTransactionIdentifier);
    }

    protected Optional<Boolean> checkTokenReferenceExists(final DirectAuthoriseServiceResponse serviceResponse) {
        return Optional.ofNullable(serviceResponse.getToken())
                .map(TokenReply::getTokenDetails)
                .map(tokenDetails -> StringUtils.isNotBlank(tokenDetails.getTokenEventReference()));
    }

    protected CreditCardPaymentInfoModel createCreditCardPaymentInfo(final DirectAuthoriseServiceResponse serviceResponse, final AbstractOrderModel abstractOrderModel, final String merchantCode) {
        final Optional<Boolean> saveCard = checkTokenReferenceExists(serviceResponse);
        return worldpayPaymentInfoService.createCreditCardPaymentInfo(abstractOrderModel, serviceResponse.getToken(), saveCard.orElse(false), merchantCode);
    }
}
