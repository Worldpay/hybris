package com.worldpay.service.payment.request.impl;

import com.worldpay.core.services.strategies.RecurringGenerateMerchantTransactionCodeStrategy;
import com.worldpay.data.*;
import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.interaction.WorldpayDynamicInteractionResolverService;
import com.worldpay.service.model.*;
import com.worldpay.service.model.payment.Cse;
import com.worldpay.service.model.payment.PayWithGoogleSSL;
import com.worldpay.service.model.payment.Payment;
import com.worldpay.service.model.payment.PaymentBuilder;
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
import com.worldpay.service.request.*;
import com.worldpay.service.response.CreateTokenResponse;
import com.worldpay.strategy.WorldpayDeliveryAddressStrategy;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayRequestFactory implements WorldpayRequestFactory {

    private static final String TOKEN_UPDATED = "Token updated ";
    private static final String TOKEN_DELETED = "Token deleted ";

    private WorldpayTokenEventReferenceCreationStrategy worldpayTokenEventReferenceCreationStrategy;
    private WorldpayOrderService worldpayOrderService;
    private Converter<AddressModel, Address> worldpayAddressConverter;
    private CustomerEmailResolutionService customerEmailResolutionService;
    private RecurringGenerateMerchantTransactionCodeStrategy recurringGenerateMerchantTransactionCodeStrategy;
    private WorldpayDeliveryAddressStrategy worldpayDeliveryAddressStrategy;
    private CommerceCommonI18NService commerceCommonI18NService;
    private WorldpayKlarnaStrategy worldpayKlarnaStrategy;
    private WorldpayDynamicInteractionResolverService worldpayDynamicInteractionResolverService;
    private WorldpayRiskDataService worldpayRiskDataService;

    /**
     * {@inheritDoc}
     */
    @Override
    public CreateTokenServiceRequest buildTokenRequest(final MerchantInfo merchantInfo, final CartModel cartModel, final CSEAdditionalAuthInfo cseAdditionalAuthInfo,
                                                       final WorldpayAdditionalInfoData worldpayAdditionalInfoData) {

        final Address billingAddress = getBillingAddress(cartModel, cseAdditionalAuthInfo);
        final String tokenEventReference = getWorldpayTokenEventReferenceCreationStrategy().createTokenEventReference();
        final TokenRequest tokenRequest = worldpayOrderService.createTokenRequest(tokenEventReference, null);
        final Cse csePayment = createCsePayment(cseAdditionalAuthInfo, billingAddress);
        return worldpayOrderService.createTokenServiceRequest(merchantInfo, worldpayAdditionalInfoData.getAuthenticatedShopperId(), csePayment, tokenRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UpdateTokenServiceRequest buildTokenUpdateRequest(final MerchantInfo merchantInfo, final CSEAdditionalAuthInfo cseAdditionalAuthInfo,
                                                             final WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                             final CreateTokenResponse createTokenResponse) {
        final String tokenReason = TOKEN_UPDATED + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);
        final TokenRequest tokenRequest = worldpayOrderService.createTokenRequest(getWorldpayTokenEventReferenceCreationStrategy().createTokenEventReference(), tokenReason);

        final String paymentTokenID = createTokenResponse.getToken().getTokenDetails().getPaymentTokenID();
        final CardDetails cardDetails = new CardDetails();
        final Date expiryDate = new Date(cseAdditionalAuthInfo.getExpiryMonth(), cseAdditionalAuthInfo.getExpiryYear());
        cardDetails.setExpiryDate(expiryDate);
        cardDetails.setCardHolderName(cseAdditionalAuthInfo.getCardHolderName());
        return worldpayOrderService.createUpdateTokenServiceRequest(merchantInfo, worldpayAdditionalInfoData,
                tokenRequest, paymentTokenID, cardDetails);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DeleteTokenServiceRequest buildTokenDeleteRequest(final MerchantInfo merchantInfo, final CreditCardPaymentInfoModel creditCardPaymentInfoModel) {
        final String tokenReason = TOKEN_DELETED + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);
        final TokenRequest tokenRequest = worldpayOrderService.createTokenRequestForDeletion(creditCardPaymentInfoModel.getEventReference(), tokenReason, creditCardPaymentInfoModel.getAuthenticatedShopperID());
        return createDeleteTokenServiceRequest(merchantInfo, creditCardPaymentInfoModel, tokenRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectAuthoriseServiceRequest buildDirectAuthoriseRequestWithTokenForCSE(final MerchantInfo merchantInfo, final CartModel cartModel, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) {
        final String orderCode = recurringGenerateMerchantTransactionCodeStrategy.generateCode(cartModel);
        final Amount amount = worldpayOrderService.createAmount(cartModel.getCurrency(), cartModel.getTotalPrice());
        final BasicOrderInfo orderInfo = worldpayOrderService.createBasicOrderInfo(orderCode, orderCode, amount);
        final Additional3DSData additional3DSData = worldpayOrderService.createAdditional3DSData(worldpayAdditionalInfoData);
        final RiskData riskData = worldpayRiskDataService.createRiskData(cartModel, worldpayAdditionalInfoData);
        final Token token = worldpayOrderService.createToken(((CreditCardPaymentInfoModel) cartModel.getPaymentInfo()).getSubscriptionId(), worldpayAdditionalInfoData.getSecurityCode());

        final CustomerModel customerModel = (CustomerModel) cartModel.getUser();

        final String shopperEmailAddress = customerEmailResolutionService.getEmailForCustomer(customerModel);

        final Session session = worldpayOrderService.createSession(worldpayAdditionalInfoData);
        final Browser browser = worldpayOrderService.createBrowser(worldpayAdditionalInfoData);

        final Shopper authenticatedShopper = worldpayOrderService.createAuthenticatedShopper(shopperEmailAddress, worldpayAdditionalInfoData.getAuthenticatedShopperId(), session, browser);
        final AddressModel deliveryAddress = worldpayDeliveryAddressStrategy.getDeliveryAddress(cartModel);
        final DynamicInteractionType dynamicInteractionType = worldpayDynamicInteractionResolverService.resolveInteractionTypeForDirectIntegration(worldpayAdditionalInfoData);

        final AuthoriseRequestParameters requestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
                .withMerchantInfo(merchantInfo)
                .withOrderInfo(orderInfo)
                .withPayment(token)
                .withShopper(authenticatedShopper)
                .withShippingAddress(worldpayAddressConverter.convert(deliveryAddress))
                .withBillingAddress(null)
                .withStatementNarrative(null)
                .withDynamicInteractionType(dynamicInteractionType)
                .withAdditional3DSData(additional3DSData)
                .withRiskData(riskData)
                .build();

        return createTokenisedDirectAuthoriseRequest(requestParameters);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public DirectAuthoriseServiceRequest buildDirectAuthoriseGooglePayRequest(final MerchantInfo merchantInfo, final CartModel cartModel, final GooglePayAdditionalAuthInfo googlePayAdditionalAuthInfo) {

        final String orderCode = recurringGenerateMerchantTransactionCodeStrategy.generateCode(cartModel);
        final Amount amount = worldpayOrderService.createAmount(cartModel.getCurrency(), cartModel.getTotalPrice());
        final BasicOrderInfo orderInfo = worldpayOrderService.createBasicOrderInfo(orderCode, orderCode, amount);

        final PayWithGoogleSSL googlePayPayment = createGooglePayPayment(googlePayAdditionalAuthInfo.getProtocolVersion(), googlePayAdditionalAuthInfo.getSignature(), googlePayAdditionalAuthInfo.getSignedMessage());

        final CustomerModel customerModel = (CustomerModel) cartModel.getUser();
        final String shopperEmailAddress = customerEmailResolutionService.getEmailForCustomer(customerModel);

        final Shopper shopper = worldpayOrderService.createShopper(shopperEmailAddress, null, null);
        final AddressModel deliveryAddress = worldpayDeliveryAddressStrategy.getDeliveryAddress(cartModel);

        final AuthoriseRequestParameters requestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
                .withMerchantInfo(merchantInfo)
                .withOrderInfo(orderInfo)
                .withPayment(googlePayPayment)
                .withShopper(shopper)
                .withShippingAddress(worldpayAddressConverter.convert(deliveryAddress))
                .withBillingAddress(null)
                .withStatementNarrative(null)
                .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE)
                .build();

        return createGooglePayDirectAuthoriseRequest(requestParameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectAuthoriseServiceRequest build3dDirectAuthoriseRequest(final MerchantInfo merchantInfo, final String worldpayOrderCode,
                                                                       final WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                                       final String paRes, final String cookie) {
        final BasicOrderInfo orderInfo = worldpayOrderService.createBasicOrderInfo(worldpayOrderCode, worldpayOrderCode, null);

        final Session session = worldpayOrderService.createSession(worldpayAdditionalInfoData);
        final Shopper shopper = worldpayOrderService.createShopper(null, session, null);

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
        final String orderCode = recurringGenerateMerchantTransactionCodeStrategy.generateCode(cartModel);
        final Amount amount = worldpayOrderService.createAmount(cartModel.getCurrency(), cartModel.getTotalPrice());
        final BasicOrderInfo orderInfo = worldpayOrderService.createBasicOrderInfo(orderCode, orderCode, amount);

        final Address billingAddress = getBillingAddress(cartModel, bankTransferAdditionalAuthInfo);
        final AddressModel deliveryAddressModel = worldpayDeliveryAddressStrategy.getDeliveryAddress(cartModel);

        final Address shippingAddress = worldpayAddressConverter.convert(deliveryAddressModel);

        final Payment payment = worldpayOrderService.createBankPayment(bankTransferAdditionalAuthInfo.getPaymentMethod(),
                bankTransferAdditionalAuthInfo.getShopperBankCode());

        final CustomerModel customerModel = (CustomerModel) cartModel.getUser();

        final String shopperEmailAddress = customerEmailResolutionService.getEmailForCustomer(customerModel);
        final String statementNarrative = bankTransferAdditionalAuthInfo.getStatementNarrative();

        final Session session = worldpayOrderService.createSession(worldpayAdditionalInfoData);
        final Browser browser = worldpayOrderService.createBrowser(worldpayAdditionalInfoData);
        final Shopper shopper = worldpayOrderService.createShopper(shopperEmailAddress, session, browser);
        final DynamicInteractionType dynamicInteractionType = worldpayDynamicInteractionResolverService.resolveInteractionTypeForDirectIntegration(worldpayAdditionalInfoData);

        final AuthoriseRequestParameters requestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
                .withMerchantInfo(merchantInfo)
                .withOrderInfo(orderInfo)
                .withPayment(payment)
                .withShopper(shopper)
                .withShippingAddress(shippingAddress)
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
        final String worldpayOrderCode = recurringGenerateMerchantTransactionCodeStrategy.generateCode(abstractOrderModel);
        final Amount amount = worldpayOrderService.createAmount(abstractOrderModel.getCurrency(), abstractOrderModel.getTotalPrice());
        final BasicOrderInfo orderInfo = worldpayOrderService.createBasicOrderInfo(worldpayOrderCode, worldpayOrderCode, amount);
        final Additional3DSData additional3DSData = worldpayOrderService.createAdditional3DSData(worldpayAdditionalInfoData);

        final Session session = worldpayOrderService.createSession(worldpayAdditionalInfoData);
        final Browser browser = worldpayOrderService.createBrowser(worldpayAdditionalInfoData);
        final String customerEmail = customerEmailResolutionService.getEmailForCustomer((CustomerModel) abstractOrderModel.getUser());
        final Shopper shopper = worldpayOrderService.createAuthenticatedShopper(customerEmail, worldpayAdditionalInfoData.getAuthenticatedShopperId(), session, browser);
        final Token token = worldpayOrderService.createToken(((CreditCardPaymentInfoModel) abstractOrderModel.getPaymentInfo()).getSubscriptionId(), worldpayAdditionalInfoData.getSecurityCode());
        final Address shippingAddress = worldpayAddressConverter.convert(worldpayDeliveryAddressStrategy.getDeliveryAddress(abstractOrderModel));
        final DynamicInteractionType dynamicInteractionType = worldpayDynamicInteractionResolverService.resolveInteractionTypeForDirectIntegration(worldpayAdditionalInfoData);

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
                .build();

        return createTokenisedDirectAuthoriseRequest(requestParameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectAuthoriseServiceRequest buildDirectAuthoriseKlarnaRequest(final MerchantInfo merchantInfo, final CartModel cartModel, final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final AdditionalAuthInfo additionalAuthInfo) throws WorldpayConfigurationException {
        final String orderCode = recurringGenerateMerchantTransactionCodeStrategy.generateCode(cartModel);
        final Amount amount = worldpayOrderService.createAmount(cartModel.getCurrency(), cartModel.getTotalPrice());
        final BasicOrderInfo orderInfo = worldpayOrderService.createBasicOrderInfo(orderCode, orderCode, amount);

        final Address billingAddress = getBillingAddress(cartModel, additionalAuthInfo);
        final AddressModel deliveryAddressModel = worldpayDeliveryAddressStrategy.getDeliveryAddress(cartModel);

        final Address shippingAddress = worldpayAddressConverter.convert(deliveryAddressModel);

        final CustomerModel customerModel = (CustomerModel) cartModel.getUser();

        final Payment payment = worldpayOrderService.createKlarnaPayment(billingAddress.getCountryCode(), commerceCommonI18NService.getLocaleForLanguage(customerModel.getSessionLanguage()).toLanguageTag(), null);
        final OrderLines orderLines = worldpayKlarnaStrategy.createOrderLines(cartModel);
        final String shopperEmailAddress = customerEmailResolutionService.getEmailForCustomer(customerModel);
        final String statementNarrative = additionalAuthInfo.getStatementNarrative();

        final Session session = worldpayOrderService.createSession(worldpayAdditionalInfoData);
        final Browser browser = worldpayOrderService.createBrowser(worldpayAdditionalInfoData);
        final Shopper shopper = worldpayOrderService.createShopper(shopperEmailAddress, session, browser);

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
        final String orderCode = recurringGenerateMerchantTransactionCodeStrategy.generateCode(cartModel);

        final Amount amount = worldpayOrderService.createAmount(cartModel.getCurrency(), cartModel.getTotalPrice());
        final BasicOrderInfo orderInfo = worldpayOrderService.createBasicOrderInfo(orderCode, orderCode, amount);

        final Payment payment = worldpayOrderService.createApplePayPayment(worldpayAdditionalInfoApplePayData);
        final String shopperEmailAddress = customerEmailResolutionService.getEmailForCustomer(((CustomerModel) cartModel.getUser()));
        final Shopper shopper = worldpayOrderService.createShopper(shopperEmailAddress, null, null);
        final AddressModel deliveryAddress = worldpayDeliveryAddressStrategy.getDeliveryAddress(cartModel);
        final Address shippingAddress = worldpayAddressConverter.convert(deliveryAddress);

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

    /**
     * {@inheritDoc}
     */
    @Override
    public SecondThreeDSecurePaymentRequest buildSecondThreeDSecurePaymentRequest(final MerchantInfo merchantInfo, final String worldpayOrderCode, final String sessionId, final String cookie) {
        final SecondThreeDSecurePaymentRequest request = new SecondThreeDSecurePaymentRequest(merchantInfo, worldpayOrderCode, sessionId);
        request.setCookie(cookie);
        return request;
    }

    protected Address getBillingAddress(final CartModel cartModel, final AdditionalAuthInfo additionalAuthInfo) {
        final AddressModel deliveryAddressModel = cartModel.getDeliveryAddress();
        if (deliveryAddressModel != null && additionalAuthInfo.getUsingShippingAsBilling()) {
            return worldpayAddressConverter.convert(deliveryAddressModel);
        } else {
            if (cartModel.getPaymentAddress() != null) {
                return worldpayAddressConverter.convert(cartModel.getPaymentAddress());
            }
        }
        return null;
    }

    protected DeleteTokenServiceRequest createDeleteTokenServiceRequest(final MerchantInfo merchantInfo,
                                                                        final CreditCardPaymentInfoModel creditCardPaymentInfoModel, final TokenRequest tokenRequest) {
        return DeleteTokenServiceRequest.deleteTokenRequest(merchantInfo, creditCardPaymentInfoModel.getAuthenticatedShopperID(),
                creditCardPaymentInfoModel.getSubscriptionId(), tokenRequest);
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

    protected Cse createCsePayment(final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final Address billingAddress) {
        return PaymentBuilder.createCSE(cseAdditionalAuthInfo.getEncryptedData(), billingAddress);
    }

    public PayWithGoogleSSL createGooglePayPayment(final String protocolVersion, final String signature, final String signedMessage) {
        return new PayWithGoogleSSL(protocolVersion, signature, signedMessage);
    }

    protected DirectAuthoriseServiceRequest createDirect3DAuthoriseRequest(final AuthoriseRequestParameters requestParameters) {
        return DirectAuthoriseServiceRequest.createDirect3DAuthoriseRequest(requestParameters);
    }

    protected DirectAuthoriseServiceRequest createTokenisedDirectAuthoriseRequest(final AuthoriseRequestParameters requestParameters) {
        return DirectAuthoriseServiceRequest.createTokenisedDirectAuthoriseRequest(requestParameters);
    }

    private WorldpayTokenEventReferenceCreationStrategy getWorldpayTokenEventReferenceCreationStrategy() {
        return worldpayTokenEventReferenceCreationStrategy;
    }

    @Required
    public void setWorldpayTokenEventReferenceCreationStrategy(final WorldpayTokenEventReferenceCreationStrategy worldpayTokenEventReferenceCreationStrategy) {
        this.worldpayTokenEventReferenceCreationStrategy = worldpayTokenEventReferenceCreationStrategy;
    }

    @Required
    public void setWorldpayOrderService(final WorldpayOrderService worldpayOrderService) {
        this.worldpayOrderService = worldpayOrderService;
    }

    @Required
    public void setWorldpayAddressConverter(final Converter<AddressModel, Address> worldpayAddressConverter) {
        this.worldpayAddressConverter = worldpayAddressConverter;
    }

    @Required
    public void setCustomerEmailResolutionService(final CustomerEmailResolutionService customerEmailResolutionService) {
        this.customerEmailResolutionService = customerEmailResolutionService;
    }

    @Required
    public void setRecurringGenerateMerchantTransactionCodeStrategy(final RecurringGenerateMerchantTransactionCodeStrategy recurringGenerateMerchantTransactionCodeStrategy) {
        this.recurringGenerateMerchantTransactionCodeStrategy = recurringGenerateMerchantTransactionCodeStrategy;
    }

    @Required
    public void setWorldpayDeliveryAddressStrategy(final WorldpayDeliveryAddressStrategy worldpayDeliveryAddressStrategy) {
        this.worldpayDeliveryAddressStrategy = worldpayDeliveryAddressStrategy;
    }

    @Required
    public void setCommerceCommonI18NService(final CommerceCommonI18NService commerceCommonI18NService) {
        this.commerceCommonI18NService = commerceCommonI18NService;
    }

    @Required
    public void setWorldpayKlarnaStrategy(final WorldpayKlarnaStrategy worldpayKlarnaStrategy) {
        this.worldpayKlarnaStrategy = worldpayKlarnaStrategy;
    }

    @Required
    public void setWorldpayDynamicInteractionResolverService(final WorldpayDynamicInteractionResolverService worldpayDynamicInteractionResolverService) {
        this.worldpayDynamicInteractionResolverService = worldpayDynamicInteractionResolverService;
    }

    @Required
    public void setWorldpayRiskDataService(final WorldpayRiskDataService worldpayRiskDataService) {
        this.worldpayRiskDataService = worldpayRiskDataService;
    }
}
