package com.worldpay.service.payment.impl;

import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.data.*;
import com.worldpay.exception.WorldpayException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.Address;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.model.SchemeResponse;
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
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.strategies.GenerateMerchantTransactionCodeStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.AddressService;

import java.math.BigDecimal;
import java.util.Optional;

import static com.worldpay.enums.token.TokenEvent.CONFLICT;

/**
 * Default implementation of {@link WorldpayDirectOrderService}
 */
public class DefaultWorldpayDirectOrderService extends AbstractWorldpayOrderService implements WorldpayDirectOrderService {
    private final WorldpayRequestFactory worldpayRequestFactory;
    private final WorldpaySessionService worldpaySessionService;

    public DefaultWorldpayDirectOrderService(final WorldpayRequestFactory worldpayRequestFactory, final WorldpaySessionService worldpaySessionService, final CommonI18NService commonI18NService, final CommerceCheckoutService commerceCheckoutService, final CustomerEmailResolutionService customerEmailResolutionService, final GenerateMerchantTransactionCodeStrategy worldpayGenerateMerchantTransactionCodeStrategy, final WorldpayPaymentInfoService worldpayPaymentInfoService, final WorldpayPaymentTransactionService worldpayPaymentTransactionService, final WorldpayOrderService worldpayOrderService, final Converter<AddressModel, Address> worldpayAddressConverter, final WorldpayServiceGateway worldpayServiceGateway, final AddressService addressService) {
        super(commonI18NService, commerceCheckoutService, customerEmailResolutionService, worldpayGenerateMerchantTransactionCodeStrategy, worldpayPaymentInfoService, worldpayPaymentTransactionService, worldpayOrderService, worldpayAddressConverter, worldpayServiceGateway, addressService);
        this.worldpayRequestFactory = worldpayRequestFactory;
        this.worldpaySessionService = worldpaySessionService;
    }


