package com.worldpay.service.payment.request.impl;

import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.data.*;
import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.enums.payment.storedCredentials.MerchantInitiatedReason;
import com.worldpay.enums.payment.storedCredentials.Usage;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.data.MerchantInfo;
import com.worldpay.data.payment.*;
import com.worldpay.data.threeds2.Additional3DSData;
import com.worldpay.data.threeds2.RiskData;
import com.worldpay.data.token.CardDetails;
import com.worldpay.data.token.Token;
import com.worldpay.data.token.TokenRequest;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.payment.*;
import com.worldpay.service.payment.request.WorldpayRequestFactory;
import com.worldpay.service.payment.request.WorldpayRequestService;
import com.worldpay.service.request.*;
import com.worldpay.service.request.AuthoriseRequestParameters.AuthoriseRequestParametersCreator;
import com.worldpay.service.response.CreateTokenResponse;
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
    protected final WorldpayKlarnaService worldpayKlarnaService;
    protected final WorldpayRiskDataService worldpayRiskDataService;
    protected final WorldpayCartService worldpayCartService;
    protected final WorldpayRequestService worldpayRequestService;
    protected final WorldpayAdditionalRequestDataService worldpayAdditionalRequestDataService;

    public DefaultWorldpayRequestFactory(final WorldpayTokenEventReferenceCreationStrategy worldpayTokenEventReferenceCreationStrategy,
                                         final WorldpayOrderService worldpayOrderService,
                                         final WorldpayKlarnaService worldpayKlarnaService,
                                         final WorldpayRiskDataService worldpayRiskDataService,
                                         final WorldpayCartService worldpayCartService,
                                         final WorldpayRequestService worldpayRequestService,
                                         final WorldpayAdditionalRequestDataService worldpayAdditionalRequestDataService) {
        this.worldpayTokenEventReferenceCreationStrategy = worldpayTokenEventReferenceCreationStrategy;
        this.worldpayOrderService = worldpayOrderService;
        this.worldpayKlarnaService = worldpayKlarnaService;
        this.worldpayRiskDataService = worldpayRiskDataService;
        this.worldpayCartService = worldpayCartService;
        this.worldpayRequestService = worldpayRequestService;
        this.worldpayAdditionalRequestDataService = worldpayAdditionalRequestDataService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CreateTokenServiceRequest buildTokenRequest(final MerchantInfo merchantInfo, final CartModel cartModel, final CSEAdditionalAuthInfo cseAdditionalAuthInfo,
                                                       final WorldpayAdditionalInfoData worldpayAdditionalInfoData) {

        final Address billingAddress = worldpayCartService.getBillingAddress(cartModel, cseAdditionalAuthInfo);
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
        final String shopperEmailAddress = worldpayCartService.getEmailForCustomer(cartModel);

        final Session session = worldpayRequestService.createSession(worldpayAdditionalInfoData);
        final Browser browser = worldpayRequestService.createBrowser(worldpayAdditionalInfoData);

        final Shopper authenticatedShopper = worldpayRequestService.createAuthenticatedShopper(shopperEmailAddress, worldpayAdditionalInfoData.getAuthenticatedShopperId(), session, browser);

        final DynamicInteractionType dynamicInteractionType = worldpayRequestService.getDynamicInteractionType(worldpayAdditionalInfoData);
        final StoredCredentials storedCredentials = createStoredCredentials(worldpayAdditionalInfoData);

        final AuthoriseRequestParametersCreator authoriseRequestParametersCreator = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(orderInfo)
            .withPayment(token)
            .withShopper(authenticatedShopper)
            .withShippingAddress(worldpayCartService.getAddressFromCart(cartModel, true))
            .withBillingAddress(worldpayCartService.getAddressFromCart(cartModel, false))
            .withStatementNarrative(null)
            .withDynamicInteractionType(dynamicInteractionType)
            .withAdditional3DSData(additional3DSData)
            .withRiskData(riskData)
            .withStoredCredentials(storedCredentials);

        worldpayAdditionalRequestDataService.populateDirectRequestAdditionalData(cartModel, worldpayAdditionalInfoData, authoriseRequestParametersCreator);
        return createTokenisedDirectAuthoriseRequest(authoriseRequestParametersCreator
            .build());
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

        final String shopperEmailAddress = worldpayCartService.getEmailForCustomer(cartModel);

        final AuthoriseRequestParametersCreator authoriseRequestParametersCreator = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(orderInfo)
            .withPayment(googlePayPayment)
            .withShippingAddress(worldpayCartService.getAddressFromCart(cartModel, true))
            .withBillingAddress(worldpayCartService.getAddressFromCart(cartModel, false))
            .withStatementNarrative(null)
            .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE);

        if (googlePayAdditionalAuthInfo.getSaveCard()) {
            final String authenticatedShopperId = worldpayCartService.getAuthenticatedShopperId(cartModel);
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

        worldpayAdditionalRequestDataService.populateRequestGuaranteedPayments(cartModel, new WorldpayAdditionalInfoData(), authoriseRequestParametersCreator);

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

        final Address billingAddress = worldpayCartService.getBillingAddress(cartModel, bankTransferAdditionalAuthInfo);

        final Payment payment = worldpayRequestService.createBankPayment(orderCode, bankTransferAdditionalAuthInfo.getPaymentMethod(),
            bankTransferAdditionalAuthInfo.getShopperBankCode());

        final String shopperEmailAddress = worldpayCartService.getEmailForCustomer(cartModel);

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
            .withShippingAddress(worldpayCartService.getAddressFromCart(cartModel, true))
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
        final String customerEmail = worldpayCartService.getEmailForCustomer(abstractOrderModel);
        final Shopper shopper = worldpayRequestService.createAuthenticatedShopper(customerEmail, worldpayAdditionalInfoData.getAuthenticatedShopperId(), session, browser);
        final String subscriptionId = getSubscriptionId(abstractOrderModel);
        final Token token = worldpayRequestService.createToken(subscriptionId, worldpayAdditionalInfoData.getSecurityCode());
        final Address shippingAddress = worldpayCartService.getAddressFromCart(abstractOrderModel, true);
        final Address billingAddress = worldpayCartService.getAddressFromCart(abstractOrderModel, false);
        final DynamicInteractionType dynamicInteractionType = worldpayRequestService.getDynamicInteractionType(worldpayAdditionalInfoData);
        final StoredCredentials storedCredentials = createRecurringStoredCredentials(abstractOrderModel, dynamicInteractionType);

        final AuthoriseRequestParametersCreator authoriseRequestParametersCreator = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(orderInfo)
            .withPayment(token)
            .withShopper(shopper)
            .withShippingAddress(shippingAddress)
            .withBillingAddress(billingAddress)
            .withStatementNarrative(null)
            .withDynamicInteractionType(dynamicInteractionType)
            .withAdditional3DSData(additional3DSData)
            .withStoredCredentials(storedCredentials);

        worldpayAdditionalRequestDataService.populateDirectRequestAdditionalData(abstractOrderModel, worldpayAdditionalInfoData, authoriseRequestParametersCreator);

        return createTokenisedDirectAuthoriseRequest(authoriseRequestParametersCreator
            .build());
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

        final Address billingAddress = worldpayCartService.getBillingAddress(cartModel, additionalAuthInfo);
        final Address shippingAddress = worldpayCartService.getAddressFromCart(cartModel, true);

        final CustomerModel customerModel = (CustomerModel) cartModel.getUser();

        final Payment payment = worldpayOrderService.createKlarnaPayment(billingAddress.getCountryCode(), customerModel.getSessionLanguage(), null, additionalAuthInfo.getPaymentMethod());
        final OrderLines orderLines = worldpayKlarnaService.createOrderLines(cartModel);
        final String shopperEmailAddress = worldpayCartService.getEmailForCustomer(cartModel);
        final String statementNarrative = additionalAuthInfo.getStatementNarrative();

        final Session session = worldpayRequestService.createSession(worldpayAdditionalInfoData);
        final Browser browser = worldpayRequestService.createBrowser(worldpayAdditionalInfoData);
        final Shopper shopper = worldpayRequestService.createShopper(shopperEmailAddress, session, browser);

        final AuthoriseRequestParametersCreator requestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(orderInfo)
            .withPayment(payment)
            .withShopper(shopper)
            .withShippingAddress(shippingAddress)
            .withBillingAddress(billingAddress)
            .withStatementNarrative(statementNarrative)
            .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE)
            .withOrderLines(orderLines);

        worldpayAdditionalRequestDataService.populateRequestGuaranteedPayments(cartModel, worldpayAdditionalInfoData, requestParameters);

        return createKlarnaDirectAuthoriseRequest(requestParameters.build());
    }

    @Override
    public DirectAuthoriseServiceRequest buildApplePayDirectAuthorisationRequest(final MerchantInfo merchantInfo, final CartModel cartModel, final ApplePayAdditionalAuthInfo worldpayAdditionalInfoApplePayData) {
        final String orderCode = worldpayOrderService.generateWorldpayOrderCode(cartModel);

        final Amount amount = worldpayOrderService.createAmount(cartModel.getCurrency(), cartModel.getTotalPrice());
        final BasicOrderInfo orderInfo = worldpayOrderService.createBasicOrderInfo(orderCode, orderCode, amount);

        final Payment payment = worldpayOrderService.createApplePayPayment(worldpayAdditionalInfoApplePayData);
        final String shopperEmailAddress = worldpayCartService.getEmailForCustomer(cartModel);
        final Shopper shopper = worldpayRequestService.createShopper(shopperEmailAddress, null, null);
        final Address shippingAddress = worldpayCartService.getAddressFromCart(cartModel, true);
        final Address billingAddress = worldpayCartService.getAddressFromCart(cartModel, false);

        final AuthoriseRequestParametersCreator requestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(orderInfo)
            .withPayment(payment)
            .withShopper(shopper)
            .withShippingAddress(shippingAddress)
            .withBillingAddress(billingAddress)
            .withStatementNarrative(null)
            .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE);

        worldpayAdditionalRequestDataService.populateRequestGuaranteedPayments(cartModel, new WorldpayAdditionalInfoData(), requestParameters);

        return createApplePayDirectAuthoriseRequest(requestParameters.build());
    }

    @Override
    public DirectAuthoriseServiceRequest buildDirectTokenAndAuthorise(final MerchantInfo merchantInfo, final CartModel cartModel, final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final CSEAdditionalAuthInfo cseAdditionalAuthInfo) {
        final String orderCode = worldpayOrderService.generateWorldpayOrderCode(cartModel);
        final Amount amount = worldpayOrderService.createAmount(cartModel.getCurrency(), cartModel.getTotalPrice());
        final BasicOrderInfo orderInfo = worldpayOrderService.createBasicOrderInfo(orderCode, orderCode, amount);
        final Address billingAddress = worldpayCartService.getBillingAddress(cartModel, cseAdditionalAuthInfo);
        final Address shippingAddress = worldpayCartService.getAddressFromCart(cartModel, true);
        final Additional3DSData additional3DSData = worldpayRequestService.createAdditional3DSData(worldpayAdditionalInfoData);
        final RiskData riskData = worldpayRiskDataService.createRiskData(cartModel, worldpayAdditionalInfoData);

        final Cse csePayment = worldpayOrderService.createCsePayment(cseAdditionalAuthInfo, billingAddress);
        final String shopperEmailAddress = worldpayCartService.getEmailForCustomer(cartModel);
        final String statementNarrative = cseAdditionalAuthInfo.getStatementNarrative();

        final Session session = worldpayRequestService.createSession(worldpayAdditionalInfoData);
        final Browser browser = worldpayRequestService.createBrowser(worldpayAdditionalInfoData);
        final Shopper shopper = worldpayRequestService.createAuthenticatedShopper(shopperEmailAddress, worldpayAdditionalInfoData.getAuthenticatedShopperId(), session, browser);
        final DynamicInteractionType dynamicInteractionType = worldpayRequestService.getDynamicInteractionType(worldpayAdditionalInfoData);
        final String tokenEventReference = getWorldpayTokenEventReferenceCreationStrategy().createTokenEventReference();
        final TokenRequest tokenRequest = worldpayRequestService.createTokenRequest(tokenEventReference, null);
        final StoredCredentials storedCredentials = worldpayRequestService.createStoredCredentials(Usage.FIRST, null, null);

        final AuthoriseRequestParametersCreator authoriseRequestParametersCreator = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
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
            .withAdditional3DSData(additional3DSData);

        worldpayAdditionalRequestDataService.populateDirectRequestAdditionalData(cartModel, worldpayAdditionalInfoData, authoriseRequestParametersCreator);

        return buildDirectTokenAndAuthoriseRequest(authoriseRequestParametersCreator.build());

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
    public RedirectAuthoriseServiceRequest buildRedirectAuthoriseRequest(final MerchantInfo merchantInfo,
                                                                         final CartModel cartModel,
                                                                         final AdditionalAuthInfo additionalAuthInfo,
                                                                         final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayConfigurationException {
        final AuthoriseRequestParametersCreator authoriseRequestParametersCreator = internalCreateCommonRedirectAuthoriseServiceRequest(merchantInfo, cartModel, additionalAuthInfo, worldpayAdditionalInfoData);

        if (worldpayKlarnaService.isKlarnaPaymentType(additionalAuthInfo.getPaymentMethod())) {
            return internalGetRedirectAuthoriseServiceRequestForKlarna(cartModel, additionalAuthInfo, authoriseRequestParametersCreator);
        } else {
            return internalGetRedirectAuthoriseServiceRequest(cartModel, additionalAuthInfo, authoriseRequestParametersCreator, worldpayAdditionalInfoData);
        }
    }

    protected void addPayPalPaymentMethodAttribute(final AuthoriseRequestParametersCreator authoriseRequestParametersCreator) {
        final PaymentMethodAttribute paypalPaymentMethodAttribute = new PaymentMethodAttribute();
        paypalPaymentMethodAttribute.setPaymentMethod(PaymentType.PAYPAL.getMethodCode());
        paypalPaymentMethodAttribute.setAttrName("firstInBillingRun");
        paypalPaymentMethodAttribute.setAttrValue("true");
        authoriseRequestParametersCreator.withPaymentMethodAttributes(List.of(paypalPaymentMethodAttribute));
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

    protected AuthoriseRequestParametersCreator internalCreateCommonRedirectAuthoriseServiceRequest(final MerchantInfo merchantInfo, final CartModel cartModel, final AdditionalAuthInfo additionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) {
        final String orderCode = worldpayOrderService.generateWorldpayOrderCode(cartModel);
        final CurrencyModel currencyModel = cartModel.getCurrency();
        final Amount amount = worldpayOrderService.createAmount(currencyModel, cartModel.getTotalPrice());
        final BasicOrderInfo orderInfo = worldpayOrderService.createBasicOrderInfo(orderCode, orderCode, amount);
        final Address shippingAddress = worldpayCartService.getAddressFromCart(cartModel, true);
        final Address billingAddress = worldpayCartService.getBillingAddress(cartModel, additionalAuthInfo);
        final RiskData riskData = worldpayRiskDataService.createRiskData(cartModel, worldpayAdditionalInfoData);

        final AuthoriseRequestParametersCreator authoriseRequestParametersCreator = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withOrderInfo(orderInfo)
            .withShippingAddress(shippingAddress)
            .withBillingAddress(billingAddress)
            .withMerchantInfo(merchantInfo)
            .withRiskData(riskData);

        worldpayAdditionalRequestDataService.populateRedirectRequestAdditionalData(cartModel, worldpayAdditionalInfoData, authoriseRequestParametersCreator);

        return authoriseRequestParametersCreator;
    }

    protected RedirectAuthoriseServiceRequest internalGetRedirectAuthoriseServiceRequest(final CartModel cartModel,
                                                                                         final AdditionalAuthInfo additionalAuthInfo,
                                                                                         final AuthoriseRequestParametersCreator authoriseRequestParametersCreator,
                                                                                         final WorldpayAdditionalInfoData worldpayAdditionalInfoData) {
        final String customerEmail = worldpayCartService.getEmailForCustomer(cartModel);
        final List<PaymentType> includedPTs = getIncludedPaymentTypeList(additionalAuthInfo);

        authoriseRequestParametersCreator.withInstallationId(additionalAuthInfo.getInstallationId())
            .withIncludedPTs(includedPTs);

        if (additionalAuthInfo.getSaveCard()) {
            final String authenticatedShopperId = worldpayCartService.getAuthenticatedShopperId(cartModel);
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

        worldpayAdditionalRequestDataService.populateRedirectRequestAdditionalData(cartModel, worldpayAdditionalInfoData, authoriseRequestParametersCreator);

        return createRedirectAuthoriseRequest(authoriseRequestParametersCreator.build());
    }

    protected RedirectAuthoriseServiceRequest internalGetRedirectAuthoriseServiceRequestForKlarna(final CartModel cartModel, final AdditionalAuthInfo additionalAuthInfo, final AuthoriseRequestParametersCreator authoriseRequestParametersCreator) throws WorldpayConfigurationException {
        final CustomerModel customerModel = (CustomerModel) cartModel.getUser();
        final Payment payment = worldpayOrderService.createKlarnaPayment(worldpayCartService.getBillingAddress(cartModel, additionalAuthInfo).getCountryCode(),
            customerModel.getSessionLanguage(), null,
            additionalAuthInfo.getPaymentMethod());
        final OrderLines orderLines = worldpayKlarnaService.createOrderLines(cartModel);
        final String shopperEmailAddress = worldpayCartService.getEmailForCustomer(cartModel);
        final String statementNarrative = additionalAuthInfo.getStatementNarrative();
        final Shopper shopper = worldpayRequestService.createShopper(shopperEmailAddress, null, null);

        authoriseRequestParametersCreator.withPayment(payment)
            .withShopper(shopper)
            .withStatementNarrative(statementNarrative)
            .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE)
            .withOrderLines(orderLines)
            .withAlternativeShippingAddress(worldpayRequestService.createAlternativeShippingAddress())
            .build();

        return createRedirectAuthoriseRequest(authoriseRequestParametersCreator.build());
    }

    private WorldpayTokenEventReferenceCreationStrategy getWorldpayTokenEventReferenceCreationStrategy() {
        return worldpayTokenEventReferenceCreationStrategy;
    }
}
