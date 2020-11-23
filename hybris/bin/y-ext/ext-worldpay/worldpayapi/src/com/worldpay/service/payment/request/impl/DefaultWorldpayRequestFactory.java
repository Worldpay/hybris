package com.worldpay.service.payment.request.impl;

import com.worldpay.data.*;
import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.enums.payment.storedCredentials.MerchantInitiatedReason;
import com.worldpay.enums.payment.storedCredentials.Usage;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.klarna.WorldpayKlarnaUtils;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.model.*;
import com.worldpay.service.model.payment.*;
import com.worldpay.service.model.threeds2.Additional3DSData;
import com.worldpay.service.model.threeds2.RiskData;
import com.worldpay.service.model.token.CardDetails;
import com.worldpay.service.model.token.Token;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.payment.WorldpayKlarnaStrategy;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.payment.WorldpayRiskDataService;
import com.worldpay.service.payment.WorldpayTokenEventReferenceCreationStrategy;
import com.worldpay.service.payment.request.WorldpayRequestFactory;
import com.worldpay.service.payment.request.WorldpayRequestService;
import com.worldpay.service.request.*;
import com.worldpay.service.request.AuthoriseRequestParameters.AuthoriseRequestParametersCreator;
import com.worldpay.service.response.CreateTokenResponse;
import com.worldpay.strategy.WorldpayAuthenticatedShopperIdStrategy;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayRequestFactory implements WorldpayRequestFactory {

    private static final String TOKEN_UPDATED = "Token updated ";
    private static final String TOKEN_DELETED = "Token deleted ";

    protected final WorldpayTokenEventReferenceCreationStrategy worldpayTokenEventReferenceCreationStrategy;
    protected final WorldpayOrderService worldpayOrderService;
    protected final WorldpayKlarnaStrategy worldpayKlarnaStrategy;
    protected final WorldpayRiskDataService worldpayRiskDataService;
    protected final WorldpayAuthenticatedShopperIdStrategy worldpayAuthenticatedShopperIdStrategy;
    protected final WorldpayKlarnaUtils worldpayKlarnaUtils;
    protected final WorldpayRequestService worldpayRequestService;

    public DefaultWorldpayRequestFactory(final WorldpayTokenEventReferenceCreationStrategy worldpayTokenEventReferenceCreationStrategy,
                                         final WorldpayOrderService worldpayOrderService,
                                         final WorldpayKlarnaStrategy worldpayKlarnaStrategy,
                                         final WorldpayRiskDataService worldpayRiskDataService,
                                         final WorldpayAuthenticatedShopperIdStrategy worldpayAuthenticatedShopperIdStrategy,
                                         final WorldpayKlarnaUtils worldpayKlarnaUtils,
                                         final WorldpayRequestService worldpayRequestService) {
        this.worldpayTokenEventReferenceCreationStrategy = worldpayTokenEventReferenceCreationStrategy;
        this.worldpayOrderService = worldpayOrderService;
        this.worldpayKlarnaStrategy = worldpayKlarnaStrategy;
        this.worldpayRiskDataService = worldpayRiskDataService;
        this.worldpayAuthenticatedShopperIdStrategy = worldpayAuthenticatedShopperIdStrategy;
        this.worldpayKlarnaUtils = worldpayKlarnaUtils;
        this.worldpayRequestService = worldpayRequestService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CreateTokenServiceRequest buildTokenRequest(final MerchantInfo merchantInfo, final CartModel cartModel, final CSEAdditionalAuthInfo cseAdditionalAuthInfo,
                                                       final WorldpayAdditionalInfoData worldpayAdditionalInfoData) {

        final Address billingAddress = worldpayRequestService.getBillingAddress(cartModel, cseAdditionalAuthInfo);
        final String tokenEventReference = getWorldpayTokenEventReferenceCreationStrategy().createTokenEventReference();
        final TokenRequest tokenRequest = worldpayRequestService.createTokenRequest(tokenEventReference, null);
        final Cse csePayment = worldpayOrderService.createCsePayment(cseAdditionalAuthInfo, billingAddress);
        return worldpayRequestService.createTokenServiceRequest(merchantInfo, worldpayAdditionalInfoData.getAuthenticatedShopperId(), csePayment, tokenRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UpdateTokenServiceRequest buildTokenUpdateRequest(final MerchantInfo merchantInfo,
                                                             final CSEAdditionalAuthInfo cseAdditionalAuthInfo,
                                                             final WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                             final AddressModel paymentAddress,
                                                             final CreateTokenResponse createTokenResponse) {
        final String tokenReason = TOKEN_UPDATED + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);
        final TokenRequest tokenRequest = worldpayRequestService.createTokenRequest(getWorldpayTokenEventReferenceCreationStrategy().createTokenEventReference(), tokenReason);

        final String paymentTokenID = createTokenResponse.getToken().getTokenDetails().getPaymentTokenID();
        final CardDetails cardDetails = worldpayRequestService.createCardDetails(cseAdditionalAuthInfo, paymentAddress);
        return worldpayRequestService.createUpdateTokenServiceRequest(merchantInfo, worldpayAdditionalInfoData,
            tokenRequest, paymentTokenID, cardDetails);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public DeleteTokenServiceRequest buildTokenDeleteRequest(final MerchantInfo merchantInfo, final PaymentInfoModel paymentInfoModel, final String subscriptionId) {
        final String tokenReason = TOKEN_DELETED + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);
        final TokenRequest tokenRequest = worldpayRequestService.createTokenRequestForDeletion(paymentInfoModel.getEventReference(), tokenReason, paymentInfoModel.getAuthenticatedShopperID());
        return createDeleteTokenServiceRequest(merchantInfo, paymentInfoModel.getAuthenticatedShopperID(), subscriptionId, tokenRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectAuthoriseServiceRequest buildDirectAuthoriseRequestWithTokenForCSE(final MerchantInfo merchantInfo, final CartModel cartModel, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) {
        final String orderCode = worldpayOrderService.generateWorldpayOrderCode(cartModel);
        final Amount amount = worldpayOrderService.createAmount(cartModel.getCurrency(), cartModel.getTotalPrice());
        final BasicOrderInfo orderInfo = worldpayOrderService.createBasicOrderInfo(orderCode, orderCode, amount);
        final Additional3DSData additional3DSData = worldpayRequestService.createAdditional3DSData(worldpayAdditionalInfoData);
        final RiskData riskData = worldpayRiskDataService.createRiskData(cartModel, worldpayAdditionalInfoData);
        final Token token = worldpayRequestService.createToken(((CreditCardPaymentInfoModel) cartModel.getPaymentInfo()).getSubscriptionId(), worldpayAdditionalInfoData.getSecurityCode());

        final String shopperEmailAddress = worldpayRequestService.getEmailForCustomer((CustomerModel) cartModel.getUser());

        final Session session = worldpayRequestService.createSession(worldpayAdditionalInfoData);
        final Browser browser = worldpayRequestService.createBrowser(worldpayAdditionalInfoData);

        final Shopper authenticatedShopper = worldpayRequestService.createAuthenticatedShopper(shopperEmailAddress, worldpayAdditionalInfoData.getAuthenticatedShopperId(), session, browser);

        final DynamicInteractionType dynamicInteractionType = worldpayRequestService.getDynamicInteractionType(worldpayAdditionalInfoData);
        final StoredCredentials storedCredentials = createStoredCredentials(worldpayAdditionalInfoData);

        final AuthoriseRequestParameters requestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(orderInfo)
            .withPayment(token)
            .withShopper(authenticatedShopper)
            .withShippingAddress(worldpayRequestService.getAddressFromCart(cartModel, true))
            .withBillingAddress(null)
            .withStatementNarrative(null)
            .withDynamicInteractionType(dynamicInteractionType)
            .withAdditional3DSData(additional3DSData)
            .withRiskData(riskData)
            .withStoredCredentials(storedCredentials)
            .build();

        return createTokenisedDirectAuthoriseRequest(requestParameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectAuthoriseServiceRequest buildDirectAuthoriseGooglePayRequest(final MerchantInfo merchantInfo, final CartModel cartModel, final GooglePayAdditionalAuthInfo googlePayAdditionalAuthInfo) {

        final String orderCode = worldpayOrderService.generateWorldpayOrderCode(cartModel);
        final Amount amount = worldpayOrderService.createAmount(cartModel.getCurrency(), cartModel.getTotalPrice());
        final BasicOrderInfo orderInfo = worldpayOrderService.createBasicOrderInfo(orderCode, orderCode, amount);

        final PayWithGoogleSSL googlePayPayment = worldpayOrderService.createGooglePayPayment(googlePayAdditionalAuthInfo.getProtocolVersion(), googlePayAdditionalAuthInfo.getSignature(), googlePayAdditionalAuthInfo.getSignedMessage());

        final CustomerModel customerModel = (CustomerModel) cartModel.getUser();
        final String shopperEmailAddress = worldpayRequestService.getEmailForCustomer(customerModel);

        final AuthoriseRequestParametersCreator authoriseRequestParametersCreator = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(orderInfo)
            .withPayment(googlePayPayment)
            .withShippingAddress(worldpayRequestService.getAddressFromCart(cartModel, true))
            .withBillingAddress(worldpayRequestService.getAddressFromCart(cartModel, false))
            .withStatementNarrative(null)
            .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE);

        if (googlePayAdditionalAuthInfo.getSaveCard()) {
            final String authenticatedShopperId = worldpayAuthenticatedShopperIdStrategy.getAuthenticatedShopperId(customerModel);
            final String tokenEventReference = worldpayTokenEventReferenceCreationStrategy.createTokenEventReference();
            final TokenRequest tokenRequest = worldpayRequestService.createTokenRequest(tokenEventReference, null);
            final StoredCredentials storedCredentials = worldpayRequestService.createStoredCredentials(Usage.FIRST, null, null);
            authoriseRequestParametersCreator
                .withTokenRequest(tokenRequest)
                .withStoredCredentials(storedCredentials)
                .withShopper(worldpayRequestService.createAuthenticatedShopper(shopperEmailAddress, authenticatedShopperId, null, null));
        } else {
            authoriseRequestParametersCreator
                .withShopper(worldpayRequestService.createShopper(shopperEmailAddress, null, null));
        }

        return createGooglePayDirectAuthoriseRequest(authoriseRequestParametersCreator.build());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectAuthoriseServiceRequest build3dDirectAuthoriseRequest(final MerchantInfo merchantInfo, final String worldpayOrderCode,
                                                                       final WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                                       final String paRes, final String cookie) {
        final BasicOrderInfo orderInfo = worldpayOrderService.createBasicOrderInfo(worldpayOrderCode, worldpayOrderCode, null);

        final Session session = worldpayRequestService.createSession(worldpayAdditionalInfoData);
        final Shopper shopper = worldpayRequestService.createShopper(null, session, null);

        final AuthoriseRequestParameters requestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(orderInfo)
            .withPayment(null)
            .withShopper(shopper)
            .withShippingAddress(null)
            .withBillingAddress(null)
            .withStatementNarrative(null)
            .withDynamicInteractionType(null)
            .withPaRes(paRes)
            .build();

        final DirectAuthoriseServiceRequest directAuthoriseServiceRequest = createDirect3DAuthoriseRequest(requestParameters);
        directAuthoriseServiceRequest.setCookie(cookie);
        return directAuthoriseServiceRequest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectAuthoriseServiceRequest buildDirectAuthoriseBankTransferRequest(final MerchantInfo merchantInfo, final CartModel cartModel,
                                                                                 final BankTransferAdditionalAuthInfo bankTransferAdditionalAuthInfo,
                                                                                 final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayConfigurationException {
        final String orderCode = worldpayOrderService.generateWorldpayOrderCode(cartModel);
        final Amount amount = worldpayOrderService.createAmount(cartModel.getCurrency(), cartModel.getTotalPrice());
        final BasicOrderInfo orderInfo = worldpayOrderService.createBasicOrderInfo(orderCode, orderCode, amount);

        final Address billingAddress = worldpayRequestService.getBillingAddress(cartModel, bankTransferAdditionalAuthInfo);

        final Payment payment = worldpayRequestService.createBankPayment(bankTransferAdditionalAuthInfo.getPaymentMethod(),
            bankTransferAdditionalAuthInfo.getShopperBankCode());

        final String shopperEmailAddress = worldpayRequestService.getEmailForCustomer((CustomerModel) cartModel.getUser());

        final String statementNarrative = bankTransferAdditionalAuthInfo.getStatementNarrative();

        final Session session = worldpayRequestService.createSession(worldpayAdditionalInfoData);
        final Browser browser = worldpayRequestService.createBrowser(worldpayAdditionalInfoData);
        final Shopper shopper = worldpayRequestService.createShopper(shopperEmailAddress, session, browser);
        final DynamicInteractionType dynamicInteractionType = worldpayRequestService.getDynamicInteractionType(worldpayAdditionalInfoData);

        final AuthoriseRequestParameters requestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(orderInfo)
            .withPayment(payment)
            .withShopper(shopper)
            .withShippingAddress(worldpayRequestService.getAddressFromCart(cartModel, true))
            .withBillingAddress(billingAddress)
            .withStatementNarrative(statementNarrative)
            .withDynamicInteractionType(dynamicInteractionType)
            .build();

        return createBankTransferAuthoriseRequest(requestParameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectAuthoriseServiceRequest buildDirectAuthoriseRecurringPayment(final MerchantInfo merchantInfo, final AbstractOrderModel abstractOrderModel,
                                                                              final WorldpayAdditionalInfoData worldpayAdditionalInfoData) {
        final String worldpayOrderCode = worldpayOrderService.generateWorldpayOrderCode(abstractOrderModel);
        final Amount amount = worldpayOrderService.createAmount(abstractOrderModel.getCurrency(), abstractOrderModel.getTotalPrice());
        final BasicOrderInfo orderInfo = worldpayOrderService.createBasicOrderInfo(worldpayOrderCode, worldpayOrderCode, amount);
        final Additional3DSData additional3DSData = worldpayRequestService.createAdditional3DSData(worldpayAdditionalInfoData);

        final Session session = worldpayRequestService.createSession(worldpayAdditionalInfoData);
        final Browser browser = worldpayRequestService.createBrowser(worldpayAdditionalInfoData);
        final String customerEmail = worldpayRequestService.getEmailForCustomer((CustomerModel) abstractOrderModel.getUser());
        final Shopper shopper = worldpayRequestService.createAuthenticatedShopper(customerEmail, worldpayAdditionalInfoData.getAuthenticatedShopperId(), session, browser);
        final String subscriptionId = getSubscriptionId(abstractOrderModel);
        final Token token = worldpayRequestService.createToken(subscriptionId, worldpayAdditionalInfoData.getSecurityCode());
        final Address shippingAddress = worldpayRequestService.getAddressFromCart(abstractOrderModel, true);
        final DynamicInteractionType dynamicInteractionType = worldpayRequestService.getDynamicInteractionType(worldpayAdditionalInfoData);
        final StoredCredentials storedCredentials = createRecurringStoredCredentials(abstractOrderModel, dynamicInteractionType);

        final AuthoriseRequestParameters requestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(orderInfo)
            .withPayment(token)
            .withShopper(shopper)
            .withShippingAddress(shippingAddress)
            .withBillingAddress(null)
            .withStatementNarrative(null)
            .withDynamicInteractionType(dynamicInteractionType)
            .withAdditional3DSData(additional3DSData)
            .withStoredCredentials(storedCredentials)
            .build();

        return createTokenisedDirectAuthoriseRequest(requestParameters);
    }

    protected String getSubscriptionId(final AbstractOrderModel abstractOrderModel) {
        final PaymentInfoModel paymentInfo = abstractOrderModel.getPaymentInfo();
        return Optional.of(paymentInfo)
            .filter(CreditCardPaymentInfoModel.class::isInstance)
            .map(CreditCardPaymentInfoModel.class::cast)
            .map(CreditCardPaymentInfoModel::getSubscriptionId)
            .orElseGet(() ->
                Optional.of(paymentInfo)
                    .filter(WorldpayAPMPaymentInfoModel.class::isInstance)
                    .map(WorldpayAPMPaymentInfoModel.class::cast)
                    .map(WorldpayAPMPaymentInfoModel::getSubscriptionId)
                    .orElse(null));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectAuthoriseServiceRequest buildDirectAuthoriseKlarnaRequest(final MerchantInfo merchantInfo, final CartModel cartModel, final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final AdditionalAuthInfo additionalAuthInfo) throws WorldpayConfigurationException {
        final String orderCode = worldpayOrderService.generateWorldpayOrderCode(cartModel);
        final Amount amount = worldpayOrderService.createAmount(cartModel.getCurrency(), cartModel.getTotalPrice());
        final BasicOrderInfo orderInfo = worldpayOrderService.createBasicOrderInfo(orderCode, orderCode, amount);

        final Address billingAddress = worldpayRequestService.getBillingAddress(cartModel, additionalAuthInfo);
        final Address shippingAddress = worldpayRequestService.getAddressFromCart(cartModel, true);

        final CustomerModel customerModel = (CustomerModel) cartModel.getUser();

        final Payment payment = worldpayOrderService.createKlarnaPayment(billingAddress.getCountryCode(), customerModel.getSessionLanguage(), null, additionalAuthInfo.getPaymentMethod());
        final OrderLines orderLines = worldpayKlarnaStrategy.createOrderLines(cartModel);
        final String shopperEmailAddress = worldpayRequestService.getEmailForCustomer(customerModel);
        final String statementNarrative = additionalAuthInfo.getStatementNarrative();

        final Session session = worldpayRequestService.createSession(worldpayAdditionalInfoData);
        final Browser browser = worldpayRequestService.createBrowser(worldpayAdditionalInfoData);
        final Shopper shopper = worldpayRequestService.createShopper(shopperEmailAddress, session, browser);

        final AuthoriseRequestParameters requestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(orderInfo)
            .withPayment(payment)
            .withShopper(shopper)
            .withShippingAddress(shippingAddress)
            .withBillingAddress(billingAddress)
            .withStatementNarrative(statementNarrative)
            .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE)
            .withOrderLines(orderLines)
            .build();

        return createKlarnaDirectAuthoriseRequest(requestParameters);
    }

    @Override
    public DirectAuthoriseServiceRequest buildApplePayDirectAuthorisationRequest(final MerchantInfo merchantInfo, final CartModel cartModel, final ApplePayAdditionalAuthInfo worldpayAdditionalInfoApplePayData) {
        final String orderCode = worldpayOrderService.generateWorldpayOrderCode(cartModel);

        final Amount amount = worldpayOrderService.createAmount(cartModel.getCurrency(), cartModel.getTotalPrice());
        final BasicOrderInfo orderInfo = worldpayOrderService.createBasicOrderInfo(orderCode, orderCode, amount);

        final Payment payment = worldpayOrderService.createApplePayPayment(worldpayAdditionalInfoApplePayData);
        final String shopperEmailAddress = worldpayRequestService.getEmailForCustomer((CustomerModel) cartModel.getUser());
        final Shopper shopper = worldpayRequestService.createShopper(shopperEmailAddress, null, null);
        final Address shippingAddress = worldpayRequestService.getAddressFromCart(cartModel, true);

        final AuthoriseRequestParameters requestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(orderInfo)
            .withPayment(payment)
            .withShopper(shopper)
            .withShippingAddress(shippingAddress)
            .withBillingAddress(null)
            .withStatementNarrative(null)
            .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE)
            .build();

        return createApplePayDirectAuthoriseRequest(requestParameters);
    }

    @Override
    public DirectAuthoriseServiceRequest buildDirectTokenAndAuthorise(final MerchantInfo merchantInfo, final CartModel cartModel, final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final CSEAdditionalAuthInfo cseAdditionalAuthInfo) {
        final String orderCode = worldpayOrderService.generateWorldpayOrderCode(cartModel);
        final Amount amount = worldpayOrderService.createAmount(cartModel.getCurrency(), cartModel.getTotalPrice());
        final BasicOrderInfo orderInfo = worldpayOrderService.createBasicOrderInfo(orderCode, orderCode, amount);
        final Address billingAddress = worldpayRequestService.getBillingAddress(cartModel, cseAdditionalAuthInfo);
        final Address shippingAddress = worldpayRequestService.getAddressFromCart(cartModel, true);
        final Additional3DSData additional3DSData = worldpayRequestService.createAdditional3DSData(worldpayAdditionalInfoData);
        final RiskData riskData = worldpayRiskDataService.createRiskData(cartModel, worldpayAdditionalInfoData);

        final Cse csePayment = worldpayOrderService.createCsePayment(cseAdditionalAuthInfo, billingAddress);
        final String shopperEmailAddress = worldpayRequestService.getEmailForCustomer((CustomerModel) cartModel.getUser());
        final String statementNarrative = cseAdditionalAuthInfo.getStatementNarrative();

        final Session session = worldpayRequestService.createSession(worldpayAdditionalInfoData);
        final Browser browser = worldpayRequestService.createBrowser(worldpayAdditionalInfoData);
        final Shopper shopper = worldpayRequestService.createAuthenticatedShopper(shopperEmailAddress, worldpayAdditionalInfoData.getAuthenticatedShopperId(), session, browser);
        final DynamicInteractionType dynamicInteractionType = worldpayRequestService.getDynamicInteractionType(worldpayAdditionalInfoData);
        final String tokenEventReference = getWorldpayTokenEventReferenceCreationStrategy().createTokenEventReference();
        final TokenRequest tokenRequest = worldpayRequestService.createTokenRequest(tokenEventReference, null);
        final StoredCredentials storedCredentials = worldpayRequestService.createStoredCredentials(Usage.FIRST, null, null);

        final AuthoriseRequestParameters requestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(orderInfo)
            .withPayment(csePayment)
            .withShopper(shopper)
            .withShippingAddress(shippingAddress)
            .withBillingAddress(billingAddress)
            .withStatementNarrative(statementNarrative)
            .withDynamicInteractionType(dynamicInteractionType)
            .withAuthenticatedShopperId(worldpayAdditionalInfoData.getAuthenticatedShopperId())
            .withTokenRequest(tokenRequest)
            .withStoredCredentials(cseAdditionalAuthInfo.getSaveCard() ? storedCredentials : null)
            .withRiskData(riskData)
            .withAdditional3DSData(additional3DSData)
            .build();
        return buildDirectTokenAndAuthoriseRequest(requestParameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SecondThreeDSecurePaymentRequest buildSecondThreeDSecurePaymentRequest(final MerchantInfo merchantInfo, final String worldpayOrderCode, final String sessionId, final String cookie) {
        final SecondThreeDSecurePaymentRequest request = new SecondThreeDSecurePaymentRequest(merchantInfo, worldpayOrderCode, sessionId);
        request.setCookie(cookie);
        return request;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RedirectAuthoriseServiceRequest buildRedirectAuthoriseRequest(final MerchantInfo merchantInfo, final CartModel cartModel, final AdditionalAuthInfo additionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayConfigurationException {
        final String orderCode = worldpayOrderService.generateWorldpayOrderCode(cartModel);
        final CurrencyModel currencyModel = cartModel.getCurrency();
        final Amount amount = worldpayOrderService.createAmount(currencyModel, cartModel.getTotalPrice());
        final BasicOrderInfo orderInfo = worldpayOrderService.createBasicOrderInfo(orderCode, orderCode, amount);
        final Address shippingAddress = worldpayRequestService.getAddressFromCart(cartModel, true);
        final Address billingAddress = worldpayRequestService.getBillingAddress(cartModel, additionalAuthInfo);
        final RiskData riskData = worldpayRiskDataService.createRiskData(cartModel, worldpayAdditionalInfoData);

        final AuthoriseRequestParametersCreator authoriseRequestParametersCreator = internalCreateCommonRedirectAuthoriseServiceRequest(merchantInfo, orderInfo, shippingAddress, billingAddress, riskData);

        if (worldpayKlarnaUtils.isKlarnaPaymentType(additionalAuthInfo.getPaymentMethod())) {
            return internalGetRedirectAuthoriseServiceRequestForKlarna(cartModel, additionalAuthInfo, authoriseRequestParametersCreator);
        } else {
            return internalGetRedirectAuthoriseServiceRequest(cartModel, additionalAuthInfo, authoriseRequestParametersCreator);
        }
    }

    protected void addPayPalPaymentMethodAttribute(final AuthoriseRequestParametersCreator authoriseRequestParametersCreator) {
        final PaymentMethodAttribute paypalPaymentMethodAttribute = new PaymentMethodAttribute();
        paypalPaymentMethodAttribute.setPaymentMethod(PaymentType.PAYPAL.getMethodCode());
        paypalPaymentMethodAttribute.setAttrName("firstInBillingRun");
        paypalPaymentMethodAttribute.setAttrValue("true");
        authoriseRequestParametersCreator.withPaymentMethodAttributes(Arrays.asList(paypalPaymentMethodAttribute));
    }

    protected StoredCredentials createRecurringStoredCredentials(final AbstractOrderModel abstractOrderModel, final DynamicInteractionType dynamicInteractionType) {
        final StoredCredentials storedCredentials;
        if (DynamicInteractionType.ECOMMERCE.equals(dynamicInteractionType) || DynamicInteractionType.MOTO.equals(dynamicInteractionType)) {
            storedCredentials = worldpayRequestService.createStoredCredentials(Usage.USED, null, null);
        } else {
            storedCredentials = worldpayRequestService.createStoredCredentials(Usage.USED, MerchantInitiatedReason.RECURRING, abstractOrderModel.getPaymentInfo().getTransactionIdentifier());
        }
        return storedCredentials;
    }

    protected StoredCredentials createStoredCredentials(final WorldpayAdditionalInfoData worldpayAdditionalInfoData) {
        StoredCredentials storedCredentials = null;

        if (worldpayAdditionalInfoData.isSavedCardPayment()) {
            storedCredentials = worldpayRequestService.createStoredCredentials(Usage.FIRST, null, null);
        }
        return storedCredentials;
    }

    protected RedirectAuthoriseServiceRequest createRedirectAuthoriseRequest(final AuthoriseRequestParameters authoriseRequestParameters) {
        return RedirectAuthoriseServiceRequest.createRedirectAuthoriseRequest(authoriseRequestParameters);
    }

    protected List<PaymentType> getIncludedPaymentTypeList(final AdditionalAuthInfo additionalAuthInfo) {
        return singletonList(PaymentType.getPaymentType(additionalAuthInfo.getPaymentMethod()));
    }

    protected DeleteTokenServiceRequest createDeleteTokenServiceRequest(final MerchantInfo merchantInfo,
                                                                        final String authenticatedShopperID,
                                                                        final String subscriptionId,
                                                                        final TokenRequest tokenRequest) {
        return DeleteTokenServiceRequest.deleteTokenRequest(merchantInfo, authenticatedShopperID,
            subscriptionId, tokenRequest);
    }

    protected DirectAuthoriseServiceRequest createBankTransferAuthoriseRequest(final AuthoriseRequestParameters requestParameters) {
        return DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(requestParameters);
    }

    protected DirectAuthoriseServiceRequest createKlarnaDirectAuthoriseRequest(final AuthoriseRequestParameters requestParameters) {
        return DirectAuthoriseServiceRequest.createKlarnaDirectAuthoriseRequest(requestParameters);
    }

    protected DirectAuthoriseServiceRequest createGooglePayDirectAuthoriseRequest(final AuthoriseRequestParameters requestParameters) {
        return DirectAuthoriseServiceRequest.createGooglePayDirectAuthoriseRequest(requestParameters);
    }

    protected DirectAuthoriseServiceRequest createApplePayDirectAuthoriseRequest(final AuthoriseRequestParameters requestParameters) {
        return DirectAuthoriseServiceRequest.createApplePayDirectAuthoriseRequest(requestParameters);
    }

    protected DirectAuthoriseServiceRequest createDirect3DAuthoriseRequest(final AuthoriseRequestParameters requestParameters) {
        return DirectAuthoriseServiceRequest.createDirect3DAuthoriseRequest(requestParameters);
    }

    protected DirectAuthoriseServiceRequest createTokenisedDirectAuthoriseRequest(final AuthoriseRequestParameters requestParameters) {
        return DirectAuthoriseServiceRequest.createTokenisedDirectAuthoriseRequest(requestParameters);
    }

    protected DirectAuthoriseServiceRequest buildDirectTokenAndAuthoriseRequest(final AuthoriseRequestParameters requestParameters) {
        return DirectAuthoriseServiceRequest.createDirectTokenAndAuthoriseRequest(requestParameters);
    }

    protected AuthoriseRequestParametersCreator internalCreateCommonRedirectAuthoriseServiceRequest(final MerchantInfo merchantInfo, final BasicOrderInfo orderInfo, final Address shippingAddress, final Address billingAddress, final RiskData riskData) {
        return AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withOrderInfo(orderInfo)
            .withShippingAddress(shippingAddress)
            .withBillingAddress(billingAddress)
            .withMerchantInfo(merchantInfo)
            .withRiskData(riskData);
    }

    protected RedirectAuthoriseServiceRequest internalGetRedirectAuthoriseServiceRequest(final CartModel cartModel, final AdditionalAuthInfo additionalAuthInfo, final AuthoriseRequestParametersCreator authoriseRequestParametersCreator) {
        final CustomerModel customerModel = (CustomerModel) cartModel.getUser();
        final String customerEmail = worldpayRequestService.getEmailForCustomer(customerModel);
        final List<PaymentType> includedPTs = getIncludedPaymentTypeList(additionalAuthInfo);

        authoriseRequestParametersCreator.withInstallationId(additionalAuthInfo.getInstallationId())
            .withIncludedPTs(includedPTs);

        if (additionalAuthInfo.getSaveCard()) {
            final String authenticatedShopperId = worldpayAuthenticatedShopperIdStrategy.getAuthenticatedShopperId(customerModel);
            final Shopper shopper = worldpayRequestService.createAuthenticatedShopper(customerEmail, authenticatedShopperId, null, null);
            final String tokenEventReference = worldpayTokenEventReferenceCreationStrategy.createTokenEventReference();
            final TokenRequest tokenRequest = worldpayRequestService.createTokenRequest(tokenEventReference, null);
            authoriseRequestParametersCreator
                .withShopper(shopper)
                .withTokenRequest(tokenRequest);
            if (PaymentType.PAYPAL.getMethodCode().equals(additionalAuthInfo.getPaymentMethod())) {
                addPayPalPaymentMethodAttribute(authoriseRequestParametersCreator);
            } else {
                final StoredCredentials storedCredentials = worldpayRequestService.createStoredCredentials(Usage.FIRST, null, null);
                authoriseRequestParametersCreator.withStoredCredentials(storedCredentials);
            }
        } else {
            final Shopper shopper = worldpayRequestService.createShopper(customerEmail, null, null);
            authoriseRequestParametersCreator.withShopper(shopper);
        }
        return createRedirectAuthoriseRequest(authoriseRequestParametersCreator.build());
    }

    protected RedirectAuthoriseServiceRequest internalGetRedirectAuthoriseServiceRequestForKlarna(final CartModel cartModel, final AdditionalAuthInfo additionalAuthInfo, final AuthoriseRequestParametersCreator authoriseRequestParametersCreator) throws WorldpayConfigurationException {
        final CustomerModel customerModel = (CustomerModel) cartModel.getUser();
        final Payment payment = worldpayOrderService.createKlarnaPayment(worldpayRequestService.getBillingAddress(cartModel, additionalAuthInfo).getCountryCode(),
            customerModel.getSessionLanguage(), null,
            additionalAuthInfo.getPaymentMethod());
        final OrderLines orderLines = worldpayKlarnaStrategy.createOrderLines(cartModel);
        final String shopperEmailAddress = worldpayRequestService.getEmailForCustomer(customerModel);
        final String statementNarrative = additionalAuthInfo.getStatementNarrative();
        final Shopper shopper = worldpayRequestService.createShopper(shopperEmailAddress, null, null);

        authoriseRequestParametersCreator.withPayment(payment)
            .withShopper(shopper)
            .withStatementNarrative(statementNarrative)
            .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE)
            .withOrderLines(orderLines)
            .build();

        return createRedirectAuthoriseRequest(authoriseRequestParametersCreator.build());
    }

    private WorldpayTokenEventReferenceCreationStrategy getWorldpayTokenEventReferenceCreationStrategy() {
        return worldpayTokenEventReferenceCreationStrategy;
    }
}
