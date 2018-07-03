package com.worldpay.service.payment.request.impl;

import com.worldpay.core.services.strategies.RecurringGenerateMerchantTransactionCodeStrategy;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.data.BankTransferAdditionalAuthInfo;
import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.interaction.WorldpayDynamicInteractionResolverService;
import com.worldpay.service.model.*;
import com.worldpay.service.model.payment.Cse;
import com.worldpay.service.model.payment.Payment;
import com.worldpay.service.model.payment.PaymentBuilder;
import com.worldpay.service.model.token.CardDetails;
import com.worldpay.service.model.token.Token;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.payment.WorldpayKlarnaStrategy;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.payment.WorldpayTokenEventReferenceCreationStrategy;
import com.worldpay.service.payment.request.WorldpayRequestFactory;
import com.worldpay.service.request.CreateTokenServiceRequest;
import com.worldpay.service.request.DeleteTokenServiceRequest;
import com.worldpay.service.request.DirectAuthoriseServiceRequest;
import com.worldpay.service.request.UpdateTokenServiceRequest;
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

    private WorldpayTokenEventReferenceCreationStrategy worldpayTokenEventReferenceCreationStrategy;
    private WorldpayOrderService worldpayOrderService;
    private Converter<AddressModel, Address> worldpayAddressConverter;
    private CustomerEmailResolutionService customerEmailResolutionService;
    private RecurringGenerateMerchantTransactionCodeStrategy recurringGenerateMerchantTransactionCodeStrategy;
    private WorldpayDeliveryAddressStrategy worldpayDeliveryAddressStrategy;
    private CommerceCommonI18NService commerceCommonI18NService;
    private WorldpayKlarnaStrategy worldpayKlarnaStrategy;
    private WorldpayDynamicInteractionResolverService worldpayDynamicInteractionResolverService;

    protected static final String TOKEN_UPDATED = "Token updated ";
    protected static final String TOKEN_DELETED = "Token deleted ";

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
    public DirectAuthoriseServiceRequest buildDirectAuthoriseRequest(final MerchantInfo merchantInfo, final CartModel cartModel, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) {
        final String orderCode = recurringGenerateMerchantTransactionCodeStrategy.generateCode(cartModel);
        final Amount amount = worldpayOrderService.createAmount(cartModel.getCurrency(), cartModel.getTotalPrice());
        final BasicOrderInfo orderInfo = worldpayOrderService.createBasicOrderInfo(orderCode, orderCode, amount);

        final Token token = worldpayOrderService.createToken(((CreditCardPaymentInfoModel) cartModel.getPaymentInfo()).getSubscriptionId(), worldpayAdditionalInfoData.getSecurityCode());

        final CustomerModel customerModel = (CustomerModel) cartModel.getUser();

        final String shopperEmailAddress = customerEmailResolutionService.getEmailForCustomer(customerModel);

        final Session session = worldpayOrderService.createSession(worldpayAdditionalInfoData);
        final Browser browser = worldpayOrderService.createBrowser(worldpayAdditionalInfoData);

        final Shopper authenticatedShopper = worldpayOrderService.createAuthenticatedShopper(shopperEmailAddress, worldpayAdditionalInfoData.getAuthenticatedShopperId(), session, browser);
        final AddressModel deliveryAddress = worldpayDeliveryAddressStrategy.getDeliveryAddress(cartModel);
        final DynamicInteractionType dynamicInteractionType = worldpayDynamicInteractionResolverService.resolveInteractionTypeForDirectIntegration(worldpayAdditionalInfoData);
        return createTokenisedDirectAuthoriseRequest(merchantInfo, orderInfo, token, authenticatedShopper, worldpayAddressConverter.convert(deliveryAddress), dynamicInteractionType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectAuthoriseServiceRequest build3dDirectAuthoriseRequest(MerchantInfo merchantInfo, String worldpayOrderCode,
                                                                       WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                                       String paRes, String cookie) {
        final BasicOrderInfo orderInfo = worldpayOrderService.createBasicOrderInfo(worldpayOrderCode, worldpayOrderCode, null);

        final Session session = worldpayOrderService.createSession(worldpayAdditionalInfoData);

        final DirectAuthoriseServiceRequest directAuthoriseServiceRequest = createDirect3DAuthoriseRequest(merchantInfo, orderInfo,
                session, paRes);
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
        return createDirectAuthoriseRequest(merchantInfo, orderInfo, payment, shopper, shippingAddress, billingAddress, statementNarrative, dynamicInteractionType);
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
        final Session session = worldpayOrderService.createSession(worldpayAdditionalInfoData);
        final Browser browser = worldpayOrderService.createBrowser(worldpayAdditionalInfoData);
        final String authenticatedShopperId = worldpayAdditionalInfoData.getAuthenticatedShopperId();
        final String customerEmail = customerEmailResolutionService.getEmailForCustomer((CustomerModel) abstractOrderModel.getUser());
        final Shopper shopper = worldpayOrderService.createAuthenticatedShopper(customerEmail, authenticatedShopperId, session, browser);
        final Token token = worldpayOrderService.createToken(((CreditCardPaymentInfoModel) abstractOrderModel.getPaymentInfo()).getSubscriptionId(), worldpayAdditionalInfoData.getSecurityCode());
        final Address shippingAddress = worldpayAddressConverter.convert(worldpayDeliveryAddressStrategy.getDeliveryAddress(abstractOrderModel));
        final DynamicInteractionType dynamicInteractionType = worldpayDynamicInteractionResolverService.resolveInteractionTypeForDirectIntegration(worldpayAdditionalInfoData);
        return createTokenisedDirectAuthoriseRequest(merchantInfo, orderInfo, token, shopper, shippingAddress, dynamicInteractionType);
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
        final DynamicInteractionType dynamicInteractionType = worldpayDynamicInteractionResolverService.resolveInteractionTypeForDirectIntegration(worldpayAdditionalInfoData);
        return createKlarnaDirectAuthoriseRequest(merchantInfo, orderInfo, payment, shopper, shippingAddress, billingAddress, statementNarrative, orderLines, dynamicInteractionType);
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

    protected DirectAuthoriseServiceRequest createDirectAuthoriseRequest(final MerchantInfo merchantInfo, final BasicOrderInfo orderInfo,
                                                                         final Payment payment, final Shopper shopper, final Address shippingAddress, final Address billingAddress,
                                                                         final String statementNarrative, final DynamicInteractionType dynamicInteractionType) {
        return DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(merchantInfo, orderInfo, payment, shopper, shopper.getSession(), shippingAddress, billingAddress, statementNarrative, dynamicInteractionType);
    }

    protected DirectAuthoriseServiceRequest createKlarnaDirectAuthoriseRequest(final MerchantInfo merchantInfo, final BasicOrderInfo orderInfo, final Payment payment, final Shopper shopper, final Address shippingAddress, final Address billingAddress, final String statementNarrative, final OrderLines orderLines, final DynamicInteractionType dynamicInteractionType) {
        return DirectAuthoriseServiceRequest.createKlarnaDirectAuthoriseRequest(merchantInfo, orderInfo, payment, shopper, shopper.getSession(), shippingAddress, billingAddress, statementNarrative, orderLines, dynamicInteractionType);
    }


    protected Cse createCsePayment(final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final Address billingAddress) {
        return PaymentBuilder.createCSE(cseAdditionalAuthInfo.getEncryptedData(), billingAddress);
    }

    protected DirectAuthoriseServiceRequest createDirect3DAuthoriseRequest(final MerchantInfo merchantInfo,
                                                                           final BasicOrderInfo basicOrderInfo, final Session session, final String paRes) {
        return DirectAuthoriseServiceRequest.createDirect3DAuthoriseRequest(merchantInfo, basicOrderInfo, session, paRes);
    }

    protected DirectAuthoriseServiceRequest createTokenisedDirectAuthoriseRequest(final MerchantInfo merchantInfo,
                                                                                  final BasicOrderInfo basicOrderInfo, final Token token, final Shopper shopper, final Address shippingAddress, final DynamicInteractionType dynamicInteractionType) {
        return DirectAuthoriseServiceRequest.createTokenisedDirectAuthoriseRequest(merchantInfo, basicOrderInfo, token, shopper, shippingAddress, null, dynamicInteractionType);
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
}