    protected boolean createTokenRepliesWithConflict(final CreateTokenResponse createTokenResponse) {
        return CONFLICT.name().equals(createTokenResponse.getToken().getTokenDetails().getTokenEvent());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectAuthoriseServiceResponse authorise(final MerchantInfo merchantInfo, final CartModel cartModel, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException {

        final DirectAuthoriseServiceRequest directAuthoriseRequest = worldpayRequestFactory.buildDirectAuthoriseRequestWithTokenForCSE(merchantInfo, cartModel, worldpayAdditionalInfoData);
        final DirectAuthoriseServiceResponse response = getWorldpayServiceGateway().directAuthorise(directAuthoriseRequest);
        worldpaySessionService.setSessionAttributesFor3DSecure(response, worldpayAdditionalInfoData);

        return response;
    }

    @Override
    public DirectAuthoriseServiceResponse createTokenAndAuthorise(final MerchantInfo merchantInfo, final CartModel cartModel, final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final CSEAdditionalAuthInfo cseAdditionalAuthInfo) throws WorldpayException {
        final DirectAuthoriseServiceRequest directAuthoriseServiceRequest = worldpayRequestFactory.buildDirectTokenAndAuthorise(merchantInfo, cartModel, worldpayAdditionalInfoData, cseAdditionalAuthInfo);
        final DirectAuthoriseServiceResponse directAuthoriseServiceResponse = getWorldpayServiceGateway().directAuthorise(directAuthoriseServiceRequest);
        worldpaySessionService.setSessionAttributesFor3DSecure(directAuthoriseServiceResponse, worldpayAdditionalInfoData);
        if (directAuthoriseServiceResponse.isError()) {
            throw new WorldpayException(directAuthoriseServiceResponse.getErrorDetail().getMessage());
        }
        return directAuthoriseServiceResponse;
    }

    protected void setPaymentInfoOnCart(final CartModel cartModel, final CreditCardPaymentInfoModel creditCardPaymentInfo) {
        Optional.ofNullable(creditCardPaymentInfo).ifPresent(creditCardPaymentInfoModel -> {
            final CommerceCheckoutParameter commerceCheckoutParameter = new CommerceCheckoutParameter();
            commerceCheckoutParameter.setCart(cartModel);
            commerceCheckoutParameter.setPaymentInfo(creditCardPaymentInfoModel);
            getCommerceCheckoutService().setPaymentInfo(commerceCheckoutParameter);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectAuthoriseServiceResponse authoriseRecurringPayment(final MerchantInfo merchantInfo, final AbstractOrderModel abstractOrderModel,
                                                                    final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException {
        final DirectAuthoriseServiceRequest directAuthoriseRequest = worldpayRequestFactory.buildDirectAuthoriseRecurringPayment(merchantInfo, abstractOrderModel, worldpayAdditionalInfoData);
        final DirectAuthoriseServiceResponse response = getWorldpayServiceGateway().directAuthorise(directAuthoriseRequest);

        worldpaySessionService.setSessionAttributesFor3DSecure(response, worldpayAdditionalInfoData);

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
    public DirectAuthoriseServiceResponse authoriseApplePay(final MerchantInfo merchantInfo, final CartModel cartModel, final ApplePayAdditionalAuthInfo applePayAdditionalAuthInfo) throws WorldpayException {
        final DirectAuthoriseServiceRequest directAuthoriseApplePayRequest = worldpayRequestFactory.buildApplePayDirectAuthorisationRequest(
                merchantInfo, cartModel, applePayAdditionalAuthInfo);
        return getWorldpayServiceGateway().directAuthorise(directAuthoriseApplePayRequest);
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
     * @return
     */
    @Override
    public DirectAuthoriseServiceResponse authorise3DSecureAgain(final MerchantInfo merchantInfo, final String worldpayOrderCode) throws WorldpayException {
        final String cookie = worldpaySessionService.getAndRemoveThreeDSecureCookie();
        final String sessionId = worldpaySessionService.getAndRemoveAdditionalDataSessionId();
        final SecondThreeDSecurePaymentRequest request = worldpayRequestFactory.buildSecondThreeDSecurePaymentRequest(merchantInfo, worldpayOrderCode, sessionId, cookie);
        return getWorldpayServiceGateway().sendSecondThreeDSecurePayment(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createToken(final CartModel cartModel, final MerchantInfo merchantInfo, final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData)
            throws WorldpayException {
        final CreateTokenServiceRequest createTokenRequest = worldpayRequestFactory.buildTokenRequest(merchantInfo, cartModel, cseAdditionalAuthInfo, worldpayAdditionalInfoData);
        final CreateTokenResponse createTokenResponse = getWorldpayServiceGateway().createToken(createTokenRequest);
        if (createTokenResponse.isError()) {
            throw new WorldpayException(createTokenResponse.getErrorDetail().getMessage());
        }
        final Boolean saveCard = cseAdditionalAuthInfo.getSaveCard();
        final String merchantCode = merchantInfo.getMerchantCode();

        final CreditCardPaymentInfoModel creditCardPaymentInfo = handleCreateTokenResponse(merchantInfo, cartModel, cseAdditionalAuthInfo, worldpayAdditionalInfoData, createTokenResponse, saveCard, merchantCode);
        setPaymentInfoOnCart(cartModel, creditCardPaymentInfo);
    }

    protected CreditCardPaymentInfoModel handleCreateTokenResponse(final MerchantInfo merchantInfo, final CartModel cartModel, final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final CreateTokenResponse createTokenResponse, final Boolean saveCard, final String merchantCode) throws WorldpayException {
        final CreditCardPaymentInfoModel creditCardPaymentInfoModel;
        if (createTokenRepliesWithConflict(createTokenResponse)) {
            creditCardPaymentInfoModel = handleTokenConflict(merchantInfo, cartModel, cseAdditionalAuthInfo, worldpayAdditionalInfoData, createTokenResponse, saveCard, merchantCode);
        } else {
            creditCardPaymentInfoModel = getWorldpayPaymentInfoService().createCreditCardPaymentInfo(cartModel, createTokenResponse.getToken(), saveCard, merchantCode);
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
                        getWorldpayPaymentInfoService().createCreditCardPaymentInfo(cartModel, createTokenResponse.getToken(), saveCard, merchantCode));
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

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectAuthoriseServiceResponse authorise3DSecure(final MerchantInfo merchantInfo,
                                                            final String worldpayOrderCode,
                                                            final WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                            final String paResponse) throws WorldpayException {
        final String cookie = worldpaySessionService.getAndRemoveThreeDSecureCookie();

        final DirectAuthoriseServiceRequest directAuthoriseServiceRequest = worldpayRequestFactory.build3dDirectAuthoriseRequest(
                merchantInfo, worldpayOrderCode, worldpayAdditionalInfoData, paResponse, cookie);
        return getWorldpayServiceGateway().directAuthorise(directAuthoriseServiceRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completeAuthorise(final DirectAuthoriseServiceResponse serviceResponse, final AbstractOrderModel abstractOrderModel, final String merchantCode) {
        final PaymentInfoModel paymentInfoModel = Optional.ofNullable(abstractOrderModel.getPaymentInfo())
                .orElseGet(() -> createCreditCardPaymentInfo(serviceResponse, abstractOrderModel, merchantCode));
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
        getTransactionIdentifier(serviceResponse)
                .ifPresent(transactionIdentifier ->
                        getWorldpayPaymentInfoService().setTransactionIdentifierOnPaymentInfo(paymentInfoModel, transactionIdentifier));
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

    protected Optional<String> getTransactionIdentifier(final DirectAuthoriseServiceResponse serviceResponse) {
        return Optional.ofNullable(serviceResponse.getPaymentReply().getSchemeResponse())
                .map(SchemeResponse::getTransactionIdentifier);
    }

    protected CreditCardPaymentInfoModel createCreditCardPaymentInfo(final DirectAuthoriseServiceResponse serviceResponse, final AbstractOrderModel abstractOrderModel, final String merchantCode) {
        final boolean isSavedCard = getTransactionIdentifier(serviceResponse).isPresent();
        return getWorldpayPaymentInfoService()
                .createCreditCardPaymentInfo(abstractOrderModel, serviceResponse.getToken(), isSavedCard, merchantCode);
    }
}
